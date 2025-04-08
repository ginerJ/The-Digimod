package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Divisor;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2ic;

@OnlyIn(Dist.CLIENT)
public class GuiGraphics implements net.minecraftforge.client.extensions.IForgeGuiGraphics {
   public static final float MAX_GUI_Z = 10000.0F;
   public static final float MIN_GUI_Z = -10000.0F;
   private static final int EXTRA_SPACE_AFTER_FIRST_TOOLTIP_LINE = 2;
   private final Minecraft minecraft;
   private final PoseStack pose;
   private final MultiBufferSource.BufferSource bufferSource;
   private final GuiGraphics.ScissorStack scissorStack = new GuiGraphics.ScissorStack();
   private boolean managed;

   private GuiGraphics(Minecraft p_282144_, PoseStack p_281551_, MultiBufferSource.BufferSource p_281460_) {
      this.minecraft = p_282144_;
      this.pose = p_281551_;
      this.bufferSource = p_281460_;
   }

   public GuiGraphics(Minecraft p_283406_, MultiBufferSource.BufferSource p_282238_) {
      this(p_283406_, new PoseStack(), p_282238_);
   }

   /** @deprecated */
   @Deprecated
   public void drawManaged(Runnable p_286277_) {
      this.flush();
      this.managed = true;
      p_286277_.run();
      this.managed = false;
      this.flush();
   }

   /** @deprecated */
   @Deprecated
   private void flushIfUnmanaged() {
      if (!this.managed) {
         this.flush();
      }

   }

   /** @deprecated */
   @Deprecated
   private void flushIfManaged() {
      if (this.managed) {
         this.flush();
      }

   }

   public int guiWidth() {
      return this.minecraft.getWindow().getGuiScaledWidth();
   }

   public int guiHeight() {
      return this.minecraft.getWindow().getGuiScaledHeight();
   }

   public PoseStack pose() {
      return this.pose;
   }

   public MultiBufferSource.BufferSource bufferSource() {
      return this.bufferSource;
   }

   public void flush() {
      RenderSystem.disableDepthTest();
      this.bufferSource.endBatch();
      RenderSystem.enableDepthTest();
   }

   public void hLine(int p_283318_, int p_281662_, int p_281346_, int p_281672_) {
      this.hLine(RenderType.gui(), p_283318_, p_281662_, p_281346_, p_281672_);
   }

   public void hLine(RenderType p_286630_, int p_286453_, int p_286247_, int p_286814_, int p_286623_) {
      if (p_286247_ < p_286453_) {
         int i = p_286453_;
         p_286453_ = p_286247_;
         p_286247_ = i;
      }

      this.fill(p_286630_, p_286453_, p_286814_, p_286247_ + 1, p_286814_ + 1, p_286623_);
   }

   public void vLine(int p_282951_, int p_281591_, int p_281568_, int p_282718_) {
      this.vLine(RenderType.gui(), p_282951_, p_281591_, p_281568_, p_282718_);
   }

   public void vLine(RenderType p_286607_, int p_286309_, int p_286480_, int p_286707_, int p_286855_) {
      if (p_286707_ < p_286480_) {
         int i = p_286480_;
         p_286480_ = p_286707_;
         p_286707_ = i;
      }

      this.fill(p_286607_, p_286309_, p_286480_ + 1, p_286309_ + 1, p_286707_, p_286855_);
   }

   public void enableScissor(int p_281479_, int p_282788_, int p_282924_, int p_282826_) {
      this.applyScissor(this.scissorStack.push(new ScreenRectangle(p_281479_, p_282788_, p_282924_ - p_281479_, p_282826_ - p_282788_)));
   }

   public void disableScissor() {
      this.applyScissor(this.scissorStack.pop());
   }

   private void applyScissor(@Nullable ScreenRectangle p_281569_) {
      this.flushIfManaged();
      if (p_281569_ != null) {
         Window window = Minecraft.getInstance().getWindow();
         int i = window.getHeight();
         double d0 = window.getGuiScale();
         double d1 = (double)p_281569_.left() * d0;
         double d2 = (double)i - (double)p_281569_.bottom() * d0;
         double d3 = (double)p_281569_.width() * d0;
         double d4 = (double)p_281569_.height() * d0;
         RenderSystem.enableScissor((int)d1, (int)d2, Math.max(0, (int)d3), Math.max(0, (int)d4));
      } else {
         RenderSystem.disableScissor();
      }

   }

   public void setColor(float p_281272_, float p_281734_, float p_282022_, float p_281752_) {
      this.flushIfManaged();
      RenderSystem.setShaderColor(p_281272_, p_281734_, p_282022_, p_281752_);
   }

   public void fill(int p_282988_, int p_282861_, int p_281278_, int p_281710_, int p_281470_) {
      this.fill(p_282988_, p_282861_, p_281278_, p_281710_, 0, p_281470_);
   }

   public void fill(int p_281437_, int p_283660_, int p_282606_, int p_283413_, int p_283428_, int p_283253_) {
      this.fill(RenderType.gui(), p_281437_, p_283660_, p_282606_, p_283413_, p_283428_, p_283253_);
   }

   public void fill(RenderType p_286602_, int p_286738_, int p_286614_, int p_286741_, int p_286610_, int p_286560_) {
      this.fill(p_286602_, p_286738_, p_286614_, p_286741_, p_286610_, 0, p_286560_);
   }

   public void fill(RenderType p_286711_, int p_286234_, int p_286444_, int p_286244_, int p_286411_, int p_286671_, int p_286599_) {
      Matrix4f matrix4f = this.pose.last().pose();
      if (p_286234_ < p_286244_) {
         int i = p_286234_;
         p_286234_ = p_286244_;
         p_286244_ = i;
      }

      if (p_286444_ < p_286411_) {
         int j = p_286444_;
         p_286444_ = p_286411_;
         p_286411_ = j;
      }

      float f3 = (float)FastColor.ARGB32.alpha(p_286599_) / 255.0F;
      float f = (float)FastColor.ARGB32.red(p_286599_) / 255.0F;
      float f1 = (float)FastColor.ARGB32.green(p_286599_) / 255.0F;
      float f2 = (float)FastColor.ARGB32.blue(p_286599_) / 255.0F;
      VertexConsumer vertexconsumer = this.bufferSource.getBuffer(p_286711_);
      vertexconsumer.vertex(matrix4f, (float)p_286234_, (float)p_286444_, (float)p_286671_).color(f, f1, f2, f3).endVertex();
      vertexconsumer.vertex(matrix4f, (float)p_286234_, (float)p_286411_, (float)p_286671_).color(f, f1, f2, f3).endVertex();
      vertexconsumer.vertex(matrix4f, (float)p_286244_, (float)p_286411_, (float)p_286671_).color(f, f1, f2, f3).endVertex();
      vertexconsumer.vertex(matrix4f, (float)p_286244_, (float)p_286444_, (float)p_286671_).color(f, f1, f2, f3).endVertex();
      this.flushIfUnmanaged();
   }

   public void fillGradient(int p_283290_, int p_283278_, int p_282670_, int p_281698_, int p_283374_, int p_283076_) {
      this.fillGradient(p_283290_, p_283278_, p_282670_, p_281698_, 0, p_283374_, p_283076_);
   }

   public void fillGradient(int p_282702_, int p_282331_, int p_281415_, int p_283118_, int p_282419_, int p_281954_, int p_282607_) {
      this.fillGradient(RenderType.gui(), p_282702_, p_282331_, p_281415_, p_283118_, p_281954_, p_282607_, p_282419_);
   }

   public void fillGradient(RenderType p_286522_, int p_286535_, int p_286839_, int p_286242_, int p_286856_, int p_286809_, int p_286833_, int p_286706_) {
      VertexConsumer vertexconsumer = this.bufferSource.getBuffer(p_286522_);
      this.fillGradient(vertexconsumer, p_286535_, p_286839_, p_286242_, p_286856_, p_286706_, p_286809_, p_286833_);
      this.flushIfUnmanaged();
   }

   private void fillGradient(VertexConsumer p_286862_, int p_283414_, int p_281397_, int p_283587_, int p_281521_, int p_283505_, int p_283131_, int p_282949_) {
      float f = (float)FastColor.ARGB32.alpha(p_283131_) / 255.0F;
      float f1 = (float)FastColor.ARGB32.red(p_283131_) / 255.0F;
      float f2 = (float)FastColor.ARGB32.green(p_283131_) / 255.0F;
      float f3 = (float)FastColor.ARGB32.blue(p_283131_) / 255.0F;
      float f4 = (float)FastColor.ARGB32.alpha(p_282949_) / 255.0F;
      float f5 = (float)FastColor.ARGB32.red(p_282949_) / 255.0F;
      float f6 = (float)FastColor.ARGB32.green(p_282949_) / 255.0F;
      float f7 = (float)FastColor.ARGB32.blue(p_282949_) / 255.0F;
      Matrix4f matrix4f = this.pose.last().pose();
      p_286862_.vertex(matrix4f, (float)p_283414_, (float)p_281397_, (float)p_283505_).color(f1, f2, f3, f).endVertex();
      p_286862_.vertex(matrix4f, (float)p_283414_, (float)p_281521_, (float)p_283505_).color(f5, f6, f7, f4).endVertex();
      p_286862_.vertex(matrix4f, (float)p_283587_, (float)p_281521_, (float)p_283505_).color(f5, f6, f7, f4).endVertex();
      p_286862_.vertex(matrix4f, (float)p_283587_, (float)p_281397_, (float)p_283505_).color(f1, f2, f3, f).endVertex();
   }

   public void drawCenteredString(Font p_282122_, String p_282898_, int p_281490_, int p_282853_, int p_281258_) {
      this.drawString(p_282122_, p_282898_, p_281490_ - p_282122_.width(p_282898_) / 2, p_282853_, p_281258_);
   }

   public void drawCenteredString(Font p_282901_, Component p_282456_, int p_283083_, int p_282276_, int p_281457_) {
      FormattedCharSequence formattedcharsequence = p_282456_.getVisualOrderText();
      this.drawString(p_282901_, formattedcharsequence, p_283083_ - p_282901_.width(formattedcharsequence) / 2, p_282276_, p_281457_);
   }

   public void drawCenteredString(Font p_282592_, FormattedCharSequence p_281854_, int p_281573_, int p_283511_, int p_282577_) {
      this.drawString(p_282592_, p_281854_, p_281573_ - p_282592_.width(p_281854_) / 2, p_283511_, p_282577_);
   }

   public int drawString(Font p_282003_, @Nullable String p_281403_, int p_282714_, int p_282041_, int p_281908_) {
      return this.drawString(p_282003_, p_281403_, p_282714_, p_282041_, p_281908_, true);
   }

   public int drawString(Font p_283343_, @Nullable String p_281896_, int p_283569_, int p_283418_, int p_281560_, boolean p_282130_) {
      return this.drawString(p_283343_, p_281896_, (float)p_283569_, (float)p_283418_, p_281560_, p_282130_);
   }

   // Forge: Add float variant for x,y coordinates
   public int drawString(Font p_283343_, @Nullable String p_281896_, float p_283569_, float p_283418_, int p_281560_, boolean p_282130_) {
      if (p_281896_ == null) {
         return 0;
      } else {
         int i = p_283343_.drawInBatch(p_281896_, (float)p_283569_, (float)p_283418_, p_281560_, p_282130_, this.pose.last().pose(), this.bufferSource, Font.DisplayMode.NORMAL, 0, 15728880, p_283343_.isBidirectional());
         this.flushIfUnmanaged();
         return i;
      }
   }

   public int drawString(Font p_283019_, FormattedCharSequence p_283376_, int p_283379_, int p_283346_, int p_282119_) {
      return this.drawString(p_283019_, p_283376_, p_283379_, p_283346_, p_282119_, true);
   }

   public int drawString(Font p_282636_, FormattedCharSequence p_281596_, int p_281586_, int p_282816_, int p_281743_, boolean p_282394_) {
      return this.drawString(p_282636_, p_281596_, (float)p_281586_, (float)p_282816_, p_281743_, p_282394_);
   }

   // Forge: Add float variant for x,y coordinates
   public int drawString(Font p_282636_, FormattedCharSequence p_281596_, float p_281586_, float p_282816_, int p_281743_, boolean p_282394_) {
      int i = p_282636_.drawInBatch(p_281596_, (float)p_281586_, (float)p_282816_, p_281743_, p_282394_, this.pose.last().pose(), this.bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
      this.flushIfUnmanaged();
      return i;
   }

   public int drawString(Font p_281653_, Component p_283140_, int p_283102_, int p_282347_, int p_281429_) {
      return this.drawString(p_281653_, p_283140_, p_283102_, p_282347_, p_281429_, true);
   }

   public int drawString(Font p_281547_, Component p_282131_, int p_282857_, int p_281250_, int p_282195_, boolean p_282791_) {
      return this.drawString(p_281547_, p_282131_.getVisualOrderText(), p_282857_, p_281250_, p_282195_, p_282791_);
   }

   public void drawWordWrap(Font p_281494_, FormattedText p_283463_, int p_282183_, int p_283250_, int p_282564_, int p_282629_) {
      for(FormattedCharSequence formattedcharsequence : p_281494_.split(p_283463_, p_282564_)) {
         this.drawString(p_281494_, formattedcharsequence, p_282183_, p_283250_, p_282629_, false);
         p_283250_ += 9;
      }

   }

   public void blit(int p_282225_, int p_281487_, int p_281985_, int p_281329_, int p_283035_, TextureAtlasSprite p_281614_) {
      this.innerBlit(p_281614_.atlasLocation(), p_282225_, p_282225_ + p_281329_, p_281487_, p_281487_ + p_283035_, p_281985_, p_281614_.getU0(), p_281614_.getU1(), p_281614_.getV0(), p_281614_.getV1());
   }

   public void blit(int p_282416_, int p_282989_, int p_282618_, int p_282755_, int p_281717_, TextureAtlasSprite p_281874_, float p_283559_, float p_282730_, float p_283530_, float p_282246_) {
      this.innerBlit(p_281874_.atlasLocation(), p_282416_, p_282416_ + p_282755_, p_282989_, p_282989_ + p_281717_, p_282618_, p_281874_.getU0(), p_281874_.getU1(), p_281874_.getV0(), p_281874_.getV1(), p_283559_, p_282730_, p_283530_, p_282246_);
   }

   public void renderOutline(int p_281496_, int p_282076_, int p_281334_, int p_283576_, int p_283618_) {
      this.fill(p_281496_, p_282076_, p_281496_ + p_281334_, p_282076_ + 1, p_283618_);
      this.fill(p_281496_, p_282076_ + p_283576_ - 1, p_281496_ + p_281334_, p_282076_ + p_283576_, p_283618_);
      this.fill(p_281496_, p_282076_ + 1, p_281496_ + 1, p_282076_ + p_283576_ - 1, p_283618_);
      this.fill(p_281496_ + p_281334_ - 1, p_282076_ + 1, p_281496_ + p_281334_, p_282076_ + p_283576_ - 1, p_283618_);
   }

   public void blit(ResourceLocation p_283377_, int p_281970_, int p_282111_, int p_283134_, int p_282778_, int p_281478_, int p_281821_) {
      this.blit(p_283377_, p_281970_, p_282111_, 0, (float)p_283134_, (float)p_282778_, p_281478_, p_281821_, 256, 256);
   }

   public void blit(ResourceLocation p_283573_, int p_283574_, int p_283670_, int p_283545_, float p_283029_, float p_283061_, int p_282845_, int p_282558_, int p_282832_, int p_281851_) {
      this.blit(p_283573_, p_283574_, p_283574_ + p_282845_, p_283670_, p_283670_ + p_282558_, p_283545_, p_282845_, p_282558_, p_283029_, p_283061_, p_282832_, p_281851_);
   }

   public void blit(ResourceLocation p_282034_, int p_283671_, int p_282377_, int p_282058_, int p_281939_, float p_282285_, float p_283199_, int p_282186_, int p_282322_, int p_282481_, int p_281887_) {
      this.blit(p_282034_, p_283671_, p_283671_ + p_282058_, p_282377_, p_282377_ + p_281939_, 0, p_282186_, p_282322_, p_282285_, p_283199_, p_282481_, p_281887_);
   }

   public void blit(ResourceLocation p_283272_, int p_283605_, int p_281879_, float p_282809_, float p_282942_, int p_281922_, int p_282385_, int p_282596_, int p_281699_) {
      this.blit(p_283272_, p_283605_, p_281879_, p_281922_, p_282385_, p_282809_, p_282942_, p_281922_, p_282385_, p_282596_, p_281699_);
   }

   void blit(ResourceLocation p_282639_, int p_282732_, int p_283541_, int p_281760_, int p_283298_, int p_283429_, int p_282193_, int p_281980_, float p_282660_, float p_281522_, int p_282315_, int p_281436_) {
      this.innerBlit(p_282639_, p_282732_, p_283541_, p_281760_, p_283298_, p_283429_, (p_282660_ + 0.0F) / (float)p_282315_, (p_282660_ + (float)p_282193_) / (float)p_282315_, (p_281522_ + 0.0F) / (float)p_281436_, (p_281522_ + (float)p_281980_) / (float)p_281436_);
   }

   void innerBlit(ResourceLocation p_283461_, int p_281399_, int p_283222_, int p_283615_, int p_283430_, int p_281729_, float p_283247_, float p_282598_, float p_282883_, float p_283017_) {
      RenderSystem.setShaderTexture(0, p_283461_);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      Matrix4f matrix4f = this.pose.last().pose();
      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.vertex(matrix4f, (float)p_281399_, (float)p_283615_, (float)p_281729_).uv(p_283247_, p_282883_).endVertex();
      bufferbuilder.vertex(matrix4f, (float)p_281399_, (float)p_283430_, (float)p_281729_).uv(p_283247_, p_283017_).endVertex();
      bufferbuilder.vertex(matrix4f, (float)p_283222_, (float)p_283430_, (float)p_281729_).uv(p_282598_, p_283017_).endVertex();
      bufferbuilder.vertex(matrix4f, (float)p_283222_, (float)p_283615_, (float)p_281729_).uv(p_282598_, p_282883_).endVertex();
      BufferUploader.drawWithShader(bufferbuilder.end());
   }

   void innerBlit(ResourceLocation p_283254_, int p_283092_, int p_281930_, int p_282113_, int p_281388_, int p_283583_, float p_281327_, float p_281676_, float p_283166_, float p_282630_, float p_282800_, float p_282850_, float p_282375_, float p_282754_) {
      RenderSystem.setShaderTexture(0, p_283254_);
      RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
      RenderSystem.enableBlend();
      Matrix4f matrix4f = this.pose.last().pose();
      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
      bufferbuilder.vertex(matrix4f, (float)p_283092_, (float)p_282113_, (float)p_283583_).color(p_282800_, p_282850_, p_282375_, p_282754_).uv(p_281327_, p_283166_).endVertex();
      bufferbuilder.vertex(matrix4f, (float)p_283092_, (float)p_281388_, (float)p_283583_).color(p_282800_, p_282850_, p_282375_, p_282754_).uv(p_281327_, p_282630_).endVertex();
      bufferbuilder.vertex(matrix4f, (float)p_281930_, (float)p_281388_, (float)p_283583_).color(p_282800_, p_282850_, p_282375_, p_282754_).uv(p_281676_, p_282630_).endVertex();
      bufferbuilder.vertex(matrix4f, (float)p_281930_, (float)p_282113_, (float)p_283583_).color(p_282800_, p_282850_, p_282375_, p_282754_).uv(p_281676_, p_283166_).endVertex();
      BufferUploader.drawWithShader(bufferbuilder.end());
      RenderSystem.disableBlend();
   }

   public void blitNineSliced(ResourceLocation p_282546_, int p_282275_, int p_281581_, int p_283274_, int p_281626_, int p_283005_, int p_282047_, int p_282125_, int p_283423_, int p_281424_) {
      this.blitNineSliced(p_282546_, p_282275_, p_281581_, p_283274_, p_281626_, p_283005_, p_283005_, p_283005_, p_283005_, p_282047_, p_282125_, p_283423_, p_281424_);
   }

   public void blitNineSliced(ResourceLocation p_282543_, int p_281513_, int p_281865_, int p_282482_, int p_282661_, int p_282068_, int p_281294_, int p_281681_, int p_281957_, int p_282300_, int p_282769_) {
      this.blitNineSliced(p_282543_, p_281513_, p_281865_, p_282482_, p_282661_, p_282068_, p_281294_, p_282068_, p_281294_, p_281681_, p_281957_, p_282300_, p_282769_);
   }

   public void blitNineSliced(ResourceLocation p_282712_, int p_283509_, int p_283259_, int p_283273_, int p_282043_, int p_281430_, int p_281412_, int p_282566_, int p_281971_, int p_282879_, int p_281529_, int p_281924_, int p_281407_) {
      p_281430_ = Math.min(p_281430_, p_283273_ / 2);
      p_282566_ = Math.min(p_282566_, p_283273_ / 2);
      p_281412_ = Math.min(p_281412_, p_282043_ / 2);
      p_281971_ = Math.min(p_281971_, p_282043_ / 2);
      if (p_283273_ == p_282879_ && p_282043_ == p_281529_) {
         this.blit(p_282712_, p_283509_, p_283259_, p_281924_, p_281407_, p_283273_, p_282043_);
      } else if (p_282043_ == p_281529_) {
         this.blit(p_282712_, p_283509_, p_283259_, p_281924_, p_281407_, p_281430_, p_282043_);
         this.blitRepeating(p_282712_, p_283509_ + p_281430_, p_283259_, p_283273_ - p_282566_ - p_281430_, p_282043_, p_281924_ + p_281430_, p_281407_, p_282879_ - p_282566_ - p_281430_, p_281529_);
         this.blit(p_282712_, p_283509_ + p_283273_ - p_282566_, p_283259_, p_281924_ + p_282879_ - p_282566_, p_281407_, p_282566_, p_282043_);
      } else if (p_283273_ == p_282879_) {
         this.blit(p_282712_, p_283509_, p_283259_, p_281924_, p_281407_, p_283273_, p_281412_);
         this.blitRepeating(p_282712_, p_283509_, p_283259_ + p_281412_, p_283273_, p_282043_ - p_281971_ - p_281412_, p_281924_, p_281407_ + p_281412_, p_282879_, p_281529_ - p_281971_ - p_281412_);
         this.blit(p_282712_, p_283509_, p_283259_ + p_282043_ - p_281971_, p_281924_, p_281407_ + p_281529_ - p_281971_, p_283273_, p_281971_);
      } else {
         this.blit(p_282712_, p_283509_, p_283259_, p_281924_, p_281407_, p_281430_, p_281412_);
         this.blitRepeating(p_282712_, p_283509_ + p_281430_, p_283259_, p_283273_ - p_282566_ - p_281430_, p_281412_, p_281924_ + p_281430_, p_281407_, p_282879_ - p_282566_ - p_281430_, p_281412_);
         this.blit(p_282712_, p_283509_ + p_283273_ - p_282566_, p_283259_, p_281924_ + p_282879_ - p_282566_, p_281407_, p_282566_, p_281412_);
         this.blit(p_282712_, p_283509_, p_283259_ + p_282043_ - p_281971_, p_281924_, p_281407_ + p_281529_ - p_281971_, p_281430_, p_281971_);
         this.blitRepeating(p_282712_, p_283509_ + p_281430_, p_283259_ + p_282043_ - p_281971_, p_283273_ - p_282566_ - p_281430_, p_281971_, p_281924_ + p_281430_, p_281407_ + p_281529_ - p_281971_, p_282879_ - p_282566_ - p_281430_, p_281971_);
         this.blit(p_282712_, p_283509_ + p_283273_ - p_282566_, p_283259_ + p_282043_ - p_281971_, p_281924_ + p_282879_ - p_282566_, p_281407_ + p_281529_ - p_281971_, p_282566_, p_281971_);
         this.blitRepeating(p_282712_, p_283509_, p_283259_ + p_281412_, p_281430_, p_282043_ - p_281971_ - p_281412_, p_281924_, p_281407_ + p_281412_, p_281430_, p_281529_ - p_281971_ - p_281412_);
         this.blitRepeating(p_282712_, p_283509_ + p_281430_, p_283259_ + p_281412_, p_283273_ - p_282566_ - p_281430_, p_282043_ - p_281971_ - p_281412_, p_281924_ + p_281430_, p_281407_ + p_281412_, p_282879_ - p_282566_ - p_281430_, p_281529_ - p_281971_ - p_281412_);
         this.blitRepeating(p_282712_, p_283509_ + p_283273_ - p_282566_, p_283259_ + p_281412_, p_281430_, p_282043_ - p_281971_ - p_281412_, p_281924_ + p_282879_ - p_282566_, p_281407_ + p_281412_, p_282566_, p_281529_ - p_281971_ - p_281412_);
      }
   }

   public void blitRepeating(ResourceLocation p_283059_, int p_283575_, int p_283192_, int p_281790_, int p_283642_, int p_282691_, int p_281912_, int p_281728_, int p_282324_) {
      blitRepeating(p_283059_, p_283575_, p_283192_, p_281790_, p_283642_, p_282691_, p_281912_, p_281728_, p_282324_, 256, 256);
   }

   public void blitRepeating(ResourceLocation p_283059_, int p_283575_, int p_283192_, int p_281790_, int p_283642_, int p_282691_, int p_281912_, int p_281728_, int p_282324_, int textureWidth, int textureHeight) {
      int i = p_283575_;

      int j;
      for(IntIterator intiterator = slices(p_281790_, p_281728_); intiterator.hasNext(); i += j) {
         j = intiterator.nextInt();
         int k = (p_281728_ - j) / 2;
         int l = p_283192_;

         int i1;
         for(IntIterator intiterator1 = slices(p_283642_, p_282324_); intiterator1.hasNext(); l += i1) {
            i1 = intiterator1.nextInt();
            int j1 = (p_282324_ - i1) / 2;
            this.blit(p_283059_, i, l, p_282691_ + k, p_281912_ + j1, j, i1, textureWidth, textureHeight);
         }
      }

   }

   private static IntIterator slices(int p_282197_, int p_282161_) {
      int i = Mth.positiveCeilDiv(p_282197_, p_282161_);
      return new Divisor(p_282197_, i);
   }

   public void renderItem(ItemStack p_281978_, int p_282647_, int p_281944_) {
      this.renderItem(this.minecraft.player, this.minecraft.level, p_281978_, p_282647_, p_281944_, 0);
   }

   public void renderItem(ItemStack p_282262_, int p_283221_, int p_283496_, int p_283435_) {
      this.renderItem(this.minecraft.player, this.minecraft.level, p_282262_, p_283221_, p_283496_, p_283435_);
   }

   public void renderItem(ItemStack p_282786_, int p_282502_, int p_282976_, int p_281592_, int p_282314_) {
      this.renderItem(this.minecraft.player, this.minecraft.level, p_282786_, p_282502_, p_282976_, p_281592_, p_282314_);
   }

   public void renderFakeItem(ItemStack p_281946_, int p_283299_, int p_283674_) {
      this.renderItem((LivingEntity)null, this.minecraft.level, p_281946_, p_283299_, p_283674_, 0);
   }

   public void renderItem(LivingEntity p_282154_, ItemStack p_282777_, int p_282110_, int p_281371_, int p_283572_) {
      this.renderItem(p_282154_, p_282154_.level(), p_282777_, p_282110_, p_281371_, p_283572_);
   }

   private void renderItem(@Nullable LivingEntity p_283524_, @Nullable Level p_282461_, ItemStack p_283653_, int p_283141_, int p_282560_, int p_282425_) {
      this.renderItem(p_283524_, p_282461_, p_283653_, p_283141_, p_282560_, p_282425_, 0);
   }

   private void renderItem(@Nullable LivingEntity p_282619_, @Nullable Level p_281754_, ItemStack p_281675_, int p_281271_, int p_282210_, int p_283260_, int p_281995_) {
      if (!p_281675_.isEmpty()) {
         BakedModel bakedmodel = this.minecraft.getItemRenderer().getModel(p_281675_, p_281754_, p_282619_, p_283260_);
         this.pose.pushPose();
         this.pose.translate((float)(p_281271_ + 8), (float)(p_282210_ + 8), (float)(150 + (bakedmodel.isGui3d() ? p_281995_ : 0)));

         try {
            this.pose.mulPoseMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
            this.pose.scale(16.0F, 16.0F, 16.0F);
            boolean flag = !bakedmodel.usesBlockLight();
            if (flag) {
               Lighting.setupForFlatItems();
            }

            this.minecraft.getItemRenderer().render(p_281675_, ItemDisplayContext.GUI, false, this.pose, this.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
            this.flush();
            if (flag) {
               Lighting.setupFor3DItems();
            }
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
            crashreportcategory.setDetail("Item Type", () -> {
               return String.valueOf((Object)p_281675_.getItem());
            });
            crashreportcategory.setDetail("Registry Name", () -> String.valueOf(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(p_281675_.getItem())));
            crashreportcategory.setDetail("Item Damage", () -> {
               return String.valueOf(p_281675_.getDamageValue());
            });
            crashreportcategory.setDetail("Item NBT", () -> {
               return String.valueOf((Object)p_281675_.getTag());
            });
            crashreportcategory.setDetail("Item Foil", () -> {
               return String.valueOf(p_281675_.hasFoil());
            });
            throw new ReportedException(crashreport);
         }

         this.pose.popPose();
      }
   }

   public void renderItemDecorations(Font p_281721_, ItemStack p_281514_, int p_282056_, int p_282683_) {
      this.renderItemDecorations(p_281721_, p_281514_, p_282056_, p_282683_, (String)null);
   }

   public void renderItemDecorations(Font p_282005_, ItemStack p_283349_, int p_282641_, int p_282146_, @Nullable String p_282803_) {
      if (!p_283349_.isEmpty()) {
         this.pose.pushPose();
         if (p_283349_.getCount() != 1 || p_282803_ != null) {
            String s = p_282803_ == null ? String.valueOf(p_283349_.getCount()) : p_282803_;
            this.pose.translate(0.0F, 0.0F, 200.0F);
            this.drawString(p_282005_, s, p_282641_ + 19 - 2 - p_282005_.width(s), p_282146_ + 6 + 3, 16777215, true);
         }

         if (p_283349_.isBarVisible()) {
            int l = p_283349_.getBarWidth();
            int i = p_283349_.getBarColor();
            int j = p_282641_ + 2;
            int k = p_282146_ + 13;
            this.fill(RenderType.guiOverlay(), j, k, j + 13, k + 2, -16777216);
            this.fill(RenderType.guiOverlay(), j, k, j + l, k + 1, i | -16777216);
         }

         LocalPlayer localplayer = this.minecraft.player;
         float f = localplayer == null ? 0.0F : localplayer.getCooldowns().getCooldownPercent(p_283349_.getItem(), this.minecraft.getFrameTime());
         if (f > 0.0F) {
            int i1 = p_282146_ + Mth.floor(16.0F * (1.0F - f));
            int j1 = i1 + Mth.ceil(16.0F * f);
            this.fill(RenderType.guiOverlay(), p_282641_, i1, p_282641_ + 16, j1, Integer.MAX_VALUE);
         }

         this.pose.popPose();
         net.minecraftforge.client.ItemDecoratorHandler.of(p_283349_).render(this, p_282005_, p_283349_, p_282641_, p_282146_);
      }
   }

   private ItemStack tooltipStack = ItemStack.EMPTY;

   public void renderTooltip(Font p_282308_, ItemStack p_282781_, int p_282687_, int p_282292_) {
      this.tooltipStack = p_282781_;
      this.renderTooltip(p_282308_, Screen.getTooltipFromItem(this.minecraft, p_282781_), p_282781_.getTooltipImage(), p_282687_, p_282292_);
      this.tooltipStack = ItemStack.EMPTY;
   }

   public void renderTooltip(Font font, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, ItemStack stack, int mouseX, int mouseY) {
      this.tooltipStack = stack;
      this.renderTooltip(font, textComponents, tooltipComponent, mouseX, mouseY);
      this.tooltipStack = ItemStack.EMPTY;
   }

   public void renderTooltip(Font p_283128_, List<Component> p_282716_, Optional<TooltipComponent> p_281682_, int p_283678_, int p_281696_) {
      List<ClientTooltipComponent> list = net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(this.tooltipStack, p_282716_, p_281682_, p_283678_, guiWidth(), guiHeight(), p_283128_);
      this.renderTooltipInternal(p_283128_, list, p_283678_, p_281696_, DefaultTooltipPositioner.INSTANCE);
   }

   public void renderTooltip(Font p_282269_, Component p_282572_, int p_282044_, int p_282545_) {
      this.renderTooltip(p_282269_, List.of(p_282572_.getVisualOrderText()), p_282044_, p_282545_);
   }

   public void renderComponentTooltip(Font p_282739_, List<Component> p_281832_, int p_282191_, int p_282446_) {
      List<ClientTooltipComponent> components = net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(this.tooltipStack, p_281832_, p_282191_, guiWidth(), guiHeight(), p_282739_);
      this.renderTooltipInternal(p_282739_, components, p_282191_, p_282446_, DefaultTooltipPositioner.INSTANCE);
   }

   public void renderComponentTooltip(Font font, List<? extends net.minecraft.network.chat.FormattedText> tooltips, int mouseX, int mouseY, ItemStack stack) {
      this.tooltipStack = stack;
      List<ClientTooltipComponent> components = net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(stack, tooltips, mouseX, guiWidth(), guiHeight(), font);
      this.renderTooltipInternal(font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE);
      this.tooltipStack = ItemStack.EMPTY;
   }

   public void renderTooltip(Font p_282192_, List<? extends FormattedCharSequence> p_282297_, int p_281680_, int p_283325_) {
      this.renderTooltipInternal(p_282192_, p_282297_.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), p_281680_, p_283325_, DefaultTooltipPositioner.INSTANCE);
   }

   public void renderTooltip(Font p_281627_, List<FormattedCharSequence> p_283313_, ClientTooltipPositioner p_283571_, int p_282367_, int p_282806_) {
      this.renderTooltipInternal(p_281627_, p_283313_.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), p_282367_, p_282806_, p_283571_);
   }

   private void renderTooltipInternal(Font p_282675_, List<ClientTooltipComponent> p_282615_, int p_283230_, int p_283417_, ClientTooltipPositioner p_282442_) {
      if (!p_282615_.isEmpty()) {
         net.minecraftforge.client.event.RenderTooltipEvent.Pre preEvent = net.minecraftforge.client.ForgeHooksClient.onRenderTooltipPre(this.tooltipStack, this, p_283230_, p_283417_, guiWidth(), guiHeight(), p_282615_, p_282675_, p_282442_);
         if (preEvent.isCanceled()) return;
         int i = 0;
         int j = p_282615_.size() == 1 ? -2 : 0;

         for(ClientTooltipComponent clienttooltipcomponent : p_282615_) {
            int k = clienttooltipcomponent.getWidth(preEvent.getFont());
            if (k > i) {
               i = k;
            }

            j += clienttooltipcomponent.getHeight();
         }

         int i2 = i;
         int j2 = j;
         Vector2ic vector2ic = p_282442_.positionTooltip(this.guiWidth(), this.guiHeight(), preEvent.getX(), preEvent.getY(), i2, j2);
         int l = vector2ic.x();
         int i1 = vector2ic.y();
         this.pose.pushPose();
         int j1 = 400;
         this.drawManaged(() -> {
            net.minecraftforge.client.event.RenderTooltipEvent.Color colorEvent = net.minecraftforge.client.ForgeHooksClient.onRenderTooltipColor(this.tooltipStack, this, l, i1, preEvent.getFont(), p_282615_);
            TooltipRenderUtil.renderTooltipBackground(this, l, i1, i2, j2, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), colorEvent.getBorderStart(), colorEvent.getBorderEnd());
         });
         this.pose.translate(0.0F, 0.0F, 400.0F);
         int k1 = i1;

         for(int l1 = 0; l1 < p_282615_.size(); ++l1) {
            ClientTooltipComponent clienttooltipcomponent1 = p_282615_.get(l1);
            clienttooltipcomponent1.renderText(preEvent.getFont(), l, k1, this.pose.last().pose(), this.bufferSource);
            k1 += clienttooltipcomponent1.getHeight() + (l1 == 0 ? 2 : 0);
         }

         k1 = i1;

         for(int k2 = 0; k2 < p_282615_.size(); ++k2) {
            ClientTooltipComponent clienttooltipcomponent2 = p_282615_.get(k2);
            clienttooltipcomponent2.renderImage(preEvent.getFont(), l, k1, this);
            k1 += clienttooltipcomponent2.getHeight() + (k2 == 0 ? 2 : 0);
         }

         this.pose.popPose();
      }
   }

   public void renderComponentHoverEffect(Font p_282584_, @Nullable Style p_282156_, int p_283623_, int p_282114_) {
      if (p_282156_ != null && p_282156_.getHoverEvent() != null) {
         HoverEvent hoverevent = p_282156_.getHoverEvent();
         HoverEvent.ItemStackInfo hoverevent$itemstackinfo = hoverevent.getValue(HoverEvent.Action.SHOW_ITEM);
         if (hoverevent$itemstackinfo != null) {
            this.renderTooltip(p_282584_, hoverevent$itemstackinfo.getItemStack(), p_283623_, p_282114_);
         } else {
            HoverEvent.EntityTooltipInfo hoverevent$entitytooltipinfo = hoverevent.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (hoverevent$entitytooltipinfo != null) {
               if (this.minecraft.options.advancedItemTooltips) {
                  this.renderComponentTooltip(p_282584_, hoverevent$entitytooltipinfo.getTooltipLines(), p_283623_, p_282114_);
               }
            } else {
               Component component = hoverevent.getValue(HoverEvent.Action.SHOW_TEXT);
               if (component != null) {
                  this.renderTooltip(p_282584_, p_282584_.split(component, Math.max(this.guiWidth() / 2, 200)), p_283623_, p_282114_);
               }
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ScissorStack {
      private final Deque<ScreenRectangle> stack = new ArrayDeque<>();

      public ScreenRectangle push(ScreenRectangle p_281812_) {
         ScreenRectangle screenrectangle = this.stack.peekLast();
         if (screenrectangle != null) {
            ScreenRectangle screenrectangle1 = Objects.requireNonNullElse(p_281812_.intersection(screenrectangle), ScreenRectangle.empty());
            this.stack.addLast(screenrectangle1);
            return screenrectangle1;
         } else {
            this.stack.addLast(p_281812_);
            return p_281812_;
         }
      }

      public @Nullable ScreenRectangle pop() {
         if (this.stack.isEmpty()) {
            throw new IllegalStateException("Scissor stack underflow");
         } else {
            this.stack.removeLast();
            return this.stack.peekLast();
         }
      }
   }
}
