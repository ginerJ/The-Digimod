package net.modderg.thedigimod.client.gui.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DigimonInventoryScreen extends AbstractContainerScreen<DigimonMenu> {

    private static final ResourceLocation TEXTURE_B = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/inventory_baby.png");
    private static final ResourceLocation TEXTURE_R = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/inventory_rookie.png");
    private static final ResourceLocation TEXTURE_C = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/inventory_champion.png");
    private static final ResourceLocation TEXTURE_U = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/inventory_ultimate.png");
    private static final ResourceLocation TEXTURE_T = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/inventory_transporter.png");

    public DigimonInventoryScreen(DigimonMenu container, Inventory inv, Component title) {
        super(container, inv, title);

        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        renderBackground(guiGraphics);
        final ResourceLocation TEXTURE;

        DigimonEntity digimon = menu.getDigimon();
        boolean transporter = digimon.profession != null && digimon.profession.equals("transporter");
        int stage = digimon.getEvoStage() + (transporter ? 1:0);

        if(stage == 0)
            TEXTURE = TEXTURE_B;
        else if(stage == 1)
            TEXTURE = TEXTURE_R;
        else if(stage == 2)
            TEXTURE = TEXTURE_C;
        else if(stage == 3)
            TEXTURE = TEXTURE_U;
        else
            TEXTURE = TEXTURE_T;

        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int mouseX, int mouseY, float pPartialTicks) {
        super.render(pGuiGraphics, mouseX, mouseY, pPartialTicks);
        renderTooltip(pGuiGraphics, mouseX, mouseY);
    }
}