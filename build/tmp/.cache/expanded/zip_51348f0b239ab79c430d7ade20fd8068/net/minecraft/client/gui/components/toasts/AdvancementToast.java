package net.minecraft.client.gui.components.toasts;

import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementToast implements Toast {
   public static final int DISPLAY_TIME = 5000;
   private final Advancement advancement;
   private boolean playedSound;

   public AdvancementToast(Advancement p_94798_) {
      this.advancement = p_94798_;
   }

   public Toast.Visibility render(GuiGraphics p_281813_, ToastComponent p_282243_, long p_282604_) {
      DisplayInfo displayinfo = this.advancement.getDisplay();
      p_281813_.blit(TEXTURE, 0, 0, 0, 0, this.width(), this.height());
      if (displayinfo != null) {
         List<FormattedCharSequence> list = p_282243_.getMinecraft().font.split(displayinfo.getTitle(), 125);
         int i = displayinfo.getFrame() == FrameType.CHALLENGE ? 16746751 : 16776960;
         if (list.size() == 1) {
            p_281813_.drawString(p_282243_.getMinecraft().font, displayinfo.getFrame().getDisplayName(), 30, 7, i | -16777216, false);
            p_281813_.drawString(p_282243_.getMinecraft().font, list.get(0), 30, 18, -1, false);
         } else {
            int j = 1500;
            float f = 300.0F;
            if (p_282604_ < 1500L) {
               int k = Mth.floor(Mth.clamp((float)(1500L - p_282604_) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
               p_281813_.drawString(p_282243_.getMinecraft().font, displayinfo.getFrame().getDisplayName(), 30, 11, i | k, false);
            } else {
               int i1 = Mth.floor(Mth.clamp((float)(p_282604_ - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
               int l = this.height() / 2 - list.size() * 9 / 2;

               for(FormattedCharSequence formattedcharsequence : list) {
                  p_281813_.drawString(p_282243_.getMinecraft().font, formattedcharsequence, 30, l, 16777215 | i1, false);
                  l += 9;
               }
            }
         }

         if (!this.playedSound && p_282604_ > 0L) {
            this.playedSound = true;
            if (displayinfo.getFrame() == FrameType.CHALLENGE) {
               p_282243_.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
            }
         }

         p_281813_.renderFakeItem(displayinfo.getIcon(), 8, 8);
         return (double)p_282604_ >= 5000.0D * p_282243_.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
      } else {
         return Toast.Visibility.HIDE;
      }
   }
}