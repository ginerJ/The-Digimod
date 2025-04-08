package net.minecraft.world.entity.animal;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class TropicalFish extends AbstractSchoolingFish implements VariantHolder<TropicalFish.Pattern> {
   public static final String BUCKET_VARIANT_TAG = "BucketVariantTag";
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(TropicalFish.class, EntityDataSerializers.INT);
   public static final List<TropicalFish.Variant> COMMON_VARIANTS = List.of(new TropicalFish.Variant(TropicalFish.Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), new TropicalFish.Variant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), new TropicalFish.Variant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), new TropicalFish.Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), new TropicalFish.Variant(TropicalFish.Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), new TropicalFish.Variant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE), new TropicalFish.Variant(TropicalFish.Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), new TropicalFish.Variant(TropicalFish.Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), new TropicalFish.Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED), new TropicalFish.Variant(TropicalFish.Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), new TropicalFish.Variant(TropicalFish.Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY), new TropicalFish.Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), new TropicalFish.Variant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK), new TropicalFish.Variant(TropicalFish.Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), new TropicalFish.Variant(TropicalFish.Pattern.BETTY, DyeColor.RED, DyeColor.WHITE), new TropicalFish.Variant(TropicalFish.Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED), new TropicalFish.Variant(TropicalFish.Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), new TropicalFish.Variant(TropicalFish.Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), new TropicalFish.Variant(TropicalFish.Pattern.KOB, DyeColor.RED, DyeColor.WHITE), new TropicalFish.Variant(TropicalFish.Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), new TropicalFish.Variant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW), new TropicalFish.Variant(TropicalFish.Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW));
   private boolean isSchool = true;

   public TropicalFish(EntityType<? extends TropicalFish> p_30015_, Level p_30016_) {
      super(p_30015_, p_30016_);
   }

   public static String getPredefinedName(int p_30031_) {
      return "entity.minecraft.tropical_fish.predefined." + p_30031_;
   }

   static int packVariant(TropicalFish.Pattern p_262598_, DyeColor p_262618_, DyeColor p_262683_) {
      return p_262598_.getPackedId() & '\uffff' | (p_262618_.getId() & 255) << 16 | (p_262683_.getId() & 255) << 24;
   }

   public static DyeColor getBaseColor(int p_30051_) {
      return DyeColor.byId(p_30051_ >> 16 & 255);
   }

   public static DyeColor getPatternColor(int p_30053_) {
      return DyeColor.byId(p_30053_ >> 24 & 255);
   }

   public static TropicalFish.Pattern getPattern(int p_262604_) {
      return TropicalFish.Pattern.byId(p_262604_ & '\uffff');
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundTag p_30033_) {
      super.addAdditionalSaveData(p_30033_);
      p_30033_.putInt("Variant", this.getPackedVariant());
   }

   public void readAdditionalSaveData(CompoundTag p_30029_) {
      super.readAdditionalSaveData(p_30029_);
      this.setPackedVariant(p_30029_.getInt("Variant"));
   }

   private void setPackedVariant(int p_30057_) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, p_30057_);
   }

   public boolean isMaxGroupSizeReached(int p_30035_) {
      return !this.isSchool;
   }

   private int getPackedVariant() {
      return this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public DyeColor getBaseColor() {
      return getBaseColor(this.getPackedVariant());
   }

   public DyeColor getPatternColor() {
      return getPatternColor(this.getPackedVariant());
   }

   public TropicalFish.Pattern getVariant() {
      return getPattern(this.getPackedVariant());
   }

   public void setVariant(TropicalFish.Pattern p_262594_) {
      int i = this.getPackedVariant();
      DyeColor dyecolor = getBaseColor(i);
      DyeColor dyecolor1 = getPatternColor(i);
      this.setPackedVariant(packVariant(p_262594_, dyecolor, dyecolor1));
   }

   public void saveToBucketTag(ItemStack p_30049_) {
      super.saveToBucketTag(p_30049_);
      CompoundTag compoundtag = p_30049_.getOrCreateTag();
      compoundtag.putInt("BucketVariantTag", this.getPackedVariant());
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.TROPICAL_FISH_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.TROPICAL_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.TROPICAL_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_30039_) {
      return SoundEvents.TROPICAL_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.TROPICAL_FISH_FLOP;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_30023_, DifficultyInstance p_30024_, MobSpawnType p_30025_, @Nullable SpawnGroupData p_30026_, @Nullable CompoundTag p_30027_) {
      p_30026_ = super.finalizeSpawn(p_30023_, p_30024_, p_30025_, p_30026_, p_30027_);
      if (p_30025_ == MobSpawnType.BUCKET && p_30027_ != null && p_30027_.contains("BucketVariantTag", 3)) {
         this.setPackedVariant(p_30027_.getInt("BucketVariantTag"));
         return p_30026_;
      } else {
         RandomSource randomsource = p_30023_.getRandom();
         TropicalFish.Variant tropicalfish$variant;
         if (p_30026_ instanceof TropicalFish.TropicalFishGroupData) {
            TropicalFish.TropicalFishGroupData tropicalfish$tropicalfishgroupdata = (TropicalFish.TropicalFishGroupData)p_30026_;
            tropicalfish$variant = tropicalfish$tropicalfishgroupdata.variant;
         } else if ((double)randomsource.nextFloat() < 0.9D) {
            tropicalfish$variant = Util.getRandom(COMMON_VARIANTS, randomsource);
            p_30026_ = new TropicalFish.TropicalFishGroupData(this, tropicalfish$variant);
         } else {
            this.isSchool = false;
            TropicalFish.Pattern[] atropicalfish$pattern = TropicalFish.Pattern.values();
            DyeColor[] adyecolor = DyeColor.values();
            TropicalFish.Pattern tropicalfish$pattern = Util.getRandom(atropicalfish$pattern, randomsource);
            DyeColor dyecolor = Util.getRandom(adyecolor, randomsource);
            DyeColor dyecolor1 = Util.getRandom(adyecolor, randomsource);
            tropicalfish$variant = new TropicalFish.Variant(tropicalfish$pattern, dyecolor, dyecolor1);
         }

         this.setPackedVariant(tropicalfish$variant.getPackedId());
         return p_30026_;
      }
   }

   public static boolean checkTropicalFishSpawnRules(EntityType<TropicalFish> p_218267_, LevelAccessor p_218268_, MobSpawnType p_218269_, BlockPos p_218270_, RandomSource p_218271_) {
      return p_218268_.getFluidState(p_218270_.below()).is(FluidTags.WATER) && p_218268_.getBlockState(p_218270_.above()).is(Blocks.WATER) && (p_218268_.getBiome(p_218270_).is(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT) || WaterAnimal.checkSurfaceWaterAnimalSpawnRules(p_218267_, p_218268_, p_218269_, p_218270_, p_218271_));
   }

   public static enum Base {
      SMALL(0),
      LARGE(1);

      final int id;

      private Base(int p_262648_) {
         this.id = p_262648_;
      }
   }

   public static enum Pattern implements StringRepresentable {
      KOB("kob", TropicalFish.Base.SMALL, 0),
      SUNSTREAK("sunstreak", TropicalFish.Base.SMALL, 1),
      SNOOPER("snooper", TropicalFish.Base.SMALL, 2),
      DASHER("dasher", TropicalFish.Base.SMALL, 3),
      BRINELY("brinely", TropicalFish.Base.SMALL, 4),
      SPOTTY("spotty", TropicalFish.Base.SMALL, 5),
      FLOPPER("flopper", TropicalFish.Base.LARGE, 0),
      STRIPEY("stripey", TropicalFish.Base.LARGE, 1),
      GLITTER("glitter", TropicalFish.Base.LARGE, 2),
      BLOCKFISH("blockfish", TropicalFish.Base.LARGE, 3),
      BETTY("betty", TropicalFish.Base.LARGE, 4),
      CLAYFISH("clayfish", TropicalFish.Base.LARGE, 5);

      public static final Codec<TropicalFish.Pattern> CODEC = StringRepresentable.fromEnum(TropicalFish.Pattern::values);
      private static final IntFunction<TropicalFish.Pattern> BY_ID = ByIdMap.sparse(TropicalFish.Pattern::getPackedId, values(), KOB);
      private final String name;
      private final Component displayName;
      private final TropicalFish.Base base;
      private final int packedId;

      private Pattern(String p_262625_, TropicalFish.Base p_262680_, int p_262584_) {
         this.name = p_262625_;
         this.base = p_262680_;
         this.packedId = p_262680_.id | p_262584_ << 8;
         this.displayName = Component.translatable("entity.minecraft.tropical_fish.type." + this.name);
      }

      public static TropicalFish.Pattern byId(int p_262653_) {
         return BY_ID.apply(p_262653_);
      }

      public TropicalFish.Base base() {
         return this.base;
      }

      public int getPackedId() {
         return this.packedId;
      }

      public String getSerializedName() {
         return this.name;
      }

      public Component displayName() {
         return this.displayName;
      }
   }

   static class TropicalFishGroupData extends AbstractSchoolingFish.SchoolSpawnGroupData {
      final TropicalFish.Variant variant;

      TropicalFishGroupData(TropicalFish p_262626_, TropicalFish.Variant p_262579_) {
         super(p_262626_);
         this.variant = p_262579_;
      }
   }

   public static record Variant(TropicalFish.Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
      public int getPackedId() {
         return TropicalFish.packVariant(this.pattern, this.baseColor, this.patternColor);
      }
   }
}