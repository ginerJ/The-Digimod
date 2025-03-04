package net.modderg.thedigimod.client.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.server.projectiles.ProjectileDefault;
import net.modderg.thedigimod.server.projectiles.ProjectileExplosion;
import net.modderg.thedigimod.server.projectiles.ProjectileParticleStreamDefault;
import net.modderg.thedigimod.server.projectiles.ProjectileSkyFallingDefault;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CustomProjectileRender<D extends ProjectileDefault> extends GeoEntityRenderer<ProjectileDefault> {

    public CustomProjectileRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel<ProjectileDefault>) new CustomProjectileModel());
    }

    @Override
    public void render(ProjectileDefault entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity instanceof ProjectileParticleStreamDefault)
            return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, ProjectileDefault animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if(!(animatable instanceof ProjectileSkyFallingDefault))
            pointEntityTowardsDeltaMovement(poseStack, animatable);
        if(animatable.bright){
            packedLight = LightTexture.FULL_BRIGHT;
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public static void pointEntityTowardsDeltaMovement(PoseStack poseStack, Entity projectile) {

        Vec3 deltaMovement = projectile.getDeltaMovement();

        double deltaX = deltaMovement.x;
        double deltaY = deltaMovement.y;
        double deltaZ = deltaMovement.z;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX));
        float pitch = (float) Math.toDegrees(Math.atan2(deltaY, horizontalDistance));

        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw + 90.0F));

        if(!(projectile instanceof ProjectileExplosion)) poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
    }
}
