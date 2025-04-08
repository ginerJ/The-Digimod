package net.minecraft.client.resources.metadata.animation;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationMetadataSection {
   public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
   public static final String SECTION_NAME = "animation";
   public static final int DEFAULT_FRAME_TIME = 1;
   public static final int UNKNOWN_SIZE = -1;
   public static final AnimationMetadataSection EMPTY = new AnimationMetadataSection(Lists.newArrayList(), -1, -1, 1, false) {
      public FrameSize calculateFrameSize(int p_251622_, int p_252064_) {
         return new FrameSize(p_251622_, p_252064_);
      }
   };
   private final List<AnimationFrame> frames;
   private final int frameWidth;
   private final int frameHeight;
   private final int defaultFrameTime;
   private final boolean interpolatedFrames;

   public AnimationMetadataSection(List<AnimationFrame> p_119020_, int p_119021_, int p_119022_, int p_119023_, boolean p_119024_) {
      this.frames = p_119020_;
      this.frameWidth = p_119021_;
      this.frameHeight = p_119022_;
      this.defaultFrameTime = p_119023_;
      this.interpolatedFrames = p_119024_;
   }

   public FrameSize calculateFrameSize(int p_249859_, int p_250148_) {
      if (this.frameWidth != -1) {
         return this.frameHeight != -1 ? new FrameSize(this.frameWidth, this.frameHeight) : new FrameSize(this.frameWidth, p_250148_);
      } else if (this.frameHeight != -1) {
         return new FrameSize(p_249859_, this.frameHeight);
      } else {
         int i = Math.min(p_249859_, p_250148_);
         return new FrameSize(i, i);
      }
   }

   public int getDefaultFrameTime() {
      return this.defaultFrameTime;
   }

   public boolean isInterpolatedFrames() {
      return this.interpolatedFrames;
   }

   public void forEachFrame(AnimationMetadataSection.FrameOutput p_174862_) {
      for(AnimationFrame animationframe : this.frames) {
         p_174862_.accept(animationframe.getIndex(), animationframe.getTime(this.defaultFrameTime));
      }

   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface FrameOutput {
      void accept(int p_174864_, int p_174865_);
   }
}