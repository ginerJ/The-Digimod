package net.modderg.thedigimod.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.gui.DigiviceScreen;
import net.modderg.thedigimod.packet.PacketInit;
import net.modderg.thedigimod.packet.SToCDigiviceScreenPacket;

import java.util.List;
import java.util.function.Predicate;

public class DigiviceItem extends DigimonItem {
    public DigiviceItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player player, InteractionHand hand) {

        if(player.getItemInHand(hand.equals(InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND: InteractionHand.MAIN_HAND).getItem() instanceof DigimonItem)
            return super.use(p_41432_, player, hand);

        if (!player.level().isClientSide && !player.isShiftKeyDown()) {

            int range = 30;

            Vec3 eyePosition = player.getEyePosition(1.0F);
            Vec3 viewVector = player.getViewVector(1.0F);
            Vec3 traceEnd = eyePosition.add(viewVector.scale(range));
            AABB boundingBox = player.getBoundingBox().expandTowards(viewVector.scale(range)).inflate(1.0, 1.0, 1.0);

            Predicate<Entity> predicate = (entity) -> !entity.isSpectator() && entity.isPickable();
            EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(player, eyePosition, traceEnd, boundingBox, predicate, range * range);

            if(!player.level().isClientSide())
                if (hitResult != null) {
                    PacketInit.sendToClient(new SToCDigiviceScreenPacket(hitResult.getEntity().getId()), (ServerPlayer) player);
                } else {
                    PacketInit.sendToClient(new SToCDigiviceScreenPacket(-1), (ServerPlayer) player);
                }
        }
        return super.use(p_41432_, player, hand);
    }
}
