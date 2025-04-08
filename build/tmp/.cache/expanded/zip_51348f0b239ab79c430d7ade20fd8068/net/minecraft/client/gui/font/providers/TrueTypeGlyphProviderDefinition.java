package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public record TrueTypeGlyphProviderDefinition(ResourceLocation location, float size, float oversample, TrueTypeGlyphProviderDefinition.Shift shift, String skip) implements GlyphProviderDefinition {
   private static final Codec<String> SKIP_LIST_CODEC = Codec.either(Codec.STRING, Codec.STRING.listOf()).xmap((p_286728_) -> {
      return p_286728_.map((p_286306_) -> {
         return p_286306_;
      }, (p_286852_) -> {
         return String.join("", p_286852_);
      });
   }, Either::left);
   public static final MapCodec<TrueTypeGlyphProviderDefinition> CODEC = RecordCodecBuilder.mapCodec((p_286284_) -> {
      return p_286284_.group(ResourceLocation.CODEC.fieldOf("file").forGetter(TrueTypeGlyphProviderDefinition::location), Codec.FLOAT.optionalFieldOf("size", Float.valueOf(11.0F)).forGetter(TrueTypeGlyphProviderDefinition::size), Codec.FLOAT.optionalFieldOf("oversample", Float.valueOf(1.0F)).forGetter(TrueTypeGlyphProviderDefinition::oversample), TrueTypeGlyphProviderDefinition.Shift.CODEC.optionalFieldOf("shift", TrueTypeGlyphProviderDefinition.Shift.NONE).forGetter(TrueTypeGlyphProviderDefinition::shift), SKIP_LIST_CODEC.optionalFieldOf("skip", "").forGetter(TrueTypeGlyphProviderDefinition::skip)).apply(p_286284_, TrueTypeGlyphProviderDefinition::new);
   });

   public GlyphProviderType type() {
      return GlyphProviderType.TTF;
   }

   public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
      return Either.left(this::load);
   }

   private GlyphProvider load(ResourceManager p_286229_) throws IOException {
      STBTTFontinfo stbttfontinfo = null;
      ByteBuffer bytebuffer = null;

      try (InputStream inputstream = p_286229_.open(this.location.withPrefix("font/"))) {
         stbttfontinfo = STBTTFontinfo.malloc();
         bytebuffer = TextureUtil.readResource(inputstream);
         bytebuffer.flip();
         if (!STBTruetype.stbtt_InitFont(stbttfontinfo, bytebuffer)) {
            throw new IOException("Invalid ttf");
         } else {
            return new TrueTypeGlyphProvider(bytebuffer, stbttfontinfo, this.size, this.oversample, this.shift.x, this.shift.y, this.skip);
         }
      } catch (Exception exception) {
         if (stbttfontinfo != null) {
            stbttfontinfo.free();
         }

         MemoryUtil.memFree(bytebuffer);
         throw exception;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static record Shift(float x, float y) {
      public static final TrueTypeGlyphProviderDefinition.Shift NONE = new TrueTypeGlyphProviderDefinition.Shift(0.0F, 0.0F);
      public static final Codec<TrueTypeGlyphProviderDefinition.Shift> CODEC = Codec.FLOAT.listOf().comapFlatMap((p_286374_) -> {
         return Util.fixedSize(p_286374_, 2).map((p_286746_) -> {
            return new TrueTypeGlyphProviderDefinition.Shift(p_286746_.get(0), p_286746_.get(1));
         });
      }, (p_286274_) -> {
         return List.of(p_286274_.x, p_286274_.y);
      });
   }
}