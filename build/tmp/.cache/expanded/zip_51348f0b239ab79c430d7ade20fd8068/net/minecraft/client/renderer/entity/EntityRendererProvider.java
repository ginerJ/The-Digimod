package net.minecraft.client.renderer.entity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface EntityRendererProvider<T extends Entity> {
   EntityRenderer<T> create(EntityRendererProvider.Context p_174010_);

   @OnlyIn(Dist.CLIENT)
   public static class Context {
      private final EntityRenderDispatcher entityRenderDispatcher;
      private final ItemRenderer itemRenderer;
      private final BlockRenderDispatcher blockRenderDispatcher;
      private final ItemInHandRenderer itemInHandRenderer;
      private final ResourceManager resourceManager;
      private final EntityModelSet modelSet;
      private final Font font;

      public Context(EntityRenderDispatcher p_234590_, ItemRenderer p_234591_, BlockRenderDispatcher p_234592_, ItemInHandRenderer p_234593_, ResourceManager p_234594_, EntityModelSet p_234595_, Font p_234596_) {
         this.entityRenderDispatcher = p_234590_;
         this.itemRenderer = p_234591_;
         this.blockRenderDispatcher = p_234592_;
         this.itemInHandRenderer = p_234593_;
         this.resourceManager = p_234594_;
         this.modelSet = p_234595_;
         this.font = p_234596_;
      }

      public EntityRenderDispatcher getEntityRenderDispatcher() {
         return this.entityRenderDispatcher;
      }

      public ItemRenderer getItemRenderer() {
         return this.itemRenderer;
      }

      public BlockRenderDispatcher getBlockRenderDispatcher() {
         return this.blockRenderDispatcher;
      }

      public ItemInHandRenderer getItemInHandRenderer() {
         return this.itemInHandRenderer;
      }

      public ResourceManager getResourceManager() {
         return this.resourceManager;
      }

      public EntityModelSet getModelSet() {
         return this.modelSet;
      }

      public ModelManager getModelManager() {
         return this.blockRenderDispatcher.getBlockModelShaper().getModelManager();
      }

      public ModelPart bakeLayer(ModelLayerLocation p_174024_) {
         return this.modelSet.bakeLayer(p_174024_);
      }

      public Font getFont() {
         return this.font;
      }
   }
}