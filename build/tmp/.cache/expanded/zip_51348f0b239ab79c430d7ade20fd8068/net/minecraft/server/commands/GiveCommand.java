package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class GiveCommand {
   public static final int MAX_ALLOWED_ITEMSTACKS = 100;

   public static void register(CommandDispatcher<CommandSourceStack> p_214446_, CommandBuildContext p_214447_) {
      p_214446_.register(Commands.literal("give").requires((p_137777_) -> {
         return p_137777_.hasPermission(2);
      }).then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("item", ItemArgument.item(p_214447_)).executes((p_137784_) -> {
         return giveItem(p_137784_.getSource(), ItemArgument.getItem(p_137784_, "item"), EntityArgument.getPlayers(p_137784_, "targets"), 1);
      }).then(Commands.argument("count", IntegerArgumentType.integer(1)).executes((p_137775_) -> {
         return giveItem(p_137775_.getSource(), ItemArgument.getItem(p_137775_, "item"), EntityArgument.getPlayers(p_137775_, "targets"), IntegerArgumentType.getInteger(p_137775_, "count"));
      })))));
   }

   private static int giveItem(CommandSourceStack p_137779_, ItemInput p_137780_, Collection<ServerPlayer> p_137781_, int p_137782_) throws CommandSyntaxException {
      int i = p_137780_.getItem().getMaxStackSize();
      int j = i * 100;
      ItemStack itemstack = p_137780_.createItemStack(p_137782_, false);
      if (p_137782_ > j) {
         p_137779_.sendFailure(Component.translatable("commands.give.failed.toomanyitems", j, itemstack.getDisplayName()));
         return 0;
      } else {
         for(ServerPlayer serverplayer : p_137781_) {
            int k = p_137782_;

            while(k > 0) {
               int l = Math.min(i, k);
               k -= l;
               ItemStack itemstack1 = p_137780_.createItemStack(l, false);
               boolean flag = serverplayer.getInventory().add(itemstack1);
               if (flag && itemstack1.isEmpty()) {
                  itemstack1.setCount(1);
                  ItemEntity itementity1 = serverplayer.drop(itemstack1, false);
                  if (itementity1 != null) {
                     itementity1.makeFakeItem();
                  }

                  serverplayer.level().playSound((Player)null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverplayer.getRandom().nextFloat() - serverplayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                  serverplayer.containerMenu.broadcastChanges();
               } else {
                  ItemEntity itementity = serverplayer.drop(itemstack1, false);
                  if (itementity != null) {
                     itementity.setNoPickUpDelay();
                     itementity.setTarget(serverplayer.getUUID());
                  }
               }
            }
         }

         if (p_137781_.size() == 1) {
            p_137779_.sendSuccess(() -> {
               return Component.translatable("commands.give.success.single", p_137782_, itemstack.getDisplayName(), p_137781_.iterator().next().getDisplayName());
            }, true);
         } else {
            p_137779_.sendSuccess(() -> {
               return Component.translatable("commands.give.success.single", p_137782_, itemstack.getDisplayName(), p_137781_.size());
            }, true);
         }

         return p_137781_.size();
      }
   }
}