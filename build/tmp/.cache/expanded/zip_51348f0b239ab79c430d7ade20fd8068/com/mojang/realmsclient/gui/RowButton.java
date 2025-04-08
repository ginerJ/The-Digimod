package com.mojang.realmsclient.gui;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RowButton {
   public final int width;
   public final int height;
   public final int xOffset;
   public final int yOffset;

   public RowButton(int p_88012_, int p_88013_, int p_88014_, int p_88015_) {
      this.width = p_88012_;
      this.height = p_88013_;
      this.xOffset = p_88014_;
      this.yOffset = p_88015_;
   }

   public void drawForRowAt(GuiGraphics p_281584_, int p_88020_, int p_88021_, int p_88022_, int p_88023_) {
      int i = p_88020_ + this.xOffset;
      int j = p_88021_ + this.yOffset;
      boolean flag = p_88022_ >= i && p_88022_ <= i + this.width && p_88023_ >= j && p_88023_ <= j + this.height;
      this.draw(p_281584_, i, j, flag);
   }

   protected abstract void draw(GuiGraphics p_281291_, int p_88025_, int p_88026_, boolean p_88027_);

   public int getRight() {
      return this.xOffset + this.width;
   }

   public int getBottom() {
      return this.yOffset + this.height;
   }

   public abstract void onClick(int p_88017_);

   public static void drawButtonsInRow(GuiGraphics p_281401_, List<RowButton> p_283164_, RealmsObjectSelectionList<?> p_282348_, int p_282527_, int p_281326_, int p_281575_, int p_282538_) {
      for(RowButton rowbutton : p_283164_) {
         if (p_282348_.getRowWidth() > rowbutton.getRight()) {
            rowbutton.drawForRowAt(p_281401_, p_282527_, p_281326_, p_281575_, p_282538_);
         }
      }

   }

   public static void rowButtonMouseClicked(RealmsObjectSelectionList<?> p_88037_, ObjectSelectionList.Entry<?> p_88038_, List<RowButton> p_88039_, int p_88040_, double p_88041_, double p_88042_) {
      if (p_88040_ == 0) {
         int i = p_88037_.children().indexOf(p_88038_);
         if (i > -1) {
            p_88037_.selectItem(i);
            int j = p_88037_.getRowLeft();
            int k = p_88037_.getRowTop(i);
            int l = (int)(p_88041_ - (double)j);
            int i1 = (int)(p_88042_ - (double)k);

            for(RowButton rowbutton : p_88039_) {
               if (l >= rowbutton.xOffset && l <= rowbutton.getRight() && i1 >= rowbutton.yOffset && i1 <= rowbutton.getBottom()) {
                  rowbutton.onClick(i);
               }
            }
         }
      }

   }
}