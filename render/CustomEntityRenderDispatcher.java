package dev.mark.system.render;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import dev.mark.system.core.ClientMain;
import dev.mark.system.module.render.HitboxTweaksModule;
import dev.mark.system.module.render.NoRenderModule;
import dev.mark.system.module.visual.PlayerOutlinesModule;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.SkinTextures.Model;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class CustomEntityRenderDispatcher extends EntityRenderDispatcher {
    private final Map<Model, EntityRendererFactory<AbstractClientPlayerEntity>> PLAYER_RENDERER_FACTORIES = Map.of(
        Model.field_41123,
        context -> new CustomPlayerEntityRenderer(context, false),
        Model.field_41122,
        context -> new CustomPlayerEntityRenderer(context, true)
    );
    private static final RenderLayer SHADOW_LAYER = RenderLayer.method_24469(Identifier.method_60656("textures/misc/shadow.png"));
    private Map<Model, EntityRenderer<? extends PlayerEntity, ?>> modelRenderers = Map.of();
    private final ItemModelManager itemModelManager;
    private final MapRenderer mapRenderer;
    private final BlockRenderManager blockRenderManager;
    private final TextRenderer textRenderer;
    public final TextureManager textureManager;
    public final GameOptions gameOptions;
    private final Supplier<LoadedEntityModels> entityModelsGetter;
    private final EquipmentModelLoader equipmentModelLoader;
    private final HeldItemRenderer itemInHandRenderer;
    private World world;
    private Quaternionf rotation;
    private boolean renderHitboxes;
    private boolean renderShadows = true;

    public CustomEntityRenderDispatcher(
        MinecraftClient minecraftclient,
        TextureManager texturemanager,
        ItemModelManager itemmodelmanager,
        ItemRenderer itemrenderer,
        MapRenderer maprenderer,
        BlockRenderManager blockrendermanager,
        TextRenderer textrenderer,
        GameOptions gameoptions,
        Supplier<LoadedEntityModels> supplier,
        EquipmentModelLoader equipmentmodelloader
    ) {
        super(
            minecraftclient,
            texturemanager,
            itemmodelmanager,
            itemrenderer,
            maprenderer,
            blockrendermanager,
            textrenderer,
            gameoptions,
            supplier,
            equipmentmodelloader
        );
        this.textureManager = texturemanager;
        this.itemModelManager = itemmodelmanager;
        this.mapRenderer = maprenderer;
        this.blockRenderManager = blockrendermanager;
        this.textRenderer = textrenderer;
        this.itemInHandRenderer = new HeldItemRenderer(minecraftclient, this, itemrenderer, itemmodelmanager);
        this.gameOptions = gameoptions;
        this.entityModelsGetter = supplier;
        this.equipmentModelLoader = equipmentmodelloader;
    }

    public void method_3941(World world, Camera camera, Entity entity) {
        this.world = world;
        this.field_4686 = camera;
        this.rotation = camera.method_23767();
        this.field_4678 = entity;
    }

    public <T extends Entity> EntityRenderer<? super T, ?> method_3953(T entity) {
        if (entity instanceof AbstractClientPlayerEntity abstractclientplayerentity) {
            Model model = abstractclientplayerentity.method_52814().comp_1629();
            EntityRenderer entityrenderer = this.modelRenderers.get(model);
            return entityrenderer != null ? entityrenderer : this.modelRenderers.get(Model.field_41123);
        } else {
            return super.method_3953(entity);
        }
    }

    public <E extends Entity> void method_62424(
        E entity, double d0, double d1, double d2, float f, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i
    ) {
        EntityRenderer entityrenderer = this.method_3953(entity);
        this.render(entity, d0, d1, d2, f, matrixstack, vertexconsumerprovider, i, entityrenderer);
    }

    private <E extends Entity, S extends EntityRenderState> void render(
        E entity,
        double d0,
        double d1,
        double d2,
        float f,
        MatrixStack matrixstack,
        VertexConsumerProvider vertexconsumerprovider,
        int i,
        EntityRenderer<? super E, S> entityrenderer
    ) {
        try {
            if (vertexconsumerprovider instanceof OutlineVertexConsumerProvider outlinevertexconsumerprovider) {
                PlayerOutlinesModule playeroutlinesmodule = PlayerOutlinesModule.h();
                if (playeroutlinesmodule != null && playeroutlinesmodule.isEnabled() && playeroutlinesmodule.f(entity)) {
                    int[] aint = playeroutlinesmodule.g();
                    outlinevertexconsumerprovider.method_23286(aint[0], aint[1], aint[2], aint[3]);
                }
            }

            EntityRenderState entityrenderstate = entityrenderer.method_62425(entity, f);
            Vec3d vec3d = entityrenderer.method_23169(entityrenderstate);
            double d6 = d0 + vec3d.method_10216();
            double d3 = d1 + vec3d.method_10214();
            double d4 = d2 + vec3d.method_10215();
            matrixstack.method_22903();
            matrixstack.method_22904(d6, d3, d4);
            entityrenderer.method_3936(entityrenderstate, matrixstack, vertexconsumerprovider, i);
            if (entityrenderstate.field_53335) {
                this.renderFire(
                    matrixstack, vertexconsumerprovider, entityrenderstate, MathHelper.method_53948(MathHelper.field_46242, this.rotation, new Quaternionf())
                );
            }

            if (entity instanceof PlayerEntity) {
                matrixstack.method_22904(-vec3d.method_10216(), -vec3d.method_10214(), -vec3d.method_10215());
            }

            if ((Boolean)this.gameOptions.method_42435().method_41753() && this.renderShadows && !entityrenderstate.field_53333) {
                float f1 = 0.15F;
                if (f1 > 0.0F) {
                    double d5 = entityrenderstate.field_53332;
                    float f2 = (float)((1.0 - d5 / 256.0) * 1.0);
                    if (f2 > 0.0F) {
                        renderShadow(matrixstack, vertexconsumerprovider, entityrenderstate, f2, f, this.world, Math.min(f1, 32.0F));
                    }
                }
            }

            if (!(entity instanceof PlayerEntity)) {
                matrixstack.method_22904(-vec3d.method_10216(), -vec3d.method_10214(), -vec3d.method_10215());
            }

            if (this.renderHitboxes && !entityrenderstate.field_53333 && !MinecraftClient.method_1551().method_1555()) {
                HitboxTweaksModule hitboxtweaksmodule = ClientMain.getInstance().getModuleManager().getModule(HitboxTweaksModule.class);
                if (hitboxtweaksmodule != null && hitboxtweaksmodule.isEnabled()) {
                    hitboxtweaksmodule.a();
                    hitboxtweaksmodule.b(matrixstack, vertexconsumerprovider, entity, f);
                } else {
                    renderHitbox(matrixstack, vertexconsumerprovider.getBuffer(RenderLayer.method_23594()), entity, f, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }

            matrixstack.method_22909();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.method_560(throwable, "Rendering entity in world");
            CrashReportSection crashreportsection = crashreport.method_562("Entity being rendered");
            entity.method_5819(crashreportsection);
            CrashReportSection crashreportsection1 = crashreport.method_562("Renderer details");
            crashreportsection1.method_578("Assigned renderer", entityrenderer);
            crashreportsection1.method_578("Location", CrashReportSection.method_583(this.world, d0, d1, d2));
            crashreportsection1.method_578("Delta", f);
            throw new CrashException(crashreport);
        }
    }

    private static void renderServerSideHitbox(MatrixStack matrixstack, Entity entity, VertexConsumerProvider vertexconsumerprovider) {
        Entity entity1 = getIntegratedServerEntity(entity);
        if (entity1 == null) {
            DebugRenderer.method_23105(
                matrixstack, vertexconsumerprovider, "Missing", entity.method_23317(), entity.method_5829().field_1325 + 1.5, entity.method_23321(), -65536
            );
        } else {
            matrixstack.method_22903();
            matrixstack.method_22904(
                entity1.method_23317() - entity.method_23317(), entity1.method_23318() - entity.method_23318(), entity1.method_23321() - entity.method_23321()
            );
            renderHitbox(matrixstack, vertexconsumerprovider.getBuffer(RenderLayer.method_23594()), entity1, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F);
            VertexRendering.method_62298(
                matrixstack, vertexconsumerprovider.getBuffer(RenderLayer.method_23594()), new Vector3f(), entity1.method_18798(), -256
            );
            matrixstack.method_22909();
        }
    }

    @Nullable
    private static Entity getIntegratedServerEntity(Entity entity) {
        IntegratedServer integratedserver = MinecraftClient.method_1551().method_1576();
        if (integratedserver != null) {
            ServerWorld serverworld = integratedserver.method_3847(entity.method_37908().method_27983());
            if (serverworld != null) {
                return serverworld.method_8469(entity.method_5628());
            }
        }

        return null;
    }

    private static void renderHitbox(MatrixStack matrixstack, VertexConsumer vertexconsumer, Entity entity, float f, float f1, float f2, float f3, float f4) {
        Box box = entity.method_5829().method_989(-entity.method_23317(), -entity.method_23318(), -entity.method_23321());
        VertexRendering.method_62295(matrixstack, vertexconsumer, box, f1, f2, f3, f4);
        if (entity instanceof EnderDragonEntity) {
            double d0 = -MathHelper.method_16436(f, entity.field_6038, entity.method_23317());
            double d1 = -MathHelper.method_16436(f, entity.field_5971, entity.method_23318());
            double d2 = -MathHelper.method_16436(f, entity.field_5989, entity.method_23321());

            for (EnderDragonPart enderdragonpart : ((EnderDragonEntity)entity).method_5690()) {
                matrixstack.method_22903();
                double d3 = d0 + MathHelper.method_16436(f, enderdragonpart.field_6038, enderdragonpart.method_23317());
                double d4 = d1 + MathHelper.method_16436(f, enderdragonpart.field_5971, enderdragonpart.method_23318());
                double d5 = d2 + MathHelper.method_16436(f, enderdragonpart.field_5989, enderdragonpart.method_23321());
                matrixstack.method_22904(d3, d4, d5);
                VertexRendering.method_62295(
                    matrixstack,
                    vertexconsumer,
                    enderdragonpart.method_5829().method_989(-enderdragonpart.method_23317(), -enderdragonpart.method_23318(), -enderdragonpart.method_23321()),
                    0.25F,
                    1.0F,
                    0.0F,
                    1.0F
                );
                matrixstack.method_22909();
            }
        }

        if (entity instanceof LivingEntity) {
            float var25 = 0.01F;
        }

        Entity entity1 = entity.method_5854();
        if (entity1 != null) {
            float f5 = Math.min(entity1.method_17681(), entity.method_17681()) / 2.0F;
            float f7 = 0.0625F;
            Vec3d vec3d = entity1.method_52538(entity).method_1020(entity.method_19538());
            VertexRendering.method_62292(
                matrixstack,
                vertexconsumer,
                vec3d.field_1352 - f5,
                vec3d.field_1351,
                vec3d.field_1350 - f5,
                vec3d.field_1352 + f5,
                vec3d.field_1351 + 0.0625,
                vec3d.field_1350 + f5,
                1.0F,
                0.0F,
                0.0F,
                1.0F
            );
        }
    }

    private static void renderShadow(
        MatrixStack matrixstack,
        VertexConsumerProvider vertexconsumerprovider,
        EntityRenderState entityrenderstate,
        float f,
        float f4,
        WorldView worldview,
        float f1
    ) {
        float f2 = Math.min(f / 0.5F, f1);
        int i = MathHelper.method_15357(entityrenderstate.field_53325 - f1);
        int j = MathHelper.method_15357(entityrenderstate.field_53325 + f1);
        int k = MathHelper.method_15357(entityrenderstate.field_53326 - f2);
        int l = MathHelper.method_15357(entityrenderstate.field_53326);
        int i1 = MathHelper.method_15357(entityrenderstate.field_53327 - f1);
        int j1 = MathHelper.method_15357(entityrenderstate.field_53327 + f1);
        Entry entry = matrixstack.method_23760();
        VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(SHADOW_LAYER);
        Mutable mutable = new Mutable();

        for (int k1 = i1; k1 <= j1; k1++) {
            for (int l1 = i; l1 <= j; l1++) {
                mutable.method_10103(l1, 0, k1);
                Chunk chunk = worldview.method_22350(mutable);

                for (int i2 = k; i2 <= l; i2++) {
                    mutable.method_33098(i2);
                    float f3 = f - (float)(entityrenderstate.field_53326 - mutable.method_10264()) * 0.5F;
                    renderShadowPart(
                        entry,
                        vertexconsumer,
                        chunk,
                        worldview,
                        mutable,
                        entityrenderstate.field_53325,
                        entityrenderstate.field_53326,
                        entityrenderstate.field_53327,
                        f1,
                        f3
                    );
                }
            }
        }
    }

    private static void renderShadowPart(
        Entry entry, VertexConsumer vertexconsumer, Chunk chunk, WorldView worldview, BlockPos blockpos, double d0, double d1, double d2, float f, float f1
    ) {
        BlockPos blockpos1 = blockpos.method_10074();
        BlockState blockstate = chunk.method_8320(blockpos1);
        if (blockstate.method_26217() != BlockRenderType.field_11455 && worldview.method_22339(blockpos) > 3 && blockstate.method_26234(chunk, blockpos1)) {
            VoxelShape voxelshape = blockstate.method_26218(chunk, blockpos1);
            if (!voxelshape.method_1110()) {
                float f2 = LightmapTextureManager.method_23284(worldview.method_8597(), worldview.method_22339(blockpos));
                float f3 = f1 * 0.5F * f2;
                if (f3 >= 0.0F) {
                    if (f3 > 1.0F) {
                        f3 = 1.0F;
                    }

                    int i = ColorHelper.method_61324(MathHelper.method_15375(f3 * 255.0F), 255, 255, 255);
                    Box box = voxelshape.method_1107();
                    double d3 = blockpos.method_10263() + box.field_1323;
                    double d4 = blockpos.method_10263() + box.field_1320;
                    double d5 = blockpos.method_10264() + box.field_1322;
                    double d6 = blockpos.method_10260() + box.field_1321;
                    double d7 = blockpos.method_10260() + box.field_1324;
                    float f4 = (float)(d3 - d0);
                    float f5 = (float)(d4 - d0);
                    float f6 = (float)(d5 - d1);
                    float f7 = (float)(d6 - d2);
                    float f8 = (float)(d7 - d2);
                    float f9 = -f4 / 2.0F / f + 0.5F;
                    float f10 = -f5 / 2.0F / f + 0.5F;
                    float f11 = -f7 / 2.0F / f + 0.5F;
                    float f12 = -f8 / 2.0F / f + 0.5F;
                    drawShadowVertex(entry, vertexconsumer, i, f4, f6, f7, f9, f11);
                    drawShadowVertex(entry, vertexconsumer, i, f4, f6, f8, f9, f12);
                    drawShadowVertex(entry, vertexconsumer, i, f5, f6, f8, f10, f12);
                    drawShadowVertex(entry, vertexconsumer, i, f5, f6, f7, f10, f11);
                }
            }
        }
    }

    private static void drawShadowVertex(Entry entry, VertexConsumer vertexconsumer, int i, float f, float f1, float f2, float f3, float f4) {
        Vector3f vector3f = entry.method_23761().transformPosition(f, f1, f2, new Vector3f());
        vertexconsumer.method_23919(vector3f.x(), vector3f.y(), vector3f.z(), i, f3, f4, OverlayTexture.field_21444, 15728880, 0.0F, 1.0F, 0.0F);
    }

    private void renderFire(
        MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, EntityRenderState entityrenderstate, Quaternionf quaternionf
    ) {
        NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
        if (NoRenderModule == null || !NoRenderModule.isEnabled()) {
            Sprite sprite = ModelBaker.field_5397.method_24148();
            Sprite sprite1 = ModelBaker.field_5370.method_24148();
            matrixstack.method_22903();
            float f = entityrenderstate.field_53329 * 1.4F;
            matrixstack.method_22905(f, f, f);
            float f1 = 0.5F;
            float f2 = 0.0F;
            float f3 = entityrenderstate.field_53330 / f;
            float f4 = 0.0F;
            matrixstack.method_22907(quaternionf);
            matrixstack.method_46416(0.0F, 0.0F, 0.3F - (int)f3 * 0.02F);
            float f5 = 0.0F;
            int i = 0;
            VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(TexturedRenderLayers.method_24074());
            Entry entry = matrixstack.method_23760();

            while (f3 > 0.0F) {
                Sprite sprite2 = i % 2 == 0 ? sprite : sprite1;
                float f6 = sprite2.method_4594();
                float f7 = sprite2.method_4593();
                float f8 = sprite2.method_4577();
                float f9 = sprite2.method_4575();
                if (i / 2 % 2 == 0) {
                    float f10 = f8;
                    f8 = f6;
                    f6 = f10;
                }

                drawFireVertex(entry, vertexconsumer, -f1 - 0.0F, 0.0F - f4, f5, f8, f9);
                drawFireVertex(entry, vertexconsumer, f1 - 0.0F, 0.0F - f4, f5, f6, f9);
                drawFireVertex(entry, vertexconsumer, f1 - 0.0F, 1.4F - f4, f5, f6, f7);
                drawFireVertex(entry, vertexconsumer, -f1 - 0.0F, 1.4F - f4, f5, f8, f7);
                f3 -= 0.45F;
                f4 -= 0.45F;
                f1 *= 0.9F;
                f5 -= 0.03F;
                i++;
            }

            matrixstack.method_22909();
        }
    }

    private static void drawFireVertex(Entry entry, VertexConsumer vertexconsumer, float f, float f1, float f2, float f3, float f4) {
        NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
        if (NoRenderModule == null || !NoRenderModule.isEnabled()) {
            vertexconsumer.method_56824(entry, f, f1, f2)
                .method_39415(-1)
                .method_22913(f3, f4)
                .method_60796(0, 10)
                .method_60803(240)
                .method_60831(entry, 0.0F, 1.0F, 0.0F);
        }
    }

    public Map<Model, EntityRenderer<? extends PlayerEntity, ?>> reloadPlayerRenderers(Context context) {
        Builder builder = ImmutableMap.builder();
        this.PLAYER_RENDERER_FACTORIES.forEach((model, entityrendererfactory) -> {
            try {
                builder.put(model, entityrendererfactory.create(context));
            } catch (Exception exception) {
                String s = String.valueOf(model);
                throw new IllegalArgumentException("Failed SmallStateData create player model for " + s, exception);
            }
        });
        return builder.build();
    }

    public void method_3955(boolean flag) {
        this.renderHitboxes = flag;
        super.method_3955(flag);
    }

    public boolean method_3958() {
        return this.renderHitboxes;
    }

    public void method_3948(boolean flag) {
        this.renderShadows = flag;
        super.method_3948(flag);
    }

    public void method_14491(ResourceManager resourcemanager) {
        Context context = new Context(
            this,
            this.itemModelManager,
            this.mapRenderer,
            this.blockRenderManager,
            resourcemanager,
            this.entityModelsGetter.get(),
            this.equipmentModelLoader,
            this.textRenderer
        );
        this.modelRenderers = this.reloadPlayerRenderers(context);
        super.method_14491(resourcemanager);
    }

    public Quaternionf method_24197() {
        return this.rotation;
    }

    public void method_24196(Quaternionf quaternionf) {
        this.rotation = quaternionf;
    }
}
