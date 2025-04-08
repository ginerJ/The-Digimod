package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NetherPortalBlock extends Block {
   public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
   protected static final int AABB_OFFSET = 2;
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

   public NetherPortalBlock(BlockBehaviour.Properties p_54909_) {
      super(p_54909_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
   }

   public VoxelShape getShape(BlockState p_54942_, BlockGetter p_54943_, BlockPos p_54944_, CollisionContext p_54945_) {
      switch ((Direction.Axis)p_54942_.getValue(AXIS)) {
         case Z:
            return Z_AXIS_AABB;
         case X:
         default:
            return X_AXIS_AABB;
      }
   }

   public void randomTick(BlockState p_221799_, ServerLevel p_221800_, BlockPos p_221801_, RandomSource p_221802_) {
      if (p_221800_.dimensionType().natural() && p_221800_.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && p_221802_.nextInt(2000) < p_221800_.getDifficulty().getId()) {
         while(p_221800_.getBlockState(p_221801_).is(this)) {
            p_221801_ = p_221801_.below();
         }

         if (p_221800_.getBlockState(p_221801_).isValidSpawn(p_221800_, p_221801_, EntityType.ZOMBIFIED_PIGLIN)) {
            Entity entity = EntityType.ZOMBIFIED_PIGLIN.spawn(p_221800_, p_221801_.above(), MobSpawnType.STRUCTURE);
            if (entity != null) {
               entity.setPortalCooldown();
            }
         }
      }

   }

   public BlockState updateShape(BlockState p_54928_, Direction p_54929_, BlockState p_54930_, LevelAccessor p_54931_, BlockPos p_54932_, BlockPos p_54933_) {
      Direction.Axis direction$axis = p_54929_.getAxis();
      Direction.Axis direction$axis1 = p_54928_.getValue(AXIS);
      boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
      return !flag && !p_54930_.is(this) && !(new PortalShape(p_54931_, p_54932_, direction$axis1)).isComplete() ? Blocks.AIR.defaultBlockState() : super.updateShape(p_54928_, p_54929_, p_54930_, p_54931_, p_54932_, p_54933_);
   }

   public void entityInside(BlockState p_54915_, Level p_54916_, BlockPos p_54917_, Entity p_54918_) {
      if (p_54918_.canChangeDimensions()) {
         p_54918_.handleInsidePortal(p_54917_);
      }

   }

   public void animateTick(BlockState p_221794_, Level p_221795_, BlockPos p_221796_, RandomSource p_221797_) {
      if (p_221797_.nextInt(100) == 0) {
         p_221795_.playLocalSound((double)p_221796_.getX() + 0.5D, (double)p_221796_.getY() + 0.5D, (double)p_221796_.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, p_221797_.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int i = 0; i < 4; ++i) {
         double d0 = (double)p_221796_.getX() + p_221797_.nextDouble();
         double d1 = (double)p_221796_.getY() + p_221797_.nextDouble();
         double d2 = (double)p_221796_.getZ() + p_221797_.nextDouble();
         double d3 = ((double)p_221797_.nextFloat() - 0.5D) * 0.5D;
         double d4 = ((double)p_221797_.nextFloat() - 0.5D) * 0.5D;
         double d5 = ((double)p_221797_.nextFloat() - 0.5D) * 0.5D;
         int j = p_221797_.nextInt(2) * 2 - 1;
         if (!p_221795_.getBlockState(p_221796_.west()).is(this) && !p_221795_.getBlockState(p_221796_.east()).is(this)) {
            d0 = (double)p_221796_.getX() + 0.5D + 0.25D * (double)j;
            d3 = (double)(p_221797_.nextFloat() * 2.0F * (float)j);
         } else {
            d2 = (double)p_221796_.getZ() + 0.5D + 0.25D * (double)j;
            d5 = (double)(p_221797_.nextFloat() * 2.0F * (float)j);
         }

         p_221795_.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
      }

   }

   public ItemStack getCloneItemStack(BlockGetter p_54911_, BlockPos p_54912_, BlockState p_54913_) {
      return ItemStack.EMPTY;
   }

   public BlockState rotate(BlockState p_54925_, Rotation p_54926_) {
      switch (p_54926_) {
         case COUNTERCLOCKWISE_90:
         case CLOCKWISE_90:
            switch ((Direction.Axis)p_54925_.getValue(AXIS)) {
               case Z:
                  return p_54925_.setValue(AXIS, Direction.Axis.X);
               case X:
                  return p_54925_.setValue(AXIS, Direction.Axis.Z);
               default:
                  return p_54925_;
            }
         default:
            return p_54925_;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54935_) {
      p_54935_.add(AXIS);
   }
}