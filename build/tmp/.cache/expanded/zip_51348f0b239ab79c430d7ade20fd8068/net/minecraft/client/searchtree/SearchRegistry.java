package net.minecraft.client.searchtree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchRegistry implements ResourceManagerReloadListener {
   public static final SearchRegistry.Key<ItemStack> CREATIVE_NAMES = new SearchRegistry.Key<>();
   public static final SearchRegistry.Key<ItemStack> CREATIVE_TAGS = new SearchRegistry.Key<>();
   public static final SearchRegistry.Key<RecipeCollection> RECIPE_COLLECTIONS = new SearchRegistry.Key<>();
   private final Map<SearchRegistry.Key<?>, SearchRegistry.TreeEntry<?>> searchTrees = new HashMap<>();

   public void onResourceManagerReload(ResourceManager p_119948_) {
      for(SearchRegistry.TreeEntry<?> treeentry : this.searchTrees.values()) {
         treeentry.refresh();
      }

   }

   public <T> void register(SearchRegistry.Key<T> p_235233_, SearchRegistry.TreeBuilderSupplier<T> p_235234_) {
      this.searchTrees.put(p_235233_, new SearchRegistry.TreeEntry<>(p_235234_));
   }

   private <T> SearchRegistry.TreeEntry<T> getSupplier(SearchRegistry.Key<T> p_235239_) {
      SearchRegistry.TreeEntry<T> treeentry = (SearchRegistry.TreeEntry<T>)this.searchTrees.get(p_235239_);
      if (treeentry == null) {
         throw new IllegalStateException("Tree builder not registered");
      } else {
         return treeentry;
      }
   }

   public <T> void populate(SearchRegistry.Key<T> p_235236_, List<T> p_235237_) {
      this.getSupplier(p_235236_).populate(p_235237_);
   }

   public <T> SearchTree<T> getTree(SearchRegistry.Key<T> p_235231_) {
      return this.getSupplier(p_235231_).tree;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Key<T> {
   }

   @OnlyIn(Dist.CLIENT)
   public interface TreeBuilderSupplier<T> extends Function<List<T>, RefreshableSearchTree<T>> {
   }

   @OnlyIn(Dist.CLIENT)
   static class TreeEntry<T> {
      private final SearchRegistry.TreeBuilderSupplier<T> factory;
      RefreshableSearchTree<T> tree = RefreshableSearchTree.empty();

      TreeEntry(SearchRegistry.TreeBuilderSupplier<T> p_235243_) {
         this.factory = p_235243_;
      }

      void populate(List<T> p_235246_) {
         this.tree = this.factory.apply(p_235246_);
         this.tree.refresh();
      }

      void refresh() {
         this.tree.refresh();
      }
   }
}