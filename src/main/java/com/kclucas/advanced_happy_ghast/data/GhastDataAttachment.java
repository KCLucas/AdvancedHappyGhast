package com.kclucas.advanced_happy_ghast.data;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public class GhastDataAttachment {

    public static final AttachmentType<GhastData> GHAST_DATA =
            AttachmentRegistry.<GhastData>builder()
                    .initializer(GhastData::new)
                    .persistent(GhastData.CODEC) // This enables NBT saving
                    .buildAndRegister(Identifier.of("advanced_happy_ghast", "ghast_data"));

    public static void register() {
        // Just triggers static initialization
    }
}