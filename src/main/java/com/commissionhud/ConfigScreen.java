package com.commissionhud;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private int dragX, dragY;
    private boolean dragging = false;
    
    public ConfigScreen() {
        super(Text.literal("Commission HUD Config"));
        this.parent = null;
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    protected void init() {
        // Get fresh config values each time init is called
        ConfigManager.Config cfg = CommissionHudMod.config.getConfig();
        
        int centerX = width / 2;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 24;
        int startY = 35;
        
        // Toggle enabled
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Enabled: " + cfg.enabled),
            button -> {
                cfg.enabled = !cfg.enabled;
                CommissionHudMod.config.save();
                button.setMessage(Text.literal("Enabled: " + cfg.enabled));
            })
            .dimensions(centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight)
            .build());
        
        // Toggle percentage display
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Show Percentage: " + cfg.showPercentage),
            button -> {
                cfg.showPercentage = !cfg.showPercentage;
                CommissionHudMod.config.save();
                button.setMessage(Text.literal("Show Percentage: " + cfg.showPercentage));
            })
            .dimensions(centerX - buttonWidth / 2, startY + spacing, buttonWidth, buttonHeight)
            .build());
        
        // Display mode toggle
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Display: " + cfg.displayMode.getDisplayName()),
            button -> {
                CommissionHudMod.config.cycleDisplayMode();
                button.setMessage(Text.literal("Display: " + CommissionHudMod.config.getDisplayMode().getDisplayName()));
            })
            .dimensions(centerX - buttonWidth / 2, startY + spacing * 2, buttonWidth, buttonHeight)
            .build());
        
        // Scale slider
        addDrawableChild(new SliderWidget(centerX - buttonWidth / 2, startY + spacing * 3, buttonWidth, buttonHeight, 
            Text.literal("Scale: " + String.format("%.1f", cfg.scale)), (cfg.scale - 0.5) / 1.5) {
            @Override
            protected void updateMessage() {
                cfg.scale = (float) (0.5 + value * 1.5);
                setMessage(Text.literal("Scale: " + String.format("%.1f", cfg.scale)));
            }
            
            @Override
            protected void applyValue() {
                CommissionHudMod.config.save();
            }
        });
        
        // Text color picker button
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Text Color: #" + String.format("%06X", cfg.color)),
            button -> {
                if (client != null) {
                    client.setScreen(new ColorPickerScreen(this, ColorPickerScreen.ColorType.TEXT_COLOR));
                }
            })
            .dimensions(centerX - buttonWidth / 2, startY + spacing * 4, buttonWidth, buttonHeight)
            .build());
        
        // Progress bar color picker button
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Progress Bar Color: #" + String.format("%06X", cfg.progressBarColor)),
            button -> {
                if (client != null) {
                    client.setScreen(new ColorPickerScreen(this, ColorPickerScreen.ColorType.PROGRESS_BAR_COLOR));
                }
            })
            .dimensions(centerX - buttonWidth / 2, startY + spacing * 5, buttonWidth, buttonHeight)
            .build());
        
        // Done button
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> close())
            .dimensions(centerX - buttonWidth / 2, height - 28, buttonWidth, buttonHeight)
            .build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFF);
        
        ConfigManager.Config cfg = CommissionHudMod.config.getConfig();
        
        // Draw color preview squares next to the color buttons
        int colorPreviewX = width / 2 + 105;
        
        // Text color preview
        int textColorY = 35 + 24 * 4;
        drawColorPreview(context, colorPreviewX, textColorY, cfg.color);
        
        // Progress bar color preview
        int barColorY = 35 + 24 * 5;
        drawColorPreview(context, colorPreviewX, barColorY, cfg.progressBarColor);
        
        // Instructions
        context.drawCenteredTextWithShadow(textRenderer, 
            Text.literal("Drag the preview to reposition the HUD"), 
            width / 2, height - 55, 0x888888);
        
        // Show current location if in mining islands only mode
        if (cfg.displayMode == ConfigManager.DisplayMode.MINING_ISLANDS_ONLY) {
            String location = CommissionHudMod.locationDetector.getCurrentLocation();
            boolean inMining = CommissionHudMod.locationDetector.isInMiningIsland();
            
            String locationText = location.isEmpty() ? "Unknown" : location;
            int locationColor = inMining ? 0x55FF55 : 0xFF5555;
            
            context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Location: " + locationText + (inMining ? " ✓" : " ✗")),
                width / 2, height - 43, locationColor);
        }
        
        super.render(context, mouseX, mouseY, delta);
        
        // Render HUD preview
        context.getMatrices().push();
        context.getMatrices().translate(cfg.x, cfg.y, 0);
        context.getMatrices().scale(cfg.scale, cfg.scale, 1.0f);
        
        context.drawText(textRenderer, Text.literal("Commissions:"), 0, 0, cfg.color, true);
        
        String exampleText = "• Mithril Miner";
        if (cfg.showPercentage) {
            exampleText += ": 45%";
        }
        context.drawText(textRenderer, Text.literal(exampleText), 0, 12, cfg.color, true);
        
        if (cfg.showPercentage) {
            // Progress bar background
            context.fill(0, 21, 100, 23, 0x88000000);
            // Progress bar with custom color
            context.fill(0, 21, 45, 23, 0xFF000000 | cfg.progressBarColor);
        }
        
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
        int previewWidth = 150;
        int previewHeight = 50;
        
        if (mouseX >= cfg.x && mouseX <= cfg.x + previewWidth * cfg.scale && 
            mouseY >= cfg.y && mouseY <= cfg.y + previewHeight * cfg.scale) {
            dragging = true;
            dragX = (int) (mouseX - cfg.x);
            dragY = (int) (mouseY - cfg.y);
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
            CommissionHudMod.config.setPosition((int) mouseX - dragX, (int) mouseY - dragY);
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
