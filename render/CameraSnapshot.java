package dev.mark.system.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

public class CameraSnapshot {
    public final Vec3d VS;
    public final float AZ;
    public final float Sd;
    public final double avF;
    public final double ajW;
    public final double auw;
    public final double ayT;

    public CameraSnapshot(Camera camera, float f) {
        this.VS = camera.method_19326();
        this.AZ = camera.method_19329();
        this.Sd = camera.method_19330();
        float f1 = (float)Math.cos(Math.toRadians(this.AZ));
        float f2 = (float)Math.sin(Math.toRadians(this.AZ));
        float f3 = (float)Math.cos(Math.toRadians(this.Sd + 90.0F));
        float f4 = (float)Math.sin(Math.toRadians(this.Sd + 90.0F));
        this.avF = f1 * f3;
        this.ajW = -f2;
        this.auw = f1 * f4;
        this.ayT = Math.cos(Math.toRadians(f / 2.0 + 20.0));
    }

    public static CameraSnapshot a(MinecraftClient minecraftclient) {
        Camera camera = minecraftclient.method_1561().field_4686;
        float f = ((Integer)minecraftclient.field_1690.method_41808().method_41753()).intValue();
        return new CameraSnapshot(camera, f);
    }
}
