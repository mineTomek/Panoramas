package com.panoramas.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

@Environment(EnvType.CLIENT)
public abstract class PanoramaListWidget$Entry extends AlwaysSelectedEntryListWidget.Entry<PanoramaListWidget$Entry>
        implements AutoCloseable {
    public PanoramaListWidget$Entry() {
    }

    public void close() {
    }
}
