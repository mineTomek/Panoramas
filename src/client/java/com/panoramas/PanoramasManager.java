package com.panoramas;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;

public class PanoramasManager {
    private MinecraftClient client;
    private List<String> panoramaPackNames = new ArrayList<String>();
    private List<Panorama> panoramas = new ArrayList<Panorama>();

    public PanoramasManager(MinecraftClient client) {
        this.client = client;
    }

    public void loadPanoramaPackNames() {
        if (!panoramaPackNames.isEmpty()) {
            return;
        }

        ResourcePackManager manager = client.getResourcePackManager();

        for (String name : manager.getNames()) {
            if (PanoramasClient.isPanoramasResourcePack(name)) {
                panoramaPackNames.add(name);

                Panoramas.LOGGER.info("Pack recognized: " + name);
            }
        }
    }

    public void loadPanoramas() {
        if (!panoramas.isEmpty()) {
            return;
        }

        loadPanoramaPackNames();

        for (String name : panoramaPackNames) {
            panoramas.add(new Panorama(client, name));
        }
    }

    public List<String> getPanoramaPackNames() {
        loadPanoramaPackNames();

        return panoramaPackNames;
    }

    public List<Panorama> getPanoramas() {
        loadPanoramas();
        
        return panoramas;
    }
}
