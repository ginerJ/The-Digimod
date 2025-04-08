package net.minecraft.client.renderer.block;

import java.util.Map;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelShaper {
   private Map<BlockState, BakedModel> modelByStateCache = Map.of();
   private final ModelManager modelManager;

   public BlockModelShaper(ModelManager p_110880_) {
      this.modelManager = p_110880_;
   }

   @Deprecated
   public TextureAtlasSprite getParticleIcon(BlockState p_110883_) {
      return this.getBlockModel(p_110883_).getParticleIcon(net.minecraftforge.client.model.data.ModelData.EMPTY);
   }

   public TextureAtlasSprite getTexture(BlockState p_110883_, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
      var data = level.getModelDataManager().getAt(pos);
      BakedModel model = this.getBlockModel(p_110883_);
      return model.getParticleIcon(model.getModelData(level, pos, p_110883_, data == null ? net.minecraftforge.client.model.data.ModelData.EMPTY : data));
   }

   public BakedModel getBlockModel(BlockState p_110894_) {
      BakedModel bakedmodel = this.modelByStateCache.get(p_110894_);
      if (bakedmodel == null) {
         bakedmodel = this.modelManager.getMissingModel();
      }

      return bakedmodel;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void replaceCache(Map<BlockState, BakedModel> p_248582_) {
      this.modelByStateCache = p_248582_;
   }

   public static ModelResourceLocation stateToModelLocation(BlockState p_110896_) {
      return stateToModelLocation(BuiltInRegistries.BLOCK.getKey(p_110896_.getBlock()), p_110896_);
   }

   public static ModelResourceLocation stateToModelLocation(ResourceLocation p_110890_, BlockState p_110891_) {
      return new ModelResourceLocation(p_110890_, statePropertiesToString(p_110891_.getValues()));
   }

   public static String statePropertiesToString(Map<Property<?>, Comparable<?>> p_110888_) {
      StringBuilder stringbuilder = new StringBuilder();

      for(Map.Entry<Property<?>, Comparable<?>> entry : p_110888_.entrySet()) {
         if (stringbuilder.length() != 0) {
            stringbuilder.append(',');
         }

         Property<?> property = entry.getKey();
         stringbuilder.append(property.getName());
         stringbuilder.append('=');
         stringbuilder.append(getValue(property, entry.getValue()));
      }

      return stringbuilder.toString();
   }

   private static <T extends Comparable<T>> String getValue(Property<T> p_110885_, Comparable<?> p_110886_) {
      return p_110885_.getName((T)p_110886_);
   }
}
