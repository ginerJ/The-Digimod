package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2ic;

@OnlyIn(Dist.CLIENT)
public interface ClientTooltipPositioner {
   Vector2ic positionTooltip(int p_263026_, int p_262969_, int p_262971_, int p_263058_, int p_281643_, int p_282590_);
}