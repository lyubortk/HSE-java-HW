package ru.hse.lyubortk.test5;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PairsLogicTest {
    private PairsLogic logic = new PairsLogic(2 * 2, new Random(0));
    private boolean[] open = new boolean[4];

    @Test
    void testWin() {
        boolean[] finished = new boolean[1];
        logic.setOnGameOverListener(() -> finished[0] = true);
        logic.pickCard(0);
        logic.pickCard(3);
        logic.pickCard(1);

        assertFalse(finished[0]);

        logic.pickCard(2);

        assertTrue(finished[0]);
    }

    @Test
    void testLose() {
        boolean[] finished = new boolean[1];
        logic.setOnGameOverListener(() -> finished[0] = true);
        logic.pickCard(0);
        logic.pickCard(1);
        logic.pickCard(2);
        logic.pickCard(3);

        assertFalse(finished[0]);
    }

    @Test
    void testOpen() {
        logic.setOnCardOpenListener((index, text) -> open[index] = true);
        logic.setOnCardCloseListener(index -> open[index] = false);

        logic.pickCard(0);
        assertTrue(open[0]);
        logic.pickCard(3);
        assertTrue(open[3]);

        assertFalse(open[1]);
        assertFalse(open[2]);

        logic.pickCard(1);
        logic.pickCard(2);

        assertTrue(open[0]);
        assertTrue(open[3]);
        assertTrue(open[1]);
        assertTrue(open[2]);
    }

    @Test
    void testWrongArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> new PairsLogic(0, new Random(0)));
        assertThrows(IllegalArgumentException.class,
                () -> new PairsLogic(5, new Random(0)));
        assertThrows(IllegalArgumentException.class,
                () -> new PairsLogic(14 * 14 + 1, new Random(0)));
    }

    @Test
    void closesAfterDelay() {
        logic.setOnCardOpenListener((index, text) -> open[index] = true);
        logic.setOnCardCloseListener(index -> open[index] = false);
        Runnable[] delayedClose = new Runnable[1];
        logic.setOnDelayedActionCreator((delay, runnable) -> delayedClose[0] = runnable);

        logic.pickCard(0);
        logic.pickCard(1);

        assertTrue(open[0]);
        assertTrue(open[1]);
        assertFalse(open[2]);
        assertFalse(open[3]);

        delayedClose[0].run();

        assertFalse(open[0]);
        assertFalse(open[1]);
        assertFalse(open[2]);
        assertFalse(open[3]);
    }

    @Test
    void closesImmediatelyIfNeeded() {
        logic.setOnCardOpenListener((index, text) -> open[index] = true);
        logic.setOnCardCloseListener(index -> open[index] = false);
        logic.setOnDelayedActionCreator((delay, runnable) -> (runnable));

        logic.pickCard(0);
        logic.pickCard(1);

        assertTrue(open[0]);
        assertTrue(open[1]);
        assertFalse(open[2]);
        assertFalse(open[3]);

        logic.pickCard(2);

        assertFalse(open[0]);
        assertFalse(open[1]);
        assertFalse(open[2]);
        assertFalse(open[3]);
    }
}