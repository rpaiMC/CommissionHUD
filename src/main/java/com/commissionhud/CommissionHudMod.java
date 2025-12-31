package com.commissionhud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import com.mojang.brigadier.Command;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class CommissionHudMod implements ClientModInitializer {
    public static final String MOD_ID = "commissionhud";
    public static final ConfigManager config = new ConfigManager();
    public static final CommissionManager commissionManager = new CommissionManager();
    public static final LocationDetector locationDetector = new LocationDetector();
    
    @Override
    public void onInitializeClient() {
        config.load();
        
        // Register command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("chud")
                .executes(context -> {
                    // Schedule screen opening for next tick on the main thread
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.send(() -> {
                        client.setScreen(new ConfigScreen());
                    });
                    return Command.SINGLE_SUCCESS;
                }));
        });
        
        // Register HUD renderer
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (config.isEnabled() && client.world != null && client.currentScreen == null) {
                // Check if we should display based on location settings
                if (shouldDisplayHud()) {
                    CommissionRenderer.render(context, commissionManager.getActiveCommissions());
                }
            }
        });
        
        System.out.println("CommissionHud mod initialized!");
    }
    
    private boolean shouldDisplayHud() {
        // If set to show everywhere, always display
        if (config.getDisplayMode() == ConfigManager.DisplayMode.EVERYWHERE) {
            return true;
        }
        
        // Otherwise, only show in mining islands
        return locationDetector.isInMiningIsland();
    }
}
