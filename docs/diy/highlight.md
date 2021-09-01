# Code Highlight

<style>
    .color-lump {
        width: 25px;
        height: 25px;
        border: 3px solid white;
        border-radius: 2px;
        background-color: var(--color);
    }
</style>

Use [hightlight.js](https://github.com/highlightjs/highlight.js/) to add code highlighting to the generated html document

- The user configures style in the smart-doc configuration file (optional)

- Configure the specified value (95 types are optional), but some light backgrounds have conflicts with the template colors, smart-doc has been removed.

- If you like highlighting, please refer to the list below to set up a highlight you like. Random generation is not recommended, because each highlight needs to be automatically downloaded online.

The configurable values of style are as follows:

| Style | Color |description
|-------|-------|------
|randomLight|Random light color|The value of style is randomLight will randomly generate a light background (not recommended)
|randomDar|Random light color|style value randomDar will randomly generate a dark background (not recommended)
|a11y-dark|#2b2b2b|<div class="color-lump" style="--color: #2b2b2b">
|agate|#333|<div class="color-lump" style="--color: #333">
|an-old-hope|#1c1d21|<div class="color-lump" style="--color: #1c1d21">
|androidstudio|#282b2e|<div class="color-lump" style="--color: #282b2e">
|arta|#222|<div class="color-lump" style="--color: #222">
|atelier-cave-dark|#19171c|<div class="color-lump" style="--color: #19171c">
|atelier-cave-light|#efecf4|<div class="color-lump" style="--color: #efecf4">
|atelier-dune-dark|#20201d|<div class="color-lump" style="--color: #20201d">
|atelier-dune-light|#fefbec|<div class="color-lump" style="--color: #fefbec">
|atelier-estuary-dark|#22221b|<div class="color-lump" style="--color: #22221b">
|atelier-estuary-light|#f4f3ec|<div class="color-lump" style="--color: #f4f3ec">
|atelier-forest-dark|#1b1918|<div class="color-lump" style="--color: #1b1918">
|atelier-forest-light|#f1efee|<div class="color-lump" style="--color: #f1efee">
|atelier-heath-dark|#1b181b|<div class="color-lump" style="--color: #1b181b">
|atelier-heath-light|#f7f3f7|<div class="color-lump" style="--color: #f7f3f7">
|atelier-lakeside-dark|#161b1d|<div class="color-lump" style="--color: #161b1d">
|atelier-lakeside-light|#ebf8ff|<div class="color-lump" style="--color: #ebf8ff">
|atelier-plateau-dark|#1b1818|<div class="color-lump" style="--color: #1b1818">
|atelier-plateau-light|#f4ecec|<div class="color-lump" style="--color: #f4ecec">
|atelier-savanna-dark|#171c19|<div class="color-lump" style="--color: #171c19">
|atelier-savanna-light|#ecf4ee|<div class="color-lump" style="--color: #ecf4ee">
|atelier-seaside-dark|#131513|<div class="color-lump" style="--color: #131513">
|atelier-seaside-light|#f4fbf4|<div class="color-lump" style="--color: #f4fbf4">
|atelier-sulphurpool-dark|#202746|<div class="color-lump" style="--color: #202746">
|atelier-sulphurpool-light|#f5f7ff|<div class="color-lump" style="--color: #f5f7ff">
|atom-one-dark|#282c34|<div class="color-lump" style="--color: #282c34">
|atom-one-dark-reasonable|#282c34|<div class="color-lump" style="--color: #282c34">
|atom-one-light|#fafafa|<div class="color-lump" style="--color: #fafafa">
|codepen-embed|#222|<div class="color-lump" style="--color: #222">
|darcula|#2b2b2b|<div class="color-lump" style="--color: #2b2b2b">
|dark|#444|<div class="color-lump" style="--color: #444">
|default|#F0F0F0|<div class="color-lump" style="--color: #F0F0F0">
|docco|#f8f8ff|<div class="color-lump" style="--color: #f8f8ff">
|dracula|#282a36|<div class="color-lump" style="--color: #282a36">
|far|#000080|<div class="color-lump" style="--color: #000080">
|foundation|#eee|<div class="color-lump" style="--color: #eee">
|github|#f8f8f8|<div class="color-lump" style="--color: #f8f8f8">
|gml|#222222|<div class="color-lump" style="--color: #222222">
|gradient-dark|linear-gradient(166deg, rgba(80,31,122,1) 0%, rgba(40,32,179,1) 80%)|<div class="color-lump" style="--color: linear-gradient(166deg, rgba(80,31,122,1) 0%, rgba(40,32,179,1) 80%)">
|gruvbox-dark|#282828|<div class="color-lump" style="--color: #282828">
|gruvbox-light|#fbf1c7|<div class="color-lump" style="--color: #fbf1c7">
|hopscotch|#322931|<div class="color-lump" style="--color: #322931">
|hybrid|#1d1f21|<div class="color-lump" style="--color: #1d1f21">
|ir-black|#000|<div class="color-lump" style="--color: #000">
|isbl-editor-dark|#404040|<div class="color-lump" style="--color: #404040">
|kimbie.dark|#221a0f|<div class="color-lump" style="--color: #221a0f">
|kimbie.light|#fbebd4|<div class="color-lump" style="--color: #fbebd4">
|lioshi|#303030|<div class="color-lump" style="--color: #303030">
|magula|#f4f4f4|<div class="color-lump" style="--color: #f4f4f4">
|mono-blue|#eaeef3|<div class="color-lump" style="--color: #eaeef3">
|monokai|#272822|<div class="color-lump" style="--color: #272822">
|monokai-sublime|#23241f|<div class="color-lump" style="--color: #23241f">
|night-owl|#011627|<div class="color-lump" style="--color: #011627">
|nnfx-dark|#333|<div class="color-lump" style="--color: #333">
|nord|#2E3440|<div class="color-lump" style="--color: #2E3440">
|obsidian|#282b2e|<div class="color-lump" style="--color: #282b2e">
|ocean|#2b303b|<div class="color-lump" style="--color: #2b303b">
|paraiso-dark|#2f1e2e|<div class="color-lump" style="--color: #2f1e2e">
|paraiso-light|#e7e9db|<div class="color-lump" style="--color: #e7e9db">
|purebasic|#FFFFDF|<div class="color-lump" style="--color: #FFFFDF">
|qtcreator_dark|#000000|<div class="color-lump" style="--color: #000000">
|railscasts|#232323|<div class="color-lump" style="--color: #232323">
|rainbow|#474949|<div class="color-lump" style="--color: #474949">
|routeros|#f0f0f0|<div class="color-lump" style="--color: #f0f0f0">
|shades-of-purple|#2d2b57|<div class="color-lump" style="--color: #2d2b57">
|solarized-dark|#002b36|<div class="color-lump" style="--color: #002b36">
|solarized-light|#fdf6e3|<div class="color-lump" style="--color: #fdf6e3">
|srcery|#1C1B19|<div class="color-lump" style="--color: #1C1B19">
|sunburst|#000|<div class="color-lump" style="--color: #000">
|tomorrow-night|#1d1f21|<div class="color-lump" style="--color: #1d1f21">
|tomorrow-night-blue|#002451|<div class="color-lump" style="--color: #002451">
|tomorrow-night-bright|black|<div class="color-lump" style="--color: black">
|tomorrow-night-eighties|#2d2d2d|<div class="color-lump" style="--color: #2d2d2d">
|vs2015|#1E1E1E|<div class="color-lump" style="--color: #1E1E1E">
|xt256|#000|<div class="color-lump" style="--color: #000">
|zenburn|#3f3f3f|<div class="color-lump" style="--color: #3f3f3f">

