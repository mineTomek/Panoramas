package com.panoramas;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.Text;

public class Panorama {
    private MinecraftClient client;
    public ResourcePackProfile profile;

    public Panorama(MinecraftClient client, String resourcepackName) {
        this.client = client;
        profile = client.getResourcePackManager().getProfile(resourcepackName);
    }

    public String getDisplayName() {
        return profile.getDisplayName().getString();
    }

    public String getName() {
        return profile.getName().replace("file/", "");
    }

    public Text getDetails() {
        return profile.getDescription();
    }

    public String getSourceWorldName() {
        PanoramaMetadata metadata;
        
        try {
            Path resourcePackDir = client.getResourcePackDir();
            FileReader reader = new FileReader(resourcePackDir.resolve(".panoramas").toFile());
            metadata = new Gson().fromJson(reader, PanoramaMetadata.class);
        } catch (IOException e) {
            Panoramas.LOGGER.error(e.getMessage());

            return "";
        }

        return metadata.sourceWorldName;
    }

    public @Nullable Path getIconPath() {
        Path resourcePackDir = client.getResourcePackDir();
        return resourcePackDir.resolve(getName()).resolve("pack.png");
    }
}
