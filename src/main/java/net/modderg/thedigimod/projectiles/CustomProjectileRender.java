package net.modderg.thedigimod.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.projectiles.CustomProjectile;
import net.modderg.thedigimod.projectiles.CustomProjectileModel;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class CustomProjectileRender<D extends CustomProjectile> extends GeoEntityRenderer<CustomProjectile> {

    public CustomProjectileRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel<CustomProjectile>) new CustomProjectileModel());
    }

    @Override
    public void renderRecursively(PoseStack poseStack, CustomProjectile animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        pointEntityTowardsDeltaMovement(poseStack, animatable);
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public static void pointEntityTowardsDeltaMovement(PoseStack poseStack, Entity entity) {
        Vec3 deltaMovement = entity.getDeltaMovement();
        double deltaX = deltaMovement.x;
        double deltaY = deltaMovement.y;
        double deltaZ = deltaMovement.z;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX));
        float pitch = (float) Math.toDegrees(Math.atan2(deltaY, horizontalDistance));

        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw + 90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
    }
}
