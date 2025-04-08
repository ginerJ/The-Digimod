package net.modderg.thedigimod.client.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.client.packet.CToSSetRollFirstEvoPacket;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.entity.managers.EvolutionCondition;
import net.modderg.thedigimod.server.item.TDItems;
import net.modderg.thedigimod.server.packet.PacketInit;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class DigiviceScreenStats {

    private static int guiId = 1;
    public static void switchGui(){guiId = guiId + (guiId == 2 ? -guiId : 1);}

    private static final ResourceLocation STATS_GUI = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/right_gui.png");
    private static final ResourceLocation DATA_GUI = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/left_gui.png");
    private static final ResourceLocation EVO_GUI = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/digimon_gui2.png");

    public static void renderAnalysisGui(DigiviceScreen screen, GuiGraphics guiGraphics, int screenWidth, int screenHeight, DigimonEntity cd) {
        if (guiId == 0)
            renderDigimonStatInfo(guiGraphics, cd);
        else if (guiId == 1)
            renderDigimonEvolutionInfo(screen, guiGraphics, screenWidth, screenHeight, cd);
        else
            renderDigimonDataInfo(guiGraphics, cd);
    }


    private static void renderDigimonDataInfo(GuiGraphics guiGraphics, DigimonEntity cd){
        guiGraphics.blit(DATA_GUI,guiGraphics.guiWidth()-95,guiGraphics.guiHeight()-115,0,0,95,115,
                95,115);

        drawHUDRightStrings(guiGraphics, 95,0,
                Component.literal("Dragon: " + cd.getSpecificGainedXps(0)).withStyle(style -> style.withColor(TextColor.fromRgb(0xD86D1C))),
                Component.literal("Beast: " + cd.getSpecificGainedXps(1)).withStyle(style -> style.withColor(TextColor.fromRgb(0xC5DE21))),
                Component.literal("Insect/Plant: " + cd.getSpecificGainedXps(2)).withStyle(style -> style.withColor(TextColor.fromRgb(0x63DC1C))),
                Component.literal("Aquan: " + cd.getSpecificGainedXps(3)).withStyle(style -> style.withColor(TextColor.fromRgb(0x3492F0))),
                Component.literal("Wind: " + cd.getSpecificGainedXps(4)).withStyle(style -> style.withColor(TextColor.fromRgb(0x47DBCC))),
                Component.literal("Machine: " + cd.getSpecificGainedXps(5)).withStyle(style -> style.withColor(TextColor.fromRgb(0xAEB0B0))),
                Component.literal("Earth: " + cd.getSpecificGainedXps(6)).withStyle(style -> style.withColor(TextColor.fromRgb(0xB57F68))),
                Component.literal("Nightmare: " + cd.getSpecificGainedXps(7)).withStyle(style -> style.withColor(TextColor.fromRgb(0x9546CA))),
                Component.literal("Holy: " + cd.getSpecificGainedXps(8)).withStyle(style -> style.withColor(TextColor.fromRgb(0xDED4CA))),
                Component.literal("Poop: " + cd.getSpecificGainedXps(9)).withStyle(style -> style.withColor(TextColor.fromRgb(0xCA4CB5)))
        );
    }

    private static void renderDigimonStatInfo(GuiGraphics guiGraphics, DigimonEntity cd){

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

    public static void blitEvoPiece(GuiGraphics graphics, int x, int y, int startX, int startY, int endX, int endY) {
        int width = endX - startX;
        int height = endY - startY;

        graphics.blit(EVO_GUI, x, y, 0, startX, startY, width, height, 160, 176);
    }

    public static boolean buttonsRendered = false;

    private static void renderDigimonEvolutionInfo(DigiviceScreen screen, GuiGraphics graphics, int screenWidth, int screenHeight, DigimonEntity cd){

        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();

        int x = Mth.floor(minecraft.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth());
        int y = Mth.floor(minecraft.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight());

        blitEvoPiece(graphics, screenWidth-99,screenHeight-104, 36, 0, 135, 103);

        renderProfessionIcons(graphics, screenWidth, screenHeight, cd);

        int evoRowCols = (cd.evolutionConditions.length + 2) / 3;
        int evoRowColsStart = 1;

        if(evoRowCols == 0){
            evoRowCols = 1;
            evoRowColsStart = 2;
        }

        IntStream.range(evoRowColsStart, evoRowCols+1).forEach( i-> blitEvoPiece(graphics, screenWidth-27 - 24*i,screenHeight-76, 61, 104, 85, 176));
        IntStream.range(3, evoRowCols+1).forEach( i-> blitEvoPiece(graphics, screenWidth-51 - 24*i,screenHeight-95, 136, 9, 160, 103));

        blitEvoPiece(graphics, screenWidth-51 - 24*evoRowCols,screenHeight-76, 36, 104, 60, 176);

        blitEvoPiece(graphics, screenWidth-27,screenHeight-76, 11, 104, 35, 176);

        IntStream.range(0, cd.getXpDrop().length).forEach(i->
                digitalBlit(graphics,"item/"+ getXpItem(cd.getXpDrop()[i]) + ".png", screenWidth-26,screenHeight-74 + 22*i,20,20));

        int iconX = screenWidth-49 - 24*evoRowCols, iconY = screenHeight-50;
        digitalBlit(graphics,"icons/"+ cd.getLowerCaseSpecies()+".png",
                iconX ,iconY,20,20);

        int edgeX = Math.min(iconX-37, screenWidth-134), edgeY = screenHeight-94;
        blitEvoPiece(graphics, edgeX, edgeY, 0, 9, 35, 102);

        int chipX = screenWidth-48 - 24*evoRowCols, chipY = screenHeight-75;
        digitalBlit(graphics,"item/chip_"+ cd.getSpMoveName() + ".png",
                chipX ,chipY,20,20);

        if(x>= chipX && chipX+20 >=x && y>= chipY && chipY+20 >=y)
            graphics.renderTooltip(Minecraft.getInstance().font, new ItemStack(TDItems.itemMap.get("chip_"+ cd.getSpMoveName()).get()), x, y);
        else
            digitalBlit(graphics,"icons/rank_"+ cd.getRank()+".png",
                    iconX+13,iconY-6,8,9);

        int foodX = edgeX+8, foodY = screenHeight -84;
        boolean mouseOnFoodX = x>= foodX && foodX+16 >=x;

        graphics.renderItem(cd.getDiet().maxTierFood, foodX, foodY);
        if(mouseOnFoodX && y>= foodY && foodY+16 >=y)
            graphics.renderTooltip(Minecraft.getInstance().font, cd.getDiet().maxTierFood, x, y);
        foodY += 20;

        graphics.renderItem(cd.getDiet().highTierFood, foodX, foodY);
        if(mouseOnFoodX && y>= foodY && foodY+16 >=y)
            graphics.renderTooltip(Minecraft.getInstance().font, cd.getDiet().highTierFood, x, y);
        foodY += 20;

        graphics.renderItem(cd.getDiet().midTierFood, foodX, foodY);
        if(mouseOnFoodX && y>= foodY && foodY+16 >=y)
            graphics.renderTooltip(Minecraft.getInstance().font, cd.getDiet().midTierFood, x, y);
        foodY += 20;

        graphics.renderItem(cd.getDiet().lowTierFood, foodX, foodY);
        if(mouseOnFoodX && y>= foodY && foodY+16 >=y)
            graphics.renderTooltip(Minecraft.getInstance().font, cd.getDiet().lowTierFood, x, y);

        EvolutionCondition[] renderCond = {null};
        int[] renderCondPos = {0,0};
        IntStream.range(0, cd.evolutionConditions.length).forEach(i->{

            EvolutionCondition cond = cd.evolutionConditions[i];

            if(cond!=null){
                int xPos = iconX + ((i+3)/3) * 24;
                int yOffset = i % 3 ==0 ? 0 : (i%2==0 ? 24 : -24);
                int yPos = screenHeight-50 + yOffset;

                if(i == cd.getRollFirstEvo())
                    blitEvoPiece(graphics, xPos-2, yPos-2, 136, 152, 160, 176);

                if(!buttonsRendered)
                    renderEvoRouteButton(screen, cd, i, xPos, yPos);

                digitalBlit(graphics,"icons/"+ cond.getEvolution() +".png",
                        xPos,yPos,20,20);

                if(x>= xPos && xPos +20 >=x && y>= yPos && yPos+20 >=y){
                    renderCond[0] = cond;
                    renderCondPos[0] = xPos;
                    renderCondPos[1] = yPos;
                }
            }
        });
        buttonsRendered = true;

        if (renderCond[0] != null) {
            EvolutionCondition cond = renderCond[0];

            digitalBlit(graphics, "icons/rank_" + cond.getRank() + ".png", renderCondPos[0] + 14, renderCondPos[1] - 6, 8, 9);

            renderEvoConditions(graphics, cond, x - 69, y, I18n.get("entity.thedigimod." + cond.getEvolution()), cond.checkConditions());
        }
    }

    private static void renderProfessionIcons(GuiGraphics graphics, int screenWidth, int screenHeight, DigimonEntity cd){

        if(cd.profession != null)
            switch (cd.profession) {
                case "transporter" ->
                        digitalBlit(graphics, "gui/prof_storer.png", screenWidth - 90, screenHeight - 91, 16, 16);
                case "tree_lover" ->
                        digitalBlit(graphics, "gui/prof_sappling.png", screenWidth - 76, screenHeight - 92, 16, 16);
                case "lumberjack" ->
                        digitalBlit(graphics, "gui/prof_log.png", screenWidth - 62, screenHeight - 93, 16, 16);
                case "farmer" ->
                        digitalBlit(graphics, "gui/prof_wheat.png", screenWidth - 48, screenHeight - 93, 16, 16);
                case "miner" ->
                        digitalBlit(graphics, "gui/prof_miner.png", screenWidth - 34, screenHeight - 92, 16, 16);
            }

        if(cd.isMountDigimon())
            digitalBlit(graphics, "gui/prof_ride.png", screenWidth - 21, screenHeight - 93, 16, 16);
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
        return switch (id) {
            case 0 -> "dragon_data";
            case 1 -> "beast_data";
            case 2 -> "plantinsect_data";
            case 3 -> "aquan_data";
            case 4 -> "wind_data";
            case 5 -> "machine_data";
            case 6 -> "earth_data";
            case 7 -> "nightmare_data";
            case 8 -> "holy_data";
            default -> "poop_data";
        };
    }

    public static void renderEvoConditions(GuiGraphics guiGraphics, EvolutionCondition conditions, int x, int y, String name, boolean meetsConds) {
        int yMultiplier = y - 11;

        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(0, 0, 200);

        RenderSystem.disableDepthTest();

        for (EvolutionCondition cond : conditions.conditions) {
            cond.renderCondition(guiGraphics, x, yMultiplier);
            yMultiplier -= cond.getGuiHeight();
        }

        if (conditions.conditions.isEmpty()) {
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_none.png"),
                    x, yMultiplier, 0, 0, 69, 11, 69, 11);
            yMultiplier -= 11;
        }

        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_top.png"),
                x, yMultiplier - 3, 0, 0, 69, 3, 69, 3);

        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_xp.png"),
                x, yMultiplier, 0, 0, 69, 11, 69, 11);

        guiGraphics.drawString(
                Minecraft.getInstance().font,
                name.substring(0, Math.min(name.length(), 11)),
                x + 3,
                yMultiplier + 2,
                meetsConds ? 0x00FF00 : CommonColors.WHITE
        );

        guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_end.png"),
                x, y, 0, 0, 69, 3, 69, 3);

        RenderSystem.enableDepthTest();

        guiGraphics.pose().popPose();
    }


    static final ResourceLocation EMPTYPIC = new ResourceLocation(TheDigiMod.MOD_ID,"textures/icons/empty.png");

    public static void renderEvoRouteButton(DigiviceScreen screen, DigimonEntity cd, int index, int x, int y) {
        ImageButton attButton = new ImageButton(x, y, 20, 20, 19, 19, 0, EMPTYPIC, 20, 20,
                (button) -> {
                    cd.setRollFirstEvo(index);
                    PacketInit.sendToServer(new CToSSetRollFirstEvoPacket(cd.getId(), index));
                });

        screen.addRenderableWidget(attButton);
    }
}
