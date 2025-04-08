package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

public class Passthrough implements RuleBlockEntityModifier {
   public static final Passthrough INSTANCE = new Passthrough();
   public static final Codec<Passthrough> CODEC = Codec.unit(INSTANCE);

   @Nullable
   public CompoundTag apply(RandomSource p_277737_, @Nullable CompoundTag p_277665_) {
      return p_277665_;
   }

   public RuleBlockEntityModifierType<?> getType() {
      return RuleBlockEntityModifierType.PASSTHROUGH;
   }
}