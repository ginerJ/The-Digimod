package net.modderg.thedigimod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.entity.CustomDigimon;

public class DigitronItem extends EvolutionTriggerItem{
    public DigitronItem(Properties p_41383_, RegistryObject<SimpleParticleType> p) {
        super(p_41383_, p);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack item, Player player, LivingEntity entity, InteractionHand hand) {
        if(entity instanceof CustomDigimon cd && cd.getOwner() != null && cd.isOwnedBy(player) && cd.digitronEvo() != null){
            CustomDigimon evoD =  (CustomDigimon) cd.digitronEvo().create(entity.level());
            evoD.copyOtherDigi(cd);
            entity.level().addFreshEntity(evoD);
            entity.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        }
        return super.interactLivingEntity(item, player, entity, hand);
    }
}
