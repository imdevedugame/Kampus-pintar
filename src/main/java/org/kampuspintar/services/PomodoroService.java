package org.kampuspintar.services;

import javafx.application.Platform;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PomodoroService {
    private Timer timer;
    private int secondsLeft;
    private int currentSession;
    private boolean isRunning;
    private boolean isPaused;
    
    private BiConsumer<Integer, Integer> onTimeUpdate;
    private Consumer<Integer> onSessionComplete;
    
    private static final int WORK_DURATION = 25 * 60; // 25 minutes
    private static final int SHORT_BREAK = 5 * 60;   // 5 minutes
    private static final int LONG_BREAK = 15 * 60;   // 15 minutes
    
    public PomodoroService() {
        reset();
    }
    
    public void start() {
        if (isPaused) {
            resume();
            return;
        }
        
        isRunning = true;
        isPaused = false;
        timer = new Timer();
        
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isRunning && !isPaused) {
                    secondsLeft--;
                    
                    Platform.runLater(() -> {
                        if (onTimeUpdate != null) {
                            onTimeUpdate.accept(secondsLeft, currentSession);
                        }
                    });
                    
                    if (secondsLeft <= 0) {
                        completeSession();
                    }
                }
            }
        }, 1000, 1000);
    }
    
    public void pause() {
        isPaused = true;
    }
    
    public void resume() {
        isPaused = false;
    }
    
    public void reset() {
        if (timer != null) {
            timer.cancel();
        }
        isRunning = false;
        isPaused = false;
        currentSession = 1;
        secondsLeft = WORK_DURATION;
    }
    
    private void completeSession() {
        timer.cancel();
        isRunning = false;
        
        Platform.runLater(() -> {
            if (onSessionComplete != null) {
                onSessionComplete.accept(currentSession);
            }
        });
        
        currentSession++;
        
        // Set next session duration
        if (currentSession % 4 == 1) {
            secondsLeft = LONG_BREAK; // Long break after 4 sessions
        } else if (currentSession % 2 == 1) {
            secondsLeft = WORK_DURATION; // Work session
        } else {
            secondsLeft = SHORT_BREAK; // Short break
        }
    }
    
    public void setOnTimeUpdate(BiConsumer<Integer, Integer> callback) {
        this.onTimeUpdate = callback;
    }
    
    public void setOnSessionComplete(Consumer<Integer> callback) {
        this.onSessionComplete = callback;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public int getCurrentSession() {
        return currentSession;
    }
    
    public int getSecondsLeft() {
        return secondsLeft;
    }
}
