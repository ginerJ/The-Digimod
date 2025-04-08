package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.CatVariantTags;
import net.minecraft.world.entity.animal.CatVariant;

public class CatVariantTagsProvider extends TagsProvider<CatVariant> {
   /** @deprecated Forge: Use the {@linkplain #CatVariantTagsProvider(PackOutput, CompletableFuture, String, net.minecraftforge.common.data.ExistingFileHelper) mod id variant} */
   @Deprecated
   public CatVariantTagsProvider(PackOutput p_256547_, CompletableFuture<HolderLookup.Provider> p_256090_) {
      super(p_256547_, Registries.CAT_VARIANT, p_256090_);
   }
   public CatVariantTagsProvider(PackOutput p_256547_, CompletableFuture<HolderLookup.Provider> p_256090_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
      super(p_256547_, Registries.CAT_VARIANT, p_256090_, modId, existingFileHelper);
   }

   protected void addTags(HolderLookup.Provider p_256078_) {
      this.tag(CatVariantTags.DEFAULT_SPAWNS).add(CatVariant.TABBY, CatVariant.BLACK, CatVariant.RED, CatVariant.SIAMESE, CatVariant.BRITISH_SHORTHAIR, CatVariant.CALICO, CatVariant.PERSIAN, CatVariant.RAGDOLL, CatVariant.WHITE, CatVariant.JELLIE);
      this.tag(CatVariantTags.FULL_MOON_SPAWNS).addTag(CatVariantTags.DEFAULT_SPAWNS).add(CatVariant.ALL_BLACK);
   }
}
