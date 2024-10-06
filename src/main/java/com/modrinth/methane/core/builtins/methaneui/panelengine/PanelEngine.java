package com.modrinth.methane.core.builtins.methaneui.panelengine;


public class PanelEngine {

    /// Panel Engine.
    /// Concept: Render the content we want to display in-game off-screen, so we can then present it as an in-game 'panel' window.
    /// If the player opens the panel with say `\`, then can press it again to clear it.
    /// If they're too far to realistically see it, pressing that keybind will bring it up again, and the player can then close it by pressing again.
    /// this is mainly intended for VR, but will drive rendering for everything regardless.

    public static PanelFramebuffer testFB = new PanelFramebuffer();

}
