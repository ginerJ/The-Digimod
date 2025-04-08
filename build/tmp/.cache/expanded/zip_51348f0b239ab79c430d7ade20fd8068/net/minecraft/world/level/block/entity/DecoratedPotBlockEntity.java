package net.minecraft.world.level.block.entity;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DecoratedPotBlockEntity extends BlockEntity {
   public static final String TAG_SHERDS = "sherds";
   private DecoratedPotBlockEntity.Decorations decorations = DecoratedPotBlockEntity.Decorations.EMPTY;

   public DecoratedPotBlockEntity(BlockPos p_273660_, BlockState p_272831_) {
      super(BlockEntityType.DECORATED_POT, p_273660_, p_272831_);
   }

   protected void saveAdditional(CompoundTag p_272957_) {
      super.saveAdditional(p_272957_);
      this.decorations.save(p_272957_);
   }

   public void load(CompoundTag p_272924_) {
      super.load(p_272924_);
      this.decorations = DecoratedPotBlockEntity.Decorations.load(p_272924_);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public Direction getDirection() {
      return this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
   }

   public DecoratedPotBlockEntity.Decorations getDecorations() {
      return this.decorations;
   }

   public void setFromItem(ItemStack p_273109_) {
      this.decorations = DecoratedPotBlockEntity.Decorations.load(BlockItem.getBlockEntityData(p_273109_));
   }

   public static record Decorations(Item back, Item left, Item right, Item front) {
      public static final DecoratedPotBlockEntity.Decorations EMPTY = new DecoratedPotBlockEntity.Decorations(Items.BRICK, Items.BRICK, Items.BRICK, Items.BRICK);

      public CompoundTag save(CompoundTag p_285011_) {
         ListTag listtag = new ListTag();
         this.sorted().forEach((p_285298_) -> {
            listtag.add(StringTag.valueOf(BuiltInRegistries.ITEM.getKey(p_285298_).toString()));
         });
         p_285011_.put("sherds", listtag);
         return p_285011_;
      }

      public Stream<Item> sorted() {
         return Stream.of(this.back, this.left, this.right, this.front);
      }

      public static DecoratedPotBlockEntity.Decorations load(@Nullable CompoundTag p_284959_) {
         if (p_284959_ != null && p_284959_.contains("sherds", 9)) {
            ListTag listtag = p_284959_.getList("sherds", 8);
            return new DecoratedPotBlockEntity.Decorations(itemFromTag(listtag, 0), itemFromTag(listtag, 1), itemFromTag(listtag, 2), itemFromTag(listtag, 3));
         } else {
            return EMPTY;
         }
      }

      private static Item itemFromTag(ListTag p_285179_, int p_285060_) {
         if (p_285060_ >= p_285179_.size()) {
            return Items.BRICK;
         } else {
            Tag tag = p_285179_.get(p_285060_);
            return BuiltInRegistries.ITEM.get(new ResourceLocation(tag.getAsString()));
         }
      }
   }
}