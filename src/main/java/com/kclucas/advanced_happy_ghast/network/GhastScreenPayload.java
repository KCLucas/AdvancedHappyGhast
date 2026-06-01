package com.kclucas.advanced_happy_ghast.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GhastScreenPayload(int level) implements CustomPayload {
    public static final Id<GhastScreenPayload> ID = new Id<>(Identifier.of("advanced_happy_ghast", "ghast_screen_data"));

    public static final PacketCodec<RegistryByteBuf, GhastScreenPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, GhastScreenPayload::level,
            GhastScreenPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}