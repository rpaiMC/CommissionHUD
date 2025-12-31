package com.commissionhud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import java.util.*;
import java.util.regex.*;

public class CommissionManager {
    private List<Commission> activeCommissions = new ArrayList<>();
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 500; // Update every 0.5 seconds
    
    // General pattern to match ANY line with "text: XX%" format
    // This will catch all commission formats including location-prefixed ones
    private static final Pattern PERCENTAGE_PATTERN = Pattern.compile("^\\s*(.+?):\\s*(\\d+(?:\\.\\d+)?)%\\s*$");
    
    // Track if we're in the commissions section of tab list
    private boolean inCommissionsSection = false;
    
    // Section headers to detect when we leave the commissions section
    private static final Set<String> SECTION_HEADERS = new HashSet<>(Arrays.asList(
        "players", "info", "profile", "skills", "stats", 
        "forges", "powders", "daily quests", "active effects",
        "cookie buff", "upgrades", "pet", "area", "server",
        "pickaxe ability", "event", "winter", "spooky",
        "objectives", "quests", "party", "guild", "skyblock"
    ));
    
    public List<Commission> getActiveCommissions() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate > UPDATE_INTERVAL) {
            updateCommissions();
            lastUpdate = currentTime;
        }
        return new ArrayList<>(activeCommissions);
    }
    
    /**
     * Clears all stored commissions. Call this when you want to reset.
     */
    public void clearCommissions() {
        activeCommissions.clear();
    }
    
    private void updateCommissions() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null || client.getNetworkHandler() == null) {
            return;
        }
        
        List<Commission> newCommissions = new ArrayList<>();
        inCommissionsSection = false;
        boolean foundCommissionsHeader = false;
        
        // Get player list entries (Tab list)
        Collection<PlayerListEntry> playerList = client.getNetworkHandler().getPlayerList();
        
        // Convert to list and sort by name to maintain order
        List<PlayerListEntry> sortedList = new ArrayList<>(playerList);
        sortedList.sort((a, b) -> {
            String nameA = a.getProfile().getName();
            String nameB = b.getProfile().getName();
            return nameA.compareTo(nameB);
        });
        
        for (PlayerListEntry entry : sortedList) {
            if (entry.getDisplayName() == null) {
                continue;
            }
            
            String displayText = entry.getDisplayName().getString();
            
            // Remove all color/formatting codes
            String cleanText = displayText.replaceAll("§[0-9a-fk-or]", "").trim();
            
            // Skip empty lines
            if (cleanText.isEmpty()) {
                continue;
            }
            
            String lowerText = cleanText.toLowerCase();
            
            // Check if this is the Commissions header
            if (lowerText.equals("commissions") || lowerText.equals("commissions:")) {
                inCommissionsSection = true;
                foundCommissionsHeader = true;
                continue;
            }
            
            // Check if we've hit another section header (end of commissions)
            if (isNewSection(lowerText)) {
                if (inCommissionsSection) {
                    inCommissionsSection = false;
                }
                continue;
            }
            
            // If we're in the commissions section, parse the commission
            if (inCommissionsSection) {
                Commission commission = parseCommissionLine(cleanText);
                if (commission != null) {
                    newCommissions.add(commission);
                }
            }
        }
        
        // IMPORTANT: Only update activeCommissions if we actually found the commissions section
        // This way, when the player leaves the mining area, the last known commissions persist
        if (foundCommissionsHeader) {
            activeCommissions = newCommissions;
        }
        // If no commissions header was found, we keep the old activeCommissions
        // This allows the HUD to display even when not in mining areas
    }
    
    private boolean isNewSection(String lowerText) {
        // Check exact matches for section headers
        for (String header : SECTION_HEADERS) {
            if (lowerText.equals(header) || lowerText.equals(header + ":")) {
                return true;
            }
        }
        
        // Check if line starts with a known header followed by colon
        // but NOT if it contains a percentage (which would be a commission)
        if (!lowerText.contains("%")) {
            for (String header : SECTION_HEADERS) {
                if (lowerText.startsWith(header + ":")) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private Commission parseCommissionLine(String text) {
        // Match pattern: "Commission Name: XX%" or "Location Name Commission: XX%"
        Matcher matcher = PERCENTAGE_PATTERN.matcher(text);
        if (matcher.find()) {
            String name = matcher.group(1).trim();
            String percentStr = matcher.group(2);
            
            // Validate it looks like a real commission (not too short, not a header)
            if (name.length() < 2) {
                return null;
            }
            
            // Skip if it matches a known non-commission pattern
            String lowerName = name.toLowerCase();
            if (lowerName.equals("pet") || lowerName.equals("area") || 
                lowerName.equals("server") || lowerName.equals("profile") ||
                lowerName.contains("sb level") || lowerName.contains("bank") ||
                lowerName.contains("interest") || lowerName.contains("xp")) {
                return null;
            }
            
            try {
                double percent = Double.parseDouble(percentStr);
                return new Commission(name, (int) Math.round(percent));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        // Also check for "DONE" status
        if (text.toLowerCase().contains("done")) {
            // Extract the name part before "DONE" or ": DONE"
            String name = text.replaceAll("(?i):?\\s*done\\s*$", "").trim();
            if (name.length() >= 2 && !isNewSection(name.toLowerCase())) {
                return new Commission(name, 100);
            }
        }
        
        return null;
    }
    
    public static class Commission {
        public final String name;
        public final int percentage;
        
        public Commission(String name, int percentage) {
            this.name = name;
            this.percentage = percentage;
        }
        
        @Override
        public String toString() {
            return name + ": " + percentage + "%";
        }
    }
}
