package net.modderg.thedigimod.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.modderg.thedigimod.TheDigiMod;

import static net.modderg.thedigimod.item.InitItems.ITEMS;

public class BlocksInit {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TheDigiMod.MOD_ID);

    public static final RegistryObject<Block> MEAT_CROP = BLOCKS.register("digimeat_crop",
            () -> new MeatCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT)));

    public static final RegistryObject<Block> DIGITAMA_DRAGON = BLOCKS.register("digitama_dragon",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_DRAGON_ITEM = ITEMS.register("digitama_dragon",
            () -> new BlockItem(BlocksInit.DIGITAMA_DRAGON.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_BEAST = BLOCKS.register("digitama_beast",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_BEAST_ITEM = ITEMS.register("digitama_beast",
            () -> new BlockItem(BlocksInit.DIGITAMA_BEAST.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_HOLY = BLOCKS.register("digitama_holy",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_HOLY_ITEM = ITEMS.register("digitama_holy",
            () -> new BlockItem(BlocksInit.DIGITAMA_HOLY.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_PLANTINSECT = BLOCKS.register("digitama_plantinsect",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_PLANTINSECT_ITEM = ITEMS.register("digitama_plantinsect",
            () -> new BlockItem(BlocksInit.DIGITAMA_PLANTINSECT.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_NIGHTMARE = BLOCKS.register("digitama_nightmare",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_NIGHTMARE_ITEM = ITEMS.register("digitama_nightmare",
            () -> new BlockItem(BlocksInit.DIGITAMA_NIGHTMARE.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_WIND = BLOCKS.register("digitama_wind",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_WIND_ITEM = ITEMS.register("digitama_wind",
            () -> new BlockItem(BlocksInit.DIGITAMA_WIND.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_EARTH = BLOCKS.register("digitama_earth",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_EARTH_ITEM = ITEMS.register("digitama_earth",
            () -> new BlockItem(BlocksInit.DIGITAMA_EARTH.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_AQUAN = BLOCKS.register("digitama_aquan",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_AQUAN_ITEM = ITEMS.register("digitama_aquan",
            () -> new BlockItem(BlocksInit.DIGITAMA_AQUAN.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_MACHINE = BLOCKS.register("digitama_machine",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_MACHINE_ITEM = ITEMS.register("digitama_machine",
            () -> new BlockItem(BlocksInit.DIGITAMA_MACHINE.get(), new Item.Properties()));
}