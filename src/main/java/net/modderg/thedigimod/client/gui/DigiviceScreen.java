package net.modderg.thedigimod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.sound.DigiSounds;
import org.jetbrains.annotations.NotNull;

import static net.modderg.thedigimod.client.gui.DigiviceScreenStats.renderAnalysisGui;
import static net.modderg.thedigimod.client.gui.DigiviceScreenCommands.renderCommands;

@OnlyIn(Dist.CLIENT)

public class DigiviceScreen extends Screen {

    public int size = 1;
    public boolean isClicking = false;

    //DO NOT DELETE BRO
    public void handleDelayedNarration() {}
    public void triggerImmediateNarration() {}

    Entity targetDigimon;

    public DigiviceScreen(Component p_96550_, Entity targetDigimon) {
        super(p_96550_);
        DigiviceScreenStats.buttonsRendered = false;
        this.targetDigimon = targetDigimon;
    }

    protected <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(@NotNull T p_169406_) {
        return super.addRenderableWidget(p_169406_);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        renderCommands(this, targetDigimon);
        super.init();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (this.getMinecraft() == null) return false;

        if (KeyBindings.NAVIGATING_KEY.matches(keyCode, scanCode)) {
            DigiviceScreenStats.switchGui();
            Minecraft.getInstance().player.playNotifySound(DigiSounds.R_KEY_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Minecraft minecraft = this.getMinecraft();
        if (minecraft == null) return;

        int height = minecraft.getWindow().getGuiScaledHeight();
        int width = minecraft.getWindow().getGuiScaledWidth();

        if (targetDigimon instanceof DigimonEntity cd)
            renderAnalysisGui(this, guiGraphics, width, height, cd);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }


}
