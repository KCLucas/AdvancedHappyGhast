package com.kclucas.advanced_happy_ghast.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GhastDataPayload(int level, double distance, double speed, double maxDist, int tears, int stars) implements CustomPayload {
    public static final Id<GhastDataPayload> ID = new Id<>(Identifier.of("advanced_happy_ghast", "ghast_data_sync"));

    public static final PacketCodec<RegistryByteBuf, GhastDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, GhastDataPayload::level,
            PacketCodecs.DOUBLE, GhastDataPayload::distance,
            PacketCodecs.DOUBLE, GhastDataPayload::speed,
            PacketCodecs.DOUBLE, GhastDataPayload::maxDist,
            PacketCodecs.VAR_INT, GhastDataPayload::tears, // Add this
            PacketCodecs.VAR_INT, GhastDataPayload::stars, // Add this
            GhastDataPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}