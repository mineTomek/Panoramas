package com.panoramas.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.panoramas.PanoramasClient;

import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.PackScreen;

@Mixin(PackScreen.class)
public abstract class PackScreenMixin {
    @Shadow
    private PackListWidget availablePackList;

    @Shadow
    private PackListWidget selectedPackList;

    @Inject(method = "updatePackLists", at = @At("TAIL"))
    private void updatePackListsInjected(CallbackInfo info) {
        if (PanoramasClient.GetConfig().hidePanoramasRP) {
            selectedPackList.children().removeIf(PanoramasClient::isPanoramasResourcePack);
            availablePackList.children().removeIf(PanoramasClient::isPanoramasResourcePack);
        }
    }
}