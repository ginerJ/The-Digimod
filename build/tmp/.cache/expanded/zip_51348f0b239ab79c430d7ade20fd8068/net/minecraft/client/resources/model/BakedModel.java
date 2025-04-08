package net.minecraft.client.resources.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface BakedModel extends net.minecraftforge.client.extensions.IForgeBakedModel {
   /**@deprecated Forge: Use {@link net.minecraftforge.client.extensions.IForgeBakedModel#getQuads(BlockState, Direction, RandomSource, net.minecraftforge.client.model.data.ModelData, net.minecraft.client.renderer.RenderType)}*/
   @Deprecated
   List<BakedQuad> getQuads(@Nullable BlockState p_235039_, @Nullable Direction p_235040_, RandomSource p_235041_);

   boolean useAmbientOcclusion();

   boolean isGui3d();

   boolean usesBlockLight();

   boolean isCustomRenderer();

   /**@deprecated Forge: Use {@link net.minecraftforge.client.extensions.IForgeBakedModel#getParticleIcon(net.minecraftforge.client.model.data.ModelData)}*/
   @Deprecated
   TextureAtlasSprite getParticleIcon();

   /**@deprecated Forge: Use {@link net.minecraftforge.client.extensions.IForgeBakedModel#applyTransform(ItemTransforms.TransformType, com.mojang.blaze3d.vertex.PoseStack, boolean)} instead */
   @Deprecated
   default ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }

   ItemOverrides getOverrides();
}
