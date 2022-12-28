package io.github.fabricators_of_create.porting_lib.fake_players.extensions;

import net.minecraft.server.level.ServerPlayer;

public interface ServerPlayerExtensions {
	default boolean isFakePlayer() {
		return getClass() != ServerPlayer.class;
	}
}
