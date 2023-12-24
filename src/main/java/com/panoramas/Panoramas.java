package com.panoramas;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panoramas.config.ModConfig;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class Panoramas implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Panoramas");

	public static final String MOD_ID = "panoramas";

	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
	}
}