package com.commissionhud.mixin;

import com.commissionhud.CommissionHudMod;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class BlockBreakMixin {
    
    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void commissionhud$onBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        // Notify flowstate manager when a block is broken
        if (CommissionHudMod.flowstateManager != null) {
            CommissionHudMod.flowstateManager.onBlockBreak();
        }
    }
}
