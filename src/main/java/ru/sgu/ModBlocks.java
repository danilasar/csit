package ru.sgu;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
	public static final Block DESK1 = register(
		new DeskBlock(
			AbstractBlock.Settings
				.create()
				.sounds(BlockSoundGroup.WOOD)
				.burnable()
				.strength(4.0f)
				.nonOpaque()
				.allowsSpawning((state, world, pos, type) -> false)
				.solidBlock((state, world, pos) -> false)
		),
		"desk1",
		true
	);

    private static Block register(Block block, String name, boolean shouldRegisterItem) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        // блоки вроде minecraft:moving_piston или minecraft:end_gateway в регистрации не нуждаются
        if(shouldRegisterItem) {
            RegistryKey<Item> itemKey = keyOfItem(name);
			BlockItem blockItem = new BlockItem(block, new Item.Settings());
			Registry.register(Registries.ITEM, itemKey, blockItem);
        }
        return Registry.register(Registries.BLOCK, blockKey, block);
    }

	private static RegistryKey<Block> keyOfBlock(String name) {
		return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(CSIT.MOD_ID, name));
	}

	private static RegistryKey<Item> keyOfItem(String name) {
		return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(CSIT.MOD_ID, name));
	}

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(CSIT.DESKS_ITEM_GROUP_KEY).register((itemGroup) -> {
			itemGroup.add(DESK1.asItem());
		});
	}
}
