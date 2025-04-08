package net.minecraft.data.advancements;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

/**
 * @deprecated Forge: Use {@link net.minecraftforge.common.data.ForgeAdvancementProvider} instead,
 *             provides ease of access for the {@link net.minecraftforge.common.data.ExistingFileHelper} in the generator
 */
@Deprecated
public class AdvancementProvider implements DataProvider {
   private final PackOutput.PathProvider pathProvider;
   private final List<AdvancementSubProvider> subProviders;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public AdvancementProvider(PackOutput p_256529_, CompletableFuture<HolderLookup.Provider> p_255722_, List<AdvancementSubProvider> p_255883_) {
      this.pathProvider = p_256529_.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
      this.subProviders = p_255883_;
      this.registries = p_255722_;
   }

   public CompletableFuture<?> run(CachedOutput p_254268_) {
      return this.registries.thenCompose((p_255484_) -> {
         Set<ResourceLocation> set = new HashSet<>();
         List<CompletableFuture<?>> list = new ArrayList<>();
         Consumer<Advancement> consumer = (p_253397_) -> {
            if (!set.add(p_253397_.getId())) {
               throw new IllegalStateException("Duplicate advancement " + p_253397_.getId());
            } else {
               Path path = this.pathProvider.json(p_253397_.getId());
               list.add(DataProvider.saveStable(p_254268_, p_253397_.deconstruct().serializeToJson(), path));
            }
         };

         for(AdvancementSubProvider advancementsubprovider : this.subProviders) {
            advancementsubprovider.generate(p_255484_, consumer);
         }

         return CompletableFuture.allOf(list.toArray((p_253393_) -> {
            return new CompletableFuture[p_253393_];
         }));
      });
   }

   public final String getName() {
      return "Advancements";
   }
}
