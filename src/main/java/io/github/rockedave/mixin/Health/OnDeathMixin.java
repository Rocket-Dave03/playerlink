package io.github.rockedave.mixin.Health;


import io.github.rockedave.Main;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class OnDeathMixin {
	
	@Inject(method = "onDeath", at = @At("HEAD"))
	private void onDeath(DamageSource damageSource, CallbackInfo ci) {
		ServerPlayerEntity self = (ServerPlayerEntity)(Object) this;
		if (Main.isLinked(self)) {
			Main.killInLink(self);
		}
	}
}
