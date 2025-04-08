package net.minecraft.client.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TextureAtlasHolder implements PreparableReloadListener, AutoCloseable {
   protected final TextureAtlas textureAtlas;
   private final ResourceLocation atlasInfoLocation;

   public TextureAtlasHolder(TextureManager p_262057_, ResourceLocation p_261554_, ResourceLocation p_262147_) {
      this.atlasInfoLocation = p_262147_;
      this.textureAtlas = new TextureAtlas(p_261554_);
      p_262057_.register(this.textureAtlas.location(), this.textureAtlas);
   }

   protected TextureAtlasSprite getSprite(ResourceLocation p_118902_) {
      return this.textureAtlas.getSprite(p_118902_);
   }

   public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_249641_, ResourceManager p_250036_, ProfilerFiller p_249806_, ProfilerFiller p_250732_, Executor p_249427_, Executor p_250510_) {
      return SpriteLoader.create(this.textureAtlas).loadAndStitch(p_250036_, this.atlasInfoLocation, 0, p_249427_).thenCompose(SpriteLoader.Preparations::waitForUpload).thenCompose(p_249641_::wait).thenAcceptAsync((p_249246_) -> {
         this.apply(p_249246_, p_250732_);
      }, p_250510_);
   }

   private void apply(SpriteLoader.Preparations p_252333_, ProfilerFiller p_250624_) {
      p_250624_.startTick();
      p_250624_.push("upload");
      this.textureAtlas.upload(p_252333_);
      p_250624_.pop();
      p_250624_.endTick();
   }

   public void close() {
      this.textureAtlas.clearTextureData();
   }
}