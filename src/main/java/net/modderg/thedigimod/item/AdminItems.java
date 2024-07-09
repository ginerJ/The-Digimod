package net.modderg.thedigimod.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.item.custom.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AdminItems {

    public static void register(IEventBus bus) {
        CREATIVE_ITEMS.register(bus);
        init();
    }

    public static final DeferredRegister<Item> CREATIVE_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheDigiMod.MOD_ID);

    public static Map<String, RegistryObject<Item>> adminmMap;

    public static void init() {

        List<RegistryObject<Item>> itemList = CREATIVE_ITEMS.getEntries().stream().toList();
        List<String> itemNameList = CREATIVE_ITEMS.getEntries().stream().map(e -> e.getId().getPath()).toList();

        adminmMap = IntStream.range(0, itemNameList.size())
                .boxed()
                .collect(Collectors.toMap(itemNameList::get, itemList::get));
    }

    //Exp Items
    public static final RegistryObject<Item> DRAGON_DATA = CREATIVE_ITEMS.register("dragon_data", () -> new CustomXpItem(new Item.Properties(), 0,1));
    public static final RegistryObject<Item> BEAST_DATA = CREATIVE_ITEMS.register("beast_data", () -> new CustomXpItem(new Item.Properties(), 1,1));
    public static final RegistryObject<Item> PLANTINSECT_DATA = CREATIVE_ITEMS.register("plantinsect_data", () -> new CustomXpItem(new Item.Properties(), 2,1));
    public static final RegistryObject<Item> AQUAN_DATA = CREATIVE_ITEMS.register("aquan_data", () -> new CustomXpItem(new Item.Properties(), 3,1));
    public static final RegistryObject<Item> WIND_DATA = CREATIVE_ITEMS.register("wind_data", () -> new CustomXpItem(new Item.Properties(), 4,1));
    public static final RegistryObject<Item> MACHINE_DATA = CREATIVE_ITEMS.register("machine_data", () -> new CustomXpItem(new Item.Properties(), 5,1));
    public static final RegistryObject<Item> EARTH_DATA = CREATIVE_ITEMS.register("earth_data", () -> new CustomXpItem(new Item.Properties(), 6,1));
    public static final RegistryObject<Item> NIGHTMARE_DATA = CREATIVE_ITEMS.register("nightmare_data", () -> new CustomXpItem(new Item.Properties(), 7,1));
    public static final RegistryObject<Item> HOLY_DATA = CREATIVE_ITEMS.register("holy_data", () -> new CustomXpItem(new Item.Properties(), 8,1));

    public static final RegistryObject<Item> DRAGON_PACK = CREATIVE_ITEMS.register("dragon_pack", () -> new CustomXpItem(new Item.Properties(), 0,9));
    public static final RegistryObject<Item> BEAST_PACK= CREATIVE_ITEMS.register("beast_pack", () -> new CustomXpItem(new Item.Properties(), 1,9));
    public static final RegistryObject<Item> PLANTINSECT_PACK = CREATIVE_ITEMS.register("plantinsect_pack", () -> new CustomXpItem(new Item.Properties(), 2,9));
    public static final RegistryObject<Item> AQUAN_PACK = CREATIVE_ITEMS.register("aquan_pack", () -> new CustomXpItem(new Item.Properties(), 3,9));
    public static final RegistryObject<Item> WIND_PACK = CREATIVE_ITEMS.register("wind_pack", () -> new CustomXpItem(new Item.Properties(), 4,9));
    public static final RegistryObject<Item> MACHINE_PACK = CREATIVE_ITEMS.register("machine_pack", () -> new CustomXpItem(new Item.Properties(), 5,9));
    public static final RegistryObject<Item> EARTH_PACK = CREATIVE_ITEMS.register("earth_pack", () -> new CustomXpItem(new Item.Properties(), 6,9));
    public static final RegistryObject<Item> NIGHTMARE_PACK = CREATIVE_ITEMS.register("nightmare_pack", () -> new CustomXpItem(new Item.Properties(), 7,9));
    public static final RegistryObject<Item> HOLY_PACK = CREATIVE_ITEMS.register("holy_pack", () -> new CustomXpItem(new Item.Properties(), 8,9));

    //admin stuff
    public static final RegistryObject<Item> ADMIN_LOGO = CREATIVE_ITEMS.register("admin_logo", () -> new DigimonItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ATTACK_GB = CREATIVE_ITEMS.register("gbattack", () -> new StatUpItem(new Item.Properties().stacksTo(1), "attack", CustomDigimon.MAXMEGASTAT));
    public static final RegistryObject<Item> DEFENCE_GB = CREATIVE_ITEMS.register("gbdefence", () -> new StatUpItem(new Item.Properties().stacksTo(1), "defence",CustomDigimon.MAXMEGASTAT));
    public static final RegistryObject<Item> SPATTACK_GB = CREATIVE_ITEMS.register("gbspattack", () -> new StatUpItem(new Item.Properties().stacksTo(1), "spattack",CustomDigimon.MAXMEGASTAT));
    public static final RegistryObject<Item> SPDEFENCE_GB = CREATIVE_ITEMS.register("gbspdefence", () -> new StatUpItem(new Item.Properties().stacksTo(1), "spdefence",CustomDigimon.MAXMEGASTAT));
    public static final RegistryObject<Item> HEALTH_DRIVES = CREATIVE_ITEMS.register("health_drives", () -> new StatUpItem(new Item.Properties().stacksTo(1), "health",CustomDigimon.MAXMEGASTAT));
    public static final RegistryObject<Item> BATTLE_CHIP = CREATIVE_ITEMS.register("battles_chip", () -> new StatUpItem(new Item.Properties().stacksTo(1), "battle",0));
    public static final RegistryObject<Item> GOBLIMON_BAT = CREATIVE_ITEMS.register("goblimon_bat", () -> new StatUpItem(new Item.Properties().stacksTo(1), "mistakes",0));
    public static final RegistryObject<Item> TAMER_LEASH = CREATIVE_ITEMS.register("tamer_leash", () -> new TameItem(new Item.Properties()));
    public static final RegistryObject<Item> BOSS_CUBE = CREATIVE_ITEMS.register("boss_cube", () -> new BossCubeItem(new Item.Properties()));
}
