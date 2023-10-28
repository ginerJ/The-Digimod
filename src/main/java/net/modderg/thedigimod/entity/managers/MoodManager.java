package net.modderg.thedigimod.entity.managers;

import net.minecraft.network.syncher.EntityDataSerializers;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;

public class MoodManager {

    private CustomDigimon digimon;

    public MoodManager (CustomDigimon cd){
        this.digimon = cd;
    }

    protected static final EntityDataAccessor<Integer> MOODPOINTS = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> getMoodAccessor(){
        return MOODPOINTS;
    }

    public void setMoodPoints(int i){
        digimon.getEntityData().set(MOODPOINTS, i);
    }
    public int getMoodPoints(){
        return digimon.getEntityData().get(MOODPOINTS);
    }
    public void restMoodPoints(int i){
        boolean mistake = getMoodPoints() > 10;
        digimon.getEntityData().set(MOODPOINTS, Math.max(this.getMoodPoints() - i,0));
        if (mistake && this.getMood().equals("Depressed")){
            digimon.setCareMistakesStat(digimon.getCareMistakesStat() + 1);
        }
    }
    public void addMoodPoints(int i){
        digimon.getEntityData().set(MOODPOINTS, Math.min(this.getMoodPoints() + i,250));
    }

    public String getMood(){
        if(getMoodPoints() > 200){
            return "Joyful";
        } else if (getMoodPoints() > 150){
            return "Happy";
        } else if (getMoodPoints() > 100){
            return "Meh";
        } else if (getMoodPoints() > 10){
            return "Sad";
        }
        return "Depressed";
    }
    public int getMoodColor() {
        int moodPoints = getMoodPoints();
        if (moodPoints > 200) return 16761177;
        if (moodPoints > 150) return 16777088;
        if (moodPoints > 100) return 16646143;
        if (moodPoints > 50) return 10262007;
        return 6579711;
    }

    public int getMoodColor(String mood) {
        if (mood.equals("Joyful")) return 16761177;
        if (mood.equals("Happy")) return 16777088;
        if (mood.equals("Meh")) return 16646143;
        if (mood.equals("Sad")) return 10262007;
        return 6579711;
    }
}
