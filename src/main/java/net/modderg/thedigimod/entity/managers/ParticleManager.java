package net.modderg.thedigimod.entity.managers;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.particles.DigitalParticles;

import java.util.Objects;

public class ParticleManager {

    private CustomDigimon digimon;

    public ParticleManager (CustomDigimon cd){
        this.digimon = cd;
    }

    public void spawnStatUpParticles(RegistryObject<SimpleParticleType> particle, int multiplier) {
        for(int y = multiplier; y > 0; --y) {
            for(int i = 0; i < 7; ++i) {
                double d0 = digimon.getRandom().nextGaussian() * 0.02D;
                double d1 = digimon.getRandom().nextGaussian() * 0.02D;
                double d2 = digimon.getRandom().nextGaussian() * 0.02D;
                digimon.level().addParticle(particle.get(), digimon.getRandomX(1.0D), digimon.getRandomY() + 0.5D, digimon.getRandomZ(1.0D), d0, d1, d2);
            }
        }
    }

    public void spawnEvoParticles(RegistryObject<SimpleParticleType> particle) {
        for(int i = 0; i < 360; i++) {
            if(digimon.getRandom().nextInt(0,20) == 5) {
                digimon.level().addParticle(particle.get(),
                        digimon.blockPosition().getX() + 1d, digimon.blockPosition().getY(), digimon.blockPosition().getZ() + 1d,
                        Math.cos(i) * 0.3d, 0.15d + digimon.getRandom().nextDouble()*0.1d, Math.sin(i) * 0.3d);

            }
        }
    }

    public void spawnBubbleParticle(RegistryObject<SimpleParticleType> particle) {
        for(int i = 0; i < 360; i++) {
            if(digimon.getRandom().nextInt(0,100) == 1){
                digimon.level().addParticle(particle.get(),
                        digimon.blockPosition().getX() , digimon.blockPosition().getY() + digimon.getDimensions(Pose.STANDING).height + 0.5f, digimon.blockPosition().getZ(),
                        0, 0.05d + digimon.getRandom().nextDouble() * 0.01d, 0);
                break;
            }
        }
    }

    public void spawnItemParticles(ItemStack p_21061_, int p_21062_) {
        for(int i = 0; i < p_21062_; ++i) {
            Vec3 vec3 = new Vec3(((double)digimon.getRandom().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vec3 = vec3.xRot(-digimon.getXRot() * ((float)Math.PI / 180F));
            vec3 = vec3.yRot(-digimon.getYRot() * ((float)Math.PI / 180F));
            double d0 = (double)(-digimon.getRandom().nextFloat()) * 0.6D - 0.3D;
            Vec3 vec31 = new Vec3(((double)digimon.getRandom().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
            vec31 = vec31.xRot(-digimon.getXRot() * ((float)Math.PI / 180F));
            vec31 = vec31.yRot(-digimon.getYRot() * ((float)Math.PI / 180F));
            vec31 = vec31.add(digimon.getX(), digimon.getEyeY(), digimon.getZ());
            if (digimon.level() instanceof ServerLevel) //Forge: Fix MC-2518 spawnParticle is nooped on server, need to use server specific variant
                ((ServerLevel) digimon.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, p_21061_), vec31.x, vec31.y, vec31.z, 1, vec3.x, vec3.y + 0.05D, vec3.z, 0.0D);
            else
                digimon.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, p_21061_), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);
        }

    }
}
