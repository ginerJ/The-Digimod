package net.minecraft.client.searchtree;

import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface RefreshableSearchTree<T> extends SearchTree<T> {
   static <T> RefreshableSearchTree<T> empty() {
      return (p_235203_) -> {
         return List.of();
      };
   }

   default void refresh() {
   }
}