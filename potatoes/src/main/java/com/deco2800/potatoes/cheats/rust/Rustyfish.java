package com.deco2800.potatoes.cheats.rust;

import com.badlogic.gdx.Gdx;
import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;

public class Rustyfish {

    private interface RLibrary extends Library {
        RLibrary INSTANCE = (RLibrary) Native.loadLibrary("rustyfish", RLibrary.class);

        void startGame(Callback startDraw, Callback endDraw,
                       Callback updateWindow, Callback getWindowInfo,
                       Callback drawSprite);
    }


    /**
     * Starts a draw batch
     */
    private static Callback startDraw = new Callback() {
        @SuppressWarnings("unused")
        public void run() {
            System.out.println("Start draw");
        }
    };

    /**
     * Ends the current draw batch
     */
    private static Callback endDraw = new Callback() {
        @SuppressWarnings("unused")
        public void run() {
            System.out.println("End draw");
        }
    };

    /**
     * Updates the window, checking for resize events, key events etc.
     *
     * Places key information inside STRUCT TODO
     */
    private static Callback updateWindow = new Callback() {
        @SuppressWarnings("unused")
        public void run() {
            System.out.println("Update window");
        }
    };

    /**
     * Get's the window information and places it inside the info object
     */
    private static Callback getWindowInfo = new Callback() {
        @SuppressWarnings("unused")
        public void run(RenderInfo.ByReference info) {
            info.sizeX = Gdx.graphics.getWidth();
            info.sizeY = Gdx.graphics.getHeight();

            System.out.println(info);
        }
    };

    /**
     * Draw's a given sprite with
     *
     * name
     * x
     * y
     * etc... TODO
     */
    private static Callback drawSprite = new Callback() {
        @SuppressWarnings("unused")
        public void run() {
            System.out.println("Draw sprite");
        }
    };

    public static void run() {
        RLibrary.INSTANCE.startGame(startDraw, endDraw, updateWindow, getWindowInfo, drawSprite);
    }
}
