package dev.mark.system.render;

import com.mojang.authlib.GameProfile;
import dev.mark.system.core.ClientMain;
import dev.mark.system.core.FriendManager;
import dev.mark.system.gui.ScoreboardScoreTracker;
import dev.mark.system.gui.animation.ProgressAnimation;
import dev.mark.system.gui.animation.easing.EasingFunctions;
import dev.mark.system.gui.model.ScoreboardEntryRecord;
import dev.mark.system.module.render.TabTweaksModule;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.ScoreboardCriterion.RenderType;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CustomPlayerListHud extends PlayerListHud {
    private static final Identifier PING_UNKNOWN_ICON_TEXTURE = Identifier.method_60656("icon/ping_unknown");
    private static final Identifier PING_1_ICON_TEXTURE = Identifier.method_60656("icon/ping_1");
    private static final Identifier PING_2_ICON_TEXTURE = Identifier.method_60656("icon/ping_2");
    private static final Identifier PING_3_ICON_TEXTURE = Identifier.method_60656("icon/ping_3");
    private static final Identifier PING_4_ICON_TEXTURE = Identifier.method_60656("icon/ping_4");
    private static final Identifier PING_5_ICON_TEXTURE = Identifier.method_60656("icon/ping_5");
    private static final Identifier CONTAINER_HEART_BLINKING_TEXTURE = Identifier.method_60656("hud/heart/container_blinking");
    private static final Identifier CONTAINER_HEART_TEXTURE = Identifier.method_60656("hud/heart/container");
    private static final Identifier FULL_HEART_BLINKING_TEXTURE = Identifier.method_60656("hud/heart/full_blinking");
    private static final Identifier HALF_HEART_BLINKING_TEXTURE = Identifier.method_60656("hud/heart/half_blinking");
    private static final Identifier ABSORBING_FULL_HEART_BLINKING_TEXTURE = Identifier.method_60656("hud/heart/absorbing_full_blinking");
    private static final Identifier FULL_HEART_TEXTURE = Identifier.method_60656("hud/heart/full");
    private static final Identifier ABSORBING_HALF_HEART_BLINKING_TEXTURE = Identifier.method_60656("hud/heart/absorbing_half_blinking");
    private static final Identifier HALF_HEART_TEXTURE = Identifier.method_60656("hud/heart/half");
    private static final Comparator<PlayerListEntry> ENTRY_ORDERING = Comparator.<PlayerListEntry>comparingInt(
            playerlistentry -> -playerlistentry.method_62154()
        )
        .thenComparingInt(playerlistentry -> playerlistentry.method_2958() == GameMode.field_9219 ? 1 : 0)
        .thenComparing(playerlistentry -> (String)Nullables.method_49078(playerlistentry.method_2955(), Team::method_1197, ""))
        .thenComparing(playerlistentry -> playerlistentry.method_2966().getName(), String::compareToIgnoreCase);
    public static final int MAX_ROWS = 20;
    private final MinecraftClient client;
    private final InGameHud inGameHud;
    @Nullable
    private Text footer;
    @Nullable
    private Text header;
    private boolean visible;
    private final Map<UUID, ScoreboardScoreTracker> hearts = new HashMap<>();
    private ProgressAnimation tabAnimation = null;
    private long lastVisibilityChange = 0L;
    private boolean wasVisible = false;
    private boolean shouldRenderForAnimation = false;

    public CustomPlayerListHud(MinecraftClient minecraftclient, InGameHud ingamehud) {
        super(minecraftclient, ingamehud);
        this.client = minecraftclient;
        this.inGameHud = ingamehud;
    }

    public Text method_1918(PlayerListEntry playerlistentry) {
        return playerlistentry.method_2971() != null
            ? this.applyGameModeFormatting(playerlistentry, playerlistentry.method_2971().method_27661())
            : this.applyGameModeFormatting(
                playerlistentry, Team.method_1142(playerlistentry.method_2955(), Text.method_43470(playerlistentry.method_2966().getName()))
            );
    }

    private Text applyGameModeFormatting(PlayerListEntry playerlistentry, MutableText mutabletext) {
        return playerlistentry.method_2958() == GameMode.field_9219 ? mutabletext.method_27692(Formatting.field_1056) : mutabletext;
    }

    public void method_1921(boolean flag) {
        if (this.tabAnimation == null) {
            this.tabAnimation = new ProgressAnimation();
            this.tabAnimation.a();
        }

        if (this.wasVisible != flag) {
            this.hearts.clear();
            this.wasVisible = flag;
            this.tabAnimation.e();
            this.tabAnimation.f(flag);
            this.lastVisibilityChange = System.currentTimeMillis();
            if (flag) {
                MutableText mutabletext = Texts.method_36332(this.collectPlayerEntries(), Text.method_43470(", "), this::method_1918);
                this.client.method_44713().method_37015(Text.method_43469("multiplayer.player.list.narration", new Object[]{mutabletext}));
            }
        }

        TabTweaksModule tabtweaksmodule = ClientMain.getInstance().getModuleManager().getModule(TabTweaksModule.class);
        boolean flag1 = tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.c().getValue();
        if (flag1 && this.tabAnimation.h() > 0.001F) {
            this.visible = true;
            this.shouldRenderForAnimation = !flag;
        } else {
            this.visible = flag;
            this.shouldRenderForAnimation = false;
        }
    }

    private List<PlayerListEntry> collectPlayerEntries() {
        return this.client.field_1724.field_3944.method_45732().stream().sorted(ENTRY_ORDERING).limit(80L).toList();
    }

    public void method_1919(DrawContext drawcontext, int i, Scoreboard scoreboard, @Nullable ScoreboardObjective scoreboardobjective) {
        TabTweaksModule tabtweaksmodule = ClientMain.getInstance().getModuleManager().getModule(TabTweaksModule.class);
        boolean flag = tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.c().getValue();
        float f = flag && tabtweaksmodule != null ? tabtweaksmodule.d().getFloatValue() : 6.0F;
        float f1 = flag && tabtweaksmodule != null ? 300.0F : 150.0F;
        if (this.tabAnimation == null) {
            this.tabAnimation = new ProgressAnimation();
            this.tabAnimation.a();
            this.tabAnimation.f(this.visible);
            if (this.visible) {
                this.tabAnimation.d(1.0F);
            }
        }

        this.tabAnimation.i(f);
        if (flag) {
            this.tabAnimation.b();
            if (this.tabAnimation.h() < 0.001F) {
                if (this.shouldRenderForAnimation) {
                    this.visible = false;
                    this.shouldRenderForAnimation = false;
                }

                return;
            }

            if (this.shouldRenderForAnimation && this.tabAnimation.h() > 0.001F) {
                this.visible = true;
            }
        } else {
            this.tabAnimation.d(this.visible ? 1.0F : 0.0F);
        }

        float f2 = this.tabAnimation.h();
        float f3 = EasingFunctions.a(f2);
        if (flag || this.visible) {
            drawcontext.method_51448().method_22903();
            if (flag) {
                float f4 = (1.0F - f3) * -f1;
                drawcontext.method_51448().method_46416(0.0F, f4, 0.0F);
            }

            List list1 = this.collectPlayerEntries();
            ArrayList arraylist = new ArrayList(list1.size());
            int j = this.client.field_1772.method_1727(" ");
            int k = 0;
            int l = 0;

            for (Object _ple : list1) {
                PlayerListEntry playerlistentry = (PlayerListEntry)_ple;
                Text text = this.method_1918(playerlistentry);
                k = Math.max(k, this.client.field_1772.method_27525(text));
                int i1 = 0;
                MutableText mutabletext = null;
                int j1 = 0;
                if (scoreboardobjective != null) {
                    ScoreHolder scoreholder = ScoreHolder.method_55420(playerlistentry.method_2966());
                    ReadableScoreboardScore readablescoreboardscore = scoreboard.method_55430(scoreholder, scoreboardobjective);
                    if (readablescoreboardscore != null) {
                        i1 = readablescoreboardscore.method_55397();
                    }

                    if (scoreboardobjective.method_1118() != RenderType.field_1471) {
                        NumberFormat numberformat = scoreboardobjective.method_55380(StyledNumberFormat.field_47568);
                        mutabletext = ReadableScoreboardScore.method_55398(readablescoreboardscore, numberformat);
                        j1 = this.client.field_1772.method_27525(mutabletext);
                        l = Math.max(l, j1 > 0 ? j + j1 : 0);
                    }
                }

                arraylist.add(new ScoreboardEntryRecord(text, i1, mutabletext, j1));
            }

            if (!this.hearts.isEmpty()) {
                Set set = list1.stream().map(playerlistentry3 -> ((PlayerListEntry)playerlistentry3).method_2966().getId()).collect(Collectors.toSet());
                this.hearts.keySet().removeIf(uuid -> !set.contains(uuid));
            }

            int i3 = list1.size();
            int j3 = i3;
            int k3;
            if (tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.f().getValue()) {
                k3 = tabtweaksmodule.g().getIntValue();
                j3 = (i3 + k3 - 1) / k3;
            } else {
                for (k3 = 1; j3 > 20; j3 = (i3 + k3 - 1) / k3) {
                    k3++;
                }
            }

            boolean flag4 = this.client.method_1542() || this.client.method_1562().method_48296().method_10771();
            boolean flag5 = tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.e().getValue();
            int l3 = 13;
            if (flag5) {
                for (Object _ple2 : list1) {
                    PlayerListEntry playerlistentry2 = (PlayerListEntry)_ple2;
                    int k4 = playerlistentry2.method_2959();
                    String s = k4 == 0 ? "?" : String.valueOf(k4);
                    l3 = Math.max(l3, this.client.field_1772.method_1727(s) + 4);
                }
            }

            int i4;
            if (scoreboardobjective != null) {
                if (scoreboardobjective.method_1118() == RenderType.field_1471) {
                    i4 = 90;
                } else {
                    i4 = l;
                }
            } else {
                i4 = 0;
            }

            int j4 = Math.min(k3 * ((flag4 ? 9 : 0) + k + i4 + l3), i - 50) / k3;
            int l4 = i / 2 - (j4 * k3 + (k3 - 1) * 5) / 2;
            int i5 = 10;
            int k1 = j4 * k3 + (k3 - 1) * 5;
            boolean flag1 = tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.b().getValue();
            List list = null;
            if (this.header != null && !flag1) {
                list = this.client.field_1772.method_1728(this.header, i - 50);

                for (Object _ot : list) {
                    OrderedText orderedtext = (OrderedText)_ot;
                    k1 = Math.max(k1, this.client.field_1772.method_30880(orderedtext));
                }
            }

            List list2 = null;
            if (this.footer != null && !flag1) {
                list2 = this.client.field_1772.method_1728(this.footer, i - 50);

                for (Object _ot1 : list2) {
                    OrderedText orderedtext1 = (OrderedText)_ot1;
                    k1 = Math.max(k1, this.client.field_1772.method_30880(orderedtext1));
                }
            }

            if (list != null) {
                drawcontext.method_25294(i / 2 - k1 / 2 - 1, i5 - 1, i / 2 + k1 / 2 + 1, i5 + list.size() * 9, Integer.MIN_VALUE);

                for (Object _ot2 : list) {
                    OrderedText orderedtext2 = (OrderedText)_ot2;
                    int l1 = this.client.field_1772.method_30880(orderedtext2);
                    drawcontext.method_35720(this.client.field_1772, orderedtext2, i / 2 - l1 / 2, i5, -1);
                    i5 += 9;
                }

                i5++;
            }

            drawcontext.method_25294(i / 2 - k1 / 2 - 1, i5 - 1, i / 2 + k1 / 2 + 1, i5 + j3 * 9, Integer.MIN_VALUE);
            int j5 = this.client.field_1690.method_19344(553648127);

            for (int k5 = 0; k5 < i3; k5++) {
                int l5 = k5 / j3;
                int i2 = k5 % j3;
                int j2 = l4 + l5 * j4 + l5 * 5;
                int k2 = i5 + i2 * 9;
                PlayerListEntry playerlistentry1 = k5 < list1.size() ? (PlayerListEntry)list1.get(k5) : null;
                boolean flag2 = playerlistentry1 != null && this.isFriend(playerlistentry1.method_2966().getName());
                int l2;
                if (flag2 && tabtweaksmodule != null && tabtweaksmodule.isEnabled()) {
                    l2 = new Color(0, 255, 234, 80).getRGB();
                } else {
                    l2 = j5;
                }

                drawcontext.method_25294(j2, k2, j2 + j4, k2 + 8, l2);
                if (k5 < list1.size()) {
                    ScoreboardEntryRecord ScoreboardEntryRecord = (ScoreboardEntryRecord)arraylist.get(k5);
                    GameProfile gameprofile = playerlistentry1.method_2966();
                    if (flag4) {
                        PlayerEntity playerentity = this.client.field_1687.method_18470(gameprofile.getId());
                        boolean flag3 = playerentity != null && LivingEntityRenderer.method_38563(playerentity);
                        PlayerSkinDrawer.method_44445(
                            drawcontext, playerlistentry1.method_52810().comp_1626(), j2, k2, 8, playerlistentry1.method_65195(), flag3, -1
                        );
                        j2 += 9;
                    }

                    drawcontext.method_27535(
                        this.client.field_1772, ScoreboardEntryRecord.name(), j2, k2, playerlistentry1.method_2958() == GameMode.field_9219 ? -1862270977 : -1
                    );
                    if (scoreboardobjective != null && playerlistentry1.method_2958() != GameMode.field_9219) {
                        int j6 = j2 + k + 1;
                        int k6 = j6 + i4;
                        if (k6 - j6 > 5) {
                            this.renderScoreboardObjective(scoreboardobjective, k2, ScoreboardEntryRecord, j6, k6, gameprofile.getId(), drawcontext);
                        }
                    }

                    this.method_1923(drawcontext, j4, j2 - (flag4 ? 9 : 0), k2, playerlistentry1);
                }
            }

            if (list2 != null) {
                i5 += j3 * 9 + 1;
                drawcontext.method_25294(i / 2 - k1 / 2 - 1, i5 - 1, i / 2 + k1 / 2 + 1, i5 + list2.size() * 9, Integer.MIN_VALUE);

                for (Object _ot3 : list2) {
                    OrderedText orderedtext3 = (OrderedText)_ot3;
                    int i6 = this.client.field_1772.method_30880(orderedtext3);
                    drawcontext.method_35720(this.client.field_1772, orderedtext3, i / 2 - i6 / 2, i5, -1);
                    i5 += 9;
                }
            }

            drawcontext.method_51448().method_22909();
        }
    }

    private boolean isFriend(String s) {
        FriendManager friendmanager = FriendManager.getInstance();
        return friendmanager != null && friendmanager.isFriend(s);
    }

    protected void method_1923(DrawContext drawcontext, int i, int j, int k, PlayerListEntry playerlistentry) {
        TabTweaksModule tabtweaksmodule = ClientMain.getInstance().getModuleManager().getModule(TabTweaksModule.class);
        boolean flag = tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.e().getValue();
        if (flag) {
            int j1 = playerlistentry.method_2959();
            String s = j1 == 0 ? "?" : String.valueOf(j1);
            int l;
            if (j1 < 0 || j1 == 0) {
                l = -5592406;
            } else if (j1 < 75) {
                l = -16711936;
            } else if (j1 < 150) {
                l = -11141291;
            } else if (j1 < 300) {
                l = -256;
            } else if (j1 < 600) {
                l = -43691;
            } else {
                l = -65536;
            }

            int i1 = this.client.field_1772.method_1727(s);
            drawcontext.method_51448().method_22903();
            drawcontext.method_51448().method_46416(0.0F, 0.0F, 100.0F);
            drawcontext.method_25303(this.client.field_1772, s, j + i - i1 - 1, k, l);
            drawcontext.method_51448().method_22909();
        } else {
            Identifier identifier;
            if (playerlistentry.method_2959() < 0) {
                identifier = PING_UNKNOWN_ICON_TEXTURE;
            } else if (playerlistentry.method_2959() < 150) {
                identifier = PING_5_ICON_TEXTURE;
            } else if (playerlistentry.method_2959() < 300) {
                identifier = PING_4_ICON_TEXTURE;
            } else if (playerlistentry.method_2959() < 600) {
                identifier = PING_3_ICON_TEXTURE;
            } else if (playerlistentry.method_2959() < 1000) {
                identifier = PING_2_ICON_TEXTURE;
            } else {
                identifier = PING_1_ICON_TEXTURE;
            }

            drawcontext.method_51448().method_22903();
            drawcontext.method_51448().method_46416(0.0F, 0.0F, 100.0F);
            drawcontext.method_52706(RenderLayer::method_62277, identifier, j + i - 11, k, 10, 8);
            drawcontext.method_51448().method_22909();
        }
    }

    private void renderScoreboardObjective(
        ScoreboardObjective scoreboardobjective, int i, ScoreboardEntryRecord ScoreboardEntryRecord, int j, int k, UUID uuid, DrawContext drawcontext
    ) {
        if (scoreboardobjective.method_1118() == RenderType.field_1471) {
            this.renderHearts(i, j, k, uuid, drawcontext, ScoreboardEntryRecord.score());
        } else if (ScoreboardEntryRecord.formattedScore() != null) {
            drawcontext.method_27535(this.client.field_1772, ScoreboardEntryRecord.formattedScore(), k - ScoreboardEntryRecord.scoreWidth(), i, 16777215);
        }
    }

    private void renderHearts(int i, int j, int k, UUID uuid, DrawContext drawcontext, int l) {
        ScoreboardScoreTracker ScoreboardScoreTracker = this.hearts.computeIfAbsent(uuid, uuid1 -> new ScoreboardScoreTracker(l));
        ScoreboardScoreTracker.tick(l, this.inGameHud.method_1738());
        int i1 = MathHelper.method_38788(Math.max(l, ScoreboardScoreTracker.getPrevScore()), 2);
        int j1 = Math.max(l, Math.max(ScoreboardScoreTracker.getPrevScore(), 20)) / 2;
        boolean flag = ScoreboardScoreTracker.useHighlighted(this.inGameHud.method_1738());
        if (i1 > 0) {
            int k1 = MathHelper.method_15375(Math.min((float)(k - j - 4) / j1, 9.0F));
            if (k1 <= 3) {
                float f1 = MathHelper.method_15363(l / 20.0F, 0.0F, 1.0F);
                int j2 = (int)((1.0F - f1) * 255.0F) << 16 | (int)(f1 * 255.0F) << 8;
                float f = l / 2.0F;
                MutableText mutabletext = Text.method_43469("multiplayer.player.list.hp", new Object[]{f});
                MutableText mutabletext1;
                if (k - this.client.field_1772.method_27525(mutabletext) >= j) {
                    mutabletext1 = mutabletext;
                } else {
                    mutabletext1 = Text.method_43470(Float.toString(f));
                }

                drawcontext.method_27535(this.client.field_1772, mutabletext1, (k + j - this.client.field_1772.method_27525(mutabletext1)) / 2, i, j2);
                return;
            }

            Identifier identifier = flag ? CONTAINER_HEART_BLINKING_TEXTURE : CONTAINER_HEART_TEXTURE;

            for (int l1 = i1; l1 < j1; l1++) {
                drawcontext.method_52706(RenderLayer::method_62277, identifier, j + l1 * k1, i, 9, 9);
            }

            for (int i2 = 0; i2 < i1; i2++) {
                drawcontext.method_52706(RenderLayer::method_62277, identifier, j + i2 * k1, i, 9, 9);
                if (flag) {
                    if (i2 * 2 + 1 < ScoreboardScoreTracker.getPrevScore()) {
                        drawcontext.method_52706(RenderLayer::method_62277, FULL_HEART_BLINKING_TEXTURE, j + i2 * k1, i, 9, 9);
                    }

                    if (i2 * 2 + 1 == ScoreboardScoreTracker.getPrevScore()) {
                        drawcontext.method_52706(RenderLayer::method_62277, HALF_HEART_BLINKING_TEXTURE, j + i2 * k1, i, 9, 9);
                    }
                }

                if (i2 * 2 + 1 < l) {
                    drawcontext.method_52706(
                        RenderLayer::method_62277, i2 >= 10 ? ABSORBING_FULL_HEART_BLINKING_TEXTURE : FULL_HEART_TEXTURE, j + i2 * k1, i, 9, 9
                    );
                }

                if (i2 * 2 + 1 == l) {
                    drawcontext.method_52706(
                        RenderLayer::method_62277, i2 >= 10 ? ABSORBING_HALF_HEART_BLINKING_TEXTURE : HALF_HEART_TEXTURE, j + i2 * k1, i, 9, 9
                    );
                }
            }
        }
    }

    public void method_1924(@Nullable Text text) {
        this.footer = text;
    }

    public void method_1925(@Nullable Text text) {
        this.header = text;
    }

    public void method_1920() {
        this.header = null;
        this.footer = null;
    }

    public boolean isVisible() {
        return this.visible;
    }
}
