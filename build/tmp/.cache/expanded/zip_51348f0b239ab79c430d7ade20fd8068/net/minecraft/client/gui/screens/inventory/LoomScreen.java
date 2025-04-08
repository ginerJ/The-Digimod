package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LoomScreen extends AbstractContainerScreen<LoomMenu> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/loom.png");
   private static final int PATTERN_COLUMNS = 4;
   private static final int PATTERN_ROWS = 4;
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   private static final int PATTERN_IMAGE_SIZE = 14;
   private static final int SCROLLER_FULL_HEIGHT = 56;
   private static final int PATTERNS_X = 60;
   private static final int PATTERNS_Y = 13;
   private ModelPart flag;
   @Nullable
   private List<Pair<Holder<BannerPattern>, DyeColor>> resultBannerPatterns;
   private ItemStack bannerStack = ItemStack.EMPTY;
   private ItemStack dyeStack = ItemStack.EMPTY;
   private ItemStack patternStack = ItemStack.EMPTY;
   private boolean displayPatterns;
   private boolean hasMaxPatterns;
   private float scrollOffs;
   private boolean scrolling;
   private int startRow;

   public LoomScreen(LoomMenu p_99075_, Inventory p_99076_, Component p_99077_) {
      super(p_99075_, p_99076_, p_99077_);
      p_99075_.registerUpdateListener(this::containerChanged);
      this.titleLabelY -= 2;
   }

   protected void init() {
      super.init();
      this.flag = this.minecraft.getEntityModels().bakeLayer(ModelLayers.BANNER).getChild("flag");
   }

   public void render(GuiGraphics p_283513_, int p_282700_, int p_282637_, float p_281433_) {
      super.render(p_283513_, p_282700_, p_282637_, p_281433_);
      this.renderTooltip(p_283513_, p_282700_, p_282637_);
   }

   private int totalRowCount() {
      return Mth.positiveCeilDiv(this.menu.getSelectablePatterns().size(), 4);
   }

   protected void renderBg(GuiGraphics p_282870_, float p_281777_, int p_283331_, int p_283087_) {
      this.renderBackground(p_282870_);
      int i = this.leftPos;
      int j = this.topPos;
      p_282870_.blit(BG_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
      Slot slot = this.menu.getBannerSlot();
      Slot slot1 = this.menu.getDyeSlot();
      Slot slot2 = this.menu.getPatternSlot();
      Slot slot3 = this.menu.getResultSlot();
      if (!slot.hasItem()) {
         p_282870_.blit(BG_LOCATION, i + slot.x, j + slot.y, this.imageWidth, 0, 16, 16);
      }

      if (!slot1.hasItem()) {
         p_282870_.blit(BG_LOCATION, i + slot1.x, j + slot1.y, this.imageWidth + 16, 0, 16, 16);
      }

      if (!slot2.hasItem()) {
         p_282870_.blit(BG_LOCATION, i + slot2.x, j + slot2.y, this.imageWidth + 32, 0, 16, 16);
      }

      int k = (int)(41.0F * this.scrollOffs);
      p_282870_.blit(BG_LOCATION, i + 119, j + 13 + k, 232 + (this.displayPatterns ? 0 : 12), 0, 12, 15);
      Lighting.setupForFlatItems();
      if (this.resultBannerPatterns != null && !this.hasMaxPatterns) {
         p_282870_.pose().pushPose();
         p_282870_.pose().translate((float)(i + 139), (float)(j + 52), 0.0F);
         p_282870_.pose().scale(24.0F, -24.0F, 1.0F);
         p_282870_.pose().translate(0.5F, 0.5F, 0.5F);
         float f = 0.6666667F;
         p_282870_.pose().scale(0.6666667F, -0.6666667F, -0.6666667F);
         this.flag.xRot = 0.0F;
         this.flag.y = -32.0F;
         BannerRenderer.renderPatterns(p_282870_.pose(), p_282870_.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, this.resultBannerPatterns);
         p_282870_.pose().popPose();
         p_282870_.flush();
      } else if (this.hasMaxPatterns) {
         p_282870_.blit(BG_LOCATION, i + slot3.x - 2, j + slot3.y - 2, this.imageWidth, 17, 17, 16);
      }

      if (this.displayPatterns) {
         int l2 = i + 60;
         int l = j + 13;
         List<Holder<BannerPattern>> list = this.menu.getSelectablePatterns();

         label64:
         for(int i1 = 0; i1 < 4; ++i1) {
            for(int j1 = 0; j1 < 4; ++j1) {
               int k1 = i1 + this.startRow;
               int l1 = k1 * 4 + j1;
               if (l1 >= list.size()) {
                  break label64;
               }

               int i2 = l2 + j1 * 14;
               int j2 = l + i1 * 14;
               boolean flag = p_283331_ >= i2 && p_283087_ >= j2 && p_283331_ < i2 + 14 && p_283087_ < j2 + 14;
               int k2;
               if (l1 == this.menu.getSelectedBannerPatternIndex()) {
                  k2 = this.imageHeight + 14;
               } else if (flag) {
                  k2 = this.imageHeight + 28;
               } else {
                  k2 = this.imageHeight;
               }

               p_282870_.blit(BG_LOCATION, i2, j2, 0, k2, 14, 14);
               this.renderPattern(p_282870_, list.get(l1), i2, j2);
            }
         }
      }

      Lighting.setupFor3DItems();
   }

   private void renderPattern(GuiGraphics p_282452_, Holder<BannerPattern> p_281940_, int p_281872_, int p_282995_) {
      CompoundTag compoundtag = new CompoundTag();
      ListTag listtag = (new BannerPattern.Builder()).addPattern(BannerPatterns.BASE, DyeColor.GRAY).addPattern(p_281940_, DyeColor.WHITE).toListTag();
      compoundtag.put("Patterns", listtag);
      ItemStack itemstack = new ItemStack(Items.GRAY_BANNER);
      BlockItem.setBlockEntityData(itemstack, BlockEntityType.BANNER, compoundtag);
      PoseStack posestack = new PoseStack();
      posestack.pushPose();
      posestack.translate((float)p_281872_ + 0.5F, (float)(p_282995_ + 16), 0.0F);
      posestack.scale(6.0F, -6.0F, 1.0F);
      posestack.translate(0.5F, 0.5F, 0.0F);
      posestack.translate(0.5F, 0.5F, 0.5F);
      float f = 0.6666667F;
      posestack.scale(0.6666667F, -0.6666667F, -0.6666667F);
      this.flag.xRot = 0.0F;
      this.flag.y = -32.0F;
      List<Pair<Holder<BannerPattern>, DyeColor>> list = BannerBlockEntity.createPatterns(DyeColor.GRAY, BannerBlockEntity.getItemPatterns(itemstack));
      BannerRenderer.renderPatterns(posestack, p_282452_.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, list);
      posestack.popPose();
      p_282452_.flush();
   }

   public boolean mouseClicked(double p_99083_, double p_99084_, int p_99085_) {
      this.scrolling = false;
      if (this.displayPatterns) {
         int i = this.leftPos + 60;
         int j = this.topPos + 13;

         for(int k = 0; k < 4; ++k) {
            for(int l = 0; l < 4; ++l) {
               double d0 = p_99083_ - (double)(i + l * 14);
               double d1 = p_99084_ - (double)(j + k * 14);
               int i1 = k + this.startRow;
               int j1 = i1 * 4 + l;
               if (d0 >= 0.0D && d1 >= 0.0D && d0 < 14.0D && d1 < 14.0D && this.menu.clickMenuButton(this.minecraft.player, j1)) {
                  Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
                  this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, j1);
                  return true;
               }
            }
         }

         i = this.leftPos + 119;
         j = this.topPos + 9;
         if (p_99083_ >= (double)i && p_99083_ < (double)(i + 12) && p_99084_ >= (double)j && p_99084_ < (double)(j + 56)) {
            this.scrolling = true;
         }
      }

      return super.mouseClicked(p_99083_, p_99084_, p_99085_);
   }

   public boolean mouseDragged(double p_99087_, double p_99088_, int p_99089_, double p_99090_, double p_99091_) {
      int i = this.totalRowCount() - 4;
      if (this.scrolling && this.displayPatterns && i > 0) {
         int j = this.topPos + 13;
         int k = j + 56;
         this.scrollOffs = ((float)p_99088_ - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startRow = Math.max((int)((double)(this.scrollOffs * (float)i) + 0.5D), 0);
         return true;
      } else {
         return super.mouseDragged(p_99087_, p_99088_, p_99089_, p_99090_, p_99091_);
      }
   }

   public boolean mouseScrolled(double p_99079_, double p_99080_, double p_99081_) {
      int i = this.totalRowCount() - 4;
      if (this.displayPatterns && i > 0) {
         float f = (float)p_99081_ / (float)i;
         this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
         this.startRow = Math.max((int)(this.scrollOffs * (float)i + 0.5F), 0);
      }

      return true;
   }

   protected boolean hasClickedOutside(double p_99093_, double p_99094_, int p_99095_, int p_99096_, int p_99097_) {
      return p_99093_ < (double)p_99095_ || p_99094_ < (double)p_99096_ || p_99093_ >= (double)(p_99095_ + this.imageWidth) || p_99094_ >= (double)(p_99096_ + this.imageHeight);
   }

   private void containerChanged() {
      ItemStack itemstack = this.menu.getResultSlot().getItem();
      if (itemstack.isEmpty()) {
         this.resultBannerPatterns = null;
      } else {
         this.resultBannerPatterns = BannerBlockEntity.createPatterns(((BannerItem)itemstack.getItem()).getColor(), BannerBlockEntity.getItemPatterns(itemstack));
      }

      ItemStack itemstack1 = this.menu.getBannerSlot().getItem();
      ItemStack itemstack2 = this.menu.getDyeSlot().getItem();
      ItemStack itemstack3 = this.menu.getPatternSlot().getItem();
      CompoundTag compoundtag = BlockItem.getBlockEntityData(itemstack1);
      this.hasMaxPatterns = compoundtag != null && compoundtag.contains("Patterns", 9) && !itemstack1.isEmpty() && compoundtag.getList("Patterns", 10).size() >= 6;
      if (this.hasMaxPatterns) {
         this.resultBannerPatterns = null;
      }

      if (!ItemStack.matches(itemstack1, this.bannerStack) || !ItemStack.matches(itemstack2, this.dyeStack) || !ItemStack.matches(itemstack3, this.patternStack)) {
         this.displayPatterns = !itemstack1.isEmpty() && !itemstack2.isEmpty() && !this.hasMaxPatterns && !this.menu.getSelectablePatterns().isEmpty();
      }

      if (this.startRow >= this.totalRowCount()) {
         this.startRow = 0;
         this.scrollOffs = 0.0F;
      }

      this.bannerStack = itemstack1.copy();
      this.dyeStack = itemstack2.copy();
      this.patternStack = itemstack3.copy();
   }
}