package net.minecraft.client.renderer.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RabbitRenderer extends MobRenderer<Rabbit, RabbitModel<Rabbit>> {
   private static final ResourceLocation RABBIT_BROWN_LOCATION = new ResourceLocation("textures/entity/rabbit/brown.png");
   private static final ResourceLocation RABBIT_WHITE_LOCATION = new ResourceLocation("textures/entity/rabbit/white.png");
   private static final ResourceLocation RABBIT_BLACK_LOCATION = new ResourceLocation("textures/entity/rabbit/black.png");
   private static final ResourceLocation RABBIT_GOLD_LOCATION = new ResourceLocation("textures/entity/rabbit/gold.png");
   private static final ResourceLocation RABBIT_SALT_LOCATION = new ResourceLocation("textures/entity/rabbit/salt.png");
   private static final ResourceLocation RABBIT_WHITE_SPLOTCHED_LOCATION = new ResourceLocation("textures/entity/rabbit/white_splotched.png");
   private static final ResourceLocation RABBIT_TOAST_LOCATION = new ResourceLocation("textures/entity/rabbit/toast.png");
   private static final ResourceLocation RABBIT_EVIL_LOCATION = new ResourceLocation("textures/entity/rabbit/caerbannog.png");

   public RabbitRenderer(EntityRendererProvider.Context p_174360_) {
      super(p_174360_, new RabbitModel<>(p_174360_.bakeLayer(ModelLayers.RABBIT)), 0.3F);
   }

   public ResourceLocation getTextureLocation(Rabbit p_115803_) {
      String s = ChatFormatting.stripFormatting(p_115803_.getName().getString());
      if ("Toast".equals(s)) {
         return RABBIT_TOAST_LOCATION;
      } else {
         ResourceLocation resourcelocation;
         switch (p_115803_.getVariant()) {
            case BROWN:
               resourcelocation = RABBIT_BROWN_LOCATION;
               break;
            case WHITE:
               resourcelocation = RABBIT_WHITE_LOCATION;
               break;
            case BLACK:
               resourcelocation = RABBIT_BLACK_LOCATION;
               break;
            case GOLD:
               resourcelocation = RABBIT_GOLD_LOCATION;
               break;
            case SALT:
               resourcelocation = RABBIT_SALT_LOCATION;
               break;
            case WHITE_SPLOTCHED:
               resourcelocation = RABBIT_WHITE_SPLOTCHED_LOCATION;
               break;
            case EVIL:
               resourcelocation = RABBIT_EVIL_LOCATION;
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return resourcelocation;
      }
   }
}