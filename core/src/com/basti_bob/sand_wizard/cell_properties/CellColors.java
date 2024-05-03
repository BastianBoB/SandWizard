package com.basti_bob.sand_wizard.cell_properties;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.basti_bob.sand_wizard.world.World;

import java.util.ArrayList;
import java.util.List;

public class CellColors {


    public static final CellColors EMPTY = new CellColors(c(40, 40, 40)); //c(255, 255, 255)); //hC(0x080808));
    public static final CellColors STONE = new CellColors(c(85, 85, 85), c(75, 71, 68), c(70, 70, 70), c(61, 55, 51), c(51, 44, 40));
    public static final CellColors GRASS = new CellColors(c(19, 109, 21), c(38, 139, 7), c(65, 152, 10));
    public static final CellColors ICE = new CellColors(c(255, 255, 255), c(185, 232, 234), c(134, 214, 216), c(63, 208, 212), c(32, 195, 208));
    public static final CellColors SUMMER_LEAF = new CellColors(c(119, 163, 122), c(95, 146, 106), c(88, 126, 96));
    public static final CellColors SPRING_LEAF = new CellColors(c(154, 195, 123), c(114, 162, 78), c(84, 134, 46));
    public static final CellColors AUTUMN_LEAF = new CellColors(c(245, 206, 137), c(228, 163, 49), c(204, 128, 20), c(177, 121, 0), c(153, 97, 0));
    public static final CellColors COMPACT_SNOW = new CellColors(c(240, 240, 240), c(235, 235, 235), c(245, 245, 245), c(230, 230, 230), c(250, 250, 250));
    public static final CellColors WOOD = new CellColors(hC(0x6E470B), hC(0x785115), hC(0x643D10));
    public static final CellColors RED_WOOD = new CellColors(c(70, 31, 0), c(80, 41, 0), c(90, 51, 0));
    public static final CellColors IRON_ORE = new CellColors(c(105, 105, 105), c(128, 128, 128), c(169, 169, 169));
    public static final CellColors GRANITE = new CellColors(c(180, 120, 80), c(60, 40, 30), c(120, 80, 60));
    public static final CellColors ANDESITE = new CellColors(c(135, 135, 135), c(105, 105, 105), c(169, 169, 169));
    public static final CellColors BASALT = new CellColors(c(34, 34, 34), c(85, 85, 85), c(136, 136, 136));
    public static final CellColors MARBLE = new CellColors(c(255, 255, 255), c(245, 245, 245), c(230, 230, 230));
    public static final CellColors LIMESTONE = new CellColors(c(250, 250, 210), c(238, 232, 170), c(222, 184, 135));
    public static final CellColors DIORITE = new CellColors(c(220, 220, 220), c(192, 192, 192), c(169, 169, 169));
    public static final CellColors SHALE = new CellColors(c(105, 105, 105), c(128, 128, 128), c(169, 169, 169));
    public static final CellColors STALACTITE_LIGHT = new CellColors(c(150, 90, 70), c(162, 104, 84), c(179, 125, 97));
    public static final CellColors STALACTITE_DARK = new CellColors(c(98, 56, 42), c(120, 71, 50), c(139, 81, 61));


    public static final CellColors SAND = new CellColors(c(223, 190, 147), c(204, 168, 121), c(187, 156, 104));
    public static final CellColors FINE_SAND = new CellColors(c(255, 240, 180), c(255, 220, 140), c(255, 200, 100));
    public static final CellColors SAND_STONE = new CellColors(c(194, 158, 110), c(166, 134, 95), c(138, 111, 75));
    public static final CellColors DIRT = new CellColors(hC(0xA0522D), hC(0x8B4513), hC(0xD2691E));
    public static final CellColors COAL = new CellColors(hC(0x363232), hC(0x292929), hC(0x393939));
    public static final CellColors GRAVEL = new CellColors(hC(0x4c4c4c), hC(0x666666), hC(0x7f7f7f));
    public static final CellColors POWDER_SNOW = new CellColors(c(255, 255, 255), c(245, 245, 245), c(250, 250, 250), c(240, 240, 240), c(255, 250, 250));

    public static final CellColors WATER = new CellColors(c(30, 125, 200), c(35, 137, 218), c(25, 115, 185));
    public static final CellColors OIL = new CellColors(hC(0x0E0F0E), hC(0x0B0C0B), hC(0x121312));
    public static final CellColors ACID = new CellColors(hC(0x9BE60F), hC(0xBFFF28), hC(0xAffA19));
    public static final CellColors LAVA = new CellColors(c(255, 60, 0), c(255, 80, 0), c(255, 100, 0));

    public static final CellColors FIRE = new CellColors(hC(0xFF8800), hC(0xFF2200), hC(0xFFFF00), hC(0xFFFF00), hC(0xFF2200), hC(0xFFFF00), hC(0xFF8800), hC(0xFFFF00), hC(0xFFFFFF));
    public static final CellColors STEAM = new CellColors(hC(0xC7D5E0), hC(0xC7D5E0), hC(0xFFFFFF));
    public static final CellColors METHANE = new CellColors(hC(0xFFFFFF), hC(0xDDDDFF), hC(0xEEEEFF));

    public static final class FLOWER_PETAL {
        public static final CellColors RED = new CellColors(c(255, 51, 51), c(204, 0, 0), c(255, 0, 0));
        public static final CellColors PINK = new CellColors(c(255, 160, 200), c(255, 128, 192), c(255, 192, 203));
        public static final CellColors YELLOW = new CellColors(c(255, 255, 102), c(204, 204, 0), c(255, 255, 0));
        public static final CellColors ORANGE = new CellColors(c(255, 178, 102), c(255, 128, 0), c(255, 165, 0));
        public static final CellColors PURPLE = new CellColors(c(153, 51, 204), c(102, 0, 102), c(128, 0, 128));
        public static final CellColors WHITE = new CellColors(c(230, 230, 230), c(200, 200, 200), c(255, 255, 255));
        public static final CellColors BLUE = new CellColors(c(51, 51, 255), c(0, 0, 204), c(0, 0, 255));
        public static final CellColors LAVENDER = new CellColors(c(204, 204, 255), c(153, 153, 204), c(230, 230, 250));
        public static final CellColors PEACH = new CellColors(c(255, 204, 170), c(255, 187, 153), c(255, 218, 185));
        public static final CellColors LILAC = new CellColors(c(204, 153, 204), c(179, 102, 179), c(200, 162, 200));
        public static final CellColors MAGENTA = new CellColors(c(255, 51, 255), c(204, 0, 204), c(255, 0, 255));
        public static final CellColors CORAL = new CellColors(c(255, 153, 102), c(255, 102, 51), c(255, 127, 80));
        public static final CellColors CYAN = new CellColors(c(102, 255, 255), c(0, 204, 204), c(0, 255, 255));
        public static final CellColors GREEN = new CellColors(c(51, 153, 51), c(0, 102, 0), c(0, 128, 0));
        public static final CellColors BROWN = new CellColors(c(139, 69, 19), c(160, 82, 45), c(205, 133, 63));
    }

    private final List<Color> colors;

    public CellColors(Color... colors) {
        this.colors = List.of(colors);

//        for (Color color : colors) {
//            color.set((float) Math.pow(color.r, 2.2), (float) Math.pow(color.g, 2.2), (float) Math.pow(color.b, 2.2), color.a);
//        }
    }

    public Color getColor(World world) {
        int i = (int) (MathUtils.random() * colors.size());
        return colors.get(i);
    }

    public static Color hC(int hex) {
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);

        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

    public static Color c(int r, int g, int b) {
        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

}
