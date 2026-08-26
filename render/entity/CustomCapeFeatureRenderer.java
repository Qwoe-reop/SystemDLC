package dev.mark.system.render.entity;

import dev.mark.system.core.ClientMain;
import dev.mark.system.core.FriendManager;
import dev.mark.system.hook.GameRendererInitHook;
import dev.mark.system.module.render.CapeModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.equipment.EquipmentModel.LayerType;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerCapeModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CustomCapeFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private final BipedEntityModel<PlayerEntityRenderState> model;
    private final EquipmentModelLoader equipmentModelLoader;

    public CustomCapeFeatureRenderer(
        FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> featurerenderercontext,
        LoadedEntityModels loadedentitymodels,
        EquipmentModelLoader equipmentmodelloader
    ) {
        super(featurerenderercontext);
        this.model = new PlayerCapeModel(loadedentitymodels.method_32072(EntityModelLayers.field_52980));
        this.equipmentModelLoader = equipmentmodelloader;
    }

    private boolean hasCustomModelForLayer(ItemStack itemstack, LayerType layertype) {
        EquippableComponent equippablecomponent = (EquippableComponent)itemstack.method_57824(DataComponentTypes.field_54196);
        if (equippablecomponent != null && !equippablecomponent.comp_3176().isEmpty()) {
            EquipmentModel equipmentmodel = this.equipmentModelLoader.method_64087((RegistryKey)equippablecomponent.comp_3176().get());
            return !equipmentmodel.method_63996(layertype).isEmpty();
        } else {
            return false;
        }
    }

    public void render(
        MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, PlayerEntityRenderState playerentityrenderstate, float f, float f1
    ) {
        MinecraftClient minecraftclient = MinecraftClient.method_1551();
        if (minecraftclient.field_1724 != null
            && minecraftclient.field_1687 != null
            && minecraftclient.field_1687.method_8469(playerentityrenderstate.field_53528) instanceof PlayerEntity playerentity) {
            CapeModule capemodule = ClientMain.getInstance().getModuleManager().getModule(CapeModule.class);
            if (capemodule != null
                && capemodule.isEnabled()
                && !playerentityrenderstate.field_53333
                && playerentityrenderstate.field_53532
                && (playerentity.method_5628() == minecraftclient.field_1724.method_5628() || FriendManager.getInstance().isFriendPlayer(playerentity))) {
                Identifier identifier = GameRendererInitHook.cape;
                if (!this.hasCustomModelForLayer(playerentityrenderstate.field_53418, LayerType.field_54127)) {
                    matrixstack.method_22903();
                    if (this.hasCustomModelForLayer(playerentityrenderstate.field_53418, LayerType.field_54125)) {
                        matrixstack.method_46416(0.0F, -0.053125F, 0.06875F);
                    }

                    VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(RenderLayer.method_23572(identifier));
                    ((PlayerEntityModel)this.method_17165()).method_64254(this.model);
                    this.model.method_17087(playerentityrenderstate);
                    this.model.method_60879(matrixstack, vertexconsumer, i, OverlayTexture.field_21444);
                    matrixstack.method_22909();
                }
            }
        }
    }
}
