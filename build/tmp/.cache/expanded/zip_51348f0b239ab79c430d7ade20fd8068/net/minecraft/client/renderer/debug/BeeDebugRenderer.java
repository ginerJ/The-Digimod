package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final boolean SHOW_GOAL_FOR_ALL_BEES = true;
   private static final boolean SHOW_NAME_FOR_ALL_BEES = true;
   private static final boolean SHOW_HIVE_FOR_ALL_BEES = true;
   private static final boolean SHOW_FLOWER_POS_FOR_ALL_BEES = true;
   private static final boolean SHOW_TRAVEL_TICKS_FOR_ALL_BEES = true;
   private static final boolean SHOW_PATH_FOR_ALL_BEES = false;
   private static final boolean SHOW_GOAL_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_NAME_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_HIVE_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_FLOWER_POS_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_TRAVEL_TICKS_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_PATH_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_HIVE_MEMBERS = true;
   private static final boolean SHOW_BLACKLISTS = true;
   private static final int MAX_RENDER_DIST_FOR_HIVE_OVERLAY = 30;
   private static final int MAX_RENDER_DIST_FOR_BEE_OVERLAY = 30;
   private static final int MAX_TARGETING_DIST = 8;
   private static final int HIVE_TIMEOUT = 20;
   private static final float TEXT_SCALE = 0.02F;
   private static final int WHITE = -1;
   private static final int YELLOW = -256;
   private static final int ORANGE = -23296;
   private static final int GREEN = -16711936;
   private static final int GRAY = -3355444;
   private static final int PINK = -98404;
   private static final int RED = -65536;
   private final Minecraft minecraft;
   private final Map<BlockPos, BeeDebugRenderer.HiveInfo> hives = Maps.newHashMap();
   private final Map<UUID, BeeDebugRenderer.BeeInfo> beeInfosPerEntity = Maps.newHashMap();
   private UUID lastLookedAtUuid;

   public BeeDebugRenderer(Minecraft p_113053_) {
      this.minecraft = p_113053_;
   }

   public void clear() {
      this.hives.clear();
      this.beeInfosPerEntity.clear();
      this.lastLookedAtUuid = null;
   }

   public void addOrUpdateHiveInfo(BeeDebugRenderer.HiveInfo p_113072_) {
      this.hives.put(p_113072_.pos, p_113072_);
   }

   public void addOrUpdateBeeInfo(BeeDebugRenderer.BeeInfo p_113067_) {
      this.beeInfosPerEntity.put(p_113067_.uuid, p_113067_);
   }

   public void removeBeeInfo(int p_173764_) {
      this.beeInfosPerEntity.values().removeIf((p_173767_) -> {
         return p_173767_.id == p_173764_;
      });
   }

   public void render(PoseStack p_113061_, MultiBufferSource p_113062_, double p_113063_, double p_113064_, double p_113065_) {
      this.clearRemovedHives();
      this.clearRemovedBees();
      this.doRender(p_113061_, p_113062_);
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void clearRemovedBees() {
      this.beeInfosPerEntity.entrySet().removeIf((p_113132_) -> {
         return this.minecraft.level.getEntity((p_113132_.getValue()).id) == null;
      });
   }

   private void clearRemovedHives() {
      long i = this.minecraft.level.getGameTime() - 20L;
      this.hives.entrySet().removeIf((p_113057_) -> {
         return (p_113057_.getValue()).lastSeen < i;
      });
   }

   private void doRender(PoseStack p_270886_, MultiBufferSource p_270808_) {
      BlockPos blockpos = this.getCamera().getBlockPosition();
      this.beeInfosPerEntity.values().forEach((p_269703_) -> {
         if (this.isPlayerCloseEnoughToMob(p_269703_)) {
            this.renderBeeInfo(p_270886_, p_270808_, p_269703_);
         }

      });
      this.renderFlowerInfos(p_270886_, p_270808_);

      for(BlockPos blockpos1 : this.hives.keySet()) {
         if (blockpos.closerThan(blockpos1, 30.0D)) {
            highlightHive(p_270886_, p_270808_, blockpos1);
         }
      }

      Map<BlockPos, Set<UUID>> map = this.createHiveBlacklistMap();
      this.hives.values().forEach((p_269692_) -> {
         if (blockpos.closerThan(p_269692_.pos, 30.0D)) {
            Set<UUID> set = map.get(p_269692_.pos);
            this.renderHiveInfo(p_270886_, p_270808_, p_269692_, (Collection<UUID>)(set == null ? Sets.newHashSet() : set));
         }

      });
      this.getGhostHives().forEach((p_269699_, p_269700_) -> {
         if (blockpos.closerThan(p_269699_, 30.0D)) {
            this.renderGhostHive(p_270886_, p_270808_, p_269699_, p_269700_);
         }

      });
   }

   private Map<BlockPos, Set<UUID>> createHiveBlacklistMap() {
      Map<BlockPos, Set<UUID>> map = Maps.newHashMap();
      this.beeInfosPerEntity.values().forEach((p_113135_) -> {
         p_113135_.blacklistedHives.forEach((p_173771_) -> {
            map.computeIfAbsent(p_173771_, (p_173777_) -> {
               return Sets.newHashSet();
            }).add(p_113135_.getUuid());
         });
      });
      return map;
   }

   private void renderFlowerInfos(PoseStack p_270578_, MultiBufferSource p_270098_) {
      Map<BlockPos, Set<UUID>> map = Maps.newHashMap();
      this.beeInfosPerEntity.values().stream().filter(BeeDebugRenderer.BeeInfo::hasFlower).forEach((p_113121_) -> {
         map.computeIfAbsent(p_113121_.flowerPos, (p_173775_) -> {
            return Sets.newHashSet();
         }).add(p_113121_.getUuid());
      });
      map.entrySet().forEach((p_269695_) -> {
         BlockPos blockpos = p_269695_.getKey();
         Set<UUID> set = p_269695_.getValue();
         Set<String> set1 = set.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
         int i = 1;
         renderTextOverPos(p_270578_, p_270098_, set1.toString(), blockpos, i++, -256);
         renderTextOverPos(p_270578_, p_270098_, "Flower", blockpos, i++, -1);
         float f = 0.05F;
         DebugRenderer.renderFilledBox(p_270578_, p_270098_, blockpos, 0.05F, 0.8F, 0.8F, 0.0F, 0.3F);
      });
   }

   private static String getBeeUuidsAsString(Collection<UUID> p_113116_) {
      if (p_113116_.isEmpty()) {
         return "-";
      } else {
         return p_113116_.size() > 3 ? p_113116_.size() + " bees" : p_113116_.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet()).toString();
      }
   }

   private static void highlightHive(PoseStack p_270133_, MultiBufferSource p_270766_, BlockPos p_270687_) {
      float f = 0.05F;
      DebugRenderer.renderFilledBox(p_270133_, p_270766_, p_270687_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostHive(PoseStack p_270949_, MultiBufferSource p_270718_, BlockPos p_270550_, List<String> p_270221_) {
      float f = 0.05F;
      DebugRenderer.renderFilledBox(p_270949_, p_270718_, p_270550_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      renderTextOverPos(p_270949_, p_270718_, "" + p_270221_, p_270550_, 0, -256);
      renderTextOverPos(p_270949_, p_270718_, "Ghost Hive", p_270550_, 1, -65536);
   }

   private void renderHiveInfo(PoseStack p_270194_, MultiBufferSource p_270431_, BeeDebugRenderer.HiveInfo p_270658_, Collection<UUID> p_270946_) {
      int i = 0;
      if (!p_270946_.isEmpty()) {
         renderTextOverHive(p_270194_, p_270431_, "Blacklisted by " + getBeeUuidsAsString(p_270946_), p_270658_, i++, -65536);
      }

      renderTextOverHive(p_270194_, p_270431_, "Out: " + getBeeUuidsAsString(this.getHiveMembers(p_270658_.pos)), p_270658_, i++, -3355444);
      if (p_270658_.occupantCount == 0) {
         renderTextOverHive(p_270194_, p_270431_, "In: -", p_270658_, i++, -256);
      } else if (p_270658_.occupantCount == 1) {
         renderTextOverHive(p_270194_, p_270431_, "In: 1 bee", p_270658_, i++, -256);
      } else {
         renderTextOverHive(p_270194_, p_270431_, "In: " + p_270658_.occupantCount + " bees", p_270658_, i++, -256);
      }

      renderTextOverHive(p_270194_, p_270431_, "Honey: " + p_270658_.honeyLevel, p_270658_, i++, -23296);
      renderTextOverHive(p_270194_, p_270431_, p_270658_.hiveType + (p_270658_.sedated ? " (sedated)" : ""), p_270658_, i++, -1);
   }

   private void renderPath(PoseStack p_270424_, MultiBufferSource p_270123_, BeeDebugRenderer.BeeInfo p_270137_) {
      if (p_270137_.path != null) {
         PathfindingRenderer.renderPath(p_270424_, p_270123_, p_270137_.path, 0.5F, false, false, this.getCamera().getPosition().x(), this.getCamera().getPosition().y(), this.getCamera().getPosition().z());
      }

   }

   private void renderBeeInfo(PoseStack p_270154_, MultiBufferSource p_270397_, BeeDebugRenderer.BeeInfo p_270783_) {
      boolean flag = this.isBeeSelected(p_270783_);
      int i = 0;
      renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, i++, p_270783_.toString(), -1, 0.03F);
      if (p_270783_.hivePos == null) {
         renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, i++, "No hive", -98404, 0.02F);
      } else {
         renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, i++, "Hive: " + this.getPosDescription(p_270783_, p_270783_.hivePos), -256, 0.02F);
      }

      if (p_270783_.flowerPos == null) {
         renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, i++, "No flower", -98404, 0.02F);
      } else {
         renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, i++, "Flower: " + this.getPosDescription(p_270783_, p_270783_.flowerPos), -256, 0.02F);
      }

      for(String s : p_270783_.goals) {
         renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, i++, s, -16711936, 0.02F);
      }

      if (flag) {
         this.renderPath(p_270154_, p_270397_, p_270783_);
      }

      if (p_270783_.travelTicks > 0) {
         int j = p_270783_.travelTicks < 600 ? -3355444 : -23296;
         renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, i++, "Travelling: " + p_270783_.travelTicks + " ticks", j, 0.02F);
      }

   }

   private static void renderTextOverHive(PoseStack p_270915_, MultiBufferSource p_270663_, String p_270119_, BeeDebugRenderer.HiveInfo p_270243_, int p_270930_, int p_270094_) {
      BlockPos blockpos = p_270243_.pos;
      renderTextOverPos(p_270915_, p_270663_, p_270119_, blockpos, p_270930_, p_270094_);
   }

   private static void renderTextOverPos(PoseStack p_270438_, MultiBufferSource p_270244_, String p_270486_, BlockPos p_270062_, int p_270574_, int p_270228_) {
      double d0 = 1.3D;
      double d1 = 0.2D;
      double d2 = (double)p_270062_.getX() + 0.5D;
      double d3 = (double)p_270062_.getY() + 1.3D + (double)p_270574_ * 0.2D;
      double d4 = (double)p_270062_.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(p_270438_, p_270244_, p_270486_, d2, d3, d4, p_270228_, 0.02F, true, 0.0F, true);
   }

   private static void renderTextOverMob(PoseStack p_270426_, MultiBufferSource p_270600_, Position p_270548_, int p_270592_, String p_270198_, int p_270792_, float p_270938_) {
      double d0 = 2.4D;
      double d1 = 0.25D;
      BlockPos blockpos = BlockPos.containing(p_270548_);
      double d2 = (double)blockpos.getX() + 0.5D;
      double d3 = p_270548_.y() + 2.4D + (double)p_270592_ * 0.25D;
      double d4 = (double)blockpos.getZ() + 0.5D;
      float f = 0.5F;
      DebugRenderer.renderFloatingText(p_270426_, p_270600_, p_270198_, d2, d3, d4, p_270792_, p_270938_, false, 0.5F, true);
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }

   private Set<String> getHiveMemberNames(BeeDebugRenderer.HiveInfo p_173773_) {
      return this.getHiveMembers(p_173773_.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
   }

   private String getPosDescription(BeeDebugRenderer.BeeInfo p_113069_, BlockPos p_113070_) {
      double d0 = Math.sqrt(p_113070_.distToCenterSqr(p_113069_.pos));
      double d1 = (double)Math.round(d0 * 10.0D) / 10.0D;
      return p_113070_.toShortString() + " (dist " + d1 + ")";
   }

   private boolean isBeeSelected(BeeDebugRenderer.BeeInfo p_113143_) {
      return Objects.equals(this.lastLookedAtUuid, p_113143_.uuid);
   }

   private boolean isPlayerCloseEnoughToMob(BeeDebugRenderer.BeeInfo p_113148_) {
      Player player = this.minecraft.player;
      BlockPos blockpos = BlockPos.containing(player.getX(), p_113148_.pos.y(), player.getZ());
      BlockPos blockpos1 = BlockPos.containing(p_113148_.pos);
      return blockpos.closerThan(blockpos1, 30.0D);
   }

   private Collection<UUID> getHiveMembers(BlockPos p_113130_) {
      return this.beeInfosPerEntity.values().stream().filter((p_113087_) -> {
         return p_113087_.hasHive(p_113130_);
      }).map(BeeDebugRenderer.BeeInfo::getUuid).collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> getGhostHives() {
      Map<BlockPos, List<String>> map = Maps.newHashMap();

      for(BeeDebugRenderer.BeeInfo beedebugrenderer$beeinfo : this.beeInfosPerEntity.values()) {
         if (beedebugrenderer$beeinfo.hivePos != null && !this.hives.containsKey(beedebugrenderer$beeinfo.hivePos)) {
            map.computeIfAbsent(beedebugrenderer$beeinfo.hivePos, (p_113140_) -> {
               return Lists.newArrayList();
            }).add(beedebugrenderer$beeinfo.getName());
         }
      }

      return map;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((p_113059_) -> {
         this.lastLookedAtUuid = p_113059_.getUUID();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static class BeeInfo {
      public final UUID uuid;
      public final int id;
      public final Position pos;
      @Nullable
      public final Path path;
      @Nullable
      public final BlockPos hivePos;
      @Nullable
      public final BlockPos flowerPos;
      public final int travelTicks;
      public final List<String> goals = Lists.newArrayList();
      public final Set<BlockPos> blacklistedHives = Sets.newHashSet();

      public BeeInfo(UUID p_113167_, int p_113168_, Position p_113169_, @Nullable Path p_113170_, @Nullable BlockPos p_113171_, @Nullable BlockPos p_113172_, int p_113173_) {
         this.uuid = p_113167_;
         this.id = p_113168_;
         this.pos = p_113169_;
         this.path = p_113170_;
         this.hivePos = p_113171_;
         this.flowerPos = p_113172_;
         this.travelTicks = p_113173_;
      }

      public boolean hasHive(BlockPos p_113176_) {
         return this.hivePos != null && this.hivePos.equals(p_113176_);
      }

      public UUID getUuid() {
         return this.uuid;
      }

      public String getName() {
         return DebugEntityNameGenerator.getEntityName(this.uuid);
      }

      public String toString() {
         return this.getName();
      }

      public boolean hasFlower() {
         return this.flowerPos != null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class HiveInfo {
      public final BlockPos pos;
      public final String hiveType;
      public final int occupantCount;
      public final int honeyLevel;
      public final boolean sedated;
      public final long lastSeen;

      public HiveInfo(BlockPos p_113187_, String p_113188_, int p_113189_, int p_113190_, boolean p_113191_, long p_113192_) {
         this.pos = p_113187_;
         this.hiveType = p_113188_;
         this.occupantCount = p_113189_;
         this.honeyLevel = p_113190_;
         this.sedated = p_113191_;
         this.lastSeen = p_113192_;
      }
   }
}