package com.commissionhud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import java.util.*;

public class StatsManager {
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 500; // Update every 0.5 seconds
    
    // Map of stat name to value (e.g., "Mining Speed" -> "1549")
    private Map<String, String> stats = new LinkedHashMap<>();
    
    // Map of stat names to their icons
    private static final Map<String, String> STAT_ICONS = new HashMap<>();
    
    static {
        // Combat stats
        STAT_ICONS.put("Health", "❤");
        STAT_ICONS.put("Defense", "❈");
        STAT_ICONS.put("True Defense", "❂");
        STAT_ICONS.put("Strength", "❁");
        STAT_ICONS.put("Speed", "✦");
        STAT_ICONS.put("Crit Chance", "☣");
        STAT_ICONS.put("Crit Damage", "☠");
        STAT_ICONS.put("Bonus Attack Speed", "⚔");
        STAT_ICONS.put("Attack Speed", "⚔");
        STAT_ICONS.put("Intelligence", "✎");
        STAT_ICONS.put("Ferocity", "⫽");
        STAT_ICONS.put("Ability Damage", "๑");
        STAT_ICONS.put("Magic Find", "✯");
        STAT_ICONS.put("Pet Luck", "♣");
        STAT_ICONS.put("Sea Creature Chance", "α");
        
        // Mining stats
        STAT_ICONS.put("Mining Speed", "⸕");
        STAT_ICONS.put("Mining Fortune", "☘");
        STAT_ICONS.put("Pristine", "✧");
        STAT_ICONS.put("Breaking Power", "Ⓟ");
        
        // Farming stats
        STAT_ICONS.put("Farming Fortune", "☘");
        
        // Foraging stats
        STAT_ICONS.put("Foraging Fortune", "☘");
        
        // Fishing stats
        STAT_ICONS.put("Fishing Speed", "☂");
        
        // Misc stats
        STAT_ICONS.put("Soulflow", "⸎");
        STAT_ICONS.put("Overflow Mana", "ʬ");
        STAT_ICONS.put("Mending", "☄");
        STAT_ICONS.put("Vitality", "♨");
        STAT_ICONS.put("Health Regen", "❣");
        STAT_ICONS.put("Wisdom", "☯");
    }
    
    // Map of stat names to their colors (as hex)
    private static final Map<String, Integer> STAT_COLORS = new HashMap<>();
    
    static {
        // Combat stats
        STAT_COLORS.put("Health", 0xFF5555); // Red
        STAT_COLORS.put("Defense", 0x55FF55); // Green
        STAT_COLORS.put("True Defense", 0xFFFFFF); // White
        STAT_COLORS.put("Strength", 0xFF5555); // Red
        STAT_COLORS.put("Speed", 0xFFFFFF); // White
        STAT_COLORS.put("Crit Chance", 0x5555FF); // Blue
        STAT_COLORS.put("Crit Damage", 0x5555FF); // Blue
        STAT_COLORS.put("Bonus Attack Speed", 0xFFFF55); // Yellow
        STAT_COLORS.put("Attack Speed", 0xFFFF55); // Yellow
        STAT_COLORS.put("Intelligence", 0x55FFFF); // Aqua
        STAT_COLORS.put("Ferocity", 0xFF5555); // Red
        STAT_COLORS.put("Ability Damage", 0xFF5555); // Red
        STAT_COLORS.put("Magic Find", 0x55FFFF); // Aqua
        STAT_COLORS.put("Pet Luck", 0xFF55FF); // Light Purple
        STAT_COLORS.put("Sea Creature Chance", 0x00AAAA); // Dark Aqua
        
        // Mining stats
        STAT_COLORS.put("Mining Speed", 0xFFAA00); // Gold
        STAT_COLORS.put("Mining Fortune", 0xFFAA00); // Gold
        STAT_COLORS.put("Pristine", 0xAA00AA); // Dark Purple
        STAT_COLORS.put("Breaking Power", 0x00AA00); // Dark Green
        
        // Farming stats
        STAT_COLORS.put("Farming Fortune", 0xFFAA00); // Gold
        
        // Foraging stats
        STAT_COLORS.put("Foraging Fortune", 0xFFAA00); // Gold
        
        // Fishing stats
        STAT_COLORS.put("Fishing Speed", 0x55FFFF); // Aqua
        
        // Misc stats
        STAT_COLORS.put("Soulflow", 0x00AAAA); // Dark Aqua
        STAT_COLORS.put("Overflow Mana", 0x00AAAA); // Dark Aqua
        STAT_COLORS.put("Mending", 0x55FF55); // Green
        STAT_COLORS.put("Vitality", 0xAA0000); // Dark Red
        STAT_COLORS.put("Health Regen", 0xFF5555); // Red
        STAT_COLORS.put("Wisdom", 0x00AAAA); // Dark Aqua
    }
    
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
        
        // Find the Stats header index
        int statsIndex = -1;
        for (int i = 0; i < tabLines.size(); i++) {
            String line = tabLines.get(i).toLowerCase();
            if (line.startsWith("stats") || line.equals("stats:")) {
                statsIndex = i;
                break;
            }
        }
        
        if (statsIndex == -1) {
            return; // No stats section found
        }
        
        // Clear old stats
        stats.clear();
        
        // Read lines after "Stats:" header until we hit an empty line or another section
        for (int i = statsIndex + 1; i < tabLines.size() && i < statsIndex + 15; i++) {
            String line = tabLines.get(i);
            
            // Stop if we hit an empty line or another section header
            if (line.isEmpty() || isNewSection(line)) {
                break;
            }
            
            // Parse stat line - format is usually "StatName: IconValue" or "StatName: Value"
            // Example: "Mining Speed: ⸕1549" or "Mining Speed: 1549"
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String statName = line.substring(0, colonIndex).trim();
                String statValue = line.substring(colonIndex + 1).trim();
                
                // Remove any icon characters from the value to get just the number
                // But keep the original for display
                if (!statName.isEmpty() && !statValue.isEmpty()) {
                    stats.put(statName, statValue);
                }
            }
        }
    }
    
    private boolean isNewSection(String line) {
        String lower = line.toLowerCase();
        return lower.endsWith(":") && !lower.contains(" ") && line.length() < 20;
    }
    
    public Map<String, String> getStats() {
        return stats;
    }
    
    public boolean hasStats() {
        return !stats.isEmpty();
    }
    
    public String getIconForStat(String statName) {
        // First try exact match
        if (STAT_ICONS.containsKey(statName)) {
            return STAT_ICONS.get(statName);
        }
        
        // Then try partial match
        for (Map.Entry<String, String> entry : STAT_ICONS.entrySet()) {
            if (statName.toLowerCase().contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        
        return ""; // No icon found
    }
    
    public int getColorForStat(String statName) {
        // First try exact match
        if (STAT_COLORS.containsKey(statName)) {
            return STAT_COLORS.get(statName);
        }
        
        // Then try partial match
        for (Map.Entry<String, Integer> entry : STAT_COLORS.entrySet()) {
            if (statName.toLowerCase().contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        
        return 0xAAAAAA; // Default gray
    }
    
    public void clear() {
        stats.clear();
    }
}
