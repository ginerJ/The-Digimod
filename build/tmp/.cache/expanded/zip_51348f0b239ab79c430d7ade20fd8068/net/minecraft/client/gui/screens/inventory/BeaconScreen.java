package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeaconScreen extends AbstractContainerScreen<BeaconMenu> {
   static final ResourceLocation BEACON_LOCATION = new ResourceLocation("textures/gui/container/beacon.png");
   private static final Component PRIMARY_EFFECT_LABEL = Component.translatable("block.minecraft.beacon.primary");
   private static final Component SECONDARY_EFFECT_LABEL = Component.translatable("block.minecraft.beacon.secondary");
   private final List<BeaconScreen.BeaconButton> beaconButtons = Lists.newArrayList();
   @Nullable
   MobEffect primary;
   @Nullable
   MobEffect secondary;

   public BeaconScreen(final BeaconMenu p_97912_, Inventory p_97913_, Component p_97914_) {
      super(p_97912_, p_97913_, p_97914_);
      this.imageWidth = 230;
      this.imageHeight = 219;
      p_97912_.addSlotListener(new ContainerListener() {
         public void slotChanged(AbstractContainerMenu p_97973_, int p_97974_, ItemStack p_97975_) {
         }

         public void dataChanged(AbstractContainerMenu p_169628_, int p_169629_, int p_169630_) {
            BeaconScreen.this.primary = p_97912_.getPrimaryEffect();
            BeaconScreen.this.secondary = p_97912_.getSecondaryEffect();
         }
      });
   }

   private <T extends AbstractWidget & BeaconScreen.BeaconButton> void addBeaconButton(T p_169617_) {
      this.addRenderableWidget(p_169617_);
      this.beaconButtons.add(p_169617_);
   }

   protected void init() {
      super.init();
      this.beaconButtons.clear();
      this.addBeaconButton(new BeaconScreen.BeaconConfirmButton(this.leftPos + 164, this.topPos + 107));
      this.addBeaconButton(new BeaconScreen.BeaconCancelButton(this.leftPos + 190, this.topPos + 107));

      for(int i = 0; i <= 2; ++i) {
         int j = BeaconBlockEntity.BEACON_EFFECTS[i].length;
         int k = j * 22 + (j - 1) * 2;

         for(int l = 0; l < j; ++l) {
            MobEffect mobeffect = BeaconBlockEntity.BEACON_EFFECTS[i][l];
            BeaconScreen.BeaconPowerButton beaconscreen$beaconpowerbutton = new BeaconScreen.BeaconPowerButton(this.leftPos + 76 + l * 24 - k / 2, this.topPos + 22 + i * 25, mobeffect, true, i);
            beaconscreen$beaconpowerbutton.active = false;
            this.addBeaconButton(beaconscreen$beaconpowerbutton);
         }
      }

      int i1 = 3;
      int j1 = BeaconBlockEntity.BEACON_EFFECTS[3].length + 1;
      int k1 = j1 * 22 + (j1 - 1) * 2;

      for(int l1 = 0; l1 < j1 - 1; ++l1) {
         MobEffect mobeffect1 = BeaconBlockEntity.BEACON_EFFECTS[3][l1];
         BeaconScreen.BeaconPowerButton beaconscreen$beaconpowerbutton2 = new BeaconScreen.BeaconPowerButton(this.leftPos + 167 + l1 * 24 - k1 / 2, this.topPos + 47, mobeffect1, false, 3);
         beaconscreen$beaconpowerbutton2.active = false;
         this.addBeaconButton(beaconscreen$beaconpowerbutton2);
      }

      BeaconScreen.BeaconPowerButton beaconscreen$beaconpowerbutton1 = new BeaconScreen.BeaconUpgradePowerButton(this.leftPos + 167 + (j1 - 1) * 24 - k1 / 2, this.topPos + 47, BeaconBlockEntity.BEACON_EFFECTS[0][0]);
      beaconscreen$beaconpowerbutton1.visible = false;
      this.addBeaconButton(beaconscreen$beaconpowerbutton1);
   }

   public void containerTick() {
      super.containerTick();
      this.updateButtons();
   }

   void updateButtons() {
      int i = this.menu.getLevels();
      this.beaconButtons.forEach((p_169615_) -> {
         p_169615_.updateStatus(i);
      });
   }

   protected void renderLabels(GuiGraphics p_283369_, int p_282699_, int p_281296_) {
      p_283369_.drawCenteredString(this.font, PRIMARY_EFFECT_LABEL, 62, 10, 14737632);
      p_283369_.drawCenteredString(this.font, SECONDARY_EFFECT_LABEL, 169, 10, 14737632);
   }

   protected void renderBg(GuiGraphics p_282454_, float p_282185_, int p_282362_, int p_282987_) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      p_282454_.blit(BEACON_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
      p_282454_.pose().pushPose();
      p_282454_.pose().translate(0.0F, 0.0F, 100.0F);
      p_282454_.renderItem(new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 109);
      p_282454_.renderItem(new ItemStack(Items.EMERALD), i + 41, j + 109);
      p_282454_.renderItem(new ItemStack(Items.DIAMOND), i + 41 + 22, j + 109);
      p_282454_.renderItem(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
      p_282454_.renderItem(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
      p_282454_.pose().popPose();
   }

   public void render(GuiGraphics p_283062_, int p_282876_, int p_282015_, float p_281395_) {
      this.renderBackground(p_283062_);
      super.render(p_283062_, p_282876_, p_282015_, p_281395_);
      this.renderTooltip(p_283062_, p_282876_, p_282015_);
   }

   @OnlyIn(Dist.CLIENT)
   interface BeaconButton {
      void updateStatus(int p_169631_);
   }

   @OnlyIn(Dist.CLIENT)
   class BeaconCancelButton extends BeaconScreen.BeaconSpriteScreenButton {
      public BeaconCancelButton(int p_97982_, int p_97983_) {
         super(p_97982_, p_97983_, 112, 220, CommonComponents.GUI_CANCEL);
      }

      public void onPress() {
         BeaconScreen.this.minecraft.player.closeContainer();
      }

      public void updateStatus(int p_169636_) {
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BeaconConfirmButton extends BeaconScreen.BeaconSpriteScreenButton {
      public BeaconConfirmButton(int p_97992_, int p_97993_) {
         super(p_97992_, p_97993_, 90, 220, CommonComponents.GUI_DONE);
      }

      public void onPress() {
         BeaconScreen.this.minecraft.getConnection().send(new ServerboundSetBeaconPacket(Optional.ofNullable(BeaconScreen.this.primary), Optional.ofNullable(BeaconScreen.this.secondary)));
         BeaconScreen.this.minecraft.player.closeContainer();
      }

      public void updateStatus(int p_169638_) {
         this.active = BeaconScreen.this.menu.hasPayment() && BeaconScreen.this.primary != null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BeaconPowerButton extends BeaconScreen.BeaconScreenButton {
      private final boolean isPrimary;
      protected final int tier;
      private MobEffect effect;
      private TextureAtlasSprite sprite;

      public BeaconPowerButton(int p_169642_, int p_169643_, MobEffect p_169644_, boolean p_169645_, int p_169646_) {
         super(p_169642_, p_169643_);
         this.isPrimary = p_169645_;
         this.tier = p_169646_;
         this.setEffect(p_169644_);
      }

      protected void setEffect(MobEffect p_169650_) {
         this.effect = p_169650_;
         this.sprite = Minecraft.getInstance().getMobEffectTextures().get(p_169650_);
         this.setTooltip(Tooltip.create(this.createEffectDescription(p_169650_), (Component)null));
      }

      protected MutableComponent createEffectDescription(MobEffect p_169652_) {
         return Component.translatable(p_169652_.getDescriptionId());
      }

      public void onPress() {
         if (!this.isSelected()) {
            if (this.isPrimary) {
               BeaconScreen.this.primary = this.effect;
            } else {
               BeaconScreen.this.secondary = this.effect;
            }

            BeaconScreen.this.updateButtons();
         }
      }

      protected void renderIcon(GuiGraphics p_282265_) {
         p_282265_.blit(this.getX() + 2, this.getY() + 2, 0, 18, 18, this.sprite);
      }

      public void updateStatus(int p_169648_) {
         this.active = this.tier < p_169648_;
         this.setSelected(this.effect == (this.isPrimary ? BeaconScreen.this.primary : BeaconScreen.this.secondary));
      }

      protected MutableComponent createNarrationMessage() {
         return this.createEffectDescription(this.effect);
      }
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class BeaconScreenButton extends AbstractButton implements BeaconScreen.BeaconButton {
      private boolean selected;

      protected BeaconScreenButton(int p_98022_, int p_98023_) {
         super(p_98022_, p_98023_, 22, 22, CommonComponents.EMPTY);
      }

      protected BeaconScreenButton(int p_169654_, int p_169655_, Component p_169656_) {
         super(p_169654_, p_169655_, 22, 22, p_169656_);
      }

      public void renderWidget(GuiGraphics p_281837_, int p_281780_, int p_283603_, float p_283562_) {
         int i = 219;
         int j = 0;
         if (!this.active) {
            j += this.width * 2;
         } else if (this.selected) {
            j += this.width * 1;
         } else if (this.isHoveredOrFocused()) {
            j += this.width * 3;
         }

         p_281837_.blit(BeaconScreen.BEACON_LOCATION, this.getX(), this.getY(), j, 219, this.width, this.height);
         this.renderIcon(p_281837_);
      }

      protected abstract void renderIcon(GuiGraphics p_283292_);

      public boolean isSelected() {
         return this.selected;
      }

      public void setSelected(boolean p_98032_) {
         this.selected = p_98032_;
      }

      public void updateWidgetNarration(NarrationElementOutput p_259705_) {
         this.defaultButtonNarrationText(p_259705_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class BeaconSpriteScreenButton extends BeaconScreen.BeaconScreenButton {
      private final int iconX;
      private final int iconY;

      protected BeaconSpriteScreenButton(int p_169663_, int p_169664_, int p_169665_, int p_169666_, Component p_169667_) {
         super(p_169663_, p_169664_, p_169667_);
         this.iconX = p_169665_;
         this.iconY = p_169666_;
      }

      protected void renderIcon(GuiGraphics p_283624_) {
         p_283624_.blit(BeaconScreen.BEACON_LOCATION, this.getX() + 2, this.getY() + 2, this.iconX, this.iconY, 18, 18);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BeaconUpgradePowerButton extends BeaconScreen.BeaconPowerButton {
      public BeaconUpgradePowerButton(int p_169675_, int p_169676_, MobEffect p_169677_) {
         super(p_169675_, p_169676_, p_169677_, false, 3);
      }

      protected MutableComponent createEffectDescription(MobEffect p_169681_) {
         return Component.translatable(p_169681_.getDescriptionId()).append(" II");
      }

      public void updateStatus(int p_169679_) {
         if (BeaconScreen.this.primary != null) {
            this.visible = true;
            this.setEffect(BeaconScreen.this.primary);
            super.updateStatus(p_169679_);
         } else {
            this.visible = false;
         }

      }
   }
}