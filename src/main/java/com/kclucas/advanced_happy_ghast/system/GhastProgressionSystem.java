package com.kclucas.advanced_happy_ghast.system;

import com.kclucas.advanced_happy_ghast.config.GhastWorldState;
import com.kclucas.advanced_happy_ghast.data.GhastData;
import com.kclucas.advanced_happy_ghast.data.GhastDataAttachment;
import com.kclucas.advanced_happy_ghast.network.GhastDataPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class GhastProgressionSystem {
    private static final Identifier SPEED_MODIFIER_ID = Identifier.of("advanced_happy_ghast", "ghast_speed_bonus");

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(GhastProgressionSystem::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {
        GhastWorldState config = GhastWorldState.getServerState(world.getServer());

        for (HappyGhastEntity ghast : world.getEntitiesByType(net.minecraft.entity.EntityType.HAPPY_GHAST, entity -> true)) {
            GhastData data = ghast.getAttached(GhastDataAttachment.GHAST_DATA);
            if (data == null) {
                data = new GhastData();
                ghast.setAttached(GhastDataAttachment.GHAST_DATA, data);
            }

            applySpeedBonus(ghast, data.level, config);

            if (!(ghast.getFirstPassenger() instanceof PlayerEntity player)) {
                data.lastX = Double.MAX_VALUE;
                continue;
            }

            if (data.ownerUuid == null) data.ownerUuid = player.getUuid();

            double x = ghast.getX();
            double z = ghast.getZ();
            if (data.lastX != Double.MAX_VALUE) {
                double dx = x - data.lastX;
                double dz = z - data.lastZ;
                double horizontalDist = Math.sqrt(dx * dx + dz * dz);

                if (horizontalDist > 0.01 && player.getUuid().equals(data.ownerUuid)) {
                    data.distanceTraveled += horizontalDist;
                    if (data.level == 0 && data.distanceTraveled >= config.level1DistanceReq) {
                        data.level = 1;
                        player.sendMessage(net.minecraft.text.Text.literal("§6Your Ghast is now Calm! (Level 1)"), false);
                    }
                }
            }
            data.lastX = x;
            data.lastZ = z;

            if (player instanceof ServerPlayerEntity serverPlayer) {
                double currentSpeed = ghast.getAttributeValue(EntityAttributes.FLYING_SPEED);
                ServerPlayNetworking.send(serverPlayer, new GhastDataPayload(
                        data.level,
                        data.distanceTraveled,
                        currentSpeed,
                        config.level1DistanceReq,
                        data.tearsSubmitted,
                        data.starsSubmitted
                ));
            }
        }
    }

    private static void applySpeedBonus(HappyGhastEntity ghast, int level, GhastWorldState config) {
        EntityAttributeInstance flyingSpeed = ghast.getAttributeInstance(EntityAttributes.FLYING_SPEED);
        if (flyingSpeed != null) {
            EntityAttributeModifier currentMod = flyingSpeed.getModifier(SPEED_MODIFIER_ID);
            double bonus = 0.0;
            if (level == 1) bonus = config.level1SpeedBonus;
            if (level == 2) bonus = config.level2SpeedBonus;
            if (level == 3) bonus = config.level3SpeedBonus;

            if (currentMod == null || currentMod.value() != bonus) {
                flyingSpeed.removeModifier(SPEED_MODIFIER_ID);
                if (bonus > 0) {
                    flyingSpeed.addTemporaryModifier(new EntityAttributeModifier(SPEED_MODIFIER_ID, bonus, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                }
            }
        }
    }
}