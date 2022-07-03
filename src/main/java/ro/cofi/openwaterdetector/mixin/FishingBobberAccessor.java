package ro.cofi.openwaterdetector.mixin;

import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FishingBobberEntity.class)
public interface FishingBobberAccessor {

    @Accessor
    FishingBobberEntity.State getState();

    @Invoker
    boolean invokeIsOpenOrWaterAround(BlockPos pos);

}
