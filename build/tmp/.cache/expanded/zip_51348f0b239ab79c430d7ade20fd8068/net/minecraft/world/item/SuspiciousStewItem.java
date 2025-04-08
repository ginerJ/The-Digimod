package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class SuspiciousStewItem extends Item {
   public static final String EFFECTS_TAG = "Effects";
   public static final String EFFECT_ID_TAG = "EffectId";
   public static final String EFFECT_DURATION_TAG = "EffectDuration";
   public static final int DEFAULT_DURATION = 160;

   public SuspiciousStewItem(Item.Properties p_43257_) {
      super(p_43257_);
   }

   public static void saveMobEffect(ItemStack p_43259_, MobEffect p_43260_, int p_43261_) {
      CompoundTag compoundtag = p_43259_.getOrCreateTag();
      ListTag listtag = compoundtag.getList("Effects", 9);
      CompoundTag compoundtag1 = new CompoundTag();
      compoundtag1.putInt("EffectId", MobEffect.getId(p_43260_));
      net.minecraftforge.common.ForgeHooks.saveMobEffect(compoundtag1, "forge:effect_id", p_43260_);
      compoundtag1.putInt("EffectDuration", p_43261_);
      listtag.add(compoundtag1);
      compoundtag.put("Effects", listtag);
   }

   private static void listPotionEffects(ItemStack p_260126_, Consumer<MobEffectInstance> p_259500_) {
      CompoundTag compoundtag = p_260126_.getTag();
      if (compoundtag != null && compoundtag.contains("Effects", 9)) {
         ListTag listtag = compoundtag.getList("Effects", 10);

         for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag1 = listtag.getCompound(i);
            int j;
            if (compoundtag1.contains("EffectDuration", 99)) {
               j = compoundtag1.getInt("EffectDuration");
            } else {
               j = 160;
            }

            MobEffect mobeffect = MobEffect.byId(compoundtag1.getInt("EffectId"));
            mobeffect = net.minecraftforge.common.ForgeHooks.loadMobEffect(compoundtag1, "forge:effect_id", mobeffect);
            if (mobeffect != null) {
               p_259500_.accept(new MobEffectInstance(mobeffect, j));
            }
         }
      }

   }

   public void appendHoverText(ItemStack p_260314_, @Nullable Level p_259224_, List<Component> p_259700_, TooltipFlag p_260021_) {
      super.appendHoverText(p_260314_, p_259224_, p_259700_, p_260021_);
      if (p_260021_.isCreative()) {
         List<MobEffectInstance> list = new ArrayList<>();
         listPotionEffects(p_260314_, list::add);
         PotionUtils.addPotionTooltip(list, p_259700_, 1.0F);
      }

   }

   public ItemStack finishUsingItem(ItemStack p_43263_, Level p_43264_, LivingEntity p_43265_) {
      ItemStack itemstack = super.finishUsingItem(p_43263_, p_43264_, p_43265_);
      listPotionEffects(itemstack, p_43265_::addEffect);
      return p_43265_ instanceof Player && ((Player)p_43265_).getAbilities().instabuild ? itemstack : new ItemStack(Items.BOWL);
   }
}
