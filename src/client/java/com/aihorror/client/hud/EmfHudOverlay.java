package com.aihorror.client.hud;

import com.aihorror.block.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;

public class EmfHudOverlay {
    public static void renderOverlay(GuiGraphicsExtractor graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        var stack = mc.player.getMainHandItem();
        boolean holdingEmf = stack.is(com.aihorror.item.ModItems.EMF_READER) || mc.player.getOffhandItem().is(com.aihorror.item.ModItems.EMF_READER);
        boolean holdingScanner = stack.is(com.aihorror.item.ModItems.SCANNER) || mc.player.getOffhandItem().is(com.aihorror.item.ModItems.SCANNER);
        if (!holdingEmf && !holdingScanner) return;
        int emf = 0;
        var pos = mc.player.blockPosition();
        for (int dx=-8; dx<=8; dx++) for (int dy=-4; dy<=4; dy++) for (int dz=-8; dz<=8; dz++) {
            var p = pos.offset(dx,dy,dz);
            var state = mc.level.getBlockState(p);
            if (state.is(Blocks.SOUL_SOIL) || state.is(ModBlocks.CORRUPTED_BLOCK)) emf++;
            if (state.is(Blocks.SOUL_SOIL)) emf++;
        }
        var near = mc.level.getEntitiesOfClass(com.aihorror.entity.GlitchEntity.class, new AABB(pos).inflate(20));
        if (!near.isEmpty()) emf += 10;
        String label;
        int color;
        if (emf > 15) { label = "EMF 5 PARANORMAL"; color = 0xFFFF0000; }
        else if (emf > 8) { label = "EMF 4 HIGH"; color = 0xFFFF5555; }
        else if (emf > 4) { label = "EMF 3 MEDIUM"; color = 0xFFFFFF55; }
        else if (emf > 1) { label = "EMF 2 LOW"; color = 0xFF55FF55; }
        else { label = "EMF 1 NONE"; color = 0xFFAAAAAA; }
        var nearest = mc.level.getEntitiesOfClass(com.aihorror.entity.GlitchEntity.class, new AABB(pos).inflate(64));
        String dist = "";
        float needle = 0;
        if (!nearest.isEmpty()) {
            double d = mc.player.distanceTo(nearest.get(0));
            dist = String.format(" %.1fm", d);
            needle = (float)Math.max(0, Math.min(1, 1 - d/64));
        } else {
            needle = emf / 25f;
        }
        int w = graphics.guiWidth();
        int h = graphics.guiHeight();
        int x = w - 110;
        int y = 10;
        graphics.fill(x-2, y-2, x+108, y+28, 0x88000000);
        graphics.text(mc.font, Component.literal(label + dist), x, y, color);
        int barW = (int)(100 * needle);
        int barColor = emf > 8 ? 0xFFFF0000 : 0xFF00FF00;
        graphics.fill(x, y+12, x+100, y+16, 0xFF333333);
        graphics.fill(x, y+12, x+barW, y+16, barColor);
        graphics.text(mc.font, Component.literal(holdingScanner ? "[Scanner]" : "[EMF] Needle " + (int)(needle*100) + "%"), x, y+18, 0xFFFFFFFF);
    }
}
