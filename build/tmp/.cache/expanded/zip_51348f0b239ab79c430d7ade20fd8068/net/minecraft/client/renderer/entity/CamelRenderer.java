package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CamelRenderer extends MobRenderer<Camel, CamelModel<Camel>> {
   private static final ResourceLocation CAMEL_LOCATION = new ResourceLocation("textures/entity/camel/camel.png");

   public CamelRenderer(EntityRendererProvider.Context p_251790_, ModelLayerLocation p_249929_) {
      super(p_251790_, new CamelModel<>(p_251790_.bakeLayer(p_249929_)), 0.7F);
   }

   public ResourceLocation getTextureLocation(Camel p_249584_) {
      return CAMEL_LOCATION;
   }
}