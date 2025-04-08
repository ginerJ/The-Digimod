package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public abstract class AbstractSkullBlock extends BaseEntityBlock implements Equipable {
   private final SkullBlock.Type type;

   public AbstractSkullBlock(SkullBlock.Type p_48745_, BlockBehaviour.Properties p_48746_) {
      super(p_48746_);
      this.type = p_48745_;
   }

   public BlockEntity newBlockEntity(BlockPos p_151996_, BlockState p_151997_) {
      return new SkullBlockEntity(p_151996_, p_151997_);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_151992_, BlockState p_151993_, BlockEntityType<T> p_151994_) {
      if (p_151992_.isClientSide) {
         boolean flag = p_151993_.is(Blocks.DRAGON_HEAD) || p_151993_.is(Blocks.DRAGON_WALL_HEAD) || p_151993_.is(Blocks.PIGLIN_HEAD) || p_151993_.is(Blocks.PIGLIN_WALL_HEAD);
         if (flag) {
            return createTickerHelper(p_151994_, BlockEntityType.SKULL, SkullBlockEntity::animation);
         }
      }

      return null;
   }

   public SkullBlock.Type getType() {
      return this.type;
   }

   public boolean isPathfindable(BlockState p_48750_, BlockGetter p_48751_, BlockPos p_48752_, PathComputationType p_48753_) {
      return false;
   }

   public EquipmentSlot getEquipmentSlot() {
      return EquipmentSlot.HEAD;
   }
}