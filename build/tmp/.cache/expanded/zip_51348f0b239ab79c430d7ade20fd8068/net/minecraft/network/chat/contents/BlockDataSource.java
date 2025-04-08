package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public record BlockDataSource(String posPattern, @Nullable Coordinates compiledPos) implements DataSource {
   public BlockDataSource(String p_237312_) {
      this(p_237312_, compilePos(p_237312_));
   }

   @Nullable
   private static Coordinates compilePos(String p_237318_) {
      try {
         return BlockPosArgument.blockPos().parse(new StringReader(p_237318_));
      } catch (CommandSyntaxException commandsyntaxexception) {
         return null;
      }
   }

   public Stream<CompoundTag> getData(CommandSourceStack p_237323_) {
      if (this.compiledPos != null) {
         ServerLevel serverlevel = p_237323_.getLevel();
         BlockPos blockpos = this.compiledPos.getBlockPos(p_237323_);
         if (serverlevel.isLoaded(blockpos)) {
            BlockEntity blockentity = serverlevel.getBlockEntity(blockpos);
            if (blockentity != null) {
               return Stream.of(blockentity.saveWithFullMetadata());
            }
         }
      }

      return Stream.empty();
   }

   public String toString() {
      return "block=" + this.posPattern;
   }

   public boolean equals(Object p_237321_) {
      if (this == p_237321_) {
         return true;
      } else {
         if (p_237321_ instanceof BlockDataSource) {
            BlockDataSource blockdatasource = (BlockDataSource)p_237321_;
            if (this.posPattern.equals(blockdatasource.posPattern)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return this.posPattern.hashCode();
   }
}