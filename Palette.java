import java.awt.Color;

/**
 * Catalog of the CSS color names the simulator accepts as symbols.
 *
 * The Canvas of the shapes package only knows seven color names. A
 * machine with n wheels needs n DIFFERENT symbols (n can be up to 50
 * in the marathon problem), so this class keeps a list of 50 CSS
 * colors together with their RGB value. The Canvas asks this class
 * for the color it does not know how to paint by itself.
 *
 * @author Jhazael and Santiago
 * @version 1.0 (Cycle 3 - 2026-2)
 */
public class Palette {

    // Names of the colors. The first seven are the ones the Canvas
    // already knew from the previous cycles.
    private static final String[] NAMES = {
        "red", "black", "blue", "yellow", "green", "magenta", "white",
        "orange", "purple", "pink", "brown", "cyan", "gray", "navy",
        "teal", "olive", "maroon", "gold", "coral", "salmon", "tomato",
        "crimson", "orchid", "plum", "violet", "indigo", "khaki", "tan",
        "wheat", "beige", "lavender", "turquoise", "skyblue", "steelblue",
        "royalblue", "dodgerblue", "deepskyblue", "cadetblue", "seagreen",
        "forestgreen", "limegreen", "springgreen", "chartreuse",
        "yellowgreen", "olivedrab", "sienna", "chocolate", "peru",
        "hotpink", "deeppink"
    };

    // RGB value (0xRRGGBB) of each color, in the same order as NAMES.
    private static final int[] RGB = {
        0xFF0000, 0x000000, 0x0000FF, 0xFFFF00, 0x00FF00, 0xFF00FF, 0xFFFFFF,
        0xFFA500, 0x800080, 0xFFC0CB, 0xA52A2A, 0x00FFFF, 0x808080, 0x000080,
        0x008080, 0x808000, 0x800000, 0xFFD700, 0xFF7F50, 0xFA8072, 0xFF6347,
        0xDC143C, 0xDA70D6, 0xDDA0DD, 0xEE82EE, 0x4B0082, 0xF0E68C, 0xD2B48C,
        0xF5DEB3, 0xF5F5DC, 0xE6E6FA, 0x40E0D0, 0x87CEEB, 0x4682B4,
        0x4169E1, 0x1E90FF, 0x00BFFF, 0x5F9EA0, 0x2E8B57,
        0x228B22, 0x32CD32, 0x00FF7F, 0x7FFF00,
        0x9ACD32, 0x6B8E23, 0xA0522D, 0xD2691E, 0xCD853F,
        0xFF69B4, 0xFF1493
    };

    /**
     * @return how many different colors the palette has.
     */
    public static int size() {
        return NAMES.length;
    }

    /**
     * @param index position of the color in the palette (starts at 0).
     * @return the name of the color at that position, or null if the
     * index is outside the palette.
     */
    public static String colorAt(int index) {
        if (index < 0 || index >= NAMES.length) {
            return null;
        }
        return NAMES[index];
    }

    /**
     * @param name CSS color name to look for.
     * @return true if the palette contains a color with that name.
     */
    public static boolean isValid(String name) {
        return indexOf(name) >= 0;
    }

    /**
     * Translates a color name into a java.awt.Color so the Canvas can
     * paint it. Unknown names are painted black, which is what the
     * Canvas did before.
     *
     * @param name CSS color name.
     * @return the matching Color, or black if the name is unknown.
     */
    public static Color toColor(String name) {
        int index = indexOf(name);
        if (index < 0) {
            return Color.black;
        }
        return new Color(RGB[index]);
    }

    /**
     * Looks up a name in the list.
     *
     * @param name color name searched.
     * @return its position in the list, or -1 if it is not there.
     */
    private static int indexOf(String name) {
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
