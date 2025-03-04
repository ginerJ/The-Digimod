package net.modderg.thedigimod.server.events;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.item.TDItems;

import java.util.HashMap;
import java.util.stream.IntStream;

public class TagLootableStuff {

    private static final TagKey<EntityType<?>> featherKey = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "drops/feather"));
    private static final TagKey<EntityType<?>> ironIngotKey = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "drops/iron_ingot"));
    private static final TagKey<EntityType<?>> goldIngotKey = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "drops/gold_ingot"));
    private static final TagKey<EntityType<?>> slimeBallKey = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "drops/slime_ball"));
    private static final TagKey<EntityType<?>> inkSacKey = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "drops/ink_sac"));
    private static final TagKey<EntityType<?>> scuteKey = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "drops/scute"));
    private static final TagKey<EntityType<?>> rabbitHideKey = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "drops/rabbit_hide"));
    private static final TagKey<EntityType<?>> poopKey = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "drops/poop"));
    private static final TagKey<EntityType<?>> fireChargeKey = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "drops/fire_charge"));
    private static final TagKey<EntityType<?>> snowBallKey = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "drops/snow_ball"));

    public static HashMap<TagKey<EntityType<?>>, Item> keyToItemMap = new HashMap<>();

    public static void init(){
        keyToItemMap.put(featherKey, Items.FEATHER);
        keyToItemMap.put(ironIngotKey, Items.IRON_INGOT);
        keyToItemMap.put(goldIngotKey, Items.GOLD_INGOT);
        keyToItemMap.put(slimeBallKey, Items.SLIME_BALL);
        keyToItemMap.put(inkSacKey, Items.INK_SAC);
        keyToItemMap.put(scuteKey, Items.SCUTE);
        keyToItemMap.put(rabbitHideKey, Items.RABBIT_HIDE);
        keyToItemMap.put(poopKey, TDItems.POOP.get());
        keyToItemMap.put(fireChargeKey, Items.FIRE_CHARGE);
        keyToItemMap.put(snowBallKey, Items.SNOWBALL);
    }

    public static void TryToAddDropToDigimon(LivingDropsEvent event, DigimonEntity digimon){
        for(TagKey<EntityType<?>> key : keyToItemMap.keySet())
            if(digimon.getType().is(key))
                IntStream.range(0, digimon.getRandom().nextInt(1, 4))
                        .forEach(i -> event.getDrops().add(new ItemEntity(digimon.level(), digimon.getX(), digimon.getY(), digimon.getZ(), new ItemStack(keyToItemMap.get(key)))));
    }
}
