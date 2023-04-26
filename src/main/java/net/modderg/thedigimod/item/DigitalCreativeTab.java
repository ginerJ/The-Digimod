package net.modderg.thedigimod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.modderg.thedigimod.TheDigiMod;

@Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DigitalCreativeTab {
    public static CreativeModeTab DIGITAL_TAB;
    public static CreativeModeTab ADMIN_TAB;

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event){
        DIGITAL_TAB = event.registerCreativeModeTab(new ResourceLocation(TheDigiMod.MOD_ID, "digital_tab"),
                builder -> builder.m_257737_(() -> new ItemStack(DigiItems.VITALBRACELET.get())).m_257941_(Component.literal("Digital Tab")));
        ADMIN_TAB = event.registerCreativeModeTab(new ResourceLocation(TheDigiMod.MOD_ID, "digiadmin_tab"),
                builder -> builder.m_257737_(() -> new ItemStack(DigiItems.ADMIN_LOGO.get())).m_257941_(Component.literal("Admin Tab")));
    }
}
