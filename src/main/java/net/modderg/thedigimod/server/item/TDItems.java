package net.modderg.thedigimod.server.item;

import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.block.TDBlocks;
import net.modderg.thedigimod.server.goods.InitGoods;
import net.modderg.thedigimod.client.particles.DigitalParticles;
import net.modderg.thedigimod.server.item.custom.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TDItems {

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        init();
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheDigiMod.MOD_ID);

    public static Map<String, RegistryObject<Item>> itemMap;

    public static void init() {

        List<RegistryObject<Item>> itemList = ITEMS.getEntries().stream().toList();
        List<String> itemNameList = ITEMS.getEntries().stream().map(e -> e.getId().getPath()).toList();

        itemMap = IntStream.range(0, itemNameList.size())
                .boxed()
                .collect(Collectors.toMap(itemNameList::get, itemList::get));
    }

    //Ores
    public static final RegistryObject<Item> HUANGLONG_ORE = ITEMS.register("huanglong_ore", () -> new DigimonItem(new Item.Properties()));
    public static final RegistryObject<Item> CHRONDIGIZOIT = ITEMS.register("chrondigizoit", () -> new DigimonItem(new Item.Properties()));
    public static final RegistryObject<Item> CHROME_DIGIZOID = ITEMS.register("chrome_digizoid", () -> new DigimonItem(new Item.Properties()));


    public static final RegistryObject<Item> DIGIMON_CARD = ITEMS.register("digimon_card", () -> new DigimonItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> DIGIMON_BLUE_CARD = ITEMS.register("blue_card", () -> new DigimonItem(new Item.Properties().stacksTo(16)));

    //Food(? Items
    public static final RegistryObject<Item> DIGI_MEAT = ITEMS.register("digi_meat", () -> new DigitalItemNameBlockItem(TDBlocks.MEAT_CROP.get(), new Item.Properties().food(Foods.POTATO)));
    public static final RegistryObject<Item> DIGI_MEAT_ROTTEN = ITEMS.register("digi_meat_rotten", () -> new Item(new Item.Properties().food(Foods.POTATO)));
    public static final RegistryObject<Item> DIGI_MEAT_BIG = ITEMS.register("digi_meat_big", () -> new DigitalItemNameBlockItem(TDBlocks.MEAT_CROP.get(), new Item.Properties().food(Foods.BAKED_POTATO)));
    public static final RegistryObject<Item> DIGI_RIBS = ITEMS.register("digi_ribs", () -> new Item(new Item.Properties().food(Foods.COOKED_BEEF)));

    public static final RegistryObject<Item> DIGI_SUSHI = ITEMS.register("digi_sushi", () -> new Item(new Item.Properties().food(Foods.COOKED_SALMON)));
    public static final RegistryObject<Item> GUILMON_BREAD = ITEMS.register("guilmon_bread", () -> new Item(new Item.Properties().food(Foods.BREAD)));
    public static final RegistryObject<Item> DIGI_CAKE = ITEMS.register("digi_cake", () -> new Item(new Item.Properties().food(Foods.BREAD)));

    public static final RegistryObject<Item> POOP = ITEMS.register("poop", () -> new Item(new Item.Properties().food(Foods.SPIDER_EYE)));
    public static final RegistryObject<Item> GOLD_POOP = ITEMS.register("gold_poop", () -> new Item(new Item.Properties().food(Foods.SPIDER_EYE)));

    //Misc Items
    public static final RegistryObject<Item> DIGI_MEMORY = ITEMS.register("digi_memory", () -> new DigiMemory(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLACK_DIGITRON = ITEMS.register("black_digitron", () -> new DigitronItem(new Item.Properties(), DigitalParticles.DIGITRON_PARTICLES));
    public static final RegistryObject<Item> DIGI_CORE = ITEMS.register("digi_core", () -> new StatUpItem(new Item.Properties().stacksTo(1), "lifes",0));
    public static final RegistryObject<Item> DARK_TOWER_SHARD = ITEMS.register("dark_tower_shard", () -> new DarkTowerShardItem(new Item.Properties()));

    //Stat Stuff
    public static final RegistryObject<Item> TRAINING_BAG = ITEMS.register("training_bag", () -> new ContainerItem(new Item.Properties(), new RegistryObject[]{TDItems.TABLE_ITEM, TDItems.BAG_ITEM, TDItems.SHIELD_ITEM, TDItems.TARGET_ITEM, TDItems.UPDATE_ITEM}));
    public static final RegistryObject<Item> ATTACK_BYTE = ITEMS.register("byteattack", () -> new StatUpItem(new Item.Properties(), "attack", 5));
    public static final RegistryObject<Item> DEFENSE_BYTE = ITEMS.register("bytedefense", () -> new StatUpItem(new Item.Properties(), "defence",5));
    public static final RegistryObject<Item> SPATTACK_BYTE = ITEMS.register("bytespattack", () -> new StatUpItem(new Item.Properties(), "spattack",5));
    public static final RegistryObject<Item> SPDEFENSE_BYTE = ITEMS.register("bytespdefense", () -> new StatUpItem(new Item.Properties(), "spdefence",5));
    public static final RegistryObject<Item> HEALTH_BYTE = ITEMS.register("bytehealth", () -> new StatUpItem(new Item.Properties(), "health",5));

    //training good items
    public static final RegistryObject<Item> BAG_ITEM = ITEMS.register("bag_item", () -> new SpawnGoodItem(InitGoods.PUNCHING_BAG,new Item.Properties().stacksTo(1), "attack"));
    public static final RegistryObject<Item> TABLE_ITEM = ITEMS.register("table_item", () -> new SpawnGoodItem(InitGoods.SP_TABLE,new Item.Properties().stacksTo(1), "sp.defense"));
    public static final RegistryObject<Item> TARGET_ITEM = ITEMS.register("target_item", () -> new SpawnGoodItem(InitGoods.SP_TARGET,new Item.Properties().stacksTo(1), "sp.attack"));
    public static final RegistryObject<Item> SHIELD_ITEM = ITEMS.register("shield_item", () -> new SpawnGoodItem(InitGoods.SHIELD_STAND,new Item.Properties().stacksTo(1), "defense"));
    public static final RegistryObject<Item> UPDATE_ITEM = ITEMS.register("update_item", () -> new SpawnGoodItem(InitGoods.UPDATE_GOOD,new Item.Properties().stacksTo(1), "health"));

    public static final RegistryObject<Item> DRAGON_BONE_ITEM = ITEMS.register("dragon_bone", () -> new SpawnGoodItem(InitGoods.DRAGON_BONE,new Item.Properties().stacksTo(1), "attack"));
    public static final RegistryObject<Item> BALL_GOOD_ITEM = ITEMS.register("ball_good", () -> new SpawnGoodItem(InitGoods.BALL_GOOD,new Item.Properties().stacksTo(1), "attack"));
    public static final RegistryObject<Item> CLOWN_BOX = ITEMS.register("clown_box", () -> new SpawnGoodItem(InitGoods.CLOWN_BOX,new Item.Properties().stacksTo(1), "sp.attack"));
    public static final RegistryObject<Item> FLYTRAP_GOOD = ITEMS.register("flytrap_good", () -> new SpawnGoodItem(InitGoods.FLYTRAP_GOOD,new Item.Properties().stacksTo(1), "sp.defense"));
    public static final RegistryObject<Item> OLD_PC_GOOD = ITEMS.register("old_pc", () -> new SpawnGoodItem(InitGoods.OLD_PC_GOOD,new Item.Properties().stacksTo(1), "defense"));
    public static final RegistryObject<Item> LIRA_GOOD = ITEMS.register("lira_good", () -> new SpawnGoodItem(InitGoods.LIRA_GOOD,new Item.Properties().stacksTo(1), "sp.defense"));
    public static final RegistryObject<Item> RED_FREEZER = ITEMS.register("red_freezer", () -> new SpawnGoodItem(InitGoods.RED_FREEZER,new Item.Properties().stacksTo(1), "sp.attack"));
    public static final RegistryObject<Item> WIND_VANE = ITEMS.register("wind_vane", () -> new SpawnGoodItem(InitGoods.WIND_VANE,new Item.Properties().stacksTo(1), "sp.attack"));
    public static final RegistryObject<Item> TRAINING_ROCK = ITEMS.register("training_rock", () -> new SpawnGoodItem(InitGoods.TRAINING_ROCK,new Item.Properties().stacksTo(1), "defense"));
    public static final RegistryObject<Item> M2_HEALTH_DISK = ITEMS.register("m2_disk_item", () -> new SpawnGoodItem(InitGoods.M2_DISK_GOOD,new Item.Properties().stacksTo(1), "health"));

    //move items
    public static final RegistryObject<Item> CHIP_BULLET = ITEMS.register("chip_bullet", () -> new SpMoveItem(new Item.Properties(), "bullet"));
    public static final RegistryObject<Item> CHIP_PEPPER_BREATH = ITEMS.register("chip_pepper_breath", () -> new SpMoveItem(new Item.Properties(), "pepper_breath").addDescriptionEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_MEGA_FLAME = ITEMS.register("chip_mega_flame", () -> new SpMoveItem(new Item.Properties(), "mega_flame").addDescriptionEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_V_ARROW = ITEMS.register("chip_v_arrow", () -> new SpMoveItem(new Item.Properties(), "v_arrow"));
    public static final RegistryObject<Item> CHIP_GOLD_ARROW = ITEMS.register("chip_gold_arrow", () -> new SpMoveItem(new Item.Properties(), "gold_arrow").addDescriptionEffect(" Golwing Effect"));
    public static final RegistryObject<Item> CHIP_HYPER_HEAT = ITEMS.register("chip_hyper_heat", () -> new SpMoveItem(new Item.Properties(), "hyper_heat").addDescriptionEffect(" Sure Hit").addDescriptionEffect(" -20% damage").addDescriptionEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_METEOR_WING = ITEMS.register("chip_meteor_wing", () -> new SpMoveItem(new Item.Properties(), "meteor_wing").addDescriptionEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_BEAST_SLASH = ITEMS.register("chip_beast_slash", () -> new SpMoveItem(new Item.Properties(), "beast_slash").setPhysical());
    public static final RegistryObject<Item> CHIP_INK_GUN = ITEMS.register("chip_ink_gun", () -> new SpMoveItem(new Item.Properties(), "ink_gun").addDescriptionEffect(" Poisons Target").addDescriptionEffect(" Blinds Target"));
    public static final RegistryObject<Item> CHIP_SNOW_BULLET = ITEMS.register("chip_snow_bullet", () -> new SpMoveItem(new Item.Properties(), "snow_bullet").setRepeat(4));
    public static final RegistryObject<Item> CHIP_OCEAN_STORM = ITEMS.register("chip_ocean_storm", () -> new SpMoveItem(new Item.Properties(), "ocean_storm").setRepeat(2));
    public static final RegistryObject<Item> CHIP_PETIT_THUNDER = ITEMS.register("chip_petit_thunder", () -> new SpMoveItem(new Item.Properties(), "petit_thunder").addDescriptionEffect(" User Speed Up"));
    public static final RegistryObject<Item> CHIP_MEGA_BLASTER = ITEMS.register("chip_mega_blaster", () -> new SpMoveItem(new Item.Properties(), "mega_blaster").addDescriptionEffect(" Sure Hit").addDescriptionEffect(" -20% damage").addDescriptionEffect(" User Speed Up"));
    public static final RegistryObject<Item> CHIP_THUNDERBOLT = ITEMS.register("chip_thunderbolt", () -> new SpMoveItem(new Item.Properties(), "thunderbolt").addDescriptionEffect(" User Speed Up"));
    public static final RegistryObject<Item> CHIP_GATLING_ARM = ITEMS.register("chip_gatling_arm", () -> new SpMoveItem(new Item.Properties(), "gatling_arm").setRepeat(5));
    public static final RegistryObject<Item> CHIP_DEADLY_STING = ITEMS.register("chip_deadly_sting", () -> new SpMoveItem(new Item.Properties(), "deadly_sting").addDescriptionEffect(" Poisons Target"));
    public static final RegistryObject<Item> CHIP_TRON_FLAME = ITEMS.register("chip_tron_flame", () -> new SpMoveItem(new Item.Properties(), "tron_flame").addDescriptionEffect(" Burns Target").addDescriptionEffect(" Extra Dmg To Digitron Counterpart"));
    public static final RegistryObject<Item> CHIP_DEATH_CLAW = ITEMS.register("chip_death_claw", () -> new SpMoveItem(new Item.Properties(), "death_claw"));
    public static final RegistryObject<Item> CHIP_POISON_BREATH = ITEMS.register("chip_poison_breath", () -> new SpMoveItem(new Item.Properties(), "poison_breath").addDescriptionEffect("").addDescriptionEffect(" Poisons Target").addDescriptionEffect(" Extra Dmg To Digitron Counterpart"));
    public static final RegistryObject<Item> CHIP_HEAVENS_KNUCKLE = ITEMS.register("chip_heavens_knuckle", () -> new SpMoveItem(new Item.Properties(), "heavens_knuckle").setPhysical());
    public static final RegistryObject<Item> CHIP_HOLY_SHOOT = ITEMS.register("chip_holy_shoot", () -> new SpMoveItem(new Item.Properties(), "holy_shoot").addDescriptionEffect(" Heals 10 Hp"));
    public static final RegistryObject<Item> CHIP_GLIDING_ROCKS = ITEMS.register("chip_gliding_rocks", () -> new SpMoveItem(new Item.Properties(), "gliding_rocks").setRepeat(3));
    public static final RegistryObject<Item> CHIP_POOP_THROW = ITEMS.register("chip_poop_throw", () -> new SpMoveItem(new Item.Properties(), "poop_throw").addDescriptionEffect(" Dirties Target"));
    public static final RegistryObject<Item> CHIP_SAND_BLAST = ITEMS.register("chip_sand_blast", () -> new SpMoveItem(new Item.Properties(), "sand_blast").addDescriptionEffect("").setRepeat(4));
    public static final RegistryObject<Item> CHIP_BEAR_PUNCH = ITEMS.register("chip_bear_punch", () -> new SpMoveItem(new Item.Properties(), "bear_punch").setPhysical());
    public static final RegistryObject<Item> CHIP_PETIT_TWISTER = ITEMS.register("chip_petit_twister", () -> new SpMoveItem(new Item.Properties(), "petit_twister").addDescriptionEffect(" High Up Knockback"));
    public static final RegistryObject<Item> CHIP_NIGHT_OF_FIRE = ITEMS.register("chip_night_of_fire", () -> new SpMoveItem(new Item.Properties(), "night_of_fire").addDescriptionEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_DISC_ATTACK = ITEMS.register("chip_disc_attack", () -> new SpMoveItem(new Item.Properties(), "disc_attack").addDescriptionEffect(" User Speed Up").setRepeat(3));
    public static final RegistryObject<Item> CHIP_SMILEY_BOMB = ITEMS.register("chip_smiley_bomb", () -> new SpMoveItem(new Item.Properties(), "smiley_bomb").addDescriptionEffect(" Explosion after some secs."));
    public static final RegistryObject<Item> CHIP_GIGA_DESTROYER = ITEMS.register("chip_giga_destroyer", () -> new SpMoveItem(new Item.Properties(), "giga_destroyer").setRepeat(2).addDescriptionEffect(" On Hit Explosion"));
    public static final RegistryObject<Item> CHIP_DOCTASE = ITEMS.register("chip_doctase", () -> new SpMoveItem(new Item.Properties(), "doctase").addDescriptionEffect(" 6 Block Range").addDescriptionEffect(" Poisons Target").addDescriptionEffect(" Heals 10 Hp"));
    public static final RegistryObject<Item> CHIP_MAGMA_SPIT = ITEMS.register("chip_magma_spit", () -> new SpMoveItem(new Item.Properties(), "magma_spit").addDescriptionEffect(" Burns Target").addDescriptionEffect(" 7 Block Range"));
    public static final RegistryObject<Item> CHIP_CRYSTAL_RAIN = ITEMS.register("chip_crystal_rain", () -> new SpMoveItem(new Item.Properties(), "crystal_rain"));
    public static final RegistryObject<Item> CHIP_LOVE_SONG = ITEMS.register("chip_love_song", () -> new SpMoveItem(new Item.Properties(), "love_song").addDescriptionEffect(" 6 Block Range").addDescriptionEffect(" Slows Target Movement"));
    public static final RegistryObject<Item> CHIP_MACH_TORNADO = ITEMS.register("chip_mach_tornado", () -> new SpMoveItem(new Item.Properties(), "mach_tornado").addDescriptionEffect(" Sure Hit").addDescriptionEffect(" -20% damage").addDescriptionEffect(" High Up Knockback").addDescriptionEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_DIVINE_AXE = ITEMS.register("chip_divine_axe", () -> new SpMoveItem(new Item.Properties(), "divine_axe").addDescriptionEffect(" Heals 15 Hp"));
    public static final RegistryObject<Item> CHIP_CRUEL_SISSORS = ITEMS.register("chip_cruel_sissors", () -> new SpMoveItem(new Item.Properties(), "cruel_sissors").addDescriptionEffect(" Sure Hit").addDescriptionEffect(" -20% damage").addDescriptionEffect(" High Up Knockback").addDescriptionEffect(" Withers Target"));
    public static final RegistryObject<Item> CHIP_X_ATTACK = ITEMS.register("chip_x_attack", () -> new SpMoveItem(new Item.Properties(), "x_attack").addDescriptionEffect(" Burns Target"));
    public static final RegistryObject<Item> CHIP_WORLD_SLASH = ITEMS.register("chip_world_slash", () -> new SpMoveItem(new Item.Properties(), "world_slash").setPhysical());
    public static final RegistryObject<Item> CHIP_SECRET_IDENTITY = ITEMS.register("chip_secret_identity", () -> new SpMoveItem(new Item.Properties(), "secret_identity").addDescriptionEffect(" Blinds Target"));
}
