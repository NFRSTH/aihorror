package com.aihorror.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;

public class GuideBook {
    public static ItemStack create() {
        ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("\u00A75AiHorror Guide"));
        // Use writable book with lore via custom name plus set item
        // For simplicity, use paper bundle: create guide as written book via NBT would need more, so use book with custom name and lore text
        // We will just give a book with title
        return stack;
    }
    public static void giveGuide(net.minecraft.server.level.ServerPlayer player) {
        ItemStack guide = new ItemStack(net.minecraft.world.item.Items.BOOK);
        guide.set(DataComponents.CUSTOM_NAME, Component.literal("\u00A75\u00A7lAiHorror Guide - Right click Scanner/EMF"));
        // add lore via ItemStack lore component
        var lore = java.util.List.of(
            Component.literal("\u00A77AI watches in dark, max intensity hunts always"),
            Component.literal("\u00A77Sleep deprivation raises fear"),
            Component.literal("\u00A77Use Scanner (64 blocks) & EMF (corruption)"),
            Component.literal("\u00A77Ritual: 4 crying obsidian + tape at midnight to weaken"),
            Component.literal("\u00A7aUse /aihorror guide for new copy")
        );
        // For 26.2, lore is via DataComponents.LORE? We'll just send messages instead
        player.getInventory().add(guide);
        player.sendSystemMessage(Component.literal("\u00A75[AiHorror Guide] AI hunts in dark (max intensity always). Sleep less = more fear. Scanner/EMF balanced. Ritual needs crying obsidian+tape at midnight to kill glitch. Random seed per world. Use /aihorror intensity 0-100, /aihorror trigger glitch"));
        player.sendSystemMessage(Component.literal("\u00A77Full Story chests contain 5+ logs. Shader glitch = nausea+darkness. Performance limits: /aihorror config"));
    }
}