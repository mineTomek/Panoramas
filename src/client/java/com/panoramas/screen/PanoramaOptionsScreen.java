package com.panoramas.screen;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.screen.pack.PackListWidget.ResourcePackEntry;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer.Pack;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PanoramaOptionsScreen extends Screen {

    // private final ResourcePackManager manager;

    private final PackScreen parent;

    private short refreshTimeout = 20;

    private PackListWidget packList;

    private final ResourcePackOrganizer organizer;

    public PanoramaOptionsScreen(PackScreen parent, ResourcePackManager manager,
            Function<ResourcePackProfile, Identifier> iconIdSupplier, Consumer<ResourcePackManager> applier) {
        super(Text.translatable("panoramas.packs.title"));
        organizer = new ResourcePackOrganizer(this::updatePackLists, iconIdSupplier, manager, applier);
        // this.manager = manager;
        this.parent = parent;
    }

    protected void init() {
        packList = (PackListWidget) addDrawableChild(
                new PackListWidget(client, parent, 200, height, title));
        packList.setX(width / 2 - 200 / 2);

        ButtonWidget doneButton = addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            close();
        })
                .position(width / 2 - (ButtonWidget.DEFAULT_WIDTH + 8 + ButtonWidget.DEFAULT_HEIGHT) / 2,
                        height - 48)
                .build());

        addDrawableChild(ButtonWidget.builder(Text.of("?"), button -> {
        })
                .position(doneButton.getRight() + 8, doneButton.getY())
                .tooltip(Tooltip.of(Text.translatable("panoramas.packs.help", Text.keybind("key.panoramas.create"))))
                .build())
                .setWidth(ButtonWidget.DEFAULT_HEIGHT);

        refresh();
    }

    @Override
    public void tick() {
        super.tick();

        if (refreshTimeout > 0 && --refreshTimeout == 0) {
            refresh();
        }
    }

    private void refresh() {
        organizer.refresh();
        updatePackLists();
        refreshTimeout = 20;
        // iconTextures.clear();
    }

    private void updatePackLists() {
        packList.children().clear();
        PackListWidget.ResourcePackEntry selectedResourcePackEntry = (PackListWidget.ResourcePackEntry) packList
                .getSelectedOrNull();
        String string = selectedResourcePackEntry == null ? "" : selectedResourcePackEntry.getName();
        packList.setSelected((ResourcePackEntry) null);

        Stream<Pack> allPacks = Stream.concat(organizer.getDisabledPacks(), organizer.getEnabledPacks());

        allPacks.forEach((pack) -> {
            if (!pack.getName().toLowerCase().contains("panorama"))
                return;
            PackListWidget.ResourcePackEntry resourcePackEntry = new PackListWidget.ResourcePackEntry(client,
                    packList, pack);
            packList.children().add(resourcePackEntry);

            if (pack.getName().equals(string)) {
                packList.setSelected(resourcePackEntry);
            }

        });
        // doneButton.active = !selectedPackList.children().isEmpty();
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
