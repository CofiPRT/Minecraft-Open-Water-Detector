package ro.cofi.openwaterdetector;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import ro.cofi.openwaterdetector.mixin.FishingBobberAccessor;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class MainTickEvent implements ClientTickEvents.EndTick {

    private final KeyBinding key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Toggle Detector",
            GLFW.GLFW_KEY_O,
            "Open Water Detector"
    ));

    private boolean lastPressed = false;
    private boolean enabled = true;

    @Override
    public void onEndTick(MinecraftClient client) {
        try {
            // handle key press
            if (key.isPressed() && !lastPressed)
                enabled = !enabled; // toggle

            lastPressed = key.isPressed();

            // offer game tick to the event
            if (enabled)
                tick();

        } catch (Exception ex) {
            OpenWaterDetector.logException(ex);
        }
    }

    private void tick() {
        // don't perform these checks in singleplayer if the game is paused
        if (OpenWaterDetector.getClient().isPaused() && OpenWaterDetector.getClient().isInSingleplayer())
            return;

        // find the bobber belonging to the player
        ClientPlayerEntity player = OpenWaterDetector.getPlayer();
        Vec3d pos = player.getPos();

        Optional<FishingBobberEntity> optionalBobber = player.getWorld().getEntitiesByClass(
                FishingBobberEntity.class,
                new Box(pos.add(-32, -32, -32), pos.add(32, 32, 32)),
                bobber -> Objects.equals(bobber.getPlayerOwner(), player)
        ).stream().findFirst();

        // keep waiting - low tick rate can cause the bobber to appear later than expected
        if (optionalBobber.isEmpty())
            return;

        FishingBobberEntity bobber = optionalBobber.get();

        // only care about bobbing bobbers - the ones that are in water, and not flying or stuck
        if (((FishingBobberAccessor) bobber).getState() != FishingBobberEntity.State.BOBBING)
            return;

        boolean inOpenWater = ((FishingBobberAccessor) bobber).invokeIsOpenOrWaterAround(bobber.getBlockPos());

        Random random = new Random();
        double randomX = 0.25 + 0.5 * random.nextDouble();
        double randomZ = 0.25 + 0.5 * random.nextDouble();

        Vec3d randomDirection = new Vec3d(randomX, 0, randomZ).rotateY((float) (360 * random.nextDouble()));

        Vec3d bobberPos = bobber.getPos();

        player.getWorld().addParticle(
                inOpenWater ? ParticleTypes.COMPOSTER : ParticleTypes.FLAME,
                bobberPos.getX() + randomDirection.getX(),
                bobberPos.getY(),
                bobberPos.getZ() + randomDirection.getZ(),
                0, 0, 0
        );
    }

}
