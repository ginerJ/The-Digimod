package net.modderg.thedigimod.server.entity;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.TDEntitiesJsonLoad;
import net.modderg.thedigimod.server.item.custom.BabyDigimonItem;
import oshi.util.tuples.Pair;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unused","SpellCheckingInspection"})
public class TDEntities {

    public static void register(IEventBus bus) {
        DIGIMONS.register(bus);
        BABIES.register(bus);
        registerEntitiesFromJsonData();
    }

    public static Set<RegistryObject<EntityType<DigimonEntity>>> AQUATIC_DIGIMON = new HashSet<>();

    public static DeferredRegister<EntityType<?>> DIGIMONS = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TheDigiMod.MOD_ID);

    public static final DeferredRegister<Item> BABIES = DeferredRegister.create(ForgeRegistries.ITEMS, TheDigiMod.MOD_ID);

    public static void registerEntitiesFromJsonData() {
        for (Pair<String, JsonObject> entry : TDEntitiesJsonLoad.getAllDigimonJsons()) {

            String digimonName = entry.getA();
            JsonObject data = entry.getB();

            String movType = data.get("movement_type").getAsString();

            JsonObject dimensions = data.get("hitbox").getAsJsonObject();
            float width = dimensions.get("width").getAsFloat();
            float height = dimensions.get("height").getAsFloat();

            boolean firepassive = data.has("passive") && data.get("passive").getAsString().equals("fire_immune");

            EntityType.Builder<DigimonEntity> builder = EntityType.Builder.of(
                    (type, world) -> createDigimonWithJsonData(type, world, movType, data),
                    MobCategory.CREATURE);

            RegistryObject<EntityType<DigimonEntity>> registeredDigimon = DIGIMONS.register(
                    digimonName,
                    () -> (firepassive ? builder.fireImmune() : builder)
                            .sized(width, height)
                            .build(new ResourceLocation(TheDigiMod.MOD_ID, digimonName).toString()));

            if (movType.equals("swimmer"))
                AQUATIC_DIGIMON.add(registeredDigimon);

            if(data.get("evo_stage").getAsInt() == 0)
                BABIES.register(digimonName, () -> new BabyDigimonItem(new Item.Properties().stacksTo(16).fireResistant()));
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