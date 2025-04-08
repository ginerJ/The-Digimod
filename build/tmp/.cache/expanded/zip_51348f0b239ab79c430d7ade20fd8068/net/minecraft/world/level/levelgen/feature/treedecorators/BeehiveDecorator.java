package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BeehiveDecorator extends TreeDecorator {
   public static final Codec<BeehiveDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(BeehiveDecorator::new, (p_69971_) -> {
      return p_69971_.probability;
   }).codec();
   private static final Direction WORLDGEN_FACING = Direction.SOUTH;
   private static final Direction[] SPAWN_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().filter((p_202307_) -> {
      return p_202307_ != WORLDGEN_FACING.getOpposite();
   }).toArray((p_202297_) -> {
      return new Direction[p_202297_];
   });
   private final float probability;

   public BeehiveDecorator(float p_69958_) {
      this.probability = p_69958_;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.BEEHIVE;
   }

   public void place(TreeDecorator.Context p_226019_) {
      RandomSource randomsource = p_226019_.random();
      if (!(randomsource.nextFloat() >= this.probability)) {
         List<BlockPos> list = p_226019_.leaves();
         List<BlockPos> list1 = p_226019_.logs();
         int i = !list.isEmpty() ? Math.max(list.get(0).getY() - 1, list1.get(0).getY() + 1) : Math.min(list1.get(0).getY() + 1 + randomsource.nextInt(3), list1.get(list1.size() - 1).getY());
         List<BlockPos> list2 = list1.stream().filter((p_202300_) -> {
            return p_202300_.getY() == i;
         }).flatMap((p_202305_) -> {
            return Stream.of(SPAWN_DIRECTIONS).map(p_202305_::relative);
         }).collect(Collectors.toList());
         if (!list2.isEmpty()) {
            Collections.shuffle(list2);
            Optional<BlockPos> optional = list2.stream().filter((p_226022_) -> {
               return p_226019_.isAir(p_226022_) && p_226019_.isAir(p_226022_.relative(WORLDGEN_FACING));
            }).findFirst();
            if (!optional.isEmpty()) {
               p_226019_.setBlock(optional.get(), Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.FACING, WORLDGEN_FACING));
               p_226019_.level().getBlockEntity(optional.get(), BlockEntityType.BEEHIVE).ifPresent((p_259007_) -> {
                  int j = 2 + randomsource.nextInt(2);

                  for(int k = 0; k < j; ++k) {
                     CompoundTag compoundtag = new CompoundTag();
                     compoundtag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.BEE).toString());
                     p_259007_.storeBee(compoundtag, randomsource.nextInt(599), false);
                  }

               });
            }
         }
      }
   }
}