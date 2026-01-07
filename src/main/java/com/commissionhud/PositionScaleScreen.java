package com.commissionhud;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class PositionScaleScreen extends Screen {
    private final Screen parent;
    
    // Dragging state
    private enum DragTarget { NONE, COMMISSION, POWDER, ABILITY }
    private DragTarget dragging = DragTarget.NONE;
    private int dragOffsetX, dragOffsetY;
    
    public PositionScaleScreen(Screen parent) {
        super(Text.literal("Position & Scale Settings"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        ConfigManager.Config cfg = CommissionHudMod.config.getConfig();
        
        int centerX = width / 2;
        int buttonWidth = 120;
        int buttonHeight = 20;
        int sliderWidth = 120;
        
        // Left side - Commission settings
        int leftX = centerX - 180;
        
        addDrawableChild(new SliderWidget(leftX - sliderWidth / 2, height - 75, sliderWidth, buttonHeight,
            Text.literal("Comm Scale: " + String.format("%.1f", cfg.scale)), (cfg.scale - 0.5) / 1.5) {
            @Override
            protected void updateMessage() {
                cfg.scale = (float) (0.5 + value * 1.5);
                setMessage(Text.literal("Comm Scale: " + String.format("%.1f", cfg.scale)));
            }
            
            @Override
            protected void applyValue() {
                CommissionHudMod.config.save();
            }
        });
        
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset Comm"), button -> {
            CommissionHudMod.config.setPosition(10, 10);
        }).dimensions(leftX - buttonWidth / 2, height - 50, buttonWidth, buttonHeight).build());
        
        // Center - Powder settings
        addDrawableChild(new SliderWidget(centerX - sliderWidth / 2, height - 75, sliderWidth, buttonHeight,
            Text.literal("Powder Scale: " + String.format("%.1f", cfg.powderScale)), (cfg.powderScale - 0.5) / 1.5) {
            @Override
            protected void updateMessage() {
                cfg.powderScale = (float) (0.5 + value * 1.5);
                setMessage(Text.literal("Powder Scale: " + String.format("%.1f", cfg.powderScale)));
            }
            
            @Override
            protected void applyValue() {
                CommissionHudMod.config.save();
            }
        });
        
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset Powder"), button -> {
            CommissionHudMod.config.setPowderPosition(10, 150);
        }).dimensions(centerX - buttonWidth / 2, height - 50, buttonWidth, buttonHeight).build());
        
        // Right side - Ability settings
        int rightX = centerX + 180;
        
        addDrawableChild(new SliderWidget(rightX - sliderWidth / 2, height - 75, sliderWidth, buttonHeight,
            Text.literal("Ability Scale: " + String.format("%.1f", cfg.abilityScale)), (cfg.abilityScale - 0.5) / 1.5) {
            @Override
            protected void updateMessage() {
                cfg.abilityScale = (float) (0.5 + value * 1.5);
                setMessage(Text.literal("Ability Scale: " + String.format("%.1f", cfg.abilityScale)));
            }
            
            @Override
            protected void applyValue() {
                CommissionHudMod.config.save();
            }
        });
        
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset Ability"), button -> {
            CommissionHudMod.config.setAbilityPosition(10, 200);
        }).dimensions(rightX - buttonWidth / 2, height - 50, buttonWidth, buttonHeight).build());
        
        // Back button - center bottom
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> close())
            .dimensions(centerX - 50, height - 25, 100, buttonHeight)
            .build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Drag previews to reposition"), width / 2, 22, 0x888888);
        
        ConfigManager.Config cfg = CommissionHudMod.config.getConfig();
        
        super.render(context, mouseX, mouseY, delta);
        
        // Render Commission preview
        renderCommissionPreview(context, cfg);
        
        // Render Powder preview
        renderPowderPreview(context, cfg);
        
        // Render Ability preview
        renderAbilityPreview(context, cfg);
    }
    
    private void renderCommissionPreview(DrawContext context, ConfigManager.Config cfg) {
        context.getMatrices().push();
        context.getMatrices().translate(cfg.x, cfg.y, 0);
        context.getMatrices().scale(cfg.scale, cfg.scale, 1.0f);
        
        int y = 0;
        context.drawText(textRenderer, Text.literal("Commissions:"), 0, y, cfg.titleColor, true);
        y += 12;
        
        // Example commission 1
        String example1 = "• Mithril Miner";
        if (cfg.showPercentage) {
            example1 += cfg.progressFormat == ConfigManager.ProgressFormat.PERCENTAGE ? ": 45%" : ": 158/350";
        }
        context.drawText(textRenderer, Text.literal(example1), 0, y, cfg.color, true);
        if (cfg.showPercentage) {
            context.fill(0, y + 9, 100, y + 11, 0x88000000);
            context.fill(0, y + 9, 45, y + 11, 0xFF000000 | cfg.progressBarColor);
        }
        y += cfg.showPercentage ? 14 : 10;
        
        // Example commission 2
        String example2 = "• Goblin Slayer";
        if (cfg.showPercentage) {
            example2 += cfg.progressFormat == ConfigManager.ProgressFormat.PERCENTAGE ? ": 80%" : ": 80/100";
        }
        context.drawText(textRenderer, Text.literal(example2), 0, y, 0xFFFF55, true);
        if (cfg.showPercentage) {
            context.fill(0, y + 9, 100, y + 11, 0x88000000);
            context.fill(0, y + 9, 80, y + 11, 0xFF000000 | cfg.progressBarColor);
        }
        y += cfg.showPercentage ? 14 : 10;
        
        // Example commission 3
        String example3 = "• Titanium Miner";
        if (cfg.showPercentage) {
            example3 += cfg.progressFormat == ConfigManager.ProgressFormat.PERCENTAGE ? ": 100%" : ": 15/15";
        }
        context.drawText(textRenderer, Text.literal(example3), 0, y, 0x55FF55, true);
        if (cfg.showPercentage) {
            context.fill(0, y + 9, 100, y + 11, 0x88000000);
            context.fill(0, y + 9, 100, y + 11, 0xFF55FF55);
        }
        
        context.getMatrices().pop();
    }
    
    private void renderPowderPreview(DrawContext context, ConfigManager.Config cfg) {
        context.getMatrices().push();
        context.getMatrices().translate(cfg.powderX, cfg.powderY, 0);
        context.getMatrices().scale(cfg.powderScale, cfg.powderScale, 1.0f);
        
        context.drawText(textRenderer, Text.literal("Powder:"), 0, 0, cfg.powderTitleColor, true);
        
        String mithrilLabel = "Mithril: ";
        String mithrilValue = "123,456";
        int mithrilLabelWidth = textRenderer.getWidth(mithrilLabel);
        context.drawText(textRenderer, Text.literal(mithrilLabel), 0, 12, cfg.powderLabelColor, true);
        context.drawText(textRenderer, Text.literal(mithrilValue), mithrilLabelWidth, 12, cfg.powderValueColor, true);
        
        String gemstoneLabel = "Gemstone: ";
        String gemstoneValue = "78,901";
        int gemstoneLabelWidth = textRenderer.getWidth(gemstoneLabel);
        context.drawText(textRenderer, Text.literal(gemstoneLabel), 0, 22, cfg.powderLabelColor, true);
        context.drawText(textRenderer, Text.literal(gemstoneValue), gemstoneLabelWidth, 22, cfg.powderValueColor, true);
        
        context.getMatrices().pop();
    }
    
    private void renderAbilityPreview(DrawContext context, ConfigManager.Config cfg) {
        context.getMatrices().push();
        context.getMatrices().translate(cfg.abilityX, cfg.abilityY, 0);
        context.getMatrices().scale(cfg.abilityScale, cfg.abilityScale, 1.0f);
        
        context.drawText(textRenderer, Text.literal("Pickaxe Ability:"), 0, 0, cfg.abilityTitleColor, true);
        
        String nameLabel = "Pickobulus: ";
        String statusText = "Available";
        int nameLabelWidth = textRenderer.getWidth(nameLabel);
        context.drawText(textRenderer, Text.literal(nameLabel), 0, 12, cfg.abilityLabelColor, true);
        context.drawText(textRenderer, Text.literal(statusText), nameLabelWidth, 12, 0x55FF55, true);
        
        context.getMatrices().pop();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ConfigManager.Config cfg = CommissionHudMod.config.getConfig();
        
        // Check if clicking on commission preview
        int commissionWidth = (int)(120 * cfg.scale);
        int commissionHeight = (int)(60 * cfg.scale);
        if (mouseX >= cfg.x && mouseX <= cfg.x + commissionWidth &&
            mouseY >= cfg.y && mouseY <= cfg.y + commissionHeight) {
            dragging = DragTarget.COMMISSION;
            dragOffsetX = (int)(mouseX - cfg.x);
            dragOffsetY = (int)(mouseY - cfg.y);
            return true;
        }
        
        // Check if clicking on powder preview
        int powderWidth = (int)(100 * cfg.powderScale);
        int powderHeight = (int)(35 * cfg.powderScale);
        if (mouseX >= cfg.powderX && mouseX <= cfg.powderX + powderWidth &&
            mouseY >= cfg.powderY && mouseY <= cfg.powderY + powderHeight) {
            dragging = DragTarget.POWDER;
            dragOffsetX = (int)(mouseX - cfg.powderX);
            dragOffsetY = (int)(mouseY - cfg.powderY);
            return true;
        }
        
        // Check if clicking on ability preview
        int abilityWidth = (int)(120 * cfg.abilityScale);
        int abilityHeight = (int)(25 * cfg.abilityScale);
        if (mouseX >= cfg.abilityX && mouseX <= cfg.abilityX + abilityWidth &&
            mouseY >= cfg.abilityY && mouseY <= cfg.abilityY + abilityHeight) {
            dragging = DragTarget.ABILITY;
            dragOffsetX = (int)(mouseX - cfg.abilityX);
            dragOffsetY = (int)(mouseY - cfg.abilityY);
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = DragTarget.NONE;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging == DragTarget.COMMISSION) {
            CommissionHudMod.config.setPosition((int)mouseX - dragOffsetX, (int)mouseY - dragOffsetY);
            return true;
        } else if (dragging == DragTarget.POWDER) {
            CommissionHudMod.config.setPowderPosition((int)mouseX - dragOffsetX, (int)mouseY - dragOffsetY);
            return true;
        } else if (dragging == DragTarget.ABILITY) {
            CommissionHudMod.config.setAbilityPosition((int)mouseX - dragOffsetX, (int)mouseY - dragOffsetY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
}
