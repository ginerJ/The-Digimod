package net.modderg.thedigimod.gui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.managers.EvolutionCondition;
import net.modderg.thedigimod.item.InitItems;
import net.modderg.thedigimod.packet.CToSMemorySpawnCommandPacket;
import net.modderg.thedigimod.packet.PacketInit;
import net.modderg.thedigimod.sound.DigiSounds;

import java.util.HashMap;
import java.util.Map;

public class StatsGui {

    private static int guiId = 1;
    public static void switchGui(){guiId = guiId + (guiId == 2 ? -guiId : 1);}

    private static final ResourceLocation STATS_GUI = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/right_gui.png");
    private static final ResourceLocation DATA_GUI = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/left_gui.png");
    private static final ResourceLocation EVO_GUI = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/digimon_gui2.png");

    public static void renderAnalysisGui(GuiGraphics guiGraphics, int screenWidth, int screenHeight, CustomDigimon cd) {
        if (guiId == 0) {
            renderDigimonStatInfo(guiGraphics, cd);
        } else if (guiId == 1){
            renderDigimonEvolutionInfo(guiGraphics, screenWidth, screenHeight, cd);
        } else {
            renderDigimonDataInfo(guiGraphics, cd);
        }
    }


    private static void renderDigimonDataInfo(GuiGraphics guiGraphics, CustomDigimon cd){
        guiGraphics.blit(DATA_GUI,guiGraphics.guiWidth()-95,guiGraphics.guiHeight()-104,0,0,95,104,
                95,104);

        drawHUDRightStrings(guiGraphics, 95,0,
                Component.literal("Dragon: " + cd.getSpecificGainedXps(0)).withStyle(style -> style.withColor(TextColor.fromRgb(0xD86D1C))),
                Component.literal("Beast: " + cd.getSpecificGainedXps(1)).withStyle(style -> style.withColor(TextColor.fromRgb(0xC5DE21))),
                Component.literal("Insect/Plant: " + cd.getSpecificGainedXps(2)).withStyle(style -> style.withColor(TextColor.fromRgb(0x63DC1C))),
                Component.literal("Aquan: " + cd.getSpecificGainedXps(3)).withStyle(style -> style.withColor(TextColor.fromRgb(0x3492F0))),
                Component.literal("Wind: " + cd.getSpecificGainedXps(4)).withStyle(style -> style.withColor(TextColor.fromRgb(0x47DBCC))),
                Component.literal("Machine: " + cd.getSpecificGainedXps(5)).withStyle(style -> style.withColor(TextColor.fromRgb(0xAEB0B0))),
                Component.literal("Earth: " + cd.getSpecificGainedXps(6)).withStyle(style -> style.withColor(TextColor.fromRgb(0xB57F68))),
                Component.literal("Nightmare: " + cd.getSpecificGainedXps(7)).withStyle(style -> style.withColor(TextColor.fromRgb(0x9546CA))),
                Component.literal("Holy: " + cd.getSpecificGainedXps(8)).withStyle(style -> style.withColor(TextColor.fromRgb(0xDED4CA)))
        );
    }

    private static void renderDigimonStatInfo(GuiGraphics guiGraphics, CustomDigimon cd){

        int lvl = cd.getCurrentLevel();

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

    }

    private static void renderDigimonEvolutionInfo(GuiGraphics graphics, int screenWidth, int screenHeight, CustomDigimon cd){

        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();

        int x = Mth.floor(minecraft.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth());
        int y = Mth.floor(minecraft.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight());

        graphics.blit(EVO_GUI,screenWidth-125,screenHeight-104,0,0,125,104,
                125,104);

        renderProfessionIcons(graphics, screenWidth, screenHeight, cd);

        digitalBlit(graphics,"icons/"+ cd.getLowerCaseSpecies()+".png",
                screenWidth-57,screenHeight-75,20,20);

        digitalBlit(graphics,"item/"+ getXpItem(cd.getXpDrop()) + ".png",
                screenWidth-33,screenHeight-74,20,20);

        digitalBlit(graphics,"item/chip_"+ cd.getSpMoveName() + ".png",
                screenWidth-81,screenHeight-74,20,20);

        if(x>= screenWidth-81 && screenWidth-61 >=x && y>= screenHeight-75 && screenHeight-55 >=y)
            graphics.renderTooltip(Minecraft.getInstance().font, new ItemStack(InitItems.itemMap.get("chip_"+ cd.getSpMoveName()).get()), x, y);
        else {
            digitalBlit(graphics,"icons/rank_"+ cd.getRank()+".png",
                    screenWidth-42,screenHeight-83,8,9);
        }

        int conditionToRender = -1;

        if(cd.evolutionConditions[0] != null){
            digitalBlit(graphics,"icons/"+ cd.evolutionConditions[0].getEvolution() +".png",
                    screenWidth-57,screenHeight-52,20,20);
            if(x>= screenWidth-57 && screenWidth-37 >=x && y>= screenHeight-52 && screenHeight-32 >=y) {
                conditionToRender = 0;
                digitalBlit(graphics,"icons/rank_"+ cd.evolutionConditions[0].getRank() +".png",
                        screenWidth-42,screenHeight-57,8,9);
            }
        }

        if(cd.evolutionConditions[1] != null){
            digitalBlit(graphics,"icons/"+ cd.evolutionConditions[1].getEvolution() +".png",
                    screenWidth-81,screenHeight-52,20,20);
            if(x>= screenWidth-81 && screenWidth-61 >=x && y>= screenHeight-52 && screenHeight-32 >=y) {
                conditionToRender = 1;
                digitalBlit(graphics,"icons/rank_"+ cd.evolutionConditions[1].getRank() +".png",
                        screenWidth-66,screenHeight-57,8,9);
            }
        }
        if(cd.evolutionConditions[2] != null){
            digitalBlit(graphics,"icons/"+ cd.evolutionConditions[2].getEvolution() +".png",
                    screenWidth-33,screenHeight-52,20,20);
            if(x>= screenWidth-33 && screenWidth-13 >=x && y>= screenHeight-52 && screenHeight-32 >=y) {
                conditionToRender = 2;
                digitalBlit(graphics,"icons/rank_"+ cd.evolutionConditions[2].getRank() +".png",
                        screenWidth-18,screenHeight-57,8,9);
            }
        }
        if(cd.evolutionConditions[3] != null){
            digitalBlit(graphics,"icons/"+ cd.evolutionConditions[3].getEvolution() +".png",
                    screenWidth-57,screenHeight-28,20,20);
            if(x>= screenWidth-57 && screenWidth-37 >=x && y>= screenHeight-28 && screenHeight-8 >=y) {
                conditionToRender = 3;
                digitalBlit(graphics,"icons/rank_"+ cd.evolutionConditions[3].getRank() +".png",
                        screenWidth-42,screenHeight-33,8,9);
            }
        }
        if(cd.evolutionConditions[4] != null){
            digitalBlit(graphics,"icons/"+ cd.evolutionConditions[4].getEvolution() +".png",
                    screenWidth-81,screenHeight-28,20,20);
            if(x>= screenWidth-81 && screenWidth-61 >=x && y>= screenHeight-28 && screenHeight-8 >=y) {
                conditionToRender = 4;
                digitalBlit(graphics,"icons/rank_"+ cd.evolutionConditions[4].getRank() +".png",
                        screenWidth-66,screenHeight-33,8,9);
            }
        }
        if(cd.evolutionConditions[5] != null){
            digitalBlit(graphics,"icons/"+ cd.evolutionConditions[5].getEvolution() +".png",
                    screenWidth-33,screenHeight-28,20,20);
            if(x>= screenWidth-33 && screenWidth-13 >=x && y>= screenHeight-28 && screenHeight-8 >=y) {
                conditionToRender = 5;
                digitalBlit(graphics,"icons/rank_"+ cd.evolutionConditions[5].getRank() +".png",
                        screenWidth-18,screenHeight-33,8,9);
            }
        }

        String[] preEvos = cd.getPreEvo().split("-");
        if(!preEvos[0].equals("a")){
            digitalBlit(graphics,"icons/"+ preEvos[0] +".png",
                    screenWidth-118,screenHeight-88,20,20);

            if(!preEvos[1].equals("a")){
                digitalBlit(graphics,"icons/"+ preEvos[1] +".png",
                        screenWidth-118,screenHeight-67,20,20);
                if(!preEvos[2].equals("a")){
                    digitalBlit(graphics,"icons/"+ preEvos[2] +".png",
                            screenWidth-118,screenHeight-46,20,20);
                    if(!preEvos[3].equals("a")){
                        digitalBlit(graphics,"icons/"+ preEvos[3] +".png",
                                screenWidth-118,screenHeight-25,20,20);
                    }
                }
            }
            if(x>= screenWidth-118 && screenWidth-98 >=x && y>= screenHeight-88 && screenHeight-5 >=y)
                digitalBlit(graphics,"gui/dark_shard_cond.png",
                        x-25,y-22,25,24);
        }


        if (conditionToRender != -1) renderEvoConditions(graphics,cd.evolutionConditions[conditionToRender],x-69,y, I18n.get("entity.thedigimod." + cd.evolutionConditions[conditionToRender].getEvolution()), cd.evolutionConditions[conditionToRender].checkConditions());

        if(minecraft.player != null && cd.isOwnedBy(minecraft.player))
            renderCommandMemoryButton((DigiviceScreen) minecraft.screen, screenWidth, screenHeight, cd);
    }

    private static void renderProfessionIcons(GuiGraphics graphics, int screenWidth, int screenHeight, CustomDigimon cd){

        if(cd.profession != null){

            switch (cd.profession) {
                case "transporter" ->
                        digitalBlit(graphics, "gui/prof_storer.png", screenWidth - 90, screenHeight - 91, 16, 16);
                case "tree_lover" ->
                        digitalBlit(graphics, "gui/prof_sappling.png", screenWidth - 76, screenHeight - 92, 16, 16);
                case "lumberjack" ->
                        digitalBlit(graphics, "gui/prof_log.png", screenWidth - 62, screenHeight - 93, 16, 16);
                case "farmer" ->
                        digitalBlit(graphics, "gui/prof_wheat.png", screenWidth - 49, screenHeight - 93, 16, 16);
                case "miner" ->
                        digitalBlit(graphics, "gui/prof_miner.png", screenWidth - 34, screenHeight - 93, 16, 16);
            }
        }

        if(cd.isMountDigimon())
            digitalBlit(graphics, "gui/prof_ride.png", screenWidth - 21, screenHeight - 92, 16, 16);
    }

    private static final ResourceLocation MEMORYBUTTON =new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/button_memory.png");

    public static void renderCommandMemoryButton(DigiviceScreen screen, int screenWidth, int screenHeight, CustomDigimon cd) {

        ImageButton attButton = new ImageButton(screenWidth - 145, screenHeight - 22, 20, 20, 0, 0, 0, MEMORYBUTTON, 20, 20,
                (button) -> {
            PacketInit.sendToServer(new CToSMemorySpawnCommandPacket(cd.getId()));
            Minecraft.getInstance().player.playNotifySound(DigiSounds.COMMAND_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
            Minecraft.getInstance().setScreen(null);
        });

        screen.addRenderableWidget(attButton);
    }

    private static final Map<String, ResourceLocation> resourceCache = new HashMap<>();

    private static void digitalBlit(GuiGraphics graphics, String path, int xPos, int yPos, int xSize, int ySize){
        ResourceLocation resource = resourceCache.computeIfAbsent(path, p -> new ResourceLocation(TheDigiMod.MOD_ID,"textures/" + p));
        graphics.blit(resource, xPos, yPos, 0, 0, xSize, ySize, xSize, ySize);
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

    public static String getXpItem(int id) {
        switch (id) {
            case 0: return "dragon_data";
            case 1: return "beast_data";
            case 2: return "plantinsect_data";
            case 3: return "aquan_data";
            case 4: return "wind_data";
            case 5: return "machine_data";
            case 6: return "earth_data";
            case 7: return "nightmare_data";
        }
        return "holy_data";
    }

    public static void renderEvoConditions(GuiGraphics guiGraphics, EvolutionCondition conditions, int x, int y, String name, boolean meetsConds){
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
                x,yMultiplier-3,0,0,69,3, 69,3);

        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_xp.png"),
                x,yMultiplier,0,0,69,11, 69,11);

        guiGraphics.drawString(Minecraft.getInstance().font, name.substring(0, Math.min(name.length(), 12)), x+2, yMultiplier + 2,
                meetsConds ? 0x00FF00:CommonColors.WHITE);

        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_end.png"),
                x,y,0,0,69,3, 69,3);
    }
}
