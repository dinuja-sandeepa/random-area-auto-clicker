package com.autoclicker;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Random;

/**
 * Service that handles the background thread for automatic clicking.
 */
public class AutoClickerService {

    private Robot robot;
    private Thread workerThread;
    private volatile boolean running = false;
    private final MainFrame mainFrame;
    private final Random random;

    private double minDelaySeconds;
    private double maxDelaySeconds;
    private Rectangle area;

    public AutoClickerService(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.random = new Random();
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            // Handle error, typically Robot is supported on desktop environments.
        }
    }

    /**
     * Updates the clicking parameters.
     */
    public void setParameters(double minDelay, double maxDelay, Rectangle area) {
        this.minDelaySeconds = minDelay;
        this.maxDelaySeconds = maxDelay;
        this.area = area;
    }

    /**
     * Starts the auto-clicking process in a background thread.
     */
    public void start() {
        if (running) return;
        if (robot == null) return;

        running = true;
        mainFrame.onServiceStarted();

        workerThread = new Thread(() -> {
            int clickCount = 0;
            try {
                while (running) {
                    // Calculate random delay
                    double randomDelay = minDelaySeconds + (maxDelaySeconds - minDelaySeconds) * random.nextDouble();
                    long delayMillis = (long) (randomDelay * 1000);
                    
                    // Notify UI about next delay
                    mainFrame.onNextDelayScheduled(randomDelay);

                    // Sleep in smaller increments to allow quick stopping
                    long timeElapsed = 0;
                    long increment = 100; // 100ms chunks
                    while (timeElapsed < delayMillis) {
                        if (!running) break;
                        long sleepTime = Math.min(increment, delayMillis - timeElapsed);
                        Thread.sleep(sleepTime);
                        timeElapsed += sleepTime;
                        
                        // Update countdown
                        double remaining = (delayMillis - timeElapsed) / 1000.0;
                        mainFrame.onCountdownTick(remaining);
                    }

                    if (!running) break;

                    // Generate random point inside area
                    int x = area.x + random.nextInt(area.width > 0 ? area.width : 1);
                    int y = area.y + random.nextInt(area.height > 0 ? area.height : 1);

                    // Move mouse and click
                    robot.mouseMove(x, y);
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                    clickCount++;
                    mainFrame.onClickPerformed(clickCount, x, y);
                }
            } catch (InterruptedException e) {
                // Thread interrupted
                Thread.currentThread().interrupt();
            } finally {
                running = false;
                mainFrame.onServiceStopped();
            }
        });
        
        workerThread.setDaemon(true);
        workerThread.start();
    }

    /**
     * Stops the auto-clicking process.
     */
    public void stop() {
        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    public boolean isRunning() {
        return running;
    }
}
