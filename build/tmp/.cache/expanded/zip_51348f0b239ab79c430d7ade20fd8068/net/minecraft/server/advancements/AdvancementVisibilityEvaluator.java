package net.minecraft.server.advancements;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;

public class AdvancementVisibilityEvaluator {
   private static final int VISIBILITY_DEPTH = 2;

   private static AdvancementVisibilityEvaluator.VisibilityRule evaluateVisibilityRule(Advancement p_265736_, boolean p_265426_) {
      DisplayInfo displayinfo = p_265736_.getDisplay();
      if (displayinfo == null) {
         return AdvancementVisibilityEvaluator.VisibilityRule.HIDE;
      } else if (p_265426_) {
         return AdvancementVisibilityEvaluator.VisibilityRule.SHOW;
      } else {
         return displayinfo.isHidden() ? AdvancementVisibilityEvaluator.VisibilityRule.HIDE : AdvancementVisibilityEvaluator.VisibilityRule.NO_CHANGE;
      }
   }

   private static boolean evaluateVisiblityForUnfinishedNode(Stack<AdvancementVisibilityEvaluator.VisibilityRule> p_265343_) {
      for(int i = 0; i <= 2; ++i) {
         AdvancementVisibilityEvaluator.VisibilityRule advancementvisibilityevaluator$visibilityrule = p_265343_.peek(i);
         if (advancementvisibilityevaluator$visibilityrule == AdvancementVisibilityEvaluator.VisibilityRule.SHOW) {
            return true;
         }

         if (advancementvisibilityevaluator$visibilityrule == AdvancementVisibilityEvaluator.VisibilityRule.HIDE) {
            return false;
         }
      }

      return false;
   }

   private static boolean evaluateVisibility(Advancement p_265202_, Stack<AdvancementVisibilityEvaluator.VisibilityRule> p_265086_, Predicate<Advancement> p_265561_, AdvancementVisibilityEvaluator.Output p_265381_) {
      boolean flag = p_265561_.test(p_265202_);
      AdvancementVisibilityEvaluator.VisibilityRule advancementvisibilityevaluator$visibilityrule = evaluateVisibilityRule(p_265202_, flag);
      boolean flag1 = flag;
      p_265086_.push(advancementvisibilityevaluator$visibilityrule);

      for(Advancement advancement : p_265202_.getChildren()) {
         flag1 |= evaluateVisibility(advancement, p_265086_, p_265561_, p_265381_);
      }

      boolean flag2 = flag1 || evaluateVisiblityForUnfinishedNode(p_265086_);
      p_265086_.pop();
      p_265381_.accept(p_265202_, flag2);
      return flag1;
   }

   public static void evaluateVisibility(Advancement p_265578_, Predicate<Advancement> p_265359_, AdvancementVisibilityEvaluator.Output p_265303_) {
      Advancement advancement = p_265578_.getRoot();
      Stack<AdvancementVisibilityEvaluator.VisibilityRule> stack = new ObjectArrayList<>();

      for(int i = 0; i <= 2; ++i) {
         stack.push(AdvancementVisibilityEvaluator.VisibilityRule.NO_CHANGE);
      }

      evaluateVisibility(advancement, stack, p_265359_, p_265303_);
   }

   public static boolean isVisible(Advancement advancement, Predicate<Advancement> test) {
      Stack<AdvancementVisibilityEvaluator.VisibilityRule> stack = new ObjectArrayList<>();

      for(int i = 0; i <= 2; ++i) {
         stack.push(AdvancementVisibilityEvaluator.VisibilityRule.NO_CHANGE);
      }
      return evaluateVisibility(advancement.getRoot(), stack, test, (adv, visible) -> {});
   }

   @FunctionalInterface
   public interface Output {
      void accept(Advancement p_265639_, boolean p_265580_);
   }

   static enum VisibilityRule {
      SHOW,
      HIDE,
      NO_CHANGE;
   }
}
