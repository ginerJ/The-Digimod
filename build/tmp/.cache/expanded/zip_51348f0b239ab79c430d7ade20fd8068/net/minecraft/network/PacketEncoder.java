package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PacketFlow flow;

   public PacketEncoder(PacketFlow p_130543_) {
      this.flow = p_130543_;
   }

   protected void encode(ChannelHandlerContext p_130545_, Packet<?> p_130546_, ByteBuf p_130547_) throws Exception {
      ConnectionProtocol connectionprotocol = p_130545_.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get();
      if (connectionprotocol == null) {
         throw new RuntimeException("ConnectionProtocol unknown: " + p_130546_);
      } else {
         int i = connectionprotocol.getPacketId(this.flow, p_130546_);
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Connection.PACKET_SENT_MARKER, "OUT: [{}:{}] {}", p_130545_.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get(), i, p_130546_.getClass().getName());
         }

         if (i == -1) {
            throw new IOException("Can't serialize unregistered packet");
         } else {
            FriendlyByteBuf friendlybytebuf = new FriendlyByteBuf(p_130547_);
            friendlybytebuf.writeVarInt(i);

            try {
               int j = friendlybytebuf.writerIndex();
               p_130546_.write(friendlybytebuf);
               int k = friendlybytebuf.writerIndex() - j;
               if (k > 8388608) {
                  throw new IllegalArgumentException("Packet too big (is " + k + ", should be less than 8388608): " + p_130546_);
               } else {
                  int l = p_130545_.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get().getId();
                  JvmProfiler.INSTANCE.onPacketSent(l, i, p_130545_.channel().remoteAddress(), k);
               }
            } catch (Throwable throwable) {
               LOGGER.error("Error receiving packet {}", i, throwable);
               if (p_130546_.isSkippable()) {
                  throw new SkipPacketException(throwable);
               } else {
                  throw throwable;
               }
            }
         }
      }
   }
}