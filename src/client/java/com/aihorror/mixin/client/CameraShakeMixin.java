package com.aihorror.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraShakeMixin {
    @Shadow public abstract Vec3 position();
    @Shadow protected abstract void setPosition(Vec3 pos);

    @Inject(method = "tick", at = @At("TAIL"))
    private void aihorror$cameraShake(CallbackInfo ci) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;
            if (mc.player.hasEffect(net.minecraft.world.effect.MobEffects.NAUSEA) || mc.player.hasEffect(net.minecraft.world.effect.MobEffects.DARKNESS)) {
                double shake = 0.06;
                if (mc.player.hasEffect(net.minecraft.world.effect.MobEffects.NAUSEA)) shake *= 1.5;
                double dx = (mc.level.getRandom().nextDouble() - 0.5) * shake;
                double dy = (mc.level.getRandom().nextDouble() - 0.5) * shake;
                double dz = (mc.level.getRandom().nextDouble() - 0.5) * shake;
                setPosition(position().add(dx, dy, dz));
            }
        } catch (Exception e) {}
    }
}