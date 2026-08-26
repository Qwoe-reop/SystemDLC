package dev.mark.system.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.mark.system.core.ClientMain;
import dev.mark.system.module.render.CustomFogModule;
import dev.mark.system.module.render.CustomTimeModule;
import dev.mark.system.module.visual.NameTagsModule;
import dev.mark.system.module.visual.PlayerOutlinesModule;
import dev.mark.system.util.UnsafeFieldAccessor;
import java.lang.reflect.Method;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterEntities;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterSetup;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterTranslucent;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeEntities;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.End;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.Start;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPass;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;

public class CustomWorldRenderer extends WorldRenderer {
    private static final Identifier ENTITY_OUTLINE = Identifier.method_60656("entity_outline");
    private static final Identifier CUSTOM_ENTITY_OUTLINE = Identifier.method_60656("custom_entity_outline");
    private static final Identifier TRANSPARENCY = Identifier.method_60656("transparency");
    private final MinecraftClient client;
    private UnsafeFieldAccessor<Frustum> frustumField;
    private UnsafeFieldAccessor<Frustum> capturedFrustumField;
    private UnsafeFieldAccessor<Boolean> shouldCaptureFrustumField;
    private UnsafeFieldAccessor<DefaultFramebufferSet> framebufferSetField;
    private UnsafeFieldAccessor<Framebuffer> entityOutlineFramebufferField;
    private UnsafeFieldAccessor<List<Entity>> renderedEntitiesField;
    private UnsafeFieldAccessor<Integer> renderedEntitiesCountField;
    private UnsafeFieldAccessor<Integer> ticksField;
    private UnsafeFieldAccessor<EntityRenderDispatcher> entityRenderDispatcherField;
    private UnsafeFieldAccessor<BlockEntityRenderDispatcher> blockEntityRenderDispatcherField;
    private Method getEntitiesToRenderMethod;
    private Method setupTerrainMethod;
    private Method updateChunksMethod;
    private Method getTransparencyPostEffectProcessorMethod;
    private Method renderSkyMethod;
    private Method renderMainMethod;
    private Method renderParticlesMethod;
    private Method renderCloudsMethod;
    private Method renderWeatherMethod;
    private Method renderLateDebugMethod;
    private Method renderLayerMethod;
    private Method renderEntitiesMethod;
    private Method renderBlockEntitiesMethod;
    private Method renderBlockDamageMethod;
    private Method renderTargetBlockOutlineMethod;
    private Method checkEmptyMethod;
    private Method canDrawEntityOutlinesMethod;
    private UnsafeFieldAccessor<BufferBuilderStorage> bufferBuildersField;
    private UnsafeFieldAccessor<Object> weatherRenderingField;
    private boolean initialized = false;
    private final WorldRenderContextImpl fabricContext = new WorldRenderContextImpl();
    String getEntitiesName = ClientMain.getInstance().isDev() ? "getEntitiesToRender" : "method_62211";
    String setupTerrainName = ClientMain.getInstance().isDev() ? "setupTerrain" : "method_3273";
    String updateChunksName = ClientMain.getInstance().isDev() ? "updateChunks" : "method_3269";
    String getTransparencyName = ClientMain.getInstance().isDev() ? "getTransparencyPostEffectProcessor" : "method_62907";
    String renderSkyName = ClientMain.getInstance().isDev() ? "renderSky" : "method_62200";
    String renderMainName = ClientMain.getInstance().isDev() ? "renderMain" : "method_62202";
    String renderParticlesName = ClientMain.getInstance().isDev() ? "renderParticles" : "method_62201";
    String renderCloudsName = ClientMain.getInstance().isDev() ? "renderClouds" : "method_62204";
    String renderWeatherName = ClientMain.getInstance().isDev() ? "renderWeather" : "method_62203";
    String renderLateDebugName = ClientMain.getInstance().isDev() ? "renderLateDebug" : "method_62199";
    String renderLayerName = ClientMain.getInstance().isDev() ? "renderLayer" : "method_3251";
    String renderEntitiesName = ClientMain.getInstance().isDev() ? "renderEntities" : "method_62207";
    String renderBlockEntitiesName = ClientMain.getInstance().isDev() ? "renderBlockEntities" : "method_62208";
    String renderBlockDamageName = ClientMain.getInstance().isDev() ? "renderBlockDamage" : "method_62206";
    String renderTargetBlockOutlineName = ClientMain.getInstance().isDev() ? "renderTargetBlockOutline" : "method_62210";
    String checkEmptyName = ClientMain.getInstance().isDev() ? "checkEmpty" : "method_22979";
    String canDrawEntityOutlinesName = ClientMain.getInstance().isDev() ? "canDrawEntityOutlines" : "method_3270";

    public CustomWorldRenderer(
        MinecraftClient minecraftclient,
        EntityRenderDispatcher entityrenderdispatcher,
        BlockEntityRenderDispatcher blockentityrenderdispatcher,
        BufferBuilderStorage bufferbuilderstorage
    ) {
        super(minecraftclient, entityrenderdispatcher, blockentityrenderdispatcher, bufferbuilderstorage);
        this.client = minecraftclient;
    }

    private void initReflection() {
        if (!this.initialized) {
            try {
                Class<WorldRenderer> oclass = WorldRenderer.class;
                this.entityRenderDispatcherField = new UnsafeFieldAccessor<>(this, oclass, 8);
                this.blockEntityRenderDispatcherField = new UnsafeFieldAccessor<>(this, oclass, 9);
                this.ticksField = new UnsafeFieldAccessor<>(this, oclass, 21);
                this.entityOutlineFramebufferField = new UnsafeFieldAccessor<>(this, oclass, 24);
                this.framebufferSetField = new UnsafeFieldAccessor<>(this, oclass, 25);
                this.renderedEntitiesField = new UnsafeFieldAccessor<>(this, oclass, 36);
                this.renderedEntitiesCountField = new UnsafeFieldAccessor<>(this, oclass, 37);
                this.frustumField = new UnsafeFieldAccessor<>(this, oclass, 38);
                this.shouldCaptureFrustumField = new UnsafeFieldAccessor<>(this, oclass, 39);
                this.capturedFrustumField = new UnsafeFieldAccessor<>(this, oclass, 40);

                try {
                    this.getEntitiesToRenderMethod = oclass.getDeclaredMethod(this.getEntitiesName, Camera.class, Frustum.class, List.class);
                } catch (NoSuchMethodException e) {
                    String[] alternativeNames = new String[]{"method_3258", "method_22973", "getEntitiesToRender"};

                    for (String altName : alternativeNames) {
                        try {
                            this.getEntitiesToRenderMethod = oclass.getDeclaredMethod(altName, Camera.class, Frustum.class, List.class);
                            break;
                        } catch (NoSuchMethodException var9) {
                        }
                    }

                    if (this.getEntitiesToRenderMethod == null) {
                        throw e;
                    }
                }

                this.getEntitiesToRenderMethod.setAccessible(true);
                this.setupTerrainMethod = oclass.getDeclaredMethod(this.setupTerrainName, Camera.class, Frustum.class, boolean.class, boolean.class);
                this.setupTerrainMethod.setAccessible(true);
                this.updateChunksMethod = oclass.getDeclaredMethod(this.updateChunksName, Camera.class);
                this.updateChunksMethod.setAccessible(true);
                this.getTransparencyPostEffectProcessorMethod = oclass.getDeclaredMethod(this.getTransparencyName);
                this.getTransparencyPostEffectProcessorMethod.setAccessible(true);
                this.renderSkyMethod = oclass.getDeclaredMethod(this.renderSkyName, FrameGraphBuilder.class, Camera.class, float.class, Fog.class);
                this.renderSkyMethod.setAccessible(true);
                this.renderMainMethod = oclass.getDeclaredMethod(
                    this.renderMainName,
                    FrameGraphBuilder.class,
                    Frustum.class,
                    Camera.class,
                    Matrix4f.class,
                    Matrix4f.class,
                    Fog.class,
                    boolean.class,
                    boolean.class,
                    RenderTickCounter.class,
                    Profiler.class
                );
                this.renderMainMethod.setAccessible(true);
                this.renderParticlesMethod = oclass.getDeclaredMethod(this.renderParticlesName, FrameGraphBuilder.class, Camera.class, float.class, Fog.class);
                this.renderParticlesMethod.setAccessible(true);
                this.renderCloudsMethod = oclass.getDeclaredMethod(
                    this.renderCloudsName,
                    FrameGraphBuilder.class,
                    Matrix4f.class,
                    Matrix4f.class,
                    CloudRenderMode.class,
                    Vec3d.class,
                    float.class,
                    int.class,
                    float.class
                );
                this.renderCloudsMethod.setAccessible(true);
                this.renderWeatherMethod = oclass.getDeclaredMethod(this.renderWeatherName, FrameGraphBuilder.class, Vec3d.class, float.class, Fog.class);
                this.renderWeatherMethod.setAccessible(true);
                this.renderLateDebugMethod = oclass.getDeclaredMethod(this.renderLateDebugName, FrameGraphBuilder.class, Vec3d.class, Fog.class);
                this.renderLateDebugMethod.setAccessible(true);
                this.renderLayerMethod = oclass.getDeclaredMethod(
                    this.renderLayerName, RenderLayer.class, double.class, double.class, double.class, Matrix4f.class, Matrix4f.class
                );
                this.renderLayerMethod.setAccessible(true);
                this.renderEntitiesMethod = oclass.getDeclaredMethod(
                    this.renderEntitiesName, MatrixStack.class, Immediate.class, Camera.class, RenderTickCounter.class, List.class
                );
                this.renderEntitiesMethod.setAccessible(true);
                this.renderBlockEntitiesMethod = oclass.getDeclaredMethod(
                    this.renderBlockEntitiesName, MatrixStack.class, Immediate.class, Immediate.class, Camera.class, float.class
                );
                this.renderBlockEntitiesMethod.setAccessible(true);
                this.renderBlockDamageMethod = oclass.getDeclaredMethod(this.renderBlockDamageName, MatrixStack.class, Camera.class, Immediate.class);
                this.renderBlockDamageMethod.setAccessible(true);
                this.renderTargetBlockOutlineMethod = oclass.getDeclaredMethod(
                    this.renderTargetBlockOutlineName, Camera.class, Immediate.class, MatrixStack.class, boolean.class
                );
                this.renderTargetBlockOutlineMethod.setAccessible(true);
                this.checkEmptyMethod = oclass.getDeclaredMethod(this.checkEmptyName, MatrixStack.class);
                this.checkEmptyMethod.setAccessible(true);
                this.canDrawEntityOutlinesMethod = oclass.getDeclaredMethod(this.canDrawEntityOutlinesName);
                this.canDrawEntityOutlinesMethod.setAccessible(true);
                this.bufferBuildersField = new UnsafeFieldAccessor<>(this, oclass, BufferBuilderStorage.class);
                this.weatherRenderingField = new UnsafeFieldAccessor<>(this, oclass, 14);
                this.initialized = true;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void method_22710(
        ObjectAllocator objectallocator,
        RenderTickCounter rendertickcounter,
        boolean flag,
        Camera camera,
        GameRenderer gamerenderer,
        Matrix4f matrix4f,
        Matrix4f matrix4f1
    ) {
        this.applyCustomTimeIfEnabled();
        CustomFogModule customfogmodule = this.getCustomFogModule();
        PlayerOutlinesModule playeroutlinesmodule = PlayerOutlinesModule.h();
        NameTagsModule nametagsmodule = null;

        try {
            nametagsmodule = ClientMain.getInstance().getModuleManager().getModule(NameTagsModule.class);
        } catch (Exception var14) {
        }

        boolean flag1 = customfogmodule != null && customfogmodule.e()
            || playeroutlinesmodule != null && playeroutlinesmodule.isEnabled()
            || nametagsmodule != null && nametagsmodule.isEnabled();
        if (!flag1) {
            super.method_22710(objectallocator, rendertickcounter, flag, camera, gamerenderer, matrix4f, matrix4f1);
        } else {
            this.initReflection();
            if (!this.initialized) {
                super.method_22710(objectallocator, rendertickcounter, flag, camera, gamerenderer, matrix4f, matrix4f1);
            } else {
                try {
                    this.render(objectallocator, rendertickcounter, flag, camera, gamerenderer, matrix4f, matrix4f1, customfogmodule);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    super.method_22710(objectallocator, rendertickcounter, flag, camera, gamerenderer, matrix4f, matrix4f1);
                }
            }
        }
    }

    private void render(
        ObjectAllocator objectallocator,
        RenderTickCounter rendertickcounter,
        boolean flag,
        Camera camera,
        GameRenderer gamerenderer,
        Matrix4f matrix4f,
        Matrix4f matrix4f1,
        CustomFogModule customfogmodule
    ) throws Exception {
        Frustum frustum = this.frustumField.getValue();
        DefaultFramebufferSet defaultframebufferset = this.framebufferSetField.getValue();
        List list = this.renderedEntitiesField.getValue();
        BufferBuilderStorage bufferbuilderstorage = this.bufferBuildersField.getValue();
        if (frustum != null && defaultframebufferset != null && list != null && bufferbuilderstorage != null) {
            float f = rendertickcounter.method_60637(false);
            RenderSystem.setShaderGameTime(this.client.field_1687.method_8510(), f);
            this.blockEntityRenderDispatcherField.getValue().method_3549(this.client.field_1687, camera, this.client.field_1765);
            this.entityRenderDispatcherField.getValue().method_3941(this.client.field_1687, camera, this.client.field_1692);
            Profiler profiler = Profilers.method_64146();
            profiler.method_15405("light_update_queue");
            this.client.field_1687.method_38534();
            profiler.method_15405("light_updates");
            this.client.field_1687.method_2935().method_12130().method_15516();
            Vec3d vec3d = camera.method_19326();
            double d0 = vec3d.method_10216();
            double d1 = vec3d.method_10214();
            double d2 = vec3d.method_10215();
            profiler.method_15405("culling");
            boolean flag1 = this.capturedFrustumField.getValue() != null;
            Frustum frustum1 = flag1 ? this.capturedFrustumField.getValue() : frustum;
            Profilers.method_64146().method_15405("captureFrustum");
            if (this.shouldCaptureFrustumField.getBoolean(this)) {
                if (flag1) {
                    this.capturedFrustumField.setValue(new Frustum(matrix4f, matrix4f1));
                } else {
                    this.capturedFrustumField.setValue(frustum1);
                }

                this.capturedFrustumField.getValue().method_23088(d0, d1, d2);
                this.shouldCaptureFrustumField.setBoolean(this, false);
            }

            this.frustumField.setValue(frustum1);
            profiler.method_15405("fog");
            float f1 = gamerenderer.method_3193();

            boolean flag2;
            Vector4f vector4f;
            try {
                flag2 = this.client.field_1687.method_28103().method_28110(MathHelper.method_15357(d0), MathHelper.method_15357(d1))
                    || this.client.field_1705.method_1740().method_1800();
                vector4f = BackgroundRenderer.method_62185(
                    camera, f, this.client.field_1687, this.client.field_1690.method_38521(), gamerenderer.method_3195(f)
                );
            } catch (Exception exception2) {
                flag2 = false;
                vector4f = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
            }

            Fog fog = customfogmodule != null ? customfogmodule.d() : null;
            Fog fog1 = customfogmodule != null ? customfogmodule.d() : null;
            if (fog == null) {
                fog = BackgroundRenderer.method_3211(camera, FogType.field_20946, vector4f, f1, flag2, f);
            }

            if (fog1 == null) {
                fog1 = BackgroundRenderer.method_3211(camera, FogType.field_20945, vector4f, f1, flag2, f);
            }

            profiler.method_15405("cullEntities");
            boolean flag3 = (Boolean)this.getEntitiesToRenderMethod.invoke(this, camera, frustum1, list);
            this.renderedEntitiesCountField.setInt(this, list.size());
            PlayerOutlinesModule playeroutlinesmodule = PlayerOutlinesModule.h();
            boolean flag4 = playeroutlinesmodule != null && playeroutlinesmodule.isEnabled();
            if (flag4) {
                flag3 = true;
            } else {
                flag3 = false;
            }

            profiler.method_15405("terrain_setup");
            this.setupTerrainMethod.invoke(this, camera, frustum1, flag1, this.client.field_1724.method_7325());
            profiler.method_15405("compile_sections");
            this.updateChunksMethod.invoke(this, camera);
            boolean flag5 = MinecraftClient.method_29611();
            this.fabricContext.prepare(this, rendertickcounter, flag, camera, gamerenderer, matrix4f, matrix4f1, this.client.field_1687, flag5, frustum1);

            try {
                ((Start)WorldRenderEvents.START.invoker()).onStart(this.fabricContext);
            } catch (Exception var50) {
            }

            Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
            matrix4fstack.pushMatrix();
            matrix4fstack.mul(matrix4f);
            FrameGraphBuilder framegraphbuilder = new FrameGraphBuilder();
            defaultframebufferset.field_53091 = framegraphbuilder.method_61914("main", this.client.method_1522());
            int i = this.client.method_1522().field_1482;
            int j = this.client.method_1522().field_1481;
            SimpleFramebufferFactory simpleframebufferfactory = new SimpleFramebufferFactory(i, j, true);
            PostEffectProcessor posteffectprocessor = (PostEffectProcessor)this.getTransparencyPostEffectProcessorMethod.invoke(this);
            if (posteffectprocessor != null) {
                defaultframebufferset.field_53092 = framegraphbuilder.method_61912("translucent", simpleframebufferfactory);
                defaultframebufferset.field_53093 = framegraphbuilder.method_61912("item_entity", simpleframebufferfactory);
                defaultframebufferset.field_53094 = framegraphbuilder.method_61912("particles", simpleframebufferfactory);
                defaultframebufferset.field_53095 = framegraphbuilder.method_61912("weather", simpleframebufferfactory);
                defaultframebufferset.field_53096 = framegraphbuilder.method_61912("clouds", simpleframebufferfactory);
            }

            Framebuffer framebuffer = this.entityOutlineFramebufferField.getValue();
            if (framebuffer != null && framebuffer.field_1476 != -1) {
                defaultframebufferset.field_53097 = framegraphbuilder.method_61914("entity_outline", framebuffer);
            }

            RenderPass renderpass = framegraphbuilder.method_61911("clear");
            defaultframebufferset.field_53091 = renderpass.method_61933(defaultframebufferset.field_53091);
            Fog fog2 = fog;
            renderpass.method_61929(() -> {
                RenderSystem.clearColor(fog2.comp_3012(), fog2.comp_3013(), fog2.comp_3014(), 0.0F);
                RenderSystem.clear(16640);
            });
            if (!flag2) {
                this.renderSkyMethod.invoke(this, framegraphbuilder, camera, f, fog1);
            }

            this.renderMainWithFabricEvents(
                framegraphbuilder,
                frustum1,
                camera,
                matrix4f,
                matrix4f1,
                fog,
                flag,
                flag3,
                rendertickcounter,
                profiler,
                defaultframebufferset,
                bufferbuilderstorage,
                list,
                d0,
                d1,
                d2,
                f
            );
            PlayerOutlinesModule playeroutlinesmodule1 = PlayerOutlinesModule.h();
            boolean flag6 = playeroutlinesmodule1 != null && playeroutlinesmodule1.isEnabled();
            if (flag6 && flag3 && defaultframebufferset.field_53097 != null) {
                PostEffectProcessor posteffectprocessor1 = this.client.method_62887().method_62941(CUSTOM_ENTITY_OUTLINE, DefaultFramebufferSet.field_53903);
                if (posteffectprocessor1 != null) {
                    float[] afloat = playeroutlinesmodule1.i();
                    float[] afloat1 = playeroutlinesmodule1.j();
                    posteffectprocessor1.method_57799("BlurWeight0", afloat[0]);
                    posteffectprocessor1.method_57799("BlurWeight1", afloat[1]);
                    posteffectprocessor1.method_57799("BlurWeight2", afloat[2]);
                    posteffectprocessor1.method_57799("BlurWeight3", afloat[3]);
                    posteffectprocessor1.method_57799("BlurRadius1", afloat1[0]);
                    posteffectprocessor1.method_57799("BlurRadius2", afloat1[1]);
                    posteffectprocessor1.method_57799("BlurRadius3", afloat1[2]);
                    posteffectprocessor1.method_57799("Brightness", playeroutlinesmodule1.k());
                    posteffectprocessor1.method_57799("GlowIntensity", playeroutlinesmodule1.l());
                    posteffectprocessor1.method_57799("OriginalIntensity", playeroutlinesmodule1.m());
                    posteffectprocessor1.method_62234(framegraphbuilder, i, j, defaultframebufferset);
                }
            }

            this.renderParticlesMethod.invoke(this, framegraphbuilder, camera, f, fog);
            CloudRenderMode cloudrendermode = this.client.field_1690.method_1632();
            if (cloudrendermode != CloudRenderMode.field_18162) {
                float f3 = this.client.field_1687.method_28103().method_28108();
                if (!Float.isNaN(f3)) {
                    int l = this.ticksField.getInt(this);
                    float f2 = l + f;
                    int k = this.client.field_1687.method_23785(f);
                    this.renderCloudsMethod.invoke(this, framegraphbuilder, matrix4f, matrix4f1, cloudrendermode, camera.method_19326(), f2, k, f3 + 0.33F);
                }
            }

            this.renderWeatherWithoutBorder(framegraphbuilder, camera.method_19326(), f, fog);
            if (posteffectprocessor != null) {
                posteffectprocessor.method_62234(framegraphbuilder, i, j, defaultframebufferset);
            }

            RenderPass renderpass1 = framegraphbuilder.method_61911("after_clouds");
            defaultframebufferset.field_53091 = renderpass1.method_61933(defaultframebufferset.field_53091);
            renderpass1.method_61929(() -> {
                try {
                    MatrixStack matrixstack = new MatrixStack();
                    Immediate immediate = bufferbuilderstorage.method_23000();
                    this.fabricContext.setMatrixStack(matrixstack);
                    this.fabricContext.setConsumers(immediate);
                    ClientMain.getInstance().getEventManager().t(this.fabricContext);
                } catch (Exception var4) {
                }
            });
            this.renderLateDebugMethod.invoke(this, framegraphbuilder, vec3d, fog);
            profiler.method_15405("framegraph");
            framegraphbuilder.method_61910(objectallocator, new WorldRendererProfilerAdapter(this, profiler));
            this.client.method_1522().method_1235(false);
            list.clear();
            defaultframebufferset.method_62223();
            matrix4fstack.popMatrix();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.setShaderFog(Fog.field_53065);

            try {
                ((End)WorldRenderEvents.END.invoker()).onEnd(this.fabricContext);
            } catch (Exception var49) {
            }
        } else {
            super.method_22710(objectallocator, rendertickcounter, flag, camera, gamerenderer, matrix4f, matrix4f1);
        }
    }

    private void renderMainWithFabricEvents(
        FrameGraphBuilder framegraphbuilder,
        Frustum frustum,
        Camera camera,
        Matrix4f matrix4f,
        Matrix4f matrix4f1,
        Fog fog,
        boolean flag,
        boolean flag1,
        RenderTickCounter rendertickcounter,
        Profiler profiler,
        DefaultFramebufferSet defaultframebufferset,
        BufferBuilderStorage bufferbuilderstorage,
        List<Entity> list,
        double d0,
        double d1,
        double d2,
        float f
    ) {
        RenderPass renderpass = framegraphbuilder.method_61911("main");
        defaultframebufferset.field_53091 = renderpass.method_61933(defaultframebufferset.field_53091);
        if (defaultframebufferset.field_53092 != null) {
            defaultframebufferset.field_53092 = renderpass.method_61933(defaultframebufferset.field_53092);
        }

        if (defaultframebufferset.field_53093 != null) {
            defaultframebufferset.field_53093 = renderpass.method_61933(defaultframebufferset.field_53093);
        }

        if (defaultframebufferset.field_53095 != null) {
            defaultframebufferset.field_53095 = renderpass.method_61933(defaultframebufferset.field_53095);
        }

        if (flag1 && defaultframebufferset.field_53097 != null) {
            defaultframebufferset.field_53097 = renderpass.method_61933(defaultframebufferset.field_53097);
        }

        Handle handle = defaultframebufferset.field_53091;
        Handle handle1 = defaultframebufferset.field_53092;
        Handle handle2 = defaultframebufferset.field_53093;
        Handle handle3 = defaultframebufferset.field_53095;
        Handle handle4 = defaultframebufferset.field_53097;
        WorldRenderContextImpl WorldRenderContextImpl = this.fabricContext;
        renderpass.method_61929(() -> {
            try {
                RenderSystem.setShaderFog(fog);
                float f_ = rendertickcounter.method_60637(false);
                Vec3d vec3d = camera.method_19326();
                double d0_ = vec3d.method_10216();
                double d1_ = vec3d.method_10214();
                double d2_ = vec3d.method_10215();

                try {
                    ((AfterSetup)WorldRenderEvents.AFTER_SETUP.invoker()).afterSetup(WorldRenderContextImpl);
                } catch (Exception var40) {
                }

                profiler.method_15396("terrain");
                this.renderLayerMethod.invoke(this, RenderLayer.method_23577(), d0, d1, d2, matrix4f, matrix4f1);
                this.renderLayerMethod.invoke(this, RenderLayer.method_23579(), d0, d1, d2, matrix4f, matrix4f1);
                this.renderLayerMethod.invoke(this, RenderLayer.method_23581(), d0, d1, d2, matrix4f, matrix4f1);
                if (this.client.field_1687.method_28103().method_29993()) {
                    DiffuseLighting.method_1452();
                } else {
                    DiffuseLighting.method_27869();
                }

                if (handle2 != null) {
                    ((Framebuffer)handle2.get()).method_1236(0.0F, 0.0F, 0.0F, 0.0F);
                    ((Framebuffer)handle2.get()).method_1230();
                    ((Framebuffer)handle2.get()).method_29329(this.client.method_1522());
                    ((Framebuffer)handle.get()).method_1235(false);
                }

                if (handle3 != null) {
                    ((Framebuffer)handle3.get()).method_1236(0.0F, 0.0F, 0.0F, 0.0F);
                    ((Framebuffer)handle3.get()).method_1230();
                }

                boolean flag3 = (Boolean)this.canDrawEntityOutlinesMethod.invoke(this);
                if (flag3 && handle4 != null) {
                    ((Framebuffer)handle4.get()).method_1236(0.0F, 0.0F, 0.0F, 0.0F);
                    ((Framebuffer)handle4.get()).method_1230();
                    ((Framebuffer)handle.get()).method_1235(false);
                }

                MatrixStack matrixstack = new MatrixStack();
                Immediate immediate = bufferbuilderstorage.method_23000();
                Immediate immediate1 = bufferbuilderstorage.method_23001();

                try {
                    WorldRenderContextImpl.setMatrixStack(matrixstack);
                    WorldRenderContextImpl.setConsumers(immediate);
                    ((BeforeEntities)WorldRenderEvents.BEFORE_ENTITIES.invoker()).beforeEntities(WorldRenderContextImpl);
                } catch (Exception var39) {
                }

                profiler.method_15405("entities");
                this.renderEntitiesMethod.invoke(this, matrixstack, immediate, camera, rendertickcounter, list);
                immediate.method_37104();
                this.checkEmptyMethod.invoke(this, matrixstack);

                try {
                    WorldRenderContextImpl.setMatrixStack(matrixstack);
                    WorldRenderContextImpl.setConsumers(immediate);
                    ((AfterEntities)WorldRenderEvents.AFTER_ENTITIES.invoker()).afterEntities(WorldRenderContextImpl);
                } catch (Exception var38) {
                }

                profiler.method_15405("blockentities");
                this.renderBlockEntitiesMethod.invoke(this, matrixstack, immediate, immediate1, camera, f);
                immediate.method_37104();
                this.checkEmptyMethod.invoke(this, matrixstack);
                immediate.method_22994(RenderLayer.method_23577());
                immediate.method_22994(RenderLayer.method_23574());
                immediate.method_22994(RenderLayer.method_34571());
                immediate.method_22994(TexturedRenderLayers.method_24073());
                immediate.method_22994(TexturedRenderLayers.method_24074());
                immediate.method_22994(TexturedRenderLayers.method_24069());
                immediate.method_22994(TexturedRenderLayers.method_24070());
                immediate.method_22994(TexturedRenderLayers.method_24071());
                immediate.method_22994(TexturedRenderLayers.method_45783());
                immediate.method_22994(TexturedRenderLayers.method_24072());
                bufferbuilderstorage.method_23003().method_23285();
                if (flag) {
                    this.renderTargetBlockOutlineMethod.invoke(this, camera, immediate, matrixstack, false);
                }

                profiler.method_15405("debug");
                this.client.field_1709.method_23099(matrixstack, frustum, immediate, d0, d1, d2);
                immediate.method_37104();
                this.checkEmptyMethod.invoke(this, matrixstack);
                immediate.method_22994(TexturedRenderLayers.method_29382());
                immediate.method_22994(TexturedRenderLayers.method_24059());
                immediate.method_22994(TexturedRenderLayers.method_24067());
                immediate.method_22994(RenderLayer.method_27949());
                immediate.method_22994(RenderLayer.method_23590());
                immediate.method_22994(RenderLayer.method_30676());
                immediate.method_22994(RenderLayer.method_23591());
                profiler.method_15405("destroyProgress");
                this.renderBlockDamageMethod.invoke(this, matrixstack, camera, immediate1);
                immediate1.method_22993();
                this.checkEmptyMethod.invoke(this, matrixstack);
                immediate.method_22994(RenderLayer.method_23589());
                immediate.method_22993();
                if (handle1 != null) {
                    ((Framebuffer)handle1.get()).method_1236(0.0F, 0.0F, 0.0F, 0.0F);
                    ((Framebuffer)handle1.get()).method_1230();
                    ((Framebuffer)handle1.get()).method_29329((Framebuffer)handle.get());
                }

                profiler.method_15405("translucent");
                this.renderLayerMethod.invoke(this, RenderLayer.method_23583(), d0, d1, d2, matrix4f, matrix4f1);

                try {
                    WorldRenderContextImpl.setMatrixStack(matrixstack);
                    WorldRenderContextImpl.setConsumers(immediate);
                    ((AfterTranslucent)WorldRenderEvents.AFTER_TRANSLUCENT.invoker()).afterTranslucent(WorldRenderContextImpl);
                } catch (Exception var37) {
                }

                profiler.method_15405("string");
                this.renderLayerMethod.invoke(this, RenderLayer.method_29997(), d0, d1, d2, matrix4f, matrix4f1);
                if (flag) {
                    this.renderTargetBlockOutlineMethod.invoke(this, camera, immediate, matrixstack, true);
                }

                immediate.method_22993();
                profiler.method_15407();
            } catch (Exception exception4) {
                exception4.printStackTrace();
            }
        });
    }

    private CustomFogModule getCustomFogModule() {
        try {
            return ClientMain.getInstance().getModuleManager().getModule(CustomFogModule.class);
        } catch (Exception exception) {
            return null;
        }
    }

    private void applyCustomTimeIfEnabled() {
        try {
            CustomTimeModule customtimemodule = ClientMain.getInstance().getModuleManager().getModule(CustomTimeModule.class);
            if (customtimemodule != null && customtimemodule.isEnabled() && this.client.field_1687 != null) {
                long i = customtimemodule.worldTimeSetting.getLongValue();
                this.client.field_1687.method_29089(this.client.field_1687.method_8510(), i, false);
            }
        } catch (Exception var4) {
        }
    }

    private void renderWeatherWithoutBorder(FrameGraphBuilder framegraphbuilder, Vec3d vec3d, float f, Fog fog) {
        try {
            DefaultFramebufferSet defaultframebufferset = this.framebufferSetField.getValue();
            BufferBuilderStorage bufferbuilderstorage = this.bufferBuildersField.getValue();
            int i = this.ticksField.getInt(this);
            RenderPass renderpass = framegraphbuilder.method_61911("weather");
            if (defaultframebufferset.field_53095 != null) {
                defaultframebufferset.field_53095 = renderpass.method_61933(defaultframebufferset.field_53095);
            } else {
                defaultframebufferset.field_53091 = renderpass.method_61933(defaultframebufferset.field_53091);
            }

            renderpass.method_61929(
                () -> {
                    try {
                        RenderSystem.setShaderFog(fog);
                        Immediate immediate = bufferbuilderstorage.method_23000();
                        Object object = this.weatherRenderingField.getValue();
                        if (object != null) {
                            Method method = object.getClass()
                                .getDeclaredMethod(
                                    ClientMain.getInstance().isDev() ? "renderPrecipitation" : "method_62316",
                                    World.class,
                                    VertexConsumerProvider.class,
                                    int.class,
                                    float.class,
                                    Vec3d.class
                                );
                            method.setAccessible(true);
                            method.invoke(object, this.client.field_1687, immediate, i, f, vec3d);
                        }

                        immediate.method_22993();
                    } catch (Exception exception2) {
                        exception2.printStackTrace();
                    }
                }
            );
        } catch (Exception exception1) {
            exception1.printStackTrace();

            try {
                this.renderWeatherMethod.invoke(this, framegraphbuilder, vec3d, f, fog);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
