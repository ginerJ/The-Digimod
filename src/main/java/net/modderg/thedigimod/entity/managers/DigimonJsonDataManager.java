package net.modderg.thedigimod.entity.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.goals.*;

import java.util.Objects;
import java.util.stream.IntStream;

public class DigimonJsonDataManager {

    private String profession;

    public String getProfession() {
        return profession;
    }

    public void applyJsonData(JsonElement jsonElement, CustomDigimon digimon) {
        if (jsonElement != null) {

            JsonObject json = jsonElement.getAsJsonObject();

            if(json.has("evolutions")){

                JsonObject evolutions = json.getAsJsonObject("evolutions");

                EvolutionCondition[] conds = evolutions.keySet().stream().map(name -> getEvoConditionFronJson(
                                evolutions.getAsJsonObject(name).getAsJsonObject("conditions"), name, digimon))
                        .toList().toArray(new EvolutionCondition[0]);

                IntStream.range(0, conds.length).forEach(i -> {
                    conds[i].setDigimon(digimon);
                    digimon.evolutionConditions[i] = conds[i];
                });
            }

            if (json.has("profession")) {
                profession = json.getAsJsonPrimitive("profession").getAsString();

                digimon.profession = profession;

                if (Objects.equals(profession, "lumberjack"))
                    digimon.goalSelector.addGoal(8, new ProfessionLumberjackGoal(digimon));

                if (Objects.equals(profession, "transporter"))
                    digimon.goalSelector.addGoal(8, new ProfessionTransporterGoal(digimon));

                if (Objects.equals(profession, "farmer"))
                    digimon.goalSelector.addGoal(8, new ProfessionFarmerGoal(digimon));

                if (Objects.equals(profession, "tree_lover"))
                    digimon.goalSelector.addGoal(8, new ProfessionTreeGrower(digimon));

                if (Objects.equals(profession, "miner"))
                    digimon.goalSelector.addGoal(8, new ProfessionMinerGoal(digimon));
            }
        }
    }

    public EvolutionCondition getEvoConditionFronJson(JsonObject json, String name, CustomDigimon digimon) {
        EvolutionCondition evo = new EvolutionCondition(name).setDigimon(digimon);

        if(json.has("rank"))
            evo.setRank(json.getAsJsonPrimitive("rank").getAsString());

        if (json.getAsJsonPrimitive("always_can").getAsBoolean())
            return evo.alwaysCan();

        if (!json.getAsJsonPrimitive("mood").getAsString().isEmpty())
            evo.moodCheck(json.getAsJsonPrimitive("mood").getAsString());

        if (!json.getAsJsonPrimitive("max_mistakes").getAsString().isEmpty())
            evo.maxMistakes(json.getAsJsonPrimitive("max_mistakes").getAsInt());

        if (!json.getAsJsonPrimitive("min_wins").getAsString().isEmpty())
            evo.minWins(json.getAsJsonPrimitive("min_wins").getAsInt());

        if (!json.getAsJsonArray("xp").isEmpty())
            for (JsonElement element : json.getAsJsonArray("xp")) {
                String[] xp = element.getAsString().split(":");
                evo.xpOver(Integer.parseInt(xp[0]), Integer.parseInt(xp[1]));
            }

        return evo;
    }

}
