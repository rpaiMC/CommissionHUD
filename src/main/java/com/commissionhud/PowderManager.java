package com.commissionhud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import java.util.*;

public class PowderManager {
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 500; // Update every 0.5 seconds
    
    // Stored powder values
    private String mithrilPowder = "0";
    private String gemstonePowder = "0";
    private String glacitePowder = "0";
    private boolean foundPowders = false;
    
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
        
        Collection<PlayerListEntry> playerList = client.getNetworkHandler().getPlayerList();
        
        // Convert to list and sort to maintain consistent order
        List<PlayerListEntry> sortedList = new ArrayList<>(playerList);
        sortedList.sort((a, b) -> {
            String nameA = a.getProfile().getName();
            String nameB = b.getProfile().getName();
            return nameA.compareTo(nameB);
        });
        
        // Build a list of clean text entries
        List<String> tabLines = new ArrayList<>();
        for (PlayerListEntry entry : sortedList) {
            if (entry.getDisplayName() == null) {
                tabLines.add("");
                continue;
            }
            String displayText = entry.getDisplayName().getString();
            String cleanText = displayText.replaceAll("§[0-9a-fk-or]", "").trim();
            tabLines.add(cleanText);
        }
        
        // Find the Powders header index
        int powdersIndex = -1;
        for (int i = 0; i < tabLines.size(); i++) {
            String line = tabLines.get(i).toLowerCase();
            if (line.equals("powders") || line.equals("powders:")) {
                powdersIndex = i;
                break;
            }
        }
        
        if (powdersIndex == -1) {
            return; // No powders section found
        }
        
        foundPowders = true;
        
        // Read the lines after "Powders" header (typically 3 lines: Mithril, Gemstone, Glacite)
        for (int i = powdersIndex + 1; i < Math.min(powdersIndex + 5, tabLines.size()); i++) {
            String line = tabLines.get(i);
            String lowerLine = line.toLowerCase();
            
            // Stop if we hit another section or empty line
            if (lowerLine.isEmpty() || isNewSection(lowerLine)) {
                break;
            }
            
            // Extract the value after the colon
            String value = extractNumberAfterColon(line);
            if (value == null) {
                continue;
            }
            
            if (lowerLine.startsWith("mithril")) {
                mithrilPowder = value;
            } else if (lowerLine.startsWith("gemstone")) {
                gemstonePowder = value;
            } else if (lowerLine.startsWith("glacite")) {
                glacitePowder = value;
            }
        }
    }
    
    private String extractNumberAfterColon(String text) {
        int colonIndex = text.indexOf(':');
        if (colonIndex >= 0 && colonIndex < text.length() - 1) {
            String afterColon = text.substring(colonIndex + 1).trim();
            // Extract digits and commas
            StringBuilder number = new StringBuilder();
            for (char c : afterColon.toCharArray()) {
                if (Character.isDigit(c) || c == ',') {
                    number.append(c);
                } else if (number.length() > 0) {
                    break;
                }
            }
            if (number.length() > 0) {
                return number.toString();
            }
        }
        return null;
    }
    
    private boolean isNewSection(String lowerText) {
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
