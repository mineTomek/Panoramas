package com.panoramas.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "panoramas")
public class ModConfig implements ConfigData {
    @ConfigEntry.BoundedDiscrete(max = 6, min = 1)
    public int resolution = 2;

    public boolean hidePanoramasRP = true;

    public boolean autoSetPanoramas = true;
}
