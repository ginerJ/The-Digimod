package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class DebugRenderer {
   public final PathfindingRenderer pathfindingRenderer = new PathfindingRenderer();
   public final DebugRenderer.SimpleDebugRenderer waterDebugRenderer;
   public final DebugRenderer.SimpleDebugRenderer chunkBorderRenderer;
   public final DebugRenderer.SimpleDebugRenderer heightMapRenderer;
   public final DebugRenderer.SimpleDebugRenderer collisionBoxRenderer;
   public final DebugRenderer.SimpleDebugRenderer supportBlockRenderer;
   public final DebugRenderer.SimpleDebugRenderer neighborsUpdateRenderer;
   public final StructureRenderer structureRenderer;
   public final DebugRenderer.SimpleDebugRenderer lightDebugRenderer;
   public final DebugRenderer.SimpleDebugRenderer worldGenAttemptRenderer;
   public final DebugRenderer.SimpleDebugRenderer solidFaceRenderer;
   public final DebugRenderer.SimpleDebugRenderer chunkRenderer;
   public final BrainDebugRenderer brainDebugRenderer;
   public final VillageSectionsDebugRenderer villageSectionsDebugRenderer;
   public final BeeDebugRenderer beeDebugRenderer;
   public final RaidDebugRenderer raidDebugRenderer;
   public final GoalSelectorDebugRenderer goalSelectorRenderer;
   public final GameTestDebugRenderer gameTestDebugRenderer;
   public final GameEventListenerRenderer gameEventListenerRenderer;
   public final LightSectionDebugRenderer skyLightSectionDebugRenderer;
   private boolean renderChunkborder;

   public DebugRenderer(Minecraft p_113433_) {
      this.waterDebugRenderer = new WaterDebugRenderer(p_113433_);
      this.chunkBorderRenderer = new ChunkBorderRenderer(p_113433_);
      this.heightMapRenderer = new HeightMapRenderer(p_113433_);
      this.collisionBoxRenderer = new CollisionBoxRenderer(p_113433_);
      this.supportBlockRenderer = new SupportBlockRenderer(p_113433_);
      this.neighborsUpdateRenderer = new NeighborsUpdateRenderer(p_113433_);
      this.structureRenderer = new StructureRenderer(p_113433_);
      this.lightDebugRenderer = new LightDebugRenderer(p_113433_);
      this.worldGenAttemptRenderer = new WorldGenAttemptRenderer();
      this.solidFaceRenderer = new SolidFaceRenderer(p_113433_);
      this.chunkRenderer = new ChunkDebugRenderer(p_113433_);
      this.brainDebugRenderer = new BrainDebugRenderer(p_113433_);
      this.villageSectionsDebugRenderer = new VillageSectionsDebugRenderer();
      this.beeDebugRenderer = new BeeDebugRenderer(p_113433_);
      this.raidDebugRenderer = new RaidDebugRenderer(p_113433_);
      this.goalSelectorRenderer = new GoalSelectorDebugRenderer(p_113433_);
      this.gameTestDebugRenderer = new GameTestDebugRenderer();
      this.gameEventListenerRenderer = new GameEventListenerRenderer(p_113433_);
      this.skyLightSectionDebugRenderer = new LightSectionDebugRenderer(p_113433_, LightLayer.SKY);
   }

   public void clear() {
      this.pathfindingRenderer.clear();
      this.waterDebugRenderer.clear();
      this.chunkBorderRenderer.clear();
      this.heightMapRenderer.clear();
      this.collisionBoxRenderer.clear();
      this.supportBlockRenderer.clear();
      this.neighborsUpdateRenderer.clear();
      this.structureRenderer.clear();
      this.lightDebugRenderer.clear();
      this.worldGenAttemptRenderer.clear();
      this.solidFaceRenderer.clear();
      this.chunkRenderer.clear();
      this.brainDebugRenderer.clear();
      this.villageSectionsDebugRenderer.clear();
      this.beeDebugRenderer.clear();
      this.raidDebugRenderer.clear();
      this.goalSelectorRenderer.clear();
      this.gameTestDebugRenderer.clear();
      this.gameEventListenerRenderer.clear();
      this.skyLightSectionDebugRenderer.clear();
   }

   public boolean switchRenderChunkborder() {
      this.renderChunkborder = !this.renderChunkborder;
      return this.renderChunkborder;
   }

   public void render(PoseStack p_113458_, MultiBufferSource.BufferSource p_113459_, double p_113460_, double p_113461_, double p_113462_) {
      if (this.renderChunkborder && !Minecraft.getInstance().showOnlyReducedInfo()) {
         this.chunkBorderRenderer.render(p_113458_, p_113459_, p_113460_, p_113461_, p_113462_);
      }

      this.gameTestDebugRenderer.render(p_113458_, p_113459_, p_113460_, p_113461_, p_113462_);
   }

   public static Optional<Entity> getTargetedEntity(@Nullable Entity p_113449_, int p_113450_) {
      if (p_113449_ == null) {
         return Optional.empty();
      } else {
         Vec3 vec3 = p_113449_.getEyePosition();
         Vec3 vec31 = p_113449_.getViewVector(1.0F).scale((double)p_113450_);
         Vec3 vec32 = vec3.add(vec31);
         AABB aabb = p_113449_.getBoundingBox().expandTowards(vec31).inflate(1.0D);
         int i = p_113450_ * p_113450_;
         Predicate<Entity> predicate = (p_113447_) -> {
            return !p_113447_.isSpectator() && p_113447_.isPickable();
         };
         EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(p_113449_, vec3, vec32, aabb, predicate, (double)i);
         if (entityhitresult == null) {
            return Optional.empty();
         } else {
            return vec3.distanceToSqr(entityhitresult.getLocation()) > (double)i ? Optional.empty() : Optional.of(entityhitresult.getEntity());
         }
      }
   }

   public static void renderFilledBox(PoseStack p_270169_, MultiBufferSource p_270417_, BlockPos p_270790_, BlockPos p_270610_, float p_270515_, float p_270494_, float p_270869_, float p_270844_) {
      Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (camera.isInitialized()) {
         Vec3 vec3 = camera.getPosition().reverse();
         AABB aabb = (new AABB(p_270790_, p_270610_)).move(vec3);
         renderFilledBox(p_270169_, p_270417_, aabb, p_270515_, p_270494_, p_270869_, p_270844_);
      }
   }

   public static void renderFilledBox(PoseStack p_270877_, MultiBufferSource p_270925_, BlockPos p_270480_, float p_270569_, float p_270315_, float p_270182_, float p_270862_, float p_270973_) {
      Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (camera.isInitialized()) {
         Vec3 vec3 = camera.getPosition().reverse();
         AABB aabb = (new AABB(p_270480_)).move(vec3).inflate((double)p_270569_);
         renderFilledBox(p_270877_, p_270925_, aabb, p_270315_, p_270182_, p_270862_, p_270973_);
      }
   }

   public static void renderFilledBox(PoseStack p_271017_, MultiBufferSource p_270356_, AABB p_270833_, float p_270850_, float p_270249_, float p_270654_, float p_270476_) {
      renderFilledBox(p_271017_, p_270356_, p_270833_.minX, p_270833_.minY, p_270833_.minZ, p_270833_.maxX, p_270833_.maxY, p_270833_.maxZ, p_270850_, p_270249_, p_270654_, p_270476_);
   }

   public static void renderFilledBox(PoseStack p_270616_, MultiBufferSource p_270769_, double p_270653_, double p_270967_, double p_270556_, double p_270724_, double p_270427_, double p_270138_, float p_270391_, float p_270093_, float p_270312_, float p_270567_) {
      VertexConsumer vertexconsumer = p_270769_.getBuffer(RenderType.debugFilledBox());
      LevelRenderer.addChainedFilledBoxVertices(p_270616_, vertexconsumer, p_270653_, p_270967_, p_270556_, p_270724_, p_270427_, p_270138_, p_270391_, p_270093_, p_270312_, p_270567_);
   }

   public static void renderFloatingText(PoseStack p_270671_, MultiBufferSource p_271023_, String p_270521_, int p_270729_, int p_270562_, int p_270828_, int p_270164_) {
      renderFloatingText(p_270671_, p_271023_, p_270521_, (double)p_270729_ + 0.5D, (double)p_270562_ + 0.5D, (double)p_270828_ + 0.5D, p_270164_);
   }

   public static void renderFloatingText(PoseStack p_270905_, MultiBufferSource p_270581_, String p_270305_, double p_270645_, double p_270746_, double p_270364_, int p_270977_) {
      renderFloatingText(p_270905_, p_270581_, p_270305_, p_270645_, p_270746_, p_270364_, p_270977_, 0.02F);
   }

   public static void renderFloatingText(PoseStack p_270216_, MultiBufferSource p_270684_, String p_270564_, double p_270935_, double p_270856_, double p_270908_, int p_270180_, float p_270685_) {
      renderFloatingText(p_270216_, p_270684_, p_270564_, p_270935_, p_270856_, p_270908_, p_270180_, p_270685_, true, 0.0F, false);
   }

   public static void renderFloatingText(PoseStack p_270649_, MultiBufferSource p_270695_, String p_270703_, double p_270942_, double p_270292_, double p_270885_, int p_270956_, float p_270657_, boolean p_270731_, float p_270825_, boolean p_270222_) {
      Minecraft minecraft = Minecraft.getInstance();
      Camera camera = minecraft.gameRenderer.getMainCamera();
      if (camera.isInitialized() && minecraft.getEntityRenderDispatcher().options != null) {
         Font font = minecraft.font;
         double d0 = camera.getPosition().x;
         double d1 = camera.getPosition().y;
         double d2 = camera.getPosition().z;
         p_270649_.pushPose();
         p_270649_.translate((float)(p_270942_ - d0), (float)(p_270292_ - d1) + 0.07F, (float)(p_270885_ - d2));
         p_270649_.mulPoseMatrix((new Matrix4f()).rotation(camera.rotation()));
         p_270649_.scale(-p_270657_, -p_270657_, p_270657_);
         float f = p_270731_ ? (float)(-font.width(p_270703_)) / 2.0F : 0.0F;
         f -= p_270825_ / p_270657_;
         font.drawInBatch(p_270703_, f, 0.0F, p_270956_, false, p_270649_.last().pose(), p_270695_, p_270222_ ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, 0, 15728880);
         p_270649_.popPose();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface SimpleDebugRenderer {
      void render(PoseStack p_113507_, MultiBufferSource p_113508_, double p_113509_, double p_113510_, double p_113511_);

      default void clear() {
      }
   }
}