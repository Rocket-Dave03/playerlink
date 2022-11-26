package io.github.rockedave.mixin.Health;


import io.github.rockedave.Main;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
	private void getHealth(CallbackInfoReturnable<Float> cir) {
		LivingEntity e = (LivingEntity)(Object) this;
		if (e instanceof ServerPlayerEntity) {
			if (Main.isLinked((ServerPlayerEntity) e)) {
				if (Main.isAlive((ServerPlayerEntity) e)) {
					cir.setReturnValue(Main.getLink((ServerPlayerEntity) e).getHealth((ServerPlayerEntity) e));
				} else {
					cir.setReturnValue(e.dataTracker.get(LivingEntity.HEALTH));
				}
			}
		}
	}
	@Inject(method = "setHealth", at = @At("HEAD"))
	private void setHealth(float health, CallbackInfo ci) {
		LivingEntity e = (LivingEntity)(Object) this;
		if (e instanceof ServerPlayerEntity) {
			if (Main.isLinked((ServerPlayerEntity) e)) {
				if (Main.isAlive((ServerPlayerEntity) e)) {
					float h = MathHelper.clamp(health, 0.0f, e.getMaxHealth());
					Main.getLink((ServerPlayerEntity) e).setHealth((ServerPlayerEntity)e,  h);
				}
			}
		}
	}
}
