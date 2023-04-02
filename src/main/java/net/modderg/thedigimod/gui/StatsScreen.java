package net.modderg.thedigimod.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class StatsScreen extends Screen {
    public StatsScreen(Component p_96550_) {
        super(p_96550_);
    }

    @Override
    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        drawCenteredString(p_96562_, this.font, "caca", this.width / 2, 120, 10526880);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
    }

    @Override
    public void onClose() {
        super.onClose();
    }
}
