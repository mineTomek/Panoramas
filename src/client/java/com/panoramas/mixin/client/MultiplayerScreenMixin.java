package com.panoramas.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.panoramas.Panoramas;
import com.panoramas.screen.PanoramaScreen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    private ButtonWidget managePanoramasButton;

    @Inject(method = "init", at = @At("HEAD"))
    private void initInjected(CallbackInfo info) {
        managePanoramasButton = addDrawableChild(
                ButtonWidget.builder(ScreenTexts.EMPTY, button -> {
                    try {
                        client.setScreen(new PanoramaScreen(this));
                    } catch (Exception e) {
                        Panoramas.LOGGER.error(e.getMessage());
                    }
                }).dimensions(this.width / 2 + 158, this.height - 30, 20, 20).build());
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderInjected(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        Identifier panoramaButtonTextureLocation = new Identifier(Panoramas.MOD_ID, "panoramas_button");

        context.drawGuiTexture(panoramaButtonTextureLocation, managePanoramasButton.getX(),
                managePanoramasButton.getY(), managePanoramasButton.getWidth(),
                managePanoramasButton.getHeight());
    }
}