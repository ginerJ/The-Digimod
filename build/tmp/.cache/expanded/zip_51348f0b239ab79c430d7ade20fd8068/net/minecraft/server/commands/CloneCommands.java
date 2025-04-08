package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class CloneCommands {
   private static final SimpleCommandExceptionType ERROR_OVERLAP = new SimpleCommandExceptionType(Component.translatable("commands.clone.overlap"));
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_136743_, p_136744_) -> {
      return Component.translatable("commands.clone.toobig", p_136743_, p_136744_);
   });
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.clone.failed"));
   public static final Predicate<BlockInWorld> FILTER_AIR = (p_284652_) -> {
      return !p_284652_.getState().isAir();
   };

   public static void register(CommandDispatcher<CommandSourceStack> p_214424_, CommandBuildContext p_214425_) {
      p_214424_.register(Commands.literal("clone").requires((p_136734_) -> {
         return p_136734_.hasPermission(2);
      }).then(beginEndDestinationAndModeSuffix(p_214425_, (p_264757_) -> {
         return p_264757_.getSource().getLevel();
      })).then(Commands.literal("from").then(Commands.argument("sourceDimension", DimensionArgument.dimension()).then(beginEndDestinationAndModeSuffix(p_214425_, (p_264743_) -> {
         return DimensionArgument.getDimension(p_264743_, "sourceDimension");
      })))));
   }

   private static ArgumentBuilder<CommandSourceStack, ?> beginEndDestinationAndModeSuffix(CommandBuildContext p_265681_, CloneCommands.CommandFunction<CommandContext<CommandSourceStack>, ServerLevel> p_265514_) {
      return Commands.argument("begin", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(destinationAndModeSuffix(p_265681_, p_265514_, (p_264751_) -> {
         return p_264751_.getSource().getLevel();
      })).then(Commands.literal("to").then(Commands.argument("targetDimension", DimensionArgument.dimension()).then(destinationAndModeSuffix(p_265681_, p_265514_, (p_264756_) -> {
         return DimensionArgument.getDimension(p_264756_, "targetDimension");
      })))));
   }

   private static CloneCommands.DimensionAndPosition getLoadedDimensionAndPosition(CommandContext<CommandSourceStack> p_265513_, ServerLevel p_265183_, String p_265511_) throws CommandSyntaxException {
      BlockPos blockpos = BlockPosArgument.getLoadedBlockPos(p_265513_, p_265183_, p_265511_);
      return new CloneCommands.DimensionAndPosition(p_265183_, blockpos);
   }

   private static ArgumentBuilder<CommandSourceStack, ?> destinationAndModeSuffix(CommandBuildContext p_265238_, CloneCommands.CommandFunction<CommandContext<CommandSourceStack>, ServerLevel> p_265621_, CloneCommands.CommandFunction<CommandContext<CommandSourceStack>, ServerLevel> p_265296_) {
      CloneCommands.CommandFunction<CommandContext<CommandSourceStack>, CloneCommands.DimensionAndPosition> commandfunction = (p_264737_) -> {
         return getLoadedDimensionAndPosition(p_264737_, p_265621_.apply(p_264737_), "begin");
      };
      CloneCommands.CommandFunction<CommandContext<CommandSourceStack>, CloneCommands.DimensionAndPosition> commandfunction1 = (p_264735_) -> {
         return getLoadedDimensionAndPosition(p_264735_, p_265621_.apply(p_264735_), "end");
      };
      CloneCommands.CommandFunction<CommandContext<CommandSourceStack>, CloneCommands.DimensionAndPosition> commandfunction2 = (p_264768_) -> {
         return getLoadedDimensionAndPosition(p_264768_, p_265296_.apply(p_264768_), "destination");
      };
      return Commands.argument("destination", BlockPosArgument.blockPos()).executes((p_264761_) -> {
         return clone(p_264761_.getSource(), commandfunction.apply(p_264761_), commandfunction1.apply(p_264761_), commandfunction2.apply(p_264761_), (p_180033_) -> {
            return true;
         }, CloneCommands.Mode.NORMAL);
      }).then(wrapWithCloneMode(commandfunction, commandfunction1, commandfunction2, (p_264738_) -> {
         return (p_180041_) -> {
            return true;
         };
      }, Commands.literal("replace").executes((p_264755_) -> {
         return clone(p_264755_.getSource(), commandfunction.apply(p_264755_), commandfunction1.apply(p_264755_), commandfunction2.apply(p_264755_), (p_180039_) -> {
            return true;
         }, CloneCommands.Mode.NORMAL);
      }))).then(wrapWithCloneMode(commandfunction, commandfunction1, commandfunction2, (p_264744_) -> {
         return FILTER_AIR;
      }, Commands.literal("masked").executes((p_264742_) -> {
         return clone(p_264742_.getSource(), commandfunction.apply(p_264742_), commandfunction1.apply(p_264742_), commandfunction2.apply(p_264742_), FILTER_AIR, CloneCommands.Mode.NORMAL);
      }))).then(Commands.literal("filtered").then(wrapWithCloneMode(commandfunction, commandfunction1, commandfunction2, (p_264745_) -> {
         return BlockPredicateArgument.getBlockPredicate(p_264745_, "filter");
      }, Commands.argument("filter", BlockPredicateArgument.blockPredicate(p_265238_)).executes((p_264733_) -> {
         return clone(p_264733_.getSource(), commandfunction.apply(p_264733_), commandfunction1.apply(p_264733_), commandfunction2.apply(p_264733_), BlockPredicateArgument.getBlockPredicate(p_264733_, "filter"), CloneCommands.Mode.NORMAL);
      }))));
   }

   private static ArgumentBuilder<CommandSourceStack, ?> wrapWithCloneMode(CloneCommands.CommandFunction<CommandContext<CommandSourceStack>, CloneCommands.DimensionAndPosition> p_265374_, CloneCommands.CommandFunction<CommandContext<CommandSourceStack>, CloneCommands.DimensionAndPosition> p_265134_, CloneCommands.CommandFunction<CommandContext<CommandSourceStack>, CloneCommands.DimensionAndPosition> p_265546_, CloneCommands.CommandFunction<CommandContext<CommandSourceStack>, Predicate<BlockInWorld>> p_265798_, ArgumentBuilder<CommandSourceStack, ?> p_265069_) {
      return p_265069_.then(Commands.literal("force").executes((p_264773_) -> {
         return clone(p_264773_.getSource(), p_265374_.apply(p_264773_), p_265134_.apply(p_264773_), p_265546_.apply(p_264773_), p_265798_.apply(p_264773_), CloneCommands.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_264766_) -> {
         return clone(p_264766_.getSource(), p_265374_.apply(p_264766_), p_265134_.apply(p_264766_), p_265546_.apply(p_264766_), p_265798_.apply(p_264766_), CloneCommands.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_264750_) -> {
         return clone(p_264750_.getSource(), p_265374_.apply(p_264750_), p_265134_.apply(p_264750_), p_265546_.apply(p_264750_), p_265798_.apply(p_264750_), CloneCommands.Mode.NORMAL);
      }));
   }

   private static int clone(CommandSourceStack p_265047_, CloneCommands.DimensionAndPosition p_265232_, CloneCommands.DimensionAndPosition p_265188_, CloneCommands.DimensionAndPosition p_265594_, Predicate<BlockInWorld> p_265585_, CloneCommands.Mode p_265530_) throws CommandSyntaxException {
      BlockPos blockpos = p_265232_.position();
      BlockPos blockpos1 = p_265188_.position();
      BoundingBox boundingbox = BoundingBox.fromCorners(blockpos, blockpos1);
      BlockPos blockpos2 = p_265594_.position();
      BlockPos blockpos3 = blockpos2.offset(boundingbox.getLength());
      BoundingBox boundingbox1 = BoundingBox.fromCorners(blockpos2, blockpos3);
      ServerLevel serverlevel = p_265232_.dimension();
      ServerLevel serverlevel1 = p_265594_.dimension();
      if (!p_265530_.canOverlap() && serverlevel == serverlevel1 && boundingbox1.intersects(boundingbox)) {
         throw ERROR_OVERLAP.create();
      } else {
         int i = boundingbox.getXSpan() * boundingbox.getYSpan() * boundingbox.getZSpan();
         int j = p_265047_.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
         if (i > j) {
            throw ERROR_AREA_TOO_LARGE.create(j, i);
         } else if (serverlevel.hasChunksAt(blockpos, blockpos1) && serverlevel1.hasChunksAt(blockpos2, blockpos3)) {
            List<CloneCommands.CloneBlockInfo> list = Lists.newArrayList();
            List<CloneCommands.CloneBlockInfo> list1 = Lists.newArrayList();
            List<CloneCommands.CloneBlockInfo> list2 = Lists.newArrayList();
            Deque<BlockPos> deque = Lists.newLinkedList();
            BlockPos blockpos4 = new BlockPos(boundingbox1.minX() - boundingbox.minX(), boundingbox1.minY() - boundingbox.minY(), boundingbox1.minZ() - boundingbox.minZ());

            for(int k = boundingbox.minZ(); k <= boundingbox.maxZ(); ++k) {
               for(int l = boundingbox.minY(); l <= boundingbox.maxY(); ++l) {
                  for(int i1 = boundingbox.minX(); i1 <= boundingbox.maxX(); ++i1) {
                     BlockPos blockpos5 = new BlockPos(i1, l, k);
                     BlockPos blockpos6 = blockpos5.offset(blockpos4);
                     BlockInWorld blockinworld = new BlockInWorld(serverlevel, blockpos5, false);
                     BlockState blockstate = blockinworld.getState();
                     if (p_265585_.test(blockinworld)) {
                        BlockEntity blockentity = serverlevel.getBlockEntity(blockpos5);
                        if (blockentity != null) {
                           CompoundTag compoundtag = blockentity.saveWithoutMetadata();
                           list1.add(new CloneCommands.CloneBlockInfo(blockpos6, blockstate, compoundtag));
                           deque.addLast(blockpos5);
                        } else if (!blockstate.isSolidRender(serverlevel, blockpos5) && !blockstate.isCollisionShapeFullBlock(serverlevel, blockpos5)) {
                           list2.add(new CloneCommands.CloneBlockInfo(blockpos6, blockstate, (CompoundTag)null));
                           deque.addFirst(blockpos5);
                        } else {
                           list.add(new CloneCommands.CloneBlockInfo(blockpos6, blockstate, (CompoundTag)null));
                           deque.addLast(blockpos5);
                        }
                     }
                  }
               }
            }

            if (p_265530_ == CloneCommands.Mode.MOVE) {
               for(BlockPos blockpos7 : deque) {
                  BlockEntity blockentity1 = serverlevel.getBlockEntity(blockpos7);
                  Clearable.tryClear(blockentity1);
                  serverlevel.setBlock(blockpos7, Blocks.BARRIER.defaultBlockState(), 2);
               }

               for(BlockPos blockpos8 : deque) {
                  serverlevel.setBlock(blockpos8, Blocks.AIR.defaultBlockState(), 3);
               }
            }

            List<CloneCommands.CloneBlockInfo> list3 = Lists.newArrayList();
            list3.addAll(list);
            list3.addAll(list1);
            list3.addAll(list2);
            List<CloneCommands.CloneBlockInfo> list4 = Lists.reverse(list3);

            for(CloneCommands.CloneBlockInfo clonecommands$cloneblockinfo : list4) {
               BlockEntity blockentity2 = serverlevel1.getBlockEntity(clonecommands$cloneblockinfo.pos);
               Clearable.tryClear(blockentity2);
               serverlevel1.setBlock(clonecommands$cloneblockinfo.pos, Blocks.BARRIER.defaultBlockState(), 2);
            }

            int j1 = 0;

            for(CloneCommands.CloneBlockInfo clonecommands$cloneblockinfo1 : list3) {
               if (serverlevel1.setBlock(clonecommands$cloneblockinfo1.pos, clonecommands$cloneblockinfo1.state, 2)) {
                  ++j1;
               }
            }

            for(CloneCommands.CloneBlockInfo clonecommands$cloneblockinfo2 : list1) {
               BlockEntity blockentity3 = serverlevel1.getBlockEntity(clonecommands$cloneblockinfo2.pos);
               if (clonecommands$cloneblockinfo2.tag != null && blockentity3 != null) {
                  blockentity3.load(clonecommands$cloneblockinfo2.tag);
                  blockentity3.setChanged();
               }

               serverlevel1.setBlock(clonecommands$cloneblockinfo2.pos, clonecommands$cloneblockinfo2.state, 2);
            }

            for(CloneCommands.CloneBlockInfo clonecommands$cloneblockinfo3 : list4) {
               serverlevel1.blockUpdated(clonecommands$cloneblockinfo3.pos, clonecommands$cloneblockinfo3.state.getBlock());
            }

            serverlevel1.getBlockTicks().copyAreaFrom(serverlevel.getBlockTicks(), boundingbox, blockpos4);
            if (j1 == 0) {
               throw ERROR_FAILED.create();
            } else {
               int k1 = j1;
               p_265047_.sendSuccess(() -> {
                  return Component.translatable("commands.clone.success", k1);
               }, true);
               return j1;
            }
         } else {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
         }
      }
   }

   static class CloneBlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      @Nullable
      public final CompoundTag tag;

      public CloneBlockInfo(BlockPos p_136783_, BlockState p_136784_, @Nullable CompoundTag p_136785_) {
         this.pos = p_136783_;
         this.state = p_136784_;
         this.tag = p_136785_;
      }
   }

   @FunctionalInterface
   interface CommandFunction<T, R> {
      R apply(T p_265571_) throws CommandSyntaxException;
   }

   static record DimensionAndPosition(ServerLevel dimension, BlockPos position) {
   }

   static enum Mode {
      FORCE(true),
      MOVE(true),
      NORMAL(false);

      private final boolean canOverlap;

      private Mode(boolean p_136795_) {
         this.canOverlap = p_136795_;
      }

      public boolean canOverlap() {
         return this.canOverlap;
      }
   }
}