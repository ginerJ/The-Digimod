package net.minecraft.client.searchtree;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ResourceLocationSearchTree<T> {
   static <T> ResourceLocationSearchTree<T> empty() {
      return new ResourceLocationSearchTree<T>() {
         public List<T> searchNamespace(String p_235218_) {
            return List.of();
         }

         public List<T> searchPath(String p_235220_) {
            return List.of();
         }
      };
   }

   static <T> ResourceLocationSearchTree<T> create(List<T> p_235213_, Function<T, Stream<ResourceLocation>> p_235214_) {
      if (p_235213_.isEmpty()) {
         return empty();
      } else {
         final SuffixArray<T> suffixarray = new SuffixArray<>();
         final SuffixArray<T> suffixarray1 = new SuffixArray<>();

         for(T t : p_235213_) {
            p_235214_.apply(t).forEach((p_235210_) -> {
               suffixarray.add(t, p_235210_.getNamespace().toLowerCase(Locale.ROOT));
               suffixarray1.add(t, p_235210_.getPath().toLowerCase(Locale.ROOT));
            });
         }

         suffixarray.generate();
         suffixarray1.generate();
         return new ResourceLocationSearchTree<T>() {
            public List<T> searchNamespace(String p_235227_) {
               return suffixarray.search(p_235227_);
            }

            public List<T> searchPath(String p_235229_) {
               return suffixarray1.search(p_235229_);
            }
         };
      }
   }

   List<T> searchNamespace(String p_235211_);

   List<T> searchPath(String p_235215_);
}