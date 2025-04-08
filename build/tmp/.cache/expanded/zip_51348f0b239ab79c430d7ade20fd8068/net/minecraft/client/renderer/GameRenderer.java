package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GameRenderer implements AutoCloseable {
   private static final ResourceLocation NAUSEA_LOCATION = new ResourceLocation("textures/misc/nausea.png");
   static final Logger LOGGER = LogUtils.getLogger();
   private static final boolean DEPTH_BUFFER_DEBUG = false;
   public static final float PROJECTION_Z_NEAR = 0.05F;
   private static final float GUI_Z_NEAR = 1000.0F;
   final Minecraft minecraft;
   private final ResourceManager resourceManager;
   private final RandomSource random = RandomSource.create();
   private float renderDistance;
   public final ItemInHandRenderer itemInHandRenderer;
   private final MapRenderer mapRenderer;
   private final RenderBuffers renderBuffers;
   private int tick;
   private float fov;
   private float oldFov;
   private float darkenWorldAmount;
   private float darkenWorldAmountO;
   private boolean renderHand = true;
   private boolean renderBlockOutline = true;
   private long lastScreenshotAttempt;
   private boolean hasWorldScreenshot;
   private long lastActiveTime = Util.getMillis();
   private final LightTexture lightTexture;
   private final OverlayTexture overlayTexture = new OverlayTexture();
   private boolean panoramicMode;
   private float zoom = 1.0F;
   private float zoomX;
   private float zoomY;
   public static final int ITEM_ACTIVATION_ANIMATION_LENGTH = 40;
   @Nullable
   private ItemStack itemActivationItem;
   private int itemActivationTicks;
   private float itemActivationOffX;
   private float itemActivationOffY;
   @Nullable
   PostChain postEffect;
   static final ResourceLocation[] EFFECTS = new ResourceLocation[]{new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
   public static final int EFFECT_NONE = EFFECTS.length;
   int effectIndex = EFFECT_NONE;
   private boolean effectActive;
   private final Camera mainCamera = new Camera();
   public ShaderInstance blitShader;
   private final Map<String, ShaderInstance> shaders = Maps.newHashMap();
   @Nullable
   private static ShaderInstance positionShader;
   @Nullable
   private static ShaderInstance positionColorShader;
   @Nullable
   private static ShaderInstance positionColorTexShader;
   @Nullable
   private static ShaderInstance positionTexShader;
   @Nullable
   private static ShaderInstance positionTexColorShader;
   @Nullable
   private static ShaderInstance particleShader;
   @Nullable
   private static ShaderInstance positionColorLightmapShader;
   @Nullable
   private static ShaderInstance positionColorTexLightmapShader;
   @Nullable
   private static ShaderInstance positionTexColorNormalShader;
   @Nullable
   private static ShaderInstance positionTexLightmapColorShader;
   @Nullable
   private static ShaderInstance rendertypeSolidShader;
   @Nullable
   private static ShaderInstance rendertypeCutoutMippedShader;
   @Nullable
   private static ShaderInstance rendertypeCutoutShader;
   @Nullable
   private static ShaderInstance rendertypeTranslucentShader;
   @Nullable
   private static ShaderInstance rendertypeTranslucentMovingBlockShader;
   @Nullable
   private static ShaderInstance rendertypeTranslucentNoCrumblingShader;
   @Nullable
   private static ShaderInstance rendertypeArmorCutoutNoCullShader;
   @Nullable
   private static ShaderInstance rendertypeEntitySolidShader;
   @Nullable
   private static ShaderInstance rendertypeEntityCutoutShader;
   @Nullable
   private static ShaderInstance rendertypeEntityCutoutNoCullShader;
   @Nullable
   private static ShaderInstance rendertypeEntityCutoutNoCullZOffsetShader;
   @Nullable
   private static ShaderInstance rendertypeItemEntityTranslucentCullShader;
   @Nullable
   private static ShaderInstance rendertypeEntityTranslucentCullShader;
   @Nullable
   private static ShaderInstance rendertypeEntityTranslucentShader;
   @Nullable
   private static ShaderInstance rendertypeEntityTranslucentEmissiveShader;
   @Nullable
   private static ShaderInstance rendertypeEntitySmoothCutoutShader;
   @Nullable
   private static ShaderInstance rendertypeBeaconBeamShader;
   @Nullable
   private static ShaderInstance rendertypeEntityDecalShader;
   @Nullable
   private static ShaderInstance rendertypeEntityNoOutlineShader;
   @Nullable
   private static ShaderInstance rendertypeEntityShadowShader;
   @Nullable
   private static ShaderInstance rendertypeEntityAlphaShader;
   @Nullable
   private static ShaderInstance rendertypeEyesShader;
   @Nullable
   private static ShaderInstance rendertypeEnergySwirlShader;
   @Nullable
   private static ShaderInstance rendertypeLeashShader;
   @Nullable
   private static ShaderInstance rendertypeWaterMaskShader;
   @Nullable
   private static ShaderInstance rendertypeOutlineShader;
   @Nullable
   private static ShaderInstance rendertypeArmorGlintShader;
   @Nullable
   private static ShaderInstance rendertypeArmorEntityGlintShader;
   @Nullable
   private static ShaderInstance rendertypeGlintTranslucentShader;
   @Nullable
   private static ShaderInstance rendertypeGlintShader;
   @Nullable
   private static ShaderInstance rendertypeGlintDirectShader;
   @Nullable
   private static ShaderInstance rendertypeEntityGlintShader;
   @Nullable
   private static ShaderInstance rendertypeEntityGlintDirectShader;
   @Nullable
   private static ShaderInstance rendertypeTextShader;
   @Nullable
   private static ShaderInstance rendertypeTextBackgroundShader;
   @Nullable
   private static ShaderInstance rendertypeTextIntensityShader;
   @Nullable
   private static ShaderInstance rendertypeTextSeeThroughShader;
   @Nullable
   private static ShaderInstance rendertypeTextBackgroundSeeThroughShader;
   @Nullable
   private static ShaderInstance rendertypeTextIntensitySeeThroughShader;
   @Nullable
   private static ShaderInstance rendertypeLightningShader;
   @Nullable
   private static ShaderInstance rendertypeTripwireShader;
   @Nullable
   private static ShaderInstance rendertypeEndPortalShader;
   @Nullable
   private static ShaderInstance rendertypeEndGatewayShader;
   @Nullable
   private static ShaderInstance rendertypeLinesShader;
   @Nullable
   private static ShaderInstance rendertypeCrumblingShader;
   @Nullable
   private static ShaderInstance rendertypeGuiShader;
   @Nullable
   private static ShaderInstance rendertypeGuiOverlayShader;
   @Nullable
   private static ShaderInstance rendertypeGuiTextHighlightShader;
   @Nullable
   private static ShaderInstance rendertypeGuiGhostRecipeOverlayShader;

   public GameRenderer(Minecraft p_234219_, ItemInHandRenderer p_234220_, ResourceManager p_234221_, RenderBuffers p_234222_) {
      this.minecraft = p_234219_;
      this.resourceManager = p_234221_;
      this.itemInHandRenderer = p_234220_;
      this.mapRenderer = new MapRenderer(p_234219_.getTextureManager());
      this.lightTexture = new LightTexture(this, p_234219_);
      this.renderBuffers = p_234222_;
      this.postEffect = null;
   }

   public void close() {
      this.lightTexture.close();
      this.mapRenderer.close();
      this.overlayTexture.close();
      this.shutdownEffect();
      this.shutdownShaders();
      if (this.blitShader != null) {
         this.blitShader.close();
      }

   }

   public void setRenderHand(boolean p_172737_) {
      this.renderHand = p_172737_;
   }

   public void setRenderBlockOutline(boolean p_172776_) {
      this.renderBlockOutline = p_172776_;
   }

   public void setPanoramicMode(boolean p_172780_) {
      this.panoramicMode = p_172780_;
   }

   public boolean isPanoramicMode() {
      return this.panoramicMode;
   }

   public void shutdownEffect() {
      if (this.postEffect != null) {
         this.postEffect.close();
      }

      this.postEffect = null;
      this.effectIndex = EFFECT_NONE;
   }

   public void togglePostEffect() {
      this.effectActive = !this.effectActive;
   }

   public void checkEntityPostEffect(@Nullable Entity p_109107_) {
      if (this.postEffect != null) {
         this.postEffect.close();
      }

      this.postEffect = null;
      if (p_109107_ instanceof Creeper) {
         this.loadEffect(new ResourceLocation("shaders/post/creeper.json"));
      } else if (p_109107_ instanceof Spider) {
         this.loadEffect(new ResourceLocation("shaders/post/spider.json"));
         this.loadEffect(new ResourceLocation("shaders/post/spider.json"));
      } else if (p_109107_ instanceof EnderMan) {
         this.loadEffect(new ResourceLocation("shaders/post/invert.json"));
      } else {
         net.minecraftforge.client.ForgeHooksClient.loadEntityShader(p_109107_, this);
      }

   }

   public void cycleEffect() {
      if (this.minecraft.getCameraEntity() instanceof Player) {
         if (this.postEffect != null) {
            this.postEffect.close();
         }

         this.effectIndex = (this.effectIndex + 1) % (EFFECTS.length + 1);
         if (this.effectIndex == EFFECT_NONE) {
            this.postEffect = null;
         } else {
            this.loadEffect(EFFECTS[this.effectIndex]);
         }

      }
   }

   public void loadEffect(ResourceLocation p_109129_) {
      if (this.postEffect != null) {
         this.postEffect.close();
      }

      try {
         this.postEffect = new PostChain(this.minecraft.getTextureManager(), this.resourceManager, this.minecraft.getMainRenderTarget(), p_109129_);
         this.postEffect.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         this.effectActive = true;
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to load shader: {}", p_109129_, ioexception);
         this.effectIndex = EFFECT_NONE;
         this.effectActive = false;
      } catch (JsonSyntaxException jsonsyntaxexception) {
         LOGGER.warn("Failed to parse shader: {}", p_109129_, jsonsyntaxexception);
         this.effectIndex = EFFECT_NONE;
         this.effectActive = false;
      }

   }

   public PreparableReloadListener createReloadListener() {
      return new SimplePreparableReloadListener<GameRenderer.ResourceCache>() {
         protected GameRenderer.ResourceCache prepare(ResourceManager p_251213_, ProfilerFiller p_251006_) {
            Map<ResourceLocation, Resource> map = p_251213_.listResources("shaders", (p_251575_) -> {
               String s = p_251575_.getPath();
               return s.endsWith(".json") || s.endsWith(Program.Type.FRAGMENT.getExtension()) || s.endsWith(Program.Type.VERTEX.getExtension()) || s.endsWith(".glsl");
            });
            Map<ResourceLocation, Resource> map1 = new HashMap<>();
            map.forEach((p_250354_, p_250712_) -> {
               try (InputStream inputstream = p_250712_.open()) {
                  byte[] abyte = inputstream.readAllBytes();
                  map1.put(p_250354_, new Resource(p_250712_.source(), () -> {
                     return new ByteArrayInputStream(abyte);
                  }));
               } catch (Exception exception) {
                  GameRenderer.LOGGER.warn("Failed to read resource {}", p_250354_, exception);
               }

            });
            return new GameRenderer.ResourceCache(p_251213_, map1);
         }

         protected void apply(GameRenderer.ResourceCache p_251168_, ResourceManager p_248902_, ProfilerFiller p_251909_) {
            GameRenderer.this.reloadShaders(p_251168_);
            if (GameRenderer.this.postEffect != null) {
               GameRenderer.this.postEffect.close();
            }

            GameRenderer.this.postEffect = null;
            if (GameRenderer.this.effectIndex == GameRenderer.EFFECT_NONE) {
               GameRenderer.this.checkEntityPostEffect(GameRenderer.this.minecraft.getCameraEntity());
            } else {
               GameRenderer.this.loadEffect(GameRenderer.EFFECTS[GameRenderer.this.effectIndex]);
            }

         }

         public String getName() {
            return "Shader Loader";
         }
      };
   }

   public void preloadUiShader(ResourceProvider p_172723_) {
      if (this.blitShader != null) {
         throw new RuntimeException("Blit shader already preloaded");
      } else {
         try {
            this.blitShader = new ShaderInstance(p_172723_, "blit_screen", DefaultVertexFormat.BLIT_SCREEN);
         } catch (IOException ioexception) {
            throw new RuntimeException("could not preload blit shader", ioexception);
         }

         rendertypeGuiShader = this.preloadShader(p_172723_, "rendertype_gui", DefaultVertexFormat.POSITION_COLOR);
         rendertypeGuiOverlayShader = this.preloadShader(p_172723_, "rendertype_gui_overlay", DefaultVertexFormat.POSITION_COLOR);
         positionShader = this.preloadShader(p_172723_, "position", DefaultVertexFormat.POSITION);
         positionColorShader = this.preloadShader(p_172723_, "position_color", DefaultVertexFormat.POSITION_COLOR);
         positionColorTexShader = this.preloadShader(p_172723_, "position_color_tex", DefaultVertexFormat.POSITION_COLOR_TEX);
         positionTexShader = this.preloadShader(p_172723_, "position_tex", DefaultVertexFormat.POSITION_TEX);
         positionTexColorShader = this.preloadShader(p_172723_, "position_tex_color", DefaultVertexFormat.POSITION_TEX_COLOR);
         rendertypeTextShader = this.preloadShader(p_172723_, "rendertype_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
      }
   }

   private ShaderInstance preloadShader(ResourceProvider p_172725_, String p_172726_, VertexFormat p_172727_) {
      try {
         ShaderInstance shaderinstance = new ShaderInstance(p_172725_, p_172726_, p_172727_);
         this.shaders.put(p_172726_, shaderinstance);
         return shaderinstance;
      } catch (Exception exception) {
         throw new IllegalStateException("could not preload shader " + p_172726_, exception);
      }
   }

   void reloadShaders(ResourceProvider p_250719_) {
      RenderSystem.assertOnRenderThread();
      List<Program> list = Lists.newArrayList();
      list.addAll(Program.Type.FRAGMENT.getPrograms().values());
      list.addAll(Program.Type.VERTEX.getPrograms().values());
      list.forEach(Program::close);
      List<Pair<ShaderInstance, Consumer<ShaderInstance>>> list1 = Lists.newArrayListWithCapacity(this.shaders.size());

      try {
         list1.add(Pair.of(new ShaderInstance(p_250719_, "particle", DefaultVertexFormat.PARTICLE), (p_172714_) -> {
            particleShader = p_172714_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "position", DefaultVertexFormat.POSITION), (p_172711_) -> {
            positionShader = p_172711_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "position_color", DefaultVertexFormat.POSITION_COLOR), (p_172708_) -> {
            positionColorShader = p_172708_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "position_color_lightmap", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP), (p_172705_) -> {
            positionColorLightmapShader = p_172705_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "position_color_tex", DefaultVertexFormat.POSITION_COLOR_TEX), (p_172702_) -> {
            positionColorTexShader = p_172702_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "position_color_tex_lightmap", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), (p_172699_) -> {
            positionColorTexLightmapShader = p_172699_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "position_tex", DefaultVertexFormat.POSITION_TEX), (p_172696_) -> {
            positionTexShader = p_172696_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "position_tex_color", DefaultVertexFormat.POSITION_TEX_COLOR), (p_172693_) -> {
            positionTexColorShader = p_172693_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "position_tex_color_normal", DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL), (p_172690_) -> {
            positionTexColorNormalShader = p_172690_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "position_tex_lightmap_color", DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR), (p_172687_) -> {
            positionTexLightmapColorShader = p_172687_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_solid", DefaultVertexFormat.BLOCK), (p_172684_) -> {
            rendertypeSolidShader = p_172684_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_cutout_mipped", DefaultVertexFormat.BLOCK), (p_172681_) -> {
            rendertypeCutoutMippedShader = p_172681_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_cutout", DefaultVertexFormat.BLOCK), (p_172678_) -> {
            rendertypeCutoutShader = p_172678_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_translucent", DefaultVertexFormat.BLOCK), (p_172675_) -> {
            rendertypeTranslucentShader = p_172675_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_translucent_moving_block", DefaultVertexFormat.BLOCK), (p_172672_) -> {
            rendertypeTranslucentMovingBlockShader = p_172672_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_translucent_no_crumbling", DefaultVertexFormat.BLOCK), (p_172669_) -> {
            rendertypeTranslucentNoCrumblingShader = p_172669_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_armor_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY), (p_172666_) -> {
            rendertypeArmorCutoutNoCullShader = p_172666_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_solid", DefaultVertexFormat.NEW_ENTITY), (p_172663_) -> {
            rendertypeEntitySolidShader = p_172663_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_cutout", DefaultVertexFormat.NEW_ENTITY), (p_172660_) -> {
            rendertypeEntityCutoutShader = p_172660_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY), (p_172657_) -> {
            rendertypeEntityCutoutNoCullShader = p_172657_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_cutout_no_cull_z_offset", DefaultVertexFormat.NEW_ENTITY), (p_172654_) -> {
            rendertypeEntityCutoutNoCullZOffsetShader = p_172654_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY), (p_172651_) -> {
            rendertypeItemEntityTranslucentCullShader = p_172651_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY), (p_172648_) -> {
            rendertypeEntityTranslucentCullShader = p_172648_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_translucent", DefaultVertexFormat.NEW_ENTITY), (p_172645_) -> {
            rendertypeEntityTranslucentShader = p_172645_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY), (p_172642_) -> {
            rendertypeEntityTranslucentEmissiveShader = p_172642_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_smooth_cutout", DefaultVertexFormat.NEW_ENTITY), (p_172639_) -> {
            rendertypeEntitySmoothCutoutShader = p_172639_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_beacon_beam", DefaultVertexFormat.BLOCK), (p_172840_) -> {
            rendertypeBeaconBeamShader = p_172840_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_decal", DefaultVertexFormat.NEW_ENTITY), (p_172837_) -> {
            rendertypeEntityDecalShader = p_172837_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_no_outline", DefaultVertexFormat.NEW_ENTITY), (p_172834_) -> {
            rendertypeEntityNoOutlineShader = p_172834_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_shadow", DefaultVertexFormat.NEW_ENTITY), (p_172831_) -> {
            rendertypeEntityShadowShader = p_172831_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_alpha", DefaultVertexFormat.NEW_ENTITY), (p_172828_) -> {
            rendertypeEntityAlphaShader = p_172828_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_eyes", DefaultVertexFormat.NEW_ENTITY), (p_172825_) -> {
            rendertypeEyesShader = p_172825_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_energy_swirl", DefaultVertexFormat.NEW_ENTITY), (p_172822_) -> {
            rendertypeEnergySwirlShader = p_172822_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_leash", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP), (p_172819_) -> {
            rendertypeLeashShader = p_172819_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_water_mask", DefaultVertexFormat.POSITION), (p_172816_) -> {
            rendertypeWaterMaskShader = p_172816_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_outline", DefaultVertexFormat.POSITION_COLOR_TEX), (p_172813_) -> {
            rendertypeOutlineShader = p_172813_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_armor_glint", DefaultVertexFormat.POSITION_TEX), (p_172810_) -> {
            rendertypeArmorGlintShader = p_172810_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_armor_entity_glint", DefaultVertexFormat.POSITION_TEX), (p_172807_) -> {
            rendertypeArmorEntityGlintShader = p_172807_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_glint_translucent", DefaultVertexFormat.POSITION_TEX), (p_172805_) -> {
            rendertypeGlintTranslucentShader = p_172805_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_glint", DefaultVertexFormat.POSITION_TEX), (p_172803_) -> {
            rendertypeGlintShader = p_172803_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_glint_direct", DefaultVertexFormat.POSITION_TEX), (p_172801_) -> {
            rendertypeGlintDirectShader = p_172801_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_glint", DefaultVertexFormat.POSITION_TEX), (p_172799_) -> {
            rendertypeEntityGlintShader = p_172799_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_entity_glint_direct", DefaultVertexFormat.POSITION_TEX), (p_172796_) -> {
            rendertypeEntityGlintDirectShader = p_172796_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), (p_172794_) -> {
            rendertypeTextShader = p_172794_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_text_background", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP), (p_269657_) -> {
            rendertypeTextBackgroundShader = p_269657_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_text_intensity", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), (p_172792_) -> {
            rendertypeTextIntensityShader = p_172792_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_text_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), (p_172789_) -> {
            rendertypeTextSeeThroughShader = p_172789_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_text_background_see_through", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP), (p_269656_) -> {
            rendertypeTextBackgroundSeeThroughShader = p_269656_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_text_intensity_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), (p_172787_) -> {
            rendertypeTextIntensitySeeThroughShader = p_172787_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_lightning", DefaultVertexFormat.POSITION_COLOR), (p_172785_) -> {
            rendertypeLightningShader = p_172785_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_tripwire", DefaultVertexFormat.BLOCK), (p_172782_) -> {
            rendertypeTripwireShader = p_172782_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_end_portal", DefaultVertexFormat.POSITION), (p_172778_) -> {
            rendertypeEndPortalShader = p_172778_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_end_gateway", DefaultVertexFormat.POSITION), (p_172774_) -> {
            rendertypeEndGatewayShader = p_172774_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_lines", DefaultVertexFormat.POSITION_COLOR_NORMAL), (p_172733_) -> {
            rendertypeLinesShader = p_172733_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_crumbling", DefaultVertexFormat.BLOCK), (p_234230_) -> {
            rendertypeCrumblingShader = p_234230_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_gui", DefaultVertexFormat.POSITION_COLOR), (p_286148_) -> {
            rendertypeGuiShader = p_286148_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_gui_overlay", DefaultVertexFormat.POSITION_COLOR), (p_286146_) -> {
            rendertypeGuiOverlayShader = p_286146_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_gui_text_highlight", DefaultVertexFormat.POSITION_COLOR), (p_286145_) -> {
            rendertypeGuiTextHighlightShader = p_286145_;
         }));
         list1.add(Pair.of(new ShaderInstance(p_250719_, "rendertype_gui_ghost_recipe_overlay", DefaultVertexFormat.POSITION_COLOR), (p_286147_) -> {
            rendertypeGuiGhostRecipeOverlayShader = p_286147_;
         }));
         net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.client.event.RegisterShadersEvent(p_250719_, list1));
      } catch (IOException ioexception) {
         list1.forEach((p_172729_) -> {
            p_172729_.getFirst().close();
         });
         throw new RuntimeException("could not reload shaders", ioexception);
      }

      this.shutdownShaders();
      list1.forEach((p_234225_) -> {
         ShaderInstance shaderinstance = p_234225_.getFirst();
         this.shaders.put(shaderinstance.getName(), shaderinstance);
         p_234225_.getSecond().accept(shaderinstance);
      });
   }

   private void shutdownShaders() {
      RenderSystem.assertOnRenderThread();
      this.shaders.values().forEach(ShaderInstance::close);
      this.shaders.clear();
   }

   @Nullable
   public ShaderInstance getShader(@Nullable String p_172735_) {
      return p_172735_ == null ? null : this.shaders.get(p_172735_);
   }

   public void tick() {
      this.tickFov();
      this.lightTexture.tick();
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(this.minecraft.player);
      }

      this.mainCamera.tick();
      ++this.tick;
      this.itemInHandRenderer.tick();
      this.minecraft.levelRenderer.tickRain(this.mainCamera);
      this.darkenWorldAmountO = this.darkenWorldAmount;
      if (this.minecraft.gui.getBossOverlay().shouldDarkenScreen()) {
         this.darkenWorldAmount += 0.05F;
         if (this.darkenWorldAmount > 1.0F) {
            this.darkenWorldAmount = 1.0F;
         }
      } else if (this.darkenWorldAmount > 0.0F) {
         this.darkenWorldAmount -= 0.0125F;
      }

      if (this.itemActivationTicks > 0) {
         --this.itemActivationTicks;
         if (this.itemActivationTicks == 0) {
            this.itemActivationItem = null;
         }
      }

   }

   @Nullable
   public PostChain currentEffect() {
      return this.postEffect;
   }

   public void resize(int p_109098_, int p_109099_) {
      if (this.postEffect != null) {
         this.postEffect.resize(p_109098_, p_109099_);
      }

      this.minecraft.levelRenderer.resize(p_109098_, p_109099_);
   }

   public void pick(float p_109088_) {
      Entity entity = this.minecraft.getCameraEntity();
      if (entity != null) {
         if (this.minecraft.level != null) {
            this.minecraft.getProfiler().push("pick");
            this.minecraft.crosshairPickEntity = null;
            double d0 = (double)this.minecraft.gameMode.getPickRange();
            double entityReach = this.minecraft.player.getEntityReach(); // Note - MC-76493 - We must validate players cannot click-through objects.
            this.minecraft.hitResult = entity.pick(Math.max(d0, entityReach), p_109088_, false); // Run pick() with the max of the two, so we can prevent click-through.
            Vec3 vec3 = entity.getEyePosition(p_109088_);
            boolean flag = false;
            int i = 3;
            double d1 = d0;
            if (false && this.minecraft.gameMode.hasFarPickRange()) {
               d1 = 6.0D;
               d0 = d1;
            } else {
               if (d0 > 3.0D) {
                  flag = true;
               }

               d0 = d0;
            }
            d0 = d1 = Math.max(d0, entityReach); // Pick entities with the max of both for the same reason.

            d1 *= d1;
            // If we picked a block, we need to ignore entities past that block. Added != MISS check to not truncate on failed picks.
            // Also fixes MC-250858
            if (this.minecraft.hitResult != null && this.minecraft.hitResult.getType() != HitResult.Type.MISS) {
               d1 = this.minecraft.hitResult.getLocation().distanceToSqr(vec3);
               double blockReach = this.minecraft.player.getBlockReach();
               // Discard the result as a miss if it is outside the block reach.
               if (d1 > blockReach * blockReach) {
                  Vec3 pos = this.minecraft.hitResult.getLocation();
                  this.minecraft.hitResult = BlockHitResult.miss(pos, Direction.getNearest(vec3.x, vec3.y, vec3.z), BlockPos.containing(pos));
               }
            }

            Vec3 vec31 = entity.getViewVector(1.0F);
            Vec3 vec32 = vec3.add(vec31.x * d0, vec31.y * d0, vec31.z * d0);
            float f = 1.0F;
            AABB aabb = entity.getBoundingBox().expandTowards(vec31.scale(d0)).inflate(1.0D, 1.0D, 1.0D);
            EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(entity, vec3, vec32, aabb, (p_234237_) -> {
               return !p_234237_.isSpectator() && p_234237_.isPickable();
            }, d1);
            if (entityhitresult != null) {
               Entity entity1 = entityhitresult.getEntity();
               Vec3 vec33 = entityhitresult.getLocation();
               double d2 = vec3.distanceToSqr(vec33);
               if (d2 > d1 || d2 > entityReach * entityReach) { // Discard if the result is behind a block, or past the entity reach max. The var "flag" no longer has a use.
                  this.minecraft.hitResult = BlockHitResult.miss(vec33, Direction.getNearest(vec31.x, vec31.y, vec31.z), BlockPos.containing(vec33));
               } else if (d2 < d1 || this.minecraft.hitResult == null) {
                  this.minecraft.hitResult = entityhitresult;
                  if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrame) {
                     this.minecraft.crosshairPickEntity = entity1;
                  }
               }
            }

            this.minecraft.getProfiler().pop();
         }
      }
   }

   private void tickFov() {
      float f = 1.0F;
      if (this.minecraft.getCameraEntity() instanceof AbstractClientPlayer) {
         AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)this.minecraft.getCameraEntity();
         f = abstractclientplayer.getFieldOfViewModifier();
      }

      this.oldFov = this.fov;
      this.fov += (f - this.fov) * 0.5F;
      if (this.fov > 1.5F) {
         this.fov = 1.5F;
      }

      if (this.fov < 0.1F) {
         this.fov = 0.1F;
      }

   }

   private double getFov(Camera p_109142_, float p_109143_, boolean p_109144_) {
      if (this.panoramicMode) {
         return 90.0D;
      } else {
         double d0 = 70.0D;
         if (p_109144_) {
            d0 = (double)this.minecraft.options.fov().get().intValue();
            d0 *= (double)Mth.lerp(p_109143_, this.oldFov, this.fov);
         }

         if (p_109142_.getEntity() instanceof LivingEntity && ((LivingEntity)p_109142_.getEntity()).isDeadOrDying()) {
            float f = Math.min((float)((LivingEntity)p_109142_.getEntity()).deathTime + p_109143_, 20.0F);
            d0 /= (double)((1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F);
         }

         FogType fogtype = p_109142_.getFluidInCamera();
         if (fogtype == FogType.LAVA || fogtype == FogType.WATER) {
            d0 *= Mth.lerp(this.minecraft.options.fovEffectScale().get(), 1.0D, (double)0.85714287F);
         }

         return net.minecraftforge.client.ForgeHooksClient.getFieldOfView(this, p_109142_, p_109143_, d0, p_109144_);
      }
   }

   private void bobHurt(PoseStack p_109118_, float p_109119_) {
      if (this.minecraft.getCameraEntity() instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)this.minecraft.getCameraEntity();
         float f = (float)livingentity.hurtTime - p_109119_;
         if (livingentity.isDeadOrDying()) {
            float f1 = Math.min((float)livingentity.deathTime + p_109119_, 20.0F);
            p_109118_.mulPose(Axis.ZP.rotationDegrees(40.0F - 8000.0F / (f1 + 200.0F)));
         }

         if (f < 0.0F) {
            return;
         }

         f /= (float)livingentity.hurtDuration;
         f = Mth.sin(f * f * f * f * (float)Math.PI);
         float f3 = livingentity.getHurtDir();
         p_109118_.mulPose(Axis.YP.rotationDegrees(-f3));
         float f2 = (float)((double)(-f) * 14.0D * this.minecraft.options.damageTiltStrength().get());
         p_109118_.mulPose(Axis.ZP.rotationDegrees(f2));
         p_109118_.mulPose(Axis.YP.rotationDegrees(f3));
      }

   }

   private void bobView(PoseStack p_109139_, float p_109140_) {
      if (this.minecraft.getCameraEntity() instanceof Player) {
         Player player = (Player)this.minecraft.getCameraEntity();
         float f = player.walkDist - player.walkDistO;
         float f1 = -(player.walkDist + f * p_109140_);
         float f2 = Mth.lerp(p_109140_, player.oBob, player.bob);
         p_109139_.translate(Mth.sin(f1 * (float)Math.PI) * f2 * 0.5F, -Math.abs(Mth.cos(f1 * (float)Math.PI) * f2), 0.0F);
         p_109139_.mulPose(Axis.ZP.rotationDegrees(Mth.sin(f1 * (float)Math.PI) * f2 * 3.0F));
         p_109139_.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(f1 * (float)Math.PI - 0.2F) * f2) * 5.0F));
      }
   }

   public void renderZoomed(float p_172719_, float p_172720_, float p_172721_) {
      this.zoom = p_172719_;
      this.zoomX = p_172720_;
      this.zoomY = p_172721_;
      this.setRenderBlockOutline(false);
      this.setRenderHand(false);
      this.renderLevel(1.0F, 0L, new PoseStack());
      this.zoom = 1.0F;
   }

   private void renderItemInHand(PoseStack p_109121_, Camera p_109122_, float p_109123_) {
      if (!this.panoramicMode) {
         this.resetProjectionMatrix(this.getProjectionMatrix(this.getFov(p_109122_, p_109123_, false)));
         p_109121_.setIdentity();
         p_109121_.pushPose();
         this.bobHurt(p_109121_, p_109123_);
         if (this.minecraft.options.bobView().get()) {
            this.bobView(p_109121_, p_109123_);
         }

         boolean flag = this.minecraft.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.minecraft.getCameraEntity()).isSleeping();
         if (this.minecraft.options.getCameraType().isFirstPerson() && !flag && !this.minecraft.options.hideGui && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.lightTexture.turnOnLightLayer();
            this.itemInHandRenderer.renderHandsWithItems(p_109123_, p_109121_, this.renderBuffers.bufferSource(), this.minecraft.player, this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, p_109123_));
            this.lightTexture.turnOffLightLayer();
         }

         p_109121_.popPose();
         if (this.minecraft.options.getCameraType().isFirstPerson() && !flag) {
            ScreenEffectRenderer.renderScreenEffect(this.minecraft, p_109121_);
            this.bobHurt(p_109121_, p_109123_);
         }

         if (this.minecraft.options.bobView().get()) {
            this.bobView(p_109121_, p_109123_);
         }

      }
   }

   public void resetProjectionMatrix(Matrix4f p_253668_) {
      RenderSystem.setProjectionMatrix(p_253668_, VertexSorting.DISTANCE_TO_ORIGIN);
   }

   public Matrix4f getProjectionMatrix(double p_254507_) {
      PoseStack posestack = new PoseStack();
      posestack.last().pose().identity();
      if (this.zoom != 1.0F) {
         posestack.translate(this.zoomX, -this.zoomY, 0.0F);
         posestack.scale(this.zoom, this.zoom, 1.0F);
      }

      posestack.last().pose().mul((new Matrix4f()).setPerspective((float)(p_254507_ * (double)((float)Math.PI / 180F)), (float)this.minecraft.getWindow().getWidth() / (float)this.minecraft.getWindow().getHeight(), 0.05F, this.getDepthFar()));
      return posestack.last().pose();
   }

   public float getDepthFar() {
      return this.renderDistance * 4.0F;
   }

   public static float getNightVisionScale(LivingEntity p_109109_, float p_109110_) {
      MobEffectInstance mobeffectinstance = p_109109_.getEffect(MobEffects.NIGHT_VISION);
      return !mobeffectinstance.endsWithin(200) ? 1.0F : 0.7F + Mth.sin(((float)mobeffectinstance.getDuration() - p_109110_) * (float)Math.PI * 0.2F) * 0.3F;
   }

   public void render(float p_109094_, long p_109095_, boolean p_109096_) {
      if (!this.minecraft.isWindowActive() && this.minecraft.options.pauseOnLostFocus && (!this.minecraft.options.touchscreen().get() || !this.minecraft.mouseHandler.isRightPressed())) {
         if (Util.getMillis() - this.lastActiveTime > 500L) {
            this.minecraft.pauseGame(false);
         }
      } else {
         this.lastActiveTime = Util.getMillis();
      }

      if (!this.minecraft.noRender) {
         int i = (int)(this.minecraft.mouseHandler.xpos() * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth());
         int j = (int)(this.minecraft.mouseHandler.ypos() * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight());
         RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         if (p_109096_ && this.minecraft.level != null) {
            this.minecraft.getProfiler().push("level");
            this.renderLevel(p_109094_, p_109095_, new PoseStack());
            this.tryTakeScreenshotIfNeeded();
            this.minecraft.levelRenderer.doEntityOutline();
            if (this.postEffect != null && this.effectActive) {
               RenderSystem.disableBlend();
               RenderSystem.disableDepthTest();
               RenderSystem.resetTextureMatrix();
               this.postEffect.process(p_109094_);
            }

            this.minecraft.getMainRenderTarget().bindWrite(true);
         }

         Window window = this.minecraft.getWindow();
         RenderSystem.clear(256, Minecraft.ON_OSX);
         Matrix4f matrix4f = (new Matrix4f()).setOrtho(0.0F, (float)((double)window.getWidth() / window.getGuiScale()), (float)((double)window.getHeight() / window.getGuiScale()), 0.0F, 1000.0F, net.minecraftforge.client.ForgeHooksClient.getGuiFarPlane());
         RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
         PoseStack posestack = RenderSystem.getModelViewStack();
         posestack.pushPose();
         posestack.setIdentity();
         posestack.translate(0.0D, 0.0D, 1000F-net.minecraftforge.client.ForgeHooksClient.getGuiFarPlane());
         RenderSystem.applyModelViewMatrix();
         Lighting.setupFor3DItems();
         GuiGraphics guigraphics = new GuiGraphics(this.minecraft, this.renderBuffers.bufferSource());
         if (p_109096_ && this.minecraft.level != null) {
            this.minecraft.getProfiler().popPush("gui");
            if (this.minecraft.player != null) {
               float f = Mth.lerp(p_109094_, this.minecraft.player.oSpinningEffectIntensity, this.minecraft.player.spinningEffectIntensity);
               float f1 = this.minecraft.options.screenEffectScale().get().floatValue();
               if (f > 0.0F && this.minecraft.player.hasEffect(MobEffects.CONFUSION) && f1 < 1.0F) {
                  this.renderConfusionOverlay(guigraphics, f * (1.0F - f1));
               }
            }

            if (!this.minecraft.options.hideGui || this.minecraft.screen != null) {
               this.renderItemActivationAnimation(this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), p_109094_);
               this.minecraft.gui.render(guigraphics, p_109094_);
               RenderSystem.clear(256, Minecraft.ON_OSX);
            }

            this.minecraft.getProfiler().pop();
         }

         if (this.minecraft.getOverlay() != null) {
            try {
               this.minecraft.getOverlay().render(guigraphics, i, j, this.minecraft.getDeltaFrameTime());
            } catch (Throwable throwable2) {
               CrashReport crashreport = CrashReport.forThrowable(throwable2, "Rendering overlay");
               CrashReportCategory crashreportcategory = crashreport.addCategory("Overlay render details");
               crashreportcategory.setDetail("Overlay name", () -> {
                  return this.minecraft.getOverlay().getClass().getCanonicalName();
               });
               throw new ReportedException(crashreport);
            }
         } else if (this.minecraft.screen != null) {
            try {
               net.minecraftforge.client.ForgeHooksClient.drawScreen(this.minecraft.screen, guigraphics, i, j, this.minecraft.getDeltaFrameTime());
            } catch (Throwable throwable1) {
               CrashReport crashreport1 = CrashReport.forThrowable(throwable1, "Rendering screen");
               CrashReportCategory crashreportcategory1 = crashreport1.addCategory("Screen render details");
               crashreportcategory1.setDetail("Screen name", () -> {
                  return this.minecraft.screen.getClass().getCanonicalName();
               });
               crashreportcategory1.setDetail("Mouse location", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.minecraft.mouseHandler.xpos(), this.minecraft.mouseHandler.ypos());
               });
               crashreportcategory1.setDetail("Screen size", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.minecraft.getWindow().getGuiScale());
               });
               throw new ReportedException(crashreport1);
            }

            try {
               if (this.minecraft.screen != null) {
                  this.minecraft.screen.handleDelayedNarration();
               }
            } catch (Throwable throwable) {
               CrashReport crashreport2 = CrashReport.forThrowable(throwable, "Narrating screen");
               CrashReportCategory crashreportcategory2 = crashreport2.addCategory("Screen details");
               crashreportcategory2.setDetail("Screen name", () -> {
                  return this.minecraft.screen.getClass().getCanonicalName();
               });
               throw new ReportedException(crashreport2);
            }
         }

         this.minecraft.getProfiler().push("toasts");
         this.minecraft.getToasts().render(guigraphics);
         this.minecraft.getProfiler().pop();
         guigraphics.flush();
         posestack.popPose();
         RenderSystem.applyModelViewMatrix();
      }
   }

   private void tryTakeScreenshotIfNeeded() {
      if (!this.hasWorldScreenshot && this.minecraft.isLocalServer()) {
         long i = Util.getMillis();
         if (i - this.lastScreenshotAttempt >= 1000L) {
            this.lastScreenshotAttempt = i;
            IntegratedServer integratedserver = this.minecraft.getSingleplayerServer();
            if (integratedserver != null && !integratedserver.isStopped()) {
               integratedserver.getWorldScreenshotFile().ifPresent((p_234239_) -> {
                  if (Files.isRegularFile(p_234239_)) {
                     this.hasWorldScreenshot = true;
                  } else {
                     this.takeAutoScreenshot(p_234239_);
                  }

               });
            }
         }
      }
   }

   private void takeAutoScreenshot(Path p_182643_) {
      if (this.minecraft.levelRenderer.countRenderedChunks() > 10 && this.minecraft.levelRenderer.hasRenderedAllChunks()) {
         NativeImage nativeimage = Screenshot.takeScreenshot(this.minecraft.getMainRenderTarget());
         Util.ioPool().execute(() -> {
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            int k = 0;
            int l = 0;
            if (i > j) {
               k = (i - j) / 2;
               i = j;
            } else {
               l = (j - i) / 2;
               j = i;
            }

            try (NativeImage nativeimage1 = new NativeImage(64, 64, false)) {
               nativeimage.resizeSubRectTo(k, l, i, j, nativeimage1);
               nativeimage1.writeToFile(p_182643_);
            } catch (IOException ioexception) {
               LOGGER.warn("Couldn't save auto screenshot", (Throwable)ioexception);
            } finally {
               nativeimage.close();
            }

         });
      }

   }

   private boolean shouldRenderBlockOutline() {
      if (!this.renderBlockOutline) {
         return false;
      } else {
         Entity entity = this.minecraft.getCameraEntity();
         boolean flag = entity instanceof Player && !this.minecraft.options.hideGui;
         if (flag && !((Player)entity).getAbilities().mayBuild) {
            ItemStack itemstack = ((LivingEntity)entity).getMainHandItem();
            HitResult hitresult = this.minecraft.hitResult;
            if (hitresult != null && hitresult.getType() == HitResult.Type.BLOCK) {
               BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
               BlockState blockstate = this.minecraft.level.getBlockState(blockpos);
               if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
                  flag = blockstate.getMenuProvider(this.minecraft.level, blockpos) != null;
               } else {
                  BlockInWorld blockinworld = new BlockInWorld(this.minecraft.level, blockpos, false);
                  Registry<Block> registry = this.minecraft.level.registryAccess().registryOrThrow(Registries.BLOCK);
                  flag = !itemstack.isEmpty() && (itemstack.hasAdventureModeBreakTagForBlock(registry, blockinworld) || itemstack.hasAdventureModePlaceTagForBlock(registry, blockinworld));
               }
            }
         }

         return flag;
      }
   }

   public void renderLevel(float p_109090_, long p_109091_, PoseStack p_109092_) {
      this.lightTexture.updateLightTexture(p_109090_);
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(this.minecraft.player);
      }

      this.pick(p_109090_);
      this.minecraft.getProfiler().push("center");
      boolean flag = this.shouldRenderBlockOutline();
      this.minecraft.getProfiler().popPush("camera");
      Camera camera = this.mainCamera;
      this.renderDistance = (float)(this.minecraft.options.getEffectiveRenderDistance() * 16);
      PoseStack posestack = new PoseStack();
      double d0 = this.getFov(camera, p_109090_, true);
      posestack.mulPoseMatrix(this.getProjectionMatrix(d0));
      this.bobHurt(posestack, p_109090_);
      if (this.minecraft.options.bobView().get()) {
         this.bobView(posestack, p_109090_);
      }

      float f = this.minecraft.options.screenEffectScale().get().floatValue();
      float f1 = Mth.lerp(p_109090_, this.minecraft.player.oSpinningEffectIntensity, this.minecraft.player.spinningEffectIntensity) * f * f;
      if (f1 > 0.0F) {
         int i = this.minecraft.player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
         float f2 = 5.0F / (f1 * f1 + 5.0F) - f1 * 0.04F;
         f2 *= f2;
         Axis axis = Axis.of(new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F));
         posestack.mulPose(axis.rotationDegrees(((float)this.tick + p_109090_) * (float)i));
         posestack.scale(1.0F / f2, 1.0F, 1.0F);
         float f3 = -((float)this.tick + p_109090_) * (float)i;
         posestack.mulPose(axis.rotationDegrees(f3));
      }

      Matrix4f matrix4f = posestack.last().pose();
      this.resetProjectionMatrix(matrix4f);
      camera.setup(this.minecraft.level, (Entity)(this.minecraft.getCameraEntity() == null ? this.minecraft.player : this.minecraft.getCameraEntity()), !this.minecraft.options.getCameraType().isFirstPerson(), this.minecraft.options.getCameraType().isMirrored(), p_109090_);

      net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles cameraSetup = net.minecraftforge.client.ForgeHooksClient.onCameraSetup(this, camera, p_109090_);
      camera.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
      p_109092_.mulPose(Axis.ZP.rotationDegrees(cameraSetup.getRoll()));

      p_109092_.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
      p_109092_.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
      Matrix3f matrix3f = (new Matrix3f(p_109092_.last().normal())).invert();
      RenderSystem.setInverseViewRotationMatrix(matrix3f);
      this.minecraft.levelRenderer.prepareCullFrustum(p_109092_, camera.getPosition(), this.getProjectionMatrix(Math.max(d0, (double)this.minecraft.options.fov().get().intValue())));
      this.minecraft.levelRenderer.renderLevel(p_109092_, p_109090_, p_109091_, flag, camera, this, this.lightTexture, matrix4f);
      this.minecraft.getProfiler().popPush("forge_render_last");
      net.minecraftforge.client.ForgeHooksClient.dispatchRenderStage(net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_LEVEL, this.minecraft.levelRenderer, posestack, matrix4f, this.minecraft.levelRenderer.getTicks(), camera, this.minecraft.levelRenderer.getFrustum());
      this.minecraft.getProfiler().popPush("hand");
      if (this.renderHand) {
         RenderSystem.clear(256, Minecraft.ON_OSX);
         this.renderItemInHand(p_109092_, camera, p_109090_);
      }

      this.minecraft.getProfiler().pop();
   }

   public void resetData() {
      this.itemActivationItem = null;
      this.mapRenderer.resetData();
      this.mainCamera.reset();
      this.hasWorldScreenshot = false;
   }

   public MapRenderer getMapRenderer() {
      return this.mapRenderer;
   }

   public void displayItemActivation(ItemStack p_109114_) {
      this.itemActivationItem = p_109114_;
      this.itemActivationTicks = 40;
      this.itemActivationOffX = this.random.nextFloat() * 2.0F - 1.0F;
      this.itemActivationOffY = this.random.nextFloat() * 2.0F - 1.0F;
   }

   private void renderItemActivationAnimation(int p_109101_, int p_109102_, float p_109103_) {
      if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
         int i = 40 - this.itemActivationTicks;
         float f = ((float)i + p_109103_) / 40.0F;
         float f1 = f * f;
         float f2 = f * f1;
         float f3 = 10.25F * f2 * f1 - 24.95F * f1 * f1 + 25.5F * f2 - 13.8F * f1 + 4.0F * f;
         float f4 = f3 * (float)Math.PI;
         float f5 = this.itemActivationOffX * (float)(p_109101_ / 4);
         float f6 = this.itemActivationOffY * (float)(p_109102_ / 4);
         RenderSystem.enableDepthTest();
         RenderSystem.disableCull();
         PoseStack posestack = new PoseStack();
         posestack.pushPose();
         posestack.translate((float)(p_109101_ / 2) + f5 * Mth.abs(Mth.sin(f4 * 2.0F)), (float)(p_109102_ / 2) + f6 * Mth.abs(Mth.sin(f4 * 2.0F)), -50.0F);
         float f7 = 50.0F + 175.0F * Mth.sin(f4);
         posestack.scale(f7, -f7, f7);
         posestack.mulPose(Axis.YP.rotationDegrees(900.0F * Mth.abs(Mth.sin(f4))));
         posestack.mulPose(Axis.XP.rotationDegrees(6.0F * Mth.cos(f * 8.0F)));
         posestack.mulPose(Axis.ZP.rotationDegrees(6.0F * Mth.cos(f * 8.0F)));
         MultiBufferSource.BufferSource multibuffersource$buffersource = this.renderBuffers.bufferSource();
         this.minecraft.getItemRenderer().renderStatic(this.itemActivationItem, ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, posestack, multibuffersource$buffersource, this.minecraft.level, 0);
         posestack.popPose();
         multibuffersource$buffersource.endBatch();
         RenderSystem.enableCull();
         RenderSystem.disableDepthTest();
      }
   }

   private void renderConfusionOverlay(GuiGraphics p_282460_, float p_282656_) {
      int i = p_282460_.guiWidth();
      int j = p_282460_.guiHeight();
      p_282460_.pose().pushPose();
      float f = Mth.lerp(p_282656_, 2.0F, 1.0F);
      p_282460_.pose().translate((float)i / 2.0F, (float)j / 2.0F, 0.0F);
      p_282460_.pose().scale(f, f, f);
      p_282460_.pose().translate((float)(-i) / 2.0F, (float)(-j) / 2.0F, 0.0F);
      float f1 = 0.2F * p_282656_;
      float f2 = 0.4F * p_282656_;
      float f3 = 0.2F * p_282656_;
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
      p_282460_.setColor(f1, f2, f3, 1.0F);
      p_282460_.blit(NAUSEA_LOCATION, 0, 0, -90, 0.0F, 0.0F, i, j, i, j);
      p_282460_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      p_282460_.pose().popPose();
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public float getDarkenWorldAmount(float p_109132_) {
      return Mth.lerp(p_109132_, this.darkenWorldAmountO, this.darkenWorldAmount);
   }

   public float getRenderDistance() {
      return this.renderDistance;
   }

   public Camera getMainCamera() {
      return this.mainCamera;
   }

   public LightTexture lightTexture() {
      return this.lightTexture;
   }

   public OverlayTexture overlayTexture() {
      return this.overlayTexture;
   }

   @Nullable
   public static ShaderInstance getPositionShader() {
      return positionShader;
   }

   @Nullable
   public static ShaderInstance getPositionColorShader() {
      return positionColorShader;
   }

   @Nullable
   public static ShaderInstance getPositionColorTexShader() {
      return positionColorTexShader;
   }

   @Nullable
   public static ShaderInstance getPositionTexShader() {
      return positionTexShader;
   }

   @Nullable
   public static ShaderInstance getPositionTexColorShader() {
      return positionTexColorShader;
   }

   @Nullable
   public static ShaderInstance getParticleShader() {
      return particleShader;
   }

   @Nullable
   public static ShaderInstance getPositionColorLightmapShader() {
      return positionColorLightmapShader;
   }

   @Nullable
   public static ShaderInstance getPositionColorTexLightmapShader() {
      return positionColorTexLightmapShader;
   }

   @Nullable
   public static ShaderInstance getPositionTexColorNormalShader() {
      return positionTexColorNormalShader;
   }

   @Nullable
   public static ShaderInstance getPositionTexLightmapColorShader() {
      return positionTexLightmapColorShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeSolidShader() {
      return rendertypeSolidShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeCutoutMippedShader() {
      return rendertypeCutoutMippedShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeCutoutShader() {
      return rendertypeCutoutShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTranslucentShader() {
      return rendertypeTranslucentShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTranslucentMovingBlockShader() {
      return rendertypeTranslucentMovingBlockShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTranslucentNoCrumblingShader() {
      return rendertypeTranslucentNoCrumblingShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeArmorCutoutNoCullShader() {
      return rendertypeArmorCutoutNoCullShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntitySolidShader() {
      return rendertypeEntitySolidShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityCutoutShader() {
      return rendertypeEntityCutoutShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityCutoutNoCullShader() {
      return rendertypeEntityCutoutNoCullShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityCutoutNoCullZOffsetShader() {
      return rendertypeEntityCutoutNoCullZOffsetShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeItemEntityTranslucentCullShader() {
      return rendertypeItemEntityTranslucentCullShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityTranslucentCullShader() {
      return rendertypeEntityTranslucentCullShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityTranslucentShader() {
      return rendertypeEntityTranslucentShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityTranslucentEmissiveShader() {
      return rendertypeEntityTranslucentEmissiveShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntitySmoothCutoutShader() {
      return rendertypeEntitySmoothCutoutShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeBeaconBeamShader() {
      return rendertypeBeaconBeamShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityDecalShader() {
      return rendertypeEntityDecalShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityNoOutlineShader() {
      return rendertypeEntityNoOutlineShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityShadowShader() {
      return rendertypeEntityShadowShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityAlphaShader() {
      return rendertypeEntityAlphaShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEyesShader() {
      return rendertypeEyesShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEnergySwirlShader() {
      return rendertypeEnergySwirlShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeLeashShader() {
      return rendertypeLeashShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeWaterMaskShader() {
      return rendertypeWaterMaskShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeOutlineShader() {
      return rendertypeOutlineShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeArmorGlintShader() {
      return rendertypeArmorGlintShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeArmorEntityGlintShader() {
      return rendertypeArmorEntityGlintShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGlintTranslucentShader() {
      return rendertypeGlintTranslucentShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGlintShader() {
      return rendertypeGlintShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGlintDirectShader() {
      return rendertypeGlintDirectShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityGlintShader() {
      return rendertypeEntityGlintShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityGlintDirectShader() {
      return rendertypeEntityGlintDirectShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTextShader() {
      return rendertypeTextShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTextBackgroundShader() {
      return rendertypeTextBackgroundShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTextIntensityShader() {
      return rendertypeTextIntensityShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTextSeeThroughShader() {
      return rendertypeTextSeeThroughShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTextBackgroundSeeThroughShader() {
      return rendertypeTextBackgroundSeeThroughShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTextIntensitySeeThroughShader() {
      return rendertypeTextIntensitySeeThroughShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeLightningShader() {
      return rendertypeLightningShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTripwireShader() {
      return rendertypeTripwireShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEndPortalShader() {
      return rendertypeEndPortalShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEndGatewayShader() {
      return rendertypeEndGatewayShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeLinesShader() {
      return rendertypeLinesShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeCrumblingShader() {
      return rendertypeCrumblingShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGuiShader() {
      return rendertypeGuiShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGuiOverlayShader() {
      return rendertypeGuiOverlayShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGuiTextHighlightShader() {
      return rendertypeGuiTextHighlightShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGuiGhostRecipeOverlayShader() {
      return rendertypeGuiGhostRecipeOverlayShader;
   }

   @OnlyIn(Dist.CLIENT)
   public static record ResourceCache(ResourceProvider original, Map<ResourceLocation, Resource> cache) implements ResourceProvider {
      public Optional<Resource> getResource(ResourceLocation p_251007_) {
         Resource resource = this.cache.get(p_251007_);
         return resource != null ? Optional.of(resource) : this.original.getResource(p_251007_);
      }
   }
}
