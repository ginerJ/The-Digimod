package net.modderg.thedigimod.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.block.DigiBlocks;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.DigitalEntities;
import net.modderg.thedigimod.item.custom.*;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.projectiles.DigitalProjectiles;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DigiItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheDigiMod.MOD_ID);

    public static Map<String, RegistryObject<Item>> itemMap;

    public static void init() {

        List<RegistryObject<Item>> itemList = ITEMS.getEntries().stream().toList();
        List<String> itemNameList = ITEMS.getEntries().stream().map(e -> e.getId().getPath()).toList();

        itemMap = IntStream.range(0, itemNameList.size())
                .boxed()
                .collect(Collectors.toMap(itemNameList::get, itemList::get));
    }

    //Babies
    public static final RegistryObject<Item> BOTAMON = ITEMS.register("botamon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.KOROMON, "Koromon"));
    public static final RegistryObject<Item> BUBBMON = ITEMS.register("bubbmon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.MOCHIMON, "Mochimon"));
    public static final RegistryObject<Item> PUNIMON = ITEMS.register("punimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.TSUNOMON, "Tsunomon"));
    public static final RegistryObject<Item> JYARIMON = ITEMS.register("jyarimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.GIGIMON, "Gigimon"));
    public static final RegistryObject<Item> PETITMON = ITEMS.register("petitmon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.BABYDMON, "Babydmon"));
    public static final RegistryObject<Item> PUYOMON = ITEMS.register("puyomon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.PUYOYOMON, "Puyoyomon"));
    public static final RegistryObject<Item> DOKIMON = ITEMS.register("dokimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.BIBIMON, "Bibimon"));
    public static final RegistryObject<Item> NYOKIMON = ITEMS.register("nyokimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.YOKOMON, "Yokomon"));
    public static final RegistryObject<Item> CONOMON = ITEMS.register("conomon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.CHOCOMON, "Kokomon"));
    public static final RegistryObject<Item> KIIMON = ITEMS.register("kiimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.KEEMON, "Yaamon"));
    public static final RegistryObject<Item> SUNAMON = ITEMS.register("sunamon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.GOROMON, "Goromon"));
    public static final RegistryObject<Item> POYOMON = ITEMS.register("poyomon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.TOKOMON, "Tokomon"));
    public static final RegistryObject<Item> DATIRIMON = ITEMS.register("datirimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), DigitalEntities.CHAPMON, "Tokomon"));

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
    public static final RegistryObject<Item> DIGI_MEAT = ITEMS.register("digi_meat", () -> new ItemNameBlockItem(DigiBlocks.MEAT_CROP.get(), new Item.Properties().food(Foods.POTATO)));
    public static final RegistryObject<Item> GUILMON_BREAD = ITEMS.register("guilmon_bread", () -> new Item(new Item.Properties().food(Foods.POTATO)));

    //Misc Items
    public static final RegistryObject<Item> TRAINING_BAG = ITEMS.register("training_bag", () -> new ContainerItem(new Item.Properties(), new RegistryObject[]{DigiItems.TABLE_ITEM, DigiItems.BAG_ITEM, DigiItems.SHIELD_ITEM, DigiItems.TARGET_ITEM, DigiItems.UPDATE_ITEM}));
    public static final RegistryObject<Item> BLACK_DIGITRON = ITEMS.register("black_digitron", () -> new DigitronItem(new Item.Properties(), DigitalParticles.DIGITRON_PARTICLES));
    public static final RegistryObject<Item> DIGI_CORE = ITEMS.register("digi_core", () -> new StatUpItem(new Item.Properties().stacksTo(1), "lifes",0));
    public static final RegistryObject<Item> DARK_TOWER_SHARD = ITEMS.register("dark_tower_shard", () -> new DarkTowerShardItem(new Item.Properties()));

    public static final RegistryObject<Item> ATTACK_BYTE = ITEMS.register("byteattack", () -> new StatUpItem(new Item.Properties(), "attack", 5));
    public static final RegistryObject<Item> DEFENSE_BYTE = ITEMS.register("bytedefense", () -> new StatUpItem(new Item.Properties(), "defence",5));
    public static final RegistryObject<Item> SPATTACK_BYTE = ITEMS.register("bytespattack", () -> new StatUpItem(new Item.Properties(), "spattack",5));
    public static final RegistryObject<Item> SPDEFENSE_BYTE = ITEMS.register("bytespdefense", () -> new StatUpItem(new Item.Properties(), "spdefence",5));
    public static final RegistryObject<Item> HEALTH_BYTE = ITEMS.register("bytehealth", () -> new StatUpItem(new Item.Properties(), "health",5));

    //training good items
    public static final RegistryObject<Item> BAG_ITEM = ITEMS.register("bag_item", () -> new SpawnGoodItem(DigitalProjectiles.PUNCHING_BAG, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "attack"));
    public static final RegistryObject<Item> TABLE_ITEM = ITEMS.register("table_item", () -> new SpawnGoodItem(DigitalProjectiles.SP_TABLE, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.defense"));
    public static final RegistryObject<Item> TARGET_ITEM = ITEMS.register("target_item", () -> new SpawnGoodItem(DigitalProjectiles.SP_TARGET, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.attack"));
    public static final RegistryObject<Item> SHIELD_ITEM = ITEMS.register("shield_item", () -> new SpawnGoodItem(DigitalProjectiles.SHIELD_STAND, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "defense"));
    public static final RegistryObject<Item> UPDATE_ITEM = ITEMS.register("update_item", () -> new SpawnGoodItem(DigitalProjectiles.UPDATE_GOOD, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "health"));

    public static final RegistryObject<Item> DRAGON_BONE_ITEM = ITEMS.register("dragon_bone", () -> new SpawnGoodItem(DigitalProjectiles.DRAGON_BONE, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "attack"));
    public static final RegistryObject<Item> BALL_GOOD_ITEM = ITEMS.register("ball_good", () -> new SpawnGoodItem(DigitalProjectiles.BALL_GOOD, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "attack"));
    public static final RegistryObject<Item> CLOWN_BOX = ITEMS.register("clown_box", () -> new SpawnGoodItem(DigitalProjectiles.CLOWN_BOX, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.attack"));
    public static final RegistryObject<Item> FLYTRAP_GOOD = ITEMS.register("flytrap_good", () -> new SpawnGoodItem(DigitalProjectiles.FLYTRAP_GOOD, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.defense"));
    public static final RegistryObject<Item> OLD_PC_GOOD = ITEMS.register("old_pc", () -> new SpawnGoodItem(DigitalProjectiles.OLD_PC_GOOD, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "defense"));
    public static final RegistryObject<Item> LIRA_GOOD = ITEMS.register("lira_good", () -> new SpawnGoodItem(DigitalProjectiles.LIRA_GOOD, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.defense"));
    public static final RegistryObject<Item> RED_FREEZER = ITEMS.register("red_freezer", () -> new SpawnGoodItem(DigitalProjectiles.RED_FREEZER, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.attack"));
    public static final RegistryObject<Item> WIND_VANE = ITEMS.register("wind_vane", () -> new SpawnGoodItem(DigitalProjectiles.WIND_VANE, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "sp.attack"));
    public static final RegistryObject<Item> TRAINING_ROCK = ITEMS.register("training_rock", () -> new SpawnGoodItem(DigitalProjectiles.TRAINING_ROCK, 0x000000, 0xFFFFFF,new Item.Properties().stacksTo(1), "defense"));

    //admin stuff
    public static final RegistryObject<Item> ADMIN_LOGO = ITEMS.register("admin_logo", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ATTACK_GB = ITEMS.register("gbattack", () -> new StatUpItem(new Item.Properties().stacksTo(1), "attack", CustomDigimon.MAXMEGASTAT));
    public static final RegistryObject<Item> DEFENCE_GB = ITEMS.register("gbdefence", () -> new StatUpItem(new Item.Properties().stacksTo(1), "defence",CustomDigimon.MAXMEGASTAT));
    public static final RegistryObject<Item> SPATTACK_GB = ITEMS.register("gbspattack", () -> new StatUpItem(new Item.Properties().stacksTo(1), "spattack",CustomDigimon.MAXMEGASTAT));
    public static final RegistryObject<Item> SPDEFENCE_GB = ITEMS.register("gbspdefence", () -> new StatUpItem(new Item.Properties().stacksTo(1), "spdefence",CustomDigimon.MAXMEGASTAT));
    public static final RegistryObject<Item> HEALTH_DRIVES = ITEMS.register("health_drives", () -> new StatUpItem(new Item.Properties().stacksTo(1), "health",CustomDigimon.MAXMEGASTAT));
    public static final RegistryObject<Item> BATTLE_CHIP = ITEMS.register("battles_chip", () -> new StatUpItem(new Item.Properties().stacksTo(1), "battle",0));
    public static final RegistryObject<Item> GOBLIMON_BAT = ITEMS.register("goblimon_bat", () -> new StatUpItem(new Item.Properties().stacksTo(1), "mistakes",0));
    public static final RegistryObject<Item> TAMER_LEASH = ITEMS.register("tamer_leash", () -> new TameItem(new Item.Properties()));

    //move items
    public static final RegistryObject<Item> CHIP_BULLET = ITEMS.register("chip_bullet", () -> new SpMoveItem(new Item.Properties(),"bullet"));
    public static final RegistryObject<Item> CHIP_PEPPER_BREATH = ITEMS.register("chip_pepper_breath", () -> new SpMoveItem(new Item.Properties(),"pepper_breath").addEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_MEGA_FLAME = ITEMS.register("chip_mega_flame", () -> new SpMoveItem(new Item.Properties(),"mega_flame").addEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_V_ARROW = ITEMS.register("chip_v_arrow", () -> new SpMoveItem(new Item.Properties(),"v_arrow"));
    public static final RegistryObject<Item> CHIP_HYPER_HEAT = ITEMS.register("chip_hyper_heat", () -> new SpMoveItem(new Item.Properties(),"hyper_heat").addEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_METEOR_WING = ITEMS.register("chip_meteor_wing", () -> new SpMoveItem(new Item.Properties(),"meteor_wing").addEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_BEAST_SLASH = ITEMS.register("chip_beast_slash", () -> new SpMoveItem(new Item.Properties(),"beast_slash"));
    public static final RegistryObject<Item> CHIP_INK_GUN = ITEMS.register("chip_ink_gun", () -> new SpMoveItem(new Item.Properties(),"ink_gun").addEffect(" Poisons"));
    public static final RegistryObject<Item> CHIP_SNOW_BULLET = ITEMS.register("chip_snow_bullet", () -> new SpMoveItem(new Item.Properties(),"snow_bullet").setRepeat(4));
    public static final RegistryObject<Item> CHIP_PETIT_THUNDER = ITEMS.register("chip_petit_thunder", () -> new SpMoveItem(new Item.Properties(),"petit_thunder").addEffect(" Speed Up"));
    public static final RegistryObject<Item> CHIP_MEGA_BLASTER = ITEMS.register("chip_mega_blaster", () -> new SpMoveItem(new Item.Properties(),"mega_blaster").addEffect(" Speed Up"));
    public static final RegistryObject<Item> CHIP_THUNDERBOLT = ITEMS.register("chip_thunderbolt", () -> new SpMoveItem(new Item.Properties(),"thunderbolt").addEffect(" Speed Up"));
    public static final RegistryObject<Item> CHIP_GATLING_ARM = ITEMS.register("chip_gatling_arm", () -> new SpMoveItem(new Item.Properties(),"gatling_arm").setRepeat(5));
    public static final RegistryObject<Item> CHIP_DEADLY_STING = ITEMS.register("chip_deadly_sting", () -> new SpMoveItem(new Item.Properties(),"deadly_sting").addEffect(" Poisons"));
    public static final RegistryObject<Item> CHIP_TRON_FLAME = ITEMS.register("chip_tron_flame", () -> new SpMoveItem(new Item.Properties(),"tron_flame").addEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_DEATH_CLAW = ITEMS.register("chip_death_claw", () -> new SpMoveItem(new Item.Properties(),"death_claw"));
    public static final RegistryObject<Item> CHIP_POISON_BREATH = ITEMS.register("chip_poison_breath", () -> new SpMoveItem(new Item.Properties(),"poison_breath").addEffect(" Poisons"));
    public static final RegistryObject<Item> CHIP_HEAVENS_KNUCKLE = ITEMS.register("chip_heavens_knuckle", () -> new SpMoveItem(new Item.Properties(),"heavens_knuckle").setPhysical());
    public static final RegistryObject<Item> CHIP_HOLY_SHOOT = ITEMS.register("chip_holy_shoot", () -> new SpMoveItem(new Item.Properties(),"holy_shoot").addEffect(" Heals 10 Hp"));
    public static final RegistryObject<Item> CHIP_GLIDING_ROCKS = ITEMS.register("chip_gliding_rocks", () -> new SpMoveItem(new Item.Properties(),"gliding_rocks").setRepeat(3));
    public static final RegistryObject<Item> CHIP_POOP_THROW = ITEMS.register("chip_poop_throw", () -> new SpMoveItem(new Item.Properties(),"poop_throw"));
    public static final RegistryObject<Item> CHIP_SAND_BLAST = ITEMS.register("chip_sand_blast", () -> new SpMoveItem(new Item.Properties(),"sand_blast").setRepeat(8));
    public static final RegistryObject<Item> CHIP_BEAR_PUNCH = ITEMS.register("chip_bear_punch", () -> new SpMoveItem(new Item.Properties(),"bear_punch").setPhysical());
    public static final RegistryObject<Item> CHIP_PETIT_TWISTER = ITEMS.register("chip_petit_twister", () -> new SpMoveItem(new Item.Properties(),"petit_twister"));
    public static final RegistryObject<Item> CHIP_NIGHT_OF_FIRE = ITEMS.register("chip_night_of_fire", () -> new SpMoveItem(new Item.Properties(),"night_of_fire").addEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_DISC_ATTACK = ITEMS.register("chip_disc_attack", () -> new SpMoveItem(new Item.Properties(),"disc_attack").addEffect(" Speed Up"));
}
