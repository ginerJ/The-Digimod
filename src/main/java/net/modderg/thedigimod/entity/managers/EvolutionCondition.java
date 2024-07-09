package net.modderg.thedigimod.entity.managers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.gui.StatsGui;
import net.modderg.thedigimod.item.custom.CustomXpItem;

import java.util.LinkedList;

public class EvolutionCondition {

    private CustomDigimon digimon = null;
    private boolean alwaysTrue = false;

    private String evolution;

    private String rank = "zero";

    public EvolutionCondition(CustomDigimon cd){
        this.digimon = cd;
    }

    public EvolutionCondition(String cd){
        this.evolution = cd;
    }

    public EvolutionCondition setDigimon(CustomDigimon cd){
        this.digimon = cd;
        return this;
    }

    public LinkedList<EvolutionCondition> conditions = new LinkedList<>();

    public boolean checkConditions(){
        return alwaysTrue || conditions.stream().allMatch(EvolutionCondition::checkConditions);
    }

    public String getEvolution(){
        return evolution;
    }

    public EvolutionCondition setRank(String rank){
        this.rank = rank;
        return this;
    }

    public String getRank(){
        return rank;
    }

    public EvolutionCondition moodCheck(String mood){
        conditions.add(new MoodEvolutionCondition(digimon, mood));
        return this;
    }

    //dragon-0 beast-1 insectplant-2 aquan-3 wind-4 machine-5 earth-6 nightmare-7 holy-8

    public EvolutionCondition xpOver(int xpId, int min){
        conditions.add(new SpecificXpOverCondition(digimon, xpId, min));
        return this;
    }

    public EvolutionCondition maxMistakes(int max){
        conditions.add(new MaxCareMistakesCondition(digimon, max));
        return this;
    }

    public EvolutionCondition minWins(int min){
        conditions.add(new MinWinsCondition(digimon, min));
        return this;
    }

    public EvolutionCondition alwaysCan(){
        this.alwaysTrue = true;
        return this;
    }

    public void renderCondition (GuiGraphics guiGraphics, int x, int y){}

    class SpecificXpOverCondition extends EvolutionCondition {
        private int xpId;
        private int min;
        public SpecificXpOverCondition(CustomDigimon cd, int xpId, int min) {super(cd);this.xpId = xpId;this.min = min;}

        @Override
        public boolean checkConditions(){return digimon.getSpecificGainedXps(xpId) >= min;}

        @Override
        public void renderCondition(GuiGraphics guiGraphics, int x, int y) {
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_xp.png"),
                    x,y,0,0,69,11, 69,11);
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/item/"+ StatsGui.getXpItem(xpId) +".png"),
                    x+2,y,0,0,12,12, 12,12);
            guiGraphics.drawString(Minecraft.getInstance().font, ">"+Integer.toString(min), x+16, y+3,CommonColors.WHITE);
        }
    }

    class MoodEvolutionCondition extends EvolutionCondition {
        private String mood;
        public MoodEvolutionCondition(CustomDigimon cd, String mood) {super(cd);this.mood = mood;}

        @Override
        public boolean checkConditions(){return digimon.moodManager.getMood().equals(mood);}

        @Override
        public void renderCondition(GuiGraphics guiGraphics, int x, int y) {
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_mood.png"),
                    x,y,0,0,69,11, 69,11);
            guiGraphics.drawString(Minecraft.getInstance().font, mood, x+30, y+3, digimon.moodManager.getMoodColor(mood));
        }
    }

    class MaxCareMistakesCondition extends EvolutionCondition {
        private int max;
        public MaxCareMistakesCondition(CustomDigimon cd, int max) {super(cd);this.max = max;}

        @Override
        public boolean checkConditions(){return digimon.getCareMistakesStat() <= max;}

        @Override
        public void renderCondition(GuiGraphics guiGraphics, int x, int y) {
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_mistakes.png"),
                    x,y,0,0,69,11, 69,11);
            guiGraphics.drawString(Minecraft.getInstance().font, (max == 0 ?"=":"<")+Integer.toString(max), x+45, y+3,CommonColors.WHITE);
        }
    }

    class MinWinsCondition extends EvolutionCondition {
        private int min;
        public MinWinsCondition(CustomDigimon cd, int min) {super(cd);this.min = min;}

        @Override
        public boolean checkConditions(){return digimon.getBattlesStat() >= min;}

        @Override
        public void renderCondition(GuiGraphics guiGraphics, int x, int y) {
            guiGraphics.blit(new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/condition_wins.png"),
                    x,y,0,0,69,11, 69,11);
            guiGraphics.drawString(Minecraft.getInstance().font, ">"+Integer.toString(min), x+25, y+3,CommonColors.WHITE);
        }
    }
}



