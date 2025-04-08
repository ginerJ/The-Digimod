package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class MultifaceSpreader {
   public static final MultifaceSpreader.SpreadType[] DEFAULT_SPREAD_ORDER = new MultifaceSpreader.SpreadType[]{MultifaceSpreader.SpreadType.SAME_POSITION, MultifaceSpreader.SpreadType.SAME_PLANE, MultifaceSpreader.SpreadType.WRAP_AROUND};
   private final MultifaceSpreader.SpreadConfig config;

   public MultifaceSpreader(MultifaceBlock p_221590_) {
      this(new MultifaceSpreader.DefaultSpreaderConfig(p_221590_));
   }

   public MultifaceSpreader(MultifaceSpreader.SpreadConfig p_221592_) {
      this.config = p_221592_;
   }

   public boolean canSpreadInAnyDirection(BlockState p_221602_, BlockGetter p_221603_, BlockPos p_221604_, Direction p_221605_) {
      return Direction.stream().anyMatch((p_221611_) -> {
         return this.getSpreadFromFaceTowardDirection(p_221602_, p_221603_, p_221604_, p_221605_, p_221611_, this.config::canSpreadInto).isPresent();
      });
   }

   public Optional<MultifaceSpreader.SpreadPos> spreadFromRandomFaceTowardRandomDirection(BlockState p_221620_, LevelAccessor p_221621_, BlockPos p_221622_, RandomSource p_221623_) {
      return Direction.allShuffled(p_221623_).stream().filter((p_221680_) -> {
         return this.config.canSpreadFrom(p_221620_, p_221680_);
      }).map((p_221629_) -> {
         return this.spreadFromFaceTowardRandomDirection(p_221620_, p_221621_, p_221622_, p_221629_, p_221623_, false);
      }).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
   }

   public long spreadAll(BlockState p_221658_, LevelAccessor p_221659_, BlockPos p_221660_, boolean p_221661_) {
      return Direction.stream().filter((p_221670_) -> {
         return this.config.canSpreadFrom(p_221658_, p_221670_);
      }).map((p_221667_) -> {
         return this.spreadFromFaceTowardAllDirections(p_221658_, p_221659_, p_221660_, p_221667_, p_221661_);
      }).reduce(0L, Long::sum);
   }

   public Optional<MultifaceSpreader.SpreadPos> spreadFromFaceTowardRandomDirection(BlockState p_221631_, LevelAccessor p_221632_, BlockPos p_221633_, Direction p_221634_, RandomSource p_221635_, boolean p_221636_) {
      return Direction.allShuffled(p_221635_).stream().map((p_221677_) -> {
         return this.spreadFromFaceTowardDirection(p_221631_, p_221632_, p_221633_, p_221634_, p_221677_, p_221636_);
      }).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
   }

   private long spreadFromFaceTowardAllDirections(BlockState p_221645_, LevelAccessor p_221646_, BlockPos p_221647_, Direction p_221648_, boolean p_221649_) {
      return Direction.stream().map((p_221656_) -> {
         return this.spreadFromFaceTowardDirection(p_221645_, p_221646_, p_221647_, p_221648_, p_221656_, p_221649_);
      }).filter(Optional::isPresent).count();
   }

   @VisibleForTesting
   public Optional<MultifaceSpreader.SpreadPos> spreadFromFaceTowardDirection(BlockState p_221638_, LevelAccessor p_221639_, BlockPos p_221640_, Direction p_221641_, Direction p_221642_, boolean p_221643_) {
      return this.getSpreadFromFaceTowardDirection(p_221638_, p_221639_, p_221640_, p_221641_, p_221642_, this.config::canSpreadInto).flatMap((p_221600_) -> {
         return this.spreadToFace(p_221639_, p_221600_, p_221643_);
      });
   }

   public Optional<MultifaceSpreader.SpreadPos> getSpreadFromFaceTowardDirection(BlockState p_221613_, BlockGetter p_221614_, BlockPos p_221615_, Direction p_221616_, Direction p_221617_, MultifaceSpreader.SpreadPredicate p_221618_) {
      if (p_221617_.getAxis() == p_221616_.getAxis()) {
         return Optional.empty();
      } else if (this.config.isOtherBlockValidAsSource(p_221613_) || this.config.hasFace(p_221613_, p_221616_) && !this.config.hasFace(p_221613_, p_221617_)) {
         for(MultifaceSpreader.SpreadType multifacespreader$spreadtype : this.config.getSpreadTypes()) {
            MultifaceSpreader.SpreadPos multifacespreader$spreadpos = multifacespreader$spreadtype.getSpreadPos(p_221615_, p_221617_, p_221616_);
            if (p_221618_.test(p_221614_, p_221615_, multifacespreader$spreadpos)) {
               return Optional.of(multifacespreader$spreadpos);
            }
         }

         return Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   public Optional<MultifaceSpreader.SpreadPos> spreadToFace(LevelAccessor p_221594_, MultifaceSpreader.SpreadPos p_221595_, boolean p_221596_) {
      BlockState blockstate = p_221594_.getBlockState(p_221595_.pos());
      return this.config.placeBlock(p_221594_, p_221595_, blockstate, p_221596_) ? Optional.of(p_221595_) : Optional.empty();
   }

   public static class DefaultSpreaderConfig implements MultifaceSpreader.SpreadConfig {
      protected MultifaceBlock block;

      public DefaultSpreaderConfig(MultifaceBlock p_221683_) {
         this.block = p_221683_;
      }

      @Nullable
      public BlockState getStateForPlacement(BlockState p_221694_, BlockGetter p_221695_, BlockPos p_221696_, Direction p_221697_) {
         return this.block.getStateForPlacement(p_221694_, p_221695_, p_221696_, p_221697_);
      }

      protected boolean stateCanBeReplaced(BlockGetter p_221688_, BlockPos p_221689_, BlockPos p_221690_, Direction p_221691_, BlockState p_221692_) {
         return p_221692_.isAir() || p_221692_.is(this.block) || p_221692_.is(Blocks.WATER) && p_221692_.getFluidState().isSource();
      }

      public boolean canSpreadInto(BlockGetter p_221685_, BlockPos p_221686_, MultifaceSpreader.SpreadPos p_221687_) {
         BlockState blockstate = p_221685_.getBlockState(p_221687_.pos());
         return this.stateCanBeReplaced(p_221685_, p_221686_, p_221687_.pos(), p_221687_.face(), blockstate) && this.block.isValidStateForPlacement(p_221685_, blockstate, p_221687_.pos(), p_221687_.face());
      }
   }

   public interface SpreadConfig {
      @Nullable
      BlockState getStateForPlacement(BlockState p_221707_, BlockGetter p_221708_, BlockPos p_221709_, Direction p_221710_);

      boolean canSpreadInto(BlockGetter p_221698_, BlockPos p_221699_, MultifaceSpreader.SpreadPos p_221700_);

      default MultifaceSpreader.SpreadType[] getSpreadTypes() {
         return MultifaceSpreader.DEFAULT_SPREAD_ORDER;
      }

      default boolean hasFace(BlockState p_221712_, Direction p_221713_) {
         return MultifaceBlock.hasFace(p_221712_, p_221713_);
      }

      default boolean isOtherBlockValidAsSource(BlockState p_221706_) {
         return false;
      }

      default boolean canSpreadFrom(BlockState p_221715_, Direction p_221716_) {
         return this.isOtherBlockValidAsSource(p_221715_) || this.hasFace(p_221715_, p_221716_);
      }

      default boolean placeBlock(LevelAccessor p_221702_, MultifaceSpreader.SpreadPos p_221703_, BlockState p_221704_, boolean p_221705_) {
         BlockState blockstate = this.getStateForPlacement(p_221704_, p_221702_, p_221703_.pos(), p_221703_.face());
         if (blockstate != null) {
            if (p_221705_) {
               p_221702_.getChunk(p_221703_.pos()).markPosForPostprocessing(p_221703_.pos());
            }

            return p_221702_.setBlock(p_221703_.pos(), blockstate, 2);
         } else {
            return false;
         }
      }
   }

   public static record SpreadPos(BlockPos pos, Direction face) {
   }

   @FunctionalInterface
   public interface SpreadPredicate {
      boolean test(BlockGetter p_221729_, BlockPos p_221730_, MultifaceSpreader.SpreadPos p_221731_);
   }

   public static enum SpreadType {
      SAME_POSITION {
         public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos p_221751_, Direction p_221752_, Direction p_221753_) {
            return new MultifaceSpreader.SpreadPos(p_221751_, p_221752_);
         }
      },
      SAME_PLANE {
         public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos p_221758_, Direction p_221759_, Direction p_221760_) {
            return new MultifaceSpreader.SpreadPos(p_221758_.relative(p_221759_), p_221760_);
         }
      },
      WRAP_AROUND {
         public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos p_221765_, Direction p_221766_, Direction p_221767_) {
            return new MultifaceSpreader.SpreadPos(p_221765_.relative(p_221766_).relative(p_221767_), p_221766_.getOpposite());
         }
      };

      public abstract MultifaceSpreader.SpreadPos getSpreadPos(BlockPos p_221741_, Direction p_221742_, Direction p_221743_);
   }
}