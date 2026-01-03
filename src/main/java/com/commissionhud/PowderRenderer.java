package com.commissionhud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class PowderRenderer {
    public static void render(DrawContext context, PowderManager powderManager) {
        if (!powderManager.hasPowderData()) {
            return;
        }
        
        ConfigManager.Config cfg = CommissionHudMod.config.getConfig();
        MinecraftClient client = MinecraftClient.getInstance();
        
        context.getMatrices().push();
        context.getMatrices().translate(cfg.powderX, cfg.powderY, 0);
        context.getMatrices().scale(cfg.powderScale, cfg.powderScale, 1.0f);
        
        int y = 0;
        
        // Title
        context.drawText(client.textRenderer, Text.literal("Powder:"), 0, y, cfg.powderTitleColor, true);
        y += 12;
        
        // Mithril Powder
        String mithrilLabel = "Mithril: ";
        String mithrilValue = powderManager.getMithrilPowder();
        int mithrilLabelWidth = client.textRenderer.getWidth(mithrilLabel);
        context.drawText(client.textRenderer, Text.literal(mithrilLabel), 0, y, cfg.powderLabelColor, true);
        context.drawText(client.textRenderer, Text.literal(mithrilValue), mithrilLabelWidth, y, cfg.powderValueColor, true);
        y += 10;
        
        // Gemstone Powder
        String gemstoneLabel = "Gemstone: ";
        String gemstoneValue = powderManager.getGemstonePowder();
        int gemstoneLabelWidth = client.textRenderer.getWidth(gemstoneLabel);
        context.drawText(client.textRenderer, Text.literal(gemstoneLabel), 0, y, cfg.powderLabelColor, true);
        context.drawText(client.textRenderer, Text.literal(gemstoneValue), gemstoneLabelWidth, y, cfg.powderValueColor, true);
        y += 10;
        
        // Glacite Powder (only show if non-zero)
        String glaciteValue = powderManager.getGlacitePowder();
        if (!glaciteValue.equals("0")) {
            String glaciteLabel = "Glacite: ";
            int glaciteLabelWidth = client.textRenderer.getWidth(glaciteLabel);
            context.drawText(client.textRenderer, Text.literal(glaciteLabel), 0, y, cfg.powderLabelColor, true);
            context.drawText(client.textRenderer, Text.literal(glaciteValue), glaciteLabelWidth, y, cfg.powderValueColor, true);
        }
        
        context.getMatrices().pop();
    }
}
