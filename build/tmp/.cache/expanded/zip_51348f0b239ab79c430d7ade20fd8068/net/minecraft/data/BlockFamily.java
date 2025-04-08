package net.minecraft.data;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

public class BlockFamily {
   private final Block baseBlock;
   final Map<BlockFamily.Variant, Block> variants = Maps.newHashMap();
   FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;
   boolean generateModel = true;
   boolean generateRecipe = true;
   @Nullable
   String recipeGroupPrefix;
   @Nullable
   String recipeUnlockedBy;

   BlockFamily(Block p_175950_) {
      this.baseBlock = p_175950_;
   }

   public Block getBaseBlock() {
      return this.baseBlock;
   }

   public Map<BlockFamily.Variant, Block> getVariants() {
      return this.variants;
   }

   public Block get(BlockFamily.Variant p_175953_) {
      return this.variants.get(p_175953_);
   }

   public boolean shouldGenerateModel() {
      return this.generateModel;
   }

   public boolean shouldGenerateRecipe(FeatureFlagSet p_250218_) {
      return this.generateRecipe && this.requiredFeatures.isSubsetOf(p_250218_);
   }

   public Optional<String> getRecipeGroupPrefix() {
      return Util.isBlank(this.recipeGroupPrefix) ? Optional.empty() : Optional.of(this.recipeGroupPrefix);
   }

   public Optional<String> getRecipeUnlockedBy() {
      return Util.isBlank(this.recipeUnlockedBy) ? Optional.empty() : Optional.of(this.recipeUnlockedBy);
   }

   public static class Builder {
      private final BlockFamily family;

      public Builder(Block p_175961_) {
         this.family = new BlockFamily(p_175961_);
      }

      public BlockFamily getFamily() {
         return this.family;
      }

      public BlockFamily.Builder button(Block p_175964_) {
         this.family.variants.put(BlockFamily.Variant.BUTTON, p_175964_);
         return this;
      }

      public BlockFamily.Builder chiseled(Block p_175972_) {
         this.family.variants.put(BlockFamily.Variant.CHISELED, p_175972_);
         return this;
      }

      public BlockFamily.Builder mosaic(Block p_251947_) {
         this.family.variants.put(BlockFamily.Variant.MOSAIC, p_251947_);
         return this;
      }

      public BlockFamily.Builder cracked(Block p_175977_) {
         this.family.variants.put(BlockFamily.Variant.CRACKED, p_175977_);
         return this;
      }

      public BlockFamily.Builder cut(Block p_175979_) {
         this.family.variants.put(BlockFamily.Variant.CUT, p_175979_);
         return this;
      }

      public BlockFamily.Builder door(Block p_175981_) {
         this.family.variants.put(BlockFamily.Variant.DOOR, p_175981_);
         return this;
      }

      public BlockFamily.Builder customFence(Block p_248790_) {
         this.family.variants.put(BlockFamily.Variant.CUSTOM_FENCE, p_248790_);
         return this;
      }

      public BlockFamily.Builder fence(Block p_175983_) {
         this.family.variants.put(BlockFamily.Variant.FENCE, p_175983_);
         return this;
      }

      public BlockFamily.Builder customFenceGate(Block p_251301_) {
         this.family.variants.put(BlockFamily.Variant.CUSTOM_FENCE_GATE, p_251301_);
         return this;
      }

      public BlockFamily.Builder fenceGate(Block p_175985_) {
         this.family.variants.put(BlockFamily.Variant.FENCE_GATE, p_175985_);
         return this;
      }

      public BlockFamily.Builder sign(Block p_175966_, Block p_175967_) {
         this.family.variants.put(BlockFamily.Variant.SIGN, p_175966_);
         this.family.variants.put(BlockFamily.Variant.WALL_SIGN, p_175967_);
         return this;
      }

      public BlockFamily.Builder slab(Block p_175987_) {
         this.family.variants.put(BlockFamily.Variant.SLAB, p_175987_);
         return this;
      }

      public BlockFamily.Builder stairs(Block p_175989_) {
         this.family.variants.put(BlockFamily.Variant.STAIRS, p_175989_);
         return this;
      }

      public BlockFamily.Builder pressurePlate(Block p_175991_) {
         this.family.variants.put(BlockFamily.Variant.PRESSURE_PLATE, p_175991_);
         return this;
      }

      public BlockFamily.Builder polished(Block p_175993_) {
         this.family.variants.put(BlockFamily.Variant.POLISHED, p_175993_);
         return this;
      }

      public BlockFamily.Builder trapdoor(Block p_175995_) {
         this.family.variants.put(BlockFamily.Variant.TRAPDOOR, p_175995_);
         return this;
      }

      public BlockFamily.Builder wall(Block p_175997_) {
         this.family.variants.put(BlockFamily.Variant.WALL, p_175997_);
         return this;
      }

      public BlockFamily.Builder dontGenerateModel() {
         this.family.generateModel = false;
         return this;
      }

      public BlockFamily.Builder dontGenerateRecipe() {
         this.family.generateRecipe = false;
         return this;
      }

      public BlockFamily.Builder featureLockedBehind(FeatureFlag... p_250956_) {
         this.family.requiredFeatures = FeatureFlags.REGISTRY.subset(p_250956_);
         return this;
      }

      public BlockFamily.Builder recipeGroupPrefix(String p_175969_) {
         this.family.recipeGroupPrefix = p_175969_;
         return this;
      }

      public BlockFamily.Builder recipeUnlockedBy(String p_175974_) {
         this.family.recipeUnlockedBy = p_175974_;
         return this;
      }
   }

   public static enum Variant {
      BUTTON("button"),
      CHISELED("chiseled"),
      CRACKED("cracked"),
      CUT("cut"),
      DOOR("door"),
      CUSTOM_FENCE("custom_fence"),
      FENCE("fence"),
      CUSTOM_FENCE_GATE("custom_fence_gate"),
      FENCE_GATE("fence_gate"),
      MOSAIC("mosaic"),
      SIGN("sign"),
      SLAB("slab"),
      STAIRS("stairs"),
      PRESSURE_PLATE("pressure_plate"),
      POLISHED("polished"),
      TRAPDOOR("trapdoor"),
      WALL("wall"),
      WALL_SIGN("wall_sign");

      private final String name;

      private Variant(String p_176019_) {
         this.name = p_176019_;
      }

      public String getName() {
         return this.name;
      }
   }
}