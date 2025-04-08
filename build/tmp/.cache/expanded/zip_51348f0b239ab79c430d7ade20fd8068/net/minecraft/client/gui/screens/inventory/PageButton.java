package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PageButton extends Button {
   private final boolean isForward;
   private final boolean playTurnSound;

   public PageButton(int p_99225_, int p_99226_, boolean p_99227_, Button.OnPress p_99228_, boolean p_99229_) {
      super(p_99225_, p_99226_, 23, 13, CommonComponents.EMPTY, p_99228_, DEFAULT_NARRATION);
      this.isForward = p_99227_;
      this.playTurnSound = p_99229_;
   }

   public void renderWidget(GuiGraphics p_283468_, int p_282922_, int p_283637_, float p_282459_) {
      int i = 0;
      int j = 192;
      if (this.isHoveredOrFocused()) {
         i += 23;
      }

      if (!this.isForward) {
         j += 13;
      }

      p_283468_.blit(BookViewScreen.BOOK_LOCATION, this.getX(), this.getY(), i, j, 23, 13);
   }

   public void playDownSound(SoundManager p_99231_) {
      if (this.playTurnSound) {
         p_99231_.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
      }

   }
}