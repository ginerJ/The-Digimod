package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SubtitleOverlay implements SoundEventListener {
   private static final long DISPLAY_TIME = 3000L;
   private final Minecraft minecraft;
   private final List<SubtitleOverlay.Subtitle> subtitles = Lists.newArrayList();
   private boolean isListening;

   public SubtitleOverlay(Minecraft p_94641_) {
      this.minecraft = p_94641_;
   }

   public void render(GuiGraphics p_282562_) {
      if (!this.isListening && this.minecraft.options.showSubtitles().get()) {
         this.minecraft.getSoundManager().addListener(this);
         this.isListening = true;
      } else if (this.isListening && !this.minecraft.options.showSubtitles().get()) {
         this.minecraft.getSoundManager().removeListener(this);
         this.isListening = false;
      }

      if (this.isListening && !this.subtitles.isEmpty()) {
         Vec3 vec3 = new Vec3(this.minecraft.player.getX(), this.minecraft.player.getEyeY(), this.minecraft.player.getZ());
         Vec3 vec31 = (new Vec3(0.0D, 0.0D, -1.0D)).xRot(-this.minecraft.player.getXRot() * ((float)Math.PI / 180F)).yRot(-this.minecraft.player.getYRot() * ((float)Math.PI / 180F));
         Vec3 vec32 = (new Vec3(0.0D, 1.0D, 0.0D)).xRot(-this.minecraft.player.getXRot() * ((float)Math.PI / 180F)).yRot(-this.minecraft.player.getYRot() * ((float)Math.PI / 180F));
         Vec3 vec33 = vec31.cross(vec32);
         int i = 0;
         int j = 0;
         double d0 = this.minecraft.options.notificationDisplayTime().get();
         Iterator<SubtitleOverlay.Subtitle> iterator = this.subtitles.iterator();

         while(iterator.hasNext()) {
            SubtitleOverlay.Subtitle subtitleoverlay$subtitle = iterator.next();
            if ((double)subtitleoverlay$subtitle.getTime() + 3000.0D * d0 <= (double)Util.getMillis()) {
               iterator.remove();
            } else {
               j = Math.max(j, this.minecraft.font.width(subtitleoverlay$subtitle.getText()));
            }
         }

         j += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");

         for(SubtitleOverlay.Subtitle subtitleoverlay$subtitle1 : this.subtitles) {
            int k = 255;
            Component component = subtitleoverlay$subtitle1.getText();
            Vec3 vec34 = subtitleoverlay$subtitle1.getLocation().subtract(vec3).normalize();
            double d1 = -vec33.dot(vec34);
            double d2 = -vec31.dot(vec34);
            boolean flag = d2 > 0.5D;
            int l = j / 2;
            int i1 = 9;
            int j1 = i1 / 2;
            float f = 1.0F;
            int k1 = this.minecraft.font.width(component);
            int l1 = Mth.floor(Mth.clampedLerp(255.0F, 75.0F, (float)(Util.getMillis() - subtitleoverlay$subtitle1.getTime()) / (float)(3000.0D * d0)));
            int i2 = l1 << 16 | l1 << 8 | l1;
            p_282562_.pose().pushPose();
            p_282562_.pose().translate((float)p_282562_.guiWidth() - (float)l * 1.0F - 2.0F, (float)(p_282562_.guiHeight() - 35) - (float)(i * (i1 + 1)) * 1.0F, 0.0F);
            p_282562_.pose().scale(1.0F, 1.0F, 1.0F);
            p_282562_.fill(-l - 1, -j1 - 1, l + 1, j1 + 1, this.minecraft.options.getBackgroundColor(0.8F));
            int j2 = i2 + -16777216;
            if (!flag) {
               if (d1 > 0.0D) {
                  p_282562_.drawString(this.minecraft.font, ">", l - this.minecraft.font.width(">"), -j1, j2);
               } else if (d1 < 0.0D) {
                  p_282562_.drawString(this.minecraft.font, "<", -l, -j1, j2);
               }
            }

            p_282562_.drawString(this.minecraft.font, component, -k1 / 2, -j1, j2);
            p_282562_.pose().popPose();
            ++i;
         }

      }
   }

   public void onPlaySound(SoundInstance p_94645_, WeighedSoundEvents p_94646_) {
      if (p_94646_.getSubtitle() != null) {
         Component component = p_94646_.getSubtitle();
         if (!this.subtitles.isEmpty()) {
            for(SubtitleOverlay.Subtitle subtitleoverlay$subtitle : this.subtitles) {
               if (subtitleoverlay$subtitle.getText().equals(component)) {
                  subtitleoverlay$subtitle.refresh(new Vec3(p_94645_.getX(), p_94645_.getY(), p_94645_.getZ()));
                  return;
               }
            }
         }

         this.subtitles.add(new SubtitleOverlay.Subtitle(component, new Vec3(p_94645_.getX(), p_94645_.getY(), p_94645_.getZ())));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Subtitle {
      private final Component text;
      private long time;
      private Vec3 location;

      public Subtitle(Component p_169072_, Vec3 p_169073_) {
         this.text = p_169072_;
         this.location = p_169073_;
         this.time = Util.getMillis();
      }

      public Component getText() {
         return this.text;
      }

      public long getTime() {
         return this.time;
      }

      public Vec3 getLocation() {
         return this.location;
      }

      public void refresh(Vec3 p_94657_) {
         this.location = p_94657_;
         this.time = Util.getMillis();
      }
   }
}