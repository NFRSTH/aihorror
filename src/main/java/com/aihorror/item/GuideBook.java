package com.aihorror.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.server.network.Filterable;
import java.util.List;

public class GuideBook {
    public static ItemStack create() {
        ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
        List<Filterable<Component>> pages = List.of(
            Filterable.passThrough(Component.literal("AiHorror Guide\n\nThe AI watches in darkness. At max intensity it hunts always. Sleep deprivation raises fear. Stay in light.")),
            Filterable.passThrough(Component.literal("Tools:\nScanner (64 blocks) detects Glitch entities.\nEMF Reader measures corruption & nearby Glitch.")),
            Filterable.passThrough(Component.literal("Ritual:\nPlace 8 crying obsidian + 4 soul soil in 5x5 ring.\nHold corrupted tape at midnight (17k-19k ticks).\nUse /aihorror ritual or tape.")),
            Filterable.passThrough(Component.literal("Commands:\n/aihorror intensity 0-5\n/aihorror trigger glitch\n/aihorror config\nSeed: per-world salt in config.")),
            Filterable.passThrough(Component.literal("Found Footage:\nChests with 5+ logs spawn near you.\nShader glitch = nausea+darkness.\nJumpscare cooldown tunable."))
        );
        WrittenBookContent content = new WrittenBookContent(
            Filterable.passThrough("AiHorror Guide"),
            "AiHorror Team",
            0,
            pages,
            true
        );
        stack.set(DataComponents.WRITTEN_BOOK_CONTENT, content);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("\u00A75AiHorror Guide"));
        return stack;
    }

    public static void giveGuide(net.minecraft.server.level.ServerPlayer player) {
        ItemStack guide = create();
        player.getInventory().add(guide);
        player.sendSystemMessage(Component.literal("\u00A75[AiHorror] Guide given - open the Written Book!"));
    }
}
