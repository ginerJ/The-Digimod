package net.modderg.thedigimod.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class DigitalCreativeTab {
    public static final CreativeModeTab DIGITAL_TAB = new CreativeModeTab("digitaltab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(DigiItems.VITALBRACELET.get());
        }
    };
}
