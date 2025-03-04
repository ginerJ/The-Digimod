package net.modderg.thedigimod.server.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;

public class DMBiomeModifier {

    public static void register(IEventBus bus) {
        BIOME_MODIFIERS.register(bus);
    }

    public static DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, TheDigiMod.MOD_ID);

    public static final RegistryObject<Codec<AddTagSpawnsBiomeModifier>> TAG_BIOME_MODIFIER_CODEC =
            BIOME_MODIFIERS.register("add_tag_spawns",
                    () -> RecordCodecBuilder.create(instance -> instance.group(
                            Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddTagSpawnsBiomeModifier::biomes),
                            Codec.INT.fieldOf("weight").forGetter(AddTagSpawnsBiomeModifier::weight),
                            Codec.INT.fieldOf("minCount").forGetter(AddTagSpawnsBiomeModifier::minCount),
                            Codec.INT.fieldOf("maxCount").forGetter(AddTagSpawnsBiomeModifier::maxCount),
                            TagKey.codec(ForgeRegistries.Keys.ENTITY_TYPES).fieldOf("entity_tag").forGetter(AddTagSpawnsBiomeModifier::entityTag)
                    ).apply(instance, AddTagSpawnsBiomeModifier::new))
            );
}
