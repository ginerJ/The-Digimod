package net.modderg.thedigimod.entity.managers;
import net.modderg.thedigimod.entity.CustomDigimon;

import java.util.LinkedList;

public class EvolutionCondition {

    private CustomDigimon digimon = null;
    private boolean alwaysTrue = false;

    public EvolutionCondition(CustomDigimon cd){
        this.digimon = cd;
    }

    private LinkedList<EvolutionCondition> conditions = new LinkedList<>();

    public boolean checkConditions(){
        return alwaysTrue || conditions.stream().allMatch(EvolutionCondition::checkConditions);
    }

    public EvolutionCondition moodCheck(String mood){
        conditions.add(new MoodEvolutionCondition(digimon, mood));
        return this;
    }

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

    public EvolutionCondition alwaysTrue(){
        this.alwaysTrue = true;
        return this;
    }

    class MoodEvolutionCondition extends EvolutionCondition {
        private String mood;
        public MoodEvolutionCondition(CustomDigimon cd, String mood) {super(cd);this.mood = mood;}

        @Override
        public boolean checkConditions(){return digimon.moodManager.getMood().equals(mood);}
    }

    class SpecificXpOverCondition extends EvolutionCondition {
        private int xpId;
        private int min;
        public SpecificXpOverCondition(CustomDigimon cd, int xpId, int min) {super(cd);this.xpId = xpId;this.min = min;}

        @Override
        public boolean checkConditions(){return digimon.getSpecificXps(xpId) >= min;}
    }

    class MaxCareMistakesCondition extends EvolutionCondition {
        private int max;
        public MaxCareMistakesCondition(CustomDigimon cd, int max) {super(cd);this.max = max;}

        @Override
        public boolean checkConditions(){return digimon.getCareMistakesStat() <= max;}
    }

    class MinWinsCondition extends EvolutionCondition {
        private int min;
        public MinWinsCondition(CustomDigimon cd, int min) {super(cd);this.min = min;}

        @Override
        public boolean checkConditions(){return digimon.getBattlesStat() >= min;}
    }
}



