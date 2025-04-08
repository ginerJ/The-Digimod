package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

@OnlyIn(Dist.CLIENT)
public class MultiPartBakedModel implements net.minecraftforge.client.model.IDynamicBakedModel {
   private final List<Pair<Predicate<BlockState>, BakedModel>> selectors;
   protected final boolean hasAmbientOcclusion;
   protected final boolean isGui3d;
   protected final boolean usesBlockLight;
   protected final TextureAtlasSprite particleIcon;
   protected final ItemTransforms transforms;
   protected final ItemOverrides overrides;
   private final Map<BlockState, BitSet> selectorCache = new Object2ObjectOpenCustomHashMap<>(Util.identityStrategy());
   private final BakedModel defaultModel;

   public MultiPartBakedModel(List<Pair<Predicate<BlockState>, BakedModel>> p_119462_) {
      this.selectors = p_119462_;
      BakedModel bakedmodel = p_119462_.iterator().next().getRight();
      this.defaultModel = bakedmodel;
      this.hasAmbientOcclusion = bakedmodel.useAmbientOcclusion();
      this.isGui3d = bakedmodel.isGui3d();
      this.usesBlockLight = bakedmodel.usesBlockLight();
      this.particleIcon = bakedmodel.getParticleIcon();
      this.transforms = bakedmodel.getTransforms();
      this.overrides = bakedmodel.getOverrides();
   }

   public BitSet getSelectors(@Nullable BlockState p_235050_) {
         BitSet bitset = this.selectorCache.get(p_235050_);
         if (bitset == null) {
            bitset = new BitSet();

            for(int i = 0; i < this.selectors.size(); ++i) {
               Pair<Predicate<BlockState>, BakedModel> pair = this.selectors.get(i);
               if (pair.getLeft().test(p_235050_)) {
                  bitset.set(i);
               }
            }

            this.selectorCache.put(p_235050_, bitset);
         }
         return bitset;
   }

   // FORGE: Implement our overloads (here and below) so child models can have custom logic
   public List<BakedQuad> getQuads(@Nullable BlockState p_235050_, @Nullable Direction p_235051_, RandomSource p_235052_, net.minecraftforge.client.model.data.ModelData modelData, @org.jetbrains.annotations.Nullable net.minecraft.client.renderer.RenderType renderType) {
      if (p_235050_ == null) {
         return Collections.emptyList();
      } else {
         BitSet bitset = getSelectors(p_235050_);
         List<List<BakedQuad>> list = Lists.newArrayList();
         long k = p_235052_.nextLong();

         for(int j = 0; j < bitset.length(); ++j) {
            if (bitset.get(j)) {
               var model = this.selectors.get(j).getRight();
               if (renderType == null || model.getRenderTypes(p_235050_, p_235052_, modelData).contains(renderType)) // FORGE: Only put quad data if the model is using the render type passed
               list.add(model.getQuads(p_235050_, p_235051_, RandomSource.create(k), net.minecraftforge.client.model.data.MultipartModelData.resolve(modelData, model), renderType));
            }
         }

         return net.minecraftforge.common.util.ConcatenatedListView.of(list);
      }
   }

   public boolean useAmbientOcclusion() {
      return this.hasAmbientOcclusion;
   }

   public boolean useAmbientOcclusion(BlockState state) {
      return this.defaultModel.useAmbientOcclusion(state);
   }

   public boolean useAmbientOcclusion(BlockState state, net.minecraft.client.renderer.RenderType renderType) {
      return this.defaultModel.useAmbientOcclusion(state, renderType);
   }

   @Override
   public net.minecraftforge.client.model.data.ModelData getModelData(net.minecraft.world.level.BlockAndTintGetter level, net.minecraft.core.BlockPos pos, BlockState state, net.minecraftforge.client.model.data.ModelData modelData) {
      return net.minecraftforge.client.model.data.MultipartModelData.create(selectors, getSelectors(state), level, pos, state, modelData);
   }

   public boolean isGui3d() {
      return this.isGui3d;
   }

   public boolean usesBlockLight() {
      return this.usesBlockLight;
   }

   public boolean isCustomRenderer() {
      return false;
   }

   @Deprecated
   public TextureAtlasSprite getParticleIcon() {
      return this.particleIcon;
   }

   public TextureAtlasSprite getParticleIcon(net.minecraftforge.client.model.data.ModelData modelData) {
      return this.defaultModel.getParticleIcon(modelData);
   }

   @Deprecated
   public ItemTransforms getTransforms() {
      return this.transforms;
   }

   public BakedModel applyTransform(net.minecraft.world.item.ItemDisplayContext transformType, com.mojang.blaze3d.vertex.PoseStack poseStack, boolean applyLeftHandTransform) {
      return this.defaultModel.applyTransform(transformType, poseStack, applyLeftHandTransform);
   }

   @Override // FORGE: Get render types based on the selectors matched by the given block state
   public net.minecraftforge.client.ChunkRenderTypeSet getRenderTypes(@org.jetbrains.annotations.NotNull BlockState state, @org.jetbrains.annotations.NotNull RandomSource rand, @org.jetbrains.annotations.NotNull net.minecraftforge.client.model.data.ModelData data) {
      var renderTypeSets = new java.util.LinkedList<net.minecraftforge.client.ChunkRenderTypeSet>();
      var selectors = getSelectors(state);
      for (int i = 0; i < selectors.length(); i++)
         if (selectors.get(i))
            renderTypeSets.add(this.selectors.get(i).getRight().getRenderTypes(state, rand, data));
      return net.minecraftforge.client.ChunkRenderTypeSet.union(renderTypeSets);
   }

   public ItemOverrides getOverrides() {
      return this.overrides;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<Pair<Predicate<BlockState>, BakedModel>> selectors = Lists.newArrayList();

      public void add(Predicate<BlockState> p_119478_, BakedModel p_119479_) {
         this.selectors.add(Pair.of(p_119478_, p_119479_));
      }

      public BakedModel build() {
         return new MultiPartBakedModel(this.selectors);
      }
   }
}
