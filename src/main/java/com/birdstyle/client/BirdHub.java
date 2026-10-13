package com.birdstyleclient;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public final class BirdHud {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static boolean registered = false;
    private static double previousGamma = 1.0;
    private static boolean gammaSaved = false;

    private BirdHud() {}

    public static void register() {
        if (registered) return;
        registered = true;

        HudElementRegistry.addLast(
            Identifier.of("birdstyleclient", "custom_hud"),
            BirdHud::render
        );
    }

    private static boolean on(String name) {
        return BirdClickGui.isModuleEnabled(name);
    }

    private static void render(DrawContext ctx,
                               net.minecraft.client.render.RenderTickCounter tickCounter) {
        if (CLIENT.player == null || CLIENT.world == null) {
            restoreGamma();
            return;
        }

        // Fullbright: change gamma while enabled, then restore it.
        if (on("Fullbright")) {
            if (!gammaSaved) {
                previousGamma = CLIENT.options.getGamma().getValue();
                gammaSaved = true;
            }
            CLIENT.options.getGamma().setValue(16.0);
        } else {
            restoreGamma();
        }

        int x = 8;
        int y = 8;

        if (on("FPS Display")) {
            ctx.drawTextWithShadow(
                CLIENT.textRenderer,
                "FPS: " + MinecraftClient.getCurrentFps(),
                x, y, 0xFFFFFFFF
            );
            y += 12;
        }

        if (on("Coordinates")) {
            var p = CLIENT.player;
            String coords = String.format(
                "XYZ: %.1f / %.1f / %.1f",
                p.getX(), p.getY(), p.getZ()
            );
            ctx.drawTextWithShadow(
                CLIENT.textRenderer, coords, x, y, 0xFFFFFFFF
            );
            y += 12;
        }

        if (on("Keystrokes")) {
            int boxX = 8;
            int boxY = CLIENT.getWindow().getScaledHeight() - 72;
            drawKey(ctx, "W", boxX + 22, boxY,
                CLIENT.options.forwardKey.isPressed());
            drawKey(ctx, "A", boxX, boxY + 22,
                CLIENT.options.leftKey.isPressed());
            drawKey(ctx, "S", boxX + 22, boxY + 22,
                CLIENT.options.backKey.isPressed());
            drawKey(ctx, "D", boxX + 44, boxY + 22,
                CLIENT.options.rightKey.isPressed());
            drawKey(ctx, "JUMP", boxX, boxY + 44,
                CLIENT.options.jumpKey.isPressed(), 66);
        }

        if (on("Armor HUD")) {
            int armorX = CLIENT.getWindow().getScaledWidth() - 80;
            int armorY = 8;

            for (int i = 0; i < CLIENT.player.getInventory().armor.size(); i++) {
                var stack = CLIENT.player.getInventory().armor.get(i);
                if (!stack.isEmpty()) {
                    ctx.drawItem(stack, armorX, armorY + i * 20);
                }
            }
        }

        if (on("Crosshair")) {
            int cx = CLIENT.getWindow().getScaledWidth() / 2;
            int cy = CLIENT.getWindow().getScaledHeight() / 2;
            ctx.fill(cx - 5, cy, cx + 6, cy + 1, 0xFFFFFFFF);
            ctx.fill(cx, cy - 5, cx + 1, cy + 6, 0xFFFFFFFF);
        }
    }

    private static void drawKey(DrawContext ctx, String label,
                                int x, int y, boolean pressed) {
        drawKey(ctx, label, x, y, pressed, 20);
    }

    private static void drawKey(DrawContext ctx, String label,
                                int x, int y, boolean pressed, int w) {
        int color = pressed ? 0xFF7966FF : 0xFF202944;
        ctx.fill(x, y, x + w, y + 20, color);
        ctx.drawCenteredTextWithShadow(
            CLIENT.textRenderer, label, x + w / 2, y + 6, 0xFFFFFFFF
        );
    }

    private static void restoreGamma() {
        if (gammaSaved) {
            CLIENT.options.getGamma().setValue(previousGamma);
            gammaSaved = false;
        }
    }
}
