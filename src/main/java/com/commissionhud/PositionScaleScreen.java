package com.commissionhud;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class PositionScaleScreen extends Screen {
    private final Screen parent;
    
    private static final String MINING_ICON = "⛏";
    
    // Dragging state
    private enum DragTarget { NONE, COMMISSION, POWDER, ABILITY, FLOWSTATE }
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
        int buttonWidth = 100;
        int buttonHeight = 20;
        int sliderWidth = 100;
        
        // Row 1: Commission and Powder
        int row1Y = height - 95;
        int leftX = centerX - 160;
        int rightX = centerX + 160;
        
        // Commission scale
        addDrawableChild(new SliderWidget(leftX - sliderWidth / 2, row1Y, sliderWidth, buttonHeight,
            Text.literal("Comm: " + String.format("%.1f", cfg.scale)), (cfg.scale - 0.5) / 1.5) {
            @Override
            protected void updateMessage() {
                cfg.scale = (float) (0.5 + value * 1.5);
                setMessage(Text.literal("Comm: " + String.format("%.1f", cfg.scale)));
            }
            @Override
            protected void applyValue() { CommissionHudMod.config.save(); }
        });
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), button -> {
            CommissionHudMod.config.setPosition(10, 10);
        }).dimensions(leftX - buttonWidth / 2, row1Y + 22, buttonWidth, buttonHeight).build());
        
        // Powder scale
        addDrawableChild(new SliderWidget(rightX - sliderWidth / 2, row1Y, sliderWidth, buttonHeight,
            Text.literal("Powder: " + String.format("%.1f", cfg.powderScale)), (cfg.powderScale - 0.5) / 1.5) {
            @Override
            protected void updateMessage() {
                cfg.powderScale = (float) (0.5 + value * 1.5);
                setMessage(Text.literal("Powder: " + String.format("%.1f", cfg.powderScale)));
            }
            @Override
            protected void applyValue() { CommissionHudMod.config.save(); }
        });
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), button -> {
            CommissionHudMod.config.setPowderPosition(10, 150);
        }).dimensions(rightX - buttonWidth / 2, row1Y + 22, buttonWidth, buttonHeight).build());
        
        // Row 2: Ability and Flowstate
        int row2Y = height - 50;
        
        // Ability scale
        addDrawableChild(new SliderWidget(leftX - sliderWidth / 2, row2Y, sliderWidth, buttonHeight,
            Text.literal("Ability: " + String.format("%.1f", cfg.abilityScale)), (cfg.abilityScale - 0.5) / 1.5) {
            @Override
            protected void updateMessage() {
                cfg.abilityScale = (float) (0.5 + value * 1.5);
                setMessage(Text.literal("Ability: " + String.format("%.1f", cfg.abilityScale)));
            }
            @Override
            protected void applyValue() { CommissionHudMod.config.save(); }
        });
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), button -> {
            CommissionHudMod.config.setAbilityPosition(10, 200);
        }).dimensions(leftX + sliderWidth / 2 + 5, row2Y, buttonWidth - 20, buttonHeight).build());
        
        // Flowstate scale
        addDrawableChild(new SliderWidget(rightX - sliderWidth / 2, row2Y, sliderWidth, buttonHeight,
            Text.literal("Flow: " + String.format("%.1f", cfg.flowstateScale)), (cfg.flowstateScale - 0.5) / 1.5) {
            @Override
            protected void updateMessage() {
                cfg.flowstateScale = (float) (0.5 + value * 1.5);
                setMessage(Text.literal("Flow: " + String.format("%.1f", cfg.flowstateScale)));
            }
            @Override
            protected void applyValue() { CommissionHudMod.config.save(); }
        });
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), button -> {
            CommissionHudMod.config.setFlowstatePosition(10, 250);
        }).dimensions(rightX + sliderWidth / 2 + 5, row2Y, buttonWidth - 20, buttonHeight).build());
        
        // Done button
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
        
        // Render all previews
        renderCommissionPreview(context, cfg);
        renderPowderPreview(context, cfg);
        renderAbilityPreview(context, cfg);
        renderFlowstatePreview(context, cfg);
    }
    
    private void renderCommissionPreview(DrawContext context, ConfigManager.Config cfg) {
        context.getMatrices().push();
        context.getMatrices().translate(cfg.x, cfg.y, 0);
        context.getMatrices().scale(cfg.scale, cfg.scale, 1.0f);
        
        int y = 0;
        context.drawText(textRenderer, Text.literal("Commissions:"), 0, y, cfg.titleColor, true);
        y += 12;
        
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
        
        String example2 = "• Goblin Slayer";
        if (cfg.showPercentage) {
            example2 += cfg.progressFormat == ConfigManager.ProgressFormat.PERCENTAGE ? ": 80%" : ": 80/100";
        }
        context.drawText(textRenderer, Text.literal(example2), 0, y, 0xFFFF55, true);
        if (cfg.showPercentage) {
            context.fill(0, y + 9, 100, y + 11, 0x88000000);
            context.fill(0, y + 9, 80, y + 11, 0xFF000000 | cfg.progressBarColor);
        }
        
        context.getMatrices().pop();
    }
    
    private void renderPowderPreview(DrawContext context, ConfigManager.Config cfg) {
        context.getMatrices().push();
        context.getMatrices().translate(cfg.powderX, cfg.powderY, 0);
        context.getMatrices().scale(cfg.powderScale, cfg.powderScale, 1.0f);
        
        context.drawText(textRenderer, Text.literal("Powder:"), 0, 0, cfg.powderTitleColor, true);
        
        String mithrilLabel = "Mithril: ";
        int mithrilLabelWidth = textRenderer.getWidth(mithrilLabel);
        context.drawText(textRenderer, Text.literal(mithrilLabel), 0, 12, cfg.powderLabelColor, true);
        context.drawText(textRenderer, Text.literal("123,456"), mithrilLabelWidth, 12, cfg.powderValueColor, true);
        
        String gemstoneLabel = "Gemstone: ";
        int gemstoneLabelWidth = textRenderer.getWidth(gemstoneLabel);
        context.drawText(textRenderer, Text.literal(gemstoneLabel), 0, 22, cfg.powderLabelColor, true);
        context.drawText(textRenderer, Text.literal("78,901"), gemstoneLabelWidth, 22, cfg.powderValueColor, true);
        
        context.getMatrices().pop();
    }
    
    private void renderAbilityPreview(DrawContext context, ConfigManager.Config cfg) {
        context.getMatrices().push();
        context.getMatrices().translate(cfg.abilityX, cfg.abilityY, 0);
        context.getMatrices().scale(cfg.abilityScale, cfg.abilityScale, 1.0f);
        
        context.drawText(textRenderer, Text.literal("Pickaxe Ability:"), 0, 0, cfg.abilityTitleColor, true);
        
        String nameLabel = "Pickobulus: ";
        int nameLabelWidth = textRenderer.getWidth(nameLabel);
        context.drawText(textRenderer, Text.literal(nameLabel), 0, 12, cfg.abilityLabelColor, true);
        context.drawText(textRenderer, Text.literal("Available"), nameLabelWidth, 12, 0x55FF55, true);
        
        context.getMatrices().pop();
    }
    
    private void renderFlowstatePreview(DrawContext context, ConfigManager.Config cfg) {
        context.getMatrices().push();
        context.getMatrices().translate(cfg.flowstateX, cfg.flowstateY, 0);
        context.getMatrices().scale(cfg.flowstateScale, cfg.flowstateScale, 1.0f);
        
        context.drawText(textRenderer, Text.literal("Flowstate: +150 " + MINING_ICON), 0, 0, cfg.flowstateTitleColor, true);
        
        String blocksLabel = "Blocks: ";
        int blocksLabelWidth = textRenderer.getWidth(blocksLabel);
        context.drawText(textRenderer, Text.literal(blocksLabel), 0, 12, cfg.flowstateLabelColor, true);
        context.drawText(textRenderer, Text.literal("150/200"), blocksLabelWidth, 12, 0xFFFF55, true);
        
        String resetLabel = "Reset: ";
        int resetLabelWidth = textRenderer.getWidth(resetLabel);
        context.drawText(textRenderer, Text.literal(resetLabel), 0, 22, cfg.flowstateLabelColor, true);
        context.drawText(textRenderer, Text.literal("8s"), resetLabelWidth, 22, cfg.flowstateValueColor, true);
        
        context.getMatrices().pop();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ConfigManager.Config cfg = CommissionHudMod.config.getConfig();
        
        // Check commission preview
        int commissionWidth = (int)(120 * cfg.scale);
        int commissionHeight = (int)(45 * cfg.scale);
        if (mouseX >= cfg.x && mouseX <= cfg.x + commissionWidth &&
            mouseY >= cfg.y && mouseY <= cfg.y + commissionHeight) {
            dragging = DragTarget.COMMISSION;
            dragOffsetX = (int)(mouseX - cfg.x);
            dragOffsetY = (int)(mouseY - cfg.y);
            return true;
        }
        
        // Check powder preview
        int powderWidth = (int)(100 * cfg.powderScale);
        int powderHeight = (int)(35 * cfg.powderScale);
        if (mouseX >= cfg.powderX && mouseX <= cfg.powderX + powderWidth &&
            mouseY >= cfg.powderY && mouseY <= cfg.powderY + powderHeight) {
            dragging = DragTarget.POWDER;
            dragOffsetX = (int)(mouseX - cfg.powderX);
            dragOffsetY = (int)(mouseY - cfg.powderY);
            return true;
        }
        
        // Check ability preview
        int abilityWidth = (int)(120 * cfg.abilityScale);
        int abilityHeight = (int)(25 * cfg.abilityScale);
        if (mouseX >= cfg.abilityX && mouseX <= cfg.abilityX + abilityWidth &&
            mouseY >= cfg.abilityY && mouseY <= cfg.abilityY + abilityHeight) {
            dragging = DragTarget.ABILITY;
            dragOffsetX = (int)(mouseX - cfg.abilityX);
            dragOffsetY = (int)(mouseY - cfg.abilityY);
            return true;
        }
        
        // Check flowstate preview
        int flowstateWidth = (int)(120 * cfg.flowstateScale);
        int flowstateHeight = (int)(35 * cfg.flowstateScale);
        if (mouseX >= cfg.flowstateX && mouseX <= cfg.flowstateX + flowstateWidth &&
            mouseY >= cfg.flowstateY && mouseY <= cfg.flowstateY + flowstateHeight) {
            dragging = DragTarget.FLOWSTATE;
            dragOffsetX = (int)(mouseX - cfg.flowstateX);
            dragOffsetY = (int)(mouseY - cfg.flowstateY);
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
        switch (dragging) {
            case COMMISSION:
                CommissionHudMod.config.setPosition((int)mouseX - dragOffsetX, (int)mouseY - dragOffsetY);
                return true;
            case POWDER:
                CommissionHudMod.config.setPowderPosition((int)mouseX - dragOffsetX, (int)mouseY - dragOffsetY);
                return true;
            case ABILITY:
                CommissionHudMod.config.setAbilityPosition((int)mouseX - dragOffsetX, (int)mouseY - dragOffsetY);
                return true;
            case FLOWSTATE:
                CommissionHudMod.config.setFlowstatePosition((int)mouseX - dragOffsetX, (int)mouseY - dragOffsetY);
                return true;
            default:
                return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }
    
    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
}
