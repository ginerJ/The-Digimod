package net.minecraft.client.gui.screens;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FaviconTexture implements AutoCloseable {
   private static final ResourceLocation MISSING_LOCATION = new ResourceLocation("textures/misc/unknown_server.png");
   private static final int WIDTH = 64;
   private static final int HEIGHT = 64;
   private final TextureManager textureManager;
   private final ResourceLocation textureLocation;
   @Nullable
   private DynamicTexture texture;
   private boolean closed;

   private FaviconTexture(TextureManager p_289556_, ResourceLocation p_289549_) {
      this.textureManager = p_289556_;
      this.textureLocation = p_289549_;
   }

   public static FaviconTexture forWorld(TextureManager p_289550_, String p_289565_) {
      return new FaviconTexture(p_289550_, new ResourceLocation("minecraft", "worlds/" + Util.sanitizeName(p_289565_, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(p_289565_) + "/icon"));
   }

   public static FaviconTexture forServer(TextureManager p_289553_, String p_289535_) {
      return new FaviconTexture(p_289553_, new ResourceLocation("minecraft", "servers/" + Hashing.sha1().hashUnencodedChars(p_289535_) + "/icon"));
   }

   public void upload(NativeImage p_289543_) {
      if (p_289543_.getWidth() == 64 && p_289543_.getHeight() == 64) {
         try {
            this.checkOpen();
            if (this.texture == null) {
               this.texture = new DynamicTexture(p_289543_);
            } else {
               this.texture.setPixels(p_289543_);
               this.texture.upload();
            }

            this.textureManager.register(this.textureLocation, this.texture);
         } catch (Throwable throwable) {
            p_289543_.close();
            this.clear();
            throw throwable;
         }
      } else {
         p_289543_.close();
         throw new IllegalArgumentException("Icon must be 64x64, but was " + p_289543_.getWidth() + "x" + p_289543_.getHeight());
      }
   }

   public void clear() {
      this.checkOpen();
      if (this.texture != null) {
         this.textureManager.release(this.textureLocation);
         this.texture.close();
         this.texture = null;
      }

   }

   public ResourceLocation textureLocation() {
      return this.texture != null ? this.textureLocation : MISSING_LOCATION;
   }

   public void close() {
      this.clear();
      this.closed = true;
   }

   private void checkOpen() {
      if (this.closed) {
         throw new IllegalStateException("Icon already closed");
      }
   }
}