package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class ClientboundMapItemDataPacket implements Packet<ClientGamePacketListener> {
   private final int mapId;
   private final byte scale;
   private final boolean locked;
   @Nullable
   private final List<MapDecoration> decorations;
   @Nullable
   private final MapItemSavedData.MapPatch colorPatch;

   public ClientboundMapItemDataPacket(int p_178970_, byte p_178971_, boolean p_178972_, @Nullable Collection<MapDecoration> p_178973_, @Nullable MapItemSavedData.MapPatch p_178974_) {
      this.mapId = p_178970_;
      this.scale = p_178971_;
      this.locked = p_178972_;
      this.decorations = p_178973_ != null ? Lists.newArrayList(p_178973_) : null;
      this.colorPatch = p_178974_;
   }

   public ClientboundMapItemDataPacket(FriendlyByteBuf p_178976_) {
      this.mapId = p_178976_.readVarInt();
      this.scale = p_178976_.readByte();
      this.locked = p_178976_.readBoolean();
      this.decorations = p_178976_.readNullable((p_237731_) -> {
         return p_237731_.readList((p_178981_) -> {
            MapDecoration.Type mapdecoration$type = p_178981_.readEnum(MapDecoration.Type.class);
            byte b0 = p_178981_.readByte();
            byte b1 = p_178981_.readByte();
            byte b2 = (byte)(p_178981_.readByte() & 15);
            Component component = p_178981_.readNullable(FriendlyByteBuf::readComponent);
            return new MapDecoration(mapdecoration$type, b0, b1, b2, component);
         });
      });
      int i = p_178976_.readUnsignedByte();
      if (i > 0) {
         int j = p_178976_.readUnsignedByte();
         int k = p_178976_.readUnsignedByte();
         int l = p_178976_.readUnsignedByte();
         byte[] abyte = p_178976_.readByteArray();
         this.colorPatch = new MapItemSavedData.MapPatch(k, l, i, j, abyte);
      } else {
         this.colorPatch = null;
      }

   }

   public void write(FriendlyByteBuf p_132447_) {
      p_132447_.writeVarInt(this.mapId);
      p_132447_.writeByte(this.scale);
      p_132447_.writeBoolean(this.locked);
      p_132447_.writeNullable(this.decorations, (p_237728_, p_237729_) -> {
         p_237728_.writeCollection(p_237729_, (p_237725_, p_237726_) -> {
            p_237725_.writeEnum(p_237726_.getType());
            p_237725_.writeByte(p_237726_.getX());
            p_237725_.writeByte(p_237726_.getY());
            p_237725_.writeByte(p_237726_.getRot() & 15);
            p_237725_.writeNullable(p_237726_.getName(), FriendlyByteBuf::writeComponent);
         });
      });
      if (this.colorPatch != null) {
         p_132447_.writeByte(this.colorPatch.width);
         p_132447_.writeByte(this.colorPatch.height);
         p_132447_.writeByte(this.colorPatch.startX);
         p_132447_.writeByte(this.colorPatch.startY);
         p_132447_.writeByteArray(this.colorPatch.mapColors);
      } else {
         p_132447_.writeByte(0);
      }

   }

   public void handle(ClientGamePacketListener p_132444_) {
      p_132444_.handleMapItemData(this);
   }

   public int getMapId() {
      return this.mapId;
   }

   public void applyToMap(MapItemSavedData p_132438_) {
      if (this.decorations != null) {
         p_132438_.addClientSideDecorations(this.decorations);
      }

      if (this.colorPatch != null) {
         this.colorPatch.applyToMap(p_132438_);
      }

   }

   public byte getScale() {
      return this.scale;
   }

   public boolean isLocked() {
      return this.locked;
   }
}