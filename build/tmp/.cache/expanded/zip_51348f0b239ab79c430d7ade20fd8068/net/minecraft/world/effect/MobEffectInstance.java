package net.minecraft.world.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

public class MobEffectInstance implements Comparable<MobEffectInstance>, net.minecraftforge.common.extensions.IForgeMobEffectInstance {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int INFINITE_DURATION = -1;
   private final MobEffect effect;
   private int duration;
   private int amplifier;
   private boolean ambient;
   private boolean visible;
   private boolean showIcon;
   @Nullable
   private MobEffectInstance hiddenEffect;
   private final Optional<MobEffectInstance.FactorData> factorData;

   public MobEffectInstance(MobEffect p_19513_) {
      this(p_19513_, 0, 0);
   }

   public MobEffectInstance(MobEffect p_19515_, int p_19516_) {
      this(p_19515_, p_19516_, 0);
   }

   public MobEffectInstance(MobEffect p_19518_, int p_19519_, int p_19520_) {
      this(p_19518_, p_19519_, p_19520_, false, true);
   }

   public MobEffectInstance(MobEffect p_19522_, int p_19523_, int p_19524_, boolean p_19525_, boolean p_19526_) {
      this(p_19522_, p_19523_, p_19524_, p_19525_, p_19526_, p_19526_);
   }

   public MobEffectInstance(MobEffect p_19528_, int p_19529_, int p_19530_, boolean p_19531_, boolean p_19532_, boolean p_19533_) {
      this(p_19528_, p_19529_, p_19530_, p_19531_, p_19532_, p_19533_, (MobEffectInstance)null, p_19528_.createFactorData());
   }

   public MobEffectInstance(MobEffect p_216887_, int p_216888_, int p_216889_, boolean p_216890_, boolean p_216891_, boolean p_216892_, @Nullable MobEffectInstance p_216893_, Optional<MobEffectInstance.FactorData> p_216894_) {
      this.effect = p_216887_;
      this.duration = p_216888_;
      this.amplifier = p_216889_;
      this.ambient = p_216890_;
      this.visible = p_216891_;
      this.showIcon = p_216892_;
      this.hiddenEffect = p_216893_;
      this.factorData = p_216894_;
   }

   public MobEffectInstance(MobEffectInstance p_19543_) {
      this.effect = p_19543_.effect;
      this.factorData = this.effect.createFactorData();
      this.setDetailsFrom(p_19543_);
   }

   public Optional<MobEffectInstance.FactorData> getFactorData() {
      return this.factorData;
   }

   void setDetailsFrom(MobEffectInstance p_19549_) {
      this.duration = p_19549_.duration;
      this.amplifier = p_19549_.amplifier;
      this.ambient = p_19549_.ambient;
      this.visible = p_19549_.visible;
      this.showIcon = p_19549_.showIcon;
      this.curativeItems = p_19549_.curativeItems == null ? null : new java.util.ArrayList<net.minecraft.world.item.ItemStack>(p_19549_.curativeItems);
   }

   public boolean update(MobEffectInstance p_19559_) {
      if (this.effect != p_19559_.effect) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      int i = this.duration;
      boolean flag = false;
      if (p_19559_.amplifier > this.amplifier) {
         if (p_19559_.isShorterDurationThan(this)) {
            MobEffectInstance mobeffectinstance = this.hiddenEffect;
            this.hiddenEffect = new MobEffectInstance(this);
            this.hiddenEffect.hiddenEffect = mobeffectinstance;
         }

         this.amplifier = p_19559_.amplifier;
         this.duration = p_19559_.duration;
         flag = true;
      } else if (this.isShorterDurationThan(p_19559_)) {
         if (p_19559_.amplifier == this.amplifier) {
            this.duration = p_19559_.duration;
            flag = true;
         } else if (this.hiddenEffect == null) {
            this.hiddenEffect = new MobEffectInstance(p_19559_);
         } else {
            this.hiddenEffect.update(p_19559_);
         }
      }

      if (!p_19559_.ambient && this.ambient || flag) {
         this.ambient = p_19559_.ambient;
         flag = true;
      }

      if (p_19559_.visible != this.visible) {
         this.visible = p_19559_.visible;
         flag = true;
      }

      if (p_19559_.showIcon != this.showIcon) {
         this.showIcon = p_19559_.showIcon;
         flag = true;
      }

      return flag;
   }

   private boolean isShorterDurationThan(MobEffectInstance p_268133_) {
      return !this.isInfiniteDuration() && (this.duration < p_268133_.duration || p_268133_.isInfiniteDuration());
   }

   public boolean isInfiniteDuration() {
      return this.duration == -1;
   }

   public boolean endsWithin(int p_268088_) {
      return !this.isInfiniteDuration() && this.duration <= p_268088_;
   }

   public int mapDuration(Int2IntFunction p_268089_) {
      return !this.isInfiniteDuration() && this.duration != 0 ? p_268089_.applyAsInt(this.duration) : this.duration;
   }

   public MobEffect getEffect() {
      return this.effect == null ? null : net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getDelegateOrThrow(this.effect).get();
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public boolean isAmbient() {
      return this.ambient;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public boolean showIcon() {
      return this.showIcon;
   }

   public boolean tick(LivingEntity p_19553_, Runnable p_19554_) {
      if (this.hasRemainingDuration()) {
         int i = this.isInfiniteDuration() ? p_19553_.tickCount : this.duration;
         if (this.effect.isDurationEffectTick(i, this.amplifier)) {
            this.applyEffect(p_19553_);
         }

         this.tickDownDuration();
         if (this.duration == 0 && this.hiddenEffect != null) {
            this.setDetailsFrom(this.hiddenEffect);
            this.hiddenEffect = this.hiddenEffect.hiddenEffect;
            p_19554_.run();
         }
      }

      this.factorData.ifPresent((p_267917_) -> {
         p_267917_.tick(this);
      });
      return this.hasRemainingDuration();
   }

   private boolean hasRemainingDuration() {
      return this.isInfiniteDuration() || this.duration > 0;
   }

   private int tickDownDuration() {
      if (this.hiddenEffect != null) {
         this.hiddenEffect.tickDownDuration();
      }

      return this.duration = this.mapDuration((p_267916_) -> {
         return p_267916_ - 1;
      });
   }

   public void applyEffect(LivingEntity p_19551_) {
      if (this.hasRemainingDuration()) {
         this.effect.applyEffectTick(p_19551_, this.amplifier);
      }

   }

   public String getDescriptionId() {
      return this.effect.getDescriptionId();
   }

   public String toString() {
      String s;
      if (this.amplifier > 0) {
         s = this.getDescriptionId() + " x " + (this.amplifier + 1) + ", Duration: " + this.describeDuration();
      } else {
         s = this.getDescriptionId() + ", Duration: " + this.describeDuration();
      }

      if (!this.visible) {
         s = s + ", Particles: false";
      }

      if (!this.showIcon) {
         s = s + ", Show Icon: false";
      }

      return s;
   }

   private String describeDuration() {
      return this.isInfiniteDuration() ? "infinite" : Integer.toString(this.duration);
   }

   public boolean equals(Object p_19574_) {
      if (this == p_19574_) {
         return true;
      } else if (!(p_19574_ instanceof MobEffectInstance)) {
         return false;
      } else {
         MobEffectInstance mobeffectinstance = (MobEffectInstance)p_19574_;
         return this.duration == mobeffectinstance.duration && this.amplifier == mobeffectinstance.amplifier && this.ambient == mobeffectinstance.ambient && this.effect.equals(mobeffectinstance.effect);
      }
   }

   public int hashCode() {
      int i = this.effect.hashCode();
      i = 31 * i + this.duration;
      i = 31 * i + this.amplifier;
      return 31 * i + (this.ambient ? 1 : 0);
   }

   public CompoundTag save(CompoundTag p_19556_) {
      p_19556_.putInt("Id", MobEffect.getId(this.getEffect()));
      net.minecraftforge.common.ForgeHooks.saveMobEffect(p_19556_, "forge:id", this.getEffect());
      this.writeDetailsTo(p_19556_);
      return p_19556_;
   }

   private void writeDetailsTo(CompoundTag p_19568_) {
      p_19568_.putByte("Amplifier", (byte)this.getAmplifier());
      p_19568_.putInt("Duration", this.getDuration());
      p_19568_.putBoolean("Ambient", this.isAmbient());
      p_19568_.putBoolean("ShowParticles", this.isVisible());
      p_19568_.putBoolean("ShowIcon", this.showIcon());
      if (this.hiddenEffect != null) {
         CompoundTag compoundtag = new CompoundTag();
         this.hiddenEffect.save(compoundtag);
         p_19568_.put("HiddenEffect", compoundtag);
      }
      writeCurativeItems(p_19568_);

      this.factorData.ifPresent((p_216903_) -> {
         MobEffectInstance.FactorData.CODEC.encodeStart(NbtOps.INSTANCE, p_216903_).resultOrPartial(LOGGER::error).ifPresent((p_216906_) -> {
            p_19568_.put("FactorCalculationData", p_216906_);
         });
      });
   }

   @Nullable
   public static MobEffectInstance load(CompoundTag p_19561_) {
      int i = p_19561_.getByte("Id") & 0xFF;
      MobEffect mobeffect = MobEffect.byId(i);
      mobeffect = net.minecraftforge.common.ForgeHooks.loadMobEffect(p_19561_, "forge:id", mobeffect);
      return mobeffect == null ? null : loadSpecifiedEffect(mobeffect, p_19561_);
   }

   private static MobEffectInstance loadSpecifiedEffect(MobEffect p_19546_, CompoundTag p_19547_) {
      int i = p_19547_.getByte("Amplifier");
      int j = p_19547_.getInt("Duration");
      boolean flag = p_19547_.getBoolean("Ambient");
      boolean flag1 = true;
      if (p_19547_.contains("ShowParticles", 1)) {
         flag1 = p_19547_.getBoolean("ShowParticles");
      }

      boolean flag2 = flag1;
      if (p_19547_.contains("ShowIcon", 1)) {
         flag2 = p_19547_.getBoolean("ShowIcon");
      }

      MobEffectInstance mobeffectinstance = null;
      if (p_19547_.contains("HiddenEffect", 10)) {
         mobeffectinstance = loadSpecifiedEffect(p_19546_, p_19547_.getCompound("HiddenEffect"));
      }

      Optional<MobEffectInstance.FactorData> optional;
      if (p_19547_.contains("FactorCalculationData", 10)) {
         optional = MobEffectInstance.FactorData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, p_19547_.getCompound("FactorCalculationData"))).resultOrPartial(LOGGER::error);
      } else {
         optional = Optional.empty();
      }

      return readCurativeItems(new MobEffectInstance(p_19546_, j, Math.max(0, i), flag, flag1, flag2, mobeffectinstance, optional), p_19547_);
   }

   public int compareTo(MobEffectInstance p_19566_) {
      int i = 32147;
      return (this.getDuration() <= 32147 || p_19566_.getDuration() <= 32147) && (!this.isAmbient() || !p_19566_.isAmbient()) ? ComparisonChain.start().compare(this.isAmbient(), p_19566_.isAmbient()).compare(this.getDuration(), p_19566_.getDuration()).compare(this.getEffect().getSortOrder(this), p_19566_.getEffect().getSortOrder(this)).result() : ComparisonChain.start().compare(this.isAmbient(), p_19566_.isAmbient()).compare(this.getEffect().getSortOrder(this), p_19566_.getEffect().getSortOrder(this)).result();
   }

   //======================= FORGE START ===========================
   private java.util.List<net.minecraft.world.item.ItemStack> curativeItems;

   @Override
   public java.util.List<net.minecraft.world.item.ItemStack> getCurativeItems() {
      if (this.curativeItems == null) //Lazy load this so that we don't create a circular dep on Items.
         this.curativeItems = getEffect().getCurativeItems();
      return this.curativeItems;
   }
   @Override
   public void setCurativeItems(java.util.List<net.minecraft.world.item.ItemStack> curativeItems) {
      this.curativeItems = curativeItems;
   }
   private static MobEffectInstance readCurativeItems(MobEffectInstance effect, CompoundTag nbt) {
      if (nbt.contains("CurativeItems", net.minecraft.nbt.Tag.TAG_LIST)) {
         java.util.List<net.minecraft.world.item.ItemStack> items = new java.util.ArrayList<net.minecraft.world.item.ItemStack>();
         net.minecraft.nbt.ListTag list = nbt.getList("CurativeItems", net.minecraft.nbt.Tag.TAG_COMPOUND);
         for (int i = 0; i < list.size(); i++) {
            items.add(net.minecraft.world.item.ItemStack.of(list.getCompound(i)));
         }
         effect.setCurativeItems(items);
      }

      return effect;
   }

   public static class FactorData {
      public static final Codec<MobEffectInstance.FactorData> CODEC = RecordCodecBuilder.create((p_216933_) -> {
         return p_216933_.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("padding_duration").forGetter((p_216945_) -> {
            return p_216945_.paddingDuration;
         }), Codec.FLOAT.fieldOf("factor_start").orElse(0.0F).forGetter((p_216943_) -> {
            return p_216943_.factorStart;
         }), Codec.FLOAT.fieldOf("factor_target").orElse(1.0F).forGetter((p_216941_) -> {
            return p_216941_.factorTarget;
         }), Codec.FLOAT.fieldOf("factor_current").orElse(0.0F).forGetter((p_216939_) -> {
            return p_216939_.factorCurrent;
         }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ticks_active").orElse(0).forGetter((p_267918_) -> {
            return p_267918_.ticksActive;
         }), Codec.FLOAT.fieldOf("factor_previous_frame").orElse(0.0F).forGetter((p_216935_) -> {
            return p_216935_.factorPreviousFrame;
         }), Codec.BOOL.fieldOf("had_effect_last_tick").orElse(false).forGetter((p_216929_) -> {
            return p_216929_.hadEffectLastTick;
         })).apply(p_216933_, MobEffectInstance.FactorData::new);
      });
      private final int paddingDuration;
      private float factorStart;
      private float factorTarget;
      private float factorCurrent;
      private int ticksActive;
      private float factorPreviousFrame;
      private boolean hadEffectLastTick;

      public FactorData(int p_216919_, float p_216920_, float p_216921_, float p_216922_, int p_216923_, float p_216924_, boolean p_216925_) {
         this.paddingDuration = p_216919_;
         this.factorStart = p_216920_;
         this.factorTarget = p_216921_;
         this.factorCurrent = p_216922_;
         this.ticksActive = p_216923_;
         this.factorPreviousFrame = p_216924_;
         this.hadEffectLastTick = p_216925_;
      }

      public FactorData(int p_216917_) {
         this(p_216917_, 0.0F, 1.0F, 0.0F, 0, 0.0F, false);
      }

      public void tick(MobEffectInstance p_268212_) {
         this.factorPreviousFrame = this.factorCurrent;
         boolean flag = !p_268212_.endsWithin(this.paddingDuration);
         ++this.ticksActive;
         if (this.hadEffectLastTick != flag) {
            this.hadEffectLastTick = flag;
            this.ticksActive = 0;
            this.factorStart = this.factorCurrent;
            this.factorTarget = flag ? 1.0F : 0.0F;
         }

         float f = Mth.clamp((float)this.ticksActive / (float)this.paddingDuration, 0.0F, 1.0F);
         this.factorCurrent = Mth.lerp(f, this.factorStart, this.factorTarget);
      }

      public float getFactor(LivingEntity p_238414_, float p_238415_) {
         if (p_238414_.isRemoved()) {
            this.factorPreviousFrame = this.factorCurrent;
         }

         return Mth.lerp(p_238415_, this.factorPreviousFrame, this.factorCurrent);
      }
   }
}
