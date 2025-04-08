package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

public class Clear implements RuleBlockEntityModifier {
   private static final Clear INSTANCE = new Clear();
   public static final Codec<Clear> CODEC = Codec.unit(INSTANCE);

   public CompoundTag apply(RandomSource p_277601_, @Nullable CompoundTag p_277931_) {
      return new CompoundTag();
   }

   public RuleBlockEntityModifierType<?> getType() {
      return RuleBlockEntityModifierType.CLEAR;
   }
}