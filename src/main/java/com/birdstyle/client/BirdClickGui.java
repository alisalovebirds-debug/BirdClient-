package com.birdstyleclient;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class BirdClickGui extends Screen {

    private static final int BG = 0xF5080C19;
    private static final int SIDEBAR = 0xFF0A0E1B;
    private static final int CARD = 0xE9141A2C;
    private static final int ACCENT = 0xFF7966FF;
    private static final int TEXT = 0xFFF1F3FF;
    private static final int MUTED = 0xFF929BB5;

    private static final List<Module> MODULES = new ArrayList<>();

    private static final String[] CATEGORIES = {
            "Combat", "Utility", "Movement", "Misc", "Tools", "Visual"
    };

    private String selectedCategory = "Visual";
    private boolean favoritesOnly = false;
    private boolean appearancePage = false;
    private double scroll = 0;

    private static class Module {
        String name;
        String category;
        boolean enabled;
        boolean favorite;

        Module(String name, String category) {
            this.name = name;
            this.category = category;
        }
    }

    static {
        MODULES.add(new Module("Sprint Assist", "Movement"));
        MODULES.add(new Module("Velocity", "Combat"));
        MODULES.add(new Module("Auto Jump", "Movement"));
        MODULES.add(new Module("FPS Display", "Visual"));
        MODULES.add(new Module("Fullbright", "Visual"));
        MODULES.add(new Module("Crosshair", "Visual"));
        MODULES.add(new Module("Hit Color", "Visual"));
        MODULES.add(new Module("Keystrokes", "Visual"));
        MODULES.add(new Module("Armor HUD", "Visual"));
        MODULES.add(new Module("Coordinates", "Visual"));
    }

    public BirdClickGui() {
        super(Text.literal("Bird Style Client"));
    }

    public static boolean isModuleEnabled(String name) {
        for (Module module : MODULES) {
            if (module.name.equals(name)) {
                return module.enabled;
            }
        }
        return false;
    }

    @Override
    public void render(
            DrawContext ctx, int mouseX, int mouseY, float delta
    ) {
        int sw = width;
        int sh = height;

        ctx.fill(0, 0, sw, sh, BG);

        int panelW = Math.min(760, sw - 24);
        int panelH = Math.min(460, sh - 24);
        int px = (sw - panelW) / 2;
        int py = (sh - panelH) / 2;
        int sidebarW = 130;

        ctx.fill(px, py, px + panelW, py + panelH, SIDEBAR);
        ctx.fill(px + sidebarW, py, px + panelW, py + panelH, BG);

        ctx.drawTextWithShadow(
                textRenderer, "BIRD STYLE", px + 14, py + 14, TEXT
        );
        ctx.drawTextWithShadow(
                textRenderer, "CLIENT  /  BETA",
                px + 14, py + 27, MUTED
        );

        int catY = py + 56;

        for (String category : CATEGORIES) {
            boolean active = !favoritesOnly
                    && !appearancePage
                    && selectedCategory.equals(category);

            if (active) {
                ctx.fill(
                        px + 8, catY - 4,
                        px + sidebarW - 8, catY + 15,
                        ACCENT
                );
            }

            ctx.drawTextWithShadow(
                    textRenderer, category,
                    px + 16, catY, active ? TEXT : MUTED
            );
            catY += 23;
        }

        int bottomY = py + panelH - 56;

        ctx.drawTextWithShadow(
                textRenderer, "Favorites",
                px + 16, bottomY, favoritesOnly ? ACCENT : MUTED
        );
        ctx.drawTextWithShadow(
                textRenderer, "Appearance",
                px + 16, bottomY + 22,
                appearancePage ? ACCENT : MUTED
        );

        int contentX = px + sidebarW + 16;
        int contentY = py + 18;

        if (appearancePage) {
            ctx.drawTextWithShadow(
                    textRenderer, "Appearance",
                    contentX, contentY, TEXT
            );
            ctx.drawTextWithShadow(
                    textRenderer, "Bird Client theme",
                    contentX, contentY + 30, MUTED
            );
            ctx.drawTextWithShadow(
                    textRenderer, "Accent: Purple",
                    contentX, contentY + 50, ACCENT
            );
            ctx.drawTextWithShadow(
                    textRenderer, "Right Shift: close menu",
                    contentX, contentY + 80, MUTED
            );
            super.render(ctx, mouseX, mouseY, delta);
            return;
        }

        ctx.drawTextWithShadow(
                textRenderer,
                favoritesOnly ? "Favorites" : selectedCategory,
                contentX, contentY, TEXT
        );

        int listY = contentY + 28;
        int cardW = panelW - sidebarW - 32;
        int cardH = 40;
        int gap = 7;

        ctx.enableScissor(
                contentX, listY, contentX + cardW, py + panelH - 12
        );

        int index = 0;

        for (Module module : MODULES) {
            if (favoritesOnly && !module.favorite) continue;
            if (!favoritesOnly
                    && !module.category.equals(selectedCategory)) continue;

            int cy = listY + index * (cardH + gap) - (int) scroll;
            index++;

            if (cy + cardH < listY || cy > py + panelH - 12) continue;

            ctx.fill(
                    contentX, cy, contentX + cardW, cy + cardH,
                    module.enabled ? 0xFF242044 : CARD
            );

            ctx.drawTextWithShadow(
                    textRenderer, module.name,
                    contentX + 10, cy + 7, TEXT
            );

            ctx.drawTextWithShadow(
                    textRenderer,
                    module.enabled ? "ON" : "OFF",
                    contentX + cardW - 54, cy + 7,
                    module.enabled ? ACCENT : MUTED
            );

            ctx.drawTextWithShadow(
                    textRenderer,
                    module.favorite ? "★" : "☆",
                    contentX + cardW - 20, cy + 7,
                    module.favorite ? ACCENT : MUTED
            );
        }

        ctx.disableScissor();

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x();
        double my = click.y();

        int panelW = Math.min(760, width - 24);
        int panelH = Math.min(460, height - 24);
        int px = (width - panelW) / 2;
        int py = (height - panelH) / 2;
        int sidebarW = 130;

        int catY = py + 56;

        for (String category : CATEGORIES) {
            if (mx >= px && mx <= px + sidebarW
                    && my >= catY - 5 && my <= catY + 17) {
                selectedCategory = category;
                favoritesOnly = false;
                appearancePage = false;
                scroll = 0;
                return true;
            }
            catY += 23;
        }

        int bottomY = py + panelH - 56;

        if (mx >= px && mx <= px + sidebarW
                && my >= bottomY - 5 && my <= bottomY + 16) {
            favoritesOnly = true;
            appearancePage = false;
            scroll = 0;
            return true;
        }

        if (mx >= px && mx <= px + sidebarW
                && my >= bottomY + 17 && my <= bottomY + 40) {
            appearancePage = true;
            favoritesOnly = false;
            return true;
        }

        if (appearancePage) return true;

        int contentX = px + sidebarW + 16;
        int listY = py + 18 + 28;
        int cardW = panelW - sidebarW - 32;
        int cardH = 40;
        int gap = 7;
        int index = 0;

        for (Module module : MODULES) {
            if (favoritesOnly && !module.favorite) continue;
            if (!favoritesOnly
                    && !module.category.equals(selectedCategory)) continue;

            int cy = listY + index * (cardH + gap) - (int) scroll;
            index++;

            if (mx < contentX || mx > contentX + cardW
                    || my < cy || my > cy + cardH) continue;

            if (click.button() == 1
                    || mx >= contentX + cardW - 28) {
                module.favorite = !module.favorite;
            } else {
                module.enabled = !module.enabled;
            }
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(
            double mouseX, double mouseY,
            double horizontalAmount, double verticalAmount
    ) {
        scroll -= verticalAmount * 24;
        if (scroll < 0) scroll = 0;
        if (scroll > 500) scroll = 500;
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
