package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidTagsProvider extends IntrinsicHolderTagsProvider<Fluid> {
   /** @deprecated Forge: Use the {@linkplain #FluidTagsProvider(PackOutput, CompletableFuture, String, net.minecraftforge.common.data.ExistingFileHelper) mod id variant} */
   @Deprecated
   public FluidTagsProvider(PackOutput p_255941_, CompletableFuture<HolderLookup.Provider> p_256600_) {
      this(p_255941_, p_256600_, "vanilla", null);
   }
   public FluidTagsProvider(PackOutput p_255941_, CompletableFuture<HolderLookup.Provider> p_256600_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
      super(p_255941_, Registries.FLUID, p_256600_, (p_256474_) -> {
         return p_256474_.builtInRegistryHolder().key();
      }, modId, existingFileHelper);
   }

   protected void addTags(HolderLookup.Provider p_256366_) {
      this.tag(FluidTags.WATER).add(Fluids.WATER, Fluids.FLOWING_WATER);
      this.tag(FluidTags.LAVA).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
   }
}
