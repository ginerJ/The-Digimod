package net.modderg.thedigimod.server.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.server.entity.DigimonEntity;

public class EvolutionTriggerItem extends DigimonItem {

    RegistryObject<SimpleParticleType> particle;

    public EvolutionTriggerItem(Properties p_41383_, RegistryObject<SimpleParticleType> p) {
        super(p_41383_);
        particle = p;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack item, Player player, LivingEntity entity, InteractionHand hand) {
        if(entity instanceof DigimonEntity){
            spawnEvoParticles(new UseOnContext(player, hand, new BlockHitResult(entity.position(), player.getDirection(), entity.blockPosition(), true)), entity.blockPosition());
        }
        return super.interactLivingEntity(item, player, entity, hand);
    }

    protected void spawnEvoParticles(UseOnContext pContext, BlockPos positionClicked) {
        for(int i = 0; i < 360; i++) {
            if(i % 20 == 0) {
                pContext.getLevel().addParticle(particle.get(),
                        positionClicked.getX() + 0.75d, positionClicked.getY(), positionClicked.getZ() + 0.75d,
                        Math.cos(i) * 0.15d, 0.15d, Math.sin(i) * 0.15d);
            }
        }
    }
}
