package dev.mark.system.render;

import dev.mark.system.core.ClientMain;
import dev.mark.system.module.render.NoRenderModule;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.BlockPos.Mutable;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class BlockOverlayRenderer {
    private static final Identifier UNDERWATER_TEXTURE = Identifier.method_60656("textures/misc/underwater.png");

    public static void renderOverlays(MinecraftClient minecraftclient, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider) {
        ClientPlayerEntity clientplayerentity = minecraftclient.field_1724;
        if (!clientplayerentity.field_5960) {
            BlockState blockstate = getInWallBlockState(clientplayerentity);
            if (blockstate != null) {
                renderInWallOverlay(minecraftclient.method_1541().method_3351().method_3339(blockstate), matrixstack, vertexconsumerprovider);
            }
        }

        if (!minecraftclient.field_1724.method_7325()) {
            if (minecraftclient.field_1724.method_5777(FluidTags.field_15517)) {
                renderUnderwaterOverlay(minecraftclient, matrixstack, vertexconsumerprovider);
            }

            if (minecraftclient.field_1724.method_5809()) {
                renderFireOverlay(matrixstack, vertexconsumerprovider);
            }
        }
    }

    @Nullable
    private static BlockState getInWallBlockState(PlayerEntity playerentity) {
        Mutable mutable = new Mutable();

        for (int i = 0; i < 8; i++) {
            double d0 = playerentity.method_23317() + ((i >> 0) % 2 - 0.5F) * playerentity.method_17681() * 0.8F;
            double d1 = playerentity.method_23320() + ((i >> 1) % 2 - 0.5F) * 0.1F * playerentity.method_55693();
            double d2 = playerentity.method_23321() + ((i >> 2) % 2 - 0.5F) * playerentity.method_17681() * 0.8F;
            mutable.method_10102(d0, d1, d2);
            BlockState blockstate = playerentity.method_37908().method_8320(mutable);
            if (blockstate.method_26217() != BlockRenderType.field_11455 && blockstate.method_26230(playerentity.method_37908(), mutable)) {
                return blockstate;
            }
        }

        return null;
    }

    private static void renderInWallOverlay(Sprite sprite, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider) {
        NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
        if (NoRenderModule == null || !NoRenderModule.isEnabled()) {
            float f = 0.1F;
            int i = ColorHelper.method_61318(1.0F, 0.1F, 0.1F, 0.1F);
            float f1 = -1.0F;
            float f2 = 1.0F;
            float f3 = -1.0F;
            float f4 = 1.0F;
            float f5 = -0.5F;
            float f6 = sprite.method_4594();
            float f7 = sprite.method_4577();
            float f8 = sprite.method_4593();
            float f9 = sprite.method_4575();
            Matrix4f matrix4f = matrixstack.method_23760().method_23761();
            VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(RenderLayer.method_65216(sprite.method_45852()));
            vertexconsumer.method_22918(matrix4f, -1.0F, -1.0F, -0.5F).method_22913(f7, f9).method_39415(i);
            vertexconsumer.method_22918(matrix4f, 1.0F, -1.0F, -0.5F).method_22913(f6, f9).method_39415(i);
            vertexconsumer.method_22918(matrix4f, 1.0F, 1.0F, -0.5F).method_22913(f6, f8).method_39415(i);
            vertexconsumer.method_22918(matrix4f, -1.0F, 1.0F, -0.5F).method_22913(f7, f8).method_39415(i);
        }
    }

    private static void renderUnderwaterOverlay(MinecraftClient minecraftclient, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider) {
        BlockPos blockpos = BlockPos.method_49637(
            minecraftclient.field_1724.method_23317(), minecraftclient.field_1724.method_23320(), minecraftclient.field_1724.method_23321()
        );
        float f = LightmapTextureManager.method_23284(
            minecraftclient.field_1724.method_37908().method_8597(), minecraftclient.field_1724.method_37908().method_22339(blockpos)
        );
        int i = ColorHelper.method_61318(0.1F, f, f, f);
        float f1 = 4.0F;
        float f2 = -1.0F;
        float f3 = 1.0F;
        float f4 = -1.0F;
        float f5 = 1.0F;
        float f6 = -0.5F;
        float f7 = -minecraftclient.field_1724.method_36454() / 64.0F;
        float f8 = minecraftclient.field_1724.method_36455() / 64.0F;
        Matrix4f matrix4f = matrixstack.method_23760().method_23761();
        VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(RenderLayer.method_65216(UNDERWATER_TEXTURE));
        vertexconsumer.method_22918(matrix4f, -1.0F, -1.0F, -0.5F).method_22913(4.0F + f7, 4.0F + f8).method_39415(i);
        vertexconsumer.method_22918(matrix4f, 1.0F, -1.0F, -0.5F).method_22913(0.0F + f7, 4.0F + f8).method_39415(i);
        vertexconsumer.method_22918(matrix4f, 1.0F, 1.0F, -0.5F).method_22913(0.0F + f7, 0.0F + f8).method_39415(i);
        vertexconsumer.method_22918(matrix4f, -1.0F, 1.0F, -0.5F).method_22913(4.0F + f7, 0.0F + f8).method_39415(i);
    }

    private static void renderFireOverlay(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider) {
        NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
        if (NoRenderModule == null || !NoRenderModule.isEnabled() || !NoRenderModule.fireSetting.getValue()) {
            Sprite sprite = ModelBaker.field_5370.method_24148();
            VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(RenderLayer.method_65217(sprite.method_45852()));
            float f = sprite.method_4594();
            float f1 = sprite.method_4577();
            float f2 = (f + f1) / 2.0F;
            float f3 = sprite.method_4593();
            float f4 = sprite.method_4575();
            float f5 = (f3 + f4) / 2.0F;
            float f6 = sprite.method_23842();
            float f7 = MathHelper.method_16439(f6, f, f2);
            float f8 = MathHelper.method_16439(f6, f1, f2);
            float f9 = MathHelper.method_16439(f6, f3, f5);
            float f10 = MathHelper.method_16439(f6, f4, f5);
            float f11 = 1.0F;

            for (int i = 0; i < 2; i++) {
                matrixstack.method_22903();
                float f12 = -0.5F;
                float f13 = 0.5F;
                float f14 = -0.5F;
                float f15 = 0.5F;
                float f16 = -0.5F;
                matrixstack.method_46416(-(i * 2 - 1) * 0.24F, -0.3F, 0.0F);
                matrixstack.method_22907(RotationAxis.field_40716.rotationDegrees((i * 2 - 1) * 10.0F));
                Matrix4f matrix4f = matrixstack.method_23760().method_23761();
                vertexconsumer.method_22918(matrix4f, -0.5F, -0.5F, -0.5F).method_22913(f8, f10).method_22915(1.0F, 1.0F, 1.0F, 0.9F);
                vertexconsumer.method_22918(matrix4f, 0.5F, -0.5F, -0.5F).method_22913(f7, f10).method_22915(1.0F, 1.0F, 1.0F, 0.9F);
                vertexconsumer.method_22918(matrix4f, 0.5F, 0.5F, -0.5F).method_22913(f7, f9).method_22915(1.0F, 1.0F, 1.0F, 0.9F);
                vertexconsumer.method_22918(matrix4f, -0.5F, 0.5F, -0.5F).method_22913(f8, f9).method_22915(1.0F, 1.0F, 1.0F, 0.9F);
                matrixstack.method_22909();
            }
        }
    }
}
