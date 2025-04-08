package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

public class AppendStatic implements RuleBlockEntityModifier {
   public static final Codec<AppendStatic> CODEC = RecordCodecBuilder.create((p_277505_) -> {
      return p_277505_.group(CompoundTag.CODEC.fieldOf("data").forGetter((p_278105_) -> {
         return p_278105_.tag;
      })).apply(p_277505_, AppendStatic::new);
   });
   private final CompoundTag tag;

   public AppendStatic(CompoundTag p_277900_) {
      this.tag = p_277900_;
   }

   public CompoundTag apply(RandomSource p_277835_, @Nullable CompoundTag p_277892_) {
      return p_277892_ == null ? this.tag.copy() : p_277892_.merge(this.tag);
   }

   public RuleBlockEntityModifierType<?> getType() {
      return RuleBlockEntityModifierType.APPEND_STATIC;
   }
}