package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AllayRenderer extends MobRenderer<Allay, AllayModel> {
   private static final ResourceLocation ALLAY_TEXTURE = new ResourceLocation("textures/entity/allay/allay.png");

   public AllayRenderer(EntityRendererProvider.Context p_234551_) {
      super(p_234551_, new AllayModel(p_234551_.bakeLayer(ModelLayers.ALLAY)), 0.4F);
      this.addLayer(new ItemInHandLayer<>(this, p_234551_.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(Allay p_234558_) {
      return ALLAY_TEXTURE;
   }

   protected int getBlockLightLevel(Allay p_234560_, BlockPos p_234561_) {
      return 15;
   }
}