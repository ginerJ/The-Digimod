package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.decoration.PaintingVariants;

public class PaintingVariantTagsProvider extends TagsProvider<PaintingVariant> {
   /** @deprecated Forge: Use the {@linkplain #PaintingVariantTagsProvider(PackOutput, CompletableFuture, String, net.minecraftforge.common.data.ExistingFileHelper) mod id variant} */
   @Deprecated
   public PaintingVariantTagsProvider(PackOutput p_255750_, CompletableFuture<HolderLookup.Provider> p_256184_) {
      super(p_255750_, Registries.PAINTING_VARIANT, p_256184_);
   }
   public PaintingVariantTagsProvider(PackOutput p_255750_, CompletableFuture<HolderLookup.Provider> p_256184_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
      super(p_255750_, Registries.PAINTING_VARIANT, p_256184_, modId, existingFileHelper);
   }

   protected void addTags(HolderLookup.Provider p_256017_) {
      this.tag(PaintingVariantTags.PLACEABLE).add(PaintingVariants.KEBAB, PaintingVariants.AZTEC, PaintingVariants.ALBAN, PaintingVariants.AZTEC2, PaintingVariants.BOMB, PaintingVariants.PLANT, PaintingVariants.WASTELAND, PaintingVariants.POOL, PaintingVariants.COURBET, PaintingVariants.SEA, PaintingVariants.SUNSET, PaintingVariants.CREEBET, PaintingVariants.WANDERER, PaintingVariants.GRAHAM, PaintingVariants.MATCH, PaintingVariants.BUST, PaintingVariants.STAGE, PaintingVariants.VOID, PaintingVariants.SKULL_AND_ROSES, PaintingVariants.WITHER, PaintingVariants.FIGHTERS, PaintingVariants.POINTER, PaintingVariants.PIGSCENE, PaintingVariants.BURNING_SKULL, PaintingVariants.SKELETON, PaintingVariants.DONKEY_KONG);
   }
}
