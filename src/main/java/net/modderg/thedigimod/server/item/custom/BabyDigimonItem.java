package net.modderg.thedigimod.server.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.TDConfig;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BabyDigimonItem extends DigimonItem {


    public BabyDigimonItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public boolean canBeHurtBy(@NotNull DamageSource source) {
        return super.canBeHurtBy(source) && !source.is(DamageTypeTags.IS_EXPLOSION) ;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();
        itemstack.shrink(1);
        DigimonEntity digi = (DigimonEntity) ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(TheDigiMod.MOD_ID,  ForgeRegistries.ITEMS.getKey(this).getPath())).create(context.getLevel());
        digi.tame(Objects.requireNonNull(context.getPlayer()));
        digi.setPos(context.getPlayer().position());
        digi.setLives(TDConfig.MAX_DIGIMON_LIVES.get());
        context.getLevel().addFreshEntity(digi);
        context.getPlayer().playSound(SoundEvents.TURTLE_EGG_HATCH);
        return InteractionResult.CONSUME;
    }
}
