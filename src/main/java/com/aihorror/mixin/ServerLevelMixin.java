package com.aihorror.mixin;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    // Simple mixin to prove refmap works - hooks tick for AI (actual logic in SurveillanceAI via events)
    @Inject(method = "tick", at = @At("HEAD"))
    private void aihorror$onTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        // No-op, just ensures mixin refmap is non-empty and working
        // Real AI tick is via ServerTickEvents in AiHorror.java
    }
}