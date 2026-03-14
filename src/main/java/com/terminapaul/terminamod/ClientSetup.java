package com.terminapaul.terminamod;

import com.terminapaul.terminamod.client.model.RedCapModel;
import com.terminapaul.terminamod.client.model.SmelterModel;
import com.terminapaul.terminamod.client.renderer.RedCapArmorRenderer;
import com.terminapaul.terminamod.client.renderer.RedCapItemRenderer;
import com.terminapaul.terminamod.client.renderer.SmelterModelLayers;
import com.terminapaul.terminamod.client.renderer.SmelterRenderer;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TerminaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SmelterModelLayers.SMELTER, SmelterModel::createBodyLayer);
        event.registerLayerDefinition(RedCapItemRenderer.RED_CAP_LAYER, RedCapModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SMELTER.get(), SmelterRenderer::new);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        // Joueur
        for (String skin : event.getSkins()) {
            var renderer = event.getSkin(skin);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new RedCapArmorRenderer<>(playerRenderer));
            }
        }

        // Armor stand
        var armorStandRenderer = event.getRenderer(EntityType.ARMOR_STAND);
        if (armorStandRenderer instanceof ArmorStandRenderer armorStand) {
            armorStand.addLayer(new RedCapArmorRenderer<>(armorStand));
        }
    }
}