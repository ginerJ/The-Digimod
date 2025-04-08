package net.modderg.thedigimod.server.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.item.custom.DigiviceItem;

import java.util.List;

public class TDItemsDigivices {

    public static void register(IEventBus bus) {
        DIGIVICES.register(bus);
        init();
    }

    public static final DeferredRegister<Item> DIGIVICES = DeferredRegister.create(ForgeRegistries.ITEMS, TheDigiMod.MOD_ID);

    public static List<RegistryObject<DigiviceItem>> vicesMap;

    private static void init() {
        vicesMap = List.of(
                VPET,
                DIGIVICE01,
                DIGIVICE,
                D3,
                DARC,
                DSCANNER,
                DIGIVICE_IC,
                DIGIVICE_BURST,
                XROSSLOADER,
                DIGIVICE2020,
                VITALBRACELET,
                DSTORAGE
        );
    }

    public static final RegistryObject<DigiviceItem> DIGIVICE01 = DIGIVICES.register("digivice01", () -> new DigiviceItem(new Item.Properties().stacksTo(1), 0x7AB9F4));
    public static final RegistryObject<DigiviceItem> VITALBRACELET = DIGIVICES.register("vitalbracelet", () -> new DigiviceItem(new Item.Properties().stacksTo(1), 0x7AB9F4));
    public static final RegistryObject<DigiviceItem> DIGIVICE = DIGIVICES.register("digivice", () -> new DigiviceItem(new Item.Properties().stacksTo(1), 0XCDF8FF));
    public static final RegistryObject<DigiviceItem> DIGIVICE2020 = DIGIVICES.register("digivice2020", () -> new DigiviceItem(new Item.Properties().stacksTo(1), 0x7AB9F4));
    public static final RegistryObject<DigiviceItem> VPET = DIGIVICES.register("vpet", () -> new DigiviceItem(new Item.Properties().stacksTo(1), 0XFD6363));
    public static final RegistryObject<DigiviceItem> DIGIVICE_IC = DIGIVICES.register("digivice_ic", () -> new DigiviceItem(new Item.Properties().stacksTo(1), 0XFFA65D));
    public static final RegistryObject<DigiviceItem> DIGIVICE_BURST = DIGIVICES.register("digivice_burst", () -> new DigiviceItem(new Item.Properties().stacksTo(1), 0XFFA65D));
    public static final RegistryObject<DigiviceItem> DARC = DIGIVICES.register("darc", () -> new DigiviceItem(new Item.Properties().stacksTo(1), 0XFF6E64));
    public static final RegistryObject<DigiviceItem> XROSSLOADER = DIGIVICES.register("xross_loader", () -> new DigiviceItem(new Item.Properties().stacksTo(1), 0xFF4C4C));
    public static final RegistryObject<DigiviceItem> D3 = DIGIVICES.register("d3", () -> new DigiviceItem(new Item.Properties().stacksTo(1), 0X12B3D8));
    public static final RegistryObject<DigiviceItem> DSCANNER = DIGIVICES.register("dscanner", () -> new DigiviceItem(new Item.Properties().stacksTo(1), 0xFF4C4C));
    public static final RegistryObject<DigiviceItem> DSTORAGE = DIGIVICES.register("dstorage", () -> new DigiviceItem(new Item.Properties().stacksTo(1), 0XCDF8FF));
}
