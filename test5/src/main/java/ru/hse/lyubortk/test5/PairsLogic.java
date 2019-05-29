package ru.hse.lyubortk.test5;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class PairsLogic {
    public static final int MAX_SIZE = 14 * 14;
    public static final int OPEN_DELAY_MILLIS = 1500;

    private int size;
    private int foundMatches;
    private boolean[] isOpen;
    private int[] values;

    //callbacks to control GUI
    private BiConsumer<Integer, String> onCardOpenListener;
    private Consumer<Integer> onCardCloseListener;
    private Runnable onGameOverListener;
    private BiFunction<Integer, Runnable, Runnable> onDelayedActionCreator;
    private Runnable delayedActionImmediateExecutor;

    private GameStatus status = GameStatus.PICK_FIRST_CARD;
    private int firstCard;

    private enum GameStatus {
        PICK_FIRST_CARD,
        PICK_SECOND_CARD,
        GAME_OVER
    }

    public PairsLogic(int size, @NotNull Random random) {
        if (size <= 0 || size > MAX_SIZE || size % 2 == 1) {
            throw new IllegalArgumentException("Wrong size");
        }
        this.size = size;

        isOpen = new boolean[size];
        for (int i = 0; i < size; i++) {
            isOpen[i] = false;
        }

        var indexes = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes, random);

        values = new int[size];
        for (int i = 0; i < size; i++) {
            values[indexes.get(i)] = i / 2 + 1;
        }
    }

    public void setOnCardOpenListener(BiConsumer<Integer, String> onCardOpenListener) {
        this.onCardOpenListener = onCardOpenListener;
    }

    public void setOnCardCloseListener(Consumer<Integer> onCardCloseListener) {
        this.onCardCloseListener = onCardCloseListener;
    }

    public void setOnGameOverListener(Runnable onGameOverListener) {
        this.onGameOverListener = onGameOverListener;
    }

    public void setOnDelayedActionCreator(BiFunction<Integer, Runnable, Runnable> onDelayedActionCreator) {
        this.onDelayedActionCreator = onDelayedActionCreator;
    }

    public void pickCard(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException();
        }
        if (isOpen[index] || status == GameStatus.GAME_OVER) {
            return;
        }

        isOpen[index] = true;
        if (onCardOpenListener != null) {
            onCardOpenListener.accept(index, Integer.toString(values[index]));
        }

        if (status == GameStatus.PICK_FIRST_CARD) {
            if (delayedActionImmediateExecutor != null) {
                delayedActionImmediateExecutor.run();
                delayedActionImmediateExecutor = null;
            }
            firstCard = index;
            status = GameStatus.PICK_SECOND_CARD;
        } else if (values[firstCard] == values[index]) {
            foundMatches++;
            if (size / 2 == foundMatches) {
                status = GameStatus.GAME_OVER;
                if (onGameOverListener != null) {
                    onGameOverListener.run();
                }
            } else {
                status = GameStatus.PICK_FIRST_CARD;
            }
        } else {
            status = GameStatus.PICK_FIRST_CARD;
            final int previousCard = firstCard;
            Runnable closeRunnable = (() -> {
                if (onCardCloseListener != null) {
                    isOpen[index] = false;
                    isOpen[previousCard] = false;
                    onCardCloseListener.accept(index);
                    onCardCloseListener.accept(previousCard);
                }
            });
            if (onDelayedActionCreator != null) {
                delayedActionImmediateExecutor =
                        onDelayedActionCreator.apply(OPEN_DELAY_MILLIS, closeRunnable);
            }
        }
    }
}
