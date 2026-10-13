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
    private static final int CARD = 0xFF151D31;
    private static final int HOVER = 0xFF202944;
    private static final int PURPLE = 0xFF7966FF;
    private static final int WHITE = 0xFFF1F3FF;
    private static final int MUTED = 0xFF929BB8;
    private static final int BORDER = 0xFF29324D;

    private static class Module {
        String name, category, description;
        boolean enabled, favorite;
        int delay = 100;
        boolean weaponsOnly = false;
        boolean extraSetting = false;

        Module(String name, String category, String description) {
            this.name = name;
            this.category = category;
            this.description = description;
        }
    }

    // Static storage keeps states when the GUI is reopened.
    private static final List<Module> MODULES = new ArrayList<>();

    static {
        add("Auto Clicker", "Combat", "Automatic clicking");
        add("Reach Display", "Combat", "Display reach information");
        add("Hit Color", "Combat", "Customize hit indicator");
        add("Sprint Assist", "Movement", "Assist with sprinting");
        add("Auto Jump", "Movement", "Automatic jumping");
        add("Step Assist", "Movement", "Step settings");
        add("FPS Display", "Visual", "Display frames per second");
        add("Fullbright", "Visual", "Brightness settings");
        add("Crosshair", "Visual", "Crosshair appearance");
        add("Keystrokes", "Visual", "Show movement keys");
        add("Armor HUD", "Visual", "Display equipped armor");
        add("Coordinates", "Visual", "Display player coordinates");
        add("Velocity", "Combat", "Velocity settings");
        add("Auto Tool", "Tools", "Tool selection settings");
        add("Durability", "Tools", "Display item durability");
        add("Item Info", "Tools", "Display item information");
        add("Chat Timestamps", "Misc", "Chat display settings");
        add("Toggle Sprint", "Misc", "Sprint toggle settings");
        add("Zoom", "Utility", "Zoom settings");
        add("Inventory Sort", "Utility", "Inventory settings");
    }

    private static void add(String name, String category, String description) {
        MODULES.add(new Module(name, category, description));
    }

    private final String[] categories = {
        "Combat", "Utility", "Movement", "Misc", "Tools", "Visual"
    };

    private int category = 0;
    private int tab = 0;
    private int scroll = 0;
    private Module selectedModule = null;
    private boolean compact = false;
    private boolean blur = false;
    private int accentChoice = 0;

    public BirdClickGui() {
        super(Text.literal("Bird Style Client"));
    }

    private int accent() {
        return switch (accentChoice) {
            case 1 -> 0xFF53C8FF;
            case 2 -> 0xFFFF6B9D;
            case 3 -> 0xFF61D995;
            default -> PURPLE;
        };
    }

    private void rect(DrawContext ctx, int x, int y,
                      int w, int h, int color) {
        ctx.fill(x, y, x + w, y + h, color);
    }

    private void text(DrawContext ctx, String value,
                      int x, int y, int color) {
        ctx.drawTextWithShadow(textRenderer, value, x, y, color);
    }

    private int panelX() {
        return (width - Math.min(1000, width - 32)) / 2;
    }

    private int panelY() {
        return (height - Math.min(620, height - 32)) / 2;
    }

    private int panelW() {
        return Math.min(1000, width - 32);
    }

    private int panelH() {
        return Math.min(620, height - 32);
    }

    private int sideW() {
        return Math.min(200, panelW() / 3);
    }

    private int mainX() {
        return panelX() + sideW() + 24;
    }

    private int mainW() {
        return panelW() - sideW() - 40;
    }

    private int contentTop() {
        return panelY() + 104;
    }

    private int listTop() {
        return contentTop() + 35;
    }

    private int listBottom() {
        return panelY() + panelH() - 18;
    }

    private int cardHeight() {
        return compact ? 54 : 68;
    }

    private int cardGap() {
        return compact ? 6 : 9;
    }

    private List<Module> visibleModules() {
        List<Module> result = new ArrayList<>();

        for (Module m : MODULES) {
            if (tab == 1) {
                if (m.favorite) result.add(m);
            } else if (selectedModule != null) {
                // Settings page doesn't use this list.
            } else if (m.category.equals(categories[category])) {
                result.add(m);
            }
        }

        return result;
    }

    @Override
    public void render(DrawContext ctx, int mouseX,
                       int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x99040710);

        int x = panelX();
        int y = panelY();
        int pw = panelW();
        int ph = panelH();
        int sw = sideW();
        int mx = mainX();
        int mw = mainW();

        rect(ctx, x, y, pw, ph, BG);
        rect(ctx, x, y, sw, ph, SIDEBAR);
        rect(ctx, x + sw, y, 2, ph, BORDER);

        text(ctx, "BIRD CLIENT", x + 20, y + 20, WHITE);
        text(ctx, "BETA RELEASE", x + 20, y + 40, MUTED);
        text(ctx, "MODULES", x + 20, y + 80, MUTED);

        for (int i = 0; i < categories.length; i++) {
            int cy = y + 100 + i * 43;
            boolean active = category == i && tab == 0
                && selectedModule == null;

            if (active) {
                rect(ctx, x + 10, cy, sw - 20, 36, 0xFF302B57);
                rect(ctx, x + 10, cy, 4, 36, accent());
            } else if (mouseX >= x + 10 && mouseX <= x + sw - 10
                    && mouseY >= cy && mouseY <= cy + 36) {
                rect(ctx, x + 10, cy, sw - 20, 36, HOVER);
            }

            text(ctx, categories[i], x + 25, cy + 12,
                 active ? WHITE : MUTED);
        }

        String[] tabs = {"MODULES", "FAVORITES", "APPEARANCE"};
        int[] tabOffsets = {0, 145, 300};

        for (int i = 0; i < tabs.length; i++) {
            int tx = mx + tabOffsets[i];
            text(ctx, tabs[i], tx, y + 26, tab == i ? WHITE : MUTED);

            if (tab == i) {
                rect(ctx, tx, y + 48, i == 2 ? 92 : 78, 3, accent());
            }
        }

        if (selectedModule != null) {
            drawSettings(ctx, mx, mw);
        } else if (tab == 2) {
            drawAppearance(ctx, mx);
        } else {
            drawModuleList(ctx, mx, mw, mouseX, mouseY);
        }

        text(ctx, "Bird Client 1.21.11",
             x + pw - 155, y + ph - 15, MUTED);
    }

    private void drawModuleList(DrawContext ctx, int mx, int mw,
                                int mouseX, int mouseY) {
        String heading = tab == 1 ? "FAVORITE MODULES"
            : categories[category].toUpperCase() + " MODULES";

        text(ctx, heading, mx, contentTop(), WHITE);

        List<Module> list = visibleModules();
        int top = listTop();
        int bottom = listBottom();
        int ch = cardHeight();
        int step = ch + cardGap();
        int maxScroll = Math.max(0, list.size() * step - (bottom - top));
        scroll = Math.max(0, Math.min(scroll, maxScroll));

        ctx.enableScissor(mx, top, mx + mw, bottom);

        for (int i = 0; i < list.size(); i++) {
            Module m = list.get(i);
            int cy = top + i * step - scroll;

            if (cy + ch < top || cy > bottom) continue;

            boolean hovering = mouseX >= mx && mouseX <= mx + mw
                && mouseY >= cy && mouseY <= cy + ch;

            rect(ctx, mx, cy, mw, ch, hovering ? HOVER : CARD);
            rect(ctx, mx, cy, mw, 1, BORDER);
            rect(ctx, mx, cy + ch - 1, mw, 1, BORDER);

            text(ctx, m.name, mx + 14, cy + 10, WHITE);
            text(ctx, m.description, mx + 14, cy + 34, MUTED);

            int starX = mx + mw - 82;
            text(ctx, m.favorite ? "*" : "+", starX, cy + ch / 2 - 4,
                 accent());

            int toggleX = mx + mw - 43;
            rect(ctx, toggleX, cy + ch / 2 - 12, 30, 24,
                 m.enabled ? accent() : 0xFF252D42);
            rect(ctx, toggleX + (m.enabled ? 13 : 3),
                 cy + ch / 2 - 9, 14, 18, WHITE);
        }

        ctx.disableScissor();

        if (maxScroll > 0) {
            int trackX = mx + mw - 4;
            int trackH = bottom - top;
            rect(ctx, trackX, top, 3, trackH, 0xFF202944);
            int thumbH = Math.max(24, trackH * trackH /
                (list.size() * step));
            int thumbY = top + (trackH - thumbH) * scroll /
                Math.max(1, maxScroll);
            rect(ctx, trackX, thumbY, 3, thumbH, accent());
        }

        if (list.isEmpty()) {
            text(ctx, tab == 1 ? "Star a module to add it here."
                : "No modules in this category.", mx, top + 12, MUTED);
        }
    }

    private void drawAppearance(DrawContext ctx, int mx) {
        int cy = contentTop();

        text(ctx, "APPEARANCE", mx, cy, WHITE);

        drawOption(ctx, mx, cy + 35, "Compact cards",
                   compact ? "ON" : "OFF", compact);
        drawOption(ctx, mx, cy + 83, "Blur background",
                   blur ? "ON" : "OFF", blur);
        drawOption(ctx, mx, cy + 131, "Accent color",
                   new String[]{"Purple", "Blue", "Pink", "Green"}[accentChoice],
                   true);

        text(ctx, "Tap an option to change it.", mx, cy + 185, MUTED);
    }

    private void drawOption(DrawContext ctx, int x, int y,
                            String name, String value, boolean on) {
        rect(ctx, x, y, mainW(), 38, CARD);
        text(ctx, name, x + 12, y + 13, WHITE);
        text(ctx, value, x + mainW() - 85, y + 13,
             on ? accent() : MUTED);
    }

    private void drawSettings(DrawContext ctx, int mx, int mw) {
        Module m = selectedModule;

        text(ctx, "< BACK", mx, contentTop(), accent());
        text(ctx, m.name.toUpperCase() + " SETTINGS",
             mx, contentTop() + 30, WHITE);
        text(ctx, m.description, mx, contentTop() + 52, MUTED);

        int cy = contentTop() + 85;

        drawOption(ctx, mx, cy, "Enabled", m.enabled ? "ON" : "OFF",
                   m.enabled);
        drawOption(ctx, mx, cy + 48, "Delay",
                   m.delay + " ms", false);
        drawOption(ctx, mx, cy + 96, "Weapons only",
                   m.weaponsOnly ? "ON" : "OFF", m.weaponsOnly);
        drawOption(ctx, mx, cy + 144, "Extra setting",
                   m.extraSetting ? "ON" : "OFF", m.extraSetting);

        text(ctx, "Click a row to change its setting.",
             mx, cy + 202, MUTED);
        text(ctx, "These are generic settings; each module needs",
             mx, cy + 224, MUTED);
        text(ctx, "its own implementation for gameplay effects.",
             mx, cy + 240, MUTED);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x();
        double my = click.y();
        int button = click.button();

        int x = panelX();
        int y = panelY();
        int sw = sideW();
        int mainX = this.mainX();
        int mainW = this.mainW();

        if (my >= y + 10 && my <= y + 58) {
            if (mx >= mainX && mx < mainX + 125) {
                tab = 0;
                selectedModule = null;
                scroll = 0;
                return true;
            }
            if (mx >= mainX + 140 && mx < mainX + 280) {
                tab = 1;
                selectedModule = null;
                scroll = 0;
                return true;
            }
            if (mx >= mainX + 295 && mx < mainX + 420) {
                tab = 2;
                selectedModule = null;
                scroll = 0;
                return true;
            }
        }

        if (selectedModule != null) {
            int cy = contentTop() + 85;

            if (my >= contentTop() && my < contentTop() + 22
                    && mx >= mainX && mx <= mainX + 70) {
                selectedModule = null;
                return true;
            }

            if (mx >= mainX && mx <= mainX + mainW) {
                if (my >= cy && my < cy + 38) {
                    selectedModule.enabled = !selectedModule.enabled;
                    return true;
                }
                if (my >= cy + 48 && my < cy + 86) {
                    selectedModule.delay += button == 1 ? -25 : 25;
                    selectedModule.delay = Math.max(0,
                        Math.min(1000, selectedModule.delay));
                    return true;
                }
                if (my >= cy + 96 && my < cy + 134) {
                    selectedModule.weaponsOnly =
                        !selectedModule.weaponsOnly;
                    return true;
                }
                if (my >= cy + 144 && my < cy + 182) {
                    selectedModule.extraSetting =
                        !selectedModule.extraSetting;
                    return true;
                }
            }

            return true;
        }

        if (mx >= x + 10 && mx <= x + sw - 10) {
            for (int i = 0; i < categories.length; i++) {
                int cy = y + 100 + i * 43;
                if (my >= cy && my <= cy + 36) {
                    category = i;
                    tab = 0;
                    scroll = 0;
                    return true;
                }
            }
        }

        if (tab == 2) {
            int cy = contentTop();

            if (mx >= mainX && mx <= mainX + mainW) {
                if (my >= cy + 35 && my <= cy + 73) {
                    compact = !compact;
                    scroll = 0;
                    return true;
                }
                if (my >= cy + 83 && my <= cy + 121) {
                    blur = !blur;
                    return true;
                }
                if (my >= cy + 131 && my <= cy + 169) {
                    accentChoice = (accentChoice + 1) % 4;
                    return true;
                }
            }
            return true;
        }

        if (mx >= mainX && mx <= mainX + mainW
                && my >= listTop() && my <= listBottom()) {
            List<Module> list = visibleModules();
            int step = cardHeight() + cardGap();
            int index = (int) ((my - listTop() + scroll) / step);

            if (index >= 0 && index < list.size()) {
                Module m = list.get(index);
                int starX = mainX + mainW - 88;
                int toggleX = mainX + mainW - 47;

                if (button == 1) {
                    selectedModule = m;
                    return true;
                } else if (mx >= starX && mx < toggleX) {
                    m.favorite = !m.favorite;
                    return true;
                } else if (mx >= toggleX) {
                    m.enabled = !m.enabled;
                    return true;
                } else {
                    selectedModule = m;
                    return true;
                }
            }
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY,
                                 double horizontalAmount,
                                 double verticalAmount) {
        if (selectedModule == null && tab != 2
                && mouseX >= mainX() && mouseX <= mainX() + mainW()
                && mouseY >= listTop() && mouseY <= listBottom()) {
            scroll -= (int) (verticalAmount * 35);

            int step = cardHeight() + cardGap();
            int max = Math.max(0, visibleModules().size() * step
                - (listBottom() - listTop()));

            scroll = Math.max(0, Math.min(scroll, max));
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY,
            horizontalAmount, verticalAmount);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
