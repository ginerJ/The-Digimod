package net.minecraft.world.item.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class FrostWalkerEnchantment extends Enchantment {
   public FrostWalkerEnchantment(Enchantment.Rarity p_45013_, EquipmentSlot... p_45014_) {
      super(p_45013_, EnchantmentCategory.ARMOR_FEET, p_45014_);
   }

   public int getMinCost(int p_45017_) {
      return p_45017_ * 10;
   }

   public int getMaxCost(int p_45027_) {
      return this.getMinCost(p_45027_) + 15;
   }

   public boolean isTreasureOnly() {
      return true;
   }

   public int getMaxLevel() {
      return 2;
   }

   public static void onEntityMoved(LivingEntity p_45019_, Level p_45020_, BlockPos p_45021_, int p_45022_) {
      if (p_45019_.onGround()) {
         BlockState blockstate = Blocks.FROSTED_ICE.defaultBlockState();
         int i = Math.min(16, 2 + p_45022_);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(BlockPos blockpos : BlockPos.betweenClosed(p_45021_.offset(-i, -1, -i), p_45021_.offset(i, -1, i))) {
            if (blockpos.closerToCenterThan(p_45019_.position(), (double)i)) {
               blockpos$mutableblockpos.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
               BlockState blockstate1 = p_45020_.getBlockState(blockpos$mutableblockpos);
               if (blockstate1.isAir()) {
                  BlockState blockstate2 = p_45020_.getBlockState(blockpos);
                  if (blockstate2 == FrostedIceBlock.meltsInto() && blockstate.canSurvive(p_45020_, blockpos) && p_45020_.isUnobstructed(blockstate, blockpos, CollisionContext.empty()) && !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(p_45019_, net.minecraftforge.common.util.BlockSnapshot.create(p_45020_.dimension(), p_45020_, blockpos), net.minecraft.core.Direction.UP)) {
                     p_45020_.setBlockAndUpdate(blockpos, blockstate);
                     p_45020_.scheduleTick(blockpos, Blocks.FROSTED_ICE, Mth.nextInt(p_45019_.getRandom(), 60, 120));
                  }
               }
            }
         }

      }
   }

   public boolean checkCompatibility(Enchantment p_45024_) {
      return super.checkCompatibility(p_45024_) && p_45024_ != Enchantments.DEPTH_STRIDER;
   }
}
