package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class SmithingScreen extends ItemCombinerScreen<SmithingMenu> {
   private static final ResourceLocation SMITHING_LOCATION = new ResourceLocation("textures/gui/container/smithing.png");
   private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM = new ResourceLocation("item/empty_slot_smithing_template_armor_trim");
   private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE = new ResourceLocation("item/empty_slot_smithing_template_netherite_upgrade");
   private static final Component MISSING_TEMPLATE_TOOLTIP = Component.translatable("container.upgrade.missing_template_tooltip");
   private static final Component ERROR_TOOLTIP = Component.translatable("container.upgrade.error_tooltip");
   private static final List<ResourceLocation> EMPTY_SLOT_SMITHING_TEMPLATES = List.of(EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE);
   private static final int TITLE_LABEL_X = 44;
   private static final int TITLE_LABEL_Y = 15;
   private static final int ERROR_ICON_WIDTH = 28;
   private static final int ERROR_ICON_HEIGHT = 21;
   private static final int ERROR_ICON_X = 65;
   private static final int ERROR_ICON_Y = 46;
   private static final int TOOLTIP_WIDTH = 115;
   public static final int ARMOR_STAND_Y_ROT = 210;
   public static final int ARMOR_STAND_X_ROT = 25;
   public static final Quaternionf ARMOR_STAND_ANGLE = (new Quaternionf()).rotationXYZ(0.43633232F, 0.0F, (float)Math.PI);
   public static final int ARMOR_STAND_SCALE = 25;
   public static final int ARMOR_STAND_OFFSET_Y = 75;
   public static final int ARMOR_STAND_OFFSET_X = 141;
   private final CyclingSlotBackground templateIcon = new CyclingSlotBackground(0);
   private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(1);
   private final CyclingSlotBackground additionalIcon = new CyclingSlotBackground(2);
   @Nullable
   private ArmorStand armorStandPreview;

   public SmithingScreen(SmithingMenu p_99290_, Inventory p_99291_, Component p_99292_) {
      super(p_99290_, p_99291_, p_99292_, SMITHING_LOCATION);
      this.titleLabelX = 44;
      this.titleLabelY = 15;
   }

   protected void subInit() {
      this.armorStandPreview = new ArmorStand(this.minecraft.level, 0.0D, 0.0D, 0.0D);
      this.armorStandPreview.setNoBasePlate(true);
      this.armorStandPreview.setShowArms(true);
      this.armorStandPreview.yBodyRot = 210.0F;
      this.armorStandPreview.setXRot(25.0F);
      this.armorStandPreview.yHeadRot = this.armorStandPreview.getYRot();
      this.armorStandPreview.yHeadRotO = this.armorStandPreview.getYRot();
      this.updateArmorStandPreview(this.menu.getSlot(3).getItem());
   }

   public void containerTick() {
      super.containerTick();
      Optional<SmithingTemplateItem> optional = this.getTemplateItem();
      this.templateIcon.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
      this.baseIcon.tick(optional.map(SmithingTemplateItem::getBaseSlotEmptyIcons).orElse(List.of()));
      this.additionalIcon.tick(optional.map(SmithingTemplateItem::getAdditionalSlotEmptyIcons).orElse(List.of()));
   }

   private Optional<SmithingTemplateItem> getTemplateItem() {
      ItemStack itemstack = this.menu.getSlot(0).getItem();
      if (!itemstack.isEmpty()) {
         Item item = itemstack.getItem();
         if (item instanceof SmithingTemplateItem) {
            SmithingTemplateItem smithingtemplateitem = (SmithingTemplateItem)item;
            return Optional.of(smithingtemplateitem);
         }
      }

      return Optional.empty();
   }

   public void render(GuiGraphics p_281961_, int p_282410_, int p_283013_, float p_282408_) {
      super.render(p_281961_, p_282410_, p_283013_, p_282408_);
      this.renderOnboardingTooltips(p_281961_, p_282410_, p_283013_);
   }

   protected void renderBg(GuiGraphics p_283264_, float p_267158_, int p_267266_, int p_266722_) {
      super.renderBg(p_283264_, p_267158_, p_267266_, p_266722_);
      this.templateIcon.render(this.menu, p_283264_, p_267158_, this.leftPos, this.topPos);
      this.baseIcon.render(this.menu, p_283264_, p_267158_, this.leftPos, this.topPos);
      this.additionalIcon.render(this.menu, p_283264_, p_267158_, this.leftPos, this.topPos);
      InventoryScreen.renderEntityInInventory(p_283264_, this.leftPos + 141, this.topPos + 75, 25, ARMOR_STAND_ANGLE, (Quaternionf)null, this.armorStandPreview);
   }

   public void slotChanged(AbstractContainerMenu p_267217_, int p_266842_, ItemStack p_267208_) {
      if (p_266842_ == 3) {
         this.updateArmorStandPreview(p_267208_);
      }

   }

   private void updateArmorStandPreview(ItemStack p_268225_) {
      if (this.armorStandPreview != null) {
         for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            this.armorStandPreview.setItemSlot(equipmentslot, ItemStack.EMPTY);
         }

         if (!p_268225_.isEmpty()) {
            ItemStack itemstack = p_268225_.copy();
            Item item = p_268225_.getItem();
            if (item instanceof ArmorItem) {
               ArmorItem armoritem = (ArmorItem)item;
               this.armorStandPreview.setItemSlot(armoritem.getEquipmentSlot(), itemstack);
            } else {
               this.armorStandPreview.setItemSlot(EquipmentSlot.OFFHAND, itemstack);
            }
         }

      }
   }

   protected void renderErrorIcon(GuiGraphics p_281835_, int p_283389_, int p_282634_) {
      if (this.hasRecipeError()) {
         p_281835_.blit(SMITHING_LOCATION, p_283389_ + 65, p_282634_ + 46, this.imageWidth, 0, 28, 21);
      }

   }

   private void renderOnboardingTooltips(GuiGraphics p_281668_, int p_267192_, int p_266859_) {
      Optional<Component> optional = Optional.empty();
      if (this.hasRecipeError() && this.isHovering(65, 46, 28, 21, (double)p_267192_, (double)p_266859_)) {
         optional = Optional.of(ERROR_TOOLTIP);
      }

      if (this.hoveredSlot != null) {
         ItemStack itemstack = this.menu.getSlot(0).getItem();
         ItemStack itemstack1 = this.hoveredSlot.getItem();
         if (itemstack.isEmpty()) {
            if (this.hoveredSlot.index == 0) {
               optional = Optional.of(MISSING_TEMPLATE_TOOLTIP);
            }
         } else {
            Item item = itemstack.getItem();
            if (item instanceof SmithingTemplateItem) {
               SmithingTemplateItem smithingtemplateitem = (SmithingTemplateItem)item;
               if (itemstack1.isEmpty()) {
                  if (this.hoveredSlot.index == 1) {
                     optional = Optional.of(smithingtemplateitem.getBaseSlotDescription());
                  } else if (this.hoveredSlot.index == 2) {
                     optional = Optional.of(smithingtemplateitem.getAdditionSlotDescription());
                  }
               }
            }
         }
      }

      optional.ifPresent((p_280863_) -> {
         p_281668_.renderTooltip(this.font, this.font.split(p_280863_, 115), p_267192_, p_266859_);
      });
   }

   private boolean hasRecipeError() {
      return this.menu.getSlot(0).hasItem() && this.menu.getSlot(1).hasItem() && this.menu.getSlot(2).hasItem() && !this.menu.getSlot(this.menu.getResultSlot()).hasItem();
   }
}