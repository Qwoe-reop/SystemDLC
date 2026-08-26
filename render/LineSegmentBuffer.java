package dev.mark.system.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Vec3d;

public class LineSegmentBuffer {
    List<Vec3d> cj = new ArrayList<>();
    List<Vec3d> Bs = new ArrayList<>();

    void a(Vec3d vec3d, Vec3d vec3d1) {
        this.cj.add(vec3d);
        this.Bs.add(vec3d1);
    }

    void b() {
        this.cj.clear();
        this.Bs.clear();
    }

    int c() {
        return this.cj.size();
    }
}
