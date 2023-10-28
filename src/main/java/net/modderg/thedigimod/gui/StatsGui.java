package net.modderg.thedigimod.gui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.managers.EvolutionCondition;
import net.modderg.thedigimod.item.custom.CustomXpItem;
import net.modderg.thedigimod.item.custom.DigiviceItem;

@Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StatsGui {

    public static boolean isShowing = false;

    public static boolean freedMouse = false;

    public static void switchFreeMouse(){
        if(!freedMouse){
            freedMouse = true;
            Minecraft.getInstance().mouseHandler.releaseMouse();
        } else {
            Minecraft.getInstance().mouseHandler.grabMouse();
            freedMouse = false;
        }
    }

    private static int guiId = 1;
    public static void switchGui(){guiId = guiId + (guiId == 2 ? -guiId : 1);}

    public static int getGuiId(){
       return guiId;
    }

    private static final ResourceLocation STATS_GUI = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/right_gui.png");
    private static final ResourceLocation DATA_GUI = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/left_gui.png");
    private static final ResourceLocation DIGIMON_GUI = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/digimon_gui.png");
private static final ResourceLocation DIGIMON_GUI2 = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/digimon_gui2.png");

    public static IGuiOverlay MENU_GUI = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {

        Entity renderViewEntity = Minecraft.getInstance().cameraEntity;

        if (renderViewEntity instanceof Player player && (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof DigiviceItem ||
                player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof DigiviceItem)
                && Minecraft.getInstance().hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof CustomDigimon cd)
        {
            isShowing = true;
            if(freedMouse){
                if(Minecraft.getInstance().mouseHandler.isLeftPressed()) switchFreeMouse();
                guiGraphics.fill(0, 0, screenWidth, screenHeight, 0x80000000);
            }

            int lvl = cd.getCurrentLevel();
            int guiId = getGuiId();

            if(guiId == 2){
                guiGraphics.blit(DATA_GUI,guiGraphics.guiWidth()-95,guiGraphics.guiHeight()-104,0,0,95,104,
                        95,104);

                drawHUDRightStrings(guiGraphics, 95,0,
                        Component.literal("Dragon: " + cd.getSpecificXps(0)).withStyle(style -> style.withColor(TextColor.fromRgb(0xD86D1C))),
                        Component.literal("Beast: " + cd.getSpecificXps(1)).withStyle(style -> style.withColor(TextColor.fromRgb(0xC5DE21))),
                        Component.literal("Insect/Plant: " + cd.getSpecificXps(2)).withStyle(style -> style.withColor(TextColor.fromRgb(0x63DC1C))),
                        Component.literal("Aquan: " + cd.getSpecificXps(3)).withStyle(style -> style.withColor(TextColor.fromRgb(0x3492F0))),
                        Component.literal("Wind: " + cd.getSpecificXps(4)).withStyle(style -> style.withColor(TextColor.fromRgb(0x47DBCC))),
                        Component.literal("Machine: " + cd.getSpecificXps(5)).withStyle(style -> style.withColor(TextColor.fromRgb(0xAEB0B0))),
                        Component.literal("Earth: " + cd.getSpecificXps(6)).withStyle(style -> style.withColor(TextColor.fromRgb(0xB57F68))),
                        Component.literal("Nightmare: " + cd.getSpecificXps(7)).withStyle(style -> style.withColor(TextColor.fromRgb(0x9546CA))),
                        Component.literal("Holy: " + cd.getSpecificXps(8)).withStyle(style -> style.withColor(TextColor.fromRgb(0xDED4CA)))
                );
            } else if (guiId == 0) {
                guiGraphics.blit(STATS_GUI,guiGraphics.guiWidth()-127,guiGraphics.guiHeight()-104,0,0,127,80,
                        127,80);

                Component lvlAdd = Component.literal(" + " + lvl).withStyle(style -> style.withColor(TextColor.fromRgb(0xECBE3E)));
                drawHUDRightStrings(guiGraphics, 127,24,
                        Component.literal("Attack: " + cd.getAttackStat()).withStyle(style -> style.withColor(TextColor.fromRgb(0xFF0000)))
                                .append(lvlAdd),
                        Component.literal("Defense: " + cd.getDefenceStat()).withStyle(style -> style.withColor(TextColor.fromRgb(0x00FF00)))
                                .append(lvlAdd),
                        Component.literal("Sp.Attack: " + cd.getSpAttackStat()).withStyle(style -> style.withColor(TextColor.fromRgb(0xFF69B4)))
                                .append(lvlAdd),
                        Component.literal("Sp.Defense: " + cd.getSpDefenceStat()).withStyle(style -> style.withColor(TextColor.fromRgb(0xADD8E6)))
                                .append(lvlAdd),
                        Component.literal("Wins: " + cd.getBattlesStat()).withStyle(style -> style.withColor(TextColor.fromRgb(0xFF542D))),
                        Component.literal("Care Mistakes: " + cd.getCareMistakesStat()).withStyle(style -> style.withColor(TextColor.fromRgb(0x5AADFF))));
            } else {
                renderDigimonGui(gui, guiGraphics, partialTick, screenWidth, screenHeight, cd);
            }
        }
        else {
            isShowing = false;
            if(freedMouse) switchFreeMouse();
        }
    });

    private static void drawHUDRightStrings(GuiGraphics graphics,int guiWidth, int guiHeight, Component... lines) {
            Font font = Minecraft.getInstance().font;
            int x = graphics.guiWidth() + 8;
            int y = graphics.guiHeight() - 5 - (font.lineHeight * lines.length);
            for (Component line : lines) {
                graphics.drawString(font, line, x - guiWidth, y -guiHeight, CommonColors.WHITE);
                y += font.lineHeight;
            }
    }

    private static void drawHudLeftStrings(GuiGraphics graphics, Component... lines) {
        Font font = Minecraft.getInstance().font;
        int x = 0;
        int y = graphics.guiHeight() - 5 - (font.lineHeight * lines.length);
        for (Component line : lines) {
            graphics.drawString(font, line, x + 5, y, CommonColors.WHITE);
            y += font.lineHeight;
        }
    }

    private static void renderDigimonGui(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight, CustomDigimon cd){

        guiGraphics.blit((freedMouse?DIGIMON_GUI2:DIGIMON_GUI),guiGraphics.guiWidth()-125,guiGraphics.guiHeight()-104,0,0,125,104,
                125,104);

        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/icons/"+ cd.getLowerCaseSpecies()+".png"),
                guiGraphics.guiWidth()-57,guiGraphics.guiHeight()-78,0,0,20,20, 20,20);

        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/item/"+ CustomXpItem.getXpItem((((CustomXpItem)cd.getXpDrop().getItem()).xpId)) + ".png"),
                guiGraphics.guiWidth()-33,guiGraphics.guiHeight()-78,0,0,20,20, 20,20);

        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/item/chip_"+ cd.getSpMoveName() + ".png"),
                guiGraphics.guiWidth()-81,guiGraphics.guiHeight()-78,0,0,20,20, 20,20);

        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();

        int x = Mth.floor(minecraft.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth());
        int y = Mth.floor(minecraft.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight());

        EvolutionCondition conditionToRender = null;

        if(cd.getEvoPath != null){
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/icons/"+ cd.getEvoPath.getLowerCaseSpecies() +".png"),
                    guiGraphics.guiWidth()-57,guiGraphics.guiHeight()-52,0,0,20,20, 20,20);
            if(x>= guiGraphics.guiWidth()-57 && guiGraphics.guiWidth()-37 >=x && y>= guiGraphics.guiHeight()-52 && guiGraphics.guiHeight()-32 >=y)
                conditionToRender = cd.evoCondition;
        }
        if(cd.getEvoPath2 != null){
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/icons/"+ cd.getEvoPath2.getLowerCaseSpecies() +".png"),
                    guiGraphics.guiWidth()-81,guiGraphics.guiHeight()-52,0,0,20,20, 20,20);
            if(x>= guiGraphics.guiWidth()-81 && guiGraphics.guiWidth()-61 >=x && y>= guiGraphics.guiHeight()-52 && guiGraphics.guiHeight()-32 >=y)
                conditionToRender = cd.evoCondition2;
        }
        if(cd.getEvoPath3 != null){
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/icons/"+ cd.getEvoPath3.getLowerCaseSpecies() +".png"),
                    guiGraphics.guiWidth()-33,guiGraphics.guiHeight()-52,0,0,20,20, 20,20);
            if(x>= guiGraphics.guiWidth()-33 && guiGraphics.guiWidth()-13 >=x && y>= guiGraphics.guiHeight()-52 && guiGraphics.guiHeight()-32 >=y)
                conditionToRender = cd.evoCondition3;
        }
        if(cd.getEvoPath4 != null){
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/icons/"+ cd.getEvoPath4.getLowerCaseSpecies() +".png"),
                    guiGraphics.guiWidth()-57,guiGraphics.guiHeight()-28,0,0,20,20, 20,20);
            if(x>= guiGraphics.guiWidth()-57 && guiGraphics.guiWidth()-37 >=x && y>= guiGraphics.guiHeight()-28 && guiGraphics.guiHeight()-8 >=y)
                conditionToRender = cd.evoCondition4;
        }
        if(cd.getEvoPath5 != null){
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/icons/"+ cd.getEvoPath5.getLowerCaseSpecies() +".png"),
                    guiGraphics.guiWidth()-81,guiGraphics.guiHeight()-28,0,0,20,20, 20,20);
            if(x>= guiGraphics.guiWidth()-81 && guiGraphics.guiWidth()-61 >=x && y>= guiGraphics.guiHeight()-28 && guiGraphics.guiHeight()-8 >=y)
                conditionToRender = cd.evoCondition5;
        }
        if(cd.getEvoPath6 != null){
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/icons/"+ cd.getEvoPath6.getLowerCaseSpecies() +".png"),
                    guiGraphics.guiWidth()-33,guiGraphics.guiHeight()-28,0,0,20,20, 20,20);
            if(x>= guiGraphics.guiWidth()-33 && guiGraphics.guiWidth()-13 >=x && y>= guiGraphics.guiHeight()-28 && guiGraphics.guiHeight()-8 >=y)
                conditionToRender = cd.evoCondition6;
        }

        if(freedMouse){
            if(!cd.getPreEvo().split("-")[0].equals("a")){
                guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/icons/"+ cd.getPreEvo().split("-")[0] +".png"),
                        guiGraphics.guiWidth()-118,guiGraphics.guiHeight()-88,0,0,20,20, 20,20);

                if(!cd.getPreEvo().split("-")[1].equals("a")){
                    guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/icons/"+ cd.getPreEvo().split("-")[1] +".png"),
                            guiGraphics.guiWidth()-118,guiGraphics.guiHeight()-67,0,0,20,20, 20,20);
                    if(!cd.getPreEvo().split("-")[2].equals("a")){
                        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/icons/"+ cd.getPreEvo().split("-")[2] +".png"),
                                guiGraphics.guiWidth()-118,guiGraphics.guiHeight()-46,0,0,20,20, 20,20);
                        if(!cd.getPreEvo().split("-")[3].equals("a")){
                            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/icons/"+ cd.getPreEvo().split("-")[3] +".png"),
                                    guiGraphics.guiWidth()-118,guiGraphics.guiHeight()-25,0,0,20,20, 20,20);
                        }
                    }
                }
                if(x>= guiGraphics.guiWidth()-118 && guiGraphics.guiWidth()-98 >=x && y>= guiGraphics.guiHeight()-88 && guiGraphics.guiHeight()-5 >=y)
                    guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/dark_shard_cond.png"),
                            x-25,y-24,0,0,25,24, 25,24);
            }

        }


        if (conditionToRender != null && freedMouse) renderEvoConditions(guiGraphics,conditionToRender,x-69,y);
    }

    public static void renderEvoConditions(GuiGraphics guiGraphics, EvolutionCondition conditions, int x, int y){
        int yMultiplier = y - 11;
        for(EvolutionCondition cond :conditions.conditions){
            cond.renderCondition(guiGraphics, x,yMultiplier);
            yMultiplier -= 11;
        }
        if(conditions.conditions.isEmpty()) {
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_none.png"),
                    x,yMultiplier,0,0,69,11, 69,11);
            yMultiplier -= 11;
        }
        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_top.png"),
                x,yMultiplier+8,0,0,69,3, 69,3);
        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_end.png"),
                x,y,0,0,69,3, 69,3);
    }


    @SubscribeEvent
    public static void registerEntityRenders(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("stats", StatsGui.MENU_GUI);
    }
}
