package net.modderg.thedigimod.gui.inventory;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import net.modderg.thedigimod.TheDigiMod;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class InitMenu {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, TheDigiMod.MOD_ID);

    public static final RegistryObject<MenuType<DigimonMenu>> DIGIMON_CONTAINER = MENUS.register("digimon_container",
            () -> IForgeMenuType.create(DigimonMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

}
