package net.modderg.thedigimod.server.item.custom;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.advancements.TDAdvancements;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.entity.TDEntities;
import net.modderg.thedigimod.server.sound.DigiSounds;

public class DigitronItem extends EvolutionTriggerItem{
    public DigitronItem(Properties p_41383_, RegistryObject<SimpleParticleType> p) {
        super(p_41383_, p);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack item, Player player, LivingEntity entity, InteractionHand hand) {
        if(entity instanceof DigimonEntity cd && cd.getOwner() != null && cd.isOwnedBy(player) && cd.digitronEvo != null){
            DigimonEntity evoD = (DigimonEntity) ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(TheDigiMod.MOD_ID, cd.digitronEvo)).create(cd.level());
            evoD.copyOtherDigiAndData(cd);
            evoD.setRank(cd.getRank());
            entity.level().addFreshEntity(evoD);
            entity.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
            entity.playSound(DigiSounds.DIGITRON_SOUND.get(), 0.25F, 1.0F);
            if(player instanceof ServerPlayer sPlayer)
                TDAdvancements.grantAdvancement(sPlayer, TDAdvancements.DIGITRON_EVO);
        }
        return super.interactLivingEntity(item, player, entity, hand);
    }
}
