package net.modderg.thedigimod.events.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.DigitalEntities;
import net.modderg.thedigimod.entity.goods.PunchingBagRender;
import net.modderg.thedigimod.entity.renders.*;
import net.modderg.thedigimod.projectiles.renders.CustomProjectileRender;

@Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)

public class ClientEventBusSubscriber {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DigitalEntities.KOROMON.get(), KoromonRender::new);
        event.registerEntityRenderer(DigitalEntities.KOROMONB.get(), KoromonBRender::new);
        event.registerEntityRenderer(DigitalEntities.MOCHIMON.get(), MochimonRender::new);
        event.registerEntityRenderer(DigitalEntities.MOCHIMONK.get(), MochimonKRender::new);
        event.registerEntityRenderer(DigitalEntities.AGUMON.get(), AgumonRender::new);
        event.registerEntityRenderer(DigitalEntities.TENTOMON.get(), TentomonRender::new);
        event.registerEntityRenderer(DigitalEntities.KABUTERIMON.get(), KabuterimonRender::new);
        event.registerEntityRenderer(DigitalEntities.GREYMON.get(), GreymonRender::new);
        event.registerEntityRenderer(DigitalEntities.TSUNOMON.get(), TsunomonRender::new);
        event.registerEntityRenderer(DigitalEntities.GRIZZLYMON.get(), GrizzlymonRender::new);
        event.registerEntityRenderer(DigitalEntities.BEARMON.get(), BearmonRender::new);
        event.registerEntityRenderer(DigitalEntities.KUNEMON.get(), KunemonRender::new);
        event.registerEntityRenderer(DigitalEntities.GIGIMON.get(), GigimonRender::new);
        event.registerEntityRenderer(DigitalEntities.GUILMON.get(), GuilmonRender::new);
        event.registerEntityRenderer(DigitalEntities.PUYOYOMON.get(), PuyoyomonRender::new);
        event.registerEntityRenderer(DigitalEntities.JELLYMON.get(), JellymonRender::new);
        event.registerEntityRenderer(DigitalEntities.TESLAJELLYMON.get(), TeslajellymonRender::new);
        event.registerEntityRenderer(DigitalEntities.GROWLMON.get(), GrowlmonRender::new);
        event.registerEntityRenderer(DigitalEntities.KUWAGAMON.get(), KuwagamonRender::new);
        event.registerEntityRenderer(DigitalEntities.BABYDMON.get(), BabydmonRender::new);
        event.registerEntityRenderer(DigitalEntities.DRACOMON.get(), DracomonRender::new);
        event.registerEntityRenderer(DigitalEntities.COREDRAMON.get(), CoredramonRender::new);
        event.registerEntityRenderer(DigitalEntities.BIBIMON.get(), BibimonRender::new);
        event.registerEntityRenderer(DigitalEntities.PULSEMON.get(), PulsemonRender::new);
        event.registerEntityRenderer(DigitalEntities.BULKMON.get(), BulkmonRender::new);
        event.registerEntityRenderer(DigitalEntities.AGUMONBLACK.get(), AgumonBlackRender::new);
        event.registerEntityRenderer(DigitalEntities.DARKTYRANNOMON.get(), DarkTyrannomonRender::new);
        event.registerEntityRenderer(DigitalEntities.TYRANNOMON.get(), TyrannomonRender::new);
        event.registerEntityRenderer(DigitalEntities.VEEDRAMON.get(), VeedramonRender::new);
        event.registerEntityRenderer(DigitalEntities.CHAKMON.get(), ChakmonRender::new);
        event.registerEntityRenderer(DigitalEntities.BLACKGAOGAMON.get(), BlackGaogamonRender::new);
        event.registerEntityRenderer(DigitalEntities.YOKOMON.get(), YokomonRender::new);
        event.registerEntityRenderer(DigitalEntities.BIYOMON.get(), BiyomonRender::new);
        event.registerEntityRenderer(DigitalEntities.BIRDRAMON.get(), BirdramonRender::new);
        event.registerEntityRenderer(DigitalEntities.SABERDRAMON.get(), SaberdramonRender::new);
        event.registerEntityRenderer(DigitalEntities.AKATORIMON.get(), AkatorimonRender::new);
        event.registerEntityRenderer(DigitalEntities.NAMAKEMON.get(), NamakemonRender::new);
        event.registerEntityRenderer(DigitalEntities.EXERMON.get(), ExermonRender::new);
        event.registerEntityRenderer(DigitalEntities.GREYMONVIRUS.get(), GreymonVirusRender::new);

        event.registerEntityRenderer(DigitalEntities.BULLET.get(), CustomProjectileRender::new);

        event.registerEntityRenderer(DigitalEntities.PUNCHING_BAG.get(), PunchingBagRender::new);
    }
    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DigitalEntities.KOROMON.get(), KoromonRender::new);
        event.registerEntityRenderer(DigitalEntities.KOROMONB.get(), KoromonBRender::new);
        event.registerEntityRenderer(DigitalEntities.MOCHIMON.get(), MochimonRender::new);
        event.registerEntityRenderer(DigitalEntities.MOCHIMONK.get(), MochimonKRender::new);
        event.registerEntityRenderer(DigitalEntities.AGUMON.get(), AgumonRender::new);
        event.registerEntityRenderer(DigitalEntities.TENTOMON.get(), TentomonRender::new);
        event.registerEntityRenderer(DigitalEntities.KABUTERIMON.get(), KabuterimonRender::new);
        event.registerEntityRenderer(DigitalEntities.GREYMON.get(), GreymonRender::new);
        event.registerEntityRenderer(DigitalEntities.TSUNOMON.get(), TsunomonRender::new);
        event.registerEntityRenderer(DigitalEntities.GRIZZLYMON.get(), GrizzlymonRender::new);
        event.registerEntityRenderer(DigitalEntities.BEARMON.get(), BearmonRender::new);
        event.registerEntityRenderer(DigitalEntities.KUNEMON.get(), KunemonRender::new);
        event.registerEntityRenderer(DigitalEntities.GIGIMON.get(), GigimonRender::new);
        event.registerEntityRenderer(DigitalEntities.GUILMON.get(), GuilmonRender::new);
        event.registerEntityRenderer(DigitalEntities.PUYOYOMON.get(), PuyoyomonRender::new);
        event.registerEntityRenderer(DigitalEntities.JELLYMON.get(), JellymonRender::new);
        event.registerEntityRenderer(DigitalEntities.TESLAJELLYMON.get(), TeslajellymonRender::new);
        event.registerEntityRenderer(DigitalEntities.GROWLMON.get(), GrowlmonRender::new);
        event.registerEntityRenderer(DigitalEntities.KUWAGAMON.get(), KuwagamonRender::new);
        event.registerEntityRenderer(DigitalEntities.BABYDMON.get(), BabydmonRender::new);
        event.registerEntityRenderer(DigitalEntities.DRACOMON.get(), DracomonRender::new);
        event.registerEntityRenderer(DigitalEntities.COREDRAMON.get(), CoredramonRender::new);
        event.registerEntityRenderer(DigitalEntities.BIBIMON.get(), BibimonRender::new);
        event.registerEntityRenderer(DigitalEntities.PULSEMON.get(), PulsemonRender::new);
        event.registerEntityRenderer(DigitalEntities.BULKMON.get(), BulkmonRender::new);
        event.registerEntityRenderer(DigitalEntities.AGUMONBLACK.get(), AgumonBlackRender::new);
        event.registerEntityRenderer(DigitalEntities.DARKTYRANNOMON.get(), DarkTyrannomonRender::new);
        event.registerEntityRenderer(DigitalEntities.TYRANNOMON.get(), TyrannomonRender::new);
        event.registerEntityRenderer(DigitalEntities.VEEDRAMON.get(), VeedramonRender::new);
        event.registerEntityRenderer(DigitalEntities.CHAKMON.get(), ChakmonRender::new);
        event.registerEntityRenderer(DigitalEntities.BLACKGAOGAMON.get(), BlackGaogamonRender::new);
        event.registerEntityRenderer(DigitalEntities.YOKOMON.get(), YokomonRender::new);
        event.registerEntityRenderer(DigitalEntities.BIYOMON.get(), BiyomonRender::new);
        event.registerEntityRenderer(DigitalEntities.BIRDRAMON.get(), BirdramonRender::new);
        event.registerEntityRenderer(DigitalEntities.AKATORIMON.get(), AkatorimonRender::new);
        event.registerEntityRenderer(DigitalEntities.SABERDRAMON.get(), SaberdramonRender::new);
        event.registerEntityRenderer(DigitalEntities.NAMAKEMON.get(), NamakemonRender::new);
        event.registerEntityRenderer(DigitalEntities.EXERMON.get(), ExermonRender::new);
        event.registerEntityRenderer(DigitalEntities.GREYMONVIRUS.get(), GreymonVirusRender::new);
        event.registerEntityRenderer(DigitalEntities.DARKTYLIZZARDMON.get(), GreymonVirusRender::new);

        event.registerEntityRenderer(DigitalEntities.BULLET.get(), CustomProjectileRender::new);
        event.registerEntityRenderer(DigitalEntities.PUNCHING_BAG.get(), PunchingBagRender::new);
    }
}
