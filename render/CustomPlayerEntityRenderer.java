package dev.mark.system.render;

import dev.mark.system.core.ClientMain;
import dev.mark.system.core.FriendManager;
import dev.mark.system.module.render.ProtestModule;
import dev.mark.system.module.visual.NameTagsModule;
import dev.mark.system.module.visual.PlayerScalerModule;
import dev.mark.system.render.entity.CustomCapeFeatureRenderer;
import dev.mark.system.render.entity.ParrotFeatureRenderer;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity.Variant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.consume.UseAction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CustomPlayerEntityRenderer extends PlayerEntityRenderer {
    public CustomPlayerEntityRenderer(Context context, boolean flag) {
        super(context, flag);
        this.field_4738.clear();
        this.method_4046(
            new ArmorFeatureRenderer(
                this,
                new ArmorEntityModel(context.method_32167(flag ? EntityModelLayers.field_27582 : EntityModelLayers.field_27579)),
                new ArmorEntityModel(context.method_32167(flag ? EntityModelLayers.field_27583 : EntityModelLayers.field_27580)),
                context.method_64072()
            )
        );
        this.method_4046(new PlayerHeldItemFeatureRenderer(this));
        this.method_4046(new StuckArrowsFeatureRenderer(this, context));
        this.method_4046(new CustomCapeFeatureRenderer(this, context.method_32170(), context.method_64071()));
        this.method_4046(new ParrotFeatureRenderer(this, context.method_32170()));
        this.method_4046(new HeadFeatureRenderer(this, context.method_32170()));
        this.method_4046(new ElytraFeatureRenderer(this, context.method_32170(), context.method_64072()));
        this.method_4046(new TridentRiptideFeatureRenderer(this, context.method_32170()));
        this.method_4046(new StuckStingersFeatureRenderer(this, context));
    }

    protected boolean method_62606(PlayerEntityRenderState playerentityrenderstate) {
        return !playerentityrenderstate.field_53542;
    }

    public Vec3d method_23206(PlayerEntityRenderState playerentityrenderstate) {
        Vec3d vec3d = super.method_23206(playerentityrenderstate);
        return playerentityrenderstate.field_53410 ? vec3d.method_1031(0.0, playerentityrenderstate.field_53453 * -2.0F / 16.0, 0.0) : vec3d;
    }

    private static ArmPose getArmPose(AbstractClientPlayerEntity abstractclientplayerentity, Arm arm) {
        ItemStack itemstack = abstractclientplayerentity.method_5998(Hand.field_5808);
        ItemStack itemstack1 = abstractclientplayerentity.method_5998(Hand.field_5810);
        ArmPose armpose = getArmPose(abstractclientplayerentity, itemstack, Hand.field_5808);
        ArmPose armpose1 = getArmPose(abstractclientplayerentity, itemstack1, Hand.field_5810);
        if (armpose.method_30156()) {
            armpose1 = itemstack1.method_7960() ? ArmPose.field_3409 : ArmPose.field_3410;
        }

        return abstractclientplayerentity.method_6068() == arm ? armpose : armpose1;
    }

    private static ArmPose getArmPose(PlayerEntity playerentity, ItemStack itemstack, Hand hand) {
        if (itemstack.method_7960()) {
            return ArmPose.field_3409;
        }

        if (playerentity.method_6058() == hand && playerentity.method_6014() > 0) {
            UseAction useaction = itemstack.method_7976();
            if (useaction == UseAction.field_8949) {
                return ArmPose.field_3406;
            }

            if (useaction == UseAction.field_8953) {
                return ArmPose.field_3403;
            }

            if (useaction == UseAction.field_8951) {
                return ArmPose.field_3407;
            }

            if (useaction == UseAction.field_8947) {
                return ArmPose.field_3405;
            }

            if (useaction == UseAction.field_27079) {
                return ArmPose.field_27434;
            }

            if (useaction == UseAction.field_39058) {
                return ArmPose.field_39071;
            }

            if (useaction == UseAction.field_42717) {
                return ArmPose.field_42877;
            }
        } else if (!playerentity.field_6252 && itemstack.method_31574(Items.field_8399) && CrossbowItem.method_7781(itemstack)) {
            return ArmPose.field_3408;
        }

        return ArmPose.field_3410;
    }

    public Identifier method_4216(PlayerEntityRenderState playerentityrenderstate) {
        return playerentityrenderstate.field_53520.comp_1626();
    }

    protected void method_4217(PlayerEntityRenderState playerentityrenderstate, MatrixStack matrixstack) {
        PlayerScalerModule playerscalermodule = ClientMain.getInstance().getModuleManager().getModule(PlayerScalerModule.class);
        boolean flag = MinecraftClient.method_1551().field_1724 != null
            && MinecraftClient.method_1551().field_1724.method_5628() == playerentityrenderstate.field_53528;
        if (playerscalermodule == null || !playerscalermodule.isEnabled() || flag) {
            matrixstack.method_22905(0.9375F, 0.9375F, 0.9375F);
        } else if (playerscalermodule.a().getValue()) {
            float f3 = playerscalermodule.b().getFloatValue();
            matrixstack.method_22905(f3, f3, f3);
        } else {
            float f = playerscalermodule.c().getFloatValue();
            float f1 = playerscalermodule.d().getFloatValue();
            float f2 = playerscalermodule.e().getFloatValue();
            matrixstack.method_22905(f, f1, f2);
        }
    }

    protected void method_4213(
        PlayerEntityRenderState playerentityrenderstate, Text text, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i
    ) {
        NameTagsModule nametagsmodule = ClientMain.getInstance().getModuleManager().getModule(NameTagsModule.class);
        if (nametagsmodule == null || !nametagsmodule.isEnabled()) {
            text = this.protectText(text);
            matrixstack.method_22903();
            if (playerentityrenderstate.field_53525 != null) {
                super.method_4213(playerentityrenderstate, this.protectText(playerentityrenderstate.field_53525), matrixstack, vertexconsumerprovider, i);
                matrixstack.method_46416(0.0F, 0.25875F, 0.0F);
            }

            super.method_4213(playerentityrenderstate, text, matrixstack, vertexconsumerprovider, i);
            matrixstack.method_22909();
        }
    }

    private Text protectText(Text text) {
        MinecraftClient minecraftclient = MinecraftClient.method_1551();
        if (minecraftclient.field_1724 == null) {
            return text;
        }

        ProtestModule protestmodule = ClientMain.getInstance().getModuleManager().getModule(ProtestModule.class);
        if (protestmodule != null && protestmodule.isEnabled()) {
            String s = text.getString();
            boolean flag = false;
            if (protestmodule.d().getValue()) {
                String s1 = minecraftclient.field_1724.method_7334().getName();
                if (s.contains(s1)) {
                    s = s.replace(s1, "SуstemPlayer");
                    flag = true;
                }
            }

            if (protestmodule.e().getValue()) {
                for (String s2 : FriendManager.getInstance().getFriends()) {
                    String s4 = Pattern.quote(s2);
                    String s3 = s.replaceAll("(?i)" + s4, "SуstemFriend");
                    if (!s3.equals(s)) {
                        s = s3;
                        flag = true;
                    }
                }
            }

            return (Text)(flag ? Text.method_43470(s).method_10862(text.method_10866()) : text);
        } else {
            return text;
        }
    }

    public PlayerEntityRenderState method_62608() {
        return new PlayerEntityRenderState();
    }

    public void method_62604(AbstractClientPlayerEntity abstractclientplayerentity, PlayerEntityRenderState playerentityrenderstate, float f) {
        super.method_62604(abstractclientplayerentity, playerentityrenderstate, f);
        BipedEntityRenderer.method_62461(abstractclientplayerentity, playerentityrenderstate, f, this.field_55298);
        playerentityrenderstate.field_55306 = getArmPose(abstractclientplayerentity, Arm.field_6182);
        playerentityrenderstate.field_55304 = getArmPose(abstractclientplayerentity, Arm.field_6183);
        playerentityrenderstate.field_53520 = abstractclientplayerentity.method_52814();
        playerentityrenderstate.field_53539 = abstractclientplayerentity.method_6022();
        playerentityrenderstate.field_53540 = abstractclientplayerentity.method_21753();
        playerentityrenderstate.field_53541 = abstractclientplayerentity.method_6014();
        playerentityrenderstate.field_53522 = abstractclientplayerentity.field_6252;
        playerentityrenderstate.field_53542 = abstractclientplayerentity.method_7325();
        playerentityrenderstate.field_53543 = abstractclientplayerentity.method_7348(PlayerModelPart.field_7563);
        playerentityrenderstate.field_53544 = abstractclientplayerentity.method_7348(PlayerModelPart.field_7564);
        playerentityrenderstate.field_53545 = abstractclientplayerentity.method_7348(PlayerModelPart.field_7566);
        playerentityrenderstate.field_53546 = abstractclientplayerentity.method_7348(PlayerModelPart.field_7565);
        playerentityrenderstate.field_53530 = abstractclientplayerentity.method_7348(PlayerModelPart.field_7568);
        playerentityrenderstate.field_53531 = abstractclientplayerentity.method_7348(PlayerModelPart.field_7570);
        playerentityrenderstate.field_53532 = abstractclientplayerentity.method_7348(PlayerModelPart.field_7559);
        updateGliding(abstractclientplayerentity, playerentityrenderstate, f);
        updateCape(abstractclientplayerentity, playerentityrenderstate, f);
        if (playerentityrenderstate.field_53332 < 100.0) {
            Scoreboard scoreboard = abstractclientplayerentity.method_7327();
            ScoreboardObjective scoreboardobjective = scoreboard.method_1189(ScoreboardDisplaySlot.field_45158);
            if (scoreboardobjective != null) {
                ReadableScoreboardScore readablescoreboardscore = scoreboard.method_55430(abstractclientplayerentity, scoreboardobjective);
                MutableText mutabletext = ReadableScoreboardScore.method_55398(
                    readablescoreboardscore, scoreboardobjective.method_55380(StyledNumberFormat.field_47566)
                );
                playerentityrenderstate.field_53525 = Text.method_43473()
                    .method_10852(mutabletext)
                    .method_10852(ScreenTexts.field_41874)
                    .method_10852(scoreboardobjective.method_1114());
            } else {
                playerentityrenderstate.field_53525 = null;
            }
        } else {
            playerentityrenderstate.field_53525 = null;
        }

        playerentityrenderstate.field_53526 = getShoulderParrotVariant(abstractclientplayerentity, true);
        playerentityrenderstate.field_53527 = getShoulderParrotVariant(abstractclientplayerentity, false);
        playerentityrenderstate.field_53528 = abstractclientplayerentity.method_5628();
        playerentityrenderstate.field_53529 = abstractclientplayerentity.method_7334().getName();
        playerentityrenderstate.field_55317.method_65605();
        if (playerentityrenderstate.field_53414) {
            ItemStack itemstack = abstractclientplayerentity.method_5998(playerentityrenderstate.field_53409);
            if (itemstack.method_31574(Items.field_27070)) {
                this.field_55298
                    .method_65597(playerentityrenderstate.field_55317, itemstack, ModelTransformationMode.field_4316, false, abstractclientplayerentity);
            }
        }
    }

    private static void updateGliding(AbstractClientPlayerEntity abstractclientplayerentity, PlayerEntityRenderState playerentityrenderstate, float f) {
        playerentityrenderstate.field_53534 = abstractclientplayerentity.method_6003() + f;
        Vec3d vec3d = abstractclientplayerentity.method_5828(f);
        Vec3d vec3d1 = abstractclientplayerentity.method_49339(f);
        double d0 = vec3d1.method_37268();
        double d1 = vec3d.method_37268();
        if (d0 > 0.0 && d1 > 0.0) {
            playerentityrenderstate.field_53535 = true;
            double d2 = Math.min(1.0, (vec3d1.field_1352 * vec3d.field_1352 + vec3d1.field_1350 * vec3d.field_1350) / Math.sqrt(d0 * d1));
            double d3 = vec3d1.field_1352 * vec3d.field_1350 - vec3d1.field_1350 * vec3d.field_1352;
            playerentityrenderstate.field_53521 = (float)(Math.signum(d3) * Math.acos(d2));
        } else {
            playerentityrenderstate.field_53535 = false;
            playerentityrenderstate.field_53521 = 0.0F;
        }
    }

    private static void updateCape(AbstractClientPlayerEntity abstractclientplayerentity, PlayerEntityRenderState playerentityrenderstate, float f) {
        double d0 = MathHelper.method_16436(f, abstractclientplayerentity.field_7524, abstractclientplayerentity.field_7500)
            - MathHelper.method_16436(f, abstractclientplayerentity.field_6014, abstractclientplayerentity.method_23317());
        double d1 = MathHelper.method_16436(f, abstractclientplayerentity.field_7502, abstractclientplayerentity.field_7521)
            - MathHelper.method_16436(f, abstractclientplayerentity.field_6036, abstractclientplayerentity.method_23318());
        double d2 = MathHelper.method_16436(f, abstractclientplayerentity.field_7522, abstractclientplayerentity.field_7499)
            - MathHelper.method_16436(f, abstractclientplayerentity.field_5969, abstractclientplayerentity.method_23321());
        float f1 = MathHelper.method_17821(f, abstractclientplayerentity.field_6220, abstractclientplayerentity.field_6283);
        double d3 = MathHelper.method_15374(f1 * (float) (Math.PI / 180.0));
        double d4 = -MathHelper.method_15362(f1 * (float) (Math.PI / 180.0));
        playerentityrenderstate.field_53536 = (float)d1 * 10.0F;
        playerentityrenderstate.field_53536 = MathHelper.method_15363(playerentityrenderstate.field_53536, -6.0F, 32.0F);
        playerentityrenderstate.field_53537 = (float)(d0 * d3 + d2 * d4) * 100.0F;
        playerentityrenderstate.field_53537 = playerentityrenderstate.field_53537 * (1.0F - playerentityrenderstate.method_64259());
        playerentityrenderstate.field_53537 = MathHelper.method_15363(playerentityrenderstate.field_53537, 0.0F, 150.0F);
        playerentityrenderstate.field_53538 = (float)(d0 * d4 - d2 * d3) * 100.0F;
        playerentityrenderstate.field_53538 = MathHelper.method_15363(playerentityrenderstate.field_53538, -20.0F, 20.0F);
        float f2 = MathHelper.method_16439(f, abstractclientplayerentity.field_7505, abstractclientplayerentity.field_7483);
        float f3 = MathHelper.method_16439(f, abstractclientplayerentity.field_53038, abstractclientplayerentity.field_53039);
        playerentityrenderstate.field_53536 = playerentityrenderstate.field_53536 + MathHelper.method_15374(f3 * 6.0F) * 32.0F * f2;
    }

    @Nullable
    private static Variant getShoulderParrotVariant(AbstractClientPlayerEntity abstractclientplayerentity, boolean flag) {
        NbtCompound nbtcompound = flag ? abstractclientplayerentity.method_7356() : abstractclientplayerentity.method_7308();
        return EntityType.method_5898(nbtcompound.method_10558("id")).filter(entitytype -> entitytype == EntityType.field_6104).isPresent()
            ? Variant.method_47850(nbtcompound.method_10550("Variant"))
            : null;
    }

    public void method_4220(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, Identifier identifier, boolean flag) {
        this.renderArm(matrixstack, vertexconsumerprovider, i, identifier, ((PlayerEntityModel)this.field_4737).field_3401, flag);
    }

    public void method_4221(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, Identifier identifier, boolean flag) {
        this.renderArm(matrixstack, vertexconsumerprovider, i, identifier, ((PlayerEntityModel)this.field_4737).field_27433, flag);
    }

    private void renderArm(
        MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, Identifier identifier, ModelPart modelpart, boolean flag
    ) {
        PlayerEntityModel playerentitymodel = (PlayerEntityModel)this.method_4038();
        modelpart.method_41923();
        modelpart.field_3665 = true;
        playerentitymodel.field_3484.field_3665 = flag;
        playerentitymodel.field_3486.field_3665 = flag;
        playerentitymodel.field_27433.field_3674 = -0.1F;
        playerentitymodel.field_3401.field_3674 = 0.1F;
        modelpart.method_22698(matrixstack, vertexconsumerprovider.getBuffer(RenderLayer.method_23580(identifier)), i, OverlayTexture.field_21444);
    }

    protected void method_4212(PlayerEntityRenderState playerentityrenderstate, MatrixStack matrixstack, float f, float f1) {
        super.method_4212(playerentityrenderstate, matrixstack, f, f1);
        if (playerentityrenderstate.field_53411) {
            if (!playerentityrenderstate.field_53459) {
                matrixstack.method_22907(RotationAxis.field_40714.rotationDegrees(0.0F));
            }

            if (playerentityrenderstate.field_53535) {
                matrixstack.method_22907(RotationAxis.field_40716.rotation(playerentityrenderstate.field_53521));
            }
        }
    }
}
