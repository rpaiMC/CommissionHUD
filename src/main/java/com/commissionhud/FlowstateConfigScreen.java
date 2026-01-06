package com.commissionhud;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class FlowstateConfigScreen extends Screen {
    private final Screen parent;
    private int dragX, dragY;
    private boolean dragging = false;
    
    private static final String MINING_ICON = "⛏";
    
    public FlowstateConfigScreen(Screen parent) {
        super(Text.literal("Flowstate Config"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        ConfigManager.Config cfg = CommissionHudMod.config.getConfig();
        
        int centerX = width / 2;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 24;
        int startY = 35;
        
        // Toggle enabled
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Enabled: " + cfg.flowstateEnabled),
            button -> {
                cfg.flowstateEnabled = !cfg.flowstateEnabled;
                CommissionHudMod.config.save();
                button.setMessage(Text.literal("Enabled: " + cfg.flowstateEnabled));
            })
            .dimensions(centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight)
            .build());
        
        // Title color picker button
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Title Color: #" + String.format("%06X", cfg.flowstateTitleColor)),
            button -> {
                if (client != null) {
                    client.setScreen(new ColorPickerScreen(this, ColorPickerScreen.ColorType.FLOWSTATE_TITLE_COLOR));
                }
            })
            .dimensions(centerX - buttonWidth / 2, startY + spacing, buttonWidth, buttonHeight)
            .build());
        
        // Label color picker button
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Label Color: #" + String.format("%06X", cfg.flowstateLabelColor)),
            button -> {
                if (client != null) {
                    client.setScreen(new ColorPickerScreen(this, ColorPickerScreen.ColorType.FLOWSTATE_LABEL_COLOR));
                }
            })
            .dimensions(centerX - buttonWidth / 2, startY + spacing * 2, buttonWidth, buttonHeight)
            .build());
        
        // Value color picker button
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Value Color: #" + String.format("%06X", cfg.flowstateValueColor)),
            button -> {
                if (client != null) {
                    client.setScreen(new ColorPickerScreen(this, ColorPickerScreen.ColorType.FLOWSTATE_VALUE_COLOR));
                }
            })
            .dimensions(centerX - buttonWidth / 2, startY + spacing * 3, buttonWidth, buttonHeight)
            .build());
        
        // Back button
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> close())
            .dimensions(centerX - buttonWidth / 2, height - 28, buttonWidth, buttonHeight)
            .build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFF);
        
        ConfigManager.Config cfg = CommissionHudMod.config.getConfig();
        
        // Draw color preview squares
        int colorPreviewX = width / 2 + 105;
        
        // Title color preview
        drawColorPreview(context, colorPreviewX, 35 + 24, cfg.flowstateTitleColor);
        
        // Label color preview
        drawColorPreview(context, colorPreviewX, 35 + 24 * 2, cfg.flowstateLabelColor);
        
        // Value color preview
        drawColorPreview(context, colorPreviewX, 35 + 24 * 3, cfg.flowstateValueColor);
        
        // Instructions
        context.drawCenteredTextWithShadow(textRenderer, 
            Text.literal("Drag the preview to reposition"), 
            width / 2, height - 55, 0x888888);
        
        super.render(context, mouseX, mouseY, delta);
        
        // Render flowstate preview
        context.getMatrices().push();
        context.getMatrices().translate(cfg.flowstateX, cfg.flowstateY, 0);
        context.getMatrices().scale(cfg.flowstateScale, cfg.flowstateScale, 1.0f);
        
        // Title with example mining speed bonus
        context.drawText(textRenderer, Text.literal("Flowstate: +150 " + MINING_ICON), 0, 0, cfg.flowstateTitleColor, true);
        
        // Blocks counter
        String blocksLabel = "Blocks: ";
        String blocksValue = "150/200";
        int blocksLabelWidth = textRenderer.getWidth(blocksLabel);
        context.drawText(textRenderer, Text.literal(blocksLabel), 0, 12, cfg.flowstateLabelColor, true);
        context.drawText(textRenderer, Text.literal(blocksValue), blocksLabelWidth, 12, 0xFFFF55, true);
        
        // Reset timer
        String resetLabel = "Reset: ";
        String resetValue = "8s";
        int resetLabelWidth = textRenderer.getWidth(resetLabel);
        context.drawText(textRenderer, Text.literal(resetLabel), 0, 22, cfg.flowstateLabelColor, true);
        context.drawText(textRenderer, Text.literal(resetValue), resetLabelWidth, 22, cfg.flowstateValueColor, true);
        
        context.getMatrices().pop();
    }
    
    private void drawColorPreview(DrawContext context, int x, int y, int color) {
        context.fill(x, y + 2, x + 16, y + 18, 0xFF000000 | color);
        // Border
        context.fill(x - 1, y + 1, x + 17, y + 2, 0xFF333333);
        context.fill(x - 1, y + 18, x + 17, y + 19, 0xFF333333);
        context.fill(x - 1, y + 1, x, y + 19, 0xFF333333);
        context.fill(x + 16, y + 1, x + 17, y + 19, 0xFF333333);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ConfigManager.Config cfg = CommissionHudMod.config.getConfig();
        int previewWidth = 120;
        int previewHeight = 35;
        
        if (mouseX >= cfg.flowstateX && mouseX <= cfg.flowstateX + previewWidth * cfg.flowstateScale && 
            mouseY >= cfg.flowstateY && mouseY <= cfg.flowstateY + previewHeight * cfg.flowstateScale) {
            dragging = true;
            dragX = (int) (mouseX - cfg.flowstateX);
            dragY = (int) (mouseY - cfg.flowstateY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            CommissionHudMod.config.setFlowstatePosition((int) mouseX - dragX, (int) mouseY - dragY);
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
