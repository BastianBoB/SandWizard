package com.basti_bob.sand_wizard.cell_properties;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.basti_bob.sand_wizard.world.World;

import java.util.ArrayList;
import java.util.List;

public class CellColors {


    public static final CellColors EMPTY = new CellColors(c(35, 35, 35)); //c(255, 255, 255)); //hC(0x080808));
    public static final CellColors STONE = new CellColors(c(85, 85, 85), c(75, 71, 68), c(70, 70, 70), c(61, 55, 51), c(51, 44, 40));
    public static final CellColors GRASS = new CellColors(c(19, 109, 21), c(38, 139, 7), c(65, 152, 10));
    public static final CellColors ICE = new CellColors(c(255, 255, 255), c(185, 232, 234), c(134, 214, 216), c(63, 208, 212), c(32, 195, 208));
    public static final CellColors SUMMER_LEAF = new CellColors(c(119, 163, 122), c(95, 146, 106), c(88, 126, 96));
    public static final CellColors SPRING_LEAF = new CellColors(c(197, 227, 175), c(154, 195, 123), c(114, 162, 78), c(84, 134, 46));
    public static final CellColors COMPACT_SNOW = new CellColors(c(240, 240, 240), c(235, 235, 235), c(245, 245, 245), c(230, 230, 230), c(250, 250, 250));
    public static final CellColors WOOD = new CellColors(hC(0x6E470B), hC(0x785115), hC(0x643D10));
    public static final CellColors RED_WOOD = new CellColors(c(70, 31, 0), c(80, 41, 0), c(90, 51, 0));
    public static final CellColors SAND = new CellColors(hC(0xB8860C), hC(0xE7B744), hC(0xF1D581));
    public static final CellColors DIRT = new CellColors(hC(0xA0522D), hC(0x8B4513), hC(0xD2691E));
    public static final CellColors COAL = new CellColors(hC(0x363232), hC(0x292929), hC(0x393939));
    public static final CellColors GRAVEL = new CellColors(hC(0x4c4c4c), hC(0x666666), hC(0x7f7f7f));
    public static final CellColors POWDER_SNOW = new CellColors(c(255, 255, 255), c(245, 245, 245), c(250, 250, 250), c(240, 240, 240), c(255, 250, 250));
    public static final CellColors WATER = new CellColors(c(30, 125, 200), c(35, 137, 218), c(25, 115, 185));
    public static final CellColors OIL = new CellColors(hC(0x0E0F0E), hC(0x0B0C0B), hC(0x121312));
    public static final CellColors ACID = new CellColors(hC(0x9BE60F), hC(0xBFFF28), hC(0xAffA19));
    public static final CellColors FIRE = new CellColors(hC(0xFF8800), hC(0xFF2200), hC(0xFFFF00), hC(0xFFFF00), hC(0xFF2200), hC(0xFFFF00), hC(0xFF8800), hC(0xFFFF00), hC(0xFFFFFF));
    public static final CellColors STEAM = new CellColors(hC(0xC7D5E0), hC(0xC7D5E0), hC(0xFFFFFF));
    public static final CellColors METHANE = new CellColors(hC(0xFFFFFF), hC(0xDDDDFF), hC(0xEEEEFF));

    private final List<Color> colors;
    public CellColors(Color... colors) {
        this.colors = List.of(colors);
    }

    public Color getColor(World world, int cellX, int cellY) {
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
