package com.panoramas.screen;

import org.jetbrains.annotations.Nullable;

import com.panoramas.Panoramas;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class PanoramaIcon implements AutoCloseable {
    public static final int ICON_WIDTH;
    public static final int ICON_HEIGHT;
    private static final Identifier UNKNOWN_SERVER_ID = new Identifier("textures/misc/unknown_server.png");
    private final TextureManager textureManager;
    private final Identifier id;
    @Nullable
    private NativeImageBackedTexture texture;
    private boolean closed;

    private PanoramaIcon(TextureManager textureManager, Identifier id) {
        this.textureManager = textureManager;
        this.id = id;
    }

    public static PanoramaIcon createIcon(TextureManager textureManager, String packName) {
        String safePackName = Util.replaceInvalidChars(packName, Identifier::isPathCharacterValid);

        return new PanoramaIcon(textureManager, new Identifier("panoramas",
                "panoramas/" + safePackName +
                        "/icon"));
    }

    public void load(NativeImage image) {
        NativeImage iconImage = image;

        if (image.getWidth() != ICON_WIDTH || image.getHeight() != ICON_HEIGHT) {
            iconImage = new NativeImage(ICON_WIDTH, ICON_HEIGHT, true);
            cropAndResizeToIcon(image, iconImage);
        }

        try {
            this.assertOpen();
            if (this.texture == null) {
                this.texture = new NativeImageBackedTexture(iconImage);
            } else {
                this.texture.setImage(iconImage);
                this.texture.upload();
            }

            this.textureManager.registerTexture(this.id, this.texture);
        } catch (Throwable exception) {
            Panoramas.LOGGER.error("Exception in loading for panorama icon", exception);

            iconImage.close();
            this.destroy();
            throw exception;
        }

        // } else {
        // image.close();
        // throw new IllegalArgumentException(
        // "Icon must be 64x64, but was " + image.getWidth() + "x" + image.getHeight());
        // }
    }

    private void cropAndResizeToIcon(NativeImage inputImage, NativeImage targetImage) {
        if (targetImage.getWidth() != ICON_WIDTH || targetImage.getHeight() != ICON_HEIGHT) {
            throw new IllegalArgumentException("Target Image must be " + ICON_WIDTH + "x" + ICON_HEIGHT + ", but was "
                    + targetImage.getWidth() + "x" + targetImage.getHeight());
        }

        int originalWidth = inputImage.getWidth();
        int originalHeight = inputImage.getHeight();

        int squareSize = Math.min(originalWidth, originalHeight);

        int x = (originalWidth - squareSize) / 2;
        int y = (originalHeight - squareSize) / 2;

        // Crop and resize the image to the correct size using bilinear interpolation
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                float srcX = x + (i + 0.5f) * squareSize / 64 - 0.5f;
                float srcY = y + (j + 0.5f) * squareSize / 64 - 0.5f;

                int color = bilinearInterpolate(inputImage, srcX, srcY);
                targetImage.setColor(i, j, color);
            }
        }
        // for (int i = 0; i < ICON_WIDTH; i++) {
        //     for (int j = 0; j < ICON_HEIGHT; j++) {
        //         int srcX = x + (i * squareSize / ICON_WIDTH);
        //         int srcY = y + (j * squareSize / ICON_HEIGHT);
        //         targetImage.setColor(i, j, inputImage.getColor(srcX, srcY));
        //     }
        // }
    }

    private static int bilinearInterpolate(NativeImage image, float x, float y) {
        int x1 = (int) Math.floor(x);
        int y1 = (int) Math.floor(y);
        int x2 = Math.min(x1 + 1, image.getWidth() - 1);
        int y2 = Math.min(y1 + 1, image.getHeight() - 1);

        float x2Weight = x - x1;
        float x1Weight = 1 - x2Weight;
        float y2Weight = y - y1;
        float y1Weight = 1 - y2Weight;

        int color11 = image.getColor(x1, y1);
        int color12 = image.getColor(x1, y2);
        int color21 = image.getColor(x2, y1);
        int color22 = image.getColor(x2, y2);

        int r = (int) (((color11 >> 24 & 0xFF) * x1Weight + (color21 >> 24 & 0xFF) * x2Weight) * y1Weight +
                ((color12 >> 24 & 0xFF) * x1Weight + (color22 >> 24 & 0xFF) * x2Weight) * y2Weight);
        int g = (int) (((color11 >> 16 & 0xFF) * x1Weight + (color21 >> 16 & 0xFF) * x2Weight) * y1Weight +
                ((color12 >> 16 & 0xFF) * x1Weight + (color22 >> 16 & 0xFF) * x2Weight) * y2Weight);
        int b = (int) (((color11 >> 8 & 0xFF) * x1Weight + (color21 >> 8 & 0xFF) * x2Weight) * y1Weight +
                ((color12 >> 8 & 0xFF) * x1Weight + (color22 >> 8 & 0xFF) * x2Weight) * y2Weight);
        int a = (int) (((color11 & 0xFF) * x1Weight + (color21 & 0xFF) * x2Weight) * y1Weight +
                ((color12 & 0xFF) * x1Weight + (color22 & 0xFF) * x2Weight) * y2Weight);

        return (r << 24) | (g << 16) | (b << 8) | a;
    }

    public void destroy() {
        this.assertOpen();
        if (this.texture != null) {
            this.textureManager.destroyTexture(this.id);
            this.texture.close();
            this.texture = null;
        }

    }

    public Identifier getTextureId() {
        return this.texture != null ? this.id : UNKNOWN_SERVER_ID;
    }

    public void close() {
        this.destroy();
        this.closed = true;
    }

    private void assertOpen() {
        if (this.closed) {
            throw new IllegalStateException("Icon already closed");
        }
    }

    static {
        ICON_WIDTH = 64;
        ICON_HEIGHT = 64;
    }
}
