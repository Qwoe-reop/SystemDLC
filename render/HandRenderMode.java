package dev.mark.system.render;

import com.google.common.annotations.VisibleForTesting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
@VisibleForTesting
public enum HandRenderMode {
    RENDER_BOTH_HANDS(true, true),
    RENDER_MAIN_HAND_ONLY(true, false),
    RENDER_OFF_HAND_ONLY(false, true);

    final boolean renderMainHand;
    final boolean renderOffHand;

    HandRenderMode(boolean flag, boolean flag1) {
        this.renderMainHand = flag;
        this.renderOffHand = flag1;
    }

    public static HandRenderMode shouldOnlyRender(Hand hand) {
        return hand == Hand.field_5808 ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
    }

    private static HandRenderMode[] $values() {
        return new HandRenderMode[]{RENDER_BOTH_HANDS, RENDER_MAIN_HAND_ONLY, RENDER_OFF_HAND_ONLY};
    }
}
