package net.minecraft.client.renderer.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BrainDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final boolean SHOW_NAME_FOR_ALL = true;
   private static final boolean SHOW_PROFESSION_FOR_ALL = false;
   private static final boolean SHOW_BEHAVIORS_FOR_ALL = false;
   private static final boolean SHOW_ACTIVITIES_FOR_ALL = false;
   private static final boolean SHOW_INVENTORY_FOR_ALL = false;
   private static final boolean SHOW_GOSSIPS_FOR_ALL = false;
   private static final boolean SHOW_PATH_FOR_ALL = false;
   private static final boolean SHOW_HEALTH_FOR_ALL = false;
   private static final boolean SHOW_WANTS_GOLEM_FOR_ALL = true;
   private static final boolean SHOW_ANGER_LEVEL_FOR_ALL = false;
   private static final boolean SHOW_NAME_FOR_SELECTED = true;
   private static final boolean SHOW_PROFESSION_FOR_SELECTED = true;
   private static final boolean SHOW_BEHAVIORS_FOR_SELECTED = true;
   private static final boolean SHOW_ACTIVITIES_FOR_SELECTED = true;
   private static final boolean SHOW_MEMORIES_FOR_SELECTED = true;
   private static final boolean SHOW_INVENTORY_FOR_SELECTED = true;
   private static final boolean SHOW_GOSSIPS_FOR_SELECTED = true;
   private static final boolean SHOW_PATH_FOR_SELECTED = true;
   private static final boolean SHOW_HEALTH_FOR_SELECTED = true;
   private static final boolean SHOW_WANTS_GOLEM_FOR_SELECTED = true;
   private static final boolean SHOW_ANGER_LEVEL_FOR_SELECTED = true;
   private static final boolean SHOW_POI_INFO = true;
   private static final int MAX_RENDER_DIST_FOR_BRAIN_INFO = 30;
   private static final int MAX_RENDER_DIST_FOR_POI_INFO = 30;
   private static final int MAX_TARGETING_DIST = 8;
   private static final float TEXT_SCALE = 0.02F;
   private static final int WHITE = -1;
   private static final int YELLOW = -256;
   private static final int CYAN = -16711681;
   private static final int GREEN = -16711936;
   private static final int GRAY = -3355444;
   private static final int PINK = -98404;
   private static final int RED = -65536;
   private static final int ORANGE = -23296;
   private final Minecraft minecraft;
   private final Map<BlockPos, BrainDebugRenderer.PoiInfo> pois = Maps.newHashMap();
   private final Map<UUID, BrainDebugRenderer.BrainDump> brainDumpsPerEntity = Maps.newHashMap();
   @Nullable
   private UUID lastLookedAtUuid;

   public BrainDebugRenderer(Minecraft p_113200_) {
      this.minecraft = p_113200_;
   }

   public void clear() {
      this.pois.clear();
      this.brainDumpsPerEntity.clear();
      this.lastLookedAtUuid = null;
   }

   public void addPoi(BrainDebugRenderer.PoiInfo p_113227_) {
      this.pois.put(p_113227_.pos, p_113227_);
   }

   public void removePoi(BlockPos p_113229_) {
      this.pois.remove(p_113229_);
   }

   public void setFreeTicketCount(BlockPos p_113231_, int p_113232_) {
      BrainDebugRenderer.PoiInfo braindebugrenderer$poiinfo = this.pois.get(p_113231_);
      if (braindebugrenderer$poiinfo == null) {
         LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: {}", (Object)p_113231_);
      } else {
         braindebugrenderer$poiinfo.freeTicketCount = p_113232_;
      }
   }

   public void addOrUpdateBrainDump(BrainDebugRenderer.BrainDump p_113220_) {
      this.brainDumpsPerEntity.put(p_113220_.uuid, p_113220_);
   }

   public void removeBrainDump(int p_173811_) {
      this.brainDumpsPerEntity.values().removeIf((p_173814_) -> {
         return p_173814_.id == p_173811_;
      });
   }

   public void render(PoseStack p_113214_, MultiBufferSource p_113215_, double p_113216_, double p_113217_, double p_113218_) {
      this.clearRemovedEntities();
      this.doRender(p_113214_, p_113215_, p_113216_, p_113217_, p_113218_);
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void clearRemovedEntities() {
      this.brainDumpsPerEntity.entrySet().removeIf((p_113263_) -> {
         Entity entity = this.minecraft.level.getEntity((p_113263_.getValue()).id);
         return entity == null || entity.isRemoved();
      });
   }

   private void doRender(PoseStack p_270747_, MultiBufferSource p_270289_, double p_270303_, double p_270416_, double p_270542_) {
      BlockPos blockpos = BlockPos.containing(p_270303_, p_270416_, p_270542_);
      this.brainDumpsPerEntity.values().forEach((p_269714_) -> {
         if (this.isPlayerCloseEnoughToMob(p_269714_)) {
            this.renderBrainInfo(p_270747_, p_270289_, p_269714_, p_270303_, p_270416_, p_270542_);
         }

      });

      for(BlockPos blockpos1 : this.pois.keySet()) {
         if (blockpos.closerThan(blockpos1, 30.0D)) {
            highlightPoi(p_270747_, p_270289_, blockpos1);
         }
      }

      this.pois.values().forEach((p_269718_) -> {
         if (blockpos.closerThan(p_269718_.pos, 30.0D)) {
            this.renderPoiInfo(p_270747_, p_270289_, p_269718_);
         }

      });
      this.getGhostPois().forEach((p_269707_, p_269708_) -> {
         if (blockpos.closerThan(p_269707_, 30.0D)) {
            this.renderGhostPoi(p_270747_, p_270289_, p_269707_, p_269708_);
         }

      });
   }

   private static void highlightPoi(PoseStack p_270066_, MultiBufferSource p_270965_, BlockPos p_270159_) {
      float f = 0.05F;
      DebugRenderer.renderFilledBox(p_270066_, p_270965_, p_270159_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostPoi(PoseStack p_270206_, MultiBufferSource p_270976_, BlockPos p_270670_, List<String> p_270882_) {
      float f = 0.05F;
      DebugRenderer.renderFilledBox(p_270206_, p_270976_, p_270670_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      renderTextOverPos(p_270206_, p_270976_, "" + p_270882_, p_270670_, 0, -256);
      renderTextOverPos(p_270206_, p_270976_, "Ghost POI", p_270670_, 1, -65536);
   }

   private void renderPoiInfo(PoseStack p_270999_, MultiBufferSource p_270627_, BrainDebugRenderer.PoiInfo p_270986_) {
      int i = 0;
      Set<String> set = this.getTicketHolderNames(p_270986_);
      if (set.size() < 4) {
         renderTextOverPoi(p_270999_, p_270627_, "Owners: " + set, p_270986_, i, -256);
      } else {
         renderTextOverPoi(p_270999_, p_270627_, set.size() + " ticket holders", p_270986_, i, -256);
      }

      ++i;
      Set<String> set1 = this.getPotentialTicketHolderNames(p_270986_);
      if (set1.size() < 4) {
         renderTextOverPoi(p_270999_, p_270627_, "Candidates: " + set1, p_270986_, i, -23296);
      } else {
         renderTextOverPoi(p_270999_, p_270627_, set1.size() + " potential owners", p_270986_, i, -23296);
      }

      ++i;
      renderTextOverPoi(p_270999_, p_270627_, "Free tickets: " + p_270986_.freeTicketCount, p_270986_, i, -256);
      ++i;
      renderTextOverPoi(p_270999_, p_270627_, p_270986_.type, p_270986_, i, -1);
   }

   private void renderPath(PoseStack p_270435_, MultiBufferSource p_270439_, BrainDebugRenderer.BrainDump p_270979_, double p_270109_, double p_270342_, double p_270834_) {
      if (p_270979_.path != null) {
         PathfindingRenderer.renderPath(p_270435_, p_270439_, p_270979_.path, 0.5F, false, false, p_270109_, p_270342_, p_270834_);
      }

   }

   private void renderBrainInfo(PoseStack p_270145_, MultiBufferSource p_270489_, BrainDebugRenderer.BrainDump p_270259_, double p_270922_, double p_270468_, double p_270838_) {
      boolean flag = this.isMobSelected(p_270259_);
      int i = 0;
      renderTextOverMob(p_270145_, p_270489_, p_270259_.pos, i, p_270259_.name, -1, 0.03F);
      ++i;
      if (flag) {
         renderTextOverMob(p_270145_, p_270489_, p_270259_.pos, i, p_270259_.profession + " " + p_270259_.xp + " xp", -1, 0.02F);
         ++i;
      }

      if (flag) {
         int j = p_270259_.health < p_270259_.maxHealth ? -23296 : -1;
         renderTextOverMob(p_270145_, p_270489_, p_270259_.pos, i, "health: " + String.format(Locale.ROOT, "%.1f", p_270259_.health) + " / " + String.format(Locale.ROOT, "%.1f", p_270259_.maxHealth), j, 0.02F);
         ++i;
      }

      if (flag && !p_270259_.inventory.equals("")) {
         renderTextOverMob(p_270145_, p_270489_, p_270259_.pos, i, p_270259_.inventory, -98404, 0.02F);
         ++i;
      }

      if (flag) {
         for(String s : p_270259_.behaviors) {
            renderTextOverMob(p_270145_, p_270489_, p_270259_.pos, i, s, -16711681, 0.02F);
            ++i;
         }
      }

      if (flag) {
         for(String s1 : p_270259_.activities) {
            renderTextOverMob(p_270145_, p_270489_, p_270259_.pos, i, s1, -16711936, 0.02F);
            ++i;
         }
      }

      if (p_270259_.wantsGolem) {
         renderTextOverMob(p_270145_, p_270489_, p_270259_.pos, i, "Wants Golem", -23296, 0.02F);
         ++i;
      }

      if (flag && p_270259_.angerLevel != -1) {
         renderTextOverMob(p_270145_, p_270489_, p_270259_.pos, i, "Anger Level: " + p_270259_.angerLevel, -98404, 0.02F);
         ++i;
      }

      if (flag) {
         for(String s2 : p_270259_.gossips) {
            if (s2.startsWith(p_270259_.name)) {
               renderTextOverMob(p_270145_, p_270489_, p_270259_.pos, i, s2, -1, 0.02F);
            } else {
               renderTextOverMob(p_270145_, p_270489_, p_270259_.pos, i, s2, -23296, 0.02F);
            }

            ++i;
         }
      }

      if (flag) {
         for(String s3 : Lists.reverse(p_270259_.memories)) {
            renderTextOverMob(p_270145_, p_270489_, p_270259_.pos, i, s3, -3355444, 0.02F);
            ++i;
         }
      }

      if (flag) {
         this.renderPath(p_270145_, p_270489_, p_270259_, p_270922_, p_270468_, p_270838_);
      }

   }

   private static void renderTextOverPoi(PoseStack p_270498_, MultiBufferSource p_270609_, String p_270070_, BrainDebugRenderer.PoiInfo p_270677_, int p_270143_, int p_271011_) {
      renderTextOverPos(p_270498_, p_270609_, p_270070_, p_270677_.pos, p_270143_, p_271011_);
   }

   private static void renderTextOverPos(PoseStack p_270640_, MultiBufferSource p_270809_, String p_270632_, BlockPos p_270082_, int p_270078_, int p_270440_) {
      double d0 = 1.3D;
      double d1 = 0.2D;
      double d2 = (double)p_270082_.getX() + 0.5D;
      double d3 = (double)p_270082_.getY() + 1.3D + (double)p_270078_ * 0.2D;
      double d4 = (double)p_270082_.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(p_270640_, p_270809_, p_270632_, d2, d3, d4, p_270440_, 0.02F, true, 0.0F, true);
   }

   private static void renderTextOverMob(PoseStack p_270664_, MultiBufferSource p_270816_, Position p_270715_, int p_270126_, String p_270487_, int p_270218_, float p_270737_) {
      double d0 = 2.4D;
      double d1 = 0.25D;
      BlockPos blockpos = BlockPos.containing(p_270715_);
      double d2 = (double)blockpos.getX() + 0.5D;
      double d3 = p_270715_.y() + 2.4D + (double)p_270126_ * 0.25D;
      double d4 = (double)blockpos.getZ() + 0.5D;
      float f = 0.5F;
      DebugRenderer.renderFloatingText(p_270664_, p_270816_, p_270487_, d2, d3, d4, p_270218_, p_270737_, false, 0.5F, true);
   }

   private Set<String> getTicketHolderNames(BrainDebugRenderer.PoiInfo p_113283_) {
      return this.getTicketHolders(p_113283_.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
   }

   private Set<String> getPotentialTicketHolderNames(BrainDebugRenderer.PoiInfo p_113288_) {
      return this.getPotentialTicketHolders(p_113288_.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
   }

   private boolean isMobSelected(BrainDebugRenderer.BrainDump p_113266_) {
      return Objects.equals(this.lastLookedAtUuid, p_113266_.uuid);
   }

   private boolean isPlayerCloseEnoughToMob(BrainDebugRenderer.BrainDump p_113281_) {
      Player player = this.minecraft.player;
      BlockPos blockpos = BlockPos.containing(player.getX(), p_113281_.pos.y(), player.getZ());
      BlockPos blockpos1 = BlockPos.containing(p_113281_.pos);
      return blockpos.closerThan(blockpos1, 30.0D);
   }

   private Collection<UUID> getTicketHolders(BlockPos p_113285_) {
      return this.brainDumpsPerEntity.values().stream().filter((p_113278_) -> {
         return p_113278_.hasPoi(p_113285_);
      }).map(BrainDebugRenderer.BrainDump::getUuid).collect(Collectors.toSet());
   }

   private Collection<UUID> getPotentialTicketHolders(BlockPos p_113290_) {
      return this.brainDumpsPerEntity.values().stream().filter((p_113235_) -> {
         return p_113235_.hasPotentialPoi(p_113290_);
      }).map(BrainDebugRenderer.BrainDump::getUuid).collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> getGhostPois() {
      Map<BlockPos, List<String>> map = Maps.newHashMap();

      for(BrainDebugRenderer.BrainDump braindebugrenderer$braindump : this.brainDumpsPerEntity.values()) {
         for(BlockPos blockpos : Iterables.concat(braindebugrenderer$braindump.pois, braindebugrenderer$braindump.potentialPois)) {
            if (!this.pois.containsKey(blockpos)) {
               map.computeIfAbsent(blockpos, (p_113292_) -> {
                  return Lists.newArrayList();
               }).add(braindebugrenderer$braindump.name);
            }
         }
      }

      return map;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((p_113212_) -> {
         this.lastLookedAtUuid = p_113212_.getUUID();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static class BrainDump {
      public final UUID uuid;
      public final int id;
      public final String name;
      public final String profession;
      public final int xp;
      public final float health;
      public final float maxHealth;
      public final Position pos;
      public final String inventory;
      public final Path path;
      public final boolean wantsGolem;
      public final int angerLevel;
      public final List<String> activities = Lists.newArrayList();
      public final List<String> behaviors = Lists.newArrayList();
      public final List<String> memories = Lists.newArrayList();
      public final List<String> gossips = Lists.newArrayList();
      public final Set<BlockPos> pois = Sets.newHashSet();
      public final Set<BlockPos> potentialPois = Sets.newHashSet();

      public BrainDump(UUID p_234497_, int p_234498_, String p_234499_, String p_234500_, int p_234501_, float p_234502_, float p_234503_, Position p_234504_, String p_234505_, @Nullable Path p_234506_, boolean p_234507_, int p_234508_) {
         this.uuid = p_234497_;
         this.id = p_234498_;
         this.name = p_234499_;
         this.profession = p_234500_;
         this.xp = p_234501_;
         this.health = p_234502_;
         this.maxHealth = p_234503_;
         this.pos = p_234504_;
         this.inventory = p_234505_;
         this.path = p_234506_;
         this.wantsGolem = p_234507_;
         this.angerLevel = p_234508_;
      }

      boolean hasPoi(BlockPos p_113327_) {
         return this.pois.stream().anyMatch(p_113327_::equals);
      }

      boolean hasPotentialPoi(BlockPos p_113332_) {
         return this.potentialPois.contains(p_113332_);
      }

      public UUID getUuid() {
         return this.uuid;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class PoiInfo {
      public final BlockPos pos;
      public String type;
      public int freeTicketCount;

      public PoiInfo(BlockPos p_113337_, String p_113338_, int p_113339_) {
         this.pos = p_113337_;
         this.type = p_113338_;
         this.freeTicketCount = p_113339_;
      }
   }
}