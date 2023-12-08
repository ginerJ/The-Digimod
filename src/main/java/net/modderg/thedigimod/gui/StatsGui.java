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
import net.minecraft.world.item.ItemStack;
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
import net.modderg.thedigimod.item.DigiItems;
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
                && Minecraft.getInstance().hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof CustomDigimon cd && !player.hasPassenger(cd))
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
                renderDigimonGui(guiGraphics, screenWidth, cd);
            }
        }
        else {
            isShowing = false;
            if(freedMouse) switchFreeMouse();
        }
    });

    private static void renderDigimonGui(GuiGraphics graphics, int screenWidth, CustomDigimon cd){

        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();

        int x = Mth.floor(minecraft.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth());
        int y = Mth.floor(minecraft.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight());

        graphics.blit((freedMouse?DIGIMON_GUI2:DIGIMON_GUI),screenWidth-125,graphics.guiHeight()-104,0,0,125,104,
                125,104);

        digitalBlit(graphics,"icons/"+ cd.getLowerCaseSpecies()+".png",
                screenWidth-57,graphics.guiHeight()-78,20,20);

        digitalBlit(graphics,"item/"+ CustomXpItem.getXpItem((((CustomXpItem)cd.getXpDrop()).xpId)) + ".png",
                screenWidth-33,graphics.guiHeight()-78,20,20);

        digitalBlit(graphics,"item/chip_"+ cd.getSpMoveName() + ".png",
                screenWidth-81,graphics.guiHeight()-78,20,20);

        if(x>= screenWidth-81 && screenWidth-61 >=x && y>= graphics.guiHeight()-78 && graphics.guiHeight()-58 >=y)
            graphics.renderTooltip(Minecraft.getInstance().font, new ItemStack(DigiItems.itemMap.get("chip_"+ cd.getSpMoveName()).get()), x, y);
        else {
            digitalBlit(graphics,"icons/rank_"+ cd.getRank()+".png",
                    screenWidth-42,graphics.guiHeight()-83,8,9);
        }

        EvolutionCondition conditionToRender = null;

        if(cd.getEvoPaths[0] != null){
            digitalBlit(graphics,"icons/"+ cd.getEvoPaths[0].getLowerCaseSpecies() +".png",
                    screenWidth-57,graphics.guiHeight()-52,20,20);
            if(x>= screenWidth-57 && screenWidth-37 >=x && y>= graphics.guiHeight()-52 && graphics.guiHeight()-32 >=y) {
                conditionToRender = cd.evolutionConditions[0];
                digitalBlit(graphics,"icons/rank_"+ cd.getEvoPaths[0].getRank() +".png",
                        screenWidth-42,graphics.guiHeight()-57,8,9);
            }
        }

        if(cd.getEvoPaths[1] != null){
            digitalBlit(graphics,"icons/"+ cd.getEvoPaths[1].getLowerCaseSpecies() +".png",
                    screenWidth-81,graphics.guiHeight()-52,20,20);
            if(x>= screenWidth-81 && screenWidth-61 >=x && y>= graphics.guiHeight()-52 && graphics.guiHeight()-32 >=y) {
                conditionToRender = cd.evolutionConditions[1];
                digitalBlit(graphics,"icons/rank_"+ cd.getEvoPaths[1].getRank() +".png",
                        screenWidth-66,graphics.guiHeight()-57,8,9);
            }
        }
        if(cd.getEvoPaths[2] != null){
            digitalBlit(graphics,"icons/"+ cd.getEvoPaths[2].getLowerCaseSpecies() +".png",
                    screenWidth-33,graphics.guiHeight()-52,20,20);
            if(x>= screenWidth-33 && screenWidth-13 >=x && y>= graphics.guiHeight()-52 && graphics.guiHeight()-32 >=y) {
                conditionToRender = cd.evolutionConditions[2];
                digitalBlit(graphics,"icons/rank_"+ cd.getEvoPaths[2].getRank() +".png",
                        screenWidth-18,graphics.guiHeight()-57,8,9);
            }
        }
        if(cd.getEvoPaths[3] != null){
            digitalBlit(graphics,"icons/"+ cd.getEvoPaths[3].getLowerCaseSpecies() +".png",
                    screenWidth-57,graphics.guiHeight()-28,20,20);
            if(x>= screenWidth-57 && screenWidth-37 >=x && y>= graphics.guiHeight()-28 && graphics.guiHeight()-8 >=y) {
                conditionToRender = cd.evolutionConditions[3];
                digitalBlit(graphics,"icons/rank_"+ cd.getEvoPaths[3].getRank() +".png",
                        screenWidth-42,graphics.guiHeight()-33,8,9);
            }
        }
        if(cd.getEvoPaths[4] != null){
            digitalBlit(graphics,"icons/"+ cd.getEvoPaths[4].getLowerCaseSpecies() +".png",
                    screenWidth-81,graphics.guiHeight()-28,20,20);
            if(x>= screenWidth-81 && screenWidth-61 >=x && y>= graphics.guiHeight()-28 && graphics.guiHeight()-8 >=y) {
                conditionToRender = cd.evolutionConditions[4];
                digitalBlit(graphics,"icons/rank_"+ cd.getEvoPaths[4].getRank() +".png",
                        screenWidth-66,graphics.guiHeight()-33,8,9);
            }
        }
        if(cd.getEvoPaths[5] != null){
            digitalBlit(graphics,"icons/"+ cd.getEvoPaths[5].getLowerCaseSpecies() +".png",
                    screenWidth-33,graphics.guiHeight()-28,20,20);
            if(x>= screenWidth-33 && screenWidth-13 >=x && y>= graphics.guiHeight()-28 && graphics.guiHeight()-8 >=y) {
                conditionToRender = cd.evolutionConditions[5];
                digitalBlit(graphics,"icons/rank_"+ cd.getEvoPaths[5].getRank() +".png",
                        screenWidth-18,graphics.guiHeight()-33,8,9);
            }
        }

        if(freedMouse){
            String[] preEvos = cd.getPreEvo().split("-");
            if(!preEvos[0].equals("a")){
                digitalBlit(graphics,"icons/"+ preEvos[0] +".png",
                        screenWidth-118,graphics.guiHeight()-88,20,20);

                if(!preEvos[1].equals("a")){
                    digitalBlit(graphics,"icons/"+ preEvos[1] +".png",
                            screenWidth-118,graphics.guiHeight()-67,20,20);
                    if(!preEvos[2].equals("a")){
                        digitalBlit(graphics,"icons/"+ preEvos[2] +".png",
                                screenWidth-118,graphics.guiHeight()-46,20,20);
                        if(!preEvos[3].equals("a")){
                            digitalBlit(graphics,"icons/"+ preEvos[3] +".png",
                                    screenWidth-118,graphics.guiHeight()-25,20,20);
                        }
                    }
                }
                if(x>= screenWidth-118 && screenWidth-98 >=x && y>= graphics.guiHeight()-88 && graphics.guiHeight()-5 >=y)
                    digitalBlit(graphics,"gui/dark_shard_cond.png",
                            x-25,y-22,25,24);
            }

        }


        if (conditionToRender != null && freedMouse) renderEvoConditions(graphics,conditionToRender,x-69,y);
    }

    private static void digitalBlit(GuiGraphics graphics, String path, int xPos, int yPos, int xSize, int ySize){
        graphics.blit(new ResourceLocation(TheDigiMod.MOD_ID,"textures/" + path),xPos,yPos,0,0,xSize,ySize,xSize,ySize);
    }

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
