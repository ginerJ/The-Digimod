package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MerchantScreen extends AbstractContainerScreen<MerchantMenu> {
   private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
   private static final int TEXTURE_WIDTH = 512;
   private static final int TEXTURE_HEIGHT = 256;
   private static final int MERCHANT_MENU_PART_X = 99;
   private static final int PROGRESS_BAR_X = 136;
   private static final int PROGRESS_BAR_Y = 16;
   private static final int SELL_ITEM_1_X = 5;
   private static final int SELL_ITEM_2_X = 35;
   private static final int BUY_ITEM_X = 68;
   private static final int LABEL_Y = 6;
   private static final int NUMBER_OF_OFFER_BUTTONS = 7;
   private static final int TRADE_BUTTON_X = 5;
   private static final int TRADE_BUTTON_HEIGHT = 20;
   private static final int TRADE_BUTTON_WIDTH = 88;
   private static final int SCROLLER_HEIGHT = 27;
   private static final int SCROLLER_WIDTH = 6;
   private static final int SCROLL_BAR_HEIGHT = 139;
   private static final int SCROLL_BAR_TOP_POS_Y = 18;
   private static final int SCROLL_BAR_START_X = 94;
   private static final Component TRADES_LABEL = Component.translatable("merchant.trades");
   private static final Component LEVEL_SEPARATOR = Component.literal(" - ");
   private static final Component DEPRECATED_TOOLTIP = Component.translatable("merchant.deprecated");
   private int shopItem;
   private final MerchantScreen.TradeOfferButton[] tradeOfferButtons = new MerchantScreen.TradeOfferButton[7];
   int scrollOff;
   private boolean isDragging;

   public MerchantScreen(MerchantMenu p_99123_, Inventory p_99124_, Component p_99125_) {
      super(p_99123_, p_99124_, p_99125_);
      this.imageWidth = 276;
      this.inventoryLabelX = 107;
   }

   private void postButtonClick() {
      this.menu.setSelectionHint(this.shopItem);
      this.menu.tryMoveItems(this.shopItem);
      this.minecraft.getConnection().send(new ServerboundSelectTradePacket(this.shopItem));
   }

   protected void init() {
      super.init();
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      int k = j + 16 + 2;

      for(int l = 0; l < 7; ++l) {
         this.tradeOfferButtons[l] = this.addRenderableWidget(new MerchantScreen.TradeOfferButton(i + 5, k, l, (p_99174_) -> {
            if (p_99174_ instanceof MerchantScreen.TradeOfferButton) {
               this.shopItem = ((MerchantScreen.TradeOfferButton)p_99174_).getIndex() + this.scrollOff;
               this.postButtonClick();
            }

         }));
         k += 20;
      }

   }

   protected void renderLabels(GuiGraphics p_283337_, int p_282009_, int p_283691_) {
      int i = this.menu.getTraderLevel();
      if (i > 0 && i <= 5 && this.menu.showProgressBar()) {
         Component component = this.title.copy().append(LEVEL_SEPARATOR).append(Component.translatable("merchant.level." + i));
         int j = this.font.width(component);
         int k = 49 + this.imageWidth / 2 - j / 2;
         p_283337_.drawString(this.font, component, k, 6, 4210752, false);
      } else {
         p_283337_.drawString(this.font, this.title, 49 + this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 4210752, false);
      }

      p_283337_.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
      int l = this.font.width(TRADES_LABEL);
      p_283337_.drawString(this.font, TRADES_LABEL, 5 - l / 2 + 48, 6, 4210752, false);
   }

   protected void renderBg(GuiGraphics p_283072_, float p_281275_, int p_282312_, int p_282984_) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      p_283072_.blit(VILLAGER_LOCATION, i, j, 0, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
      MerchantOffers merchantoffers = this.menu.getOffers();
      if (!merchantoffers.isEmpty()) {
         int k = this.shopItem;
         if (k < 0 || k >= merchantoffers.size()) {
            return;
         }

         MerchantOffer merchantoffer = merchantoffers.get(k);
         if (merchantoffer.isOutOfStock()) {
            p_283072_.blit(VILLAGER_LOCATION, this.leftPos + 83 + 99, this.topPos + 35, 0, 311.0F, 0.0F, 28, 21, 512, 256);
         }
      }

   }

   private void renderProgressBar(GuiGraphics p_281426_, int p_283008_, int p_283085_, MerchantOffer p_282094_) {
      int i = this.menu.getTraderLevel();
      int j = this.menu.getTraderXp();
      if (i < 5) {
         p_281426_.blit(VILLAGER_LOCATION, p_283008_ + 136, p_283085_ + 16, 0, 0.0F, 186.0F, 102, 5, 512, 256);
         int k = VillagerData.getMinXpPerLevel(i);
         if (j >= k && VillagerData.canLevelUp(i)) {
            int l = 100;
            float f = 100.0F / (float)(VillagerData.getMaxXpPerLevel(i) - k);
            int i1 = Math.min(Mth.floor(f * (float)(j - k)), 100);
            p_281426_.blit(VILLAGER_LOCATION, p_283008_ + 136, p_283085_ + 16, 0, 0.0F, 191.0F, i1 + 1, 5, 512, 256);
            int j1 = this.menu.getFutureTraderXp();
            if (j1 > 0) {
               int k1 = Math.min(Mth.floor((float)j1 * f), 100 - i1);
               p_281426_.blit(VILLAGER_LOCATION, p_283008_ + 136 + i1 + 1, p_283085_ + 16 + 1, 0, 2.0F, 182.0F, k1, 3, 512, 256);
            }

         }
      }
   }

   private void renderScroller(GuiGraphics p_283030_, int p_283154_, int p_281664_, MerchantOffers p_282877_) {
      int i = p_282877_.size() + 1 - 7;
      if (i > 1) {
         int j = 139 - (27 + (i - 1) * 139 / i);
         int k = 1 + j / i + 139 / i;
         int l = 113;
         int i1 = Math.min(113, this.scrollOff * k);
         if (this.scrollOff == i - 1) {
            i1 = 113;
         }

         p_283030_.blit(VILLAGER_LOCATION, p_283154_ + 94, p_281664_ + 18 + i1, 0, 0.0F, 199.0F, 6, 27, 512, 256);
      } else {
         p_283030_.blit(VILLAGER_LOCATION, p_283154_ + 94, p_281664_ + 18, 0, 6.0F, 199.0F, 6, 27, 512, 256);
      }

   }

   public void render(GuiGraphics p_283487_, int p_281994_, int p_282099_, float p_281815_) {
      this.renderBackground(p_283487_);
      super.render(p_283487_, p_281994_, p_282099_, p_281815_);
      MerchantOffers merchantoffers = this.menu.getOffers();
      if (!merchantoffers.isEmpty()) {
         int i = (this.width - this.imageWidth) / 2;
         int j = (this.height - this.imageHeight) / 2;
         int k = j + 16 + 1;
         int l = i + 5 + 5;
         this.renderScroller(p_283487_, i, j, merchantoffers);
         int i1 = 0;

         for(MerchantOffer merchantoffer : merchantoffers) {
            if (!this.canScroll(merchantoffers.size()) || i1 >= this.scrollOff && i1 < 7 + this.scrollOff) {
               ItemStack itemstack = merchantoffer.getBaseCostA();
               ItemStack itemstack1 = merchantoffer.getCostA();
               ItemStack itemstack2 = merchantoffer.getCostB();
               ItemStack itemstack3 = merchantoffer.getResult();
               p_283487_.pose().pushPose();
               p_283487_.pose().translate(0.0F, 0.0F, 100.0F);
               int j1 = k + 2;
               this.renderAndDecorateCostA(p_283487_, itemstack1, itemstack, l, j1);
               if (!itemstack2.isEmpty()) {
                  p_283487_.renderFakeItem(itemstack2, i + 5 + 35, j1);
                  p_283487_.renderItemDecorations(this.font, itemstack2, i + 5 + 35, j1);
               }

               this.renderButtonArrows(p_283487_, merchantoffer, i, j1);
               p_283487_.renderFakeItem(itemstack3, i + 5 + 68, j1);
               p_283487_.renderItemDecorations(this.font, itemstack3, i + 5 + 68, j1);
               p_283487_.pose().popPose();
               k += 20;
               ++i1;
            } else {
               ++i1;
            }
         }

         int k1 = this.shopItem;
         MerchantOffer merchantoffer1 = merchantoffers.get(k1);
         if (this.menu.showProgressBar()) {
            this.renderProgressBar(p_283487_, i, j, merchantoffer1);
         }

         if (merchantoffer1.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double)p_281994_, (double)p_282099_) && this.menu.canRestock()) {
            p_283487_.renderTooltip(this.font, DEPRECATED_TOOLTIP, p_281994_, p_282099_);
         }

         for(MerchantScreen.TradeOfferButton merchantscreen$tradeofferbutton : this.tradeOfferButtons) {
            if (merchantscreen$tradeofferbutton.isHoveredOrFocused()) {
               merchantscreen$tradeofferbutton.renderToolTip(p_283487_, p_281994_, p_282099_);
            }

            merchantscreen$tradeofferbutton.visible = merchantscreen$tradeofferbutton.index < this.menu.getOffers().size();
         }

         RenderSystem.enableDepthTest();
      }

      this.renderTooltip(p_283487_, p_281994_, p_282099_);
   }

   private void renderButtonArrows(GuiGraphics p_283020_, MerchantOffer p_281926_, int p_282752_, int p_282179_) {
      RenderSystem.enableBlend();
      if (p_281926_.isOutOfStock()) {
         p_283020_.blit(VILLAGER_LOCATION, p_282752_ + 5 + 35 + 20, p_282179_ + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
      } else {
         p_283020_.blit(VILLAGER_LOCATION, p_282752_ + 5 + 35 + 20, p_282179_ + 3, 0, 15.0F, 171.0F, 10, 9, 512, 256);
      }

   }

   private void renderAndDecorateCostA(GuiGraphics p_281357_, ItemStack p_283466_, ItemStack p_282046_, int p_282403_, int p_283601_) {
      p_281357_.renderFakeItem(p_283466_, p_282403_, p_283601_);
      if (p_282046_.getCount() == p_283466_.getCount()) {
         p_281357_.renderItemDecorations(this.font, p_283466_, p_282403_, p_283601_);
      } else {
         p_281357_.renderItemDecorations(this.font, p_282046_, p_282403_, p_283601_, p_282046_.getCount() == 1 ? "1" : null);
         // Forge: fixes Forge-8806, code for count rendering taken from GuiGraphics#renderGuiItemDecorations
         p_281357_.pose().pushPose();
         p_281357_.pose().translate(0.0F, 0.0F, 200.0F);
         String count = p_283466_.getCount() == 1 ? "1" : String.valueOf(p_283466_.getCount());
         font.drawInBatch(count, (float) (p_282403_ + 14) + 19 - 2 - font.width(count), (float)p_283601_ + 6 + 3, 0xFFFFFF, true, p_281357_.pose().last().pose(), p_281357_.bufferSource(), net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, 15728880, false);
         p_281357_.pose().popPose();
         p_281357_.pose().pushPose();
         p_281357_.pose().translate(0.0F, 0.0F, 300.0F);
         p_281357_.blit(VILLAGER_LOCATION, p_282403_ + 7, p_283601_ + 12, 0, 0.0F, 176.0F, 9, 2, 512, 256);
         p_281357_.pose().popPose();
      }

   }

   private boolean canScroll(int p_99141_) {
      return p_99141_ > 7;
   }

   public boolean mouseScrolled(double p_99127_, double p_99128_, double p_99129_) {
      int i = this.menu.getOffers().size();
      if (this.canScroll(i)) {
         int j = i - 7;
         this.scrollOff = Mth.clamp((int)((double)this.scrollOff - p_99129_), 0, j);
      }

      return true;
   }

   public boolean mouseDragged(double p_99135_, double p_99136_, int p_99137_, double p_99138_, double p_99139_) {
      int i = this.menu.getOffers().size();
      if (this.isDragging) {
         int j = this.topPos + 18;
         int k = j + 139;
         int l = i - 7;
         float f = ((float)p_99136_ - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
         f = f * (float)l + 0.5F;
         this.scrollOff = Mth.clamp((int)f, 0, l);
         return true;
      } else {
         return super.mouseDragged(p_99135_, p_99136_, p_99137_, p_99138_, p_99139_);
      }
   }

   public boolean mouseClicked(double p_99131_, double p_99132_, int p_99133_) {
      this.isDragging = false;
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      if (this.canScroll(this.menu.getOffers().size()) && p_99131_ > (double)(i + 94) && p_99131_ < (double)(i + 94 + 6) && p_99132_ > (double)(j + 18) && p_99132_ <= (double)(j + 18 + 139 + 1)) {
         this.isDragging = true;
      }

      return super.mouseClicked(p_99131_, p_99132_, p_99133_);
   }

   @OnlyIn(Dist.CLIENT)
   class TradeOfferButton extends Button {
      final int index;

      public TradeOfferButton(int p_99205_, int p_99206_, int p_99207_, Button.OnPress p_99208_) {
         super(p_99205_, p_99206_, 88, 20, CommonComponents.EMPTY, p_99208_, DEFAULT_NARRATION);
         this.index = p_99207_;
         this.visible = false;
      }

      public int getIndex() {
         return this.index;
      }

      public void renderToolTip(GuiGraphics p_281313_, int p_283342_, int p_283060_) {
         if (this.isHovered && MerchantScreen.this.menu.getOffers().size() > this.index + MerchantScreen.this.scrollOff) {
            if (p_283342_ < this.getX() + 20) {
               ItemStack itemstack = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getCostA();
               p_281313_.renderTooltip(MerchantScreen.this.font, itemstack, p_283342_, p_283060_);
            } else if (p_283342_ < this.getX() + 50 && p_283342_ > this.getX() + 30) {
               ItemStack itemstack2 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getCostB();
               if (!itemstack2.isEmpty()) {
                  p_281313_.renderTooltip(MerchantScreen.this.font, itemstack2, p_283342_, p_283060_);
               }
            } else if (p_283342_ > this.getX() + 65) {
               ItemStack itemstack1 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getResult();
               p_281313_.renderTooltip(MerchantScreen.this.font, itemstack1, p_283342_, p_283060_);
            }
         }

      }
   }
}
