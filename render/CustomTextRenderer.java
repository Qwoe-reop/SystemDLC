package dev.mark.system.render;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import dev.mark.system.core.ClientMain;
import dev.mark.system.core.FriendManager;
import dev.mark.system.module.render.ProtestModule;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class CustomTextRenderer extends TextRenderer {
    private final Function<Identifier, FontStorage> fontStorageAccessor;
    final boolean validateAdvance;
    private final TextHandler handler;

    public CustomTextRenderer(Function<Identifier, FontStorage> function, boolean flag) {
        super(function, flag);
        this.fontStorageAccessor = function;
        this.validateAdvance = flag;
        this.handler = new TextHandler(
            (i, style) -> this.method_27526(style.method_27708()).method_2011(i, this.validateAdvance).method_16798(style.method_10984())
        );
    }

    FontStorage method_27526(Identifier identifier) {
        return this.fontStorageAccessor.apply(identifier);
    }

    public String method_1721(String s) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(s), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        } catch (ArabicShapingException arabicshapingexception) {
            return s;
        }
    }

    private String replacePlayerName(String s) {
        MinecraftClient minecraftclient = MinecraftClient.method_1551();
        if (minecraftclient.field_1724 == null) {
            return s;
        }

        ProtestModule protestmodule = ClientMain.getInstance().getModuleManager().getModule(ProtestModule.class);
        if (protestmodule != null && protestmodule.isEnabled()) {
            String s1 = minecraftclient.field_1724.method_7334().getName();
            if (s.contains(s1)) {
                s = s.replace(s1, "SуstemPlayer");
            }

            if (protestmodule.e().getValue()) {
                for (String s2 : FriendManager.getInstance().getFriends()) {
                    String s3 = Pattern.quote(s2);
                    s = s.replaceAll("(?i)" + s3, "SуstemFriend");
                }
            }

            return s;
        } else {
            return s;
        }
    }

    private OrderedText replacePlayerNameInOrderedText(OrderedText orderedtext) {
        MinecraftClient minecraftclient = MinecraftClient.method_1551();
        if (minecraftclient.field_1724 == null) {
            return orderedtext;
        }

        ProtestModule protestmodule = ClientMain.getInstance().getModuleManager().getModule(ProtestModule.class);
        if (protestmodule != null && protestmodule.isEnabled()) {
            StringBuilder stringbuilder = new StringBuilder();
            orderedtext.accept((j, style, i) -> {
                stringbuilder.appendCodePoint(i);
                return true;
            });
            String s = stringbuilder.toString();
            String s1 = minecraftclient.field_1724.method_7334().getName();
            if (s.contains(s1)) {
                s = s.replace(s1, "SуstemPlayer");
            }

            if (protestmodule.e().getValue()) {
                for (String s2 : FriendManager.getInstance().getFriends()) {
                    String s3 = Pattern.quote(s2);
                    s = s.replaceAll("(?i)" + s3, "SуstemFriend");
                }
            }

            return !s.equals(stringbuilder.toString()) ? OrderedText.method_30747(s, Style.field_24360) : orderedtext;
        } else {
            return orderedtext;
        }
    }

    public int method_27521(
        String s,
        float f,
        float f1,
        int i,
        boolean flag,
        Matrix4f matrix4f,
        VertexConsumerProvider vertexconsumerprovider,
        TextLayerType textlayertype,
        int j,
        int k
    ) {
        s = this.replacePlayerName(s);
        if (this.method_1726()) {
            s = this.method_1721(s);
        }

        return this.drawInternal(s, f, f1, i, flag, matrix4f, vertexconsumerprovider, textlayertype, j, k, true);
    }

    public int method_27522(
        Text text,
        float f,
        float f1,
        int i,
        boolean flag,
        Matrix4f matrix4f,
        VertexConsumerProvider vertexconsumerprovider,
        TextLayerType textlayertype,
        int j,
        int k
    ) {
        return this.method_30882(text, f, f1, i, flag, matrix4f, vertexconsumerprovider, textlayertype, j, k, true);
    }

    public int method_30882(
        Text text,
        float f,
        float f1,
        int i,
        boolean flag,
        Matrix4f matrix4f,
        VertexConsumerProvider vertexconsumerprovider,
        TextLayerType textlayertype,
        int j,
        int k,
        boolean flag1
    ) {
        OrderedText orderedtext = this.replacePlayerNameInOrderedText(text.method_30937());
        return this.drawInternal(orderedtext, f, f1, i, flag, matrix4f, vertexconsumerprovider, textlayertype, j, k, flag1);
    }

    public int method_22942(
        OrderedText orderedtext,
        float f,
        float f1,
        int i,
        boolean flag,
        Matrix4f matrix4f,
        VertexConsumerProvider vertexconsumerprovider,
        TextLayerType textlayertype,
        int j,
        int k
    ) {
        orderedtext = this.replacePlayerNameInOrderedText(orderedtext);
        return this.drawInternal(orderedtext, f, f1, i, flag, matrix4f, vertexconsumerprovider, textlayertype, j, k, true);
    }

    public void method_37296(OrderedText orderedtext, float f, float f1, int i, int j, Matrix4f matrix4f, VertexConsumerProvider vertexconsumerprovider, int k) {
        orderedtext = this.replacePlayerNameInOrderedText(orderedtext);
        int l = tweakTransparency(j);
        CustomCharacterVisitor fzx = new CustomCharacterVisitor(this, vertexconsumerprovider, 0.0F, 0.0F, l, false, matrix4f, TextLayerType.field_33993, k);

        for (int i1 = -1; i1 <= 1; i1++) {
            for (int j1 = -1; j1 <= 1; j1++) {
                if (i1 != 0 || j1 != 0) {
                    float[] afloat = new float[]{f};
                    int k1 = i1;
                    int l1 = j1;
                    orderedtext.accept((l2, style, i3) -> {
                        boolean flag = style.method_10984();
                        FontStorage fontstorage = this.method_27526(style.method_27708());
                        Glyph glyph = fontstorage.method_2011(i3, this.validateAdvance);
                        fzx.x = afloat[0] + k1 * glyph.method_16800();
                        fzx.y = f1 + l1 * glyph.method_16800();
                        afloat[0] += glyph.method_16798(flag);
                        return fzx.accept(l2, style.method_36139(l), i3);
                    });
                }
            }
        }

        fzx.drawGlyphs();
        CustomCharacterVisitor fzx2 = new CustomCharacterVisitor(
            this, vertexconsumerprovider, f, f1, tweakTransparency(i), false, matrix4f, TextLayerType.field_33995, k
        );
        orderedtext.accept(fzx2);
        fzx2.drawLayer(f);
    }

    private static int tweakTransparency(int i) {
        return (i & -67108864) == 0 ? ColorHelper.method_61334(i) : i;
    }

    private int drawInternal(
        String s,
        float f,
        float f1,
        int i,
        boolean flag,
        Matrix4f matrix4f,
        VertexConsumerProvider vertexconsumerprovider,
        TextLayerType textlayertype,
        int j,
        int k,
        boolean flag1
    ) {
        i = tweakTransparency(i);
        f = this.drawLayer(s, f, f1, i, flag, matrix4f, vertexconsumerprovider, textlayertype, j, k, flag1);
        return (int)f + (flag ? 1 : 0);
    }

    private int drawInternal(
        OrderedText orderedtext,
        float f,
        float f1,
        int i,
        boolean flag,
        Matrix4f matrix4f,
        VertexConsumerProvider vertexconsumerprovider,
        TextLayerType textlayertype,
        int j,
        int k,
        boolean flag1
    ) {
        i = tweakTransparency(i);
        f = this.drawLayer(orderedtext, f, f1, i, flag, matrix4f, vertexconsumerprovider, textlayertype, j, k, flag1);
        return (int)f + (flag ? 1 : 0);
    }

    private float drawLayer(
        String s,
        float f,
        float f1,
        int i,
        boolean flag,
        Matrix4f matrix4f,
        VertexConsumerProvider vertexconsumerprovider,
        TextLayerType textlayertype,
        int j,
        int k,
        boolean flag1
    ) {
        CustomCharacterVisitor CustomCharacterVisitor = new CustomCharacterVisitor(
            this, vertexconsumerprovider, f, f1, i, j, flag, matrix4f, textlayertype, k, flag1
        );
        TextVisitFactory.method_27479(s, Style.field_24360, CustomCharacterVisitor);
        return CustomCharacterVisitor.drawLayer(f);
    }

    private float drawLayer(
        OrderedText orderedtext,
        float f,
        float f1,
        int i,
        boolean flag,
        Matrix4f matrix4f,
        VertexConsumerProvider vertexconsumerprovider,
        TextLayerType textlayertype,
        int j,
        int k,
        boolean flag1
    ) {
        CustomCharacterVisitor CustomCharacterVisitor = new CustomCharacterVisitor(
            this, vertexconsumerprovider, f, f1, i, j, flag, matrix4f, textlayertype, k, flag1
        );
        orderedtext.accept(CustomCharacterVisitor);
        return CustomCharacterVisitor.drawLayer(f);
    }

    public int method_1727(String s) {
        return MathHelper.method_15386(this.handler.method_27482(s));
    }

    public int method_27525(StringVisitable stringvisitable) {
        return MathHelper.method_15386(this.handler.method_27488(stringvisitable));
    }

    public int method_30880(OrderedText orderedtext) {
        return MathHelper.method_15386(this.handler.method_30875(orderedtext));
    }

    public String method_27524(String s, int i, boolean flag) {
        return flag ? this.handler.method_27497(s, i, Style.field_24360) : this.handler.method_27494(s, i, Style.field_24360);
    }

    public String method_27523(String s, int i) {
        return this.handler.method_27494(s, i, Style.field_24360);
    }

    public StringVisitable method_1714(StringVisitable stringvisitable, int i) {
        return this.handler.method_27490(stringvisitable, i, Style.field_24360);
    }

    public int method_1713(String s, int i) {
        return 9 * this.handler.method_27498(s, i, Style.field_24360).size();
    }

    public int method_44378(StringVisitable stringvisitable, int i) {
        return 9 * this.handler.method_27495(stringvisitable, i, Style.field_24360).size();
    }

    public List<OrderedText> method_1728(StringVisitable stringvisitable, int i) {
        return Language.method_10517().method_30933(this.handler.method_27495(stringvisitable, i, Style.field_24360));
    }

    public boolean method_1726() {
        return Language.method_10517().method_29428();
    }

    public TextHandler method_27527() {
        return this.handler;
    }
}
