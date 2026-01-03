package com.commissionhud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import java.util.*;
import java.util.regex.*;

public class PowderManager {
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 500; // Update every 0.5 seconds
    
    // Stored powder values
    private String mithrilPowder = "0";
    private String gemstonePowder = "0";
    private String glacitePowder = "0";
    private boolean foundPowders = false;
    
    // Pattern to match powder lines like "Mithril: 1,234" or "Mithril Powder: 1,234"
    private static final Pattern MITHRIL_PATTERN = Pattern.compile("(?i)mithril(?:\\s+powder)?:\\s*([\\d,]+)");
    private static final Pattern GEMSTONE_PATTERN = Pattern.compile("(?i)gemstone(?:\\s+powder)?:\\s*([\\d,]+)");
    private static final Pattern GLACITE_PATTERN = Pattern.compile("(?i)glacite(?:\\s+powder)?:\\s*([\\d,]+)");
    
    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate < UPDATE_INTERVAL) {
            return;
        }
        lastUpdate = currentTime;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null || client.getNetworkHandler() == null) {
            return;
        }
        
        boolean inPowderSection = false;
        boolean foundPowderHeader = false;
        
        Collection<PlayerListEntry> playerList = client.getNetworkHandler().getPlayerList();
        
        for (PlayerListEntry entry : playerList) {
            if (entry.getDisplayName() == null) {
                continue;
            }
            
            String displayText = entry.getDisplayName().getString();
            String cleanText = displayText.replaceAll("§[0-9a-fk-or]", "").trim();
            
            if (cleanText.isEmpty()) {
                continue;
            }
            
            String lowerText = cleanText.toLowerCase();
            
            // Check for Powders section header
            if (lowerText.equals("powders") || lowerText.equals("powders:") || lowerText.equals("powder")) {
                inPowderSection = true;
                foundPowderHeader = true;
                continue;
            }
            
            // Check if we've hit another section (stop parsing)
            if (inPowderSection && isNewSection(lowerText)) {
                inPowderSection = false;
                continue;
            }
            
            // Parse powder values - can be in powders section OR anywhere in tab list
            Matcher mithrilMatcher = MITHRIL_PATTERN.matcher(cleanText);
            if (mithrilMatcher.find()) {
                mithrilPowder = mithrilMatcher.group(1);
                foundPowders = true;
            }
            
            Matcher gemstoneMatcher = GEMSTONE_PATTERN.matcher(cleanText);
            if (gemstoneMatcher.find()) {
                gemstonePowder = gemstoneMatcher.group(1);
                foundPowders = true;
            }
            
            Matcher glaciteMatcher = GLACITE_PATTERN.matcher(cleanText);
            if (glaciteMatcher.find()) {
                glacitePowder = glaciteMatcher.group(1);
                foundPowders = true;
            }
        }
    }
    
    private boolean isNewSection(String lowerText) {
        // Common section headers that would end the powders section
        String[] headers = {"commissions", "crystals", "forges", "skills", "stats", 
                           "profile", "players", "info", "pet", "area", "server",
                           "hotm", "heart of the mountain", "pickaxe", "daily"};
        
        for (String header : headers) {
            if (lowerText.equals(header) || lowerText.equals(header + ":") || lowerText.startsWith(header + ":")) {
                return true;
            }
        }
        return false;
    }
    
    public String getMithrilPowder() {
        return mithrilPowder;
    }
    
    public String getGemstonePowder() {
        return gemstonePowder;
    }
    
    public String getGlacitePowder() {
        return glacitePowder;
    }
    
    public boolean hasPowderData() {
        return foundPowders;
    }
    
    public void clearPowders() {
        mithrilPowder = "0";
        gemstonePowder = "0";
        glacitePowder = "0";
        foundPowders = false;
    }
}
