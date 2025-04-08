package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ChiseledBookShelfBlock extends BaseEntityBlock {
   private static final int MAX_BOOKS_IN_STORAGE = 6;
   public static final int BOOKS_PER_ROW = 3;
   public static final List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES = List.of(BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED, BlockStateProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED, BlockStateProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED, BlockStateProperties.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED, BlockStateProperties.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED, BlockStateProperties.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED);

   public ChiseledBookShelfBlock(BlockBehaviour.Properties p_249989_) {
      super(p_249989_);
      BlockState blockstate = this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH);

      for(BooleanProperty booleanproperty : SLOT_OCCUPIED_PROPERTIES) {
         blockstate = blockstate.setValue(booleanproperty, Boolean.valueOf(false));
      }

      this.registerDefaultState(blockstate);
   }

   public RenderShape getRenderShape(BlockState p_251274_) {
      return RenderShape.MODEL;
   }

   public InteractionResult use(BlockState p_251144_, Level p_251668_, BlockPos p_249108_, Player p_249954_, InteractionHand p_249823_, BlockHitResult p_250640_) {
      BlockEntity $$8 = p_251668_.getBlockEntity(p_249108_);
      if ($$8 instanceof ChiseledBookShelfBlockEntity chiseledbookshelfblockentity) {
         Optional<Vec2> optional = getRelativeHitCoordinatesForBlockFace(p_250640_, p_251144_.getValue(HorizontalDirectionalBlock.FACING));
         if (optional.isEmpty()) {
            return InteractionResult.PASS;
         } else {
            int i = getHitSlot(optional.get());
            if (p_251144_.getValue(SLOT_OCCUPIED_PROPERTIES.get(i))) {
               removeBook(p_251668_, p_249108_, p_249954_, chiseledbookshelfblockentity, i);
               return InteractionResult.sidedSuccess(p_251668_.isClientSide);
            } else {
               ItemStack itemstack = p_249954_.getItemInHand(p_249823_);
               if (itemstack.is(ItemTags.BOOKSHELF_BOOKS)) {
                  addBook(p_251668_, p_249108_, p_249954_, chiseledbookshelfblockentity, itemstack, i);
                  return InteractionResult.sidedSuccess(p_251668_.isClientSide);
               } else {
                  return InteractionResult.CONSUME;
               }
            }
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult p_261714_, Direction p_262116_) {
      Direction direction = p_261714_.getDirection();
      if (p_262116_ != direction) {
         return Optional.empty();
      } else {
         BlockPos blockpos = p_261714_.getBlockPos().relative(direction);
         Vec3 vec3 = p_261714_.getLocation().subtract((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
         double d0 = vec3.x();
         double d1 = vec3.y();
         double d2 = vec3.z();
         Optional optional;
         switch (direction) {
            case NORTH:
               optional = Optional.of(new Vec2((float)(1.0D - d0), (float)d1));
               break;
            case SOUTH:
               optional = Optional.of(new Vec2((float)d0, (float)d1));
               break;
            case WEST:
               optional = Optional.of(new Vec2((float)d2, (float)d1));
               break;
            case EAST:
               optional = Optional.of(new Vec2((float)(1.0D - d2), (float)d1));
               break;
            case DOWN:
            case UP:
               optional = Optional.empty();
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return optional;
      }
   }

   private static int getHitSlot(Vec2 p_261991_) {
      int i = p_261991_.y >= 0.5F ? 0 : 1;
      int j = getSection(p_261991_.x);
      return j + i * 3;
   }

   private static int getSection(float p_261599_) {
      float f = 0.0625F;
      float f1 = 0.375F;
      if (p_261599_ < 0.375F) {
         return 0;
      } else {
         float f2 = 0.6875F;
         return p_261599_ < 0.6875F ? 1 : 2;
      }
   }

   private static void addBook(Level p_262592_, BlockPos p_262669_, Player p_262572_, ChiseledBookShelfBlockEntity p_262606_, ItemStack p_262587_, int p_262692_) {
      if (!p_262592_.isClientSide) {
         p_262572_.awardStat(Stats.ITEM_USED.get(p_262587_.getItem()));
         SoundEvent soundevent = p_262587_.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_INSERT;
         p_262606_.setItem(p_262692_, p_262587_.split(1));
         p_262592_.playSound((Player)null, p_262669_, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (p_262572_.isCreative()) {
            p_262587_.grow(1);
         }

         p_262592_.gameEvent(p_262572_, GameEvent.BLOCK_CHANGE, p_262669_);
      }
   }

   private static void removeBook(Level p_262654_, BlockPos p_262601_, Player p_262636_, ChiseledBookShelfBlockEntity p_262605_, int p_262673_) {
      if (!p_262654_.isClientSide) {
         ItemStack itemstack = p_262605_.removeItem(p_262673_, 1);
         SoundEvent soundevent = itemstack.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_PICKUP;
         p_262654_.playSound((Player)null, p_262601_, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (!p_262636_.getInventory().add(itemstack)) {
            p_262636_.drop(itemstack, false);
         }

         p_262654_.gameEvent(p_262636_, GameEvent.BLOCK_CHANGE, p_262601_);
      }
   }

   public @Nullable BlockEntity newBlockEntity(BlockPos p_250440_, BlockState p_248729_) {
      return new ChiseledBookShelfBlockEntity(p_250440_, p_248729_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_250973_) {
      p_250973_.add(HorizontalDirectionalBlock.FACING);
      SLOT_OCCUPIED_PROPERTIES.forEach((p_261456_) -> {
         p_250973_.add(p_261456_);
      });
   }

   public void onRemove(BlockState p_250071_, Level p_251485_, BlockPos p_251954_, BlockState p_251852_, boolean p_252250_) {
      if (!p_250071_.is(p_251852_.getBlock())) {
         BlockEntity blockentity = p_251485_.getBlockEntity(p_251954_);
         if (blockentity instanceof ChiseledBookShelfBlockEntity) {
            ChiseledBookShelfBlockEntity chiseledbookshelfblockentity = (ChiseledBookShelfBlockEntity)blockentity;
            if (!chiseledbookshelfblockentity.isEmpty()) {
               for(int i = 0; i < 6; ++i) {
                  ItemStack itemstack = chiseledbookshelfblockentity.getItem(i);
                  if (!itemstack.isEmpty()) {
                     Containers.dropItemStack(p_251485_, (double)p_251954_.getX(), (double)p_251954_.getY(), (double)p_251954_.getZ(), itemstack);
                  }
               }

               chiseledbookshelfblockentity.clearContent();
               p_251485_.updateNeighbourForOutputSignal(p_251954_, this);
            }
         }

         super.onRemove(p_250071_, p_251485_, p_251954_, p_251852_, p_252250_);
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_251318_) {
      return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, p_251318_.getHorizontalDirection().getOpposite());
   }

   public BlockState rotate(BlockState p_288975_, Rotation p_288993_) {
      return p_288975_.setValue(HorizontalDirectionalBlock.FACING, p_288993_.rotate(p_288975_.getValue(HorizontalDirectionalBlock.FACING)));
   }

   public BlockState mirror(BlockState p_289000_, Mirror p_288962_) {
      return p_289000_.rotate(p_288962_.getRotation(p_289000_.getValue(HorizontalDirectionalBlock.FACING)));
   }

   public boolean hasAnalogOutputSignal(BlockState p_249302_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_249192_, Level p_252207_, BlockPos p_248999_) {
      if (p_252207_.isClientSide()) {
         return 0;
      } else {
         BlockEntity blockentity = p_252207_.getBlockEntity(p_248999_);
         if (blockentity instanceof ChiseledBookShelfBlockEntity) {
            ChiseledBookShelfBlockEntity chiseledbookshelfblockentity = (ChiseledBookShelfBlockEntity)blockentity;
            return chiseledbookshelfblockentity.getLastInteractedSlot() + 1;
         } else {
            return 0;
         }
      }
   }
}