package dev.mark.system.shader;

import java.io.ByteArrayInputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public class ShaderResource {
    private final Identifier location;
    private final byte[] buffer;

    public ShaderResource(String s, byte[] abyte) {
        this.location = Identifier.method_60656(s);
        this.buffer = abyte;
    }

    public Resource toResource() {
        return new Resource(MinecraftClient.method_1551().method_45573(), () -> new ByteArrayInputStream(this.buffer));
    }

    public static ShaderResource b(String s, byte[] abyte) {
        return new ShaderResource(s, abyte);
    }

    public Identifier getLocation() {
        return this.location;
    }
}
