package com.aihorror.block;

import com.aihorror.AiHorror;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static Block CORRUPTED_BLOCK;

    private static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, AiHorror.id(name));
    }
    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, AiHorror.id(name));
    }

    public static void initialize() {
        ResourceKey<Block> bk = blockKey("corrupted_block");
        CORRUPTED_BLOCK = Registry.register(BuiltInRegistries.BLOCK, bk,
            new Block(BlockBehaviour.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.COLOR_BLACK).strength(2.0f, 6.0f).sound(SoundType.SOUL_SOIL).lightLevel(s -> 2).requiresCorrectToolForDrops().setId(bk))
        );
        ResourceKey<Item> ik = itemKey("corrupted_block");
        BlockItem bi = new BlockItem(CORRUPTED_BLOCK, new Item.Properties().setId(ik).useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, ik, bi);
        AiHorror.LOGGER.info("[AiHorror] Block registered: corrupted_block");
    }
}
