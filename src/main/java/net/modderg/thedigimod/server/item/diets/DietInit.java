package net.modderg.thedigimod.server.item.diets;

import net.minecraft.world.item.Items;
import net.modderg.thedigimod.server.block.BlocksInit;
import net.modderg.thedigimod.server.item.TDItems;

public class DietInit {

    public static DigimonDiet getDiet(String diet) {
        return switch (diet) {
            case "baby" -> BBY_DIET;
            case "seafood" -> SEA_DIET;
            case "rock" -> ROCK_DIET;
            case "carnivore" -> CARNIVORE_DIET;
            case "poop" -> CRAP_DIET;
            case "bug" -> BUG_DIET;
            case "scrap" -> SCRAP_DIET;
            case "botanic" -> BOTANIC_DIET;
            case "herbivore" -> HERBIVORE_DIET;
            default -> REGULAR_DIET;
        };
    }

    public final static DigimonDiet BBY_DIET = new DigimonDiet(
            Items.SUGAR, TDItems.DIGI_MEAT.get(), Items.MILK_BUCKET, TDItems.DIGI_CAKE.get());

    public final static DigimonDiet BUG_DIET = new DigimonDiet(
            Items.SUGAR, TDItems.DIGI_MEAT.get(), Items.HONEYCOMB, Items.HONEY_BOTTLE);

    public final static DigimonDiet ROCK_DIET = new DigimonDiet(
            TDItems.DIGI_MEAT.get(), Items.COAL, BlocksInit.LED_SHROOM_ITEM.get(), Items.AMETHYST_SHARD);

    public final static DigimonDiet SEA_DIET = new DigimonDiet(
            TDItems.DIGI_MEAT.get(), TDItems.DIGI_MEAT_BIG.get(), Items.SALMON, TDItems.DIGI_SUSHI.get());

    public final static DigimonDiet CARNIVORE_DIET = new DigimonDiet(
            Items.BONE, TDItems.DIGI_MEAT.get(), TDItems.DIGI_MEAT_BIG.get(), TDItems.DIGI_RIBS.get());

    public final static DigimonDiet CRAP_DIET = new DigimonDiet(
            TDItems.DIGI_MEAT.get(), TDItems.DIGI_MEAT_ROTTEN.get(), TDItems.POOP.get(), TDItems.GOLD_POOP.get());

    public final static DigimonDiet SCRAP_DIET = new DigimonDiet(
            TDItems.DIGI_MEAT.get(), Items.COPPER_INGOT, TDItems.HUANGLONG_ORE.get(), TDItems.CHROME_DIGIZOID.get());

    public final static DigimonDiet BOTANIC_DIET = new DigimonDiet(
            TDItems.DIGI_MEAT.get(), Items.BONE_MEAL, TDItems.DIGI_MEAT_BIG.get(), Items.WATER_BUCKET);

    public final static DigimonDiet HERBIVORE_DIET = new DigimonDiet(
            TDItems.DIGI_MEAT.get(), Items.BONE_MEAL, TDItems.DIGI_MEAT_BIG.get(), Items.POTION);

    public final static DigimonDiet REGULAR_DIET = new DigimonDiet(
            TDItems.GUILMON_BREAD.get(), TDItems.DIGI_MEAT.get(), TDItems.DIGI_MEAT_BIG.get(), TDItems.DIGI_CAKE.get());
}
