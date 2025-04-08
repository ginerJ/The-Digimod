package net.minecraft.network.chat.contents;

import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public class KeybindContents implements ComponentContents {
   private final String name;
   @Nullable
   private Supplier<Component> nameResolver;

   public KeybindContents(String p_237347_) {
      this.name = p_237347_;
   }

   private Component getNestedComponent() {
      if (this.nameResolver == null) {
         this.nameResolver = KeybindResolver.keyResolver.apply(this.name);
      }

      return this.nameResolver.get();
   }

   public <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_237350_) {
      return this.getNestedComponent().visit(p_237350_);
   }

   public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_237352_, Style p_237353_) {
      return this.getNestedComponent().visit(p_237352_, p_237353_);
   }

   public boolean equals(Object p_237356_) {
      if (this == p_237356_) {
         return true;
      } else {
         if (p_237356_ instanceof KeybindContents) {
            KeybindContents keybindcontents = (KeybindContents)p_237356_;
            if (this.name.equals(keybindcontents.name)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public String toString() {
      return "keybind{" + this.name + "}";
   }

   public String getName() {
      return this.name;
   }
}