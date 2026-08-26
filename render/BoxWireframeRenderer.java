package dev.mark.system.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class BoxWireframeRenderer {
    private final BuiltLine3d aqc;
    private final LineSegmentBuffer JA = new LineSegmentBuffer();

    public BoxWireframeRenderer(BuiltLine3d builtline3d) {
        this.aqc = builtline3d;
    }

    public void a(Vec3d vec3d, float f, float f1) {
        float f2 = f / 2.0F;
        this.JA.a(vec3d.method_1031(-f2, 0.0, -f2), vec3d.method_1031(-f2, 0.0, f2));
        this.JA.a(vec3d.method_1031(-f2, 0.0, f2), vec3d.method_1031(f2, 0.0, f2));
        this.JA.a(vec3d.method_1031(f2, 0.0, f2), vec3d.method_1031(f2, 0.0, -f2));
        this.JA.a(vec3d.method_1031(f2, 0.0, -f2), vec3d.method_1031(-f2, 0.0, -f2));
        this.JA.a(vec3d.method_1031(-f2, f1, -f2), vec3d.method_1031(-f2, f1, f2));
        this.JA.a(vec3d.method_1031(-f2, f1, f2), vec3d.method_1031(f2, f1, f2));
        this.JA.a(vec3d.method_1031(f2, f1, f2), vec3d.method_1031(f2, f1, -f2));
        this.JA.a(vec3d.method_1031(f2, f1, -f2), vec3d.method_1031(-f2, f1, -f2));
        this.JA.a(vec3d.method_1031(-f2, 0.0, -f2), vec3d.method_1031(-f2, f1, -f2));
        this.JA.a(vec3d.method_1031(-f2, 0.0, f2), vec3d.method_1031(-f2, f1, f2));
        this.JA.a(vec3d.method_1031(f2, 0.0, f2), vec3d.method_1031(f2, f1, f2));
        this.JA.a(vec3d.method_1031(f2, 0.0, -f2), vec3d.method_1031(f2, f1, -f2));
    }

    public void b(Vec3d vec3d, Vec3d vec3d1) {
        this.JA.a(vec3d, vec3d1);
    }

    public void c(Vec3d vec3d, Vec3d vec3d1) {
        this.JA.a(new Vec3d(vec3d.field_1352, vec3d.field_1351, vec3d.field_1350), new Vec3d(vec3d1.field_1352, vec3d.field_1351, vec3d.field_1350));
        this.JA.a(new Vec3d(vec3d1.field_1352, vec3d.field_1351, vec3d.field_1350), new Vec3d(vec3d1.field_1352, vec3d.field_1351, vec3d1.field_1350));
        this.JA.a(new Vec3d(vec3d1.field_1352, vec3d.field_1351, vec3d1.field_1350), new Vec3d(vec3d.field_1352, vec3d.field_1351, vec3d1.field_1350));
        this.JA.a(new Vec3d(vec3d.field_1352, vec3d.field_1351, vec3d1.field_1350), new Vec3d(vec3d.field_1352, vec3d.field_1351, vec3d.field_1350));
        this.JA.a(new Vec3d(vec3d.field_1352, vec3d1.field_1351, vec3d.field_1350), new Vec3d(vec3d1.field_1352, vec3d1.field_1351, vec3d.field_1350));
        this.JA.a(new Vec3d(vec3d1.field_1352, vec3d1.field_1351, vec3d.field_1350), new Vec3d(vec3d1.field_1352, vec3d1.field_1351, vec3d1.field_1350));
        this.JA.a(new Vec3d(vec3d1.field_1352, vec3d1.field_1351, vec3d1.field_1350), new Vec3d(vec3d.field_1352, vec3d1.field_1351, vec3d1.field_1350));
        this.JA.a(new Vec3d(vec3d.field_1352, vec3d1.field_1351, vec3d1.field_1350), new Vec3d(vec3d.field_1352, vec3d1.field_1351, vec3d.field_1350));
        this.JA.a(new Vec3d(vec3d.field_1352, vec3d.field_1351, vec3d.field_1350), new Vec3d(vec3d.field_1352, vec3d1.field_1351, vec3d.field_1350));
        this.JA.a(new Vec3d(vec3d1.field_1352, vec3d.field_1351, vec3d.field_1350), new Vec3d(vec3d1.field_1352, vec3d1.field_1351, vec3d.field_1350));
        this.JA.a(new Vec3d(vec3d1.field_1352, vec3d.field_1351, vec3d1.field_1350), new Vec3d(vec3d1.field_1352, vec3d1.field_1351, vec3d1.field_1350));
        this.JA.a(new Vec3d(vec3d.field_1352, vec3d.field_1351, vec3d1.field_1350), new Vec3d(vec3d.field_1352, vec3d1.field_1351, vec3d1.field_1350));
    }

    public void d(MatrixStack matrixstack, Vec3d vec3d) {
        this.aqc.c(matrixstack, vec3d, this.JA);
    }
}
