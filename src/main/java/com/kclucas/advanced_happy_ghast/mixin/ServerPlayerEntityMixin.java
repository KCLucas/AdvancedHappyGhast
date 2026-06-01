package com.kclucas.advanced_happy_ghast.mixin;

import com.kclucas.advanced_happy_ghast.data.GhastData;
import com.kclucas.advanced_happy_ghast.data.GhastDataAttachment;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.kclucas.advanced_happy_ghast.network.GhastDataPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;


import java.util.OptionalInt;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

	@Inject(at = @At("HEAD"), method = "openHandledScreen", cancellable = true)
	private void onOpenScreen(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> cir) {
		ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

		if (player.getVehicle() instanceof HappyGhastEntity ghast) {
			GhastData data = ghast.getAttached(GhastDataAttachment.GHAST_DATA);
			if (data != null) {
				// Logic to open the custom screen goes here
				// For this tiny test, we'll just print a message and cancel the normal inventory
				player.sendMessage(Text.literal("§aOpening Ghast Menu (Lvl " + data.level + ")..."), true);

				// cir.setReturnValue(OptionalInt.empty()); // This would block the normal inventory
			}
		}
	}
}