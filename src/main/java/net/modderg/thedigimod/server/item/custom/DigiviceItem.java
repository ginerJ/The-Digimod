package net.modderg.thedigimod.server.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.server.packet.PacketInit;
import net.modderg.thedigimod.server.packet.SToCDigiviceScreenPacket;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class DigiviceItem extends DigimonItem implements DyeableLeatherItem {

    public final int defaultColor;

    public DigiviceItem(Properties p_41383_, int defaultColor) {
        super(p_41383_);
        this.defaultColor = defaultColor;
    }

    @Override
    public int getColor(ItemStack p_41122_) {
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : defaultColor;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level p_41432_, Player player, InteractionHand hand) {

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
                if (hitResult != null)
                    PacketInit.sendToClient(new SToCDigiviceScreenPacket(hitResult.getEntity().getId()), (ServerPlayer) player);
                else
                    PacketInit.sendToClient(new SToCDigiviceScreenPacket(-1), (ServerPlayer) player);
        }
        return super.use(p_41432_, player, hand);
    }
}
