package dev.mark.system.shader;

import dev.mark.system.resource.CustomResourceManager;
import java.util.ArrayList;

public class ShaderResourceLoader {
    public ShaderResourceLoader(CustomResourceManager CustomResourceManager) {
        ShaderCache.a();
        ArrayList arraylist = new ArrayList();
        arraylist.addAll(new BorderShaderResource().getResources());
        arraylist.addAll(new RectangleShaderResource().getResources());
        arraylist.addAll(new WhiteRectangleShaderResource().getResources());
        arraylist.addAll(new OutlineShaderResource().getResources());
        arraylist.addAll(new LineShaderResource().getResources());
        arraylist.addAll(new Line3dShaderResource().getResources());
        arraylist.addAll(new TextureShaderResource().getResources());
        arraylist.addAll(new BlurShaderResource().getResources());
        arraylist.addAll(new MSDFFontShaderResource().getResources());
        arraylist.addAll(new ColorPickerShaderResource().getResources());
        arraylist.addAll(new EntityOutlineResources().getResources());
        arraylist.addAll(new CornerBracketShaderResource().getResources());
        arraylist.addAll(new LiquidGlassShaderResource().getResources());
        arraylist.addAll(new HandShaderBundle().b());
        arraylist.forEach(r -> CustomResourceManager.addResource((ShaderResource)r));
    }
}
