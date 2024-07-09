package net.modderg.thedigimod.entity.managers;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.particles.DigitalParticles;

import java.util.Random;

public class ParticleManager {

    private Random random = new Random();

    public void spawnStatUpParticles(RegistryObject<SimpleParticleType> particle, int multiplier, Entity digimon) {
        for(int y = multiplier; y > 0; --y) {
            for(int i = 0; i < 7; ++i) {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                digimon.level().addParticle(particle.get(), digimon.getRandomX(1.0D), digimon.getRandomY() + 0.5D, digimon.getRandomZ(1.0D), d0, d1, d2);
            }
        }
    }

    public void eatData(ParticleOptions particle,  Entity digimon) {
        for(int i = 0; i < 360; i++) {
            if (random.nextInt(0, 20) == 5) {
                digimon.level().addParticle(particle,
                        digimon.blockPosition().getX() + 1d, digimon.blockPosition().getY() + 0.5, digimon.blockPosition().getZ() + 1d,
                        Math.cos(i) * 0.3d, 0.1d + random.nextDouble() * 0.1d, Math.sin(i) * 0.3d);
            }
        }
    }

    public void spawnEvoParticles(CustomDigimon digimon) {
        for(int i = 0; i < 360; i++) {
            if(random.nextInt(0,20) == 5) {
                digimon.level().addParticle(DigitalParticles.EVO_PARTICLES.get(),
                        digimon.blockPosition().getX() + 1d, digimon.blockPosition().getY() + 0.5, digimon.blockPosition().getZ() + 1d,
                        Math.cos(i) * 0.3d, 0.1d + random.nextDouble()*0.1d, Math.sin(i) * 0.3d);

                if(digimon.isRookie())
                    digimon.level().addParticle(DigitalParticles.EVO_PARTICLES_CHAMPION.get(),
                            digimon.blockPosition().getX() + 1d, digimon.blockPosition().getY() + 0.5, digimon.blockPosition().getZ() + 1d,
                            Math.cos(i) * 0.6d, 0, Math.sin(i) * 0.6d);
                else if (digimon.isChampion()){

                    digimon.level().addParticle(DigitalParticles.EVO_PARTICLES_CHAMPION.get(),
                            digimon.blockPosition().getX() + 1d, digimon.blockPosition().getY() + 0.5, digimon.blockPosition().getZ() + 1d,
                            Math.cos(i) * 0.6d, 0, Math.sin(i) * 0.6d);

                    digimon.level().addParticle(DigitalParticles.EVO_PARTICLES_ULTIMATE.get(),
                            digimon.blockPosition().getX() + 1d, digimon.blockPosition().getY() + 0.5, digimon.blockPosition().getZ() + 1d,
                            Math.cos(i) * 0.8d, 0, Math.sin(i) * 0.8d);

                    digimon.level().addParticle(DigitalParticles.EVO_PARTICLES_ULTIMATE.get(),
                            digimon.blockPosition().getX() + 1d, digimon.blockPosition().getY() + 1, digimon.blockPosition().getZ() + 1d,
                            Math.cos(i) * 0.1d, 0.1d + random.nextDouble()*0.5d, Math.sin(i) * 0.1d);
                }
            }
        }
    }

    public void finishEvoParticles(CustomDigimon digimon){

        ParticleOptions particle = DigitalParticles.EVO_PARTICLES.get();
        double multiplier = 0.2d;

        if(digimon.isRookie()){
            particle = DigitalParticles.EVO_PARTICLES_CHAMPION.get();
            multiplier = 0.3d;
        }
        else if (digimon.isChampion()){
            particle = DigitalParticles.EVO_PARTICLES_ULTIMATE.get();
            multiplier = 0.4d;
        }

        for(int i = 0; i < 360; i++) {
            if(random.nextInt(0,20) == 5) {

                digimon.level().addParticle(particle,
                        digimon.position().x + 2d, digimon.position().y + 2, digimon.position().z + 2d,
                        Math.cos(i) * multiplier, 0, Math.sin(i) * multiplier);

                digimon.level().addParticle(particle,
                        digimon.position().x + 2d, digimon.position().y + 1, digimon.position().z + 2d,
                        Math.cos(i) * multiplier * 1.5d, 0, Math.sin(i) * multiplier * 1.5d);

                digimon.level().addParticle(particle,
                        digimon.position().x + 2d, digimon.position().y + 0.5, digimon.position().z + 2d,
                        Math.cos(i) * multiplier * 2d, 0, Math.sin(i) * multiplier * 2d);

            }
        }
    }

    public void spawnBubbleParticle(RegistryObject<SimpleParticleType> particle, Entity digimon) {
        for(int i = 0; i < 360; i++) {
            if(random.nextInt(0,100) == 1){
                digimon.level().addParticle(particle.get(),
                        digimon.blockPosition().getX() , digimon.blockPosition().getY() + digimon.getDimensions(Pose.STANDING).height + 0.5f, digimon.blockPosition().getZ(),
                        0, 0.05d + random.nextDouble() * 0.01d, 0);
                break;
            }
        }
    }

    public void spawnItemParticles(ItemStack p_21061_, int p_21062_, Entity digimon) {
        for(int i = 0; i < p_21062_; ++i) {
            Vec3 vec3 = new Vec3(((double)random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vec3 = vec3.xRot(-digimon.getXRot() * ((float)Math.PI / 180F));
            vec3 = vec3.yRot(-digimon.getYRot() * ((float)Math.PI / 180F));
            double d0 = (double)(-random.nextFloat()) * 0.6D - 0.3D;
            Vec3 vec31 = new Vec3(((double)random.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
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
