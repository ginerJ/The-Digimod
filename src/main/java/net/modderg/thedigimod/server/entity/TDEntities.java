package net.modderg.thedigimod.server.entity;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.TDEntitiesJsonLoad;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings({"unused","SpellCheckingInspection"})
public class TDEntities {

    static {
        TDEntitiesJsonLoad.loadDigimonData();
    }

    public static void register(IEventBus bus) {
        DIGIMONS.register(bus);
        registerEntitesFromJsonData();
    }

    public static Set<RegistryObject<EntityType<DigimonEntity>>> AQUATIC_DIGIMON = new HashSet<>();

    public static DeferredRegister<EntityType<?>> DIGIMONS = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TheDigiMod.MOD_ID);

    public static void registerEntitesFromJsonData(){
        for(String digimonName: TDEntitiesJsonLoad.getDigimonData().keySet()){

            JsonObject data = TDEntitiesJsonLoad.getDigimonData(digimonName);

            String movType = data.get("movement_type").getAsString();

            JsonObject dimensions = data.get("hitbox").getAsJsonObject();
            float width = dimensions.get("width").getAsJsonPrimitive().getAsFloat();
            float height = dimensions.get("height").getAsJsonPrimitive().getAsFloat();

            RegistryObject<EntityType<DigimonEntity>> registeredDigimon = DIGIMONS.register(digimonName,
                    () -> EntityType.Builder.<DigimonEntity>of((type, world) ->
                            createDigimonWithJsonData(type, world, movType, data)
                            , MobCategory.CREATURE)
                    .sized(width, height)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, digimonName).toString()));

            if (movType.equals("swimmer"))
                AQUATIC_DIGIMON.add(registeredDigimon);
        }
    }

    public static DigimonEntity createDigimonWithJsonData(EntityType<DigimonEntity> type, Level world, String movType, JsonObject data){

        DigimonEntity digimon = (movType.equals("swimmer") ? new SwimmerDigimonEntity(type, world)
                : (movType.equals("flyer") ? new FlyingDigimonEntity(type, world)
                : new DigimonEntity(type, world)));

        digimon.jsonManager.applyJsonData(data, digimon);

        return digimon;
    }
}