package com.kclucas.advanced_happy_ghast.client.mixin;

import com.kclucas.advanced_happy_ghast.network.OpenMenuPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.passive.HappyGhastEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Inject(at = @At("HEAD"), method = "init")
    private void onInventoryOpen(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.getVehicle() instanceof HappyGhastEntity) {
            // Send request to server to open the Unified Ghast Menu
            ClientPlayNetworking.send(new OpenMenuPayload());

            // Close the vanilla inventory immediately
            client.execute(() -> client.setScreen(null));
        }
    }
}