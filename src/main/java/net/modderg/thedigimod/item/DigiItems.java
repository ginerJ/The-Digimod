package net.modderg.thedigimod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.block.DigiBlocks;
import net.modderg.thedigimod.entity.DigitalEntities;
import net.modderg.thedigimod.particles.DigitalParticles;

public class DigiItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheDigiMod.MOD_ID);

    //Babies
    public static final RegistryObject<Item> BOTAMON = ITEMS.register("botamon", () -> new CustomDimItem(new Item.Properties().stacksTo(16), DigitalEntities.KOROMON, "Agumon"));
    public static final RegistryObject<Item> BOTAMOND = ITEMS.register("botamond", () -> new CustomDimItem(new Item.Properties().stacksTo(16), DigitalEntities.KOROMONB, "Black Agumon"));
    public static final RegistryObject<Item> BUBBMON = ITEMS.register("bubbmon", () -> new CustomDimItem(new Item.Properties().stacksTo(16), DigitalEntities.MOCHIMON, "Tentomon"));
    public static final RegistryObject<Item> BUBBMONK = ITEMS.register("bubbmonk", () -> new CustomDimItem(new Item.Properties().stacksTo(16), DigitalEntities.MOCHIMONK, "Kunemon"));
    public static final RegistryObject<Item> PUNIMON = ITEMS.register("punimon", () -> new CustomDimItem(new Item.Properties().stacksTo(16), DigitalEntities.TSUNOMON, "Bearmon"));
    public static final RegistryObject<Item> JYARIMON = ITEMS.register("jyarimon", () -> new CustomDimItem(new Item.Properties().stacksTo(16), DigitalEntities.GIGIMON, "Guilmon"));
    public static final RegistryObject<Item> PETITMON = ITEMS.register("petitmon", () -> new CustomDimItem(new Item.Properties().stacksTo(16), DigitalEntities.BABYDMON, "Dracomon"));
    public static final RegistryObject<Item> PUYOMON = ITEMS.register("puyomon", () -> new CustomDimItem(new Item.Properties().stacksTo(16), DigitalEntities.PUYOYOMON, "Jellymon"));
    public static final RegistryObject<Item> DOKIMON = ITEMS.register("dokimon", () -> new CustomDimItem(new Item.Properties().stacksTo(16), DigitalEntities.BIBIMON, "Pulsemon"));
    public static final RegistryObject<Item> NYOKIMON = ITEMS.register("nyokimon", () -> new CustomDimItem(new Item.Properties().stacksTo(16), DigitalEntities.YOKOMON, "Biyomon"));

    //Digivices
    public static final RegistryObject<Item> VITALBRACELET = ITEMS.register("vitalbracelet", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DIGIVICE = ITEMS.register("digivice", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> VPET = ITEMS.register("vpet", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));

    //Exp Items
    public static final RegistryObject<Item> DRAGON_DATA = ITEMS.register("dragon_data", () -> new CustomXpItem(new Item.Properties(), 0));
    public static final RegistryObject<Item> BEAST_DATA = ITEMS.register("beast_data", () -> new CustomXpItem(new Item.Properties(), 1));
    public static final RegistryObject<Item> PLANTINSECT_DATA = ITEMS.register("plantinsect_data", () -> new CustomXpItem(new Item.Properties(), 2));
    public static final RegistryObject<Item> AQUAN_DATA = ITEMS.register("aquan_data", () -> new CustomXpItem(new Item.Properties(), 3));
    public static final RegistryObject<Item> WIND_DATA = ITEMS.register("wind_data", () -> new CustomXpItem(new Item.Properties(), 4));
    public static final RegistryObject<Item> MACHINE_DATA = ITEMS.register("machine_data", () -> new CustomXpItem(new Item.Properties(), 5));
    public static final RegistryObject<Item> EARTH_DATA = ITEMS.register("earth_data", () -> new CustomXpItem(new Item.Properties(), 6));
    public static final RegistryObject<Item> NIGHTMARE_DATA = ITEMS.register("nightmare_data", () -> new CustomXpItem(new Item.Properties(), 7));
    public static final RegistryObject<Item> HOLY_DATA = ITEMS.register("holy_data", () -> new CustomXpItem(new Item.Properties(), 8));

    //Food Items
    public static final RegistryObject<Item> DIGI_MEAT = ITEMS.register("digi_meat", () -> new ItemNameBlockItem(DigiBlocks.MEAT_CROP.get(), new Item.Properties()));

    //Misc Items
    public static final RegistryObject<Item> BLACK_DIGITRON = ITEMS.register("black_digitron", () -> new DigitronItem(new Item.Properties(), DigitalParticles.DIGITRON_PARTICLES));
    public static final RegistryObject<Item> BAG_ITEM = ITEMS.register("bag_item", () -> new SpawnGoodItem(DigitalEntities.PUNCHING_BAG, 0x000000, 0xFFFFFF,new Item.Properties()));
    public static final RegistryObject<Item> TABLE_ITEM = ITEMS.register("table_item", () -> new SpawnGoodItem(DigitalEntities.SP_TABLE, 0x000000, 0xFFFFFF,new Item.Properties()));
    public static final RegistryObject<Item> TARGET_ITEM = ITEMS.register("target_item", () -> new SpawnGoodItem(DigitalEntities.SP_TARGET, 0x000000, 0xFFFFFF,new Item.Properties()));
    public static final RegistryObject<Item> SHIELD_ITEM = ITEMS.register("shield_item", () -> new SpawnGoodItem(DigitalEntities.SHIELD_STAND, 0x000000, 0xFFFFFF,new Item.Properties()));
    public static final RegistryObject<Item> UPDATE_ITEM = ITEMS.register("update_item", () -> new SpawnGoodItem(DigitalEntities.UPDATE_GOOD, 0x000000, 0xFFFFFF,new Item.Properties()));

    //admin stuff
    public static final RegistryObject<Item> ADMIN_LOGO = ITEMS.register("admin_logo", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ATTACK_GB = ITEMS.register("gbattack", () -> new StatUpItem(new Item.Properties().stacksTo(1), "attack"));
    public static final RegistryObject<Item> DEFENCE_GB = ITEMS.register("gbdefence", () -> new StatUpItem(new Item.Properties().stacksTo(1), "defence"));
    public static final RegistryObject<Item> SPATTACK_GB = ITEMS.register("gbspattack", () -> new StatUpItem(new Item.Properties().stacksTo(1), "spattack"));
    public static final RegistryObject<Item> SPDEFENCE_GB = ITEMS.register("gbspdefence", () -> new StatUpItem(new Item.Properties().stacksTo(1), "spdefence"));
    public static final RegistryObject<Item> HEALTH_DRIVES = ITEMS.register("health_drives", () -> new StatUpItem(new Item.Properties().stacksTo(1), "health"));
    public static final RegistryObject<Item> BATTLE_CHIP = ITEMS.register("battles_chip", () -> new StatUpItem(new Item.Properties().stacksTo(1), "battle"));
    public static final RegistryObject<Item> TAMER_LEASH = ITEMS.register("tamer_leash", () -> new TameItem(new Item.Properties()));
}
