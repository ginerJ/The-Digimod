package net.modderg.thedigimod.server.entity.managers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.modderg.thedigimod.client.particles.DigitalParticles;

public class MoodManager {

    private DigimonEntity digimon;

    public MoodManager (DigimonEntity cd){
        this.digimon = cd;
    }

    public static final EntityDataAccessor<Integer> MOODPOINTS = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT);

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

    protected Style XpStyle = Style.EMPTY;

    public MutableComponent getMoodComponent(){

        MutableComponent moodComp = Component.literal(this.getMood())
                .setStyle(XpStyle.withColor(TextColor.fromRgb(getMoodColor(this.getMood()))));

        if (digimon.getDirtyCounter() > 0){
            moodComp.append(Component.literal(" & ").setStyle(
                    XpStyle.withColor(ChatFormatting.WHITE)));
            if(digimon.getDirtyCounter() == 1)
                moodComp.append(Component.literal("Dirty")
                        .setStyle(XpStyle.withColor(TextColor.fromRgb(0xf294b8))));
            else if(digimon.getDirtyCounter() == 2)
                moodComp.append(Component.literal("Very Dirty")
                        .setStyle(XpStyle.withColor(TextColor.fromRgb(0xf294b8))));
            else if(digimon.getDirtyCounter() > 2)
                moodComp.append(Component.literal("Filthy")
                        .setStyle(XpStyle.withColor(TextColor.fromRgb(0xf294b8))));
        }

        return moodComp;
    }

    public String getMood() {
        if(getMoodPoints() > 300)
            return "Joyful";
        else if (getMoodPoints() > 200)
            return "Happy";
        else if (getMoodPoints() > 100)
            return "Meh";
        else if (getMoodPoints() > 10)
            return "Sad";
        return "Depressed";
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
