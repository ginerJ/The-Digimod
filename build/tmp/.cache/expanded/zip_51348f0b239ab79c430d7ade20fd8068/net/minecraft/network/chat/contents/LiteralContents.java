package net.minecraft.network.chat.contents;

import java.util.Optional;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public record LiteralContents(String text) implements ComponentContents {
   public <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_237373_) {
      return p_237373_.accept(this.text);
   }

   public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_237375_, Style p_237376_) {
      return p_237375_.accept(p_237376_, this.text);
   }

   public String toString() {
      return "literal{" + this.text + "}";
   }
}