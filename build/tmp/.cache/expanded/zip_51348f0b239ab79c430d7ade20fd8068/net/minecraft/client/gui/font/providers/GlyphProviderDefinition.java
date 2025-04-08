package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.io.IOException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface GlyphProviderDefinition {
   Codec<GlyphProviderDefinition> CODEC = GlyphProviderType.CODEC.dispatch(GlyphProviderDefinition::type, (p_286256_) -> {
      return p_286256_.mapCodec().codec();
   });

   GlyphProviderType type();

   Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack();

   @OnlyIn(Dist.CLIENT)
   public interface Loader {
      GlyphProvider load(ResourceManager p_286639_) throws IOException;
   }

   @OnlyIn(Dist.CLIENT)
   public static record Reference(ResourceLocation id) {
   }
}