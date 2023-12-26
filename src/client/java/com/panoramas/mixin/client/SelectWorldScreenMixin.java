package com.panoramas.mixin.client;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.panoramas.Panoramas;
import com.panoramas.PanoramasClient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.storage.LevelSummary;

@Mixin(SelectWorldScreen.class)
public class SelectWorldScreenMixin extends Screen {
    protected SelectWorldScreenMixin(Text title) {
        super(title);
    }

    private ButtonWidget loadPanoramaButton;

    private ButtonWidget removePanoramaButton;

    @Shadow
    private WorldListWidget levelList;

    @Inject(method = "init", at = @At("HEAD"))
    private void initInjected(CallbackInfo info) {
        loadPanoramaButton = addDrawableChild(
                ButtonWidget.builder(ScreenTexts.EMPTY, button -> {
                    try {
                        client.getResourcePackManager().enable("file/Panorama_"
                                + ((LevelSummary) FieldUtils.readField(levelList.getSelectedOrNull(), "level"))
                                        .getName());
                        client.options.refreshResourcePacks(client.getResourcePackManager());
                    } catch (Exception e) {
                        Panoramas.LOGGER.error(e.getMessage());
                    }
                }).dimensions(this.width / 2 + 158, this.height - 28, 20, 20).build());

        removePanoramaButton = addDrawableChild(ButtonWidget.builder(ScreenTexts.EMPTY, button -> {
            ResourcePackManager manager = client.getResourcePackManager();

            for (ResourcePackProfile pack : manager.getProfiles()) {
                if (PanoramasClient.isPanoramasResourcePack(pack.getName())) {
                    manager.disable(pack.getName());
                }
            }

            client.options.refreshResourcePacks(manager);
        }).dimensions(this.width / 2 + 182, this.height - 28, 20, 20).build());
    }

    @Inject(method = "worldSelected", at = @At("TAIL"))
    public void worldSelectedInjected(@Nullable LevelSummary levelSummary, CallbackInfo info) {
        if (levelSummary == null) {
            loadPanoramaButton.active = false;
            loadPanoramaButton.setTooltip(Tooltip.of(Text.translatable("panoramas.pack_button.no_world_selected")));
        } else {
            loadPanoramaButton.active = true;
            loadPanoramaButton.setTooltip(null);
        }

        if (levelSummary != null && !PanoramasClient.hasPanoramaPack(levelSummary.getName())) {
            loadPanoramaButton.active = false;
            loadPanoramaButton.setTooltip(Tooltip
                    .of(Text.translatable("panoramas.pack_button.no_panorama", Text.keybind("key.panoramas.create"))));
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderInjected(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        Identifier loadPanoramasButtonTextureLocation = new Identifier(Panoramas.MOD_ID, "load_panoramas_button");

        context.drawGuiTexture(loadPanoramasButtonTextureLocation, loadPanoramaButton.getX(),
                loadPanoramaButton.getY(), loadPanoramaButton.getWidth(),
                loadPanoramaButton.getHeight());

        Identifier removePanoramasButtonTextureLocation = new Identifier(Panoramas.MOD_ID, "remove_panoramas_button");

        context.drawGuiTexture(removePanoramasButtonTextureLocation, removePanoramaButton.getX(),
                removePanoramaButton.getY(), removePanoramaButton.getWidth(),
                removePanoramaButton.getHeight());
    }
}