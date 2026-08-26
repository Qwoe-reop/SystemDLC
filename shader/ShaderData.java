package dev.mark.system.shader;

import com.google.gson.annotations.SerializedName;
import java.nio.charset.StandardCharsets;

public class ShaderData {
    @SerializedName("name")
    private String name;
    @SerializedName("jsonData")
    private String jsonData;
    @SerializedName("fshData")
    private String fshData;
    @SerializedName("vshData")
    private String vshData;

    public byte[] getJsonBytes() {
        return this.jsonData != null ? this.jsonData.getBytes(StandardCharsets.UTF_8) : null;
    }

    public byte[] getFshBytes() {
        return this.fshData != null ? this.fshData.getBytes(StandardCharsets.UTF_8) : null;
    }

    public byte[] getVshBytes() {
        return this.vshData != null ? this.vshData.getBytes(StandardCharsets.UTF_8) : null;
    }

    public String getName() {
        return this.name;
    }

    public String getJsonData() {
        return this.jsonData;
    }

    public String getFshData() {
        return this.fshData;
    }

    public String getVshData() {
        return this.vshData;
    }

    public String e() {
        return this.getJsonData();
    }

    public String f() {
        return this.getFshData();
    }

    public String g() {
        return this.getVshData();
    }
}
