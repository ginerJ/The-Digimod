package net.modderg.thedigimod.server.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.modderg.thedigimod.TheDigiMod;

import static net.modderg.thedigimod.server.item.TDItems.ITEMS;

public class TDBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TheDigiMod.MOD_ID);

    public static final RegistryObject<Block> LED_SHROOM = BLOCKS.register("led_shroom",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH).lightLevel((state) -> 14)));

    public static final RegistryObject<Item> LED_SHROOM_ITEM = ITEMS.register("led_shroom",
            () -> new BlockItem(LED_SHROOM.get(), new Item.Properties()));

    public static final RegistryObject<Block> CARD_ORE = BLOCKS.register("digi_card_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));

    public static final RegistryObject<Item> CARD_ORE_ITEM = ITEMS.register("digi_card_ore",
            () -> new BlockItem(CARD_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Block> CARD_DEEPSLATE_ORE = BLOCKS.register("digi_card_deepslate_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));

    public static final RegistryObject<Item> CARD_DEEPSLATE_ORE_ITEM = ITEMS.register("digi_card_deepslate_ore",
            () -> new BlockItem(CARD_DEEPSLATE_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Block> HUANGLONG_DEEPSLATE_ORE = BLOCKS.register("huanglong_deepslate_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));

    public static final RegistryObject<Item> HUANGLONG_DEEPSLATE_ORE_ITEM = ITEMS.register("huanglong_deepslate_ore",
            () -> new BlockItem(HUANGLONG_DEEPSLATE_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Block> MEAT_CROP = BLOCKS.register("digimeat_crop",
            () -> new MeatCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT)));

    public static final RegistryObject<Block> DIGITAMA_DRAGON = BLOCKS.register("digitama_dragon",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_DRAGON_ITEM = ITEMS.register("digitama_dragon",
            () -> new BlockItem(TDBlocks.DIGITAMA_DRAGON.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_BEAST = BLOCKS.register("digitama_beast",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_BEAST_ITEM = ITEMS.register("digitama_beast",
            () -> new BlockItem(TDBlocks.DIGITAMA_BEAST.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_HOLY = BLOCKS.register("digitama_holy",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_HOLY_ITEM = ITEMS.register("digitama_holy",
            () -> new BlockItem(TDBlocks.DIGITAMA_HOLY.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_PLANTINSECT = BLOCKS.register("digitama_plantinsect",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_PLANTINSECT_ITEM = ITEMS.register("digitama_plantinsect",
            () -> new BlockItem(TDBlocks.DIGITAMA_PLANTINSECT.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_NIGHTMARE = BLOCKS.register("digitama_nightmare",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_NIGHTMARE_ITEM = ITEMS.register("digitama_nightmare",
            () -> new BlockItem(TDBlocks.DIGITAMA_NIGHTMARE.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_WIND = BLOCKS.register("digitama_wind",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_WIND_ITEM = ITEMS.register("digitama_wind",
            () -> new BlockItem(TDBlocks.DIGITAMA_WIND.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_EARTH = BLOCKS.register("digitama_earth",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_EARTH_ITEM = ITEMS.register("digitama_earth",
            () -> new BlockItem(TDBlocks.DIGITAMA_EARTH.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_AQUAN = BLOCKS.register("digitama_aquan",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_AQUAN_ITEM = ITEMS.register("digitama_aquan",
            () -> new BlockItem(TDBlocks.DIGITAMA_AQUAN.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIGITAMA_MACHINE = BLOCKS.register("digitama_machine",
            () -> new DigitamaBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));

    public static final RegistryObject<Item> DIGITAMA_MACHINE_ITEM = ITEMS.register("digitama_machine",
            () -> new BlockItem(TDBlocks.DIGITAMA_MACHINE.get(), new Item.Properties()));

}