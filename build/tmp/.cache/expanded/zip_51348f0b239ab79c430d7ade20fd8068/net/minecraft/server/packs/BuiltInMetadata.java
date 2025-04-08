package net.minecraft.server.packs;

import java.util.Map;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public class BuiltInMetadata {
   private static final BuiltInMetadata EMPTY = new BuiltInMetadata(Map.of());
   private final Map<MetadataSectionSerializer<?>, ?> values;

   private BuiltInMetadata(Map<MetadataSectionSerializer<?>, ?> p_251588_) {
      this.values = p_251588_;
   }

   public <T> T get(MetadataSectionSerializer<T> p_251597_) {
      return (T)this.values.get(p_251597_);
   }

   public static BuiltInMetadata of() {
      return EMPTY;
   }

   public static <T> BuiltInMetadata of(MetadataSectionSerializer<T> p_248992_, T p_249997_) {
      return new BuiltInMetadata(Map.of(p_248992_, p_249997_));
   }

   public static <T1, T2> BuiltInMetadata of(MetadataSectionSerializer<T1> p_252035_, T1 p_252174_, MetadataSectionSerializer<T2> p_249734_, T2 p_250020_) {
      return new BuiltInMetadata(Map.of(p_252035_, p_252174_, p_249734_, (T1)p_250020_));
   }
}