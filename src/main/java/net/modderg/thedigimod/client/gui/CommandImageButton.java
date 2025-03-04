package net.modderg.thedigimod.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CommandImageButton extends ImageButton {

    boolean beingPressed = false;

    public CommandImageButton(int x, int y, int width, int height, int u, int v, int hoverUOffset, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress) {
        super(x, y, width, height, u, v, hoverUOffset, texture, textureWidth, textureHeight, onPress);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        PoseStack poseStack = guiGraphics.pose();

        if(beingPressed)
            renderButtonScaled(poseStack, 0.9f, guiGraphics, mouseX, mouseY, delta);
        else if (this.isHovered())
            renderButtonScaled(poseStack, 1.1f, guiGraphics, mouseX, mouseY, delta);
        else
            super.render(guiGraphics, mouseX, mouseY, delta);
    }

    void renderButtonScaled(PoseStack poseStack, float scale, @NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta){
        poseStack.pushPose();
        poseStack.translate(this.getX() + this.width / 2, this.getY() + this.height / 2, 0);
        poseStack.scale(scale, scale, 1.0f);
        poseStack.translate(-(this.getX() + this.width / 2), -(this.getY() + this.height / 2), 0);
        super.render(guiGraphics, mouseX, mouseY, delta);
        poseStack.popPose();
    }

    @Override
    public void onClick(double p_93371_, double p_93372_) {
        beingPressed = true;
        super.onClick(p_93371_, p_93372_);
    }

    @Override
    public void onRelease(double p_93669_, double p_93670_) {
        beingPressed = false;
        super.onRelease(p_93669_, p_93670_);
    }
}
