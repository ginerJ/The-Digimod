package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.dimension.DimensionType;

public record NoiseSettings(int minY, int height, int noiseSizeHorizontal, int noiseSizeVertical) {
   public static final Codec<NoiseSettings> CODEC = RecordCodecBuilder.<NoiseSettings>create((p_64536_) -> {
      return p_64536_.group(Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("min_y").forGetter(NoiseSettings::minY), Codec.intRange(0, DimensionType.Y_SIZE).fieldOf("height").forGetter(NoiseSettings::height), Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseSettings::noiseSizeHorizontal), Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseSettings::noiseSizeVertical)).apply(p_64536_, NoiseSettings::new);
   }).comapFlatMap(NoiseSettings::guardY, Function.identity());
   protected static final NoiseSettings OVERWORLD_NOISE_SETTINGS = create(-64, 384, 1, 2);
   protected static final NoiseSettings NETHER_NOISE_SETTINGS = create(0, 128, 1, 2);
   protected static final NoiseSettings END_NOISE_SETTINGS = create(0, 128, 2, 1);
   protected static final NoiseSettings CAVES_NOISE_SETTINGS = create(-64, 192, 1, 2);
   protected static final NoiseSettings FLOATING_ISLANDS_NOISE_SETTINGS = create(0, 256, 2, 1);

   private static DataResult<NoiseSettings> guardY(NoiseSettings p_158721_) {
      if (p_158721_.minY() + p_158721_.height() > DimensionType.MAX_Y + 1) {
         return DataResult.error(() -> {
            return "min_y + height cannot be higher than: " + (DimensionType.MAX_Y + 1);
         });
      } else if (p_158721_.height() % 16 != 0) {
         return DataResult.error(() -> {
            return "height has to be a multiple of 16";
         });
      } else {
         return p_158721_.minY() % 16 != 0 ? DataResult.error(() -> {
            return "min_y has to be a multiple of 16";
         }) : DataResult.success(p_158721_);
      }
   }

   public static NoiseSettings create(int p_224526_, int p_224527_, int p_224528_, int p_224529_) {
      NoiseSettings noisesettings = new NoiseSettings(p_224526_, p_224527_, p_224528_, p_224529_);
      guardY(noisesettings).error().ifPresent((p_158719_) -> {
         throw new IllegalStateException(p_158719_.message());
      });
      return noisesettings;
   }

   public int getCellHeight() {
      return QuartPos.toBlock(this.noiseSizeVertical());
   }

   public int getCellWidth() {
      return QuartPos.toBlock(this.noiseSizeHorizontal());
   }

   public NoiseSettings clampToHeightAccessor(LevelHeightAccessor p_224531_) {
      int i = Math.max(this.minY, p_224531_.getMinBuildHeight());
      int j = Math.min(this.minY + this.height, p_224531_.getMaxBuildHeight()) - i;
      return new NoiseSettings(i, j, this.noiseSizeHorizontal, this.noiseSizeVertical);
   }
}