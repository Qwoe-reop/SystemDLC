package dev.mark.system.render;

import net.minecraft.client.render.FrameGraphBuilder.Profiler;

public class WorldRendererProfilerAdapter implements Profiler {
    final net.minecraft.util.profiler.Profiler val$profiler;

    WorldRendererProfilerAdapter(CustomWorldRenderer customworldrenderer, net.minecraft.util.profiler.Profiler profiler) {
        this.val$profiler = profiler;
    }

    public void method_61920(String s) {
        this.val$profiler.method_15396(s);
    }

    public void method_61921(String s) {
        this.val$profiler.method_15407();
    }
}
