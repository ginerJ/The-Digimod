package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseInventoryScreen extends AbstractContainerScreen<HorseInventoryMenu> {
   private static final ResourceLocation HORSE_INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/horse.png");
   private final AbstractHorse horse;
   private float xMouse;
   private float yMouse;

   public HorseInventoryScreen(HorseInventoryMenu p_98817_, Inventory p_98818_, AbstractHorse p_98819_) {
      super(p_98817_, p_98818_, p_98819_.getDisplayName());
      this.horse = p_98819_;
   }

   protected void renderBg(GuiGraphics p_282553_, float p_282998_, int p_282929_, int p_283133_) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      p_282553_.blit(HORSE_INVENTORY_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
      if (this.horse instanceof AbstractChestedHorse) {
         AbstractChestedHorse abstractchestedhorse = (AbstractChestedHorse)this.horse;
         if (abstractchestedhorse.hasChest()) {
            p_282553_.blit(HORSE_INVENTORY_LOCATION, i + 79, j + 17, 0, this.imageHeight, abstractchestedhorse.getInventoryColumns() * 18, 54);
         }
      }

      if (this.horse.isSaddleable()) {
         p_282553_.blit(HORSE_INVENTORY_LOCATION, i + 7, j + 35 - 18, 18, this.imageHeight + 54, 18, 18);
      }

      if (this.horse.canWearArmor()) {
         if (this.horse instanceof Llama) {
            p_282553_.blit(HORSE_INVENTORY_LOCATION, i + 7, j + 35, 36, this.imageHeight + 54, 18, 18);
         } else {
            p_282553_.blit(HORSE_INVENTORY_LOCATION, i + 7, j + 35, 0, this.imageHeight + 54, 18, 18);
         }
      }

      InventoryScreen.renderEntityInInventoryFollowsMouse(p_282553_, i + 51, j + 60, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.horse);
   }

   public void render(GuiGraphics p_281697_, int p_282103_, int p_283529_, float p_283079_) {
      this.renderBackground(p_281697_);
      this.xMouse = (float)p_282103_;
      this.yMouse = (float)p_283529_;
      super.render(p_281697_, p_282103_, p_283529_, p_283079_);
      this.renderTooltip(p_281697_, p_282103_, p_283529_);
   }
}