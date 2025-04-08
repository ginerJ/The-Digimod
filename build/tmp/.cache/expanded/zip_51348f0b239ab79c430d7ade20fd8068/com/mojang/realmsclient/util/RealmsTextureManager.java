package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsTextureManager {
   private static final Map<String, RealmsTextureManager.RealmsTexture> TEXTURES = Maps.newHashMap();
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation TEMPLATE_ICON_LOCATION = new ResourceLocation("textures/gui/presets/isles.png");

   public static ResourceLocation worldTemplate(String p_270945_, @Nullable String p_270612_) {
      return p_270612_ == null ? TEMPLATE_ICON_LOCATION : getTexture(p_270945_, p_270612_);
   }

   private static ResourceLocation getTexture(String p_90197_, String p_90198_) {
      RealmsTextureManager.RealmsTexture realmstexturemanager$realmstexture = TEXTURES.get(p_90197_);
      if (realmstexturemanager$realmstexture != null && realmstexturemanager$realmstexture.image().equals(p_90198_)) {
         return realmstexturemanager$realmstexture.textureId;
      } else {
         NativeImage nativeimage = loadImage(p_90198_);
         if (nativeimage == null) {
            ResourceLocation resourcelocation1 = MissingTextureAtlasSprite.getLocation();
            TEXTURES.put(p_90197_, new RealmsTextureManager.RealmsTexture(p_90198_, resourcelocation1));
            return resourcelocation1;
         } else {
            ResourceLocation resourcelocation = new ResourceLocation("realms", "dynamic/" + p_90197_);
            Minecraft.getInstance().getTextureManager().register(resourcelocation, new DynamicTexture(nativeimage));
            TEXTURES.put(p_90197_, new RealmsTextureManager.RealmsTexture(p_90198_, resourcelocation));
            return resourcelocation;
         }
      }
   }

   @Nullable
   private static NativeImage loadImage(String p_270725_) {
      byte[] abyte = Base64.getDecoder().decode(p_270725_);
      ByteBuffer bytebuffer = MemoryUtil.memAlloc(abyte.length);

      try {
         return NativeImage.read(bytebuffer.put(abyte).flip());
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to load world image: {}", p_270725_, ioexception);
      } finally {
         MemoryUtil.memFree(bytebuffer);
      }

      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public static record RealmsTexture(String image, ResourceLocation textureId) {
   }
}