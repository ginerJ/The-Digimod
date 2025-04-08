package net.minecraft.client.gui.components;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditBox extends AbstractWidget implements Renderable {
   public static final int BACKWARDS = -1;
   public static final int FORWARDS = 1;
   private static final int CURSOR_INSERT_WIDTH = 1;
   private static final int CURSOR_INSERT_COLOR = -3092272;
   private static final String CURSOR_APPEND_CHARACTER = "_";
   public static final int DEFAULT_TEXT_COLOR = 14737632;
   private static final int BORDER_COLOR_FOCUSED = -1;
   private static final int BORDER_COLOR = -6250336;
   private static final int BACKGROUND_COLOR = -16777216;
   private final Font font;
   private String value = "";
   private int maxLength = 32;
   private int frame;
   private boolean bordered = true;
   private boolean canLoseFocus = true;
   private boolean isEditable = true;
   private boolean shiftPressed;
   private int displayPos;
   private int cursorPos;
   private int highlightPos;
   private int textColor = 14737632;
   private int textColorUneditable = 7368816;
   @Nullable
   private String suggestion;
   @Nullable
   private Consumer<String> responder;
   private Predicate<String> filter = Objects::nonNull;
   private BiFunction<String, Integer, FormattedCharSequence> formatter = (p_94147_, p_94148_) -> {
      return FormattedCharSequence.forward(p_94147_, Style.EMPTY);
   };
   @Nullable
   private Component hint;

   public EditBox(Font p_94114_, int p_94115_, int p_94116_, int p_94117_, int p_94118_, Component p_94119_) {
      this(p_94114_, p_94115_, p_94116_, p_94117_, p_94118_, (EditBox)null, p_94119_);
   }

   public EditBox(Font p_94106_, int p_94107_, int p_94108_, int p_94109_, int p_94110_, @Nullable EditBox p_94111_, Component p_94112_) {
      super(p_94107_, p_94108_, p_94109_, p_94110_, p_94112_);
      this.font = p_94106_;
      if (p_94111_ != null) {
         this.setValue(p_94111_.getValue());
      }

   }

   public void setResponder(Consumer<String> p_94152_) {
      this.responder = p_94152_;
   }

   public void setFormatter(BiFunction<String, Integer, FormattedCharSequence> p_94150_) {
      this.formatter = p_94150_;
   }

   public void tick() {
      ++this.frame;
   }

   protected MutableComponent createNarrationMessage() {
      Component component = this.getMessage();
      return Component.translatable("gui.narrate.editBox", component, this.value);
   }

   public void setValue(String p_94145_) {
      if (this.filter.test(p_94145_)) {
         if (p_94145_.length() > this.maxLength) {
            this.value = p_94145_.substring(0, this.maxLength);
         } else {
            this.value = p_94145_;
         }

         this.moveCursorToEnd();
         this.setHighlightPos(this.cursorPos);
         this.onValueChange(p_94145_);
      }
   }

   public String getValue() {
      return this.value;
   }

   public String getHighlighted() {
      int i = Math.min(this.cursorPos, this.highlightPos);
      int j = Math.max(this.cursorPos, this.highlightPos);
      return this.value.substring(i, j);
   }

   public void setFilter(Predicate<String> p_94154_) {
      this.filter = p_94154_;
   }

   public void insertText(String p_94165_) {
      int i = Math.min(this.cursorPos, this.highlightPos);
      int j = Math.max(this.cursorPos, this.highlightPos);
      int k = this.maxLength - this.value.length() - (i - j);
      String s = SharedConstants.filterText(p_94165_);
      int l = s.length();
      if (k < l) {
         s = s.substring(0, k);
         l = k;
      }

      String s1 = (new StringBuilder(this.value)).replace(i, j, s).toString();
      if (this.filter.test(s1)) {
         this.value = s1;
         this.setCursorPosition(i + l);
         this.setHighlightPos(this.cursorPos);
         this.onValueChange(this.value);
      }
   }

   private void onValueChange(String p_94175_) {
      if (this.responder != null) {
         this.responder.accept(p_94175_);
      }

   }

   private void deleteText(int p_94218_) {
      if (Screen.hasControlDown()) {
         this.deleteWords(p_94218_);
      } else {
         this.deleteChars(p_94218_);
      }

   }

   public void deleteWords(int p_94177_) {
      if (!this.value.isEmpty()) {
         if (this.highlightPos != this.cursorPos) {
            this.insertText("");
         } else {
            this.deleteChars(this.getWordPosition(p_94177_) - this.cursorPos);
         }
      }
   }

   public void deleteChars(int p_94181_) {
      if (!this.value.isEmpty()) {
         if (this.highlightPos != this.cursorPos) {
            this.insertText("");
         } else {
            int i = this.getCursorPos(p_94181_);
            int j = Math.min(i, this.cursorPos);
            int k = Math.max(i, this.cursorPos);
            if (j != k) {
               String s = (new StringBuilder(this.value)).delete(j, k).toString();
               if (this.filter.test(s)) {
                  this.value = s;
                  this.moveCursorTo(j);
               }
            }
         }
      }
   }

   public int getWordPosition(int p_94185_) {
      return this.getWordPosition(p_94185_, this.getCursorPosition());
   }

   private int getWordPosition(int p_94129_, int p_94130_) {
      return this.getWordPosition(p_94129_, p_94130_, true);
   }

   private int getWordPosition(int p_94141_, int p_94142_, boolean p_94143_) {
      int i = p_94142_;
      boolean flag = p_94141_ < 0;
      int j = Math.abs(p_94141_);

      for(int k = 0; k < j; ++k) {
         if (!flag) {
            int l = this.value.length();
            i = this.value.indexOf(32, i);
            if (i == -1) {
               i = l;
            } else {
               while(p_94143_ && i < l && this.value.charAt(i) == ' ') {
                  ++i;
               }
            }
         } else {
            while(p_94143_ && i > 0 && this.value.charAt(i - 1) == ' ') {
               --i;
            }

            while(i > 0 && this.value.charAt(i - 1) != ' ') {
               --i;
            }
         }
      }

      return i;
   }

   public void moveCursor(int p_94189_) {
      this.moveCursorTo(this.getCursorPos(p_94189_));
   }

   private int getCursorPos(int p_94221_) {
      return Util.offsetByCodepoints(this.value, this.cursorPos, p_94221_);
   }

   public void moveCursorTo(int p_94193_) {
      this.setCursorPosition(p_94193_);
      if (!this.shiftPressed) {
         this.setHighlightPos(this.cursorPos);
      }

      this.onValueChange(this.value);
   }

   public void setCursorPosition(int p_94197_) {
      this.cursorPos = Mth.clamp(p_94197_, 0, this.value.length());
   }

   public void moveCursorToStart() {
      this.moveCursorTo(0);
   }

   public void moveCursorToEnd() {
      this.moveCursorTo(this.value.length());
   }

   public boolean keyPressed(int p_94132_, int p_94133_, int p_94134_) {
      if (!this.canConsumeInput()) {
         return false;
      } else {
         this.shiftPressed = Screen.hasShiftDown();
         if (Screen.isSelectAll(p_94132_)) {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            return true;
         } else if (Screen.isCopy(p_94132_)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            return true;
         } else if (Screen.isPaste(p_94132_)) {
            if (this.isEditable) {
               this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            }

            return true;
         } else if (Screen.isCut(p_94132_)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            if (this.isEditable) {
               this.insertText("");
            }

            return true;
         } else {
            switch (p_94132_) {
               case 259:
                  if (this.isEditable) {
                     this.shiftPressed = false;
                     this.deleteText(-1);
                     this.shiftPressed = Screen.hasShiftDown();
                  }

                  return true;
               case 260:
               case 264:
               case 265:
               case 266:
               case 267:
               default:
                  return false;
               case 261:
                  if (this.isEditable) {
                     this.shiftPressed = false;
                     this.deleteText(1);
                     this.shiftPressed = Screen.hasShiftDown();
                  }

                  return true;
               case 262:
                  if (Screen.hasControlDown()) {
                     this.moveCursorTo(this.getWordPosition(1));
                  } else {
                     this.moveCursor(1);
                  }

                  return true;
               case 263:
                  if (Screen.hasControlDown()) {
                     this.moveCursorTo(this.getWordPosition(-1));
                  } else {
                     this.moveCursor(-1);
                  }

                  return true;
               case 268:
                  this.moveCursorToStart();
                  return true;
               case 269:
                  this.moveCursorToEnd();
                  return true;
            }
         }
      }
   }

   public boolean canConsumeInput() {
      return this.isVisible() && this.isFocused() && this.isEditable();
   }

   public boolean charTyped(char p_94122_, int p_94123_) {
      if (!this.canConsumeInput()) {
         return false;
      } else if (SharedConstants.isAllowedChatCharacter(p_94122_)) {
         if (this.isEditable) {
            this.insertText(Character.toString(p_94122_));
         }

         return true;
      } else {
         return false;
      }
   }

   public void onClick(double p_279417_, double p_279437_) {
      int i = Mth.floor(p_279417_) - this.getX();
      if (this.bordered) {
         i -= 4;
      }

      String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
      this.moveCursorTo(this.font.plainSubstrByWidth(s, i).length() + this.displayPos);
   }

   public void playDownSound(SoundManager p_279245_) {
   }

   public void renderWidget(GuiGraphics p_283252_, int p_281594_, int p_282100_, float p_283101_) {
      if (this.isVisible()) {
         if (this.isBordered()) {
            int i = this.isFocused() ? -1 : -6250336;
            p_283252_.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, i);
            p_283252_.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
         }

         int i2 = this.isEditable ? this.textColor : this.textColorUneditable;
         int j = this.cursorPos - this.displayPos;
         int k = this.highlightPos - this.displayPos;
         String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
         boolean flag = j >= 0 && j <= s.length();
         boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
         int l = this.bordered ? this.getX() + 4 : this.getX();
         int i1 = this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
         int j1 = l;
         if (k > s.length()) {
            k = s.length();
         }

         if (!s.isEmpty()) {
            String s1 = flag ? s.substring(0, j) : s;
            j1 = p_283252_.drawString(this.font, this.formatter.apply(s1, this.displayPos), l, i1, i2);
         }

         boolean flag2 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
         int k1 = j1;
         if (!flag) {
            k1 = j > 0 ? l + this.width : l;
         } else if (flag2) {
            k1 = j1 - 1;
            --j1;
         }

         if (!s.isEmpty() && flag && j < s.length()) {
            p_283252_.drawString(this.font, this.formatter.apply(s.substring(j), this.cursorPos), j1, i1, i2);
         }

         if (this.hint != null && s.isEmpty() && !this.isFocused()) {
            p_283252_.drawString(this.font, this.hint, j1, i1, i2);
         }

         if (!flag2 && this.suggestion != null) {
            p_283252_.drawString(this.font, this.suggestion, k1 - 1, i1, -8355712);
         }

         if (flag1) {
            if (flag2) {
               p_283252_.fill(RenderType.guiOverlay(), k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
            } else {
               p_283252_.drawString(this.font, "_", k1, i1, i2);
            }
         }

         if (k != j) {
            int l1 = l + this.font.width(s.substring(0, k));
            this.renderHighlight(p_283252_, k1, i1 - 1, l1 - 1, i1 + 1 + 9);
         }

      }
   }

   private void renderHighlight(GuiGraphics p_281400_, int p_265338_, int p_265693_, int p_265618_, int p_265584_) {
      if (p_265338_ < p_265618_) {
         int i = p_265338_;
         p_265338_ = p_265618_;
         p_265618_ = i;
      }

      if (p_265693_ < p_265584_) {
         int j = p_265693_;
         p_265693_ = p_265584_;
         p_265584_ = j;
      }

      if (p_265618_ > this.getX() + this.width) {
         p_265618_ = this.getX() + this.width;
      }

      if (p_265338_ > this.getX() + this.width) {
         p_265338_ = this.getX() + this.width;
      }

      p_281400_.fill(RenderType.guiTextHighlight(), p_265338_, p_265693_, p_265618_, p_265584_, -16776961);
   }

   public void setMaxLength(int p_94200_) {
      this.maxLength = p_94200_;
      if (this.value.length() > p_94200_) {
         this.value = this.value.substring(0, p_94200_);
         this.onValueChange(this.value);
      }

   }

   private int getMaxLength() {
      return this.maxLength;
   }

   public int getCursorPosition() {
      return this.cursorPos;
   }

   private boolean isBordered() {
      return this.bordered;
   }

   public void setBordered(boolean p_94183_) {
      this.bordered = p_94183_;
   }

   public void setTextColor(int p_94203_) {
      this.textColor = p_94203_;
   }

   public void setTextColorUneditable(int p_94206_) {
      this.textColorUneditable = p_94206_;
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent p_265216_) {
      return this.visible && this.isEditable ? super.nextFocusPath(p_265216_) : null;
   }

   public boolean isMouseOver(double p_94157_, double p_94158_) {
      return this.visible && p_94157_ >= (double)this.getX() && p_94157_ < (double)(this.getX() + this.width) && p_94158_ >= (double)this.getY() && p_94158_ < (double)(this.getY() + this.height);
   }

   public void setFocused(boolean p_265520_) {
      if (this.canLoseFocus || p_265520_) {
         super.setFocused(p_265520_);
         if (p_265520_) {
            this.frame = 0;
         }

      }
   }

   private boolean isEditable() {
      return this.isEditable;
   }

   public void setEditable(boolean p_94187_) {
      this.isEditable = p_94187_;
   }

   public int getInnerWidth() {
      return this.isBordered() ? this.width - 8 : this.width;
   }

   public void setHighlightPos(int p_94209_) {
      int i = this.value.length();
      this.highlightPos = Mth.clamp(p_94209_, 0, i);
      if (this.font != null) {
         if (this.displayPos > i) {
            this.displayPos = i;
         }

         int j = this.getInnerWidth();
         String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), j);
         int k = s.length() + this.displayPos;
         if (this.highlightPos == this.displayPos) {
            this.displayPos -= this.font.plainSubstrByWidth(this.value, j, true).length();
         }

         if (this.highlightPos > k) {
            this.displayPos += this.highlightPos - k;
         } else if (this.highlightPos <= this.displayPos) {
            this.displayPos -= this.displayPos - this.highlightPos;
         }

         this.displayPos = Mth.clamp(this.displayPos, 0, i);
      }

   }

   public void setCanLoseFocus(boolean p_94191_) {
      this.canLoseFocus = p_94191_;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean p_94195_) {
      this.visible = p_94195_;
   }

   public void setSuggestion(@Nullable String p_94168_) {
      this.suggestion = p_94168_;
   }

   public int getScreenX(int p_94212_) {
      return p_94212_ > this.value.length() ? this.getX() : this.getX() + this.font.width(this.value.substring(0, p_94212_));
   }

   public void updateWidgetNarration(NarrationElementOutput p_259237_) {
      p_259237_.add(NarratedElementType.TITLE, this.createNarrationMessage());
   }

   public void setHint(Component p_259584_) {
      this.hint = p_259584_;
   }
}