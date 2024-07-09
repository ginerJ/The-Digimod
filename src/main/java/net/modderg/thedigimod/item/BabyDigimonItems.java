package net.modderg.thedigimod.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.InitDigimons;
import net.modderg.thedigimod.item.custom.BabyDigimonItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BabyDigimonItems {

    public static void register(IEventBus bus) {
        BABIES.register(bus);
        init();
    }

    public static final DeferredRegister<Item> BABIES = DeferredRegister.create(ForgeRegistries.ITEMS, TheDigiMod.MOD_ID);

    public static Map<String, RegistryObject<Item>> babiesMap;

    public static void init() {

        List<RegistryObject<Item>> itemList = BABIES.getEntries().stream().toList();
        List<String> itemNameList = BABIES.getEntries().stream().map(e -> e.getId().getPath()).toList();

        babiesMap = IntStream.range(0, itemNameList.size())
                .boxed()
                .collect(Collectors.toMap(itemNameList::get, itemList::get));
    }

    public static final RegistryObject<Item> BOTAMON = BABIES.register("botamon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.KOROMON, "Koromon"));
    public static final RegistryObject<Item> CHICOMON = BABIES.register("chicomon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.CHIBIMON, "Chibimon"));
    public static final RegistryObject<Item> BUBBMON = BABIES.register("bubbmon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.MOCHIMON, "Mochimon"));
    public static final RegistryObject<Item> PUNIMON = BABIES.register("punimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.TSUNOMON, "Tsunomon"));
    public static final RegistryObject<Item> JYARIMON = BABIES.register("jyarimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.GIGIMON, "Gigimon"));
    public static final RegistryObject<Item> PETITMON = BABIES.register("petitmon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.BABYDMON, "Babydmon"));
    public static final RegistryObject<Item> PUYOMON = BABIES.register("puyomon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.PUYOYOMON, "Puyoyomon"));
    public static final RegistryObject<Item> DOKIMON = BABIES.register("dokimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.BIBIMON, "Bibimon"));
    public static final RegistryObject<Item> NYOKIMON = BABIES.register("nyokimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.YOKOMON, "Yokomon"));
    public static final RegistryObject<Item> CONOMON = BABIES.register("conomon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.CHOCOMON, "Kokomon"));
    public static final RegistryObject<Item> KIIMON = BABIES.register("kiimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.YARMON, "Yarmon"));
    public static final RegistryObject<Item> SUNAMON = BABIES.register("sunamon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.GOROMON, "Goromon"));
    public static final RegistryObject<Item> POYOMON = BABIES.register("poyomon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.TOKOMON, "Tokomon"));
    public static final RegistryObject<Item> DATIRIMON = BABIES.register("datirimon", () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant(), InitDigimons.CHAPMON, "Chapmon"));

}
