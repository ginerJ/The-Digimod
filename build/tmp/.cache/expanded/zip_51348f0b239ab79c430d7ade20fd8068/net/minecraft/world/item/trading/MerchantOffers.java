package net.minecraft.world.item.trading;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class MerchantOffers extends ArrayList<MerchantOffer> {
   public MerchantOffers() {
   }

   private MerchantOffers(int p_220323_) {
      super(p_220323_);
   }

   public MerchantOffers(CompoundTag p_45387_) {
      ListTag listtag = p_45387_.getList("Recipes", 10);

      for(int i = 0; i < listtag.size(); ++i) {
         this.add(new MerchantOffer(listtag.getCompound(i)));
      }

   }

   @Nullable
   public MerchantOffer getRecipeFor(ItemStack p_45390_, ItemStack p_45391_, int p_45392_) {
      if (p_45392_ > 0 && p_45392_ < this.size()) {
         MerchantOffer merchantoffer1 = this.get(p_45392_);
         return merchantoffer1.satisfiedBy(p_45390_, p_45391_) ? merchantoffer1 : null;
      } else {
         for(int i = 0; i < this.size(); ++i) {
            MerchantOffer merchantoffer = this.get(i);
            if (merchantoffer.satisfiedBy(p_45390_, p_45391_)) {
               return merchantoffer;
            }
         }

         return null;
      }
   }

   public void writeToStream(FriendlyByteBuf p_45394_) {
      p_45394_.writeCollection(this, (p_220325_, p_220326_) -> {
         p_220325_.writeItem(p_220326_.getBaseCostA());
         p_220325_.writeItem(p_220326_.getResult());
         p_220325_.writeItem(p_220326_.getCostB());
         p_220325_.writeBoolean(p_220326_.isOutOfStock());
         p_220325_.writeInt(p_220326_.getUses());
         p_220325_.writeInt(p_220326_.getMaxUses());
         p_220325_.writeInt(p_220326_.getXp());
         p_220325_.writeInt(p_220326_.getSpecialPriceDiff());
         p_220325_.writeFloat(p_220326_.getPriceMultiplier());
         p_220325_.writeInt(p_220326_.getDemand());
      });
   }

   public static MerchantOffers createFromStream(FriendlyByteBuf p_45396_) {
      return p_45396_.readCollection(MerchantOffers::new, (p_220328_) -> {
         ItemStack itemstack = p_220328_.readItem();
         ItemStack itemstack1 = p_220328_.readItem();
         ItemStack itemstack2 = p_220328_.readItem();
         boolean flag = p_220328_.readBoolean();
         int i = p_220328_.readInt();
         int j = p_220328_.readInt();
         int k = p_220328_.readInt();
         int l = p_220328_.readInt();
         float f = p_220328_.readFloat();
         int i1 = p_220328_.readInt();
         MerchantOffer merchantoffer = new MerchantOffer(itemstack, itemstack2, itemstack1, i, j, k, f, i1);
         if (flag) {
            merchantoffer.setToOutOfStock();
         }

         merchantoffer.setSpecialPriceDiff(l);
         return merchantoffer;
      });
   }

   public CompoundTag createTag() {
      CompoundTag compoundtag = new CompoundTag();
      ListTag listtag = new ListTag();

      for(int i = 0; i < this.size(); ++i) {
         MerchantOffer merchantoffer = this.get(i);
         listtag.add(merchantoffer.createTag());
      }

      compoundtag.put("Recipes", listtag);
      return compoundtag;
   }
}