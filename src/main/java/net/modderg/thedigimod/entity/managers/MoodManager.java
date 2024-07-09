package net.modderg.thedigimod.entity.managers;

import net.minecraft.network.syncher.EntityDataSerializers;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.modderg.thedigimod.particles.DigitalParticles;

public class MoodManager {

    private CustomDigimon digimon;

    public MoodManager (CustomDigimon cd){
        this.digimon = cd;
    }

    public static final EntityDataAccessor<Integer> MOODPOINTS = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);

    public void setMoodPoints(int i){
        digimon.getEntityData().set(MOODPOINTS, i);
    }

    public int getMoodPoints(){
        return digimon.getEntityData().get(MOODPOINTS);
    }

    public void restMoodPoints(int i){
        digimon.getEntityData().set(MOODPOINTS, Math.max(this.getMoodPoints() - i,0));

        if (this.getMood().equals("Depressed"))
            digimon.setCareMistakesStat(digimon.getCareMistakesStat() + 1);
    }

    public void addMoodPoints(int i){
        setMoodPoints(Math.min(this.getMoodPoints() + i,350));
    }

    public String getMood(){
        int mood = getMoodPoints();

        if(mood > 300)
            return "Joyful";
        else if (mood > 200)
            return "Happy";
        else if (getMoodPoints() > 100)
            return "Meh";
        else if (getMoodPoints() > 10)
            return "Sad";

        return "Depressed";
    }
    public int getMoodColor() {
        int moodPoints = getMoodPoints();
        if (moodPoints > 300) return 16761177;
        if (moodPoints > 200) return 16777088;
        if (moodPoints > 100) return 16646143;
        if (moodPoints > 10) return 10262007;
        return 6579711;
    }

    public int getMoodColor(String mood) {
        if (mood.equals("Joyful")) return 16761177;
        if (mood.equals("Happy")) return 16777088;
        if (mood.equals("Meh")) return 16646143;
        if (mood.equals("Sad")) return 10262007;
        return 6579711;
    }

    public void spawnMoodParticle(){
        switch (getMood()) {
            case "Joyful":
                digimon.particleManager.spawnBubbleParticle(DigitalParticles.JOYFUL_BUBBLE, digimon);
                break;
            case "Happy":
                digimon.particleManager.spawnBubbleParticle(DigitalParticles.HAPPY_BUBBLE, digimon);
                break;
            case "Meh":
                digimon.particleManager.spawnBubbleParticle(DigitalParticles.MEH_BUBBLE, digimon);
                break;
            case "Sad":
                digimon.particleManager.spawnBubbleParticle(DigitalParticles.SAD_BUBBLE, digimon);
                break;
            case "Depressed":
                digimon.particleManager.spawnBubbleParticle(DigitalParticles.MISTAKE_BUBBLE, digimon);
        }
    }
}
