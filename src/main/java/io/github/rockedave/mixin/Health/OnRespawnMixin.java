package io.github.rockedave.mixin.Health;

import io.github.rockedave.Main;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerManager.class)
public class OnRespawnMixin {
	
	@Inject(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onRespawn(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir, BlockPos blockPos, float f, boolean bl, ServerWorld serverWorld, Optional optional, ServerWorld serverWorld2, ServerPlayerEntity serverPlayerEntity) {
		if (Main.isLinked(serverPlayerEntity) ) {
			Main.respawnPlayer(serverPlayerEntity);
//			serverPlayerEntity.sendMessage(Text.of("Respawned"));
		}
	}
}
