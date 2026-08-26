package dev.mark.system.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.mark.system.core.ClientMain;
import dev.mark.system.enums.HeartType;
import dev.mark.system.gui.CustomHotbarRenderer;
import dev.mark.system.hook.CustomBossBarHud;
import dev.mark.system.hud.HudManager;
import dev.mark.system.module.client.CleanHudModule;
import dev.mark.system.module.client.HudModule;
import dev.mark.system.module.combat.AimAssistModule;
import dev.mark.system.module.combat.HitboxModule;
import dev.mark.system.module.player.AutoSwapModule;
import dev.mark.system.module.render.NoRenderModule;
import dev.mark.system.module.render.TabTweaksModule;
import dev.mark.system.module.visual.TracersModule;
import dev.mark.system.module.visual.WaypointsModule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fStack;

@Environment(EnvType.CLIENT)
public class CustomInGameHud extends InGameHud {
    private static final Identifier CROSSHAIR_TEXTURE = Identifier.method_60656("hud/crosshair");
    private static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE = Identifier.method_60656("hud/crosshair_attack_indicator_full");
    private static final Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = Identifier.method_60656("hud/crosshair_attack_indicator_background");
    private static final Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE = Identifier.method_60656("hud/crosshair_attack_indicator_progress");
    private static final Identifier EFFECT_BACKGROUND_AMBIENT_TEXTURE = Identifier.method_60656("hud/effect_background_ambient");
    private static final Identifier EFFECT_BACKGROUND_TEXTURE = Identifier.method_60656("hud/effect_background");
    private static final Identifier HOTBAR_TEXTURE = Identifier.method_60656("hud/hotbar");
    private static final Identifier HOTBAR_SELECTION_TEXTURE = Identifier.method_60656("hud/hotbar_selection");
    private static final Identifier HOTBAR_OFFHAND_LEFT_TEXTURE = Identifier.method_60656("hud/hotbar_offhand_left");
    private static final Identifier HOTBAR_OFFHAND_RIGHT_TEXTURE = Identifier.method_60656("hud/hotbar_offhand_right");
    private static final Identifier HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = Identifier.method_60656("hud/hotbar_attack_indicator_background");
    private static final Identifier HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE = Identifier.method_60656("hud/hotbar_attack_indicator_progress");
    private static final Identifier JUMP_BAR_BACKGROUND_TEXTURE = Identifier.method_60656("hud/jump_bar_background");
    private static final Identifier JUMP_BAR_COOLDOWN_TEXTURE = Identifier.method_60656("hud/jump_bar_cooldown");
    private static final Identifier JUMP_BAR_PROGRESS_TEXTURE = Identifier.method_60656("hud/jump_bar_progress");
    private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = Identifier.method_60656("hud/experience_bar_background");
    private static final Identifier EXPERIENCE_BAR_PROGRESS_TEXTURE = Identifier.method_60656("hud/experience_bar_progress");
    private static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.method_60656("hud/armor_empty");
    private static final Identifier ARMOR_HALF_TEXTURE = Identifier.method_60656("hud/armor_half");
    private static final Identifier ARMOR_FULL_TEXTURE = Identifier.method_60656("hud/armor_full");
    private static final Identifier FOOD_EMPTY_HUNGER_TEXTURE = Identifier.method_60656("hud/food_empty_hunger");
    private static final Identifier FOOD_HALF_HUNGER_TEXTURE = Identifier.method_60656("hud/food_half_hunger");
    private static final Identifier FOOD_FULL_HUNGER_TEXTURE = Identifier.method_60656("hud/food_full_hunger");
    private static final Identifier FOOD_EMPTY_TEXTURE = Identifier.method_60656("hud/food_empty");
    private static final Identifier FOOD_HALF_TEXTURE = Identifier.method_60656("hud/food_half");
    private static final Identifier FOOD_FULL_TEXTURE = Identifier.method_60656("hud/food_full");
    private static final Identifier AIR_TEXTURE = Identifier.method_60656("hud/air");
    private static final Identifier AIR_BURSTING_TEXTURE = Identifier.method_60656("hud/air_bursting");
    private static final Identifier AIR_EMPTY_TEXTURE = Identifier.method_60656("hud/air_empty");
    private static final Identifier VEHICLE_CONTAINER_HEART_TEXTURE = Identifier.method_60656("hud/heart/vehicle_container");
    private static final Identifier VEHICLE_FULL_HEART_TEXTURE = Identifier.method_60656("hud/heart/vehicle_full");
    private static final Identifier VEHICLE_HALF_HEART_TEXTURE = Identifier.method_60656("hud/heart/vehicle_half");
    private static final Identifier VIGNETTE_TEXTURE = Identifier.method_60656("textures/misc/vignette.png");
    public static final Identifier NAUSEA_TEXTURE = Identifier.method_60656("textures/misc/nausea.png");
    private static final Identifier SPYGLASS_SCOPE = Identifier.method_60656("textures/misc/spyglass_scope.png");
    private static final Identifier POWDER_SNOW_OUTLINE = Identifier.method_60656("textures/misc/powder_snow_outline.png");
    private static final Comparator<ScoreboardEntry> SCOREBOARD_ENTRY_COMPARATOR = Comparator.comparing(ScoreboardEntry::comp_2128)
        .reversed()
        .thenComparing(ScoreboardEntry::comp_2127, String.CASE_INSENSITIVE_ORDER);
    private static final Text DEMO_EXPIRED_MESSAGE = Text.method_43471("demo.demoExpired");
    private static final Text SAVING_LEVEL_TEXT = Text.method_43471("menu.savingLevel");
    private static final float field_32168 = 5.0F;
    private static final int field_32169 = 10;
    private static final int field_32170 = 10;
    private static final String SCOREBOARD_JOINER = ": ";
    private static final float field_32172 = 0.2F;
    private static final int field_33942 = 9;
    private static final int field_33943 = 8;
    private static final int field_54914 = 10;
    private static final int field_54915 = 9;
    private static final int field_54916 = 8;
    private static final int field_54917 = 2;
    private static final int SUBMERGED_IN_WATER_AIR_BUBBLE_DELAY = 1;
    private static final float field_54920 = 0.5F;
    private static final float field_54921 = 0.1F;
    private static final float field_54922 = 1.0F;
    private static final float field_54923 = 0.1F;
    private static final int field_54924 = 3;
    private static final int field_54925 = 5;
    private static final float field_35431 = 0.2F;
    private static final int field_52769 = 5;
    private static final int field_52770 = 5;
    private final Random random = Random.method_43047();
    private final MinecraftClient client;
    private final CustomChatHud chatHud;
    private int ticks;
    @Nullable
    private Text overlayMessage;
    private int overlayRemaining;
    private boolean overlayTinted;
    private boolean canShowChatDisabledScreen;
    public float vignetteDarkness = 1.0F;
    private int heldItemTooltipFade;
    private ItemStack currentStack = ItemStack.field_8037;
    private final DebugHud debugHud;
    private final SubtitlesHud subtitlesHud;
    private final SpectatorHud spectatorHud;
    private final CustomPlayerListHud PlayerListHudClass;
    private final CustomBossBarHud bossBarHud;
    private int titleRemainTicks;
    @Nullable
    private Text title;
    @Nullable
    private Text subtitle;
    private int titleFadeInTicks;
    private int titleStayTicks;
    private int titleFadeOutTicks;
    private int lastHealthValue;
    private int renderHealthValue;
    private long lastHealthCheckTime;
    private long heartJumpEndTick;
    private int lastBurstBubble;
    private float autosaveIndicatorAlpha;
    private float lastAutosaveIndicatorAlpha;
    private final LayeredDrawer layeredDrawer = new LayeredDrawer();
    private float spyglassScale;
    private CustomHotbarRenderer hotBar;

    public CustomInGameHud(MinecraftClient minecraftclient) {
        super(minecraftclient);
        this.client = minecraftclient;
        this.debugHud = new DebugHud(minecraftclient);
        this.spectatorHud = new SpectatorHud(minecraftclient);
        this.chatHud = new CustomChatHud(minecraftclient);
        this.PlayerListHudClass = new CustomPlayerListHud(minecraftclient, this);
        this.bossBarHud = new CustomBossBarHud(minecraftclient);
        this.subtitlesHud = new SubtitlesHud(minecraftclient);
        this.method_1742();
        LayeredDrawer layereddrawer = new LayeredDrawer()
            .method_55810(this::renderMiscOverlays)
            .method_55810(this::renderCrosshair)
            .method_55810(this::renderMainHud)
            .method_55810(this::renderExperienceLevel)
            .method_55810(this::renderStatusEffectOverlay)
            .method_55810((drawcontext, rendertickcounter) -> this.bossBarHud.method_1796(drawcontext));
        LayeredDrawer layereddrawer1 = new LayeredDrawer()
            .method_55810(this::renderDemoTimer)
            .method_55810((drawcontext, rendertickcounter) -> {
                if (this.debugHud.method_53536()) {
                    this.debugHud.method_1846(drawcontext);
                }
            })
            .method_55810(this::renderScoreboardSidebar)
            .method_55810(this::renderOverlayMessage)
            .method_55810(this::renderTitleAndSubtitle)
            .method_55810(this::renderChat)
            .method_55810((drawcontext, rendertickcounter) -> this.subtitlesHud.method_1957(drawcontext));
        this.layeredDrawer
            .method_55811(layereddrawer, () -> !minecraftclient.field_1690.field_1842)
            .method_55810(this::renderSleepOverlay)
            .method_55811(layereddrawer1, () -> !minecraftclient.field_1690.field_1842);
        this.hotBar = new CustomHotbarRenderer();
        this.hotBar.dc.a(0);
    }

    public void method_1742() {
        this.titleFadeInTicks = 10;
        this.titleStayTicks = 70;
        this.titleFadeOutTicks = 20;
    }

    public void method_1753(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        this.layeredDrawer.method_55809(drawcontext, rendertickcounter);
        AutoSwapModule autoswapmodule = ClientMain.getInstance().getModuleManager().getModule(AutoSwapModule.class);
        if (autoswapmodule != null && autoswapmodule.isEnabled()) {
            autoswapmodule.M(drawcontext);
        }

        WaypointsModule waypointsmodule = ClientMain.getInstance().getModuleManager().getModule(WaypointsModule.class);
        if (waypointsmodule != null) {
            waypointsmodule.d(drawcontext);
        }

        TracersModule tracersmodule = ClientMain.getInstance().getModuleManager().getModule(TracersModule.class);
        if (tracersmodule != null) {
            tracersmodule.b(drawcontext);
        }

        AimAssistModule aimassistmodule = ClientMain.getInstance().getModuleManager().getModule(AimAssistModule.class);
        if (aimassistmodule != null) {
            aimassistmodule.d(drawcontext);
        }

        this.renderPlayerList(drawcontext, rendertickcounter);
        HudManager.b().e(drawcontext);
    }

    private void renderMiscOverlays(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        if (MinecraftClient.method_1517()) {
            this.renderVignetteOverlay(drawcontext, this.client.method_1560());
        }

        float f = rendertickcounter.method_60636();
        this.spyglassScale = MathHelper.method_16439(0.5F * f, this.spyglassScale, 1.125F);
        if (this.client.field_1690.method_31044().method_31034()) {
            if (this.client.field_1724.method_31550()) {
                this.renderSpyglassOverlay(drawcontext, this.spyglassScale);
            } else {
                this.spyglassScale = 0.5F;

                for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                    ItemStack itemstack = this.client.field_1724.method_6118(equipmentslot);
                    EquippableComponent equippablecomponent = (EquippableComponent)itemstack.method_57824(DataComponentTypes.field_54196);
                    if (equippablecomponent != null && equippablecomponent.comp_3174() == equipmentslot && equippablecomponent.comp_3306().isPresent()) {
                        this.renderOverlay(drawcontext, ((Identifier)equippablecomponent.comp_3306().get()).method_45134(s -> "textures/" + s + ".png"), 1.0F);
                    }
                }
            }
        }

        if (this.client.field_1724.method_32312() > 0) {
            this.renderOverlay(drawcontext, POWDER_SNOW_OUTLINE, this.client.field_1724.method_32313());
        }

        float f1 = MathHelper.method_16439(rendertickcounter.method_60637(false), this.client.field_1724.field_44912, this.client.field_1724.field_44911);
        if (f1 > 0.0F) {
            if (!this.client.field_1724.method_6059(StatusEffects.field_5916)) {
                this.renderPortalOverlay(drawcontext, f1);
                return;
            }

            float f2 = ((Double)this.client.field_1690.method_42453().method_41753()).floatValue();
            if (f2 < 1.0F) {
                float f3 = f1 * (1.0F - f2);
                this.renderNauseaOverlay(drawcontext, f3);
            }
        }
    }

    private void renderSleepOverlay(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        if (this.client.field_1724.method_7297() > 0) {
            Profilers.method_64146().method_15396("sleep");
            float f = this.client.field_1724.method_7297();
            float f1 = f / 100.0F;
            if (f1 > 1.0F) {
                f1 = 1.0F - (f - 100.0F) / 10.0F;
            }

            int i = (int)(220.0F * f1) << 24 | 1052704;
            drawcontext.method_51739(RenderLayer.method_51785(), 0, 0, drawcontext.method_51421(), drawcontext.method_51443(), i);
            Profilers.method_64146().method_15407();
        }
    }

    private void renderOverlayMessage(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        TextRenderer textrenderer = this.method_1756();
        if (this.overlayMessage != null && this.overlayRemaining > 0) {
            Profilers.method_64146().method_15396("overlayMessage");
            float f = this.overlayRemaining - rendertickcounter.method_60637(false);
            int i = (int)(f * 255.0F / 20.0F);
            if (i > 255) {
                i = 255;
            }

            if (i > 8) {
                drawcontext.method_51448().method_22903();
                drawcontext.method_51448().method_46416(drawcontext.method_51421() / 2, drawcontext.method_51443() - 68, 0.0F);
                int j;
                if (this.overlayTinted) {
                    j = MathHelper.method_60599(f / 50.0F, 0.7F, 0.6F, i);
                } else {
                    j = ColorHelper.method_61330(i, -1);
                }

                int k = textrenderer.method_27525(this.overlayMessage);
                drawcontext.method_60649(textrenderer, this.overlayMessage, -k / 2, -4, k, j);
                drawcontext.method_51448().method_22909();
            }

            Profilers.method_64146().method_15407();
        }
    }

    private void renderTitleAndSubtitle(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        if (this.title != null && this.titleRemainTicks > 0) {
            TextRenderer textrenderer = this.method_1756();
            Profilers.method_64146().method_15396("titleAndSubtitle");
            float f = this.titleRemainTicks - rendertickcounter.method_60637(false);
            int i = 255;
            if (this.titleRemainTicks > this.titleFadeOutTicks + this.titleStayTicks) {
                float f1 = this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks - f;
                i = (int)(f1 * 255.0F / this.titleFadeInTicks);
            }

            if (this.titleRemainTicks <= this.titleFadeOutTicks) {
                i = (int)(f * 255.0F / this.titleFadeOutTicks);
            }

            i = MathHelper.method_15340(i, 0, 255);
            if (i > 8) {
                drawcontext.method_51448().method_22903();
                drawcontext.method_51448().method_46416(drawcontext.method_51421() / 2, drawcontext.method_51443() / 2, 0.0F);
                drawcontext.method_51448().method_22903();
                drawcontext.method_51448().method_22905(4.0F, 4.0F, 4.0F);
                int l = textrenderer.method_27525(this.title);
                int j = ColorHelper.method_61330(i, -1);
                drawcontext.method_60649(textrenderer, this.title, -l / 2, -10, l, j);
                drawcontext.method_51448().method_22909();
                if (this.subtitle != null) {
                    drawcontext.method_51448().method_22903();
                    drawcontext.method_51448().method_22905(2.0F, 2.0F, 2.0F);
                    int k = textrenderer.method_27525(this.subtitle);
                    drawcontext.method_60649(textrenderer, this.subtitle, -k / 2, 5, k, j);
                    drawcontext.method_51448().method_22909();
                }

                drawcontext.method_51448().method_22909();
            }

            Profilers.method_64146().method_15407();
        }
    }

    private void renderChat(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().getModule(CleanHudModule.class);
        if ((cleanhudmodule == null || !cleanhudmodule.isEnabled()) && !this.chatHud.method_1819()) {
            Window window = this.client.method_22683();
            int i = MathHelper.method_15357(this.client.field_1729.method_1603() * window.method_4486() / window.method_4480());
            int j = MathHelper.method_15357(this.client.field_1729.method_1604() * window.method_4502() / window.method_4507());
            this.chatHud.method_1805(drawcontext, this.ticks, i, j, false);
        }
    }

    private void renderPlayerList(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        Scoreboard scoreboard = this.client.field_1687.method_8428();
        ScoreboardObjective scoreboardobjective = scoreboard.method_1189(ScoreboardDisplaySlot.field_45156);
        boolean flag = this.client.field_1690.field_1907.method_1434()
            && (!this.client.method_1542() || this.client.field_1724.field_3944.method_45732().size() > 1 || scoreboardobjective != null);
        TabTweaksModule tabtweaksmodule = ClientMain.getInstance().getModuleManager().getModule(TabTweaksModule.class);
        boolean flag1 = tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.c().getValue();
        if (flag1) {
            if (flag) {
                this.PlayerListHudClass.method_1921(true);
            } else if (!this.PlayerListHudClass.isVisible()) {
                this.PlayerListHudClass.method_1921(false);
            } else {
                this.PlayerListHudClass.method_1921(false);
            }

            if (this.PlayerListHudClass.isVisible()) {
                this.PlayerListHudClass.method_1919(drawcontext, drawcontext.method_51421(), scoreboard, scoreboardobjective);
                return;
            }
        } else {
            if (!flag) {
                this.PlayerListHudClass.method_1921(false);
                return;
            }

            this.PlayerListHudClass.method_1921(true);
            this.PlayerListHudClass.method_1919(drawcontext, drawcontext.method_51421(), scoreboard, scoreboardobjective);
        }
    }

    private void renderCrosshair(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        GameOptions gameoptions = this.client.field_1690;
        if (gameoptions.method_31044().method_31034()
            && (this.client.field_1761.method_2920() != GameMode.field_9219 || this.shouldRenderSpectatorCrosshair(this.client.field_1765))) {
            if (this.debugHud.method_53536() && !this.client.field_1724.method_7302() && !(Boolean)gameoptions.method_42442().method_41753()) {
                Camera camera1 = this.client.field_1773.method_19418();
                Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
                matrix4fstack.pushMatrix();
                matrix4fstack.mul(drawcontext.method_51448().method_23760().method_23761());
                matrix4fstack.translate(drawcontext.method_51421() / 2, drawcontext.method_51443() / 2, 0.0F);
                matrix4fstack.rotateX(-camera1.method_19329() * (float) (Math.PI / 180.0));
                matrix4fstack.rotateY(camera1.method_19330() * (float) (Math.PI / 180.0));
                matrix4fstack.scale(-1.0F, -1.0F, -1.0F);
                RenderSystem.renderCrosshair(10);
                matrix4fstack.popMatrix();
                return;
            }

            byte b0 = 15;
            drawcontext.method_52706(
                RenderLayer::method_62280, CROSSHAIR_TEXTURE, (drawcontext.method_51421() - 15) / 2, (drawcontext.method_51443() - 15) / 2, 15, 15
            );
            if (this.client.field_1690.method_42565().method_41753() == AttackIndicator.field_18152) {
                HitboxModule hitboxmodule = ClientMain.getInstance().getModuleManager().getModule(HitboxModule.class);
                NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
                if (NoRenderModule != null && NoRenderModule.isEnabled() && NoRenderModule.attackIndicatorSetting.getValue()) {
                    return;
                }

                if (hitboxmodule != null
                    && hitboxmodule.isEnabled()
                    && hitboxmodule.Y().getValue()
                    && this.client.field_1692 != null
                    && this.client.field_1692 instanceof LivingEntity livingentity) {
                    Box box = livingentity.method_5864().method_18386().method_30757(livingentity.method_19538());
                    Camera camera = this.client.field_1773.method_19418();
                    Vec3d vec3d = camera.method_19326();
                    float f = camera.method_19329();
                    float f1 = camera.method_19330();
                    Vec3d vec3d1 = Vec3d.method_1030(f, f1);
                    double d0 = this.client.field_1761.method_2920().method_8386() ? 5.0 : 4.5;
                    Vec3d vec3d2 = vec3d.method_1019(vec3d1.method_1021(d0));
                    Optional optional = box.method_992(vec3d, vec3d2);
                    if (optional.isEmpty()) {
                        return;
                    }
                }

                float f2 = this.client.field_1724.method_7261(0.0F);
                boolean flag = false;
                if (this.client.field_1692 != null && this.client.field_1692 instanceof LivingEntity && f2 >= 1.0F) {
                    flag = this.client.field_1724.method_7279() > 5.0F;
                    flag &= this.client.field_1692.method_5805();
                }

                int i = drawcontext.method_51443() / 2 - 7 + 16;
                int j = drawcontext.method_51421() / 2 - 8;
                if (flag) {
                    drawcontext.method_52706(RenderLayer::method_62280, CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, j, i, 16, 16);
                    return;
                }

                if (f2 < 1.0F) {
                    int k = (int)(f2 * 17.0F);
                    drawcontext.method_52706(RenderLayer::method_62280, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, j, i, 16, 4);
                    drawcontext.method_52708(RenderLayer::method_62280, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, j, i, k, 4);
                }
            }
        }
    }

    private boolean shouldRenderSpectatorCrosshair(@Nullable HitResult hitresult) {
        if (hitresult == null) {
            return false;
        } else if (hitresult.method_17783() == Type.field_1331) {
            return ((EntityHitResult)hitresult).method_17782() instanceof NamedScreenHandlerFactory;
        } else if (hitresult.method_17783() == Type.field_1332) {
            BlockPos blockpos = ((BlockHitResult)hitresult).method_17777();
            ClientWorld clientworld = this.client.field_1687;
            return clientworld.method_8320(blockpos).method_26196(clientworld, blockpos) != null;
        } else {
            return false;
        }
    }

    private void renderStatusEffectOverlay(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
        HudModule hudmodule = ClientMain.getInstance().getModuleManager().getModule(HudModule.class);
        if (NoRenderModule == null || !NoRenderModule.isEnabled() || !NoRenderModule.effectsSetting.getValue()) {
            Collection collection = this.client.field_1724.method_6026();
            if (!collection.isEmpty() && (this.client.field_1755 == null || !this.client.field_1755.method_64507())) {
                int i = 0;
                int j = 0;
                StatusEffectSpriteManager statuseffectspritemanager = this.client.method_18505();
                ArrayList arraylist = Lists.newArrayListWithExpectedSize(collection.size());

                for (Object _sei : Ordering.natural().reverse().sortedCopy(collection)) {
                    StatusEffectInstance statuseffectinstance = (StatusEffectInstance)_sei;
                    RegistryEntry registryentry = statuseffectinstance.method_5579();
                    if (statuseffectinstance.method_5592()) {
                        int k = drawcontext.method_51421();
                        byte b0 = 1;
                        if (this.client.method_1530()) {
                            b0 = (byte)(b0 + 15);
                        }

                        if (((StatusEffect)registryentry.comp_349()).method_5573()) {
                            i++;
                            k -= 25 * i;
                        } else {
                            j++;
                            k -= 25 * j;
                            b0 = (byte)(b0 + 26);
                        }

                        float f = 1.0F;
                        if (statuseffectinstance.method_5591()) {
                            drawcontext.method_52706(RenderLayer::method_62277, EFFECT_BACKGROUND_AMBIENT_TEXTURE, k, b0, 24, 24);
                        } else {
                            drawcontext.method_52706(RenderLayer::method_62277, EFFECT_BACKGROUND_TEXTURE, k, b0, 24, 24);
                            if (statuseffectinstance.method_48557(200)) {
                                int l = statuseffectinstance.method_5584();
                                int i1 = 10 - l / 20;
                                f = MathHelper.method_15363(l / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
                                    + MathHelper.method_15362(l * (float) Math.PI / 5.0F) * MathHelper.method_15363(i1 / 10.0F * 0.25F, 0.0F, 0.25F);
                                f = MathHelper.method_15363(f, 0.0F, 1.0F);
                            }
                        }

                        Sprite sprite = statuseffectspritemanager.method_18663(registryentry);
                        int j1 = k;
                        byte b1 = b0;
                        float f1 = f;
                        arraylist.add(() -> {
                            int i2 = ColorHelper.method_61317(f1);
                            drawcontext.method_52710(RenderLayer::method_62277, sprite, j1 + 3, b1 + 3, 18, 18, i2);
                        });
                    }
                }

                arraylist.forEach(r -> ((Runnable)r).run());
            }
        }
    }

    private void renderMainHud(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        if (this.client.field_1761.method_2920() == GameMode.field_9219) {
            this.spectatorHud.method_1978(drawcontext);
        } else {
            this.renderHotbar(drawcontext, rendertickcounter);
        }

        int i = drawcontext.method_51421() / 2 - 91;
        JumpingMount jumpingmount = this.client.field_1724.method_45773();
        if (jumpingmount != null) {
            this.renderMountJumpBar(jumpingmount, drawcontext, i);
        } else if (this.shouldRenderExperience()) {
            this.renderExperienceBar(drawcontext, i);
        }

        if (this.client.field_1761.method_2908()) {
            this.renderStatusBars(drawcontext);
        }

        this.renderMountHealth(drawcontext);
        if (this.client.field_1761.method_2920() != GameMode.field_9219) {
            this.renderHeldItemTooltip(drawcontext);
        } else if (this.client.field_1724.method_7325()) {
            this.spectatorHud.method_1979(drawcontext);
        }
    }

    private void renderHotbar(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().getModule(CleanHudModule.class);
        if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
            PlayerEntity playerentity = this.getCameraPlayer();
            if (playerentity != null) {
                ItemStack itemstack = playerentity.method_6079();
                Arm arm = playerentity.method_6068().method_5928();
                int i = drawcontext.method_51421() / 2;
                int j = drawcontext.method_51443();
                HudModule hudmodule = ClientMain.getInstance().getModuleManager().getModule(HudModule.class);
                boolean flag = hudmodule != null && hudmodule.isEnabled() && hudmodule.n().getValue();
                if (flag) {
                    this.hotBar.a(drawcontext, rendertickcounter, playerentity, itemstack, arm, i, j);
                    return;
                }

                this.renderVanillaHotbar(drawcontext, rendertickcounter, playerentity, itemstack, arm, i, j);
            }
        }
    }

    private void renderVanillaHotbar(
        DrawContext drawcontext, RenderTickCounter rendertickcounter, PlayerEntity playerentity, ItemStack itemstack, Arm arm, int i, int j
    ) {
        int k = i;
        short short1 = 182;
        byte b0 = 91;
        drawcontext.method_51448().method_22903();
        drawcontext.method_51448().method_46416(0.0F, 0.0F, -90.0F);
        drawcontext.method_52706(RenderLayer::method_62277, HOTBAR_TEXTURE, i - 91, j - 22, 182, 22);
        drawcontext.method_52706(
            RenderLayer::method_62277, HOTBAR_SELECTION_TEXTURE, i - 91 - 1 + playerentity.method_31548().field_7545 * 20, j - 22 - 1, 24, 23
        );
        if (!itemstack.method_7960()) {
            if (arm == Arm.field_6182) {
                drawcontext.method_52706(RenderLayer::method_62277, HOTBAR_OFFHAND_LEFT_TEXTURE, i - 91 - 29, j - 23, 29, 24);
            } else {
                drawcontext.method_52706(RenderLayer::method_62277, HOTBAR_OFFHAND_RIGHT_TEXTURE, i + 91, j - 23, 29, 24);
            }
        }

        drawcontext.method_51448().method_22909();
        int l = 1;

        for (int i1 = 0; i1 < 9; i1++) {
            int j1 = k - 90 + i1 * 20 + 2;
            int k1 = j - 16 - 3;
            this.renderHotbarItem(drawcontext, j1, k1, rendertickcounter, playerentity, (ItemStack)playerentity.method_31548().field_7547.get(i1), l++);
        }

        if (!itemstack.method_7960()) {
            int i2 = j - 16 - 3;
            if (arm == Arm.field_6182) {
                this.renderHotbarItem(drawcontext, k - 91 - 26, i2, rendertickcounter, playerentity, itemstack, l++);
            } else {
                this.renderHotbarItem(drawcontext, k + 91 + 10, i2, rendertickcounter, playerentity, itemstack, l++);
            }
        }

        if (this.client.field_1690.method_42565().method_41753() == AttackIndicator.field_18153) {
            float f = this.client.field_1724.method_7261(0.0F);
            if (f < 1.0F) {
                int j2 = j - 20;
                int k2 = k + 91 + 6;
                if (arm == Arm.field_6183) {
                    k2 = k - 91 - 22;
                }

                int l1 = (int)(f * 19.0F);
                drawcontext.method_52706(RenderLayer::method_62277, HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, k2, j2, 18, 18);
                drawcontext.method_52708(RenderLayer::method_62277, HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 18, 18, 0, 18 - l1, k2, j2 + 18 - l1, 18, l1);
            }
        }
    }

    private void renderMountJumpBar(JumpingMount jumpingmount, DrawContext drawcontext, int i) {
        Profilers.method_64146().method_15396("jumpBar");
        float f = this.client.field_1724.method_3151();
        short short1 = 182;
        int j = (int)(f * 183.0F);
        int k = drawcontext.method_51443() - 32 + 3;
        drawcontext.method_52706(RenderLayer::method_62277, JUMP_BAR_BACKGROUND_TEXTURE, i, k, 182, 5);
        if (jumpingmount.method_45327() > 0) {
            drawcontext.method_52706(RenderLayer::method_62277, JUMP_BAR_COOLDOWN_TEXTURE, i, k, 182, 5);
        } else if (j > 0) {
            drawcontext.method_52708(RenderLayer::method_62277, JUMP_BAR_PROGRESS_TEXTURE, 182, 5, 0, 0, i, k, j, 5);
        }

        Profilers.method_64146().method_15407();
    }

    private void renderExperienceBar(DrawContext drawcontext, int i) {
        CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().getModule(CleanHudModule.class);
        if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
            Profilers.method_64146().method_15396("expBar");
            HudModule hudmodule = ClientMain.getInstance().getModuleManager().getModule(HudModule.class);
            boolean flag = hudmodule != null && hudmodule.isEnabled() && hudmodule.n().getValue();
            if (!flag) {
                int j = this.client.field_1724.method_7349();
                if (j > 0) {
                    short short1 = 182;
                    int k = (int)(this.client.field_1724.field_7510 * 183.0F);
                    int l = drawcontext.method_51443() - 32 + 3;
                    drawcontext.method_52706(RenderLayer::method_62277, EXPERIENCE_BAR_BACKGROUND_TEXTURE, i, l, 182, 5);
                    if (k > 0) {
                        drawcontext.method_52708(RenderLayer::method_62277, EXPERIENCE_BAR_PROGRESS_TEXTURE, 182, 5, 0, 0, i, l, k, 5);
                    }
                }
            }

            Profilers.method_64146().method_15407();
        }
    }

    private void renderExperienceLevel(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        int i = this.client.field_1724.field_7520;
        if (this.shouldRenderExperience() && i > 0) {
            Profilers.method_64146().method_15396("expLevel");
            String s = i + "";
            int j = (drawcontext.method_51421() - this.method_1756().method_1727(s)) / 2;
            int k = drawcontext.method_51443() - 31 - 4;
            drawcontext.method_51433(this.method_1756(), s, j + 1, k, 0, false);
            drawcontext.method_51433(this.method_1756(), s, j - 1, k, 0, false);
            drawcontext.method_51433(this.method_1756(), s, j, k + 1, 0, false);
            drawcontext.method_51433(this.method_1756(), s, j, k - 1, 0, false);
            drawcontext.method_51433(this.method_1756(), s, j, k, 8453920, false);
            Profilers.method_64146().method_15407();
        }
    }

    private boolean shouldRenderExperience() {
        return this.client.field_1724.method_45773() == null && this.client.field_1761.method_2913();
    }

    private void renderHeldItemTooltip(DrawContext drawcontext) {
        CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().getModule(CleanHudModule.class);
        if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
            Profilers.method_64146().method_15396("selectedItemName");
            if (this.heldItemTooltipFade > 0 && !this.currentStack.method_7960()) {
                MutableText mutabletext = Text.method_43473()
                    .method_10852(this.currentStack.method_7964())
                    .method_27692(this.currentStack.method_7932().method_58413());
                if (this.currentStack.method_57826(DataComponentTypes.field_49631)) {
                    mutabletext.method_27692(Formatting.field_1056);
                }

                int i = this.method_1756().method_27525(mutabletext);
                int j = (drawcontext.method_51421() - i) / 2;
                int k = drawcontext.method_51443() - 59;
                if (!this.client.field_1761.method_2908()) {
                    k += 14;
                }

                int l = (int)(this.heldItemTooltipFade * 256.0F / 10.0F);
                if (l > 255) {
                    l = 255;
                }

                if (l > 0) {
                    drawcontext.method_60649(this.method_1756(), mutabletext, j, k, i, ColorHelper.method_61330(l, -1));
                }
            }

            Profilers.method_64146().method_15407();
        }
    }

    private void renderDemoTimer(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        if (this.client.method_1530()) {
            Profilers.method_64146().method_15396("demo");
            Object object;
            if (this.client.field_1687.method_8510() >= 120500L) {
                object = DEMO_EXPIRED_MESSAGE;
            } else {
                object = Text.method_43469(
                    "demo.remainingTime",
                    new Object[]{
                        StringHelper.method_15439((int)(120500L - this.client.field_1687.method_8510()), this.client.field_1687.method_54719().method_54748())
                    }
                );
            }

            int i = this.method_1756().method_27525((StringVisitable)object);
            int j = drawcontext.method_51421() - i - 10;
            byte b0 = 5;
            drawcontext.method_60649(this.method_1756(), (Text)object, j, 5, i, -1);
            Profilers.method_64146().method_15407();
        }
    }

    private void renderScoreboardSidebar(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().getModule(CleanHudModule.class);
        if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
            NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
            if (NoRenderModule == null || !NoRenderModule.isEnabled() || !NoRenderModule.scoreboardSetting.getValue()) {
                Scoreboard scoreboard = this.client.field_1687.method_8428();
                ScoreboardObjective scoreboardobjective = null;
                Team team = scoreboard.method_1164(this.client.field_1724.method_5820());
                if (team != null) {
                    ScoreboardDisplaySlot scoreboarddisplayslot = ScoreboardDisplaySlot.method_52622(team.method_1202());
                    if (scoreboarddisplayslot != null) {
                        scoreboardobjective = scoreboard.method_1189(scoreboarddisplayslot);
                    }
                }

                ScoreboardObjective scoreboardobjective1 = scoreboardobjective != null
                    ? scoreboardobjective
                    : scoreboard.method_1189(ScoreboardDisplaySlot.field_45157);
                if (scoreboardobjective1 != null) {
                    this.renderScoreboardSidebar(drawcontext, scoreboardobjective1);
                }
            }
        }
    }

    private void renderScoreboardSidebar(DrawContext drawcontext, ScoreboardObjective scoreboardobjective) {
        Scoreboard scoreboard = scoreboardobjective.method_1117();
        NumberFormat numberformat = scoreboardobjective.method_55380(StyledNumberFormat.field_47567);
        CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().getModule(CleanHudModule.class);
        if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
            ScoreboardEntryData[] ajp = scoreboard.method_1184(scoreboardobjective)
                .stream()
                .filter(scoreboardentry -> !scoreboardentry.method_55385())
                .sorted(SCOREBOARD_ENTRY_COMPARATOR)
                .limit(15L)
                .map(scoreboardentry -> {
                    Team team = scoreboard.method_1164(scoreboardentry.comp_2127());
                    Text text1 = scoreboardentry.method_55387();
                    MutableText mutabletext = Team.method_1142(team, text1);
                    MutableText mutabletext1 = scoreboardentry.method_55386(numberformat);
                    int j3 = this.method_1756().method_27525(mutabletext1);
                    return new ScoreboardEntryData(mutabletext, mutabletext1, j3);
                })
                .toArray(ScoreboardEntryData[]::new);
            Text text = scoreboardobjective.method_1114();
            int i = this.method_1756().method_27525(text);
            int j = i;
            int k = this.method_1756().method_1727(": ");

            for (ScoreboardEntryData jpx : ajp) {
                j = Math.max(j, this.method_1756().method_27525(jpx.name()) + (jpx.scoreWidth() > 0 ? k + jpx.scoreWidth() : 0));
            }

            int k2 = ajp.length;
            int l2 = k2 * 9;
            int i3 = drawcontext.method_51443() / 2 + l2 / 3;
            byte b0 = 3;
            int l = drawcontext.method_51421() - j - 3;
            int i1 = drawcontext.method_51421() - 3 + 2;
            int j1 = this.client.field_1690.method_19345(0.3F);
            int k1 = this.client.field_1690.method_19345(0.4F);
            int l1 = i3 - k2 * 9;
            drawcontext.method_25294(l - 2, l1 - 9 - 1, i1, l1 - 1, k1);
            drawcontext.method_25294(l - 2, l1 - 1, i1, i3, j1);
            drawcontext.method_51439(this.method_1756(), text, l + j / 2 - i / 2, l1 - 9, -1, false);

            for (int i2 = 0; i2 < k2; i2++) {
                ScoreboardEntryData ScoreboardEntryData = ajp[i2];
                int j2 = i3 - (k2 - i2) * 9;
                drawcontext.method_51439(this.method_1756(), ScoreboardEntryData.name(), l, j2, -1, false);
                drawcontext.method_51439(this.method_1756(), ScoreboardEntryData.score(), i1 - ScoreboardEntryData.scoreWidth(), j2, -1, false);
            }
        }
    }

    @Nullable
    private PlayerEntity getCameraPlayer() {
        return this.client.method_1560() instanceof PlayerEntity playerentity ? playerentity : null;
    }

    @Nullable
    private LivingEntity getRiddenEntity() {
        PlayerEntity playerentity = this.getCameraPlayer();
        if (playerentity != null) {
            Entity entity = playerentity.method_5854();
            if (entity == null) {
                return null;
            }

            if (entity instanceof LivingEntity) {
                return (LivingEntity)entity;
            }
        }

        return null;
    }

    private int getHeartCount(@Nullable LivingEntity livingentity) {
        if (livingentity != null && livingentity.method_5709()) {
            float f = livingentity.method_6063();
            int i = (int)(f + 0.5F) / 2;
            if (i > 30) {
                i = 30;
            }

            return i;
        } else {
            return 0;
        }
    }

    private int getHeartRows(int i) {
        return (int)Math.ceil(i / 10.0);
    }

    private void renderStatusBars(DrawContext drawcontext) {
        CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().getModule(CleanHudModule.class);
        if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
            PlayerEntity playerentity = this.getCameraPlayer();
            if (playerentity != null) {
                int i = MathHelper.method_15386(playerentity.method_6032());
                boolean flag = this.heartJumpEndTick > this.ticks && (this.heartJumpEndTick - this.ticks) / 3L % 2L == 1L;
                long j = Util.method_658();
                if (i < this.lastHealthValue && playerentity.field_6008 > 0) {
                    this.lastHealthCheckTime = j;
                    this.heartJumpEndTick = this.ticks + 20;
                } else if (i > this.lastHealthValue && playerentity.field_6008 > 0) {
                    this.lastHealthCheckTime = j;
                    this.heartJumpEndTick = this.ticks + 10;
                }

                if (j - this.lastHealthCheckTime > 1000L) {
                    this.renderHealthValue = i;
                    this.lastHealthCheckTime = j;
                }

                this.lastHealthValue = i;
                int k = this.renderHealthValue;
                this.random.method_43052(this.ticks * 312871);
                int l = drawcontext.method_51421() / 2 - 91;
                int i1 = drawcontext.method_51421() / 2 + 91;
                int j1 = drawcontext.method_51443() - 39;
                float f = Math.max((float)playerentity.method_45325(EntityAttributes.field_23716), Math.max(k, i));
                int k1 = MathHelper.method_15386(playerentity.method_6067());
                int l1 = MathHelper.method_15386((f + k1) / 2.0F / 10.0F);
                int i2 = Math.max(10 - (l1 - 2), 3);
                int j2 = j1 - 10;
                int k2 = -1;
                if (playerentity.method_6059(StatusEffects.field_5924)) {
                    k2 = this.ticks % MathHelper.method_15386(f + 5.0F);
                }

                Profilers.method_64146().method_15396("armor");
                renderArmor(drawcontext, playerentity, j1, l1, i2, l);
                Profilers.method_64146().method_15405("health");
                this.renderHealthBar(drawcontext, playerentity, l, j1, i2, k2, f, i, k, k1, flag);
                LivingEntity livingentity = this.getRiddenEntity();
                int l2 = this.getHeartCount(livingentity);
                if (l2 == 0) {
                    Profilers.method_64146().method_15405("food");
                    this.renderFood(drawcontext, playerentity, j1, i1);
                    j2 -= 10;
                }

                Profilers.method_64146().method_15405("air");
                this.renderAirBubbles(drawcontext, playerentity, l2, j2, i1);
                Profilers.method_64146().method_15407();
            }
        }
    }

    private static void renderArmor(DrawContext drawcontext, PlayerEntity playerentity, int i, int j, int k, int l) {
        int i1 = playerentity.method_6096();
        if (i1 > 0) {
            int j1 = i - (j - 1) * k - 10;

            for (int k1 = 0; k1 < 10; k1++) {
                int l1 = l + k1 * 8;
                if (k1 * 2 + 1 < i1) {
                    drawcontext.method_52706(RenderLayer::method_62277, ARMOR_FULL_TEXTURE, l1, j1, 9, 9);
                }

                if (k1 * 2 + 1 == i1) {
                    drawcontext.method_52706(RenderLayer::method_62277, ARMOR_HALF_TEXTURE, l1, j1, 9, 9);
                }

                if (k1 * 2 + 1 > i1) {
                    drawcontext.method_52706(RenderLayer::method_62277, ARMOR_EMPTY_TEXTURE, l1, j1, 9, 9);
                }
            }
        }
    }

    private void renderHealthBar(DrawContext drawcontext, PlayerEntity playerentity, int i, int j, int k, int l, float f, int i1, int j1, int k1, boolean flag) {
        HeartType hearttype = HeartType.fromPlayerState(playerentity);
        boolean flag1 = playerentity.method_37908().method_8401().method_152();
        int l1 = MathHelper.method_15384(f / 2.0);
        int i2 = MathHelper.method_15384(k1 / 2.0);
        int j2 = l1 * 2;

        for (int k2 = l1 + i2 - 1; k2 >= 0; k2--) {
            int l2 = k2 / 10;
            int i3 = k2 % 10;
            int j3 = i + i3 * 8;
            int k3 = j - l2 * k;
            if (i1 + k1 <= 4) {
                k3 += this.random.method_43048(2);
            }

            if (k2 < l1 && k2 == l) {
                k3 -= 2;
            }

            this.drawHeart(drawcontext, HeartType.CONTAINER, j3, k3, flag1, flag, false);
            int l3 = k2 * 2;
            boolean flag2 = k2 >= l1;
            if (flag2) {
                int i4 = l3 - j2;
                if (i4 < k1) {
                    boolean flag3 = i4 + 1 == k1;
                    this.drawHeart(drawcontext, hearttype == HeartType.WITHERED ? hearttype : HeartType.ABSORBING, j3, k3, flag1, false, flag3);
                }
            }

            if (flag && l3 < j1) {
                boolean flag4 = l3 + 1 == j1;
                this.drawHeart(drawcontext, hearttype, j3, k3, flag1, true, flag4);
            }

            if (l3 < i1) {
                boolean flag5 = l3 + 1 == i1;
                this.drawHeart(drawcontext, hearttype, j3, k3, flag1, false, flag5);
            }
        }
    }

    private void drawHeart(DrawContext drawcontext, HeartType hearttype, int i, int j, boolean flag, boolean flag1, boolean flag2) {
        drawcontext.method_52706(RenderLayer::method_62277, hearttype.getTexture(flag, flag2, flag1), i, j, 9, 9);
    }

    private void renderAirBubbles(DrawContext drawcontext, PlayerEntity playerentity, int i, int j, int k) {
        NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
        if (NoRenderModule == null || !NoRenderModule.isEnabled() || !NoRenderModule.bubblesSetting.getValue()) {
            int l = playerentity.method_5748();
            int i1 = Math.clamp(playerentity.method_5669(), 0, l);
            boolean flag = playerentity.method_5777(FluidTags.field_15517);
            if (flag || i1 < l) {
                j = this.getAirBubbleY(i, j);
                int j1 = getAirBubbles(i1, l, -2);
                int k1 = getAirBubbles(i1, l, 0);
                int l1 = 10 - getAirBubbles(i1, l, getAirBubbleDelay(i1, flag));
                boolean flag1 = j1 != k1;
                if (!flag) {
                    this.lastBurstBubble = 0;
                }

                for (int i2 = 1; i2 <= 10; i2++) {
                    int j2 = k - (i2 - 1) * 8 - 9;
                    if (i2 <= j1) {
                        drawcontext.method_52706(RenderLayer::method_62277, AIR_TEXTURE, j2, j, 9, 9);
                    } else if (flag1 && i2 == k1 && flag) {
                        drawcontext.method_52706(RenderLayer::method_62277, AIR_BURSTING_TEXTURE, j2, j, 9, 9);
                        this.playBurstSound(i2, playerentity, l1);
                    } else if (i2 > 10 - l1) {
                        int k2 = l1 == 10 && this.ticks % 2 == 0 ? this.random.method_43048(2) : 0;
                        drawcontext.method_52706(RenderLayer::method_62277, AIR_EMPTY_TEXTURE, j2, j + k2, 9, 9);
                    }
                }
            }
        }
    }

    private int getAirBubbleY(int i, int j) {
        int k = this.getHeartRows(i) - 1;
        return j - k * 10;
    }

    private static int getAirBubbles(int i, int j, int k) {
        return MathHelper.method_15386((float)((i + k) * 10) / j);
    }

    private static int getAirBubbleDelay(int i, boolean flag) {
        return i != 0 && flag ? 1 : 0;
    }

    private void playBurstSound(int i, PlayerEntity playerentity, int j) {
        if (this.lastBurstBubble != i) {
            float f = 0.5F + 0.1F * Math.max(0, j - 3 + 1);
            float f1 = 1.0F + 0.1F * Math.max(0, j - 5 + 1);
            playerentity.method_5783(SoundEvents.field_54891, f, f1);
            this.lastBurstBubble = i;
        }
    }

    private void renderFood(DrawContext drawcontext, PlayerEntity playerentity, int i, int j) {
        HungerManager hungermanager = playerentity.method_7344();
        int k = hungermanager.method_7586();
        int l = (int)Math.ceil(hungermanager.method_7589());
        HudModule hudmodule = ClientMain.getInstance().getModuleManager().getModule(HudModule.class);
        if (l > 0 && hudmodule != null && hudmodule.isEnabled() && hudmodule.p().getValue()) {
            int i1 = i - 10;

            for (int j1 = 0; j1 < 10; j1++) {
                int k1 = j - j1 * 8 - 9;
                if (j1 * 2 + 1 <= k) {
                    drawcontext.method_52706(RenderLayer::method_62277, FOOD_EMPTY_TEXTURE, k1, i1, 9, 9);
                }

                if (j1 * 2 + 1 < l) {
                    drawcontext.method_52706(RenderLayer::method_62277, FOOD_FULL_TEXTURE, k1, i1, 9, 9);
                } else if (j1 * 2 + 1 == l) {
                    drawcontext.method_52706(RenderLayer::method_62277, FOOD_HALF_TEXTURE, k1, i1, 9, 9);
                }
            }
        }

        for (int i2 = 0; i2 < 10; i2++) {
            int j2 = i;
            Identifier identifier1;
            Identifier identifier2;
            Identifier identifier;
            if (playerentity.method_6059(StatusEffects.field_5903)) {
                identifier2 = FOOD_EMPTY_HUNGER_TEXTURE;
                identifier = FOOD_HALF_HUNGER_TEXTURE;
                identifier1 = FOOD_FULL_HUNGER_TEXTURE;
            } else {
                identifier2 = FOOD_EMPTY_TEXTURE;
                identifier = FOOD_HALF_TEXTURE;
                identifier1 = FOOD_FULL_TEXTURE;
            }

            if (playerentity.method_7344().method_7589() <= 0.0F && this.ticks % (k * 3 + 1) == 0) {
                j2 = i + (this.random.method_43048(3) - 1);
            }

            int l1 = j - i2 * 8 - 9;
            drawcontext.method_52706(RenderLayer::method_62277, identifier2, l1, j2, 9, 9);
            if (i2 * 2 + 1 < k) {
                drawcontext.method_52706(RenderLayer::method_62277, identifier1, l1, j2, 9, 9);
            }

            if (i2 * 2 + 1 == k) {
                drawcontext.method_52706(RenderLayer::method_62277, identifier, l1, j2, 9, 9);
            }
        }
    }

    private void renderMountHealth(DrawContext drawcontext) {
        LivingEntity livingentity = this.getRiddenEntity();
        if (livingentity != null) {
            int i = this.getHeartCount(livingentity);
            if (i != 0) {
                int j = (int)Math.ceil(livingentity.method_6032());
                Profilers.method_64146().method_15405("mountHealth");
                int k = drawcontext.method_51443() - 39;
                int l = drawcontext.method_51421() / 2 + 91;
                int i1 = k;

                for (byte b0 = 0; i > 0; b0 = (byte)(b0 + 20)) {
                    int j1 = Math.min(i, 10);
                    i -= j1;

                    for (int k1 = 0; k1 < j1; k1++) {
                        int l1 = l - k1 * 8 - 9;
                        drawcontext.method_52706(RenderLayer::method_62277, VEHICLE_CONTAINER_HEART_TEXTURE, l1, i1, 9, 9);
                        if (k1 * 2 + 1 + b0 < j) {
                            drawcontext.method_52706(RenderLayer::method_62277, VEHICLE_FULL_HEART_TEXTURE, l1, i1, 9, 9);
                        }

                        if (k1 * 2 + 1 + b0 == j) {
                            drawcontext.method_52706(RenderLayer::method_62277, VEHICLE_HALF_HEART_TEXTURE, l1, i1, 9, 9);
                        }
                    }

                    i1 -= 10;
                }
            }
        }
    }

    private void renderOverlay(DrawContext drawcontext, Identifier identifier, float f) {
        int i = ColorHelper.method_61317(f);
        drawcontext.method_25291(
            RenderLayer::method_62275,
            identifier,
            0,
            0,
            0.0F,
            0.0F,
            drawcontext.method_51421(),
            drawcontext.method_51443(),
            drawcontext.method_51421(),
            drawcontext.method_51443(),
            i
        );
    }

    private void renderSpyglassOverlay(DrawContext drawcontext, float f) {
        float f1 = Math.min(drawcontext.method_51421(), drawcontext.method_51443());
        float f2 = Math.min(drawcontext.method_51421() / f1, drawcontext.method_51443() / f1) * f;
        int i = MathHelper.method_15375(f1 * f2);
        int j = MathHelper.method_15375(f1 * f2);
        int k = (drawcontext.method_51421() - i) / 2;
        int l = (drawcontext.method_51443() - j) / 2;
        int i1 = k + i;
        int j1 = l + j;
        drawcontext.method_25290(RenderLayer::method_62277, SPYGLASS_SCOPE, k, l, 0.0F, 0.0F, i, j, i, j);
        drawcontext.method_48196(RenderLayer.method_51785(), 0, j1, drawcontext.method_51421(), drawcontext.method_51443(), -90, -16777216);
        drawcontext.method_48196(RenderLayer.method_51785(), 0, 0, drawcontext.method_51421(), l, -90, -16777216);
        drawcontext.method_48196(RenderLayer.method_51785(), 0, l, k, j1, -90, -16777216);
        drawcontext.method_48196(RenderLayer.method_51785(), i1, l, drawcontext.method_51421(), j1, -90, -16777216);
    }

    private void updateVignetteDarkness(Entity entity) {
        CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().getModule(CleanHudModule.class);
        if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
            NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
            if (NoRenderModule != null && NoRenderModule.isEnabled() && NoRenderModule.vignetteSetting.getValue()) {
                this.vignetteDarkness = 0.0F;
            } else {
                BlockPos blockpos = BlockPos.method_49637(entity.method_23317(), entity.method_23320(), entity.method_23321());
                float f = LightmapTextureManager.method_23284(entity.method_37908().method_8597(), entity.method_37908().method_22339(blockpos));
                float f1 = MathHelper.method_15363(1.0F - f, 0.0F, 1.0F);
                this.vignetteDarkness = this.vignetteDarkness + (f1 - this.vignetteDarkness) * 0.01F;
            }
        }
    }

    private void renderVignetteOverlay(DrawContext drawcontext, @Nullable Entity entity) {
        WorldBorder worldborder = this.client.field_1687.method_8621();
        float f = 0.0F;
        if (entity != null) {
            float f1 = (float)worldborder.method_11979(entity);
            double d0 = Math.min(
                worldborder.method_11974() * worldborder.method_11956() * 1000.0, Math.abs(worldborder.method_11954() - worldborder.method_11965())
            );
            double d1 = Math.max(worldborder.method_11972(), d0);
            if (f1 < d1) {
                f = 1.0F - (float)(f1 / d1);
            }
        }

        int i;
        if (f > 0.0F) {
            f = MathHelper.method_15363(f, 0.0F, 1.0F);
            i = ColorHelper.method_61318(1.0F, 0.0F, f, f);
        } else {
            float f2 = this.vignetteDarkness;
            f2 = MathHelper.method_15363(f2, 0.0F, 1.0F);
            i = ColorHelper.method_61318(1.0F, f2, f2, f2);
        }

        drawcontext.method_25291(
            RenderLayer::method_62279,
            VIGNETTE_TEXTURE,
            0,
            0,
            0.0F,
            0.0F,
            drawcontext.method_51421(),
            drawcontext.method_51443(),
            drawcontext.method_51421(),
            drawcontext.method_51443(),
            i
        );
    }

    private void renderPortalOverlay(DrawContext drawcontext, float f) {
        if (f < 1.0F) {
            f *= f;
            f *= f;
            f = f * 0.8F + 0.2F;
        }

        int i = ColorHelper.method_61317(f);
        Sprite sprite = this.client.method_1541().method_3351().method_3339(Blocks.field_10316.method_9564());
        drawcontext.method_52710(RenderLayer::method_62275, sprite, 0, 0, drawcontext.method_51421(), drawcontext.method_51443(), i);
    }

    private void renderNauseaOverlay(DrawContext drawcontext, float f) {
        NoRenderModule NoRenderModule = ClientMain.getInstance().getModuleManager().getModule(NoRenderModule.class);
        if (NoRenderModule == null || !NoRenderModule.isEnabled() || !NoRenderModule.nauseaSetting.getValue()) {
            int i = drawcontext.method_51421();
            int j = drawcontext.method_51443();
            drawcontext.method_51448().method_22903();
            float f1 = MathHelper.method_16439(f, 2.0F, 1.0F);
            drawcontext.method_51448().method_46416(i / 2.0F, j / 2.0F, 0.0F);
            drawcontext.method_51448().method_22905(f1, f1, f1);
            drawcontext.method_51448().method_46416(-i / 2.0F, -j / 2.0F, 0.0F);
            float f2 = 0.2F * f;
            float f3 = 0.4F * f;
            float f4 = 0.2F * f;
            drawcontext.method_25291(
                identifier -> RenderLayer.method_62284(), NAUSEA_TEXTURE, 0, 0, 0.0F, 0.0F, i, j, i, j, ColorHelper.method_61318(1.0F, f2, f3, f4)
            );
            drawcontext.method_51448().method_22909();
        }
    }

    private void renderHotbarItem(
        DrawContext drawcontext, int i, int j, RenderTickCounter rendertickcounter, PlayerEntity playerentity, ItemStack itemstack, int k
    ) {
        if (!itemstack.method_7960()) {
            float f = itemstack.method_7965() - rendertickcounter.method_60637(false);
            if (f > 0.0F) {
                float f1 = 1.0F + f / 5.0F;
                drawcontext.method_51448().method_22903();
                drawcontext.method_51448().method_46416(i + 8, j + 12, 0.0F);
                drawcontext.method_51448().method_22905(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                drawcontext.method_51448().method_46416(-(i + 8), -(j + 12), 0.0F);
            }

            drawcontext.method_51423(playerentity, itemstack, i, j, k);
            if (f > 0.0F) {
                drawcontext.method_51448().method_22909();
            }

            drawcontext.method_51431(this.client.field_1772, itemstack, i, j);
        }
    }

    public void method_39191(boolean flag) {
        this.tickAutosaveIndicator();
        if (!flag) {
            this.tick();
        }
    }

    private void tick() {
        if (this.overlayRemaining > 0) {
            this.overlayRemaining--;
        }

        if (this.titleRemainTicks > 0) {
            this.titleRemainTicks--;
            if (this.titleRemainTicks <= 0) {
                this.title = null;
                this.subtitle = null;
            }
        }

        this.ticks++;
        Entity entity = this.client.method_1560();
        if (entity != null) {
            this.updateVignetteDarkness(entity);
        }

        if (this.client.field_1724 != null) {
            ItemStack itemstack = this.client.field_1724.method_31548().method_7391();
            if (itemstack.method_7960()) {
                this.heldItemTooltipFade = 0;
            } else if (this.currentStack.method_7960()
                || !itemstack.method_31574(this.currentStack.method_7909())
                || !itemstack.method_7964().equals(this.currentStack.method_7964())) {
                this.heldItemTooltipFade = (int)(40.0 * (Double)this.client.field_1690.method_48191().method_41753());
            } else if (this.heldItemTooltipFade > 0) {
                this.heldItemTooltipFade--;
            }

            this.currentStack = itemstack;
        }

        this.chatHud.method_45584();
    }

    private void tickAutosaveIndicator() {
        IntegratedServer integratedserver = this.client.method_1576();
        boolean flag = integratedserver != null && integratedserver.method_39219();
        this.lastAutosaveIndicatorAlpha = this.autosaveIndicatorAlpha;
        this.autosaveIndicatorAlpha = MathHelper.method_16439(0.2F, this.autosaveIndicatorAlpha, flag ? 1.0F : 0.0F);
    }

    public void method_1732(Text text) {
        MutableText mutabletext = Text.method_43469("record.nowPlaying", new Object[]{text});
        this.method_1758(mutabletext, true);
        this.client.method_44713().method_37015(mutabletext);
    }

    public void method_1758(Text text, boolean flag) {
        this.method_44354(false);
        this.overlayMessage = text;
        this.overlayRemaining = 60;
        this.overlayTinted = flag;
    }

    public void method_44354(boolean flag) {
        this.canShowChatDisabledScreen = flag;
    }

    public boolean method_44353() {
        return this.canShowChatDisabledScreen && this.overlayRemaining > 0;
    }

    public void method_34001(int i, int j, int k) {
        if (i >= 0) {
            this.titleFadeInTicks = i;
        }

        if (j >= 0) {
            this.titleStayTicks = j;
        }

        if (k >= 0) {
            this.titleFadeOutTicks = k;
        }

        if (this.titleRemainTicks > 0) {
            this.titleRemainTicks = this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks;
        }
    }

    public void method_34002(Text text) {
        this.subtitle = text;
    }

    public void method_34004(Text text) {
        this.title = text;
        this.titleRemainTicks = this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks;
    }

    public void method_34003() {
        this.title = null;
        this.subtitle = null;
        this.titleRemainTicks = 0;
    }

    public CustomChatHud getChatHud() {
        return this.chatHud;
    }

    public int method_1738() {
        return this.ticks;
    }

    public TextRenderer method_1756() {
        return this.client.field_1772;
    }

    public SpectatorHud method_1739() {
        return this.spectatorHud;
    }

    public PlayerListHud method_1750() {
        return this.PlayerListHudClass;
    }

    public CustomPlayerListHud getPlayerListHudClass() {
        return this.PlayerListHudClass;
    }

    public void method_1747() {
        this.PlayerListHudClass.method_1920();
        this.bossBarHud.method_1801();
        this.client.method_1566().method_2000();
        this.debugHud.method_53545();
        this.chatHud.method_1808(true);
        this.method_34003();
        this.method_1742();
    }

    public BossBarHud method_1740() {
        return this.bossBarHud;
    }

    public DebugHud method_53531() {
        return this.debugHud;
    }

    public void method_1745() {
        this.debugHud.method_1842();
    }

    public void method_39192(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
        if ((Boolean)this.client.field_1690.method_42452().method_41753() && (this.autosaveIndicatorAlpha > 0.0F || this.lastAutosaveIndicatorAlpha > 0.0F)) {
            int i = MathHelper.method_15375(
                255.0F
                    * MathHelper.method_15363(
                        MathHelper.method_16439(rendertickcounter.method_60638(), this.lastAutosaveIndicatorAlpha, this.autosaveIndicatorAlpha), 0.0F, 1.0F
                    )
            );
            if (i > 8) {
                TextRenderer textrenderer = this.method_1756();
                int j = textrenderer.method_27525(SAVING_LEVEL_TEXT);
                int k = ColorHelper.method_61330(i, -1);
                int l = drawcontext.method_51421() - j - 5;
                int i1 = drawcontext.method_51443() - 9 - 5;
                drawcontext.method_60649(textrenderer, SAVING_LEVEL_TEXT, l, i1, j, k);
            }
        }
    }
}
