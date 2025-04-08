package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;

public class NopProcessor extends StructureProcessor {
   public static final Codec<NopProcessor> CODEC = Codec.unit(() -> {
      return NopProcessor.INSTANCE;
   });
   public static final NopProcessor INSTANCE = new NopProcessor();

   private NopProcessor() {
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.NOP;
   }
}