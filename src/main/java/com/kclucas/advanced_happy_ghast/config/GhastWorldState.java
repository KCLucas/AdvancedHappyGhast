package com.kclucas.advanced_happy_ghast.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

public class GhastWorldState extends PersistentState {

    public double level1DistanceReq = 2000.0;
    public double level1SpeedBonus = 0.25;
    public double level2SpeedBonus = 0.50;
    public double level3SpeedBonus = 1.00;

    // 0 = Off, 1 = Fire, 2 = Damage, 3 = Full Destruction
    public int fireballMode = 0;

    public static final Codec<GhastWorldState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("level1DistanceReq").forGetter(s -> s.level1DistanceReq),
            Codec.DOUBLE.fieldOf("level1SpeedBonus").forGetter(s -> s.level1SpeedBonus),
            Codec.DOUBLE.fieldOf("level2SpeedBonus").forGetter(s -> s.level2SpeedBonus),
            Codec.DOUBLE.fieldOf("level3SpeedBonus").forGetter(s -> s.level3SpeedBonus),
            Codec.INT.fieldOf("fireballMode").forGetter(s -> s.fireballMode) // Add to codec
    ).apply(instance, (d1, s1, s2, s3, f) -> {
        GhastWorldState state = new GhastWorldState();
        state.level1DistanceReq = d1;
        state.level1SpeedBonus = s1;
        state.level2SpeedBonus = s2;
        state.level3SpeedBonus = s3;
        state.fireballMode = f;
        return state;
    }));

    public GhastWorldState() {}

    public NbtCompound save(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.putDouble("level1DistanceReq", level1DistanceReq);
        nbt.putDouble("level1SpeedBonus", level1SpeedBonus);
        nbt.putDouble("level2SpeedBonus", level2SpeedBonus);
        nbt.putDouble("level3SpeedBonus", level3SpeedBonus);
        nbt.putInt("fireballMode", fireballMode);
        return nbt;
    }

    private static final PersistentStateType<GhastWorldState> TYPE = new PersistentStateType<>(
            "advanced_happy_ghast_settings", GhastWorldState::new, CODEC, DataFixTypes.LEVEL
    );

    public static GhastWorldState getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        GhastWorldState state = persistentStateManager.getOrCreate(TYPE);
        state.markDirty();
        return state;
    }
}