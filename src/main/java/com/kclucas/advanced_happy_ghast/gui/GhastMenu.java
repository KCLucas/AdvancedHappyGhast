package com.kclucas.advanced_happy_ghast.gui;

import com.kclucas.advanced_happy_ghast.Advanced_Happy_Ghast;
import com.kclucas.advanced_happy_ghast.data.GhastData;
import com.kclucas.advanced_happy_ghast.network.GhastScreenPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class GhastMenu extends ScreenHandler {
    private final Inventory inventory;
    private final int level;
    private GhastData ghastData;

    public GhastMenu(int syncId, PlayerInventory playerInventory, GhastScreenPayload payload) {
        this(syncId, playerInventory, new SimpleInventory(19), payload.level());
    }

    public GhastMenu(int syncId, PlayerInventory playerInventory, Inventory inventory, int level) {
        super(Advanced_Happy_Ghast.GHAST_SCREEN_HANDLER_TYPE, syncId);
        this.inventory = inventory;
        this.level = level;

        // 1. ADD LISTENER (This ensures the 'onContentChanged' logic actually fires)
        if (inventory instanceof SimpleInventory simpleInv) {
            simpleInv.addListener(inv -> this.onContentChanged(inv));
        }

        inventory.onOpen(playerInventory.player);

        // SLOT 0: UPGRADE SLOT
        this.addSlot(new Slot(inventory, 0, 152, 12));

        // STORAGE SLOTS
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = 1 + col + row * 9;
                int slotY = (level == 2 && row == 1) ? 108 : (level >= 3) ? (90 + row * 18) : -2000;
                this.addSlot(new Slot(inventory, slotIndex, 8 + col * 18, slotY));
            }
        }

        // PLAYER INVENTORY
        int playerInvY = 140;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, playerInvY + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, playerInvY + 58));
        }
    }

    public void setGhastData(GhastData data) {
        this.ghastData = data;
    }

    @Override
    public void onContentChanged(Inventory inv) {
        super.onContentChanged(inv);
        // Safety: ensure data is linked and we are looking at the upgrade slot
        if (this.ghastData == null || inv != this.inventory) return;

        ItemStack stack = inv.getStack(0);
        if (stack.isEmpty()) return;

        // --- UPGRADE CONSUMPTION ---

        // Level 1 -> 2 (25 Tears)
        if (ghastData.level == 1 && stack.isOf(Items.GHAST_TEAR)) {
            int needed = 25 - ghastData.tearsSubmitted;
            int taking = Math.min(stack.getCount(), needed);

            ghastData.tearsSubmitted += taking;
            stack.decrement(taking);

            if (ghastData.tearsSubmitted >= 25) {
                ghastData.level = 2;
            }
        }
        // Level 2 -> 3 (1 Nether Star)
        else if (ghastData.level == 2 && stack.isOf(Items.NETHER_STAR)) {
            ghastData.starsSubmitted = 1;
            stack.decrement(1);
            ghastData.level = 3;
        }
    }

    @Override public ItemStack quickMove(PlayerEntity player, int slot) { return ItemStack.EMPTY; }
    @Override public boolean canUse(PlayerEntity player) { return this.inventory.canPlayerUse(player); }
}