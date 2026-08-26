package dev.mark.system.render.entity;

import dev.mark.system.core.ClientMain;
import dev.mark.system.module.render.ParrotModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.ParrotEntityModel.Pose;
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity.Variant;

@Environment(EnvType.CLIENT)
public class ParrotFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private final ParrotEntityModel model;
    private final ParrotEntityRenderState parrotState = new ParrotEntityRenderState();

    public ParrotFeatureRenderer(
        FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> featurerenderercontext, LoadedEntityModels loadedentitymodels
    ) {
        super(featurerenderercontext);
        this.model = new ParrotEntityModel(loadedentitymodels.method_32072(EntityModelLayers.field_27619));
        this.parrotState.field_53512 = Pose.field_3464;
    }

    public void render(
        MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, PlayerEntityRenderState playerentityrenderstate, float f, float f1
    ) {
        if (MinecraftClient.method_1551().method_53462().getName().equals(playerentityrenderstate.field_53529)) {
            ParrotModule parrotmodule = ClientMain.getInstance().getModuleManager().getModule(ParrotModule.class);
            if (parrotmodule != null && parrotmodule.isEnabled()) {
                String s = parrotmodule.b().getFirst();
                Variant variant = this.getVariantByName(s);
                String s1 = parrotmodule.a().getFirst();
                switch (s1) {
                    case "Справа":
                        this.render(matrixstack, vertexconsumerprovider, i, playerentityrenderstate, variant, f, f1, true);
                        return;
                    case "Слева":
                        this.render(matrixstack, vertexconsumerprovider, i, playerentityrenderstate, variant, f, f1, false);
                }
            }
        }
    }

    private Variant getVariantByName(String s) {
        switch (s) {
            case "Синий":
                return Variant.field_41551;
            case "Зеленый":
                return Variant.field_41552;
            case "Красно-синий":
                return Variant.field_41550;
            default:
                return Variant.field_41553;
        }
    }

    private void render(
        MatrixStack matrixstack,
        VertexConsumerProvider vertexconsumerprovider,
        int i,
        PlayerEntityRenderState playerentityrenderstate,
        Variant variant,
        float f,
        float f1,
        boolean flag
    ) {
        matrixstack.method_22903();
        matrixstack.method_46416(flag ? 0.4F : -0.4F, playerentityrenderstate.field_53410 ? -1.3F : -1.5F, 0.0F);
        this.parrotState.field_53328 = playerentityrenderstate.field_53328;
        this.parrotState.field_53450 = playerentityrenderstate.field_53450;
        this.parrotState.field_53451 = playerentityrenderstate.field_53451;
        this.parrotState.field_53447 = f;
        this.parrotState.field_53448 = f1;
        this.model.method_17112(this.parrotState);
        this.model
            .method_60879(
                matrixstack,
                vertexconsumerprovider.getBuffer(this.model.method_23500(ParrotEntityRenderer.method_47906(variant))),
                i,
                OverlayTexture.field_21444
            );
        matrixstack.method_22909();
    }
}
