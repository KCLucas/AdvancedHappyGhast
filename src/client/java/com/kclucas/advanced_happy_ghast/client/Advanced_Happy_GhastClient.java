package com.kclucas.advanced_happy_ghast.client;

import com.kclucas.advanced_happy_ghast.Advanced_Happy_Ghast;
import com.kclucas.advanced_happy_ghast.client.gui.GhastScreen;
import com.kclucas.advanced_happy_ghast.network.GhastDataPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class Advanced_Happy_GhastClient implements ClientModInitializer {
	public static int currentGhastLevel = 0;
	public static double currentGhastDistance = 0.0;
	public static double currentGhastSpeed = 0.0;
	public static double currentMaxDistance = 2000.0;
	public static int submittedTears = 0;
	public static int submittedStars = 0;
	public static String currentOwnerName = "Unknown";
	public static String currentOwnerUuid = "";

	@Override
	public void onInitializeClient() {
		//Old workin for black box
		// HandledScreens.register(Advanced_Happy_Ghast.GHAST_SCREEN_HANDLER_TYPE, GhastScreen::new);

		// new with Menu/slots
		net.minecraft.client.gui.screen.ingame.HandledScreens.register(
				com.kclucas.advanced_happy_ghast.Advanced_Happy_Ghast.GHAST_SCREEN_HANDLER_TYPE,
				com.kclucas.advanced_happy_ghast.client.gui.GhastScreen::new
		);
		ClientPlayNetworking.registerGlobalReceiver(GhastDataPayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				currentGhastLevel = payload.level();
				currentGhastDistance = payload.distance();
				currentGhastSpeed = payload.speed();
				currentMaxDistance = payload.maxDist();
				submittedTears = payload.tears();
				submittedStars = payload.stars();
				currentOwnerName = payload.ownerName(); // Sync Name
				currentOwnerUuid = payload.ownerUuid(); // Sync UUID
			});
		});
	}
}