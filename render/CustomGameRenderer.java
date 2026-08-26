package dev.mark.system.render;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import dev.mark.system.core.ClientMain;
import dev.mark.system.module.render.AspectRatioModule;
import dev.mark.system.module.render.BlockOutlineModule;
import dev.mark.system.module.render.HandTweaksModule;
import dev.mark.system.module.render.NoRenderModule;
import dev.mark.system.module.render.ZoomModule;
import dev.mark.system.module.visual.PlayerOutlinesModule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.ShaderLoader.LoadException;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Pool;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class CustomGameRenderer extends GameRenderer {
    private static final Identifier field_53899 = Identifier.method_60656("blur");
    public static final int field_49904 = 10;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean field_32688 = false;
    public static final float CAMERA_DEPTH = 0.05F;
    private static final float field_44940 = 1000.0F;
    private final MinecraftClient client;
    private final ResourceManager resourceManager;
    private final Random random = Random.method_43047();
    private float viewDistance;
    public final CustomHeldItemRenderer firstPersonRenderer;
    private final BufferBuilderStorage buffers;
    private int ticks;
    private float fovMultiplier;
    private float lastFovMultiplier;
    private float skyDarkness;
    private float lastSkyDarkness;
    private boolean renderHand = true;
    private boolean blockOutlineEnabled = true;
    private long lastWorldIconUpdate;
    private boolean hasWorldIcon;
    private long lastWindowFocusedTime = Util.method_658();
    private final LightmapTextureManager lightmapTextureManager;
    private final OverlayTexture overlayTexture = new OverlayTexture();
    private boolean renderingPanorama;
    private float zoom = 1.0F;
    private float zoomX;
    private float zoomY;
    public static final int field_32687 = 40;
    @Nullable
    private ItemStack floatingItem;
    private int floatingItemTimeLeft;
    private float floatingItemWidth;
    private float floatingItemHeight;
    private final Pool pool = new Pool(3);
    @Nullable
    private Identifier postProcessorId;
    private boolean postProcessorEnabled;
    private final Camera camera = new Camera();
    public static CustomGameRenderer instance;

    public CustomHeldItemRenderer getFirstPersonRenderer() {
        return this.firstPersonRenderer;
    }

    public CustomGameRenderer(
        MinecraftClient minecraftclient,
        CustomHeldItemRenderer customhelditemrenderer,
        ResourceManager resourcemanager,
        BufferBuilderStorage bufferbuilderstorage
    ) {
        super(minecraftclient, customhelditemrenderer, resourcemanager, bufferbuilderstorage);
        this.client = minecraftclient;
        this.resourceManager = resourcemanager;
        this.firstPersonRenderer = customhelditemrenderer;
        this.lightmapTextureManager = new LightmapTextureManager(this, minecraftclient);
        this.buffers = bufferbuilderstorage;
        instance = this;
    }

    public void close() {
        this.lightmapTextureManager.close();
        this.overlayTexture.close();
        this.pool.close();
    }

    public void method_35768(boolean flag) {
        this.renderHand = flag;
    }

    public void method_35769(boolean flag) {
        this.blockOutlineEnabled = flag;
    }

    public void method_35770(boolean flag) {
        this.renderingPanorama = flag;
    }

    public boolean method_35765() {
        return this.renderingPanorama;
    }

    public void method_62905() {
        this.postProcessorId = null;
    }

    public void method_3184() {
        this.postProcessorEnabled = !this.postProcessorEnabled;
    }

    public void method_3167(@Nullable Entity entity) {
        this.postProcessorId = null;
        if (entity instanceof CreeperEntity) {
            this.setPostProcessor(Identifier.method_60656("creeper"));
        } else if (entity instanceof SpiderEntity) {
            this.setPostProcessor(Identifier.method_60656("spider"));
        } else if (entity instanceof EndermanEntity) {
            this.setPostProcessor(Identifier.method_60656("invert"));
        }
    }

    private void setPostProcessor(Identifier identifier) {
        this.postProcessorId = identifier;
        this.postProcessorEnabled = true;
    }

    public void method_57796() {
        float f = this.client.field_1690.method_57703();
        if (!(f < 1.0F)) {
            PostEffectProcessor posteffectprocessor = this.client.method_62887().method_62941(field_53899, DefaultFramebufferSet.field_53902);
            if (posteffectprocessor != null) {
                posteffectprocessor.method_57799("Radius", f);
                posteffectprocessor.method_1258(this.client.method_1522(), this.pool);
            }
        }
    }

    public void method_34521(ResourceFactory resourcefactory) {
        try {
            this.client
                .method_62887()
                .method_62944(
                    resourcefactory, new ShaderProgramKey[]{ShaderProgramKeys.field_53866, ShaderProgramKeys.field_53867, ShaderProgramKeys.field_53880}
                );
        } catch (LoadException loadexception) {
            throw new RuntimeException("Could not preload shaders for loading UI", loadexception);
        } catch (IOException ioexception) {
            throw new RuntimeException("Could not preload shaders for loading UI", ioexception);
        } catch (Exception exception) {
            throw new RuntimeException("Could not preload shaders for loading UI", exception);
        }
    }

    public void method_3182() {
        this.updateFovMultiplier();
        this.lightmapTextureManager.method_3314();
        if (this.client.method_1560() == null) {
            this.client.method_1504(this.client.field_1724);
        }

        this.camera.method_19317();
        this.firstPersonRenderer.method_3220();
        this.ticks++;
        if (this.client.field_1687.method_54719().method_54751()) {
            this.lastSkyDarkness = this.skyDarkness;
            if (this.client.field_1705.method_1740().method_1797()) {
                this.skyDarkness += 0.05F;
                if (this.skyDarkness > 1.0F) {
                    this.skyDarkness = 1.0F;
                }
            } else if (this.skyDarkness > 0.0F) {
                this.skyDarkness -= 0.0125F;
            }

            if (this.floatingItemTimeLeft > 0) {
                this.floatingItemTimeLeft--;
                if (this.floatingItemTimeLeft == 0) {
                    this.floatingItem = null;
                }
            }
        }
    }

    @Nullable
    public Identifier method_62906() {
        return this.postProcessorId;
    }

    public void method_3169(int i, int j) {
        this.pool.method_61950();
        this.client.field_1769.method_3242(i, j);
    }

    public void method_3190(float f) {
        Entity entity = this.client.method_1560();
        if (entity != null && this.client.field_1687 != null && this.client.field_1724 != null) {
            Profilers.method_64146().method_15396("pick");
            double d0 = this.client.field_1724.method_55754();
            double d1 = this.client.field_1724.method_55755();
            HitResult hitresult = this.findCrosshairTarget(entity, d0, d1, f);
            this.client.field_1765 = hitresult;
            MinecraftClient minecraftclient = this.client;
            Entity entity1;
            if (hitresult instanceof EntityHitResult entityhitresult) {
                entity1 = entityhitresult.method_17782();
            } else {
                entity1 = null;
            }

            minecraftclient.field_1692 = entity1;
            Profilers.method_64146().method_15407();
        }
    }

    private HitResult findCrosshairTarget(Entity entity, double d0, double d1, float f) {
        double d2 = Math.max(d0, d1);
        double d3 = MathHelper.method_33723(d2);
        Vec3d vec3d = entity.method_5836(f);
        HitResult hitresult = entity.method_5745(d2, f, false);
        double d4 = hitresult.method_17784().method_1025(vec3d);
        if (hitresult.method_17783() != Type.field_1333) {
            d3 = d4;
            d2 = Math.sqrt(d4);
        }

        Vec3d vec3d1 = entity.method_5828(f);
        Vec3d vec3d2 = vec3d.method_1031(vec3d1.field_1352 * d2, vec3d1.field_1351 * d2, vec3d1.field_1350 * d2);
        float f1 = 1.0F;
        Box box = entity.method_5829().method_18804(vec3d1.method_1021(d2)).method_1009(1.0, 1.0, 1.0);
        EntityHitResult entityhitresult = ProjectileUtil.method_18075(entity, vec3d, vec3d2, box, EntityPredicates.field_52443, d3);
        return entityhitresult != null && entityhitresult.method_17784().method_1025(vec3d) < d4
            ? ensureTargetInRange(entityhitresult, vec3d, d1)
            : ensureTargetInRange(hitresult, vec3d, d0);
    }

    private static HitResult ensureTargetInRange(HitResult hitresult, Vec3d vec3d, double d0) {
        Vec3d vec3d1 = hitresult.method_17784();
        if (!vec3d1.method_24802(vec3d, d0)) {
            Vec3d vec3d2 = hitresult.method_17784();
            Direction direction = Direction.method_10142(
                vec3d2.field_1352 - vec3d.field_1352, vec3d2.field_1351 - vec3d.field_1351, vec3d2.field_1350 - vec3d.field_1350
            );
            return BlockHitResult.method_17778(vec3d2, direction, BlockPos.method_49638(vec3d2));
        } else {
            return hitresult;
        }
    }

    private void updateFovMultiplier() {
        float f;
        if (this.client.method_1560() instanceof AbstractClientPlayerEntity abstractclientplayerentity) {
            GameOptions gameoptions = this.client.field_1690;
            boolean flag = gameoptions.method_31044().method_31034();
            float f1 = ((Double)gameoptions.method_42454().method_41753()).floatValue();
            f = abstractclientplayerentity.method_3118(flag, f1);
        } else {
            f = 1.0F;
        }

        this.lastFovMultiplier = this.fovMultiplier;
        this.fovMultiplier = this.fovMultiplier + (f - this.fovMultiplier) * 0.5F;
        this.fovMultiplier = MathHelper.method_15363(this.fovMultiplier, 0.1F, 1.5F);
    }

    public float getFov(Camera camera, float f, boolean flag) {
        if (this.renderingPanorama) {
            return 90.0F;
        }

        float f1 = 70.0F;
        if (flag) {
            f1 = ((Integer)this.client.field_1690.method_41808().method_41753()).intValue();
            f1 *= MathHelper.method_16439(f, this.lastFovMultiplier, this.fovMultiplier);
        }

        if (camera.method_19331() instanceof LivingEntity livingentity && livingentity.method_29504()) {
            float f2 = Math.min(livingentity.field_6213 + f, 125.0F);
            f1 /= (1.0F - 500.0F / (f2 + 500.0F)) * 2.0F + 1.0F;
        }

        CameraSubmersionType camerasubmersiontype = camera.method_19334();
        if (camerasubmersiontype == CameraSubmersionType.field_27885 || camerasubmersiontype == CameraSubmersionType.field_27886) {
            float f3 = ((Double)this.client.field_1690.method_42454().method_41753()).floatValue();
            f1 *= MathHelper.method_16439(f3, 1.0F, 0.85714287F);
        }

        ZoomModule zoommodule = ZoomModule.o();
        if (zoommodule != null) {
            f1 *= zoommodule.b();
        }

        return f1;
    }

    private void tiltViewWhenHurt(MatrixStack matrixstack, float f) {
        NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
        if ((NoRenderModule == null || !NoRenderModule.isEnabled() || !NoRenderModule.hurtShakeSetting.getValue())
            && this.client.method_1560() instanceof LivingEntity livingentity) {
            float f1 = livingentity.field_6235 - f;
            if (livingentity.method_29504()) {
                float f2 = Math.min(livingentity.field_6213 + f, 20.0F);
                matrixstack.method_22907(RotationAxis.field_40718.rotationDegrees(40.0F - 8000.0F / (f2 + 200.0F)));
            }

            if (f1 < 0.0F) {
                return;
            }

            f1 /= livingentity.field_6254;
            f1 = MathHelper.method_15374(f1 * f1 * f1 * f1 * (float) Math.PI);
            float f4 = livingentity.method_48157();
            matrixstack.method_22907(RotationAxis.field_40716.rotationDegrees(-f4));
            float f3 = (float)(-f1 * 14.0 * (Double)this.client.field_1690.method_48974().method_41753());
            matrixstack.method_22907(RotationAxis.field_40718.rotationDegrees(f3));
            matrixstack.method_22907(RotationAxis.field_40716.rotationDegrees(f4));
        }
    }

    private void bobView(MatrixStack matrixstack, float f) {
        NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
        if ((NoRenderModule == null || !NoRenderModule.isEnabled() || !NoRenderModule.runShakeSetting.getValue())
            && this.client.method_1560() instanceof AbstractClientPlayerEntity abstractclientplayerentity) {
            float f1 = abstractclientplayerentity.field_53039 - abstractclientplayerentity.field_53038;
            float f2 = -(abstractclientplayerentity.field_53039 + f1 * f);
            float f3 = MathHelper.method_16439(f, abstractclientplayerentity.field_7505, abstractclientplayerentity.field_7483);
            matrixstack.method_46416(
                MathHelper.method_15374(f2 * (float) Math.PI) * f3 * 0.5F, -Math.abs(MathHelper.method_15362(f2 * (float) Math.PI) * f3), 0.0F
            );
            matrixstack.method_22907(RotationAxis.field_40718.rotationDegrees(MathHelper.method_15374(f2 * (float) Math.PI) * f3 * 3.0F));
            matrixstack.method_22907(RotationAxis.field_40714.rotationDegrees(Math.abs(MathHelper.method_15362(f2 * (float) Math.PI - 0.2F) * f3) * 5.0F));
        }
    }

    public void method_35766(float f, float f1, float f2) {
        this.zoom = f;
        this.zoomX = f1;
        this.zoomY = f2;
        this.method_35769(false);
        this.method_35768(false);
        this.method_3188(RenderTickCounter.field_51955);
        this.zoom = 1.0F;
    }

    private void renderHand(Camera camera, float f, Matrix4f matrix4f) {
        if (!this.renderingPanorama) {
            Matrix4f matrix4f1 = this.method_22973(this.getFov(camera, f, false));
            RenderSystem.setProjectionMatrix(matrix4f1, ProjectionType.field_54953);
            MatrixStack matrixstack = new MatrixStack();
            matrixstack.method_22903();
            matrixstack.method_34425(matrix4f.invert(new Matrix4f()));
            Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
            matrix4fstack.pushMatrix().mul(matrix4f);
            this.tiltViewWhenHurt(matrixstack, f);
            if ((Boolean)this.client.field_1690.method_42448().method_41753()) {
                this.bobView(matrixstack, f);
            }

            boolean flag = this.client.method_1560() instanceof LivingEntity && ((LivingEntity)this.client.method_1560()).method_6113();
            if (this.client.field_1690.method_31044().method_31034()
                && !flag
                && !this.client.field_1690.field_1842
                && this.client.field_1761.method_2920() != GameMode.field_9219) {
                this.lightmapTextureManager.method_3316();
                this.firstPersonRenderer
                    .method_22976(
                        f, matrixstack, this.buffers.method_23000(), this.client.field_1724, this.client.method_1561().method_23839(this.client.field_1724, f)
                    );
                this.lightmapTextureManager.method_3315();
            }

            matrix4fstack.popMatrix();
            matrixstack.method_22909();
            if (this.client.field_1690.method_31044().method_31034() && !flag) {
                Immediate immediate = this.buffers.method_23000();
                BlockOverlayRenderer.renderOverlays(this.client, matrixstack, immediate);
                immediate.method_22993();
            }
        }
    }

    public Matrix4f method_22973(float f) {
        Matrix4f matrix4f = new Matrix4f();
        if (this.zoom != 1.0F) {
            matrix4f.translate(this.zoomX, -this.zoomY, 0.0F);
            matrix4f.scale(this.zoom, this.zoom, 1.0F);
        }

        AspectRatioModule aspectratiomodule = ClientMain.getInstance().getModuleManager().getModule(AspectRatioModule.class);
        float f1;
        if (aspectratiomodule != null && aspectratiomodule.isEnabled()) {
            f1 = AspectRatioModule.a().getFloatValue();
        } else {
            f1 = (float)this.client.method_22683().method_4489() / this.client.method_22683().method_4506();
        }

        return matrix4f.perspective(f * (float) (Math.PI / 180.0), f1, 0.05F, this.method_32796());
    }

    public float method_32796() {
        return this.viewDistance * 4.0F;
    }

    public void method_3192(RenderTickCounter rendertickcounter, boolean flag) {
        if (!this.client.method_1569()
            && this.client.field_1690.field_1837
            && (!(Boolean)this.client.field_1690.method_42446().method_41753() || !this.client.field_1729.method_1609())) {
            if (Util.method_658() - this.lastWindowFocusedTime > 500L) {
                this.client.method_20539(false);
            }
        } else {
            this.lastWindowFocusedTime = Util.method_658();
        }

        if (!this.client.field_1743) {
            Profiler profiler = Profilers.method_64146();
            boolean flag1 = this.client.method_53466();
            int i = (int)(this.client.field_1729.method_1603() * this.client.method_22683().method_4486() / this.client.method_22683().method_4480());
            int j = (int)(this.client.field_1729.method_1604() * this.client.method_22683().method_4502() / this.client.method_22683().method_4507());
            RenderSystem.viewport(0, 0, this.client.method_22683().method_4489(), this.client.method_22683().method_4506());
            if (flag1 && flag && this.client.field_1687 != null) {
                profiler.method_15396("level");
                this.method_3188(rendertickcounter);
                this.updateWorldIcon();
                PlayerOutlinesModule playeroutlinesmodule = PlayerOutlinesModule.h();
                boolean flag2 = playeroutlinesmodule != null && playeroutlinesmodule.isEnabled();
                NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
                boolean flag3 = NoRenderModule != null && NoRenderModule.isEnabled() && NoRenderModule.glowSetting.getValue();
                if (flag2 && !flag3) {
                    this.client.field_1769.method_3254();
                }

                if (this.postProcessorId != null && this.postProcessorEnabled) {
                    RenderSystem.disableBlend();
                    RenderSystem.disableDepthTest();
                    RenderSystem.resetTextureMatrix();
                    PostEffectProcessor posteffectprocessor = this.client.method_62887().method_62941(this.postProcessorId, DefaultFramebufferSet.field_53902);
                    if (posteffectprocessor != null) {
                        posteffectprocessor.method_1258(this.client.method_1522(), this.pool);
                    }
                }

                this.client.method_1522().method_1235(true);
            }

            Window window = this.client.method_22683();
            RenderSystem.clear(256);
            Matrix4f matrix4f = new Matrix4f()
                .setOrtho(
                    0.0F, (float)(window.method_4489() / window.method_4495()), (float)(window.method_4506() / window.method_4495()), 0.0F, 1000.0F, 21000.0F
                );
            RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.field_54954);
            Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
            matrix4fstack.pushMatrix();
            matrix4fstack.translation(0.0F, 0.0F, -11000.0F);
            DiffuseLighting.method_24211();
            DrawContext drawcontext = new DrawContext(this.client, this.buffers.method_23000());
            if (flag1 && flag && this.client.field_1687 != null) {
                profiler.method_15405("gui");
                if (!this.client.field_1690.field_1842) {
                    this.renderFloatingItem(drawcontext, rendertickcounter.method_60637(false));
                }

                this.client.field_1705.method_1753(drawcontext, rendertickcounter);
                drawcontext.method_51452();
                RenderSystem.clear(256);
                profiler.method_15407();
            }

            if (this.client.method_18506() != null) {
                try {
                    this.client.method_18506().method_25394(drawcontext, i, j, rendertickcounter.method_60636());
                } catch (Throwable throwable3) {
                    LOGGER.error("Error rendering overlay", throwable3);
                }
            } else if (flag1 && this.client.field_1755 != null) {
                try {
                    this.client.field_1755.method_47413(drawcontext, i, j, rendertickcounter.method_60636());
                    ClientMain.getInstance().getEventManager().onRenderHud(drawcontext, rendertickcounter);
                } catch (Throwable throwable2) {
                    LOGGER.error("Error rendering screen", throwable2);
                }

                try {
                    if (this.client.field_1755 != null) {
                        this.client.field_1755.method_37071();
                    }
                } catch (Throwable throwable1) {
                    LOGGER.error("Error narrating screen", throwable1);
                }
            }

            if (flag1 && flag && this.client.field_1687 != null) {
                this.client.field_1705.method_39192(drawcontext, rendertickcounter);
            }

            if (flag1) {
                ScopedProfiler scopedprofiler = profiler.method_64145("toasts");

                try {
                    this.client.method_1566().method_1996(drawcontext);
                } catch (Throwable throwable4) {
                    if (scopedprofiler != null) {
                        try {
                            scopedprofiler.close();
                        } catch (Throwable throwable) {
                            throwable4.addSuppressed(throwable);
                        }
                    }

                    throw throwable4;
                }

                if (scopedprofiler != null) {
                    scopedprofiler.close();
                }
            }

            drawcontext.method_51452();
            matrix4fstack.popMatrix();
            this.pool.method_61947();
        }
    }

    private void updateWorldIcon() {
        if (!this.hasWorldIcon && this.client.method_1542()) {
            long i = Util.method_658();
            if (i - this.lastWorldIconUpdate >= 1000L) {
                this.lastWorldIconUpdate = i;
                IntegratedServer integratedserver = this.client.method_1576();
                if (integratedserver != null && !integratedserver.method_3750()) {
                    integratedserver.method_3725().ifPresent(path -> {
                        if (Files.isRegularFile(path)) {
                            this.hasWorldIcon = true;
                        } else {
                            this.updateWorldIcon(path);
                        }
                    });
                }
            }
        }
    }

    private void updateWorldIcon(Path path) {
        if (this.client.field_1769.method_3246() > 10 && this.client.field_1769.method_3281()) {
            NativeImage nativeimage = ScreenshotRecorder.method_1663(this.client.method_1522());
            Util.method_27958().execute(() -> {
                int i = nativeimage.method_4307();
                int j = nativeimage.method_4323();
                int k = 0;
                int l = 0;
                if (i > j) {
                    k = (i - j) / 2;
                    i = j;
                } else {
                    l = (j - i) / 2;
                    j = i;
                }

                try {
                    NativeImage nativeimage2 = new NativeImage(64, 64, false);

                    try {
                        nativeimage.method_4300(k, l, i, j, nativeimage2);
                        nativeimage2.method_4314(path);
                    } catch (Throwable throwable1) {
                        try {
                            nativeimage2.close();
                        } catch (Throwable throwable) {
                            throwable1.addSuppressed(throwable);
                        }

                        throw throwable1;
                    }

                    nativeimage2.close();
                    return;
                } catch (IOException ioexception) {
                    LOGGER.warn("Couldn't save auto screenshot", ioexception);
                } finally {
                    nativeimage.close();
                }
            });
        }
    }

    private boolean shouldRenderBlockOutline() {
        BlockOutlineModule blockoutlinemodule = ClientMain.getInstance().getModuleManager().getModule(BlockOutlineModule.class);
        if (this.blockOutlineEnabled && (blockoutlinemodule == null || !blockoutlinemodule.isEnabled())) {
            Entity entity = this.client.method_1560();
            boolean flag = entity instanceof PlayerEntity && !this.client.field_1690.field_1842;
            if (flag && !((PlayerEntity)entity).method_31549().field_7476) {
                ItemStack itemstack = ((LivingEntity)entity).method_6047();
                HitResult hitresult = this.client.field_1765;
                if (hitresult != null && hitresult.method_17783() == Type.field_1332) {
                    BlockPos blockpos = ((BlockHitResult)hitresult).method_17777();
                    BlockState blockstate = this.client.field_1687.method_8320(blockpos);
                    if (this.client.field_1761.method_2920() == GameMode.field_9219) {
                        flag = blockstate.method_26196(this.client.field_1687, blockpos) != null;
                    } else {
                        CachedBlockPosition cachedblockposition = new CachedBlockPosition(this.client.field_1687, blockpos, false);
                        Registry registry = this.client.field_1687.method_30349().method_30530(RegistryKeys.field_41254);
                        flag = !itemstack.method_7960() && (itemstack.method_57373(cachedblockposition) || itemstack.method_57357(cachedblockposition));
                    }
                }
            }

            return flag;
        } else {
            return false;
        }
    }

    public void method_3188(RenderTickCounter rendertickcounter) {
        float f = rendertickcounter.method_60637(true);
        this.lightmapTextureManager.method_3313(f);
        if (this.client.method_1560() == null) {
            this.client.method_1504(this.client.field_1724);
        }

        this.method_3190(f);
        Profiler profiler = Profilers.method_64146();
        profiler.method_15396("center");
        boolean flag = this.shouldRenderBlockOutline();
        profiler.method_15405("camera");
        Camera camera = this.camera;
        Object object = this.client.method_1560() == null ? this.client.field_1724 : this.client.method_1560();
        float f1 = this.client.field_1687.method_54719().method_54746((Entity)object) ? 1.0F : f;
        camera.method_19321(
            this.client.field_1687,
            (Entity)object,
            !this.client.field_1690.method_31044().method_31034(),
            this.client.field_1690.method_31044().method_31035(),
            f1
        );
        this.viewDistance = this.client.field_1690.method_38521() * 16;
        float f2 = this.getFov(camera, f, true);
        Matrix4f matrix4f = this.method_22973(f2);
        MatrixStack matrixstack = new MatrixStack();
        this.tiltViewWhenHurt(matrixstack, camera.method_55437());
        if ((Boolean)this.client.field_1690.method_42448().method_41753()) {
            this.bobView(matrixstack, camera.method_55437());
        }

        matrix4f.mul(matrixstack.method_23760().method_23761());
        float f3 = ((Double)this.client.field_1690.method_42453().method_41753()).floatValue();
        float f4 = MathHelper.method_16439(f, this.client.field_1724.field_44912, this.client.field_1724.field_44911) * (f3 * f3);
        if (f4 > 0.0F) {
            int i = this.client.field_1724.method_6059(StatusEffects.field_5916) ? 7 : 20;
            float f5 = 5.0F / (f4 * f4 + 5.0F) - f4 * 0.04F;
            f5 *= f5;
            Vector3f vector3f = new Vector3f(0.0F, MathHelper.field_15724 / 2.0F, MathHelper.field_15724 / 2.0F);
            float f6 = (this.ticks + f) * i * (float) (Math.PI / 180.0);
            matrix4f.rotate(f6, vector3f);
            matrix4f.scale(1.0F / f5, 1.0F, 1.0F);
            matrix4f.rotate(-f6, vector3f);
        }

        float f7 = Math.max(f2, ((Integer)this.client.field_1690.method_41808().method_41753()).intValue());
        Matrix4f matrix4f1 = this.method_22973(f7);
        RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.field_54953);
        Quaternionf quaternionf = camera.method_23767().conjugate(new Quaternionf());
        Matrix4f matrix4f2 = new Matrix4f().rotation(quaternionf);
        this.client.field_1769.method_32133(camera.method_19326(), matrix4f2, matrix4f1);
        this.client.method_1522().method_1235(true);
        this.client.field_1769.method_22710(this.pool, rendertickcounter, flag, camera, this, matrix4f2, matrix4f);
        profiler.method_15405("hand");
        if (this.renderHand) {
            HandTweaksModule handtweaksmodule = ClientMain.getInstance().getModuleManager().getModule(HandTweaksModule.class);
            boolean flag1 = handtweaksmodule != null && handtweaksmodule.isEnabled() && handtweaksmodule.c();
            if (flag1) {
                handtweaksmodule.e();
            }

            RenderSystem.clear(256);
            this.renderHand(camera, f, matrix4f2);
            if (flag1) {
                handtweaksmodule.f();
            }
        }

        profiler.method_15407();
    }

    public void method_3203() {
        this.floatingItem = null;
        this.client.method_61963().method_62621();
        this.camera.method_19337();
        this.hasWorldIcon = false;
    }

    public void method_3189(ItemStack itemstack) {
        this.floatingItem = itemstack;
        this.floatingItemTimeLeft = 40;
        this.floatingItemWidth = this.random.method_43057() * 2.0F - 1.0F;
        this.floatingItemHeight = this.random.method_43057() * 2.0F - 1.0F;
    }

    private void renderFloatingItem(DrawContext drawcontext, float f) {
        if (this.floatingItem != null && this.floatingItemTimeLeft > 0) {
            int i = 40 - this.floatingItemTimeLeft;
            float f1 = (i + f) / 40.0F;
            float f2 = f1 * f1;
            float f3 = f1 * f2;
            float f4 = 10.25F * f3 * f2 - 24.95F * f2 * f2 + 25.5F * f3 - 13.8F * f2 + 4.0F * f1;
            float f5 = f4 * (float) Math.PI;
            float f6 = this.floatingItemWidth * (drawcontext.method_51421() / 4);
            float f7 = this.floatingItemHeight * (drawcontext.method_51443() / 4);
            MatrixStack matrixstack = drawcontext.method_51448();
            matrixstack.method_22903();
            matrixstack.method_46416(
                drawcontext.method_51421() / 2 + f6 * MathHelper.method_15379(MathHelper.method_15374(f5 * 2.0F)),
                drawcontext.method_51443() / 2 + f7 * MathHelper.method_15379(MathHelper.method_15374(f5 * 2.0F)),
                -50.0F
            );
            float f8 = 50.0F + 175.0F * MathHelper.method_15374(f5);
            matrixstack.method_22905(f8, -f8, f8);
            matrixstack.method_22907(RotationAxis.field_40716.rotationDegrees(900.0F * MathHelper.method_15379(MathHelper.method_15374(f5))));
            matrixstack.method_22907(RotationAxis.field_40714.rotationDegrees(6.0F * MathHelper.method_15362(f1 * 8.0F)));
            matrixstack.method_22907(RotationAxis.field_40718.rotationDegrees(6.0F * MathHelper.method_15362(f1 * 8.0F)));
            drawcontext.method_64039(
                vertexconsumerprovider -> this.client
                    .method_1480()
                    .method_23178(
                        this.floatingItem,
                        ModelTransformationMode.field_4319,
                        15728880,
                        OverlayTexture.field_21444,
                        matrixstack,
                        vertexconsumerprovider,
                        this.client.field_1687,
                        0
                    )
            );
            matrixstack.method_22909();
        }
    }

    public MinecraftClient method_35772() {
        return this.client;
    }

    public float method_3195(float f) {
        return MathHelper.method_16439(f, this.lastSkyDarkness, this.skyDarkness);
    }

    public float method_3193() {
        return this.viewDistance;
    }

    public Camera method_19418() {
        return this.camera;
    }

    public LightmapTextureManager method_22974() {
        return this.lightmapTextureManager;
    }

    public OverlayTexture method_22975() {
        return this.overlayTexture;
    }

    public static CustomGameRenderer getInstance() {
        return instance;
    }
}
