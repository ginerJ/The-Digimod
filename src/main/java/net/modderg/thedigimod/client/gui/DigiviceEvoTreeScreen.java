package net.modderg.thedigimod.client.gui;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.client.packet.CToSGetTreeStringsPacket;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.packet.PacketInit;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.IntStream;

import static net.modderg.thedigimod.TheDigiMod.MAX_DIGIMON_STAGE;

@OnlyIn(Dist.CLIENT)
public class DigiviceEvoTreeScreen extends Screen {

    //DO NOT DELETE BRO
    public void handleDelayedNarration() {}
    public void triggerImmediateNarration() {}
    @Override public boolean isPauseScreen() {
        return false;
    }

    public String treeString;
    public DigimonEntity targetDigimon;
    int guiOffset = 0;
    int treeHeight = 0;

    public DigiviceEvoTreeScreen(Component p_96550_, DigimonEntity targetDigimon) {
        super(p_96550_);
        this.targetDigimon = targetDigimon;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Minecraft minecraft = this.getMinecraft();
        if (minecraft == null) return;

        if (targetDigimon == null){minecraft.screen = null; return;}

        renderEvoTree(guiGraphics, mouseX, mouseY);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    static final int scrollSpeed = 10;

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_96554_) {
        if (keyCode == InputConstants.KEY_UP || keyCode == InputConstants.KEY_W)
            guiOffset = (int) Math.max(height/1.75d - treeHeight, guiOffset - scrollSpeed);
        if (keyCode == InputConstants.KEY_DOWN|| keyCode == InputConstants.KEY_S)
            guiOffset = Math.min(0, guiOffset + scrollSpeed);

        return super.keyPressed(keyCode, scanCode, p_96554_);
    }

    @Override
    public boolean mouseScrolled(double p_94686_, double p_94687_, double delta) {
        if (delta < 0)
            guiOffset = (int) Math.max(height/1.75d - treeHeight, guiOffset - scrollSpeed);
        else if (delta > 0)
            guiOffset = Math.min(0, guiOffset + scrollSpeed);


        return super.mouseScrolled(p_94686_, p_94687_, delta);
    }

    static final ResourceLocation LINE_GRAPH = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/line_graph.png");

    public ResourceLocation getSpeciesLocation(String species) {
        return new ResourceLocation(TheDigiMod.MOD_ID,"textures/icons/" + species + ".png");
    }

    public void renderEvoTree(GuiGraphics graphics, int mouseX, int mouseY) {

        String[] preEvos = targetDigimon.getPreEvos();

        if(treeString == null)
            PacketInit.sendToServer(new CToSGetTreeStringsPacket(preEvos.length == 0? targetDigimon.getLowerCaseSpecies() : preEvos[0], this.getMinecraft().player.getStringUUID()));

        if(treeString == null)
            return;

        JsonObject json = JsonParser.parseString(treeString).getAsJsonObject();

        int treeWidth = 22 + 44 * (preEvos.length + MAX_DIGIMON_STAGE - targetDigimon.getEvoStage());

        int x = (width - treeWidth)/2, y = height/4 + guiOffset;

        String babyName = targetDigimon.isBaby1() ? targetDigimon.getLowerCaseSpecies() : null;

        if(treeHeight == 0)
            treeHeight = buildTreeOverlay(graphics, preEvos.length ==0 ? targetDigimon.getLowerCaseSpecies():preEvos[0], json, x, y, mouseX, mouseY, babyName);

        renderBackGround(graphics, treeHeight, treeWidth, x, y);

        buildTreeOverlay(graphics, preEvos.length ==0 ? targetDigimon.getLowerCaseSpecies():preEvos[0], json, x, y, mouseX, mouseY, babyName);
    }

    public int buildTreeOverlay(GuiGraphics graphics, String name, JsonObject json, int x, int y, int mouseX, int mouseY, String babyName){

        JsonObject jsonObj = json.getAsJsonObject(name);
        Set<String> entries = jsonObj.keySet();
        int treeHeight = 0;
        int oldAdd = 0;

        int count = 1;
        for (String evo: entries){

            if(oldAdd > 0)
                for(int i = 1; i < oldAdd/22; i++)
                    renderGuiBlockAt(graphics, x + 22, y + treeHeight - 22 * i, 110, 44);

            oldAdd = buildTreeOverlay(graphics, evo, jsonObj, x + 44, y + treeHeight, mouseX, mouseY, babyName);
            if(count == entries.size())
                renderGuiBlockAt(graphics, x + 22, y + treeHeight, 88, 44);
            else
                renderGuiBlockAt(graphics, x + 22, y + treeHeight, 110, 0);

            treeHeight += oldAdd;
            count++;
        }

        if(entries.size() == 1)
            renderGuiBlockAt(graphics, x + 22, y, 66, 44);
        else if (entries.size() > 1)
            renderGuiBlockAt(graphics, x + 22, y, 88, 22);

        renderDigimonBlockAt(graphics, name, x, y);

        if(mouseX > x - 11 && mouseX < x + 11 && (mouseY > y - 11 && mouseY < y + 11))
            renderEvoName(graphics, mouseX, mouseY, I18n.get("entity.thedigimod." + name));

        return entries.isEmpty() ? 22 : treeHeight;
    }

    public void renderDigimonBlockAt(GuiGraphics graphics, String name, int x, int y){
        graphics.blit(LINE_GRAPH, x - 11, y - 11,0, 66, 22, 22, 22, 132, 88);
        graphics.blit(getSpeciesLocation(name), x - 10, y - 10, 0, 0, 20, 20, 20, 20);
    }

    public void renderGuiBlockAt(GuiGraphics graphics, int x, int y, int textX, int textY){
        graphics.blit(LINE_GRAPH, x - 11, y - 11,0, textX, textY, 22, 22, 132, 88);
    }

    public void renderBackGround(GuiGraphics graphics, int height, int width, int x, int y){

        int blockHeight = height/22, blockWidth = width/22;

        //corners
        renderGuiBlockAt(graphics,x - 22, y - 22,0,0);
        renderGuiBlockAt(graphics,x - 22, y + height,0,44);
        renderGuiBlockAt(graphics,x + width, y - 22,44,0);
        renderGuiBlockAt(graphics,x + width, y + height,44,44);

        //borders
        IntStream.range(0, blockWidth).forEach(i -> {
                renderGuiBlockAt(graphics, x + i * 22, y - 22, 22, 0);
                renderGuiBlockAt(graphics, x + i * 22, y + height, 22, 44);
        });

        IntStream.range(0, blockHeight).forEach(i -> {
            renderGuiBlockAt(graphics, x - 22, y + i * 22, 0, 22);
            renderGuiBlockAt(graphics, x + width, y + i * 22, 44, 22);
        });

        //center
        IntStream.range(0, blockWidth).forEach(
                i -> IntStream.range(0, blockHeight).forEach(
                        j -> renderGuiBlockAt(graphics, x + i * 22, y + j * 22, 22, 22)
                )
        );
    }

    private void renderEvoName(GuiGraphics guiGraphics, int x, int y, String name){
        int yMultiplier = y - 11;

        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_top.png"),
                x,yMultiplier-3,0,0,69,3, 69,3);

        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_xp.png"),
                x,yMultiplier,0,0,69,11, 69,11);

        guiGraphics.drawString(Minecraft.getInstance().font, name.substring(0, Math.min(name.length(), 11)), x+3, yMultiplier + 2, CommonColors.WHITE);

        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_end.png"),
                x,y,0,0,69,3, 69,3);
    }
}
