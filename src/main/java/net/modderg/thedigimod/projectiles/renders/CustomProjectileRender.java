package net.modderg.thedigimod.projectiles.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.projectiles.CustomProjectile;
import net.modderg.thedigimod.projectiles.PepperBreath;
import net.modderg.thedigimod.projectiles.models.CustomProjectileModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CustomProjectileRender<D extends CustomProjectile> extends GeoEntityRenderer<CustomProjectile> {

    public CustomProjectileRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel<CustomProjectile>) new CustomProjectileModel());
    }
}
