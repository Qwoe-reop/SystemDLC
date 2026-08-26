package dev.mark.system.render;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import dev.mark.system.core.ClientMain;
import dev.mark.system.data.DeletableMessageSignature;
import dev.mark.system.module.render.ChatTweaksModule;
import dev.mark.system.net.ChatSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;
import net.minecraft.client.gui.hud.MessageIndicator.Icon;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Nullables;
import net.minecraft.util.collection.ArrayListDeque;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class CustomChatHud extends ChatHud {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_MESSAGES = 100;
    private static final int MISSING_MESSAGE_INDEX = -1;
    private static final int field_39772 = 4;
    private static final int field_39773 = 4;
    private static final int OFFSET_FROM_BOTTOM = 40;
    private static final int REMOVAL_QUEUE_TICKS = 60;
    private static final Text DELETED_MARKER_TEXT = Text.method_43471("chat.deleted_marker")
        .method_27695(new Formatting[]{Formatting.field_1080, Formatting.field_1056});
    private final MinecraftClient client;
    private final ArrayListDeque<String> messageHistory = new ArrayListDeque(100);
    private final List<ChatHudLine> messages = Lists.newArrayList();
    private final List<Visible> visibleMessages = Lists.newArrayList();
    private int scrolledLines;
    private boolean hasUnreadNewMessages;
    private final List<DeletableMessageSignature> removalQueue = new ArrayList<>();
    private final List<ChatMessageEntry> duplicateGroups = new ArrayList<>();

    public CustomChatHud(MinecraftClient minecraftclient) {
        super(minecraftclient);
        this.client = minecraftclient;
        this.messageHistory.addAll(minecraftclient.method_52700().method_52696());
    }

    public void method_45584() {
        if (!this.removalQueue.isEmpty()) {
            this.tickRemovalQueue();
        }
    }

    public void method_1805(DrawContext drawcontext, int i, int j, int k, boolean flag) {
        if (!this.isChatHidden()) {
            int l = this.method_1813();
            int i1 = this.visibleMessages.size();
            if (i1 > 0) {
                Profiler profiler = Profilers.method_64146();
                profiler.method_15396("chat");
                float f = (float)this.method_1814();
                int j1 = MathHelper.method_15386(this.method_1811() / f);
                int k1 = drawcontext.method_51443();
                drawcontext.method_51448().method_22903();
                drawcontext.method_51448().method_22905(f, f, 1.0F);
                drawcontext.method_51448().method_46416(4.0F, 0.0F, 0.0F);
                int l1 = MathHelper.method_15375((k1 - 40) / f);
                int i2 = this.getMessageIndex(this.toChatLineX(j), this.toChatLineY(k));
                double d0 = (Double)this.client.field_1690.method_42542().method_41753() * 0.9 + 0.1;
                double d1 = (Double)this.client.field_1690.method_42550().method_41753();
                double d2 = (Double)this.client.field_1690.method_42546().method_41753();
                int j2 = this.getLineHeight();
                int k2 = (int)Math.round(-8.0 * (d2 + 1.0) + 4.0 * d2);
                int l2 = 0;

                for (int i4 = 0; i4 + this.scrolledLines < this.visibleMessages.size() && i4 < l; i4++) {
                    int j4 = i4 + this.scrolledLines;
                    Visible visible = this.visibleMessages.get(j4);
                    if (visible != null) {
                        int i3 = i - visible.comp_895();
                        if (i3 < 200 || flag) {
                            double d3 = flag ? 1.0 : getMessageOpacityMultiplier(i3);
                            int j3 = (int)(255.0 * d3 * d0);
                            int k3 = (int)(255.0 * d3 * d1);
                            l2++;
                            if (j3 > 3) {
                                int l3 = l1 - i4 * j2;
                                int l4 = l3 + k2;
                                drawcontext.method_25294(-4, l3 - j2, 0 + j1 + 4 + 4, l3, k3 << 24);
                                MessageIndicator messageindicator = visible.comp_897();
                                if (messageindicator != null) {
                                    int i5 = messageindicator.comp_899() | j3 << 24;
                                    drawcontext.method_25294(-4, l3 - j2, -2, l3, i5);
                                    if (j4 == i2 && messageindicator.comp_900() != null) {
                                        int j5 = this.getIndicatorX(visible);
                                        Objects.requireNonNull(this.client.field_1772);
                                        int k5 = l4 + 9;
                                        this.drawIndicatorIcon(drawcontext, j5, k5, messageindicator.comp_900());
                                    }
                                }

                                drawcontext.method_51448().method_22903();
                                drawcontext.method_51448().method_46416(0.0F, 0.0F, 50.0F);
                                drawcontext.method_35720(this.client.field_1772, visible.comp_896(), 0, l4, ColorHelper.method_61330(j3, -1));
                                drawcontext.method_51448().method_22909();
                            }
                        }
                    }
                }

                long i7 = this.client.method_44714().method_44944();
                if (i7 > 0L) {
                    int j7 = (int)(128.0 * d0);
                    int l5 = (int)(255.0 * d1);
                    drawcontext.method_51448().method_22903();
                    drawcontext.method_51448().method_46416(0.0F, l1, 0.0F);
                    drawcontext.method_25294(-2, 0, j1 + 4, 9, l5 << 24);
                    drawcontext.method_51448().method_46416(0.0F, 0.0F, 50.0F);
                    drawcontext.method_27535(this.client.field_1772, Text.method_43469("chat.queue", new Object[]{i7}), 0, 1, 16777215 + (j7 << 24));
                    drawcontext.method_51448().method_22909();
                }

                if (flag) {
                    int k7 = this.getLineHeight();
                    int i6 = i1 * k7;
                    int l7 = l2 * k7;
                    int k4 = this.scrolledLines * l7 / i1 - l1;
                    int j6 = l7 * l7 / i6;
                    if (i6 != l7) {
                        int k6 = k4 > 0 ? 170 : 96;
                        int i8 = this.hasUnreadNewMessages ? 13382451 : 3355562;
                        int l6 = j1 + 4;
                        drawcontext.method_51737(l6, -k4, l6 + 2, -k4 - j6, 100, i8 + (k6 << 24));
                        drawcontext.method_51737(l6 + 2, -k4, l6 + 1, -k4 - j6, 100, 13421772 + (k6 << 24));
                    }
                }

                drawcontext.method_51448().method_22909();
                profiler.method_15407();
            }
        }
    }

    private void drawIndicatorIcon(DrawContext drawcontext, int i, int j, Icon icon) {
        int k = j - icon.field_39767 - 1;
        icon.method_44712(drawcontext, i, k);
    }

    private int getIndicatorX(Visible visible) {
        return this.client.field_1772.method_30880(visible.comp_896()) + 4;
    }

    private boolean isChatHidden() {
        return this.client.field_1690.method_42539().method_41753() == ChatVisibility.field_7536;
    }

    private static double getMessageOpacityMultiplier(int i) {
        double d0 = i / 200.0;
        d0 = 1.0 - d0;
        d0 *= 10.0;
        d0 = MathHelper.method_15350(d0, 0.0, 1.0);
        return d0 * d0;
    }

    public void method_1808(boolean flag) {
        ChatTweaksModule chattweaksmodule = ClientMain.getInstance().getModuleManager().getModule(ChatTweaksModule.class);
        if (chattweaksmodule != null && chattweaksmodule.isEnabled() && chattweaksmodule.b().getValue()) {
            this.client.method_44714().method_44945();
        } else {
            this.client.method_44714().method_44945();
            this.removalQueue.clear();
            this.visibleMessages.clear();
            this.messages.clear();
            this.duplicateGroups.clear();
            if (flag) {
                this.messageHistory.clear();
                this.messageHistory.addAll(this.client.method_52700().method_52696());
            }
        }
    }

    public void method_1812(Text text) {
        this.method_44811(text, (MessageSignatureData)null, this.client.method_47392() ? MessageIndicator.method_47391() : MessageIndicator.method_44751());
    }

    public void method_44811(Text text, @Nullable MessageSignatureData messagesignaturedata, @Nullable MessageIndicator messageindicator) {
        ChatHudLine chathudline = new ChatHudLine(this.client.field_1705.method_1738(), text, messagesignaturedata, messageindicator);
        this.logChatMessage(chathudline);
        this.addVisibleMessage(chathudline);
        this.addMessage(chathudline);
    }

    private void logChatMessage(ChatHudLine chathudline) {
        MessageIndicator messageindicator = chathudline.comp_894();
        if (messageindicator == null || messageindicator != MessageIndicator.method_44751()) {
            String s = chathudline.comp_893().getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
            String s1 = (String)Nullables.method_49077(messageindicator, MessageIndicator::comp_902);
            if (s1 != null) {
                LOGGER.info("[{}] [CHAT] {}", s1, s);
            } else {
                LOGGER.info("[CHAT] {}", s);
            }
        }
    }

    private void addVisibleMessage(ChatHudLine chathudline) {
        ChatTweaksModule chattweaksmodule = ClientMain.getInstance().getModuleManager().getModule(ChatTweaksModule.class);
        boolean flag = chattweaksmodule != null && chattweaksmodule.isEnabled() && chattweaksmodule.a().getValue();
        int i = MathHelper.method_15357(this.method_1811() / this.method_1814());
        Icon icon = chathudline.method_58742();
        if (icon != null) {
            i -= icon.field_39766 + 4 + 2;
        }

        List list = ChatMessages.method_1850(chathudline.comp_893(), i, this.client.field_1772);
        boolean flag1 = this.method_1819();

        for (int j = 0; j < list.size(); j++) {
            OrderedText orderedtext = (OrderedText)list.get(j);
            if (flag1 && this.scrolledLines > 0) {
                this.hasUnreadNewMessages = true;
                this.method_1802(1);
            }

            boolean flag2 = j == list.size() - 1;
            this.visibleMessages.add(0, new Visible(chathudline.comp_892(), orderedtext, chathudline.comp_894(), flag2));
        }

        if (!flag) {
            while (this.visibleMessages.size() > 100) {
                this.visibleMessages.remove(this.visibleMessages.size() - 1);
            }
        }
    }

    private void addMessage(ChatHudLine chathudline) {
        ChatTweaksModule chattweaksmodule = ClientMain.getInstance().getModuleManager().getModule(ChatTweaksModule.class);
        boolean flag = chattweaksmodule != null && chattweaksmodule.isEnabled() && chattweaksmodule.a().getValue();
        boolean flag1 = chattweaksmodule != null && chattweaksmodule.isEnabled() && chattweaksmodule.d().getValue();
        if (flag1) {
            String s = chathudline.comp_893().getString();

            for (int i = 0; i < this.duplicateGroups.size(); i++) {
                ChatMessageEntry chatmessageentry = this.duplicateGroups.get(i);
                if (chatmessageentry.getMessageContent().equals(s) && i < 5) {
                    this.messages.removeIf(chathudline2 -> chathudline2.comp_892() == chatmessageentry.getCreationTick());
                    chatmessageentry.increment();
                    chatmessageentry.updateTick(chathudline.comp_892());
                    int j = chatmessageentry.getCount();
                    MutableText mutabletext = Text.method_43470(s + " §7(DropdownSelector" + j + ")");
                    ChatHudLine chathudline1 = new ChatHudLine(chathudline.comp_892(), mutabletext, chathudline.comp_915(), chathudline.comp_894());
                    this.messages.add(0, chathudline1);
                    this.refresh();
                    return;
                }
            }

            this.duplicateGroups.add(0, new ChatMessageEntry(s, chathudline.comp_892(), chathudline.comp_915(), chathudline.comp_894()));

            while (this.duplicateGroups.size() > 100) {
                this.duplicateGroups.remove(this.duplicateGroups.size() - 1);
            }
        }

        this.messages.add(0, chathudline);
        if (!flag) {
            while (this.messages.size() > 100) {
                this.messages.remove(this.messages.size() - 1);
            }
        }
    }

    private void tickRemovalQueue() {
        int i = this.client.field_1705.method_1738();
        this.removalQueue
            .removeIf(
                DeletableMessageSignature -> i >= DeletableMessageSignature.deletableAfter()
                    ? this.queueForRemoval(DeletableMessageSignature.signature()) == null
                    : false
            );
    }

    public void method_44812(MessageSignatureData messagesignaturedata) {
        DeletableMessageSignature DeletableMessageSignature = this.queueForRemoval(messagesignaturedata);
        if (DeletableMessageSignature != null) {
            this.removalQueue.add(DeletableMessageSignature);
        }
    }

    @Nullable
    private DeletableMessageSignature queueForRemoval(MessageSignatureData messagesignaturedata) {
        int i = this.client.field_1705.method_1738();
        ListIterator listiterator = this.messages.listIterator();

        while (listiterator.hasNext()) {
            ChatHudLine chathudline = (ChatHudLine)listiterator.next();
            if (messagesignaturedata.equals(chathudline.comp_915())) {
                int j = chathudline.comp_892() + 60;
                if (i >= j) {
                    listiterator.set(this.createRemovalMarker(chathudline));
                    this.refresh();
                    return null;
                }

                return new DeletableMessageSignature(messagesignaturedata, j);
            }
        }

        return null;
    }

    private ChatHudLine createRemovalMarker(ChatHudLine chathudline) {
        return new ChatHudLine(chathudline.comp_892(), DELETED_MARKER_TEXT, (MessageSignatureData)null, MessageIndicator.method_44751());
    }

    public void method_1817() {
        this.method_1820();
        this.refresh();
    }

    private void refresh() {
        this.visibleMessages.clear();

        for (ChatHudLine chathudline : Lists.reverse(this.messages)) {
            this.addVisibleMessage(chathudline);
        }
    }

    public ArrayListDeque<String> method_1809() {
        return this.messageHistory;
    }

    public void method_1803(String s) {
        ChatTweaksModule chattweaksmodule = ClientMain.getInstance().getModuleManager().getModule(ChatTweaksModule.class);
        boolean flag = chattweaksmodule != null && chattweaksmodule.isEnabled() && chattweaksmodule.a().getValue();
        if (!s.equals(this.messageHistory.peekLast())) {
            if (!flag && this.messageHistory.size() >= 100) {
                this.messageHistory.removeFirst();
            }

            this.messageHistory.addLast(s);
        }

        if (s.startsWith("/")) {
            this.client.method_52700().method_52697(s);
        }
    }

    public void method_1820() {
        this.scrolledLines = 0;
        this.hasUnreadNewMessages = false;
    }

    public void method_1802(int i) {
        this.scrolledLines += i;
        int j = this.visibleMessages.size();
        if (this.scrolledLines > j - this.method_1813()) {
            this.scrolledLines = j - this.method_1813();
        }

        if (this.scrolledLines <= 0) {
            this.scrolledLines = 0;
            this.hasUnreadNewMessages = false;
        }
    }

    public boolean method_27146(double d0, double d1) {
        if (this.method_1819() && !this.client.field_1690.field_1842 && !this.isChatHidden()) {
            MessageHandler messagehandler = this.client.method_44714();
            if (messagehandler.method_44944() == 0L) {
                return false;
            } else {
                double d2 = d0 - 2.0;
                double d3 = this.client.method_22683().method_4502() - d1 - 40.0;
                if (d2 <= MathHelper.method_15357(this.method_1811() / this.method_1814())
                    && d3 < 0.0
                    && d3 > MathHelper.method_15357(-9.0 * this.method_1814())) {
                    messagehandler.method_44769();
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Nullable
    public Text getFullMessageAt(double d0, double d1) {
        double d2 = this.toChatLineX(d0);
        double d3 = this.toChatLineY(d1);
        int i = this.getMessageIndex(d2, d3);
        if (i >= 0 && i < this.visibleMessages.size()) {
            Visible visible = this.visibleMessages.get(i);
            int j = visible.comp_895();

            for (ChatHudLine chathudline : this.messages) {
                if (chathudline.comp_892() == j) {
                    return chathudline.comp_893();
                }
            }

            return null;
        } else {
            return null;
        }
    }

    @Nullable
    public Style method_1816(double d0, double d1) {
        double d2 = this.toChatLineX(d0);
        double d3 = this.toChatLineY(d1);
        int i = this.getMessageLineIndex(d2, d3);
        if (i >= 0 && i < this.visibleMessages.size()) {
            Visible visible = this.visibleMessages.get(i);
            return this.client.field_1772.method_27527().method_30876(visible.comp_896(), MathHelper.method_15357(d2));
        } else {
            return null;
        }
    }

    @Nullable
    public MessageIndicator method_44723(double d0, double d1) {
        double d2 = this.toChatLineX(d0);
        double d3 = this.toChatLineY(d1);
        int i = this.getMessageIndex(d2, d3);
        if (i >= 0 && i < this.visibleMessages.size()) {
            Visible visible = this.visibleMessages.get(i);
            MessageIndicator messageindicator = visible.comp_897();
            if (messageindicator != null && this.isXInsideIndicatorIcon(d2, visible, messageindicator)) {
                return messageindicator;
            }
        }

        return null;
    }

    private boolean isXInsideIndicatorIcon(double d0, Visible visible, MessageIndicator messageindicator) {
        if (d0 < 0.0) {
            return true;
        }

        Icon icon = messageindicator.comp_900();
        if (icon == null) {
            return false;
        }

        int i = this.getIndicatorX(visible);
        int j = i + icon.field_39766;
        return d0 >= i && d0 <= j;
    }

    private double toChatLineX(double d0) {
        return d0 / this.method_1814() - 4.0;
    }

    private double toChatLineY(double d0) {
        double d1 = this.client.method_22683().method_4502() - d0 - 40.0;
        return d1 / (this.method_1814() * this.getLineHeight());
    }

    private int getMessageIndex(double d0, double d1) {
        int i = this.getMessageLineIndex(d0, d1);
        if (i == -1) {
            return -1;
        }

        while (i >= 0) {
            if (this.visibleMessages.get(i).comp_898()) {
                return i;
            }

            i--;
        }

        return i;
    }

    private int getMessageLineIndex(double d0, double d1) {
        if (this.method_1819() && !this.isChatHidden()) {
            if (!(d0 < -4.0) && !(d0 > MathHelper.method_15357(this.method_1811() / this.method_1814()))) {
                int i = Math.min(this.method_1813(), this.visibleMessages.size());
                if (d1 >= 0.0 && d1 < i) {
                    int j = MathHelper.method_15357(d1 + this.scrolledLines);
                    if (j >= 0 && j < this.visibleMessages.size()) {
                        return j;
                    }
                }

                return -1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public boolean method_1819() {
        return this.client.field_1755 instanceof ChatScreen;
    }

    public int method_1811() {
        return getWidth((Double)this.client.field_1690.method_42556().method_41753());
    }

    public int method_1810() {
        return getHeight(
            this.method_1819() ? (Double)this.client.field_1690.method_41803().method_41753() : (Double)this.client.field_1690.method_41801().method_41753()
        );
    }

    public double method_1814() {
        return (Double)this.client.field_1690.method_42554().method_41753();
    }

    public static int getWidth(double d0) {
        return MathHelper.method_15357(d0 * 280.0 + 40.0);
    }

    public static int getHeight(double d0) {
        return MathHelper.method_15357(d0 * 160.0 + 20.0);
    }

    public static double getDefaultUnfocusedHeight() {
        return 70.0 / (getHeight(1.0) - 20);
    }

    public int method_1813() {
        return this.method_1810() / this.getLineHeight();
    }

    private int getLineHeight() {
        return (int)(9.0 * ((Double)this.client.field_1690.method_42546().method_41753() + 1.0));
    }

    public void restoreChatState(ChatSnapshot ChatSnapshot) {
        this.messageHistory.clear();
        this.messageHistory.addAll(ChatSnapshot.messageHistory);
        this.removalQueue.clear();
        this.removalQueue.addAll(ChatSnapshot.removalQueue);
        this.messages.clear();
        this.messages.addAll(ChatSnapshot.messages);
        this.refresh();
    }
}
