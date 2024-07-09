package net.modderg.thedigimod.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.item.custom.DigiviceItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DigivicesItems {

    public static void register(IEventBus bus) {
        VICES.register(bus);
        init();
    }

    public static final DeferredRegister<Item> VICES = DeferredRegister.create(ForgeRegistries.ITEMS, TheDigiMod.MOD_ID);

    public static Map<String, RegistryObject<Item>> vicesMap;

    public static void init() {

        List<RegistryObject<Item>> itemList = VICES.getEntries().stream().toList();

        List<String> itemNameList = VICES.getEntries().stream().map(e -> e.getId().getPath()).toList();

        vicesMap = IntStream.range(0, itemNameList.size())
                .boxed()
                .collect(Collectors.toMap(itemNameList::get, itemList::get));
    }

    public static final RegistryObject<Item> VITALBRACELET = VICES.register("vitalbracelet", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DIGIVICE = VICES.register("digivice", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> VPET = VICES.register("vpet", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DIGIVICE_IC = VICES.register("digivice_ic", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DIGIVICE_BURST = VICES.register("digivice_burst", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DARC = VICES.register("darc", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> XROSSLOADER = VICES.register("xross_loader", () -> new DigiviceItem(new Item.Properties().stacksTo(1)));
}
