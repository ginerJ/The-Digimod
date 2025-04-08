package net.minecraft.world;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

public class RandomSequences extends SavedData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final long seed;
   private final Map<ResourceLocation, RandomSequence> sequences = new Object2ObjectOpenHashMap<>();

   public RandomSequences(long p_287622_) {
      this.seed = p_287622_;
   }

   public RandomSource get(ResourceLocation p_287751_) {
      final RandomSource randomsource = this.sequences.computeIfAbsent(p_287751_, (p_287666_) -> {
         return new RandomSequence(this.seed, p_287666_);
      }).random();
      return new RandomSource() {
         public RandomSource fork() {
            RandomSequences.this.setDirty();
            return randomsource.fork();
         }

         public PositionalRandomFactory forkPositional() {
            RandomSequences.this.setDirty();
            return randomsource.forkPositional();
         }

         public void setSeed(long p_287659_) {
            RandomSequences.this.setDirty();
            randomsource.setSeed(p_287659_);
         }

         public int nextInt() {
            RandomSequences.this.setDirty();
            return randomsource.nextInt();
         }

         public int nextInt(int p_287717_) {
            RandomSequences.this.setDirty();
            return randomsource.nextInt(p_287717_);
         }

         public long nextLong() {
            RandomSequences.this.setDirty();
            return randomsource.nextLong();
         }

         public boolean nextBoolean() {
            RandomSequences.this.setDirty();
            return randomsource.nextBoolean();
         }

         public float nextFloat() {
            RandomSequences.this.setDirty();
            return randomsource.nextFloat();
         }

         public double nextDouble() {
            RandomSequences.this.setDirty();
            return randomsource.nextDouble();
         }

         public double nextGaussian() {
            RandomSequences.this.setDirty();
            return randomsource.nextGaussian();
         }
      };
   }

   public CompoundTag save(CompoundTag p_287658_) {
      this.sequences.forEach((p_287627_, p_287578_) -> {
         p_287658_.put(p_287627_.toString(), RandomSequence.CODEC.encodeStart(NbtOps.INSTANCE, p_287578_).result().orElseThrow());
      });
      return p_287658_;
   }

   public static RandomSequences load(long p_287756_, CompoundTag p_287587_) {
      RandomSequences randomsequences = new RandomSequences(p_287756_);

      for(String s : p_287587_.getAllKeys()) {
         try {
            RandomSequence randomsequence = RandomSequence.CODEC.decode(NbtOps.INSTANCE, p_287587_.get(s)).result().get().getFirst();
            randomsequences.sequences.put(new ResourceLocation(s), randomsequence);
         } catch (Exception exception) {
            LOGGER.error("Failed to load random sequence {}", s, exception);
         }
      }

      return randomsequences;
   }
}