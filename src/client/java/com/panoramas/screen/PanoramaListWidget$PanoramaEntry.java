package com.panoramas.screen;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.panoramas.Panorama;
import com.panoramas.Panoramas;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PanoramaListWidget$PanoramaEntry extends PanoramaListWidget$Entry {
    private final MinecraftClient client;

    // private final PanoramaListWidget listWidget;
    // private final PanoramaScreen screen;

    final Panorama panorama;

    private final PanoramaIcon icon;
    @Nullable
    private Path iconPath;

    public PanoramaListWidget$PanoramaEntry(PanoramaListWidget panoramaList, Panorama panorama) {
        this.client = MinecraftClient.getInstance();

        // this.screen = panoramaList.getParent();
        // this.listWidget = panoramaList;
        this.panorama = panorama;
        this.icon = PanoramaIcon.createIcon(this.client.getTextureManager(), panorama.getName());
        this.iconPath = panorama.getIconPath();
        this.loadIcon();
    }

    public Text getNarration() {
        Text text = Text.translatable("narrator.select.panorama_info",
                new Object[] { this.panorama.getDisplayName(), this.panorama.getDetails() });

        return Text.translatable("narrator.select", new Object[] { text });
    }

    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
            int mouseY, boolean hovered, float tickDelta) {
        TextRenderer textRenderer = this.client.textRenderer;
        Objects.requireNonNull(this.client.textRenderer);

        String title = this.panorama.getDisplayName();
        context.drawText(textRenderer, title, x + 35, y + 1, 16777215, false);

        String subtitle = "Made in " + this.panorama.getName();
        context.drawText(textRenderer, subtitle, x + 35, y + 12, -8355712, false);

        Text details = this.panorama.getDetails();
        context.drawText(textRenderer, details, x + 35, y + 21, -8355712, false);

        RenderSystem.enableBlend();
        context.drawTexture(this.icon.getTextureId(), x, y, 0.0F, 0.0F, 32, 32, 32, 32);
        RenderSystem.disableBlend();

        if (this.client.options.getTouchscreen().getValue() || hovered) {
            context.fill(x, y, x + 32, y + 32, -1601138544);

            int i = mouseX - x;
            Identifier textureIdentifier = i < 32 ? new Identifier(Panoramas.MOD_ID, "panorama_list/apply_highlighted")
                    : new Identifier(Panoramas.MOD_ID, "panorama_list/apply");

            context.drawGuiTexture(textureIdentifier, x, y, 32, 32);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // if (!this.panorama.isSelectable()) {
        // return true;
        // } else {
        // this.listWidget.setSelected(this);
        // if (!(mouseX - (double)this.listWidget.getRowLeft() <= 32.0) &&
        // Util.getMeasuringTimeMs() - this.time >= 250L) {
        // this.time = Util.getMeasuringTimeMs();
        // return true;
        // } else {
        // if (this.isLevelSelectable()) {
        this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        // this.play();
        // }

        return true;
        // }
        // }
    }

    public boolean isLevelSelectable() {
        return true;
    }

    private void loadIcon() {
        if (this.iconPath != null && Files.isRegularFile(this.iconPath, new LinkOption[0])) {
            try {
                InputStream inputStream = Files.newInputStream(this.iconPath);

                try {
                    this.icon.load(NativeImage.read(inputStream));
                } catch (Throwable loadingException) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable closeException) {
                            loadingException.addSuppressed(closeException);
                        }
                    }

                    throw loadingException;
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Throwable exception) {
                Panoramas.LOGGER.error("Invalid icon for panorama {}", this.panorama.getName(), exception);
                this.iconPath = null;
            }
        } else {
            Panoramas.LOGGER.error("Invalid icon for panorama (icon path problems) '{}'' with icon path '{}'", this.panorama.getName(), iconPath);
            this.icon.destroy();
        }

    }

    public void close() {
        this.icon.close();
    }
}
