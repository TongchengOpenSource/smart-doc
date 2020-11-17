package com.power.doc.constants;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author jitmit 2020/11/16
 */
public class HighlightStyle {

    public static final String DEFAULT_STYLE = "github";

    private static final List<String> DARK_STYLE;

    private static final List<String> LIGHT_STYLE;

    static {
        LIGHT_STYLE = Arrays.asList(
                DEFAULT_STYLE,
                "a11y-light",
                "arduino-light",
                "ascetic",
                "atelier-cave-light",
                "atelier-dune-light",
                "atelier-estuary-light",
                "atelier-forest-light",
                "atelier-heath-light",
                "atelier-lakeside-light",
                "atelier-plateau-light",
                "atelier-savanna-light",
                "atelier-seaside-light",
                "atelier-sulphurpool-light",
                "atom-one-light",
                "color-brewer",
                "docco",
                "github-gist",
                "googlecode",
                "grayscale",
                "gruvbox-light",
                "idea",
                "isbl-editor-light",
                "kimbie.light",
                "lightfair",
                "magula",
                "mono-blue",
                "nnfx",
                "paraiso-light",
                "purebasic",
                "qtcreator_light",
                "routeros",
                "school-book",
                "solarized-light",
                "tomorrow",
                "vs",
                "xcode"
        );

    }

    static {
        DARK_STYLE = Arrays.asList(
                "a11y-dark",
                "agate",
                "an-old-hope",
                "androidstudio",
                "arta",
                "atelier-cave-dark",
                "atelier-dune-dark",
                "atelier-estuary-dark",
                "atelier-forest-dark",
                "atelier-heath-dark",
                "atelier-lakeside-dark",
                "atelier-plateau-dark",
                "atelier-savanna-dark",
                "atelier-seaside-dark",
                "atelier-sulphurpool-dark",
                "atom-one-dark-reasonable",
                "atom-one-dark",
                "brown-paper",
                "codepen-embed",
                "darcula",
                "dark",
                "default",
                "dracula",
                "far",
                "foundation",
                "gml",
                "gradient-dark",
                "gruvbox-dark",
                "hopscotch",
                "hybrid",
                "ir-black",
                "isbl-editor-dark",
                "kimbie.dark",
                "lioshi",
                "monokai",
                "monokai-sublime",
                "night-owl",
                "nnfx-dark",
                "nord",
                "obsidian",
                "ocean",
                "paraiso-dark",
                "pojoaque",
                "qtcreator_dark",
                "railscasts",
                "rainbow",
                "shades-of-purple",
                "solarized-dark",
                "srcery", "sunburst",
                "tomorrow-night",
                "tomorrow-night-blue",
                "tomorrow-night-bright",
                "tomorrow-night-eighties",
                "vs2015",
                "xt256",
                "zenburn"
        );
    }


    /**
     * 随机一个 light style
     *
     * @param random
     * @return
     */
    public static String randomLight(Random random) {
        return LIGHT_STYLE.get(random.nextInt(LIGHT_STYLE.size()));
    }

    /**
     * 随机一个 dark style
     *
     * @param random
     * @return
     */
    public static String randomDark(Random random) {
        return DARK_STYLE.get(random.nextInt(DARK_STYLE.size()));
    }

    /**
     * 随机一个 style
     *
     * @param random
     * @return
     */
    public static String randomAll(Random random) {
        if (random.nextBoolean()) {
            return randomLight(random);
        } else {
            return randomDark(random);
        }
    }

    /**
     * |
     * 高亮样式是否存在
     *
     * @param style 高亮样式
     * @return
     */
    public static boolean containStyle(String style) {
        return LIGHT_STYLE.contains(style) || DARK_STYLE.contains(style) ? true : false;
    }
}
