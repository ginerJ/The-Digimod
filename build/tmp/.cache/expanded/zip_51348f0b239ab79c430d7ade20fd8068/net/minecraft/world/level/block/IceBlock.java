package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class IceBlock extends HalfTransparentBlock {
   public IceBlock(BlockBehaviour.Properties p_54155_) {
      super(p_54155_);
   }

   public static BlockState meltsInto() {
      return Blocks.WATER.defaultBlockState();
   }

   public void playerDestroy(Level p_54157_, Player p_54158_, BlockPos p_54159_, BlockState p_54160_, @Nullable BlockEntity p_54161_, ItemStack p_54162_) {
      super.playerDestroy(p_54157_, p_54158_, p_54159_, p_54160_, p_54161_, p_54162_);
      if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, p_54162_) == 0) {
         if (p_54157_.dimensionType().ultraWarm()) {
            p_54157_.removeBlock(p_54159_, false);
            return;
         }

         BlockState blockstate = p_54157_.getBlockState(p_54159_.below());
         if (blockstate.blocksMotion() || blockstate.liquid()) {
            p_54157_.setBlockAndUpdate(p_54159_, meltsInto());
         }
      }

   }

   public void randomTick(BlockState p_221355_, ServerLevel p_221356_, BlockPos p_221357_, RandomSource p_221358_) {
      if (p_221356_.getBrightness(LightLayer.BLOCK, p_221357_) > 11 - p_221355_.getLightBlock(p_221356_, p_221357_)) {
         this.melt(p_221355_, p_221356_, p_221357_);
      }

   }

   protected void melt(BlockState p_54169_, Level p_54170_, BlockPos p_54171_) {
      if (p_54170_.dimensionType().ultraWarm()) {
         p_54170_.removeBlock(p_54171_, false);
      } else {
         p_54170_.setBlockAndUpdate(p_54171_, meltsInto());
         p_54170_.neighborChanged(p_54171_, meltsInto().getBlock(), p_54171_);
      }
   }
}