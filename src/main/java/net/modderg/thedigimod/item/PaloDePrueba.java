package net.modderg.thedigimod.item;

import net.minecraft.world.item.context.UseOnContext;
import net.modderg.thedigimod.particles.ModParticle;

public class PaloDePrueba {

    private void spawnFoundParticles(UseOnContext pContext, BlockPos positionCliked){
        for(int i = 0 ; i < 360; i++){
            if(i % 20 == 0){
                pContext.getLevel().addParticle(ModParticles.DIGITRONE_PARTICLES.get(),
                        positionCliked.getX() + 0.5d, positionCliked.getY()+1, positionCliked.getZ() + 0.5d,
                        Math.cons(i) * 0.25d, 0.15d, Math.sin(i) * 0.25d);
            }
        }
    }
}
