package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class JukeboxBlock extends BaseEntityBlock {
   public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

   public JukeboxBlock(BlockBehaviour.Properties p_54257_) {
      super(p_54257_);
      this.registerDefaultState(this.stateDefinition.any().setValue(HAS_RECORD, Boolean.valueOf(false)));
   }

   public void setPlacedBy(Level p_54264_, BlockPos p_54265_, BlockState p_54266_, @Nullable LivingEntity p_54267_, ItemStack p_54268_) {
      super.setPlacedBy(p_54264_, p_54265_, p_54266_, p_54267_, p_54268_);
      CompoundTag compoundtag = BlockItem.getBlockEntityData(p_54268_);
      if (compoundtag != null && compoundtag.contains("RecordItem")) {
         p_54264_.setBlock(p_54265_, p_54266_.setValue(HAS_RECORD, Boolean.valueOf(true)), 2);
      }

   }

   public InteractionResult use(BlockState p_54281_, Level p_54282_, BlockPos p_54283_, Player p_54284_, InteractionHand p_54285_, BlockHitResult p_54286_) {
      if (p_54281_.getValue(HAS_RECORD)) {
         BlockEntity blockentity = p_54282_.getBlockEntity(p_54283_);
         if (blockentity instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukeboxblockentity = (JukeboxBlockEntity)blockentity;
            jukeboxblockentity.popOutRecord();
            return InteractionResult.sidedSuccess(p_54282_.isClientSide);
         }
      }

      return InteractionResult.PASS;
   }

   public void onRemove(BlockState p_54288_, Level p_54289_, BlockPos p_54290_, BlockState p_54291_, boolean p_54292_) {
      if (!p_54288_.is(p_54291_.getBlock())) {
         BlockEntity blockentity = p_54289_.getBlockEntity(p_54290_);
         if (blockentity instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukeboxblockentity = (JukeboxBlockEntity)blockentity;
            jukeboxblockentity.popOutRecord();
         }

         super.onRemove(p_54288_, p_54289_, p_54290_, p_54291_, p_54292_);
      }
   }

   public BlockEntity newBlockEntity(BlockPos p_153451_, BlockState p_153452_) {
      return new JukeboxBlockEntity(p_153451_, p_153452_);
   }

   public boolean isSignalSource(BlockState p_273404_) {
      return true;
   }

   public int getSignal(BlockState p_272942_, BlockGetter p_273232_, BlockPos p_273524_, Direction p_272902_) {
      BlockEntity blockentity = p_273232_.getBlockEntity(p_273524_);
      if (blockentity instanceof JukeboxBlockEntity jukeboxblockentity) {
         if (jukeboxblockentity.isRecordPlaying()) {
            return 15;
         }
      }

      return 0;
   }

   public boolean hasAnalogOutputSignal(BlockState p_54275_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_54277_, Level p_54278_, BlockPos p_54279_) {
      BlockEntity blockentity = p_54278_.getBlockEntity(p_54279_);
      if (blockentity instanceof JukeboxBlockEntity jukeboxblockentity) {
         Item item = jukeboxblockentity.getFirstItem().getItem();
         if (item instanceof RecordItem recorditem) {
            return recorditem.getAnalogOutput();
         }
      }

      return 0;
   }

   public RenderShape getRenderShape(BlockState p_54296_) {
      return RenderShape.MODEL;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54294_) {
      p_54294_.add(HAS_RECORD);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_239682_, BlockState p_239683_, BlockEntityType<T> p_239684_) {
      return p_239683_.getValue(HAS_RECORD) ? createTickerHelper(p_239684_, BlockEntityType.JUKEBOX, JukeboxBlockEntity::playRecordTick) : null;
   }
}