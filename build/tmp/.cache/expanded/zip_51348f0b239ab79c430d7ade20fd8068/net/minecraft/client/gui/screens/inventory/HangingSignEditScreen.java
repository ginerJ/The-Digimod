package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class HangingSignEditScreen extends AbstractSignEditScreen {
   public static final float MAGIC_BACKGROUND_SCALE = 4.5F;
   private static final Vector3f TEXT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);
   private static final int TEXTURE_WIDTH = 16;
   private static final int TEXTURE_HEIGHT = 16;
   private final ResourceLocation texture = new ResourceLocation(this.woodType.name() + ".png").withPrefix("textures/gui/hanging_signs/");

   public HangingSignEditScreen(SignBlockEntity p_278017_, boolean p_277942_, boolean p_277778_) {
      super(p_278017_, p_277942_, p_277778_, Component.translatable("hanging_sign.edit"));
   }

   protected void offsetSign(GuiGraphics p_282472_, BlockState p_282359_) {
      p_282472_.pose().translate((float)this.width / 2.0F, 125.0F, 50.0F);
   }

   protected void renderSignBackground(GuiGraphics p_282580_, BlockState p_283648_) {
      p_282580_.pose().translate(0.0F, -13.0F, 0.0F);
      p_282580_.pose().scale(4.5F, 4.5F, 1.0F);
      p_282580_.blit(this.texture, -8, -8, 0.0F, 0.0F, 16, 16, 16, 16);
   }

   protected Vector3f getSignTextScale() {
      return TEXT_SCALE;
   }
}
