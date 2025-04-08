package net.modderg.thedigimod.server.entity.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.entity.goals.*;
import net.modderg.thedigimod.server.item.diets.DietInit;

import java.util.Arrays;
import java.util.stream.IntStream;

public class DigimonJsonDataManager {

    private String profession;

    public String getProfession() {
        return profession;
    }

    public void applyJsonData(JsonElement jsonElement, DigimonEntity digimon) {
        if (jsonElement != null) {

            JsonObject json = jsonElement.getAsJsonObject();

            if(json.has("has_emissive_texture"))
                digimon.isEmissive = json.getAsJsonPrimitive("has_emissive_texture").getAsBoolean();

            if(json.has("evo_stage")){
                int stage = json.getAsJsonPrimitive("evo_stage").getAsInt();
                digimon.setEvoStage(stage);
            }

            if(json.has("rider_offset"))
                digimon.setMountDigimon(json.getAsJsonPrimitive("rider_offset").getAsFloat());

            if(json.has("xps")){

                JsonArray array = json.getAsJsonArray("xps");

                int[] xps = new int[array.size()];
                IntStream.range(0, array.size()).forEach(i -> xps[i] = array.get(i).getAsInt());
                digimon.setXpDrop(xps);
            }

            if(json.has("anims")){

                JsonArray anims = json.getAsJsonArray("anims");

                digimon.setAnimations(
                        anims.get(0).getAsString(),
                        anims.get(1).getAsString(),
                        anims.get(2).getAsString(),
                        anims.get(3).getAsString(),
                        anims.get(4).getAsString(),
                        anims.get(5).getAsString()
                );
            }

            if(json.has("digitron")){
                JsonPrimitive digitron = json.getAsJsonPrimitive("digitron");
                digimon.setDigitronEvo(digitron.getAsString());
            }

            if(json.has("evolutions")){

                JsonObject evolutions = json.getAsJsonObject("evolutions");

                EvolutionCondition[] conds = evolutions.keySet().stream().map(name -> getEvConditionFromJson(
                                evolutions.getAsJsonObject(name).getAsJsonObject("conditions"), name, digimon))
                        .toList().toArray(new EvolutionCondition[0]);

                digimon.evolutionConditions = Arrays.stream(conds).peek(cond -> cond.setDigimon(digimon))
                        .toArray(EvolutionCondition[]::new);
            }

            if (json.has("profession")) {
                profession = json.getAsJsonPrimitive("profession").getAsString();
                digimon.profession = profession;

                switch (profession) {
                    case "lumberjack" -> digimon.goalSelector.addGoal(8, new ProfessionLumberjackGoal(digimon));
                    case "transporter" -> digimon.goalSelector.addGoal(8, new ProfessionTransporterGoal(digimon));
                    case "farmer" -> digimon.goalSelector.addGoal(8, new ProfessionFarmerGoal(digimon));
                    case "tree_lover" -> digimon.goalSelector.addGoal(8, new ProfessionTreeGrower(digimon));
                    case "miner" -> digimon.goalSelector.addGoal(8, new ProfessionMinerGoal(digimon));
                }
            }

            if(json.has("diet"))
                digimon.setDiet(DietInit.getDiet(json.getAsJsonPrimitive("diet").getAsString()));

            if(json.has("default_sp_move") && digimon.getSpMoveName().equals("unnamed"))
                digimon.setSpMoveName(json.getAsJsonPrimitive("default_sp_move").getAsString());

            if(digimon.getDiet()==DietInit.CRAP_DIET)
                digimon.goalSelector.addGoal(8, new EatShitGoal(digimon));

            if(json.has("baby_digimon"))
                digimon.setBabyDrops(json.getAsJsonArray("baby_digimon").asList().stream().map(JsonElement::getAsString).toArray(String[]::new));
        }
    }

    public EvolutionCondition getEvConditionFromJson(JsonObject json, String name, DigimonEntity digimon) {
        EvolutionCondition evo = new EvolutionCondition(name).setDigimon(digimon);

        if(json.has("rank"))
            evo.setRank(json.getAsJsonPrimitive("rank").getAsString());

        if (json.has("always_can") && json.getAsJsonPrimitive("always_can").getAsBoolean())
            return evo.alwaysCan();

        if (json.has("mood"))
            evo.moodCheck(json.getAsJsonPrimitive("mood").getAsString());

        if (json.has("max_mistakes"))
            evo.maxMistakes(json.getAsJsonPrimitive("max_mistakes").getAsInt());

        if (json.has("min_wins"))
            evo.minWins(json.getAsJsonPrimitive("min_wins").getAsInt());

        if (json.has("item")){
            JsonObject obj = json.getAsJsonObject("item");
            obj.keySet().forEach(key ->
                    evo.itemAmount(ForgeRegistries.ITEMS.getValue(new ResourceLocation(key)), obj.getAsJsonPrimitive(key).getAsInt()));
        }

        if (json.has("xp"))
            for (JsonElement element : json.getAsJsonArray("xp")) {
                String[] xp = element.getAsString().split(":");
                evo.xpOver(Integer.parseInt(xp[0]), Integer.parseInt(xp[1]));
            }

        return evo;
    }

}
