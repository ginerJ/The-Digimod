package net.modderg.thedigimod.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String KEY_CATEGORY_THEDIGIMOD = "key.category.thedigimod.digimod";
    public static final String KEY_NAVIGATING_DIGIVICE = "key.thedigimod.navigate_digivice";

    public static final KeyMapping NAVIGATING_KEY = new KeyMapping(KEY_NAVIGATING_DIGIVICE, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KEY_CATEGORY_THEDIGIMOD);

}
