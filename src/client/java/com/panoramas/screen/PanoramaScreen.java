package com.panoramas.screen;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import com.panoramas.Panorama;
import com.panoramas.Panoramas;
import com.panoramas.PanoramasManager;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class PanoramaScreen extends Screen {
    public Screen parent;

    private PanoramasManager panoramasManager;

    protected TextFieldWidget searchBox;
    private PanoramaListWidget panoramaList;

    private ButtonWidget applyButton;
    private ButtonWidget editButton;
    private ButtonWidget deleteButton;
    private ButtonWidget duplicateButton;

    public PanoramaScreen(Screen parent) {
        super(Text.translatable("panoramas.screen.title"));
        this.parent = parent;

        panoramasManager = new PanoramasManager(MinecraftClient.getInstance());
    }

    @Override
    protected void init() {
        this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox,
                Text.translatable("panoramas.screen.search"));
        this.searchBox.setChangedListener((search) -> {
            this.panoramaList.setSearch(search);
        });
        // this.addSelectableChild(this.searchBox);
        this.addDrawableChild(this.searchBox);

        this.panoramaList = addDrawableChild(new PanoramaListWidget(this, client, width, height - 112, 48, 36,
                this.searchBox.getText(), panoramasManager));

        this.applyButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("panoramas.screen.apply"),
                (button) -> {
                    this.panoramaList.getSelectedAsOptional().ifPresent(panorama -> {
                        Panoramas.LOGGER.info("Applying pack " + panorama.panorama.getDisplayName());
                    });
                }).dimensions(this.width / 2 - 154, this.height - 52, 150, 20).build());

        // this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectWorld.create"),
        // (button) -> {
        // CreateWorldScreen.create(this.client, this);
        // }).dimensions(this.width / 2 + 4, this.height - 52, 150, 20).build());

        this.editButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("panoramas.screen.edit"),
                (button) -> {
                    // this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::edit);
                }).dimensions(this.width / 2 - 154, this.height - 28, 72, 20).build());

        this.deleteButton = this
                .addDrawableChild(ButtonWidget.builder(Text.translatable("panoramas.screen.delete"),
                        (button) -> {
                            this.panoramaList.getSelectedAsOptional().ifPresent(panorama -> {
                                try {
                                    if (FabricLoader.getInstance().getGameDir().resolve("resourcepacks/"
                                            + panorama.panorama.getName())
                                            .toFile().exists()) {
                                        FileUtils.deleteDirectory(FabricLoader.getInstance().getGameDir()
                                                .resolve("resourcepacks/"
                                                        + panorama.panorama.getName())
                                                .toFile());
                                    } else {
                                        Panoramas.LOGGER.info("Could not locate panorama '{}'",
                                                FabricLoader.getInstance().getGameDir().resolve("resourcepacks/"
                                                        + panorama.panorama.getName()).toString());
                                    }
                                } catch (IOException exception) {
                                    Panoramas.LOGGER.error("Failed to delete the previous panorama", exception);
                                }
                            });
                        }).dimensions(this.width / 2 - 76, this.height - 28, 72, 20).build());

        this.duplicateButton = this
                .addDrawableChild(ButtonWidget.builder(Text.translatable("panoramas.screen.duplicate"),
                        (button) -> {
                            // this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::recreate);
                        }).dimensions(this.width / 2 + 4, this.height - 28, 72, 20).build());

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, (button) -> {
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 + 82, this.height - 28, 72, 20).build());

        this.panoramaSelected(null);
        this.setInitialFocus(this.searchBox);
    }

    public void panoramaSelected(@Nullable Panorama panorama) {
        if (panorama == null) {
            this.applyButton.active = false;
            this.editButton.active = false;
            this.deleteButton.active = false;
            this.duplicateButton.active = false;
        } else {
            this.applyButton.active = true;
            this.editButton.active = true;
            this.deleteButton.active = true; // panorama.isDeletable();
            this.duplicateButton.active = true;
        }
    }

    public void removed() {
        if (this.panoramaList != null) {
            this.panoramaList.children().forEach(PanoramaListWidget$Entry::close);
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
