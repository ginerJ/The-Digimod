package net.modderg.thedigimod.server.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.sound.DigiSounds;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DigiMemory extends DigimonItem {

    public DigiMemory(Properties p_41383_) {
        super(p_41383_);
    }


    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {

        Player player = context.getPlayer();

        recreateEntityFromItem(player.level(), player.getItemInHand(context.getHand()), player.position());
        player.getItemInHand(context.getHand()).shrink(1);

        return super.useOn(context);
    }

    public void storeEntityInItem(DigimonEntity entity, ItemStack stack) {
        entity.playSound(DigiSounds.MEMORY_SAVE_SOUND.get(), 0.25F, 1.2F);
        CompoundTag nbt = new CompoundTag();
        CompoundTag entityNbt = entity.serializeNBT();
        nbt.put("StoredDigimon", entityNbt);
        nbt.putString("DigimonNick", entity.getType().getDescriptionId()  + " (" + entity.getCurrentLevel() + "lv)");
        stack.setTag(nbt);
        entity.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
    }

    public void recreateEntityFromItem(Level level, ItemStack stack, Vec3 pos) {
        if (stack.hasTag() && stack.getTag().contains("StoredDigimon")) {
            CompoundTag entityNbt = stack.getTag().getCompound("StoredDigimon");
            DigimonEntity entity = (DigimonEntity) EntityType.create(entityNbt, level).get();
            entity.setPos(pos);
            level.addFreshEntity(entity);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @org.jetbrains.annotations.Nullable Level level, List<Component> p_41423_, TooltipFlag p_41424_) {
        if(level != null && level.isClientSide()){
            p_41423_.add(Component.literal(""));
            String[] nameInfo =  itemStack.getTag().getString("DigimonNick").split(" ");
            p_41423_.add(Component.literal(I18n.get(nameInfo[0]) + " " + nameInfo[1]).withStyle(ChatFormatting.BLUE));
        }
        super.appendHoverText(itemStack, level, p_41423_, p_41424_);
    }
}
