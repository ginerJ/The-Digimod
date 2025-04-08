package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public class PacketBundleUnpacker extends MessageToMessageEncoder<Packet<?>> {
   private final PacketFlow flow;

   public PacketBundleUnpacker(PacketFlow p_265529_) {
      this.flow = p_265529_;
   }

   protected void encode(ChannelHandlerContext p_265691_, Packet<?> p_265038_, List<Object> p_265735_) throws Exception {
      BundlerInfo.Provider bundlerinfo$provider = p_265691_.channel().attr(BundlerInfo.BUNDLER_PROVIDER).get();
      if (bundlerinfo$provider == null) {
         throw new EncoderException("Bundler not configured: " + p_265038_);
      } else {
         bundlerinfo$provider.getBundlerInfo(this.flow).unbundlePacket(p_265038_, p_265735_::add);
      }
   }
}