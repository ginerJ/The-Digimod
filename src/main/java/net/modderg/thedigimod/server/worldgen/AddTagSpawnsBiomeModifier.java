package net.modderg.thedigimod.server.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.minecraftforge.registries.ForgeRegistries;

public record AddTagSpawnsBiomeModifier(HolderSet<Biome> biomes, int weight, int minCount, int maxCount, TagKey<EntityType<?>> entityTag) implements BiomeModifier {

    @Override
    public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
        if (!(phase == Phase.ADD && this.biomes.contains(biome)))
            return;

        MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();

        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.tags().getTag(this.entityTag)) {
            MobCategory category = entityType.getCategory();

            MobSpawnSettings.SpawnerData spawnerData = new MobSpawnSettings.SpawnerData(entityType, this.weight, this.minCount, this.maxCount);

            spawns.addSpawn(category, spawnerData);
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec()
    {
        return ForgeMod.ADD_SPAWNS_BIOME_MODIFIER_TYPE.get();
    }
}