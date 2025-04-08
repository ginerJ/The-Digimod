package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LockIconButton extends Button {
   private boolean locked;

   public LockIconButton(int p_94299_, int p_94300_, Button.OnPress p_94301_) {
      super(p_94299_, p_94300_, 20, 20, Component.translatable("narrator.button.difficulty_lock"), p_94301_, DEFAULT_NARRATION);
   }

   protected MutableComponent createNarrationMessage() {
      return CommonComponents.joinForNarration(super.createNarrationMessage(), this.isLocked() ? Component.translatable("narrator.button.difficulty_lock.locked") : Component.translatable("narrator.button.difficulty_lock.unlocked"));
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean p_94310_) {
      this.locked = p_94310_;
   }

   public void renderWidget(GuiGraphics p_282701_, int p_282638_, int p_283565_, float p_282549_) {
      LockIconButton.Icon lockiconbutton$icon;
      if (!this.active) {
         lockiconbutton$icon = this.locked ? LockIconButton.Icon.LOCKED_DISABLED : LockIconButton.Icon.UNLOCKED_DISABLED;
      } else if (this.isHoveredOrFocused()) {
         lockiconbutton$icon = this.locked ? LockIconButton.Icon.LOCKED_HOVER : LockIconButton.Icon.UNLOCKED_HOVER;
      } else {
         lockiconbutton$icon = this.locked ? LockIconButton.Icon.LOCKED : LockIconButton.Icon.UNLOCKED;
      }

      p_282701_.blit(Button.WIDGETS_LOCATION, this.getX(), this.getY(), lockiconbutton$icon.getX(), lockiconbutton$icon.getY(), this.width, this.height);
   }

   @OnlyIn(Dist.CLIENT)
   static enum Icon {
      LOCKED(0, 146),
      LOCKED_HOVER(0, 166),
      LOCKED_DISABLED(0, 186),
      UNLOCKED(20, 146),
      UNLOCKED_HOVER(20, 166),
      UNLOCKED_DISABLED(20, 186);

      private final int x;
      private final int y;

      private Icon(int p_94324_, int p_94325_) {
         this.x = p_94324_;
         this.y = p_94325_;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }
   }
}