package net.modderg.thedigimod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
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
    public static final RegistryObject<Item> BOTAMON = ITEMS.register("botamon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.KOROMON, "Agumon"));
    public static final RegistryObject<Item> BOTAMOND = ITEMS.register("botamond", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.KOROMONB, "Black Agumon"));
    public static final RegistryObject<Item> BUBBMON = ITEMS.register("bubbmon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.MOCHIMON, "Tentomon"));
    public static final RegistryObject<Item> BUBBMONK = ITEMS.register("bubbmonk", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.MOCHIMONK, "Kunemon"));
    public static final RegistryObject<Item> PUNIMON = ITEMS.register("punimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.TSUNOMON, "Bearmon"));
    public static final RegistryObject<Item> JYARIMON = ITEMS.register("jyarimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.GIGIMON, "Guilmon"));
    public static final RegistryObject<Item> PETITMON = ITEMS.register("petitmon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.BABYDMON, "Dracomon"));
    public static final RegistryObject<Item> PUYOMON = ITEMS.register("puyomon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.PUYOYOMON, "Jellymon"));
    public static final RegistryObject<Item> DOKIMON = ITEMS.register("dokimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.BIBIMON, "Pulsemon"));
    public static final RegistryObject<Item> NYOKIMON = ITEMS.register("nyokimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.YOKOMON, "Biyomon"));
    public static final RegistryObject<Item> CONOMON = ITEMS.register("conomon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.KOKOMON, "Lopmon"));
    public static final RegistryObject<Item> KIIMON = ITEMS.register("kiimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.YAAMON, "Impmon"));

    //Digivices
    public static final RegistryObject<Item> VITALBRACELET = ITEMS.register("vitalbracelet", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DIGIVICE = ITEMS.register("digivice", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> VPET = ITEMS.register("vpet", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DIGIVICE_IC = ITEMS.register("digivice_ic", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DIGIVICE_BURST = ITEMS.register("digivice_burst", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));

    //Exp Items
    public static final RegistryObject<Item> DRAGON_DATA = ITEMS.register("dragon_data", () -> new CustomXpItem(new Item.Properties(), 0,1));
    public static final RegistryObject<Item> BEAST_DATA = ITEMS.register("beast_data", () -> new CustomXpItem(new Item.Properties(), 1,1));
    public static final RegistryObject<Item> PLANTINSECT_DATA = ITEMS.register("plantinsect_data", () -> new CustomXpItem(new Item.Properties(), 2,1));
    public static final RegistryObject<Item> AQUAN_DATA = ITEMS.register("aquan_data", () -> new CustomXpItem(new Item.Properties(), 3,1));
    public static final RegistryObject<Item> WIND_DATA = ITEMS.register("wind_data", () -> new CustomXpItem(new Item.Properties(), 4,1));
    public static final RegistryObject<Item> MACHINE_DATA = ITEMS.register("machine_data", () -> new CustomXpItem(new Item.Properties(), 5,1));
    public static final RegistryObject<Item> EARTH_DATA = ITEMS.register("earth_data", () -> new CustomXpItem(new Item.Properties(), 6,1));
    public static final RegistryObject<Item> NIGHTMARE_DATA = ITEMS.register("nightmare_data", () -> new CustomXpItem(new Item.Properties(), 7,1));
    public static final RegistryObject<Item> HOLY_DATA = ITEMS.register("holy_data", () -> new CustomXpItem(new Item.Properties(), 8,1));

    public static final RegistryObject<Item> DRAGON_PACK = ITEMS.register("dragon_pack", () -> new CustomXpItem(new Item.Properties(), 0,9));
    public static final RegistryObject<Item> BEAST_PACK= ITEMS.register("beast_pack", () -> new CustomXpItem(new Item.Properties(), 1,9));
    public static final RegistryObject<Item> PLANTINSECT_PACK = ITEMS.register("plantinsect_pack", () -> new CustomXpItem(new Item.Properties(), 2,9));
    public static final RegistryObject<Item> AQUAN_PACK = ITEMS.register("aquan_pack", () -> new CustomXpItem(new Item.Properties(), 3,9));
    public static final RegistryObject<Item> WIND_PACK = ITEMS.register("wind_pack", () -> new CustomXpItem(new Item.Properties(), 4,9));
    public static final RegistryObject<Item> MACHINE_PACK = ITEMS.register("machine_pack", () -> new CustomXpItem(new Item.Properties(), 5,9));
    public static final RegistryObject<Item> EARTH_PACK = ITEMS.register("earth_pack", () -> new CustomXpItem(new Item.Properties(), 6,9));
    public static final RegistryObject<Item> NIGHTMARE_PACK = ITEMS.register("nightmare_pack", () -> new CustomXpItem(new Item.Properties(), 7,9));
    public static final RegistryObject<Item> HOLY_PACK = ITEMS.register("holy_pack", () -> new CustomXpItem(new Item.Properties(), 8,9));

    //Food Items
    public static final RegistryObject<Item> DIGI_MEAT = ITEMS.register("digi_meat", () -> new ItemNameBlockItem(DigiBlocks.MEAT_CROP.get(), new Item.Properties()));

    //Misc Items
    public static final RegistryObject<Item> TRAINING_BAG = ITEMS.register("training_bag", () -> new ContainerItem(new Item.Properties(), new RegistryObject[]{DigiItems.TABLE_ITEM, DigiItems.BAG_ITEM, DigiItems.SHIELD_ITEM, DigiItems.TARGET_ITEM, DigiItems.UPDATE_ITEM}));
    public static final RegistryObject<Item> BLACK_DIGITRON = ITEMS.register("black_digitron", () -> new DigitronItem(new Item.Properties(), DigitalParticles.DIGITRON_PARTICLES));
    public static final RegistryObject<Item> DIGI_CORE = ITEMS.register("digi_core", () -> new StatUpItem(new Item.Properties().stacksTo(1), "lifes"));

    //training items
    public static final RegistryObject<Item> BAG_ITEM = ITEMS.register("bag_item", () -> new SpawnGoodItem(DigitalEntities.PUNCHING_BAG, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "attack"));
    public static final RegistryObject<Item> TABLE_ITEM = ITEMS.register("table_item", () -> new SpawnGoodItem(DigitalEntities.SP_TABLE, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.defense"));
    public static final RegistryObject<Item> TARGET_ITEM = ITEMS.register("target_item", () -> new SpawnGoodItem(DigitalEntities.SP_TARGET, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.attack"));
    public static final RegistryObject<Item> SHIELD_ITEM = ITEMS.register("shield_item", () -> new SpawnGoodItem(DigitalEntities.SHIELD_STAND, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "defense"));
    public static final RegistryObject<Item> UPDATE_ITEM = ITEMS.register("update_item", () -> new SpawnGoodItem(DigitalEntities.UPDATE_GOOD, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "health"));

    public static final RegistryObject<Item> DRAGON_BONE_ITEM = ITEMS.register("dragon_bone", () -> new SpawnGoodItem(DigitalEntities.DRAGON_BONE, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "attack"));
    public static final RegistryObject<Item> BALL_GOOD_ITEM = ITEMS.register("ball_good", () -> new SpawnGoodItem(DigitalEntities.BALL_GOOD, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "attack"));
    public static final RegistryObject<Item> CLOWN_BOX = ITEMS.register("clown_box", () -> new SpawnGoodItem(DigitalEntities.CLOWN_BOX, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.attack"));
    public static final RegistryObject<Item> FLYTRAP_GOOD = ITEMS.register("flytrap_good", () -> new SpawnGoodItem(DigitalEntities.FLYTRAP_GOOD, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.defense"));
    public static final RegistryObject<Item> OLD_PC_GOOD = ITEMS.register("old_pc", () -> new SpawnGoodItem(DigitalEntities.OLD_PC_GOOD, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "defense"));
    public static final RegistryObject<Item> LIRA_GOOD = ITEMS.register("lira_good", () -> new SpawnGoodItem(DigitalEntities.LIRA_GOOD, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.defense"));
    public static final RegistryObject<Item> RED_FREEZER = ITEMS.register("red_freezer", () -> new SpawnGoodItem(DigitalEntities.RED_FREEZER, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.attack"));
    public static final RegistryObject<Item> WIND_VANE = ITEMS.register("wind_vane", () -> new SpawnGoodItem(DigitalEntities.WIND_VANE, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.attack"));

    //admin stuff
    public static final RegistryObject<Item> ADMIN_LOGO = ITEMS.register("admin_logo", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ATTACK_GB = ITEMS.register("gbattack", () -> new StatUpItem(new Item.Properties().stacksTo(1), "attack"));
    public static final RegistryObject<Item> DEFENCE_GB = ITEMS.register("gbdefence", () -> new StatUpItem(new Item.Properties().stacksTo(1), "defence"));
    public static final RegistryObject<Item> SPATTACK_GB = ITEMS.register("gbspattack", () -> new StatUpItem(new Item.Properties().stacksTo(1), "spattack"));
    public static final RegistryObject<Item> SPDEFENCE_GB = ITEMS.register("gbspdefence", () -> new StatUpItem(new Item.Properties().stacksTo(1), "spdefence"));
    public static final RegistryObject<Item> HEALTH_DRIVES = ITEMS.register("health_drives", () -> new StatUpItem(new Item.Properties().stacksTo(1), "health"));
    public static final RegistryObject<Item> BATTLE_CHIP = ITEMS.register("battles_chip", () -> new StatUpItem(new Item.Properties().stacksTo(1), "battle"));
    public static final RegistryObject<Item> TAMER_LEASH = ITEMS.register("tamer_leash", () -> new TameItem(new Item.Properties()));
    public static final RegistryObject<Item> GOBLIMON_BAT = ITEMS.register("goblimon_bat", () -> new StatUpItem(new Item.Properties().stacksTo(1), "mistakes"));
}
