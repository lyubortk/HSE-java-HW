package ru.hse.lyubortk.cannon;

/**
 * Game launcher for fat-jars (launcher must not extend Application class and therefore
 * CannonGameGui could not be used for this purpose).
 */
public class CannonGameLauncher {
    /** Launches the game. */
    public static void main(String[] args) {
        CannonGameGUI.main(args);
    }
}
