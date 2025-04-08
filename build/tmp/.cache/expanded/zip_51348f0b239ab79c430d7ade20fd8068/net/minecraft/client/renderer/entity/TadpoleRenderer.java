package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.TadpoleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TadpoleRenderer extends MobRenderer<Tadpole, TadpoleModel<Tadpole>> {
   private static final ResourceLocation TADPOLE_TEXTURE = new ResourceLocation("textures/entity/tadpole/tadpole.png");

   public TadpoleRenderer(EntityRendererProvider.Context p_234655_) {
      super(p_234655_, new TadpoleModel<>(p_234655_.bakeLayer(ModelLayers.TADPOLE)), 0.14F);
   }

   public ResourceLocation getTextureLocation(Tadpole p_234659_) {
      return TADPOLE_TEXTURE;
   }
}