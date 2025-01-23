package com.panoramas.screen;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.jetbrains.annotations.Nullable;

import com.panoramas.Panorama;
import com.panoramas.Panoramas;
import com.panoramas.PanoramasManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

public class PanoramaListWidget extends AlwaysSelectedEntryListWidget<PanoramaListWidget$Entry> {
    private final PanoramaScreen parent;
    private CompletableFuture<List<Panorama>> panoramasFuture;
    private PanoramasManager panoramasManager;
    @Nullable
    private List<Panorama> panoramas;
    private String search;
    // private final String loadingEntry;

    public PanoramaListWidget(PanoramaScreen parent, MinecraftClient client, int width, int height, int y,
            int itemHeight, String search, PanoramasManager panoramasManager/*
                                                                             * , @Nullable PanoramaListWidget oldWidget
                                                                             */) {
        super(client, width, height, y, itemHeight);
        this.parent = parent;
        this.panoramasManager = panoramasManager;
        // this.loadingEntry = new LoadingEntry(client);
        this.search = search;

        // if (oldWidget != null) {
        // this.panoramasFuture = oldWidget.panoramasFuture;
        // } else {
        this.panoramasFuture = this.loadPanoramas();
        // }

        this.show(this.tryGet());
    }

    protected void clearEntries() {
        this.children().forEach(PanoramaListWidget$Entry::close);
        super.clearEntries();
    }

    @Nullable
    private List<Panorama> tryGet() {
        try {
            return this.panoramasFuture.getNow(null);
        } catch (CancellationException | CompletionException var2) {
            return null;
        }
    }

    void load() {
        this.panoramasFuture = this.loadPanoramas();
    }

    // public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    // if (KeyCodes.isToggle(keyCode)) {
    // Optional<WorldEntry> optional = this.getSelectedAsOptional();
    // if (optional.isPresent()) {
    // if (((WorldEntry)optional.get()).isLevelSelectable()) {
    // this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK,
    // 1.0F));
    // ((WorldEntry)optional.get()).play();
    // }

    // return true;
    // }
    // }

    // return super.keyPressed(keyCode, scanCode, modifiers);
    // }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        List<Panorama> panoramasList = this.tryGet();
        if (panoramasList != this.panoramas) {
            this.show(panoramasList);
        }

        super.renderWidget(context, mouseX, mouseY, delta);
    }

    private void show(@Nullable List<Panorama> panoramas) {
        if (panoramas == null) {
            this.showLoadingScreen();
        } else {
            this.showPanoramas(this.search, panoramas);
        }

        this.panoramas = panoramas;
    }

    public void setSearch(String search) {
        if (this.panoramas != null && !search.equals(this.search)) {
            this.showPanoramas(search, this.panoramas);
        }

        this.search = search;
    }

    private CompletableFuture<List<Panorama>> loadPanoramas() {
        List<Panorama> levelList;
        try {
            levelList = panoramasManager.getPanoramas();
        } catch (Exception exception) {
            Panoramas.LOGGER.error("Couldn't load panorama list", exception);
            this.showUnableToLoadScreen(Text.literal(exception.getMessage()));
            return CompletableFuture.completedFuture(List.of());
        }

        // if (levelList.isEmpty()) {
        // CreateWorldScreen.create(this.client, (Screen)null);
        // return CompletableFuture.completedFuture(List.of());
        // } else {
        return CompletableFuture.completedFuture(levelList);
        // }
    }

    private void showPanoramas(String search, List<Panorama> panoramas) {
        this.clearEntries();

        search = search.toLowerCase(Locale.ROOT);
        Iterator<Panorama> iterator = panoramas.iterator();

        while (iterator.hasNext()) {
            Panorama panorama = iterator.next();
            if (this.shouldShow(search, panorama)) {
                this.addEntry(new PanoramaListWidget$PanoramaEntry(this, panorama));
            }
        }

        this.narrateScreenIfNarrationEnabled();
    }

    private boolean shouldShow(String search, Panorama panorama) {
        return panorama.getDisplayName().toLowerCase(Locale.ROOT).contains(search)
                || panorama.getName().toLowerCase(Locale.ROOT).contains(search);
    }

    private void showLoadingScreen() {
        this.clearEntries();
        // this.addEntry(this.loadingEntry);
        this.narrateScreenIfNarrationEnabled();
    }

    private void narrateScreenIfNarrationEnabled() {
        this.setScrollAmount(this.getScrollAmount());
        this.parent.narrateScreenIfNarrationEnabled(true);
    }

    private void showUnableToLoadScreen(Text message) {
        this.client.setScreen(new FatalErrorScreen(Text.translatable("selectWorld.unable_to_load"), message));
    }

    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 20;
    }

    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    public void setSelected(@Nullable PanoramaListWidget$Entry entry) {
        super.setSelected(entry);

        Panoramas.LOGGER.info("Pack selected!");

        Panorama panorama;
        if (entry instanceof PanoramaListWidget$PanoramaEntry panoramaEntry) {
            panorama = panoramaEntry.panorama;
        } else {
            panorama = null;
        }

        parent.panoramaSelected(panorama);
    }

    public Optional<PanoramaListWidget$PanoramaEntry> getSelectedAsOptional() {
        PanoramaListWidget$Entry entry = this.getSelectedOrNull();
        if (entry instanceof PanoramaListWidget$PanoramaEntry panoramaEntry) {
            return Optional.of(panoramaEntry);
        } else {
            return Optional.empty();
        }
    }

    public PanoramaScreen getParent() {
        return this.parent;
    }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        // if (this.children().contains(this.loadingEntry)) {
        // this.loadingEntry.appendNarrations(builder);
        // } else {
        super.appendClickableNarrations(builder);
        // }
    }
}