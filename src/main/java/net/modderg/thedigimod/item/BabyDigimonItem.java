package net.modderg.thedigimod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.entity.CustomDigimon;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BabyDigimonItem extends Item {

    private RegistryObject<? extends EntityType<?>> DimDigi;

    private String descendant;

    public BabyDigimonItem(Properties p_41383_, RegistryObject<? extends EntityType<?>> digimon, String name) {
        super(p_41383_);
        DimDigi = digimon;
        descendant = name;
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return super.canBeHurtBy(source) && !source.is(DamageTypeTags.IS_EXPLOSION) ;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();
        itemstack.shrink(1);
        CustomDigimon digi = (CustomDigimon) DimDigi.get().create(context.getLevel());
        digi.tame(Objects.requireNonNull(context.getPlayer()));
        digi.setPos(context.getPlayer().position());
        context.getLevel().addFreshEntity(digi);
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        p_41423_.add(Component.translatable("This digimon descended from " + descendant));
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
    }
}
