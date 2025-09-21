package ru.sgu;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


public class CSIT implements ModInitializer {
	public static final String MOD_ID = "csit";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final RegistryKey<ItemGroup> DESKS_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(CSIT.MOD_ID, "desks_group"));
	public static final ItemGroup DESKS_ITEM_GROUP = FabricItemGroup.builder()
		//.icon(() -> new ItemStack())
		.displayName(Text.translatable("itemGroup.csit.desks"))
		.build();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello CSIT!");

		Registry.register(Registries.ITEM_GROUP, DESKS_ITEM_GROUP_KEY, DESKS_ITEM_GROUP);
		ModBlocks.initialize();
		
	}
}
