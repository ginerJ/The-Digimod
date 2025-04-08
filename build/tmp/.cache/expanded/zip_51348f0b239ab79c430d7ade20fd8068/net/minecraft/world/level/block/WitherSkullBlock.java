package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

public class WitherSkullBlock extends SkullBlock {
   @Nullable
   private static BlockPattern witherPatternFull;
   @Nullable
   private static BlockPattern witherPatternBase;

   public WitherSkullBlock(BlockBehaviour.Properties p_58254_) {
      super(SkullBlock.Types.WITHER_SKELETON, p_58254_);
   }

   public void setPlacedBy(Level p_58260_, BlockPos p_58261_, BlockState p_58262_, @Nullable LivingEntity p_58263_, ItemStack p_58264_) {
      super.setPlacedBy(p_58260_, p_58261_, p_58262_, p_58263_, p_58264_);
      BlockEntity blockentity = p_58260_.getBlockEntity(p_58261_);
      if (blockentity instanceof SkullBlockEntity) {
         checkSpawn(p_58260_, p_58261_, (SkullBlockEntity)blockentity);
      }

   }

   public static void checkSpawn(Level p_58256_, BlockPos p_58257_, SkullBlockEntity p_58258_) {
      if (!p_58256_.isClientSide) {
         BlockState blockstate = p_58258_.getBlockState();
         boolean flag = blockstate.is(Blocks.WITHER_SKELETON_SKULL) || blockstate.is(Blocks.WITHER_SKELETON_WALL_SKULL);
         if (flag && p_58257_.getY() >= p_58256_.getMinBuildHeight() && p_58256_.getDifficulty() != Difficulty.PEACEFUL) {
            BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch = getOrCreateWitherFull().find(p_58256_, p_58257_);
            if (blockpattern$blockpatternmatch != null) {
               WitherBoss witherboss = EntityType.WITHER.create(p_58256_);
               if (witherboss != null) {
                  CarvedPumpkinBlock.clearPatternBlocks(p_58256_, blockpattern$blockpatternmatch);
                  BlockPos blockpos = blockpattern$blockpatternmatch.getBlock(1, 2, 0).getPos();
                  witherboss.moveTo((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.55D, (double)blockpos.getZ() + 0.5D, blockpattern$blockpatternmatch.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
                  witherboss.yBodyRot = blockpattern$blockpatternmatch.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
                  witherboss.makeInvulnerable();

                  for(ServerPlayer serverplayer : p_58256_.getEntitiesOfClass(ServerPlayer.class, witherboss.getBoundingBox().inflate(50.0D))) {
                     CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, witherboss);
                  }

                  p_58256_.addFreshEntity(witherboss);
                  CarvedPumpkinBlock.updatePatternBlocks(p_58256_, blockpattern$blockpatternmatch);
               }

            }
         }
      }
   }

   public static boolean canSpawnMob(Level p_58268_, BlockPos p_58269_, ItemStack p_58270_) {
      if (p_58270_.is(Items.WITHER_SKELETON_SKULL) && p_58269_.getY() >= p_58268_.getMinBuildHeight() + 2 && p_58268_.getDifficulty() != Difficulty.PEACEFUL && !p_58268_.isClientSide) {
         return getOrCreateWitherBase().find(p_58268_, p_58269_) != null;
      } else {
         return false;
      }
   }

   private static BlockPattern getOrCreateWitherFull() {
      if (witherPatternFull == null) {
         witherPatternFull = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', (p_58272_) -> {
            return p_58272_.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS);
         }).where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', (p_284877_) -> {
            return p_284877_.getState().isAir();
         }).build();
      }

      return witherPatternFull;
   }

   private static BlockPattern getOrCreateWitherBase() {
      if (witherPatternBase == null) {
         witherPatternBase = BlockPatternBuilder.start().aisle("   ", "###", "~#~").where('#', (p_58266_) -> {
            return p_58266_.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS);
         }).where('~', (p_284878_) -> {
            return p_284878_.getState().isAir();
         }).build();
      }

      return witherPatternBase;
   }
}