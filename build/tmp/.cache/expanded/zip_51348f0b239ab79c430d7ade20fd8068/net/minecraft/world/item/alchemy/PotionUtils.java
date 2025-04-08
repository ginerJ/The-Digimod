package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public class PotionUtils {
   public static final String TAG_CUSTOM_POTION_EFFECTS = "CustomPotionEffects";
   public static final String TAG_CUSTOM_POTION_COLOR = "CustomPotionColor";
   public static final String TAG_POTION = "Potion";
   private static final int EMPTY_COLOR = 16253176;
   private static final Component NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);

   public static List<MobEffectInstance> getMobEffects(ItemStack p_43548_) {
      return getAllEffects(p_43548_.getTag());
   }

   public static List<MobEffectInstance> getAllEffects(Potion p_43562_, Collection<MobEffectInstance> p_43563_) {
      List<MobEffectInstance> list = Lists.newArrayList();
      list.addAll(p_43562_.getEffects());
      list.addAll(p_43563_);
      return list;
   }

   public static List<MobEffectInstance> getAllEffects(@Nullable CompoundTag p_43567_) {
      List<MobEffectInstance> list = Lists.newArrayList();
      list.addAll(getPotion(p_43567_).getEffects());
      getCustomEffects(p_43567_, list);
      return list;
   }

   public static List<MobEffectInstance> getCustomEffects(ItemStack p_43572_) {
      return getCustomEffects(p_43572_.getTag());
   }

   public static List<MobEffectInstance> getCustomEffects(@Nullable CompoundTag p_43574_) {
      List<MobEffectInstance> list = Lists.newArrayList();
      getCustomEffects(p_43574_, list);
      return list;
   }

   public static void getCustomEffects(@Nullable CompoundTag p_43569_, List<MobEffectInstance> p_43570_) {
      if (p_43569_ != null && p_43569_.contains("CustomPotionEffects", 9)) {
         ListTag listtag = p_43569_.getList("CustomPotionEffects", 10);

         for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            MobEffectInstance mobeffectinstance = MobEffectInstance.load(compoundtag);
            if (mobeffectinstance != null) {
               p_43570_.add(mobeffectinstance);
            }
         }
      }

   }

   public static int getColor(ItemStack p_43576_) {
      CompoundTag compoundtag = p_43576_.getTag();
      if (compoundtag != null && compoundtag.contains("CustomPotionColor", 99)) {
         return compoundtag.getInt("CustomPotionColor");
      } else {
         return getPotion(p_43576_) == Potions.EMPTY ? 16253176 : getColor(getMobEffects(p_43576_));
      }
   }

   public static int getColor(Potion p_43560_) {
      return p_43560_ == Potions.EMPTY ? 16253176 : getColor(p_43560_.getEffects());
   }

   public static int getColor(Collection<MobEffectInstance> p_43565_) {
      int i = 3694022;
      if (p_43565_.isEmpty()) {
         return 3694022;
      } else {
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         int j = 0;

         for(MobEffectInstance mobeffectinstance : p_43565_) {
            if (mobeffectinstance.isVisible()) {
               int k = mobeffectinstance.getEffect().getColor();
               int l = mobeffectinstance.getAmplifier() + 1;
               f += (float)(l * (k >> 16 & 255)) / 255.0F;
               f1 += (float)(l * (k >> 8 & 255)) / 255.0F;
               f2 += (float)(l * (k >> 0 & 255)) / 255.0F;
               j += l;
            }
         }

         if (j == 0) {
            return 0;
         } else {
            f = f / (float)j * 255.0F;
            f1 = f1 / (float)j * 255.0F;
            f2 = f2 / (float)j * 255.0F;
            return (int)f << 16 | (int)f1 << 8 | (int)f2;
         }
      }
   }

   public static Potion getPotion(ItemStack p_43580_) {
      return getPotion(p_43580_.getTag());
   }

   public static Potion getPotion(@Nullable CompoundTag p_43578_) {
      return p_43578_ == null ? Potions.EMPTY : Potion.byName(p_43578_.getString("Potion"));
   }

   public static ItemStack setPotion(ItemStack p_43550_, Potion p_43551_) {
      ResourceLocation resourcelocation = BuiltInRegistries.POTION.getKey(p_43551_);
      if (p_43551_ == Potions.EMPTY) {
         p_43550_.removeTagKey("Potion");
      } else {
         p_43550_.getOrCreateTag().putString("Potion", resourcelocation.toString());
      }

      return p_43550_;
   }

   public static ItemStack setCustomEffects(ItemStack p_43553_, Collection<MobEffectInstance> p_43554_) {
      if (p_43554_.isEmpty()) {
         return p_43553_;
      } else {
         CompoundTag compoundtag = p_43553_.getOrCreateTag();
         ListTag listtag = compoundtag.getList("CustomPotionEffects", 9);

         for(MobEffectInstance mobeffectinstance : p_43554_) {
            listtag.add(mobeffectinstance.save(new CompoundTag()));
         }

         compoundtag.put("CustomPotionEffects", listtag);
         return p_43553_;
      }
   }

   public static void addPotionTooltip(ItemStack p_43556_, List<Component> p_43557_, float p_43558_) {
      addPotionTooltip(getMobEffects(p_43556_), p_43557_, p_43558_);
   }

   public static void addPotionTooltip(List<MobEffectInstance> p_259687_, List<Component> p_259660_, float p_259949_) {
      List<Pair<Attribute, AttributeModifier>> list = Lists.newArrayList();
      if (p_259687_.isEmpty()) {
         p_259660_.add(NO_EFFECT);
      } else {
         for(MobEffectInstance mobeffectinstance : p_259687_) {
            MutableComponent mutablecomponent = Component.translatable(mobeffectinstance.getDescriptionId());
            MobEffect mobeffect = mobeffectinstance.getEffect();
            Map<Attribute, AttributeModifier> map = mobeffect.getAttributeModifiers();
            if (!map.isEmpty()) {
               for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                  AttributeModifier attributemodifier = entry.getValue();
                  AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), mobeffect.getAttributeModifierValue(mobeffectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                  list.add(new Pair<>(entry.getKey(), attributemodifier1));
               }
            }

            if (mobeffectinstance.getAmplifier() > 0) {
               mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + mobeffectinstance.getAmplifier()));
            }

            if (!mobeffectinstance.endsWithin(20)) {
               mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobeffectinstance, p_259949_));
            }

            p_259660_.add(mutablecomponent.withStyle(mobeffect.getCategory().getTooltipFormatting()));
         }
      }

      if (!list.isEmpty()) {
         p_259660_.add(CommonComponents.EMPTY);
         p_259660_.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

         for(Pair<Attribute, AttributeModifier> pair : list) {
            AttributeModifier attributemodifier2 = pair.getSecond();
            double d0 = attributemodifier2.getAmount();
            double d1;
            if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
               d1 = attributemodifier2.getAmount();
            } else {
               d1 = attributemodifier2.getAmount() * 100.0D;
            }

            if (d0 > 0.0D) {
               p_259660_.add(Component.translatable("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId())).withStyle(ChatFormatting.BLUE));
            } else if (d0 < 0.0D) {
               d1 *= -1.0D;
               p_259660_.add(Component.translatable("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId())).withStyle(ChatFormatting.RED));
            }
         }
      }

   }
}