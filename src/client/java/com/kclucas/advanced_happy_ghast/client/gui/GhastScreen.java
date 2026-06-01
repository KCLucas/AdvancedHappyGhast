package com.kclucas.advanced_happy_ghast.client.gui;

import com.kclucas.advanced_happy_ghast.client.Advanced_Happy_GhastClient;
import com.kclucas.advanced_happy_ghast.gui.GhastMenu;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.List;

public class GhastScreen extends HandledScreen<GhastMenu> {
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/gui/container/generic_54.png");

    public GhastScreen(GhastMenu handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 222;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        int lvl = Advanced_Happy_GhastClient.currentGhastLevel;

        // 1. Draw standard chest background
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0f, 0.0f, backgroundWidth, backgroundHeight, 256, 256);

        // 2. COVERING LOGIC (Hides the slot grid based on level)
        // Cover top 4 rows
        context.fill(x + 7, y + 7, x + 169, y + 89, 0xFFC6C6C6);
        // Cover Row 5 (Locked if Lvl < 3)
        if (lvl < 3) context.fill(x + 7, y + 89, x + 169, y + 107, 0xFFC6C6C6);
        // Cover Row 6 (Locked if Lvl < 2)
        if (lvl < 2) context.fill(x + 7, y + 107, x + 169, y + 125, 0xFFC6C6C6);

        // 3. THE DARK BOX (Perfectly Symmetrical Alignment)
        // Top/Left/Right Padding: 7 pixels.
        // Bottom Padding: Row 5 starts at y+90, so ending at y+83 creates a 7 pixel gap.
        context.fill(x + 7, y + 7, x + 102, y + 83, 0xFF4A4A4A);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int lvl = Advanced_Happy_GhastClient.currentGhastLevel;

        context.drawText(this.textRenderer, "§e§nGhast Status", 10, 10, 0xFFFFFFFF, false);
        context.drawText(this.textRenderer, "Owner: §b" + Advanced_Happy_GhastClient.currentOwnerName, 10, 24, 0xFFFFFFFF, false);
        context.drawText(this.textRenderer, "Level: " + lvl, 10, 34, 0xFFFFFFFF, false);
        context.drawText(this.textRenderer, "Speed: " + String.format("%.3f", Advanced_Happy_GhastClient.currentGhastSpeed), 10, 44, 0xFFFFFFFF, false);

        String reqText = "";
        String slotLabel = "§8Upgrade:";

        if (lvl == 0) {
            reqText = String.format("Dist: %.0f/%.0f", Advanced_Happy_GhastClient.currentGhastDistance, Advanced_Happy_GhastClient.currentMaxDistance);
        } else if (lvl == 1) {
            reqText = "Tears: " + Advanced_Happy_GhastClient.submittedTears + "/25";
        } else if (lvl == 2) {
            reqText = "Star: " + Advanced_Happy_GhastClient.submittedStars + "/1";
        } else if (lvl == 3) {
            slotLabel = "§8Ammo:"; // CHANGE LABEL

            // Get the item currently in the upgrade slot (index 0)
            ItemStack ammoStack = this.handler.getSlot(0).getStack();
            if (Advanced_Happy_GhastClient.currentFireballMode == 0) {
                reqText = "§cFireballs Disabled";
            } else if (ammoStack.isOf(net.minecraft.item.Items.FIRE_CHARGE)) {
                reqText = "§6Charges: " + ammoStack.getCount() + "/64";
            } else {
                reqText = "§7Need: Fire Charges";
            }
        }

        context.drawText(this.textRenderer, reqText, 10, 58, 0xFFFFFFFF, false);
        context.drawText(this.textRenderer, slotLabel, 106, 17, 0xFF404040, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Tooltip check for Owner UUID
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        if (mouseX >= x + 15 && mouseX <= x + 100 && mouseY >= y + 30 && mouseY <= y + 40) {
            context.drawTooltip(this.textRenderer, List.of(
                    Text.literal("§7Owner UUID:"),
                    Text.literal("§f" + Advanced_Happy_GhastClient.currentOwnerUuid)
            ), mouseX, mouseY);
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}