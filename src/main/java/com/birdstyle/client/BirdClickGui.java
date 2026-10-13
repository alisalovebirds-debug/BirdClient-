
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
    private static final int PANEL = 0xFF11182A;
    private static final int CARD = 0xFF151D31;
    private static final int HOVER = 0xFF202944;
    private static final int PURPLE = 0xFF7966FF;
    private static final int WHITE = 0xFFF1F3FF;
    private static final int MUTED = 0xFF929BB8;
    private static final int BORDER = 0xFF29324D;

    private final String[] categories = {
        "Combat", "Utility", "Movement",
        "Misc", "Tools", "Visual"
    };

    private final String[] modules = {
        "Sprint Assist", "Velocity", "Auto Jump",
        "FPS Display", "Fullbright", "Crosshair",
        "Hit Color", "Keystrokes"
    };

    private final String[] descriptions = {
        "Movement assistance options.",
        "Client-side movement display options.",
        "Movement preference settings.",
        "Shows your current FPS.",
        "Improves visibility in dark areas.",
        "Customize your crosshair display.",
        "Customize hit feedback visuals.",
        "Shows keyboard input on screen."
    };

    private final List<Boolean> enabled = new ArrayList<>();
    private final List<Boolean> favorites = new ArrayList<>();

    private int category = 1;
    private int tab = 0;
    private int scroll = 0;

    public BirdClickGui() {
        super(Text.literal("Bird Style Client"));

        for (int i = 0; i < modules.length; i++) {
            enabled.add(false);
            favorites.add(false);
        }
    }

    @Override
    protected void init() {
        super.init();
    }

    private void text(DrawContext ctx, String value,
                      int x, int y, int color) {
        ctx.drawTextWithShadow(
            textRenderer, value, x, y, color
        );
    }

    private void rect(DrawContext ctx, int x, int y,
                      int w, int h, int color) {
        ctx.fill(x, y, x + w, y + h, color);
    }

    @Override
    public void render(DrawContext ctx, int mouseX,
                       int mouseY, float delta) {

        ctx.fill(0, 0, width, height, 0x99040710);

        int panelW = Math.min(1000, width - 32);
        int panelH = Math.min(620, height - 32);
        int x = (width - panelW) / 2;
        int y = (height - panelH) / 2;
        int sideW = Math.min(240, panelW / 3);

        rect(ctx, x, y, panelW, panelH, BG);
        rect(ctx, x, y, sideW, panelH, SIDEBAR);
        rect(ctx, x + sideW, y, 2, panelH, BORDER);

        // Branding
        text(ctx, "♟", x + 22, y + 26, PURPLE);
        text(ctx, "BIRD CLIENT", x + 48, y + 23, WHITE);
        text(ctx, "BETA RELEASE", x + 48, y + 43, MUTED);

        text(ctx, "MODULES", x + 22, y + 104, MUTED);

        // Sidebar categories
        for (int i = 0; i < categories.length; i++) {
            int cy = y + 132 + i * 53;
            boolean selected = category == i;

            if (selected) {
                rect(ctx, x + 12, cy, sideW - 24, 42,
                     0xFF302B57);
                rect(ctx, x + 12, cy, 4, 42, PURPLE);
            }

            if (mouseX >= x + 12 && mouseX <= x + sideW - 12
                && mouseY >= cy && mouseY <= cy + 42) {
                if (!selected) {
                    rect(ctx, x + 12, cy, sideW - 24, 42,
                         HOVER);
                }
            }

            text(ctx, categories[i], x + 30, cy + 15,
                 selected ? WHITE : MUTED);
        }

        int mainX = x + sideW + 28;
        int mainW = panelW - sideW - 48;

        // Top tabs
        String[] tabs = {"MODULES", "FAVORITES", "APPEARANCE"};
        int[] tabX = {mainX, mainX + 145, mainX + 300};

        for (int i = 0; i < tabs.length; i++) {
            text(ctx, tabs[i], tabX[i], y + 29,
                 tab == i ? WHITE : MUTED);

            if (tab == i) {
                rect(ctx, tabX[i], y + 53,
                     i == 2 ? 92 : 72, 3, PURPLE);
            }
        }

        // Search bar (visual header)
        int searchX = x + panelW - 285;
        rect(ctx, searchX, y + 14, 250, 42, SIDEBAR);
        rect(ctx, searchX, y + 14, 250, 1, BORDER);
        rect(ctx, searchX, y + 55, 250, 1, BORDER);
        text(ctx, "Search modules", searchX + 16,
             y + 29, MUTED);

        int contentY = y + 92;

        if (tab == 2) {
            text(ctx, "APPEARANCE", mainX, contentY,
                 WHITE);
            text(ctx, "Dark theme", mainX, contentY + 48,
                 MUTED);
            text(ctx, "Accent color: Purple",
                 mainX, contentY + 82, PURPLE);
            text(ctx, "Compact cards: ON",
                 mainX, contentY + 116, MUTED);
            text(ctx, "Blur background: OFF",
                 mainX, contentY + 150, MUTED);

        } else {
            text(ctx,
                tab == 1 ? "FAVORITE MODULES"
                         : categories[category].toUpperCase()
                           + " MODULES",
                mainX, contentY, WHITE);

            int count = 0;
            for (int i = 0; i < modules.length; i++) {
                if (tab == 1 && !favorites.get(i)) continue;
                count++;
            }

            text(ctx, count + " MODULES",
                 mainX, contentY + 23, MUTED);

            int cardY = contentY + 52 - scroll;
            int cardW = mainW;

            for (int i = 0; i < modules.length; i++) {
                if (tab == 1 && !favorites.get(i)) continue;

                int cy = cardY;
                boolean hovered =
                    mouseX >= mainX && mouseX <= mainX + cardW
                    && mouseY >= cy && mouseY <= cy + 68;

                rect(ctx, mainX, cy, cardW, 68,
                     hovered ? HOVER : CARD);
                rect(ctx, mainX, cy, cardW, 1, BORDER);
                rect(ctx, mainX, cy + 67, cardW, 1, BORDER);

                text(ctx, modules[i], mainX + 18,
                     cy + 13, WHITE);

                text(ctx, descriptions[i], mainX + 18,
                     cy + 39, MUTED);

                text(ctx, favorites.get(i) ? "★" : "☆",
                     mainX + cardW - 75, cy + 25, PURPLE);

                int toggleX = mainX + cardW - 48;
                rect(ctx, toggleX, cy + 20, 30, 26,
                     enabled.get(i) ? PURPLE : 0xFF252D42);
                rect(ctx, toggleX + (enabled.get(i) ? 15 : 3),
                     cy + 23, 20 - (enabled.get(i) ? 0 : 0),
                     20, WHITE);

                cardY += 77;
            }
        }

        text(ctx, "Bird Client 1.21.11",
             x + panelW - 155, y + panelH + 8, MUTED);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    protected boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();

        int panelW = Math.min(1000, width - 32);
        int panelH = Math.min(620, height - 32);
        int x = (width - panelW) / 2;
        int y = (height - panelH) / 2;
        int sideW = Math.min(240, panelW / 3);

        if (mouseX >= x + 12 && mouseX <= x + sideW - 12) {
            for (int i = 0; i < categories.length; i++) {
                int cy = y + 132 + i * 53;

                if (mouseY >= cy && mouseY <= cy + 42) {
                    category = i;
                    tab = 0;
                    scroll = 0;
                    return true;
                }
            }
        }

        int mainX = x + sideW + 28;
        int mainW = panelW - sideW - 48;

        if (mouseY >= y + 15 && mouseY <= y + 60) {
            if (mouseX >= mainX && mouseX < mainX + 125) {
                tab = 0;
                scroll = 0;
                return true;
            }
            if (mouseX >= mainX + 140 && mouseX < mainX + 280) {
                tab = 1;
                scroll = 0;
                return true;
            }
            if (mouseX >= mainX + 295 && mouseX < mainX + 410) {
                tab = 2;
                return true;
            }
        }

        if (tab != 2) {
            int cardY = y + 92 + 52 - scroll;

            for (int i = 0; i < modules.length; i++) {
                if (tab == 1 && !favorites.get(i)) continue;

                if (mouseX >= mainX && mouseX <= mainX + mainW
                    && mouseY >= cardY && mouseY <= cardY + 68) {

                    if (mouseX >= mainX + mainW - 85
                        && mouseX < mainX + mainW - 48) {
                        favorites.set(i, !favorites.get(i));
                    } else if (mouseX >= mainX + mainW - 48) {
                        enabled.set(i, !enabled.get(i));
                    }

                    return true;
                }

                cardY += 77;
            }
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

