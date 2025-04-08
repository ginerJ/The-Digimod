package net.minecraft.client.renderer.texture.atlas;

import com.mojang.serialization.Codec;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record SpriteSourceType(Codec<? extends SpriteSource> codec) {
}