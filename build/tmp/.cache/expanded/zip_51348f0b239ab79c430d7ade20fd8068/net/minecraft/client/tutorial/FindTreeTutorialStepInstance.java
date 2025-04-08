package net.minecraft.client.tutorial;

import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FindTreeTutorialStepInstance implements TutorialStepInstance {
   private static final int HINT_DELAY = 6000;
   private static final Component TITLE = Component.translatable("tutorial.find_tree.title");
   private static final Component DESCRIPTION = Component.translatable("tutorial.find_tree.description");
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;

   public FindTreeTutorialStepInstance(Tutorial p_120496_) {
      this.tutorial = p_120496_;
   }

   public void tick() {
      ++this.timeWaiting;
      if (!this.tutorial.isSurvival()) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if (this.timeWaiting == 1) {
            LocalPlayer localplayer = this.tutorial.getMinecraft().player;
            if (localplayer != null && (hasCollectedTreeItems(localplayer) || hasPunchedTreesPreviously(localplayer))) {
               this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
               return;
            }
         }

         if (this.timeWaiting >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, false);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
         }

      }
   }

   public void clear() {
      if (this.toast != null) {
         this.toast.hide();
         this.toast = null;
      }

   }

   public void onLookAt(ClientLevel p_120501_, HitResult p_120502_) {
      if (p_120502_.getType() == HitResult.Type.BLOCK) {
         BlockState blockstate = p_120501_.getBlockState(((BlockHitResult)p_120502_).getBlockPos());
         if (blockstate.is(BlockTags.COMPLETES_FIND_TREE_TUTORIAL)) {
            this.tutorial.setStep(TutorialSteps.PUNCH_TREE);
         }
      }

   }

   public void onGetItem(ItemStack p_120499_) {
      if (p_120499_.is(ItemTags.COMPLETES_FIND_TREE_TUTORIAL)) {
         this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
      }

   }

   private static boolean hasCollectedTreeItems(LocalPlayer p_235272_) {
      return p_235272_.getInventory().hasAnyMatching((p_235270_) -> {
         return p_235270_.is(ItemTags.COMPLETES_FIND_TREE_TUTORIAL);
      });
   }

   public static boolean hasPunchedTreesPreviously(LocalPlayer p_120504_) {
      for(Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.COMPLETES_FIND_TREE_TUTORIAL)) {
         Block block = holder.value();
         if (p_120504_.getStats().getValue(Stats.BLOCK_MINED.get(block)) > 0) {
            return true;
         }
      }

      return false;
   }
}