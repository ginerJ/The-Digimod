package net.minecraft.world.damagesource;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record FallLocation(String id) {
   public static final FallLocation GENERIC = new FallLocation("generic");
   public static final FallLocation LADDER = new FallLocation("ladder");
   public static final FallLocation VINES = new FallLocation("vines");
   public static final FallLocation WEEPING_VINES = new FallLocation("weeping_vines");
   public static final FallLocation TWISTING_VINES = new FallLocation("twisting_vines");
   public static final FallLocation SCAFFOLDING = new FallLocation("scaffolding");
   public static final FallLocation OTHER_CLIMBABLE = new FallLocation("other_climbable");
   public static final FallLocation WATER = new FallLocation("water");

   public static FallLocation blockToFallLocation(BlockState p_289530_) {
      if (!p_289530_.is(Blocks.LADDER) && !p_289530_.is(BlockTags.TRAPDOORS)) {
         if (p_289530_.is(Blocks.VINE)) {
            return VINES;
         } else if (!p_289530_.is(Blocks.WEEPING_VINES) && !p_289530_.is(Blocks.WEEPING_VINES_PLANT)) {
            if (!p_289530_.is(Blocks.TWISTING_VINES) && !p_289530_.is(Blocks.TWISTING_VINES_PLANT)) {
               return p_289530_.is(Blocks.SCAFFOLDING) ? SCAFFOLDING : OTHER_CLIMBABLE;
            } else {
               return TWISTING_VINES;
            }
         } else {
            return WEEPING_VINES;
         }
      } else {
         return LADDER;
      }
   }

   @Nullable
   public static FallLocation getCurrentFallLocation(LivingEntity p_289566_) {
      Optional<BlockPos> optional = p_289566_.getLastClimbablePos();
      if (optional.isPresent()) {
         BlockState blockstate = p_289566_.level().getBlockState(optional.get());
         return blockToFallLocation(blockstate);
      } else {
         return p_289566_.isInWater() ? WATER : null;
      }
   }

   public String languageKey() {
      return "death.fell.accident." + this.id;
   }
}