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
    public static final PowderManager powderManager = new PowderManager();
    
    @Override
    public void onInitializeClient() {
        config.load();
        
        // Register command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("chud")
                .executes(context -> {
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
            if (client.world != null) {
                // Don't render HUD if config screen is open (we show preview instead)
                if (client.currentScreen instanceof ConfigScreen || 
                    client.currentScreen instanceof PowderConfigScreen) {
                    return;
                }
                
                // Update powder data
                powderManager.update();
                
                // Render commission HUD
                if (config.isEnabled() && shouldDisplayHud()) {
                    CommissionRenderer.render(context, commissionManager.getActiveCommissions());
                }
                
                // Render powder HUD
                if (config.isPowderEnabled() && shouldDisplayHud()) {
                    PowderRenderer.render(context, powderManager);
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
