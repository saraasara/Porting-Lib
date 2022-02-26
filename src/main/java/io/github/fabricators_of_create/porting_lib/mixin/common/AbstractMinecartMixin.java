package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.MinecartEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.block.MinecartPassHandlerBlock;
import io.github.fabricators_of_create.porting_lib.extensions.AbstractMinecartExtensions;
import io.github.fabricators_of_create.porting_lib.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.util.NBTSerializable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity implements AbstractMinecartExtensions, NBTSerializable {
	@Unique
	public boolean port_lib$canUseRail = true;

	private AbstractMinecartMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}

	@Shadow
	protected abstract double getMaxSpeed();

	@Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
	public void port_lib$abstractMinecartEntity(EntityType<?> entityType, Level world, CallbackInfo ci) {
		MinecartEvents.SPAWN.invoker().minecartSpawn((AbstractMinecart) (Object) this, world);
	}

	@Inject(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I", ordinal = 4))
	protected void port_lib$moveAlongTrack(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		if (blockState.getBlock() instanceof MinecartPassHandlerBlock handler) {
			handler.onMinecartPass(blockState, level, blockPos, MixinHelper.cast(this));
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void port_lib$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
		compound.put("Controller", port_lib$controllerCap.port_lib$serializeNBT());
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void port_lib$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
		port_lib$controllerCap.port_lib$deserializeNBT(compound.getCompound("Controller"));
	}

	@Override
	public void port_lib$moveMinecartOnRail(BlockPos pos) {
		double d24 = isVehicle() ? 0.75D : 1.0D;
		double d25 = getMaxSpeed(); // getMaxSpeed instead of getMaxSpeedWithRail *should* be fine after intense pain looking at Forge patches
		Vec3 vec3d1 = getDeltaMovement();
		move(MoverType.SELF, new Vec3(Mth.clamp(d24 * vec3d1.x, -d25, d25), 0.0D, Mth.clamp(d24 * vec3d1.z, -d25, d25)));
	}

	@Override
	public boolean port_lib$canUseRail() {
		return port_lib$canUseRail;
	}

	@Override
	public BlockPos port_lib$getCurrentRailPos() {
		BlockPos pos = new BlockPos(Mth.floor(getX()), Mth.floor(getY()), Mth.floor(getZ()));
		BlockPos below = pos.below();
		if (level.getBlockState(below).is(BlockTags.RAILS)) {
			pos = below;
		}

		return pos;
	}
}
