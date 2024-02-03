package com.panoramas.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.panoramas.Panoramas;
import com.panoramas.PanoramasClient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.Entry;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.LanServerEntry;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.ServerEntry;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    private ButtonWidget loadPanoramaButton;

    private ButtonWidget removePanoramaButton;

    @Shadow
    protected MultiplayerServerListWidget serverListWidget;

    @Inject(method = "init", at = @At("HEAD"))
    private void initInjected(CallbackInfo info) {
        loadPanoramaButton = addDrawableChild(
                ButtonWidget.builder(ScreenTexts.EMPTY, button -> {
                    Entry entry = serverListWidget.getSelectedOrNull();

                    String packName = "";

                    if (entry instanceof LanServerEntry) {
                        packName = ((LanServerEntry) entry).getLanServerEntry().getAddressPort();
                    } else if (entry instanceof ServerEntry) {
                        packName = ((ServerEntry) entry).getServer().address;
                    }

                    client.getResourcePackManager().enable("file/Panorama_"
                            + packName);
                    client.options.refreshResourcePacks(client.getResourcePackManager());
                }).dimensions(this.width / 2 + 158, this.height - 30, 20, 20).build());

        removePanoramaButton = addDrawableChild(ButtonWidget.builder(ScreenTexts.EMPTY, button -> {
            ResourcePackManager manager = client.getResourcePackManager();

            for (ResourcePackProfile pack : manager.getProfiles()) {
                if (PanoramasClient.isPanoramasResourcePack(pack.getName())) {
                    manager.disable(pack.getName());
                }
            }

            client.options.refreshResourcePacks(manager);
        }).dimensions(this.width / 2 + 182, this.height - 30, 20, 20).build());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickInjected(CallbackInfo info) {
        Entry entry = serverListWidget.getSelectedOrNull();
        if (entry != null) {
            String packName = "";

            if (entry instanceof LanServerEntry) {
                packName = ((LanServerEntry) entry).getLanServerEntry().getAddressPort();
            } else if (entry instanceof ServerEntry) {
                packName = ((ServerEntry) entry).getServer().address;
            }

            if (PanoramasClient.hasPanoramaPack(packName)) {
                loadPanoramaButton.active = true;
                loadPanoramaButton.setTooltip(Tooltip.of(Text.literal("NO TOOLTIP")));
            } else {
                loadPanoramaButton.active = false;
                loadPanoramaButton.setTooltip(Tooltip
                        .of(Text.translatable("panoramas.pack_button.no_panorama",
                                Text.keybind("key.panoramas.create"))));
            }
        } else {
            loadPanoramaButton.active = false;
            loadPanoramaButton.setTooltip(Tooltip.of(Text.translatable("panoramas.pack_button.server_is_lan")));
        }

        removePanoramaButton.active = PanoramasClient.isAnyPanoramasResourcePackLoaded();
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