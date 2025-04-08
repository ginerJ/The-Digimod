package net.minecraft.world.entity.vehicle;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class MinecartChest extends AbstractMinecartContainer {
   public MinecartChest(EntityType<? extends MinecartChest> p_38487_, Level p_38488_) {
      super(p_38487_, p_38488_);
   }

   public MinecartChest(Level p_38490_, double p_38491_, double p_38492_, double p_38493_) {
      super(EntityType.CHEST_MINECART, p_38491_, p_38492_, p_38493_, p_38490_);
   }

   protected Item getDropItem() {
      return Items.CHEST_MINECART;
   }

   public int getContainerSize() {
      return 27;
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.CHEST;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH);
   }

   public int getDefaultDisplayOffset() {
      return 8;
   }

   public AbstractContainerMenu createMenu(int p_38496_, Inventory p_38497_) {
      return ChestMenu.threeRows(p_38496_, p_38497_, this);
   }

   public void stopOpen(Player p_270111_) {
      this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(p_270111_));
   }

   public InteractionResult interact(Player p_270398_, InteractionHand p_270576_) {
      InteractionResult interactionresult = this.interactWithContainerVehicle(p_270398_);
      if (interactionresult.consumesAction()) {
         this.gameEvent(GameEvent.CONTAINER_OPEN, p_270398_);
         PiglinAi.angerNearbyPiglins(p_270398_, true);
      }

      return interactionresult;
   }
}