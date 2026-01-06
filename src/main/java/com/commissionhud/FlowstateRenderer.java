package com.commissionhud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class FlowstateRenderer {
    // Mining speed icon (pickaxe) - using ⛏ Unicode character
    private static final String MINING_ICON = "⛏";
    
    public static void render(DrawContext context, FlowstateManager flowstateManager) {
        ConfigManager.Config cfg = CommissionHudMod.config.getConfig();
        MinecraftClient client = MinecraftClient.getInstance();
        
        context.getMatrices().push();
        context.getMatrices().translate(cfg.flowstateX, cfg.flowstateY, 0);
        context.getMatrices().scale(cfg.flowstateScale, cfg.flowstateScale, 1.0f);
        
        int y = 0;
        
        // Title with mining speed bonus
        int bonus = flowstateManager.getMiningSpeedBonus();
        String titleText = "Flowstate: +" + bonus + " " + MINING_ICON;
        int titleColor = bonus > 0 ? cfg.flowstateTitleColor : 0x888888; // Gray if inactive
        context.drawText(client.textRenderer, Text.literal(titleText), 0, y, titleColor, true);
        y += 12;
        
        // Blocks counter
        int blocks = flowstateManager.getBlockCount();
        int maxBlocks = flowstateManager.getMaxBlocks();
        String blocksLabel = "Blocks: ";
        String blocksValue = blocks + "/" + maxBlocks;
        int blocksLabelWidth = client.textRenderer.getWidth(blocksLabel);
        
        context.drawText(client.textRenderer, Text.literal(blocksLabel), 0, y, cfg.flowstateLabelColor, true);
        
        // Color the value based on progress
        int blocksValueColor = cfg.flowstateValueColor;
        if (blocks >= maxBlocks) {
            blocksValueColor = 0x55FF55; // Green when maxed
        } else if (blocks >= maxBlocks * 0.75) {
            blocksValueColor = 0xFFFF55; // Yellow when near max
        }
        context.drawText(client.textRenderer, Text.literal(blocksValue), blocksLabelWidth, y, blocksValueColor, true);
        y += 10;
        
        // Reset timer
        int seconds = flowstateManager.getSecondsUntilReset();
        String resetLabel = "Reset: ";
        String resetValue = seconds + "s";
        int resetLabelWidth = client.textRenderer.getWidth(resetLabel);
        
        context.drawText(client.textRenderer, Text.literal(resetLabel), 0, y, cfg.flowstateLabelColor, true);
        
        // Color the timer based on urgency
        int resetValueColor = cfg.flowstateValueColor;
        if (seconds <= 3 && seconds > 0) {
            resetValueColor = 0xFF5555; // Red when about to reset
        } else if (seconds <= 5 && seconds > 0) {
            resetValueColor = 0xFFFF55; // Yellow when getting low
        }
        context.drawText(client.textRenderer, Text.literal(resetValue), resetLabelWidth, y, resetValueColor, true);
        
        context.getMatrices().pop();
    }
}
