package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MagmaBlock extends Block {
   private static final int BUBBLE_COLUMN_CHECK_DELAY = 20;

   public MagmaBlock(BlockBehaviour.Properties p_54800_) {
      super(p_54800_);
   }

   public void stepOn(Level p_153777_, BlockPos p_153778_, BlockState p_153779_, Entity p_153780_) {
      if (!p_153780_.isSteppingCarefully() && p_153780_ instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)p_153780_)) {
         p_153780_.hurt(p_153777_.damageSources().hotFloor(), 1.0F);
      }

      super.stepOn(p_153777_, p_153778_, p_153779_, p_153780_);
   }

   public void tick(BlockState p_221415_, ServerLevel p_221416_, BlockPos p_221417_, RandomSource p_221418_) {
      BubbleColumnBlock.updateColumn(p_221416_, p_221417_.above(), p_221415_);
   }

   public BlockState updateShape(BlockState p_54811_, Direction p_54812_, BlockState p_54813_, LevelAccessor p_54814_, BlockPos p_54815_, BlockPos p_54816_) {
      if (p_54812_ == Direction.UP && p_54813_.is(Blocks.WATER)) {
         p_54814_.scheduleTick(p_54815_, this, 20);
      }

      return super.updateShape(p_54811_, p_54812_, p_54813_, p_54814_, p_54815_, p_54816_);
   }

   public void onPlace(BlockState p_54823_, Level p_54824_, BlockPos p_54825_, BlockState p_54826_, boolean p_54827_) {
      p_54824_.scheduleTick(p_54825_, this, 20);
   }
}