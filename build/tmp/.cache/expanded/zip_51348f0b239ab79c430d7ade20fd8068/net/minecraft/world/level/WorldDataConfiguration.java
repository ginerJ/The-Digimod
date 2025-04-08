package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public record WorldDataConfiguration(DataPackConfig dataPacks, FeatureFlagSet enabledFeatures) {
   public static final String ENABLED_FEATURES_ID = "enabled_features";
   public static final Codec<WorldDataConfiguration> CODEC = RecordCodecBuilder.create((p_249474_) -> {
      return p_249474_.group(DataPackConfig.CODEC.optionalFieldOf("DataPacks", DataPackConfig.DEFAULT).forGetter(WorldDataConfiguration::dataPacks), FeatureFlags.CODEC.optionalFieldOf("enabled_features", FeatureFlags.DEFAULT_FLAGS).forGetter(WorldDataConfiguration::enabledFeatures)).apply(p_249474_, WorldDataConfiguration::new);
   });
   public static final WorldDataConfiguration DEFAULT = new WorldDataConfiguration(DataPackConfig.DEFAULT, FeatureFlags.DEFAULT_FLAGS);

   public WorldDataConfiguration expandFeatures(FeatureFlagSet p_249090_) {
      return new WorldDataConfiguration(this.dataPacks, this.enabledFeatures.join(p_249090_));
   }
}