package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;

@Environment(EnvType.CLIENT)
@Mixin(Particle.class)
public interface ParticleAccessor {
	@Accessor("stoppedByCollision")
	void port_lib$stoppedByCollision(boolean stoppedByCollision);
}
