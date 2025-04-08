package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;

public class DiscFragmentItem extends Item {
   public DiscFragmentItem(Item.Properties p_220029_) {
      super(p_220029_);
   }

   public void appendHoverText(ItemStack p_220031_, @Nullable Level p_220032_, List<Component> p_220033_, TooltipFlag p_220034_) {
      p_220033_.add(this.getDisplayName().withStyle(ChatFormatting.GRAY));
   }

   public MutableComponent getDisplayName() {
      return Component.translatable(this.getDescriptionId() + ".desc");
   }
}