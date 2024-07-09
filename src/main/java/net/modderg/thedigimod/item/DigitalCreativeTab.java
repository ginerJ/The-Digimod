package net.modderg.thedigimod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;

@Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DigitalCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheDigiMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> DIGITAL_TAB = CREATIVE_TABS.register("digital_tab", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(DigivicesItems.VPET.get())).title(Component.literal("Digital Tab")).build());

    public static final RegistryObject<CreativeModeTab> ADMIN_TAB = CREATIVE_TABS.register("digiadmin_tab", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(AdminItems.ADMIN_LOGO.get())).title(Component.literal("Admin Tab")).build());

}
