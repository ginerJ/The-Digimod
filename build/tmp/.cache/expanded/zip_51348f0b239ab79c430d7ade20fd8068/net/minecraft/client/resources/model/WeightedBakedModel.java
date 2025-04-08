package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WeightedBakedModel implements net.minecraftforge.client.model.IDynamicBakedModel {
   private final int totalWeight;
   private final List<WeightedEntry.Wrapper<BakedModel>> list;
   private final BakedModel wrapped;

   public WeightedBakedModel(List<WeightedEntry.Wrapper<BakedModel>> p_119544_) {
      this.list = p_119544_;
      this.totalWeight = WeightedRandom.getTotalWeight(p_119544_);
      this.wrapped = p_119544_.get(0).getData();
   }

   // FORGE: Implement our overloads (here and below) so child models can have custom logic
   public List<BakedQuad> getQuads(@Nullable BlockState p_235058_, @Nullable Direction p_235059_, RandomSource p_235060_, net.minecraftforge.client.model.data.ModelData modelData, @org.jetbrains.annotations.Nullable net.minecraft.client.renderer.RenderType renderType) {
      return WeightedRandom.getWeightedItem(this.list, Math.abs((int)p_235060_.nextLong()) % this.totalWeight).map((p_235065_) -> {
         return p_235065_.getData().getQuads(p_235058_, p_235059_, p_235060_, modelData, renderType);
      }).orElse(Collections.emptyList());
   }

   public boolean useAmbientOcclusion() {
      return this.wrapped.useAmbientOcclusion();
   }

   @Override
   public boolean useAmbientOcclusion(BlockState state) {
      return this.wrapped.useAmbientOcclusion(state);
   }

   @Override
   public boolean useAmbientOcclusion(BlockState state, net.minecraft.client.renderer.RenderType renderType) {
      return this.wrapped.useAmbientOcclusion(state, renderType);
   }

   public boolean isGui3d() {
      return this.wrapped.isGui3d();
   }

   public boolean usesBlockLight() {
      return this.wrapped.usesBlockLight();
   }

   public boolean isCustomRenderer() {
      return this.wrapped.isCustomRenderer();
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.wrapped.getParticleIcon();
   }

   public TextureAtlasSprite getParticleIcon(net.minecraftforge.client.model.data.ModelData modelData) {
      return this.wrapped.getParticleIcon(modelData);
   }

   public ItemTransforms getTransforms() {
      return this.wrapped.getTransforms();
   }

   public BakedModel applyTransform(net.minecraft.world.item.ItemDisplayContext transformType, com.mojang.blaze3d.vertex.PoseStack poseStack, boolean applyLeftHandTransform) {
      return this.wrapped.applyTransform(transformType, poseStack, applyLeftHandTransform);
   }

   @Override // FORGE: Get render types based on the active weighted model
   public net.minecraftforge.client.ChunkRenderTypeSet getRenderTypes(@org.jetbrains.annotations.NotNull BlockState state, @org.jetbrains.annotations.NotNull RandomSource rand, @org.jetbrains.annotations.NotNull net.minecraftforge.client.model.data.ModelData data) {
      return WeightedRandom.getWeightedItem(this.list, Math.abs((int)rand.nextLong()) % this.totalWeight)
              .map((p_235065_) -> p_235065_.getData().getRenderTypes(state, rand, data))
              .orElse(net.minecraftforge.client.ChunkRenderTypeSet.none());
   }

   public ItemOverrides getOverrides() {
      return this.wrapped.getOverrides();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<WeightedEntry.Wrapper<BakedModel>> list = Lists.newArrayList();

      public WeightedBakedModel.Builder add(@Nullable BakedModel p_119560_, int p_119561_) {
         if (p_119560_ != null) {
            this.list.add(WeightedEntry.wrap(p_119560_, p_119561_));
         }

         return this;
      }

      @Nullable
      public BakedModel build() {
         if (this.list.isEmpty()) {
            return null;
         } else {
            return (BakedModel)(this.list.size() == 1 ? this.list.get(0).getData() : new WeightedBakedModel(this.list));
         }
      }
   }
}
