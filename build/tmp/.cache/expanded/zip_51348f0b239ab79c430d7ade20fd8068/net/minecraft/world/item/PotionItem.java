package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class PotionItem extends Item {
   private static final int DRINK_DURATION = 32;

   public PotionItem(Item.Properties p_42979_) {
      super(p_42979_);
   }

   public ItemStack getDefaultInstance() {
      return PotionUtils.setPotion(super.getDefaultInstance(), Potions.WATER);
   }

   public ItemStack finishUsingItem(ItemStack p_42984_, Level p_42985_, LivingEntity p_42986_) {
      Player player = p_42986_ instanceof Player ? (Player)p_42986_ : null;
      if (player instanceof ServerPlayer) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, p_42984_);
      }

      if (!p_42985_.isClientSide) {
         for(MobEffectInstance mobeffectinstance : PotionUtils.getMobEffects(p_42984_)) {
            if (mobeffectinstance.getEffect().isInstantenous()) {
               mobeffectinstance.getEffect().applyInstantenousEffect(player, player, p_42986_, mobeffectinstance.getAmplifier(), 1.0D);
            } else {
               p_42986_.addEffect(new MobEffectInstance(mobeffectinstance));
            }
         }
      }

      if (player != null) {
         player.awardStat(Stats.ITEM_USED.get(this));
         if (!player.getAbilities().instabuild) {
            p_42984_.shrink(1);
         }
      }

      if (player == null || !player.getAbilities().instabuild) {
         if (p_42984_.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
         }

         if (player != null) {
            player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
         }
      }

      p_42986_.gameEvent(GameEvent.DRINK);
      return p_42984_;
   }

   public InteractionResult useOn(UseOnContext p_220235_) {
      Level level = p_220235_.getLevel();
      BlockPos blockpos = p_220235_.getClickedPos();
      Player player = p_220235_.getPlayer();
      ItemStack itemstack = p_220235_.getItemInHand();
      BlockState blockstate = level.getBlockState(blockpos);
      if (p_220235_.getClickedFace() != Direction.DOWN && blockstate.is(BlockTags.CONVERTABLE_TO_MUD) && PotionUtils.getPotion(itemstack) == Potions.WATER) {
         level.playSound((Player)null, blockpos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
         player.setItemInHand(p_220235_.getHand(), ItemUtils.createFilledResult(itemstack, player, new ItemStack(Items.GLASS_BOTTLE)));
         player.awardStat(Stats.ITEM_USED.get(itemstack.getItem()));
         if (!level.isClientSide) {
            ServerLevel serverlevel = (ServerLevel)level;

            for(int i = 0; i < 5; ++i) {
               serverlevel.sendParticles(ParticleTypes.SPLASH, (double)blockpos.getX() + level.random.nextDouble(), (double)(blockpos.getY() + 1), (double)blockpos.getZ() + level.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
            }
         }

         level.playSound((Player)null, blockpos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
         level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, blockpos);
         level.setBlockAndUpdate(blockpos, Blocks.MUD.defaultBlockState());
         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public int getUseDuration(ItemStack p_43001_) {
      return 32;
   }

   public UseAnim getUseAnimation(ItemStack p_42997_) {
      return UseAnim.DRINK;
   }

   public InteractionResultHolder<ItemStack> use(Level p_42993_, Player p_42994_, InteractionHand p_42995_) {
      return ItemUtils.startUsingInstantly(p_42993_, p_42994_, p_42995_);
   }

   public String getDescriptionId(ItemStack p_43003_) {
      return PotionUtils.getPotion(p_43003_).getName(this.getDescriptionId() + ".effect.");
   }

   public void appendHoverText(ItemStack p_42988_, @Nullable Level p_42989_, List<Component> p_42990_, TooltipFlag p_42991_) {
      PotionUtils.addPotionTooltip(p_42988_, p_42990_, 1.0F);
   }
}