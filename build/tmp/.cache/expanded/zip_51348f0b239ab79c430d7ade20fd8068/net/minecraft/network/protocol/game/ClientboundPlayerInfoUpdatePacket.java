package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class ClientboundPlayerInfoUpdatePacket implements Packet<ClientGamePacketListener> {
   private final EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions;
   private final List<ClientboundPlayerInfoUpdatePacket.Entry> entries;

   public ClientboundPlayerInfoUpdatePacket(EnumSet<ClientboundPlayerInfoUpdatePacket.Action> p_251739_, Collection<ServerPlayer> p_251579_) {
      this.actions = p_251739_;
      this.entries = p_251579_.stream().map(ClientboundPlayerInfoUpdatePacket.Entry::new).toList();
   }

   public ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action p_251648_, ServerPlayer p_252273_) {
      this.actions = EnumSet.of(p_251648_);
      this.entries = List.of(new ClientboundPlayerInfoUpdatePacket.Entry(p_252273_));
   }

   public static ClientboundPlayerInfoUpdatePacket createPlayerInitializing(Collection<ServerPlayer> p_252314_) {
      EnumSet<ClientboundPlayerInfoUpdatePacket.Action> enumset = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME);
      return new ClientboundPlayerInfoUpdatePacket(enumset, p_252314_);
   }

   public ClientboundPlayerInfoUpdatePacket(FriendlyByteBuf p_251820_) {
      this.actions = p_251820_.readEnumSet(ClientboundPlayerInfoUpdatePacket.Action.class);
      this.entries = p_251820_.readList((p_249950_) -> {
         ClientboundPlayerInfoUpdatePacket.EntryBuilder clientboundplayerinfoupdatepacket$entrybuilder = new ClientboundPlayerInfoUpdatePacket.EntryBuilder(p_249950_.readUUID());

         for(ClientboundPlayerInfoUpdatePacket.Action clientboundplayerinfoupdatepacket$action : this.actions) {
            clientboundplayerinfoupdatepacket$action.reader.read(clientboundplayerinfoupdatepacket$entrybuilder, p_249950_);
         }

         return clientboundplayerinfoupdatepacket$entrybuilder.build();
      });
   }

   public void write(FriendlyByteBuf p_249907_) {
      p_249907_.writeEnumSet(this.actions, ClientboundPlayerInfoUpdatePacket.Action.class);
      p_249907_.writeCollection(this.entries, (p_251434_, p_252303_) -> {
         p_251434_.writeUUID(p_252303_.profileId());

         for(ClientboundPlayerInfoUpdatePacket.Action clientboundplayerinfoupdatepacket$action : this.actions) {
            clientboundplayerinfoupdatepacket$action.writer.write(p_251434_, p_252303_);
         }

      });
   }

   public void handle(ClientGamePacketListener p_249935_) {
      p_249935_.handlePlayerInfoUpdate(this);
   }

   public EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions() {
      return this.actions;
   }

   public List<ClientboundPlayerInfoUpdatePacket.Entry> entries() {
      return this.entries;
   }

   public List<ClientboundPlayerInfoUpdatePacket.Entry> newEntries() {
      return this.actions.contains(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER) ? this.entries : List.of();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("actions", this.actions).add("entries", this.entries).toString();
   }

   public static enum Action {
      ADD_PLAYER((p_251116_, p_251884_) -> {
         GameProfile gameprofile = new GameProfile(p_251116_.profileId, p_251884_.readUtf(16));
         gameprofile.getProperties().putAll(p_251884_.readGameProfileProperties());
         p_251116_.profile = gameprofile;
      }, (p_252022_, p_250357_) -> {
         p_252022_.writeUtf(p_250357_.profile().getName(), 16);
         p_252022_.writeGameProfileProperties(p_250357_.profile().getProperties());
      }),
      INITIALIZE_CHAT((p_253468_, p_253469_) -> {
         p_253468_.chatSession = p_253469_.readNullable(RemoteChatSession.Data::read);
      }, (p_253470_, p_253471_) -> {
         p_253470_.writeNullable(p_253471_.chatSession, RemoteChatSession.Data::write);
      }),
      UPDATE_GAME_MODE((p_251118_, p_248955_) -> {
         p_251118_.gameMode = GameType.byId(p_248955_.readVarInt());
      }, (p_249222_, p_250996_) -> {
         p_249222_.writeVarInt(p_250996_.gameMode().getId());
      }),
      UPDATE_LISTED((p_248777_, p_248837_) -> {
         p_248777_.listed = p_248837_.readBoolean();
      }, (p_249355_, p_251658_) -> {
         p_249355_.writeBoolean(p_251658_.listed());
      }),
      UPDATE_LATENCY((p_252263_, p_248964_) -> {
         p_252263_.latency = p_248964_.readVarInt();
      }, (p_248830_, p_251312_) -> {
         p_248830_.writeVarInt(p_251312_.latency());
      }),
      UPDATE_DISPLAY_NAME((p_248840_, p_251000_) -> {
         p_248840_.displayName = p_251000_.readNullable(FriendlyByteBuf::readComponent);
      }, (p_251723_, p_251870_) -> {
         p_251723_.writeNullable(p_251870_.displayName(), FriendlyByteBuf::writeComponent);
      });

      final ClientboundPlayerInfoUpdatePacket.Action.Reader reader;
      final ClientboundPlayerInfoUpdatePacket.Action.Writer writer;

      private Action(ClientboundPlayerInfoUpdatePacket.Action.Reader p_249392_, ClientboundPlayerInfoUpdatePacket.Action.Writer p_250487_) {
         this.reader = p_249392_;
         this.writer = p_250487_;
      }

      public interface Reader {
         void read(ClientboundPlayerInfoUpdatePacket.EntryBuilder p_251859_, FriendlyByteBuf p_249972_);
      }

      public interface Writer {
         void write(FriendlyByteBuf p_249775_, ClientboundPlayerInfoUpdatePacket.Entry p_249783_);
      }
   }

   public static record Entry(UUID profileId, GameProfile profile, boolean listed, int latency, GameType gameMode, @Nullable Component displayName, @Nullable RemoteChatSession.Data chatSession) {
      Entry(ServerPlayer p_252094_) {
         this(p_252094_.getUUID(), p_252094_.getGameProfile(), true, p_252094_.latency, p_252094_.gameMode.getGameModeForPlayer(), p_252094_.getTabListDisplayName(), Optionull.map(p_252094_.getChatSession(), RemoteChatSession::asData));
      }
   }

   static class EntryBuilder {
      final UUID profileId;
      GameProfile profile;
      boolean listed;
      int latency;
      GameType gameMode = GameType.DEFAULT_MODE;
      @Nullable
      Component displayName;
      @Nullable
      RemoteChatSession.Data chatSession;

      EntryBuilder(UUID p_251670_) {
         this.profileId = p_251670_;
         this.profile = new GameProfile(p_251670_, (String)null);
      }

      ClientboundPlayerInfoUpdatePacket.Entry build() {
         return new ClientboundPlayerInfoUpdatePacket.Entry(this.profileId, this.profile, this.listed, this.latency, this.gameMode, this.displayName, this.chatSession);
      }
   }
}