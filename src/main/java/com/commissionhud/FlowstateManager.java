package com.commissionhud;

public class FlowstateManager {
    private static final int MAX_BLOCKS = 200;
    private static final int RESET_TIME_MS = 10000; // 10 seconds
    private static final int SPEED_PER_BLOCK = 3; // +3 mining speed per block
    
    private int blocksMinedConsecutive = 0;
    private long lastBlockBreakTime = 0;
    private boolean isActive = false;
    
    /**
     * Called when a block is broken (including mining spread blocks)
     */
    public void onBlockBreak() {
        long currentTime = System.currentTimeMillis();
        
        // Check if flowstate has reset (more than 10 seconds since last break)
        if (currentTime - lastBlockBreakTime > RESET_TIME_MS) {
            blocksMinedConsecutive = 0;
        }
        
        // Increment block count (cap at MAX_BLOCKS)
        if (blocksMinedConsecutive < MAX_BLOCKS) {
            blocksMinedConsecutive++;
        }
        
        lastBlockBreakTime = currentTime;
        isActive = true;
    }
    
    /**
     * Called every frame to update state
     */
    public void update() {
        if (!isActive) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        long timeSinceBreak = currentTime - lastBlockBreakTime;
        
        // Check if flowstate has reset
        if (timeSinceBreak > RESET_TIME_MS) {
            blocksMinedConsecutive = 0;
            isActive = false;
        }
    }
    
    /**
     * Get the current mining speed bonus from flowstate
     */
    public int getMiningSpeedBonus() {
        if (!isActive || blocksMinedConsecutive == 0) {
            return 0;
        }
        return blocksMinedConsecutive * SPEED_PER_BLOCK;
    }
    
    /**
     * Get the current block count
     */
    public int getBlockCount() {
        return blocksMinedConsecutive;
    }
    
    /**
     * Get the max block count
     */
    public int getMaxBlocks() {
        return MAX_BLOCKS;
    }
    
    /**
     * Get seconds remaining until reset (0-10)
     */
    public int getSecondsUntilReset() {
        if (!isActive || blocksMinedConsecutive == 0) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long timeSinceBreak = currentTime - lastBlockBreakTime;
        long timeRemaining = RESET_TIME_MS - timeSinceBreak;
        
        if (timeRemaining <= 0) {
            return 0;
        }
        
        return (int) Math.ceil(timeRemaining / 1000.0);
    }
    
    /**
     * Check if flowstate is currently active (has blocks counted)
     */
    public boolean isFlowstateActive() {
        return isActive && blocksMinedConsecutive > 0;
    }
    
    /**
     * Reset the flowstate tracker
     */
    public void reset() {
        blocksMinedConsecutive = 0;
        lastBlockBreakTime = 0;
        isActive = false;
    }
}
