package com.kclucas.advanced_happy_ghast;

import com.kclucas.advanced_happy_ghast.config.GhastWorldState;
import com.kclucas.advanced_happy_ghast.data.GhastData;
import com.kclucas.advanced_happy_ghast.data.GhastDataAttachment;
import com.kclucas.advanced_happy_ghast.network.GhastDataPayload;
import com.kclucas.advanced_happy_ghast.network.OpenMenuPayload;
import com.kclucas.advanced_happy_ghast.system.GhastProgressionSystem;
import com.kclucas.advanced_happy_ghast.gui.GhastMenu;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import com.kclucas.advanced_happy_ghast.network.GhastScreenPayload;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Advanced_Happy_Ghast implements ModInitializer {
	public static final String MOD_ID = "advanced_happy_ghast";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ScreenHandlerType<GhastMenu> GHAST_SCREEN_HANDLER_TYPE;

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(GhastDataPayload.ID, GhastDataPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(OpenMenuPayload.ID, OpenMenuPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(GhastScreenPayload.ID, GhastScreenPayload.CODEC);

		GHAST_SCREEN_HANDLER_TYPE = Registry.register(
				Registries.SCREEN_HANDLER,
				Identifier.of(MOD_ID, "ghast_menu"),
				new ExtendedScreenHandlerType<>(GhastMenu::new, GhastScreenPayload.CODEC)
		);

		GhastDataAttachment.register();
		GhastProgressionSystem.register();

		ServerPlayNetworking.registerGlobalReceiver(OpenMenuPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				ServerPlayerEntity player = context.player();
				if (player.getVehicle() instanceof HappyGhastEntity ghast) {
					GhastData data = ghast.getAttached(GhastDataAttachment.GHAST_DATA);
					if (data != null) {
						player.openHandledScreen(new ExtendedScreenHandlerFactory<GhastScreenPayload>() {
							@Override
							public GhastScreenPayload getScreenOpeningData(ServerPlayerEntity player) {
								return new GhastScreenPayload(data.level);
							}
							@Override
							public Text getDisplayName() { return Text.literal("Happy Ghast Menu"); }

							@Override
							public ScreenHandler createMenu(int syncId, PlayerInventory playerInv, PlayerEntity playerEntity) {
								GhastMenu menu = new GhastMenu(syncId, playerInv, data.inventory, data.level);
								menu.setGhastData(data);
								return menu;
							}
						});
					}
				}
			});
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal("aghast")
					.then(literal("status")
							.executes(context -> {
								ServerCommandSource source = context.getSource();
								GhastWorldState state = GhastWorldState.getServerState(source.getServer());
								source.sendFeedback(() -> Text.literal("§6--- Ghast World Settings ---"), false);
								source.sendFeedback(() -> Text.literal("Lvl 1 Dist: §f" + state.level1DistanceReq + "m"), false);
								source.sendFeedback(() -> Text.literal("Lvl 1 Bonus: §b+" + (state.level1SpeedBonus * 100) + "%"), false);
								source.sendFeedback(() -> Text.literal("Lvl 2 Bonus: §b+" + (state.level2SpeedBonus * 100) + "%"), false);
								source.sendFeedback(() -> Text.literal("Lvl 3 Bonus: §b+" + (state.level3SpeedBonus * 100) + "%"), false);
								return 1;
							})
					)
					.then(literal("spawn")
							.then(argument("level", IntegerArgumentType.integer(0, 3))
									.executes(context -> {
										int level = IntegerArgumentType.getInteger(context, "level");
										ServerCommandSource source = context.getSource();
										ServerPlayerEntity player = source.getPlayer();
										if (player == null) return 0;

										HappyGhastEntity ghast = new HappyGhastEntity(EntityType.HAPPY_GHAST, player.getEntityWorld());
										ghast.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), 0);
										player.getEntityWorld().spawnEntity(ghast);

										GhastData data = ghast.getAttached(GhastDataAttachment.GHAST_DATA);
										if (data == null) {
											data = new GhastData();
											ghast.setAttached(GhastDataAttachment.GHAST_DATA, data);
										}
										data.level = level;
										data.ownerUuid = player.getUuid();

										source.sendFeedback(() -> Text.literal("§aSpawned Level " + level + " Happy Ghast!"), true);
										return 1;
									})
							)
					)
					.then(literal("set")
							.then(literal("distance")
									.then(argument("value", DoubleArgumentType.doubleArg(1.0))
											.executes(context -> {
												double newValue = DoubleArgumentType.getDouble(context, "value");
												GhastWorldState state = GhastWorldState.getServerState(context.getSource().getServer());
												state.level1DistanceReq = newValue;
												state.markDirty();
												context.getSource().sendFeedback(() -> Text.literal("§aDistance updated to: " + newValue), true);
												return 1;
											})
									)
							)
							.then(literal("speed1")
									.then(argument("value", DoubleArgumentType.doubleArg(0.0))
											.executes(context -> {
												double newValue = DoubleArgumentType.getDouble(context, "value");
												GhastWorldState state = GhastWorldState.getServerState(context.getSource().getServer());
												state.level1SpeedBonus = newValue;
												state.markDirty();
												context.getSource().sendFeedback(() -> Text.literal("§aLvl 1 Bonus set to: " + (newValue * 100) + "%"), true);
												return 1;
											})
									)
							)
							.then(literal("speed2")
									.then(argument("value", DoubleArgumentType.doubleArg(0.0))
											.executes(context -> {
												double newValue = DoubleArgumentType.getDouble(context, "value");
												GhastWorldState state = GhastWorldState.getServerState(context.getSource().getServer());
												state.level2SpeedBonus = newValue;
												state.markDirty();
												context.getSource().sendFeedback(() -> Text.literal("§aLvl 2 Bonus set to: " + (newValue * 100) + "%"), true);
												return 1;
											})
									)
							)
							.then(literal("speed3")
									.then(argument("value", DoubleArgumentType.doubleArg(0.0))
											.executes(context -> {
												double newValue = DoubleArgumentType.getDouble(context, "value");
												GhastWorldState state = GhastWorldState.getServerState(context.getSource().getServer());
												state.level3SpeedBonus = newValue;
												state.markDirty();
												context.getSource().sendFeedback(() -> Text.literal("§aLvl 3 Bonus set to: " + (newValue * 100) + "%"), true);
												return 1;
											})
									)
							)
					)
			);
		});

		LOGGER.info("Advanced Happy Ghast Initialized!");
	}
}