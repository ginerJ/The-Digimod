package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.WardenEmissiveLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WardenRenderer extends MobRenderer<Warden, WardenModel<Warden>> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/warden/warden.png");
   private static final ResourceLocation BIOLUMINESCENT_LAYER_TEXTURE = new ResourceLocation("textures/entity/warden/warden_bioluminescent_layer.png");
   private static final ResourceLocation HEART_TEXTURE = new ResourceLocation("textures/entity/warden/warden_heart.png");
   private static final ResourceLocation PULSATING_SPOTS_TEXTURE_1 = new ResourceLocation("textures/entity/warden/warden_pulsating_spots_1.png");
   private static final ResourceLocation PULSATING_SPOTS_TEXTURE_2 = new ResourceLocation("textures/entity/warden/warden_pulsating_spots_2.png");

   public WardenRenderer(EntityRendererProvider.Context p_234787_) {
      super(p_234787_, new WardenModel<>(p_234787_.bakeLayer(ModelLayers.WARDEN)), 0.9F);
      this.addLayer(new WardenEmissiveLayer<>(this, BIOLUMINESCENT_LAYER_TEXTURE, (p_234809_, p_234810_, p_234811_) -> {
         return 1.0F;
      }, WardenModel::getBioluminescentLayerModelParts));
      this.addLayer(new WardenEmissiveLayer<>(this, PULSATING_SPOTS_TEXTURE_1, (p_234805_, p_234806_, p_234807_) -> {
         return Math.max(0.0F, Mth.cos(p_234807_ * 0.045F) * 0.25F);
      }, WardenModel::getPulsatingSpotsLayerModelParts));
      this.addLayer(new WardenEmissiveLayer<>(this, PULSATING_SPOTS_TEXTURE_2, (p_234801_, p_234802_, p_234803_) -> {
         return Math.max(0.0F, Mth.cos(p_234803_ * 0.045F + (float)Math.PI) * 0.25F);
      }, WardenModel::getPulsatingSpotsLayerModelParts));
      this.addLayer(new WardenEmissiveLayer<>(this, TEXTURE, (p_234797_, p_234798_, p_234799_) -> {
         return p_234797_.getTendrilAnimation(p_234798_);
      }, WardenModel::getTendrilsLayerModelParts));
      this.addLayer(new WardenEmissiveLayer<>(this, HEART_TEXTURE, (p_234793_, p_234794_, p_234795_) -> {
         return p_234793_.getHeartAnimation(p_234794_);
      }, WardenModel::getHeartLayerModelParts));
   }

   public ResourceLocation getTextureLocation(Warden p_234791_) {
      return TEXTURE;
   }
}