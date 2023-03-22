package net.modderg.thedigimod.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.DigitalEntities;
import net.modderg.thedigimod.particles.DigitalParticles;

public class DigiItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheDigiMod.MOD_ID);

    //Digivices
    public static final RegistryObject<Item> VITALBRACELET = ITEMS.register("vitalbracelet", () -> new Item(new Item.Properties().stacksTo(1)
            .tab(DigitalCreativeTab.DIGITAL_TAB)));
    //public static final RegistryObject<Item> DIGIVICE = ITEMS.register("digivice", () -> new Item(new Item.Properties().stacksTo(1)
            //.tab(DigitalCreativeTab.DIGITAL_TAB)));

    //Vpets
    public static final RegistryObject<Item> VPET = ITEMS.register("vpet", () -> new Item(new Item.Properties().stacksTo(1)
            .tab(DigitalCreativeTab.DIGITAL_TAB)));
    //Dims
    public static final RegistryObject<Item> BOTAMOND = ITEMS.register("botamond", () -> new CustomDimItem(new Item.Properties().stacksTo(16)
            .tab(DigitalCreativeTab.DIGITAL_TAB), DigitalEntities.KOROMONB.get(), "Black Agumon"));
    public static final RegistryObject<Item> BOTAMON = ITEMS.register("botamon", () -> new CustomDimItem(new Item.Properties().stacksTo(16)
            .tab(DigitalCreativeTab.DIGITAL_TAB), DigitalEntities.KOROMON.get(), "Agumon"));
    public static final RegistryObject<Item> BUBBMON = ITEMS.register("bubbmon", () -> new CustomDimItem(new Item.Properties().stacksTo(16)
            .tab(DigitalCreativeTab.DIGITAL_TAB), DigitalEntities.MOCHIMON.get(), "Tentomon"));
    public static final RegistryObject<Item> PUNIMON = ITEMS.register("punimon", () -> new CustomDimItem(new Item.Properties().stacksTo(16)
            .tab(DigitalCreativeTab.DIGITAL_TAB), DigitalEntities.TSUNOMON.get(), "Bearmon"));
    public static final RegistryObject<Item> BUBBMONK = ITEMS.register("bubbmonk", () -> new CustomDimItem(new Item.Properties().stacksTo(16)
            .tab(DigitalCreativeTab.DIGITAL_TAB), DigitalEntities.MOCHIMONK.get(), "Kunemon"));
    public static final RegistryObject<Item> JYARIMON = ITEMS.register("jyarimon", () -> new CustomDimItem(new Item.Properties().stacksTo(16)
            .tab(DigitalCreativeTab.DIGITAL_TAB), DigitalEntities.GIGIMON.get(), "Guilmon"));
    public static final RegistryObject<Item> PETITMON = ITEMS.register("petitmon", () -> new CustomDimItem(new Item.Properties().stacksTo(16)
            .tab(DigitalCreativeTab.DIGITAL_TAB), DigitalEntities.BABYDMON.get(), "Dracomon"));
    public static final RegistryObject<Item> PUYOMON = ITEMS.register("puyomon", () -> new CustomDimItem(new Item.Properties().stacksTo(16)
            .tab(DigitalCreativeTab.DIGITAL_TAB), DigitalEntities.PUYOYOMON.get(), "Jellymon"));
    public static final RegistryObject<Item> DOKIMON = ITEMS.register("dokimon", () -> new CustomDimItem(new Item.Properties().stacksTo(16)
            .tab(DigitalCreativeTab.DIGITAL_TAB), DigitalEntities.BIBIMON.get(), "Pulsemon"));
    public static final RegistryObject<Item> NYOKIMON = ITEMS.register("nyokimon", () -> new CustomDimItem(new Item.Properties().stacksTo(16)
            .tab(DigitalCreativeTab.DIGITAL_TAB), DigitalEntities.YOKOMON.get(), "Biyomon"));

    //Exp Items
    public static final RegistryObject<Item> DRAGON_DATA = ITEMS.register("dragon_data", () -> new CustomXpItem(new Item.Properties().stacksTo(64)
            .tab(DigitalCreativeTab.DIGITAL_TAB), 0));
    public static final RegistryObject<Item> BEAST_DATA = ITEMS.register("beast_data", () -> new CustomXpItem(new Item.Properties().stacksTo(64)
            .tab(DigitalCreativeTab.DIGITAL_TAB), 1));
    public static final RegistryObject<Item> PLANTINSECT_DATA = ITEMS.register("plantinsect_data", () -> new CustomXpItem(new Item.Properties().stacksTo(64)
            .tab(DigitalCreativeTab.DIGITAL_TAB), 2));
    public static final RegistryObject<Item> AQUAN_DATA = ITEMS.register("aquan_data", () -> new CustomXpItem(new Item.Properties().stacksTo(64)
            .tab(DigitalCreativeTab.DIGITAL_TAB), 3));
    public static final RegistryObject<Item> WIND_DATA = ITEMS.register("wind_data", () -> new CustomXpItem(new Item.Properties().stacksTo(64)
            .tab(DigitalCreativeTab.DIGITAL_TAB), 4));
    public static final RegistryObject<Item> MACHINE_DATA = ITEMS.register("machine_data", () -> new CustomXpItem(new Item.Properties().stacksTo(64)
            .tab(DigitalCreativeTab.DIGITAL_TAB), 5));
    public static final RegistryObject<Item> EARTH_DATA = ITEMS.register("earth_data", () -> new CustomXpItem(new Item.Properties().stacksTo(64)
            .tab(DigitalCreativeTab.DIGITAL_TAB), 6));
    public static final RegistryObject<Item> NIGHTMARE_DATA = ITEMS.register("nightmare_data", () -> new CustomXpItem(new Item.Properties().stacksTo(64)
            .tab(DigitalCreativeTab.DIGITAL_TAB), 7));
    public static final RegistryObject<Item> HOLY_DATA = ITEMS.register("holy_data", () -> new CustomXpItem(new Item.Properties().stacksTo(64)
            .tab(DigitalCreativeTab.DIGITAL_TAB), 8));

    //Food Items
    public static final RegistryObject<Item> DIGI_MEAT = ITEMS.register("digi_meat", () -> new Item(new Item.Properties().stacksTo(64)
            .tab(DigitalCreativeTab.DIGITAL_TAB)));

    //Misc Items
    public static final RegistryObject<Item> BLACK_DIGITRON = ITEMS.register("black_digitron", () -> new EvolutionTriggerItem(new Item.Properties().stacksTo(64)
            .tab(DigitalCreativeTab.DIGITAL_TAB), DigitalParticles.DIGITRON_PARTICLES));

}
