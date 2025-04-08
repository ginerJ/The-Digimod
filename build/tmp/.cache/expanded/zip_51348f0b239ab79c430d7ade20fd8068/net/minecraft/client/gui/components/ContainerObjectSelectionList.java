package net.minecraft.client.gui.components;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ContainerObjectSelectionList<E extends ContainerObjectSelectionList.Entry<E>> extends AbstractSelectionList<E> {
   public ContainerObjectSelectionList(Minecraft p_94010_, int p_94011_, int p_94012_, int p_94013_, int p_94014_, int p_94015_) {
      super(p_94010_, p_94011_, p_94012_, p_94013_, p_94014_, p_94015_);
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent p_265385_) {
      if (this.getItemCount() == 0) {
         return null;
      } else if (!(p_265385_ instanceof FocusNavigationEvent.ArrowNavigation)) {
         return super.nextFocusPath(p_265385_);
      } else {
         FocusNavigationEvent.ArrowNavigation focusnavigationevent$arrownavigation = (FocusNavigationEvent.ArrowNavigation)p_265385_;
         E e = this.getFocused();
         if (focusnavigationevent$arrownavigation.direction().getAxis() == ScreenAxis.HORIZONTAL && e != null) {
            return ComponentPath.path(this, e.nextFocusPath(p_265385_));
         } else {
            int i = -1;
            ScreenDirection screendirection = focusnavigationevent$arrownavigation.direction();
            if (e != null) {
               i = e.children().indexOf(e.getFocused());
            }

            if (i == -1) {
               switch (screendirection) {
                  case LEFT:
                     i = Integer.MAX_VALUE;
                     screendirection = ScreenDirection.DOWN;
                     break;
                  case RIGHT:
                     i = 0;
                     screendirection = ScreenDirection.DOWN;
                     break;
                  default:
                     i = 0;
               }
            }

            E e1 = e;

            ComponentPath componentpath;
            do {
               e1 = this.nextEntry(screendirection, (p_265784_) -> {
                  return !p_265784_.children().isEmpty();
               }, e1);
               if (e1 == null) {
                  return null;
               }

               componentpath = e1.focusPathAtIndex(focusnavigationevent$arrownavigation, i);
            } while(componentpath == null);

            return ComponentPath.path(this, componentpath);
         }
      }
   }

   public void setFocused(@Nullable GuiEventListener p_265559_) {
      super.setFocused(p_265559_);
      if (p_265559_ == null) {
         this.setSelected((E)null);
      }

   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      return this.isFocused() ? NarratableEntry.NarrationPriority.FOCUSED : super.narrationPriority();
   }

   protected boolean isSelectedItem(int p_94019_) {
      return false;
   }

   public void updateNarration(NarrationElementOutput p_168851_) {
      E e = this.getHovered();
      if (e != null) {
         e.updateNarration(p_168851_.nest());
         this.narrateListElementPosition(p_168851_, e);
      } else {
         E e1 = this.getFocused();
         if (e1 != null) {
            e1.updateNarration(p_168851_.nest());
            this.narrateListElementPosition(p_168851_, e1);
         }
      }

      p_168851_.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry<E extends ContainerObjectSelectionList.Entry<E>> extends AbstractSelectionList.Entry<E> implements ContainerEventHandler {
      @Nullable
      private GuiEventListener focused;
      @Nullable
      private NarratableEntry lastNarratable;
      private boolean dragging;

      public boolean isDragging() {
         return this.dragging;
      }

      public void setDragging(boolean p_94028_) {
         this.dragging = p_94028_;
      }

      public boolean mouseClicked(double p_265453_, double p_265297_, int p_265697_) {
         return ContainerEventHandler.super.mouseClicked(p_265453_, p_265297_, p_265697_);
      }

      public void setFocused(@Nullable GuiEventListener p_94024_) {
         if (this.focused != null) {
            this.focused.setFocused(false);
         }

         if (p_94024_ != null) {
            p_94024_.setFocused(true);
         }

         this.focused = p_94024_;
      }

      @Nullable
      public GuiEventListener getFocused() {
         return this.focused;
      }

      @Nullable
      public ComponentPath focusPathAtIndex(FocusNavigationEvent p_265435_, int p_265432_) {
         if (this.children().isEmpty()) {
            return null;
         } else {
            ComponentPath componentpath = this.children().get(Math.min(p_265432_, this.children().size() - 1)).nextFocusPath(p_265435_);
            return ComponentPath.path(this, componentpath);
         }
      }

      @Nullable
      public ComponentPath nextFocusPath(FocusNavigationEvent p_265672_) {
         if (p_265672_ instanceof FocusNavigationEvent.ArrowNavigation) {
            FocusNavigationEvent.ArrowNavigation focusnavigationevent$arrownavigation = (FocusNavigationEvent.ArrowNavigation)p_265672_;
            byte b0;
            switch (focusnavigationevent$arrownavigation.direction()) {
               case LEFT:
                  b0 = -1;
                  break;
               case RIGHT:
                  b0 = 1;
                  break;
               case UP:
               case DOWN:
                  b0 = 0;
                  break;
               default:
                  throw new IncompatibleClassChangeError();
            }

            int i = b0;
            if (i == 0) {
               return null;
            }

            int j = Mth.clamp(i + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1);

            for(int k = j; k >= 0 && k < this.children().size(); k += i) {
               GuiEventListener guieventlistener = this.children().get(k);
               ComponentPath componentpath = guieventlistener.nextFocusPath(p_265672_);
               if (componentpath != null) {
                  return ComponentPath.path(this, componentpath);
               }
            }
         }

         return ContainerEventHandler.super.nextFocusPath(p_265672_);
      }

      public abstract List<? extends NarratableEntry> narratables();

      void updateNarration(NarrationElementOutput p_168855_) {
         List<? extends NarratableEntry> list = this.narratables();
         Screen.NarratableSearchResult screen$narratablesearchresult = Screen.findNarratableWidget(list, this.lastNarratable);
         if (screen$narratablesearchresult != null) {
            if (screen$narratablesearchresult.priority.isTerminal()) {
               this.lastNarratable = screen$narratablesearchresult.entry;
            }

            if (list.size() > 1) {
               p_168855_.add(NarratedElementType.POSITION, Component.translatable("narrator.position.object_list", screen$narratablesearchresult.index + 1, list.size()));
               if (screen$narratablesearchresult.priority == NarratableEntry.NarrationPriority.FOCUSED) {
                  p_168855_.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
               }
            }

            screen$narratablesearchresult.entry.updateNarration(p_168855_.nest());
         }

      }
   }
}