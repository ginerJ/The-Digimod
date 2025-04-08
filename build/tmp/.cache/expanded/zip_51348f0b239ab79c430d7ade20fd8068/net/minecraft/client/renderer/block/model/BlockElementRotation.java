package net.minecraft.client.renderer.block.model;

import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public record BlockElementRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
}