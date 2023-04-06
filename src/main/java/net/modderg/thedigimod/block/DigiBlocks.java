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
import net.modderg.thedigimod.item.DigiItems;

import java.util.function.Supplier;

public class DigiBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TheDigiMod.MOD_ID);

    public static final RegistryObject<Block> MEAT_CROP = BLOCKS.register("digimeat_crop",
            () -> new MeatCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT)));

    private static <T extends Block>RegistryObject<Item> registryBlockItem(String name, RegistryObject<T> block){
        return DigiItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties()));
    }
}