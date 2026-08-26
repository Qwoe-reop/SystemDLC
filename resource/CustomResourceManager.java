package dev.mark.system.resource;

import com.mojang.logging.LogUtils;
import dev.mark.system.core.ClientMain;
import dev.mark.system.module.client.HudModule;
import dev.mark.system.shader.ShaderResource;
import dev.mark.system.shader.ShaderResourceLoader;
import dev.mark.system.util.UnsafeFieldAccessor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.slf4j.Logger;

public class CustomResourceManager extends ReloadableResourceManagerImpl {
    private static final Logger LOGGER = LogUtils.getLogger();
    private LifecycledResourceManager activeManager;
    private final List<ResourceReloader> reloaders;
    private final ResourceType type;
    private final HashMap<String, ShaderResource> resourceCache = new HashMap<>();
    MinecraftClient mc = MinecraftClient.method_1551();

    public CustomResourceManager() {
        super(ResourceType.field_14188);
        HudModule hudmodule = ClientMain.getInstance().getModuleManager().getModule(HudModule.class);
        this.type = ResourceType.field_14188;
        this.activeManager = new LifecycledResourceManagerImpl(this.type, List.of());
        UnsafeFieldAccessor unsafefieldaccessor = new UnsafeFieldAccessor(this.mc.method_1478(), ReloadableResourceManagerImpl.class, 2).removeModifier();
        this.reloaders = (List<ResourceReloader>)unsafefieldaccessor.getValue();
        UnsafeFieldAccessor unsafefieldaccessor1 = new UnsafeFieldAccessor(this.mc, MinecraftClient.class, ReloadableResourceManagerImpl.class)
            .removeModifier();
        new ShaderResourceLoader(this);
        unsafefieldaccessor1.setValue(this);
        this.mc.method_1521();
    }

    public void close() {
        this.activeManager.close();
    }

    public void method_14477(ResourceReloader resourcereloader) {
        this.reloaders.add(resourcereloader);
    }

    public ResourceReload method_18232(Executor executor, Executor executor1, CompletableFuture<Unit> completablefuture, List<ResourcePack> list) {
        this.activeManager.close();
        this.activeManager = new LifecycledResourceManagerImpl(this.type, list);
        return SimpleResourceReload.method_40087(this, this.reloaders, executor, executor1, completablefuture, LOGGER.isDebugEnabled());
    }

    public Optional<Resource> method_14486(Identifier identifier) {
        return this.resourceCache.containsKey(identifier.method_12832())
            ? Optional.of(this.resourceCache.get(identifier.method_12832()).toResource())
            : this.activeManager.method_14486(identifier);
    }

    public Set<String> method_14487() {
        return this.activeManager.method_14487();
    }

    public List<Resource> method_14489(Identifier identifier) {
        return this.activeManager.method_14489(identifier);
    }

    public Map<Identifier, Resource> method_14488(String s, Predicate<Identifier> predicate) {
        LinkedHashMap linkedhashmap = new LinkedHashMap(this.activeManager.method_14488(s, predicate));
        this.resourceCache.keySet().forEach(s2 -> {
            if (s2.startsWith(s)) {
                linkedhashmap.put(Identifier.method_60656(s2), this.resourceCache.get(s2).toResource());
            }
        });
        return linkedhashmap;
    }

    public Map<Identifier, List<Resource>> method_41265(String s, Predicate<Identifier> predicate) {
        return this.activeManager.method_41265(s, predicate);
    }

    public Stream<ResourcePack> method_29213() {
        return this.activeManager.method_29213();
    }

    public void addResource(ShaderResource shaderresource) {
        this.resourceCache.put(shaderresource.getLocation().method_12832(), shaderresource);
    }
}
