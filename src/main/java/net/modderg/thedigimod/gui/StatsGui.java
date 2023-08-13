package net.modderg.thedigimod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.item.DigiviceItem;

@Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StatsGui {

    private static final ResourceLocation RIGHT_GUI = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/right_gui.png");
    private static final ResourceLocation LEFT_GUI = new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/left_gui.png");

    static IGuiOverlay STATS_GUI = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Entity renderViewEntity = Minecraft.getInstance().cameraEntity;
        if (renderViewEntity instanceof Player player && (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof DigiviceItem ||
                player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof DigiviceItem)
        && Minecraft.getInstance().hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof CustomDigimon cd) {

            guiGraphics.blit(RIGHT_GUI,guiGraphics.guiWidth()-127,guiGraphics.guiHeight()-80,0,0,127,80,
                    127,80);
            guiGraphics.blit(LEFT_GUI,0,guiGraphics.guiHeight()-104,0,0,95,104,
                    95,104);

            int lvl = cd.getCurrentLevel();
            Component lvlAdd = Component.literal(" + " + lvl).withStyle(style -> style.withColor(TextColor.fromRgb(0xECBE3E)));
            drawHUDRightStrings(guiGraphics,
                    Component.literal("Attack: " + cd.getAttackStat()).withStyle(style -> style.withColor(TextColor.fromRgb(0xFF0000)))
                            .append(lvlAdd),
                    Component.literal("Defense: " + cd.getDefenceStat()).withStyle(style -> style.withColor(TextColor.fromRgb(0x00FF00)))
                            .append(lvlAdd),
                    Component.literal("Sp.Attack: " + cd.getSpAttackStat()).withStyle(style -> style.withColor(TextColor.fromRgb(0xFF69B4)))
                            .append(lvlAdd),
                    Component.literal("Sp.Defense: " + cd.getSpDefenceStat()).withStyle(style -> style.withColor(TextColor.fromRgb(0xADD8E6)))
                            .append(lvlAdd),
                    Component.literal("Wins: " + cd.getBattlesStat()).withStyle(style -> style.withColor(TextColor.fromRgb(0xFF542D))),
                    Component.literal("Care Mistakes: " + cd.getCareMistakesStat()).withStyle(style -> style.withColor(TextColor.fromRgb(0x5AADFF)))
            );

            drawHudLeftStrings(guiGraphics,
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
        }
    });

    private static void drawHUDRightStrings(GuiGraphics graphics, Component... lines) {
            Font font = Minecraft.getInstance().font;
            int x = graphics.guiWidth();
            int y = graphics.guiHeight() - 5 - (font.lineHeight * lines.length);
            for (Component line : lines) {
                graphics.drawString(font, line, x - 120, y, CommonColors.WHITE);
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

    @SubscribeEvent
    public static void registerEntityRenders(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("stats", StatsGui.STATS_GUI);
    }
}



