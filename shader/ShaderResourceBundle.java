package dev.mark.system.shader;

import java.util.ArrayList;
import java.util.List;

public class ShaderResourceBundle {
    private final List<ShaderResource> resources = new ArrayList<>();

    public ShaderResourceBundle(String s, byte[] abyte, byte[] abyte1, byte[] abyte2) {
        s = "shaders/core/" + s;
        this.resources.add(ShaderResource.b(s + ".json", abyte));
        this.resources.add(ShaderResource.b(s + ".fsh", abyte1));
        this.resources.add(ShaderResource.b(s + ".vsh", abyte2));
    }

    public List<ShaderResource> a() {
        return this.resources;
    }

    protected static byte[] b(String s) {
        return s.getBytes();
    }
}
