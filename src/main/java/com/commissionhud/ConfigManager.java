package com.commissionhud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.file.*;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "commissionhud.json");
    
    private Config config = new Config();
    
    public enum DisplayMode {
        EVERYWHERE("Everywhere"),
        MINING_ISLANDS_ONLY("Mining Islands Only");
        
        private final String displayName;
        
        DisplayMode(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public DisplayMode next() {
            DisplayMode[] values = values();
            return values[(this.ordinal() + 1) % values.length];
        }
    }
    
    public void load() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                config = GSON.fromJson(json, Config.class);
                // Handle null values from old configs
                if (config.displayMode == null) {
                    config.displayMode = DisplayMode.EVERYWHERE;
                }
                if (config.progressFormat == null) {
                    config.progressFormat = ProgressFormat.PERCENTAGE;
                }
            } else {
                save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Config getConfig() { return config; }
    public boolean isEnabled() { return config.enabled; }
    public float getScale() { return config.scale; }
    public int getX() { return config.x; }
    public int getY() { return config.y; }
    public int getColor() { return config.color; }
    public boolean showPercentage() { return config.showPercentage; }
    public DisplayMode getDisplayMode() { return config.displayMode; }
    
    public void setEnabled(boolean enabled) { config.enabled = enabled; save(); }
    public void setScale(float scale) { config.scale = scale; save(); }
    public void setPosition(int x, int y) { config.x = x; config.y = y; save(); }
    public int getTitleColor() { return config.titleColor; }
    public void setTitleColor(int color) { config.titleColor = color; save(); }
    public void setColor(int color) { config.color = color; save(); }
    public void setShowPercentage(boolean show) { config.showPercentage = show; save(); }
    public int getProgressBarColor() { return config.progressBarColor; }
    public void setProgressBarColor(int color) { config.progressBarColor = color; save(); }
    public void setDisplayMode(DisplayMode mode) { config.displayMode = mode; save(); }
    
    public void cycleDisplayMode() {
        config.displayMode = config.displayMode.next();
        save();
    }
    
    public ProgressFormat getProgressFormat() { return config.progressFormat; }
    public void setProgressFormat(ProgressFormat format) { config.progressFormat = format; save(); }
    
    public void cycleProgressFormat() {
        config.progressFormat = config.progressFormat.next();
        save();
    }
    
    // Powder settings
    public boolean isPowderEnabled() { return config.powderEnabled; }
    public void setPowderEnabled(boolean enabled) { config.powderEnabled = enabled; save(); }
    public int getPowderX() { return config.powderX; }
    public int getPowderY() { return config.powderY; }
    public void setPowderPosition(int x, int y) { config.powderX = x; config.powderY = y; save(); }
    public float getPowderScale() { return config.powderScale; }
    public void setPowderScale(float scale) { config.powderScale = scale; save(); }
    public int getPowderTitleColor() { return config.powderTitleColor; }
    public void setPowderTitleColor(int color) { config.powderTitleColor = color; save(); }
    public int getPowderLabelColor() { return config.powderLabelColor; }
    public void setPowderLabelColor(int color) { config.powderLabelColor = color; save(); }
    public int getPowderValueColor() { return config.powderValueColor; }
    public void setPowderValueColor(int color) { config.powderValueColor = color; save(); }
    
    // Pickaxe ability settings
    public boolean isAbilityEnabled() { return config.abilityEnabled; }
    public void setAbilityEnabled(boolean enabled) { config.abilityEnabled = enabled; save(); }
    public int getAbilityX() { return config.abilityX; }
    public int getAbilityY() { return config.abilityY; }
    public void setAbilityPosition(int x, int y) { config.abilityX = x; config.abilityY = y; save(); }
    public float getAbilityScale() { return config.abilityScale; }
    public void setAbilityScale(float scale) { config.abilityScale = scale; save(); }
    public int getAbilityTitleColor() { return config.abilityTitleColor; }
    public void setAbilityTitleColor(int color) { config.abilityTitleColor = color; save(); }
    public int getAbilityLabelColor() { return config.abilityLabelColor; }
    public void setAbilityLabelColor(int color) { config.abilityLabelColor = color; save(); }
    public int getAbilityValueColor() { return config.abilityValueColor; }
    public void setAbilityValueColor(int color) { config.abilityValueColor = color; save(); }
    
    public static class Config {
        public boolean enabled = true;
        public float scale = 1.0f;
        public int x = 10;
        public int y = 10;
        public int titleColor = 0xFFFFFF; // White - for "Commissions:" title
        public int color = 0xFFFFFF; // White - for commission text
        public int progressBarColor = 0xFFAA00; // Gold/Orange
        public boolean showPercentage = true;
        public DisplayMode displayMode = DisplayMode.EVERYWHERE;
        public ProgressFormat progressFormat = ProgressFormat.PERCENTAGE;
        
        // Powder display settings
        public boolean powderEnabled = true;
        public int powderX = 10;
        public int powderY = 150;
        public float powderScale = 1.0f;
        public int powderTitleColor = 0xFFFFFF; // White
        public int powderLabelColor = 0xAAAAAA; // Gray
        public int powderValueColor = 0x55FFFF; // Cyan
        
        // Pickaxe ability display settings
        public boolean abilityEnabled = true;
        public int abilityX = 10;
        public int abilityY = 200;
        public float abilityScale = 1.0f;
        public int abilityTitleColor = 0xFFFFFF; // White
        public int abilityLabelColor = 0xAAAAAA; // Gray
        public int abilityValueColor = 0xFF5555; // Red for cooldown
    }
    
    public enum ProgressFormat {
        PERCENTAGE("Percentage (20%)"),
        FRACTION("Fraction (20/100)");
        
        private final String displayName;
        
        ProgressFormat(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public ProgressFormat next() {
            ProgressFormat[] values = values();
            return values[(this.ordinal() + 1) % values.length];
        }
    }
}
