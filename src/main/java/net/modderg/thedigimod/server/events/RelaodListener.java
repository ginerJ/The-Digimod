package net.modderg.thedigimod.server.events;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.TickEvent;

import java.util.Map;

public class RelaodListener extends SimpleJsonResourceReloadListener {

    public Map<ResourceLocation, JsonElement> jsonMap;

    public RelaodListener(Gson p_10768_, String p_10769_) {
        super(p_10768_, p_10769_);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> p_10793_, ResourceManager p_10794_, ProfilerFiller p_10795_) {
        jsonMap = p_10793_;
    }
}
