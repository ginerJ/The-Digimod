package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleBakedModel implements BakedModel {
   protected final List<BakedQuad> unculledFaces;
   protected final Map<Direction, List<BakedQuad>> culledFaces;
   protected final boolean hasAmbientOcclusion;
   protected final boolean isGui3d;
   protected final boolean usesBlockLight;
   protected final TextureAtlasSprite particleIcon;
   protected final ItemTransforms transforms;
   protected final ItemOverrides overrides;
   protected final net.minecraftforge.client.ChunkRenderTypeSet blockRenderTypes;
   protected final List<net.minecraft.client.renderer.RenderType> itemRenderTypes;
   protected final List<net.minecraft.client.renderer.RenderType> fabulousItemRenderTypes;

   /** @deprecated Forge: Use {@linkplain #SimpleBakedModel(List, Map, boolean, boolean, boolean, TextureAtlasSprite, ItemTransforms, ItemOverrides, net.minecraftforge.client.RenderTypeGroup) variant with RenderTypeGroup} **/
   @Deprecated
   public SimpleBakedModel(List<BakedQuad> p_119489_, Map<Direction, List<BakedQuad>> p_119490_, boolean p_119491_, boolean p_119492_, boolean p_119493_, TextureAtlasSprite p_119494_, ItemTransforms p_119495_, ItemOverrides p_119496_) {
      this(p_119489_, p_119490_, p_119491_, p_119492_, p_119493_, p_119494_, p_119495_, p_119496_, net.minecraftforge.client.RenderTypeGroup.EMPTY);
   }

   public SimpleBakedModel(List<BakedQuad> p_119489_, Map<Direction, List<BakedQuad>> p_119490_, boolean p_119491_, boolean p_119492_, boolean p_119493_, TextureAtlasSprite p_119494_, ItemTransforms p_119495_, ItemOverrides p_119496_, net.minecraftforge.client.RenderTypeGroup renderTypes) {
      this.unculledFaces = p_119489_;
      this.culledFaces = p_119490_;
      this.hasAmbientOcclusion = p_119491_;
      this.isGui3d = p_119493_;
      this.usesBlockLight = p_119492_;
      this.particleIcon = p_119494_;
      this.transforms = p_119495_;
      this.overrides = p_119496_;
      this.blockRenderTypes = !renderTypes.isEmpty() ? net.minecraftforge.client.ChunkRenderTypeSet.of(renderTypes.block()) : null;
      this.itemRenderTypes = !renderTypes.isEmpty() ? List.of(renderTypes.entity()) : null;
      this.fabulousItemRenderTypes = !renderTypes.isEmpty() ? List.of(renderTypes.entityFabulous()) : null;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState p_235054_, @Nullable Direction p_235055_, RandomSource p_235056_) {
      return p_235055_ == null ? this.unculledFaces : this.culledFaces.get(p_235055_);
   }

   public boolean useAmbientOcclusion() {
      return this.hasAmbientOcclusion;
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

   public TextureAtlasSprite getParticleIcon() {
      return this.particleIcon;
   }

   public ItemTransforms getTransforms() {
      return this.transforms;
   }

   public ItemOverrides getOverrides() {
      return this.overrides;
   }

   @Override
   public net.minecraftforge.client.ChunkRenderTypeSet getRenderTypes(@org.jetbrains.annotations.NotNull BlockState state, @org.jetbrains.annotations.NotNull RandomSource rand, @org.jetbrains.annotations.NotNull net.minecraftforge.client.model.data.ModelData data) {
      if (blockRenderTypes != null)
         return blockRenderTypes;
      return BakedModel.super.getRenderTypes(state, rand, data);
   }

   @Override
   public List<net.minecraft.client.renderer.RenderType> getRenderTypes(net.minecraft.world.item.ItemStack itemStack, boolean fabulous) {
      if (!fabulous) {
         if (itemRenderTypes != null)
             return itemRenderTypes;
      } else {
         if (fabulousItemRenderTypes != null)
             return fabulousItemRenderTypes;
      }
      return BakedModel.super.getRenderTypes(itemStack, fabulous);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<BakedQuad> unculledFaces = Lists.newArrayList();
      private final Map<Direction, List<BakedQuad>> culledFaces = Maps.newEnumMap(Direction.class);
      private final ItemOverrides overrides;
      private final boolean hasAmbientOcclusion;
      private TextureAtlasSprite particleIcon;
      private final boolean usesBlockLight;
      private final boolean isGui3d;
      private final ItemTransforms transforms;

      public Builder(BlockModel p_119517_, ItemOverrides p_119518_, boolean p_119519_) {
         this(p_119517_.hasAmbientOcclusion(), p_119517_.getGuiLight().lightLikeBlock(), p_119519_, p_119517_.getTransforms(), p_119518_);
      }

      public Builder(boolean p_119521_, boolean p_119522_, boolean p_119523_, ItemTransforms p_119524_, ItemOverrides p_119525_) {
         for(Direction direction : Direction.values()) {
            this.culledFaces.put(direction, Lists.newArrayList());
         }

         this.overrides = p_119525_;
         this.hasAmbientOcclusion = p_119521_;
         this.usesBlockLight = p_119522_;
         this.isGui3d = p_119523_;
         this.transforms = p_119524_;
      }

      public SimpleBakedModel.Builder addCulledFace(Direction p_119531_, BakedQuad p_119532_) {
         this.culledFaces.get(p_119531_).add(p_119532_);
         return this;
      }

      public SimpleBakedModel.Builder addUnculledFace(BakedQuad p_119527_) {
         this.unculledFaces.add(p_119527_);
         return this;
      }

      public SimpleBakedModel.Builder particle(TextureAtlasSprite p_119529_) {
         this.particleIcon = p_119529_;
         return this;
      }

      public SimpleBakedModel.Builder item() {
         return this;
      }

      /** @deprecated Forge: Use {@linkplain #build(net.minecraftforge.client.RenderTypeGroup) variant with RenderTypeGroup} **/
      @Deprecated
      public BakedModel build() {
         return build(net.minecraftforge.client.RenderTypeGroup.EMPTY);
      }

      public BakedModel build(net.minecraftforge.client.RenderTypeGroup renderTypes) {
         if (this.particleIcon == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides, renderTypes);
         }
      }
   }
}
