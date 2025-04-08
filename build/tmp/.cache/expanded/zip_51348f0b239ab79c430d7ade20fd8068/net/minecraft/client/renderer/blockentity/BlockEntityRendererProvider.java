package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface BlockEntityRendererProvider<T extends BlockEntity> {
   BlockEntityRenderer<T> create(BlockEntityRendererProvider.Context p_173571_);

   @OnlyIn(Dist.CLIENT)
   public static class Context {
      private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
      private final BlockRenderDispatcher blockRenderDispatcher;
      private final ItemRenderer itemRenderer;
      private final EntityRenderDispatcher entityRenderer;
      private final EntityModelSet modelSet;
      private final Font font;

      public Context(BlockEntityRenderDispatcher p_234440_, BlockRenderDispatcher p_234441_, ItemRenderer p_234442_, EntityRenderDispatcher p_234443_, EntityModelSet p_234444_, Font p_234445_) {
         this.blockEntityRenderDispatcher = p_234440_;
         this.blockRenderDispatcher = p_234441_;
         this.itemRenderer = p_234442_;
         this.entityRenderer = p_234443_;
         this.modelSet = p_234444_;
         this.font = p_234445_;
      }

      public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
         return this.blockEntityRenderDispatcher;
      }

      public BlockRenderDispatcher getBlockRenderDispatcher() {
         return this.blockRenderDispatcher;
      }

      public EntityRenderDispatcher getEntityRenderer() {
         return this.entityRenderer;
      }

      public ItemRenderer getItemRenderer() {
         return this.itemRenderer;
      }

      public EntityModelSet getModelSet() {
         return this.modelSet;
      }

      public ModelPart bakeLayer(ModelLayerLocation p_173583_) {
         return this.modelSet.bakeLayer(p_173583_);
      }

      public Font getFont() {
         return this.font;
      }
   }
}