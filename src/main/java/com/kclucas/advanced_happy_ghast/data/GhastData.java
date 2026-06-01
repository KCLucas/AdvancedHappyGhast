package com.kclucas.advanced_happy_ghast.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class GhastData {
    public UUID ownerUuid = null;
    public int level = 0;
    public double distanceTraveled = 0.0;

    // ADD THESE TWO FIELDS
    public int tearsSubmitted = 0;
    public int starsSubmitted = 0;

    public final SimpleInventory inventory = new SimpleInventory(19);
    public double lastX = Double.MAX_VALUE;
    public double lastY = Double.MAX_VALUE;
    public double lastZ = Double.MAX_VALUE;

    public GhastData() {}

    // Constructor updated for the Codec
    public GhastData(Optional<String> ownerStr, int level, double distance, int tears, int stars, List<ItemStack> items) {
        this.ownerUuid = ownerStr.map(UUID::fromString).orElse(null);
        this.level = level;
        this.distanceTraveled = distance;
        this.tearsSubmitted = tears; // Initialize
        this.starsSubmitted = stars; // Initialize

        for (int i = 0; i < items.size() && i < 19; i++) {
            this.inventory.setStack(i, items.get(i));
        }
    }

    public static final Codec<GhastData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("ownerUuid").forGetter(data -> Optional.ofNullable(data.ownerUuid).map(UUID::toString)),
            Codec.INT.fieldOf("level").forGetter(data -> data.level),
            Codec.DOUBLE.fieldOf("distanceTraveled").forGetter(data -> data.distanceTraveled),
            Codec.INT.fieldOf("tears").forGetter(data -> data.tearsSubmitted), // Add to codec
            Codec.INT.fieldOf("stars").forGetter(data -> data.starsSubmitted), // Add to codec
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("inventory").forGetter(data -> {
                List<ItemStack> stacks = new ArrayList<>();
                for (int i = 0; i < 19; i++) stacks.add(data.inventory.getStack(i));
                return stacks;
            })
    ).apply(instance, GhastData::new));
}