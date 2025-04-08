package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VexRenderer extends MobRenderer<Vex, VexModel> {
   private static final ResourceLocation VEX_LOCATION = new ResourceLocation("textures/entity/illager/vex.png");
   private static final ResourceLocation VEX_CHARGING_LOCATION = new ResourceLocation("textures/entity/illager/vex_charging.png");

   public VexRenderer(EntityRendererProvider.Context p_174435_) {
      super(p_174435_, new VexModel(p_174435_.bakeLayer(ModelLayers.VEX)), 0.3F);
      this.addLayer(new ItemInHandLayer<>(this, p_174435_.getItemInHandRenderer()));
   }

   protected int getBlockLightLevel(Vex p_116298_, BlockPos p_116299_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(Vex p_116292_) {
      return p_116292_.isCharging() ? VEX_CHARGING_LOCATION : VEX_LOCATION;
   }
}