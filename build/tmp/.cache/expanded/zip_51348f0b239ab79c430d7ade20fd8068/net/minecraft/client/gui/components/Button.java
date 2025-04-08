package net.minecraft.client.gui.components;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Button extends AbstractButton {
   public static final int SMALL_WIDTH = 120;
   public static final int DEFAULT_WIDTH = 150;
   public static final int DEFAULT_HEIGHT = 20;
   protected static final Button.CreateNarration DEFAULT_NARRATION = (p_253298_) -> {
      return p_253298_.get();
   };
   protected final Button.OnPress onPress;
   protected final Button.CreateNarration createNarration;

   public static Button.Builder builder(Component p_254439_, Button.OnPress p_254567_) {
      return new Button.Builder(p_254439_, p_254567_);
   }

   protected Button(int p_259075_, int p_259271_, int p_260232_, int p_260028_, Component p_259351_, Button.OnPress p_260152_, Button.CreateNarration p_259552_) {
      super(p_259075_, p_259271_, p_260232_, p_260028_, p_259351_);
      this.onPress = p_260152_;
      this.createNarration = p_259552_;
   }

   protected Button(Builder builder) {
      this(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration);
      setTooltip(builder.tooltip); // Forge: Make use of the Builder tooltip
   }

   public void onPress() {
      this.onPress.onPress(this);
   }

   protected MutableComponent createNarrationMessage() {
      return this.createNarration.createNarrationMessage(() -> {
         return super.createNarrationMessage();
      });
   }

   public void updateWidgetNarration(NarrationElementOutput p_259196_) {
      this.defaultButtonNarrationText(p_259196_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final Component message;
      private final Button.OnPress onPress;
      @Nullable
      private Tooltip tooltip;
      private int x;
      private int y;
      private int width = 150;
      private int height = 20;
      private Button.CreateNarration createNarration = Button.DEFAULT_NARRATION;

      public Builder(Component p_254097_, Button.OnPress p_253761_) {
         this.message = p_254097_;
         this.onPress = p_253761_;
      }

      public Button.Builder pos(int p_254538_, int p_254216_) {
         this.x = p_254538_;
         this.y = p_254216_;
         return this;
      }

      public Button.Builder width(int p_254259_) {
         this.width = p_254259_;
         return this;
      }

      public Button.Builder size(int p_253727_, int p_254457_) {
         this.width = p_253727_;
         this.height = p_254457_;
         return this;
      }

      public Button.Builder bounds(int p_254166_, int p_253872_, int p_254522_, int p_253985_) {
         return this.pos(p_254166_, p_253872_).size(p_254522_, p_253985_);
      }

      public Button.Builder tooltip(@Nullable Tooltip p_259609_) {
         this.tooltip = p_259609_;
         return this;
      }

      public Button.Builder createNarration(Button.CreateNarration p_253638_) {
         this.createNarration = p_253638_;
         return this;
      }

      public Button build() {
         return build(Button::new);
      }

      public Button build(java.util.function.Function<Builder, Button> builder) {
         return builder.apply(this);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface CreateNarration {
      MutableComponent createNarrationMessage(Supplier<MutableComponent> p_253695_);
   }

   @OnlyIn(Dist.CLIENT)
   public interface OnPress {
      void onPress(Button p_93751_);
   }
}
