package net.modderg.thedigimod.server.item.diets;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import oshi.util.tuples.Pair;

public class DigimonDiet {

    public final ItemStack maxTierFood;
    public final ItemStack highTierFood;
    public final ItemStack midTierFood;
    public final ItemStack lowTierFood;

    public DigimonDiet(Item low, Item mid, Item high, Item max) {
        this.maxTierFood = new ItemStack(max);
        this.highTierFood = new ItemStack(high);
        this.midTierFood = new ItemStack(mid);
        this.lowTierFood = new ItemStack(low);
    }

    public boolean isPartOfDiet(ItemStack itemStack){
        return itemStack.is(maxTierFood.getItem()) || itemStack.is(highTierFood.getItem()) || itemStack.is(midTierFood.getItem()) || itemStack.is(lowTierFood.getItem());
    }

    public Pair<Integer, Integer> getCaloriesAndHeal(ItemStack itemStack){
        if(itemStack.is(maxTierFood.getItem()))
            return new Pair<>(200, 60);
        else if(itemStack.is(highTierFood.getItem()))
            return new Pair<>(100, 25);
        else if(itemStack.is(midTierFood.getItem()))
            return new Pair<>(50, 10);
        else
            return new Pair<>(25, 5);
    }
}
