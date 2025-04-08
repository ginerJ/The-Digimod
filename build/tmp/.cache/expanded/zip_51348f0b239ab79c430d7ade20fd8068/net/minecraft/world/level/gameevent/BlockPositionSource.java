package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BlockPositionSource implements PositionSource {
   public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create((p_157710_) -> {
      return p_157710_.group(BlockPos.CODEC.fieldOf("pos").forGetter((p_223611_) -> {
         return p_223611_.pos;
      })).apply(p_157710_, BlockPositionSource::new);
   });
   final BlockPos pos;

   public BlockPositionSource(BlockPos p_157703_) {
      this.pos = p_157703_;
   }

   public Optional<Vec3> getPosition(Level p_157708_) {
      return Optional.of(Vec3.atCenterOf(this.pos));
   }

   public PositionSourceType<?> getType() {
      return PositionSourceType.BLOCK;
   }

   public static class Type implements PositionSourceType<BlockPositionSource> {
      public BlockPositionSource read(FriendlyByteBuf p_157716_) {
         return new BlockPositionSource(p_157716_.readBlockPos());
      }

      public void write(FriendlyByteBuf p_157718_, BlockPositionSource p_157719_) {
         p_157718_.writeBlockPos(p_157719_.pos);
      }

      public Codec<BlockPositionSource> codec() {
         return BlockPositionSource.CODEC;
      }
   }
}