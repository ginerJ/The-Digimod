package net.minecraft.client.resources.model;

import com.google.common.annotations.VisibleForTesting;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelResourceLocation extends ResourceLocation {
   @VisibleForTesting
   static final char VARIANT_SEPARATOR = '#';
   private final String variant;

   private ModelResourceLocation(String p_251021_, String p_249350_, String p_251656_, @Nullable ResourceLocation.Dummy p_248802_) {
      super(p_251021_, p_249350_, p_248802_);
      this.variant = p_251656_;
   }

   public ModelResourceLocation(String p_174908_, String p_174909_, String p_174910_) {
      super(p_174908_, p_174909_);
      this.variant = lowercaseVariant(p_174910_);
   }

   public ModelResourceLocation(ResourceLocation p_119442_, String p_119443_) {
      this(p_119442_.getNamespace(), p_119442_.getPath(), lowercaseVariant(p_119443_), (ResourceLocation.Dummy)null);
   }

   public static ModelResourceLocation vanilla(String p_251132_, String p_248987_) {
      return new ModelResourceLocation("minecraft", p_251132_, p_248987_);
   }

   private static String lowercaseVariant(String p_248567_) {
      return p_248567_.toLowerCase(Locale.ROOT);
   }

   public String getVariant() {
      return this.variant;
   }

   public boolean equals(Object p_119450_) {
      if (this == p_119450_) {
         return true;
      } else if (p_119450_ instanceof ModelResourceLocation && super.equals(p_119450_)) {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)p_119450_;
         return this.variant.equals(modelresourcelocation.variant);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 31 * super.hashCode() + this.variant.hashCode();
   }

   public String toString() {
      return super.toString() + "#" + this.variant;
   }
}