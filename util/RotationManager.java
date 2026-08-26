package dev.mark.system.util;

import dev.mark.system.data.Angle;
import dev.mark.system.event.RotationEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class RotationManager {
    public static final RotationManager INSTANCE = new RotationManager();
    private final MinecraftClient ajD = MinecraftClient.method_1551();
    private Angle BU = null;
    private Angle adY = null;
    private Angle Si = Angle.ZERO;
    private boolean LH = false;
    private boolean kX = false;
    private int aaM = 0;
    private static final int BE = 5;

    private RotationManager() {
    }

    public void a(Angle angle) {
        if (angle == null) {
            if (this.BU != null) {
                this.kX = true;
                this.aaM = 0;
            }

            this.adY = this.BU != null ? this.BU : RotationUtil.getPlayerRotation();
        } else {
            this.adY = this.BU;
            this.LH = true;
            this.kX = false;
        }

        this.BU = angle;
    }

    public Angle b() {
        return this.BU != null ? this.BU : RotationUtil.getPlayerRotation();
    }

    public boolean c() {
        return this.LH || this.kX;
    }

    public void d(RotationEvent rotationevent) {
        if (this.kX) {
            if (this.ajD.field_1724 == null) {
                this.i();
            } else {
                this.aaM++;
                if (this.aaM >= 5) {
                    this.BU = null;
                    this.LH = false;
                    this.kX = false;
                    this.aaM = 0;
                } else {
                    Angle angle = new Angle(this.ajD.field_1724.method_36454(), this.ajD.field_1724.method_36455());
                    float f = this.aaM / 5.0F;
                    float f1 = MathHelper.method_16439(f, this.BU.getYaw(), angle.getYaw());
                    float f2 = MathHelper.method_16439(f, this.BU.getPitch(), angle.getPitch());
                    rotationevent.setPitch(f2);
                    rotationevent.setYaw(f1);
                    rotationevent.setStrictMoveCorrection(true);
                }
            }
        } else if (this.LH && this.BU != null) {
            rotationevent.setPitch(this.BU.getPitch());
            rotationevent.setYaw(this.BU.getYaw());
            rotationevent.setStrictMoveCorrection(true);
        }
    }

    public void e(RotationEvent rotationevent, String s) {
        if (!s.equals("Нет") && this.LH && this.ajD.field_1724 != null) {
            float f = rotationevent.getYaw();
            float f1 = rotationevent.getStaticYaw();
            float f2 = this.f(f, f1);
            float f3 = this.ajD.field_1724.field_3913.field_3905;
            float f4 = this.ajD.field_1724.field_3913.field_3907;
            if (f3 != 0.0F || f4 != 0.0F) {
                double d0 = Math.toRadians(f2);
                double d1 = Math.cos(d0);
                double d2 = Math.sin(d0);
                float f5 = (float)(f3 * d1 - f4 * d2);
                float f6 = (float)(f3 * d2 + f4 * d1);
                this.ajD.field_1724.field_3913.field_3905 = f5;
                this.ajD.field_1724.field_3913.field_3907 = f6;
            }
        }
    }

    private float f(float f, float f1) {
        float f2 = f - f1;

        while (f2 > 180.0F) {
            f2 -= 360.0F;
        }

        while (f2 < -180.0F) {
            f2 += 360.0F;
        }

        return f2;
    }

    public static double g(Angle angle, Angle angle1) {
        return Math.hypot(Math.abs(h(angle.getYaw(), angle1.getYaw())), Math.abs(angle.getPitch() - angle1.getPitch()));
    }

    public static float h(float f, float f1) {
        return MathHelper.method_15393(f - f1);
    }

    public void i() {
        this.BU = null;
        this.adY = null;
        this.LH = false;
        this.kX = false;
        this.aaM = 0;
    }

    public Angle j() {
        return this.Si;
    }

    public void k(Angle angle) {
        this.Si = angle;
    }
}
