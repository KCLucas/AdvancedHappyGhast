package com.kclucas.advanced_happy_ghast.client.gui;

import com.kclucas.advanced_happy_ghast.client.Advanced_Happy_GhastClient;
import com.kclucas.advanced_happy_ghast.gui.GhastMenu;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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

        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0f, 0.0f, backgroundWidth, backgroundHeight, 256, 256);

        // Covering background slots
        context.fill(x + 7, y + 17, x + 169, y + 89, 0xFFC6C6C6);
        if (lvl < 3) context.fill(x + 7, y + 89, x + 169, y + 107, 0xFFC6C6C6);
        if (lvl < 2) context.fill(x + 7, y + 107, x + 169, y + 125, 0xFFC6C6C6);

        // Organic "Dashboard" box (Dark Gray but not black)
        context.fill(x + 10, y + 20, x + 90, y + 85, 0xFF4A4A4A);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // Stats Labels (White text for high contrast)
        context.drawText(this.textRenderer, "§e§nGhast Status", 15, 25, 0xFFFFFFFF, false);

        context.drawText(this.textRenderer, "Level: " + Advanced_Happy_GhastClient.currentGhastLevel, 15, 40, 0xFFFFFFFF, false);
        context.drawText(this.textRenderer, "Speed: " + String.format("%.3f", Advanced_Happy_GhastClient.currentGhastSpeed), 15, 50, 0xFFFFFFFF, false);

        // Progression Tracking
        String reqText = "";
        if (Advanced_Happy_GhastClient.currentGhastLevel == 0) {
            reqText = String.format("Dist: %.0f/%.0f", Advanced_Happy_GhastClient.currentGhastDistance, Advanced_Happy_GhastClient.currentMaxDistance);
        } else if (Advanced_Happy_GhastClient.currentGhastLevel == 1) {
            reqText = "Tears: " + Advanced_Happy_GhastClient.submittedTears + "/25";
        } else if (Advanced_Happy_GhastClient.currentGhastLevel == 2) {
            reqText = "Star: " + Advanced_Happy_GhastClient.submittedStars + "/1";
        } else {
            reqText = "§aMax Level";
        }
        context.drawText(this.textRenderer, reqText, 15, 65, 0xFFFFFFFF, false);

        // Upgrade Slot label
        context.drawText(this.textRenderer, "§8Upgrade:", 95, 14, 0xFF404040, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}