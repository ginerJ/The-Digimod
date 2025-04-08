package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public abstract class PlayerList {
   public static final File USERBANLIST_FILE = new File("banned-players.json");
   public static final File IPBANLIST_FILE = new File("banned-ips.json");
   public static final File OPLIST_FILE = new File("ops.json");
   public static final File WHITELIST_FILE = new File("whitelist.json");
   public static final Component CHAT_FILTERED_FULL = Component.translatable("chat.filtered_full");
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SEND_PLAYER_INFO_INTERVAL = 600;
   private static final SimpleDateFormat BAN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final MinecraftServer server;
   private final List<ServerPlayer> players = Lists.newArrayList();
   private final Map<UUID, ServerPlayer> playersByUUID = Maps.newHashMap();
   private final UserBanList bans = new UserBanList(USERBANLIST_FILE);
   private final IpBanList ipBans = new IpBanList(IPBANLIST_FILE);
   private final ServerOpList ops = new ServerOpList(OPLIST_FILE);
   private final UserWhiteList whitelist = new UserWhiteList(WHITELIST_FILE);
   private final Map<UUID, ServerStatsCounter> stats = Maps.newHashMap();
   private final Map<UUID, PlayerAdvancements> advancements = Maps.newHashMap();
   private final PlayerDataStorage playerIo;
   private boolean doWhiteList;
   private final LayeredRegistryAccess<RegistryLayer> registries;
   private final RegistryAccess.Frozen synchronizedRegistries;
   protected final int maxPlayers;
   private int viewDistance;
   private int simulationDistance;
   private boolean allowCheatsForAllPlayers;
   private static final boolean ALLOW_LOGOUTIVATOR = false;
   private int sendAllPlayerInfoIn;
   private final List<ServerPlayer> playersView = java.util.Collections.unmodifiableList(players);

   public PlayerList(MinecraftServer p_203842_, LayeredRegistryAccess<RegistryLayer> p_251844_, PlayerDataStorage p_203844_, int p_203845_) {
      this.server = p_203842_;
      this.registries = p_251844_;
      this.synchronizedRegistries = (new RegistryAccess.ImmutableRegistryAccess(RegistrySynchronization.networkedRegistries(p_251844_))).freeze();
      this.maxPlayers = p_203845_;
      this.playerIo = p_203844_;
   }

   public void placeNewPlayer(Connection p_11262_, ServerPlayer p_11263_) {
      GameProfile gameprofile = p_11263_.getGameProfile();
      GameProfileCache gameprofilecache = this.server.getProfileCache();
      String s;
      if (gameprofilecache != null) {
         Optional<GameProfile> optional = gameprofilecache.get(gameprofile.getId());
         s = optional.map(GameProfile::getName).orElse(gameprofile.getName());
         gameprofilecache.add(gameprofile);
      } else {
         s = gameprofile.getName();
      }

      CompoundTag compoundtag = this.load(p_11263_);
      ResourceKey<Level> resourcekey = compoundtag != null ? DimensionType.parseLegacy(new Dynamic<>(NbtOps.INSTANCE, compoundtag.get("Dimension"))).resultOrPartial(LOGGER::error).orElse(Level.OVERWORLD) : Level.OVERWORLD;
      ServerLevel serverlevel = this.server.getLevel(resourcekey);
      ServerLevel serverlevel1;
      if (serverlevel == null) {
         LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", (Object)resourcekey);
         serverlevel1 = this.server.overworld();
      } else {
         serverlevel1 = serverlevel;
      }

      p_11263_.setServerLevel(serverlevel1);
      String s1 = "local";
      if (p_11262_.getRemoteAddress() != null) {
         s1 = net.minecraftforge.network.DualStackUtils.getAddressString(p_11262_.getRemoteAddress());
      }

      LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", p_11263_.getName().getString(), s1, p_11263_.getId(), p_11263_.getX(), p_11263_.getY(), p_11263_.getZ());
      LevelData leveldata = serverlevel1.getLevelData();
      p_11263_.loadGameTypes(compoundtag);
      ServerGamePacketListenerImpl servergamepacketlistenerimpl = new ServerGamePacketListenerImpl(this.server, p_11262_, p_11263_);
      net.minecraftforge.network.NetworkHooks.sendMCRegistryPackets(p_11262_, "PLAY_TO_CLIENT");
      GameRules gamerules = serverlevel1.getGameRules();
      boolean flag = gamerules.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
      boolean flag1 = gamerules.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);
      servergamepacketlistenerimpl.send(new ClientboundLoginPacket(p_11263_.getId(), leveldata.isHardcore(), p_11263_.gameMode.getGameModeForPlayer(), p_11263_.gameMode.getPreviousGameModeForPlayer(), this.server.levelKeys(), this.synchronizedRegistries, serverlevel1.dimensionTypeId(), serverlevel1.dimension(), BiomeManager.obfuscateSeed(serverlevel1.getSeed()), this.getMaxPlayers(), this.viewDistance, this.simulationDistance, flag1, !flag, serverlevel1.isDebug(), serverlevel1.isFlat(), p_11263_.getLastDeathLocation(), p_11263_.getPortalCooldown()));
      servergamepacketlistenerimpl.send(new ClientboundUpdateEnabledFeaturesPacket(FeatureFlags.REGISTRY.toNames(serverlevel1.enabledFeatures())));
      servergamepacketlistenerimpl.send(new ClientboundCustomPayloadPacket(ClientboundCustomPayloadPacket.BRAND, (new FriendlyByteBuf(Unpooled.buffer())).writeUtf(this.getServer().getServerModName())));
      servergamepacketlistenerimpl.send(new ClientboundChangeDifficultyPacket(leveldata.getDifficulty(), leveldata.isDifficultyLocked()));
      servergamepacketlistenerimpl.send(new ClientboundPlayerAbilitiesPacket(p_11263_.getAbilities()));
      servergamepacketlistenerimpl.send(new ClientboundSetCarriedItemPacket(p_11263_.getInventory().selected));
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.OnDatapackSyncEvent(this, p_11263_));
      servergamepacketlistenerimpl.send(new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
      servergamepacketlistenerimpl.send(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(this.registries)));
      this.sendPlayerPermissionLevel(p_11263_);
      p_11263_.getStats().markAllDirty();
      p_11263_.getRecipeBook().sendInitialRecipeBook(p_11263_);
      this.updateEntireScoreboard(serverlevel1.getScoreboard(), p_11263_);
      this.server.invalidateStatus();
      MutableComponent mutablecomponent;
      if (p_11263_.getGameProfile().getName().equalsIgnoreCase(s)) {
         mutablecomponent = Component.translatable("multiplayer.player.joined", p_11263_.getDisplayName());
      } else {
         mutablecomponent = Component.translatable("multiplayer.player.joined.renamed", p_11263_.getDisplayName(), s);
      }

      this.broadcastSystemMessage(mutablecomponent.withStyle(ChatFormatting.YELLOW), false);
      servergamepacketlistenerimpl.teleport(p_11263_.getX(), p_11263_.getY(), p_11263_.getZ(), p_11263_.getYRot(), p_11263_.getXRot());
      ServerStatus serverstatus = this.server.getStatus();
      if (serverstatus != null) {
         p_11263_.sendServerStatus(serverstatus);
      }

      p_11263_.connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(this.players));
      this.players.add(p_11263_);
      this.playersByUUID.put(p_11263_.getUUID(), p_11263_);
      this.broadcastAll(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(p_11263_)));
      this.sendLevelInfo(p_11263_, serverlevel1);
      serverlevel1.addNewPlayer(p_11263_);
      this.server.getCustomBossEvents().onPlayerConnect(p_11263_);
      this.server.getServerResourcePack().ifPresent((p_215606_) -> {
         p_11263_.sendTexturePack(p_215606_.url(), p_215606_.hash(), p_215606_.isRequired(), p_215606_.prompt());
      });

      for(MobEffectInstance mobeffectinstance : p_11263_.getActiveEffects()) {
         servergamepacketlistenerimpl.send(new ClientboundUpdateMobEffectPacket(p_11263_.getId(), mobeffectinstance));
      }

      if (compoundtag != null && compoundtag.contains("RootVehicle", 10)) {
         CompoundTag compoundtag1 = compoundtag.getCompound("RootVehicle");
         Entity entity1 = EntityType.loadEntityRecursive(compoundtag1.getCompound("Entity"), serverlevel1, (p_215603_) -> {
            return !serverlevel1.addWithUUID(p_215603_) ? null : p_215603_;
         });
         if (entity1 != null) {
            UUID uuid;
            if (compoundtag1.hasUUID("Attach")) {
               uuid = compoundtag1.getUUID("Attach");
            } else {
               uuid = null;
            }

            if (entity1.getUUID().equals(uuid)) {
               p_11263_.startRiding(entity1, true);
            } else {
               for(Entity entity : entity1.getIndirectPassengers()) {
                  if (entity.getUUID().equals(uuid)) {
                     p_11263_.startRiding(entity, true);
                     break;
                  }
               }
            }

            if (!p_11263_.isPassenger()) {
               LOGGER.warn("Couldn't reattach entity to player");
               entity1.discard();

               for(Entity entity2 : entity1.getIndirectPassengers()) {
                  entity2.discard();
               }
            }
         }
      }

      p_11263_.initInventoryMenu();
      net.minecraftforge.event.ForgeEventFactory.firePlayerLoggedIn( p_11263_ );
   }

   protected void updateEntireScoreboard(ServerScoreboard p_11274_, ServerPlayer p_11275_) {
      Set<Objective> set = Sets.newHashSet();

      for(PlayerTeam playerteam : p_11274_.getPlayerTeams()) {
         p_11275_.connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(playerteam, true));
      }

      for(int i = 0; i < 19; ++i) {
         Objective objective = p_11274_.getDisplayObjective(i);
         if (objective != null && !set.contains(objective)) {
            for(Packet<?> packet : p_11274_.getStartTrackingPackets(objective)) {
               p_11275_.connection.send(packet);
            }

            set.add(objective);
         }
      }

   }

   public void addWorldborderListener(ServerLevel p_184210_) {
      p_184210_.getWorldBorder().addListener(new BorderChangeListener() {
         public void onBorderSizeSet(WorldBorder p_11321_, double p_11322_) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderSizePacket(p_11321_));
         }

         public void onBorderSizeLerping(WorldBorder p_11328_, double p_11329_, double p_11330_, long p_11331_) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderLerpSizePacket(p_11328_));
         }

         public void onBorderCenterSet(WorldBorder p_11324_, double p_11325_, double p_11326_) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderCenterPacket(p_11324_));
         }

         public void onBorderSetWarningTime(WorldBorder p_11333_, int p_11334_) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderWarningDelayPacket(p_11333_));
         }

         public void onBorderSetWarningBlocks(WorldBorder p_11339_, int p_11340_) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderWarningDistancePacket(p_11339_));
         }

         public void onBorderSetDamagePerBlock(WorldBorder p_11336_, double p_11337_) {
         }

         public void onBorderSetDamageSafeZOne(WorldBorder p_11342_, double p_11343_) {
         }
      });
   }

   @Nullable
   public CompoundTag load(ServerPlayer p_11225_) {
      CompoundTag compoundtag = this.server.getWorldData().getLoadedPlayerTag();
      CompoundTag compoundtag1;
      if (this.server.isSingleplayerOwner(p_11225_.getGameProfile()) && compoundtag != null) {
         compoundtag1 = compoundtag;
         p_11225_.load(compoundtag);
         LOGGER.debug("loading single player");
         net.minecraftforge.event.ForgeEventFactory.firePlayerLoadingEvent(p_11225_, this.playerIo, p_11225_.getUUID().toString());
      } else {
         compoundtag1 = this.playerIo.load(p_11225_);
      }

      return compoundtag1;
   }

   protected void save(ServerPlayer p_11277_) {
      if (p_11277_.connection == null) return;
      this.playerIo.save(p_11277_);
      ServerStatsCounter serverstatscounter = this.stats.get(p_11277_.getUUID());
      if (serverstatscounter != null) {
         serverstatscounter.save();
      }

      PlayerAdvancements playeradvancements = this.advancements.get(p_11277_.getUUID());
      if (playeradvancements != null) {
         playeradvancements.save();
      }

   }

   public void remove(ServerPlayer p_11287_) {
      net.minecraftforge.event.ForgeEventFactory.firePlayerLoggedOut(p_11287_);
      ServerLevel serverlevel = p_11287_.serverLevel();
      p_11287_.awardStat(Stats.LEAVE_GAME);
      this.save(p_11287_);
      if (p_11287_.isPassenger()) {
         Entity entity = p_11287_.getRootVehicle();
         if (entity.hasExactlyOnePlayerPassenger()) {
            LOGGER.debug("Removing player mount");
            p_11287_.stopRiding();
            entity.getPassengersAndSelf().forEach((p_215620_) -> {
               p_215620_.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
            });
         }
      }

      p_11287_.unRide();
      serverlevel.removePlayerImmediately(p_11287_, Entity.RemovalReason.UNLOADED_WITH_PLAYER);
      p_11287_.getAdvancements().stopListening();
      this.players.remove(p_11287_);
      this.server.getCustomBossEvents().onPlayerDisconnect(p_11287_);
      UUID uuid = p_11287_.getUUID();
      ServerPlayer serverplayer = this.playersByUUID.get(uuid);
      if (serverplayer == p_11287_) {
         this.playersByUUID.remove(uuid);
         this.stats.remove(uuid);
         this.advancements.remove(uuid);
      }

      this.broadcastAll(new ClientboundPlayerInfoRemovePacket(List.of(p_11287_.getUUID())));
   }

   @Nullable
   public Component canPlayerLogin(SocketAddress p_11257_, GameProfile p_11258_) {
      if (this.bans.isBanned(p_11258_)) {
         UserBanListEntry userbanlistentry = this.bans.get(p_11258_);
         MutableComponent mutablecomponent1 = Component.translatable("multiplayer.disconnect.banned.reason", userbanlistentry.getReason());
         if (userbanlistentry.getExpires() != null) {
            mutablecomponent1.append(Component.translatable("multiplayer.disconnect.banned.expiration", BAN_DATE_FORMAT.format(userbanlistentry.getExpires())));
         }

         return mutablecomponent1;
      } else if (!this.isWhiteListed(p_11258_)) {
         return Component.translatable("multiplayer.disconnect.not_whitelisted");
      } else if (this.ipBans.isBanned(p_11257_)) {
         IpBanListEntry ipbanlistentry = this.ipBans.get(p_11257_);
         MutableComponent mutablecomponent = Component.translatable("multiplayer.disconnect.banned_ip.reason", ipbanlistentry.getReason());
         if (ipbanlistentry.getExpires() != null) {
            mutablecomponent.append(Component.translatable("multiplayer.disconnect.banned_ip.expiration", BAN_DATE_FORMAT.format(ipbanlistentry.getExpires())));
         }

         return mutablecomponent;
      } else {
         return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(p_11258_) ? Component.translatable("multiplayer.disconnect.server_full") : null;
      }
   }

   public ServerPlayer getPlayerForLogin(GameProfile p_215625_) {
      UUID uuid = UUIDUtil.getOrCreatePlayerUUID(p_215625_);
      List<ServerPlayer> list = Lists.newArrayList();

      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayer serverplayer = this.players.get(i);
         if (serverplayer.getUUID().equals(uuid)) {
            list.add(serverplayer);
         }
      }

      ServerPlayer serverplayer2 = this.playersByUUID.get(p_215625_.getId());
      if (serverplayer2 != null && !list.contains(serverplayer2)) {
         list.add(serverplayer2);
      }

      for(ServerPlayer serverplayer1 : list) {
         serverplayer1.connection.disconnect(Component.translatable("multiplayer.disconnect.duplicate_login"));
      }

      return new ServerPlayer(this.server, this.server.overworld(), p_215625_);
   }

   public ServerPlayer respawn(ServerPlayer p_11237_, boolean p_11238_) {
      this.players.remove(p_11237_);
      p_11237_.serverLevel().removePlayerImmediately(p_11237_, Entity.RemovalReason.DISCARDED);
      BlockPos blockpos = p_11237_.getRespawnPosition();
      float f = p_11237_.getRespawnAngle();
      boolean flag = p_11237_.isRespawnForced();
      ServerLevel serverlevel = this.server.getLevel(p_11237_.getRespawnDimension());
      Optional<Vec3> optional;
      if (serverlevel != null && blockpos != null) {
         optional = Player.findRespawnPositionAndUseSpawnBlock(serverlevel, blockpos, f, flag, p_11238_);
      } else {
         optional = Optional.empty();
      }

      ServerLevel serverlevel1 = serverlevel != null && optional.isPresent() ? serverlevel : this.server.overworld();
      ServerPlayer serverplayer = new ServerPlayer(this.server, serverlevel1, p_11237_.getGameProfile());
      serverplayer.connection = p_11237_.connection;
      serverplayer.restoreFrom(p_11237_, p_11238_);
      serverplayer.setId(p_11237_.getId());
      serverplayer.setMainArm(p_11237_.getMainArm());

      for(String s : p_11237_.getTags()) {
         serverplayer.addTag(s);
      }

      boolean flag2 = false;
      if (optional.isPresent()) {
         BlockState blockstate = serverlevel1.getBlockState(blockpos);
         boolean flag1 = blockstate.is(Blocks.RESPAWN_ANCHOR);
         Vec3 vec3 = optional.get();
         float f1;
         if (!blockstate.is(BlockTags.BEDS) && !flag1) {
            f1 = f;
         } else {
            Vec3 vec31 = Vec3.atBottomCenterOf(blockpos).subtract(vec3).normalize();
            f1 = (float)Mth.wrapDegrees(Mth.atan2(vec31.z, vec31.x) * (double)(180F / (float)Math.PI) - 90.0D);
         }

         serverplayer.moveTo(vec3.x, vec3.y, vec3.z, f1, 0.0F);
         serverplayer.setRespawnPosition(serverlevel1.dimension(), blockpos, f, flag, false);
         flag2 = !p_11238_ && flag1;
      } else if (blockpos != null) {
         serverplayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
      }

      while(!serverlevel1.noCollision(serverplayer) && serverplayer.getY() < (double)serverlevel1.getMaxBuildHeight()) {
         serverplayer.setPos(serverplayer.getX(), serverplayer.getY() + 1.0D, serverplayer.getZ());
      }

      byte b0 = (byte)(p_11238_ ? 1 : 0);
      LevelData leveldata = serverplayer.level().getLevelData();
      serverplayer.connection.send(new ClientboundRespawnPacket(serverplayer.level().dimensionTypeId(), serverplayer.level().dimension(), BiomeManager.obfuscateSeed(serverplayer.serverLevel().getSeed()), serverplayer.gameMode.getGameModeForPlayer(), serverplayer.gameMode.getPreviousGameModeForPlayer(), serverplayer.level().isDebug(), serverplayer.serverLevel().isFlat(), b0, serverplayer.getLastDeathLocation(), serverplayer.getPortalCooldown()));
      serverplayer.connection.teleport(serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), serverplayer.getYRot(), serverplayer.getXRot());
      serverplayer.connection.send(new ClientboundSetDefaultSpawnPositionPacket(serverlevel1.getSharedSpawnPos(), serverlevel1.getSharedSpawnAngle()));
      serverplayer.connection.send(new ClientboundChangeDifficultyPacket(leveldata.getDifficulty(), leveldata.isDifficultyLocked()));
      serverplayer.connection.send(new ClientboundSetExperiencePacket(serverplayer.experienceProgress, serverplayer.totalExperience, serverplayer.experienceLevel));
      this.sendLevelInfo(serverplayer, serverlevel1);
      this.sendPlayerPermissionLevel(serverplayer);
      serverlevel1.addRespawnedPlayer(serverplayer);
      this.players.add(serverplayer);
      this.playersByUUID.put(serverplayer.getUUID(), serverplayer);
      serverplayer.initInventoryMenu();
      serverplayer.setHealth(serverplayer.getHealth());
      net.minecraftforge.event.ForgeEventFactory.firePlayerRespawnEvent(serverplayer, p_11238_);
      if (flag2) {
         serverplayer.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0F, 1.0F, serverlevel1.getRandom().nextLong()));
      }

      return serverplayer;
   }

   public void sendPlayerPermissionLevel(ServerPlayer p_11290_) {
      GameProfile gameprofile = p_11290_.getGameProfile();
      int i = this.server.getProfilePermissions(gameprofile);
      this.sendPlayerPermissionLevel(p_11290_, i);
   }

   public void tick() {
      if (++this.sendAllPlayerInfoIn > 600) {
         this.broadcastAll(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY), this.players));
         this.sendAllPlayerInfoIn = 0;
      }

   }

   public void broadcastAll(Packet<?> p_11269_) {
      for(ServerPlayer serverplayer : this.players) {
         serverplayer.connection.send(p_11269_);
      }

   }

   public void broadcastAll(Packet<?> p_11271_, ResourceKey<Level> p_11272_) {
      for(ServerPlayer serverplayer : this.players) {
         if (serverplayer.level().dimension() == p_11272_) {
            serverplayer.connection.send(p_11271_);
         }
      }

   }

   public void broadcastSystemToTeam(Player p_215622_, Component p_215623_) {
      Team team = p_215622_.getTeam();
      if (team != null) {
         for(String s : team.getPlayers()) {
            ServerPlayer serverplayer = this.getPlayerByName(s);
            if (serverplayer != null && serverplayer != p_215622_) {
               serverplayer.sendSystemMessage(p_215623_);
            }
         }

      }
   }

   public void broadcastSystemToAllExceptTeam(Player p_215650_, Component p_215651_) {
      Team team = p_215650_.getTeam();
      if (team == null) {
         this.broadcastSystemMessage(p_215651_, false);
      } else {
         for(int i = 0; i < this.players.size(); ++i) {
            ServerPlayer serverplayer = this.players.get(i);
            if (serverplayer.getTeam() != team) {
               serverplayer.sendSystemMessage(p_215651_);
            }
         }

      }
   }

   public String[] getPlayerNamesArray() {
      String[] astring = new String[this.players.size()];

      for(int i = 0; i < this.players.size(); ++i) {
         astring[i] = this.players.get(i).getGameProfile().getName();
      }

      return astring;
   }

   public UserBanList getBans() {
      return this.bans;
   }

   public IpBanList getIpBans() {
      return this.ipBans;
   }

   public void op(GameProfile p_11254_) {
      if (net.minecraftforge.event.ForgeEventFactory.onPermissionChanged(p_11254_, this.server.getOperatorUserPermissionLevel(), this)) return;
      this.ops.add(new ServerOpListEntry(p_11254_, this.server.getOperatorUserPermissionLevel(), this.ops.canBypassPlayerLimit(p_11254_)));
      ServerPlayer serverplayer = this.getPlayer(p_11254_.getId());
      if (serverplayer != null) {
         this.sendPlayerPermissionLevel(serverplayer);
      }

   }

   public void deop(GameProfile p_11281_) {
      if (net.minecraftforge.event.ForgeEventFactory.onPermissionChanged(p_11281_, 0, this)) return;
      this.ops.remove(p_11281_);
      ServerPlayer serverplayer = this.getPlayer(p_11281_.getId());
      if (serverplayer != null) {
         this.sendPlayerPermissionLevel(serverplayer);
      }

   }

   private void sendPlayerPermissionLevel(ServerPlayer p_11227_, int p_11228_) {
      if (p_11227_.connection != null) {
         byte b0;
         if (p_11228_ <= 0) {
            b0 = 24;
         } else if (p_11228_ >= 4) {
            b0 = 28;
         } else {
            b0 = (byte)(24 + p_11228_);
         }

         p_11227_.connection.send(new ClientboundEntityEventPacket(p_11227_, b0));
      }

      this.server.getCommands().sendCommands(p_11227_);
   }

   public boolean isWhiteListed(GameProfile p_11294_) {
      return !this.doWhiteList || this.ops.contains(p_11294_) || this.whitelist.contains(p_11294_);
   }

   public boolean isOp(GameProfile p_11304_) {
      return this.ops.contains(p_11304_) || this.server.isSingleplayerOwner(p_11304_) && this.server.getWorldData().getAllowCommands() || this.allowCheatsForAllPlayers;
   }

   @Nullable
   public ServerPlayer getPlayerByName(String p_11256_) {
      for(ServerPlayer serverplayer : this.players) {
         if (serverplayer.getGameProfile().getName().equalsIgnoreCase(p_11256_)) {
            return serverplayer;
         }
      }

      return null;
   }

   public void broadcast(@Nullable Player p_11242_, double p_11243_, double p_11244_, double p_11245_, double p_11246_, ResourceKey<Level> p_11247_, Packet<?> p_11248_) {
      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayer serverplayer = this.players.get(i);
         if (serverplayer != p_11242_ && serverplayer.level().dimension() == p_11247_) {
            double d0 = p_11243_ - serverplayer.getX();
            double d1 = p_11244_ - serverplayer.getY();
            double d2 = p_11245_ - serverplayer.getZ();
            if (d0 * d0 + d1 * d1 + d2 * d2 < p_11246_ * p_11246_) {
               serverplayer.connection.send(p_11248_);
            }
         }
      }

   }

   public void saveAll() {
      for(int i = 0; i < this.players.size(); ++i) {
         this.save(this.players.get(i));
      }

   }

   public UserWhiteList getWhiteList() {
      return this.whitelist;
   }

   public String[] getWhiteListNames() {
      return this.whitelist.getUserList();
   }

   public ServerOpList getOps() {
      return this.ops;
   }

   public String[] getOpNames() {
      return this.ops.getUserList();
   }

   public void reloadWhiteList() {
   }

   public void sendLevelInfo(ServerPlayer p_11230_, ServerLevel p_11231_) {
      WorldBorder worldborder = this.server.overworld().getWorldBorder();
      p_11230_.connection.send(new ClientboundInitializeBorderPacket(worldborder));
      p_11230_.connection.send(new ClientboundSetTimePacket(p_11231_.getGameTime(), p_11231_.getDayTime(), p_11231_.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
      p_11230_.connection.send(new ClientboundSetDefaultSpawnPositionPacket(p_11231_.getSharedSpawnPos(), p_11231_.getSharedSpawnAngle()));
      if (p_11231_.isRaining()) {
         p_11230_.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0F));
         p_11230_.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, p_11231_.getRainLevel(1.0F)));
         p_11230_.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, p_11231_.getThunderLevel(1.0F)));
      }

   }

   public void sendAllPlayerInfo(ServerPlayer p_11293_) {
      p_11293_.inventoryMenu.sendAllDataToRemote();
      p_11293_.resetSentInfo();
      p_11293_.connection.send(new ClientboundSetCarriedItemPacket(p_11293_.getInventory().selected));
   }

   public int getPlayerCount() {
      return this.players.size();
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public boolean isUsingWhitelist() {
      return this.doWhiteList;
   }

   public void setUsingWhiteList(boolean p_11276_) {
      this.doWhiteList = p_11276_;
   }

   public List<ServerPlayer> getPlayersWithAddress(String p_11283_) {
      List<ServerPlayer> list = Lists.newArrayList();

      for(ServerPlayer serverplayer : this.players) {
         if (serverplayer.getIpAddress().equals(p_11283_)) {
            list.add(serverplayer);
         }
      }

      return list;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public int getSimulationDistance() {
      return this.simulationDistance;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   @Nullable
   public CompoundTag getSingleplayerData() {
      return null;
   }

   public void setAllowCheatsForAllPlayers(boolean p_11285_) {
      this.allowCheatsForAllPlayers = p_11285_;
   }

   public void removeAll() {
      for(int i = 0; i < this.players.size(); ++i) {
         (this.players.get(i)).connection.disconnect(Component.translatable("multiplayer.disconnect.server_shutdown"));
      }

   }

   public void broadcastSystemMessage(Component p_240618_, boolean p_240644_) {
      this.broadcastSystemMessage(p_240618_, (p_215639_) -> {
         return p_240618_;
      }, p_240644_);
   }

   public void broadcastSystemMessage(Component p_240526_, Function<ServerPlayer, Component> p_240594_, boolean p_240648_) {
      this.server.sendSystemMessage(p_240526_);

      for(ServerPlayer serverplayer : this.players) {
         Component component = p_240594_.apply(serverplayer);
         if (component != null) {
            serverplayer.sendSystemMessage(component, p_240648_);
         }
      }

   }

   public void broadcastChatMessage(PlayerChatMessage p_243229_, CommandSourceStack p_243254_, ChatType.Bound p_243255_) {
      this.broadcastChatMessage(p_243229_, p_243254_::shouldFilterMessageTo, p_243254_.getPlayer(), p_243255_);
   }

   public void broadcastChatMessage(PlayerChatMessage p_243264_, ServerPlayer p_243234_, ChatType.Bound p_243204_) {
      this.broadcastChatMessage(p_243264_, p_243234_::shouldFilterMessageTo, p_243234_, p_243204_);
   }

   private void broadcastChatMessage(PlayerChatMessage p_249952_, Predicate<ServerPlayer> p_250784_, @Nullable ServerPlayer p_249623_, ChatType.Bound p_250276_) {
      boolean flag = this.verifyChatTrusted(p_249952_);
      this.server.logChatMessage(p_249952_.decoratedContent(), p_250276_, flag ? null : "Not Secure");
      OutgoingChatMessage outgoingchatmessage = OutgoingChatMessage.create(p_249952_);
      boolean flag1 = false;

      for(ServerPlayer serverplayer : this.players) {
         boolean flag2 = p_250784_.test(serverplayer);
         serverplayer.sendChatMessage(outgoingchatmessage, flag2, p_250276_);
         flag1 |= flag2 && p_249952_.isFullyFiltered();
      }

      if (flag1 && p_249623_ != null) {
         p_249623_.sendSystemMessage(CHAT_FILTERED_FULL);
      }

   }

   private boolean verifyChatTrusted(PlayerChatMessage p_251384_) {
      return p_251384_.hasSignature() && !p_251384_.hasExpiredServer(Instant.now());
   }

   public ServerStatsCounter getPlayerStats(Player p_11240_) {
      UUID uuid = p_11240_.getUUID();
      ServerStatsCounter serverstatscounter = this.stats.get(uuid);
      if (serverstatscounter == null) {
         File file1 = this.server.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile();
         File file2 = new File(file1, uuid + ".json");

         serverstatscounter = new ServerStatsCounter(this.server, file2);
         this.stats.put(uuid, serverstatscounter);
      }

      return serverstatscounter;
   }

   public PlayerAdvancements getPlayerAdvancements(ServerPlayer p_11297_) {
      UUID uuid = p_11297_.getUUID();
      PlayerAdvancements playeradvancements = this.advancements.get(uuid);
      if (playeradvancements == null) {
         Path path = this.server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).resolve(uuid + ".json");
         playeradvancements = new PlayerAdvancements(this.server.getFixerUpper(), this, this.server.getAdvancements(), path, p_11297_);
         this.advancements.put(uuid, playeradvancements);
      }

      // Forge: don't overwrite active player with a fake one.
      if (!(p_11297_ instanceof net.minecraftforge.common.util.FakePlayer))
      playeradvancements.setPlayer(p_11297_);
      return playeradvancements;
   }

   public void setViewDistance(int p_11218_) {
      this.viewDistance = p_11218_;
      this.broadcastAll(new ClientboundSetChunkCacheRadiusPacket(p_11218_));

      for(ServerLevel serverlevel : this.server.getAllLevels()) {
         if (serverlevel != null) {
            serverlevel.getChunkSource().setViewDistance(p_11218_);
         }
      }

   }

   public void setSimulationDistance(int p_184212_) {
      this.simulationDistance = p_184212_;
      this.broadcastAll(new ClientboundSetSimulationDistancePacket(p_184212_));

      for(ServerLevel serverlevel : this.server.getAllLevels()) {
         if (serverlevel != null) {
            serverlevel.getChunkSource().setSimulationDistance(p_184212_);
         }
      }

   }

   public List<ServerPlayer> getPlayers() {
      return this.playersView; //Unmodifiable view, we don't want people removing things without us knowing.
   }

   @Nullable
   public ServerPlayer getPlayer(UUID p_11260_) {
      return this.playersByUUID.get(p_11260_);
   }

   public boolean canBypassPlayerLimit(GameProfile p_11298_) {
      return false;
   }

   public void reloadResources() {
      for(PlayerAdvancements playeradvancements : this.advancements.values()) {
         playeradvancements.reload(this.server.getAdvancements());
      }

      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.OnDatapackSyncEvent(this, null));
      this.broadcastAll(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(this.registries)));
      ClientboundUpdateRecipesPacket clientboundupdaterecipespacket = new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes());

      for(ServerPlayer serverplayer : this.players) {
         serverplayer.connection.send(clientboundupdaterecipespacket);
         serverplayer.getRecipeBook().sendInitialRecipeBook(serverplayer);
      }

   }

   public boolean isAllowCheatsForAllPlayers() {
      return this.allowCheatsForAllPlayers;
   }
}
