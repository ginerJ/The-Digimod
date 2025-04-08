package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class RootPlacerType<P extends RootPlacer> {
   public static final RootPlacerType<MangroveRootPlacer> MANGROVE_ROOT_PLACER = register("mangrove_root_placer", MangroveRootPlacer.CODEC);
   private final Codec<P> codec;

   private static <P extends RootPlacer> RootPlacerType<P> register(String p_225905_, Codec<P> p_225906_) {
      return Registry.register(BuiltInRegistries.ROOT_PLACER_TYPE, p_225905_, new RootPlacerType<>(p_225906_));
   }

   public RootPlacerType(Codec<P> p_225902_) {
      this.codec = p_225902_;
   }

   public Codec<P> codec() {
      return this.codec;
   }
}