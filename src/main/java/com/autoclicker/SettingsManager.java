package com.autoclicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Manages saving and loading user settings using a local properties file.
 */
public class SettingsManager {
    
    private static final String CONFIG_FILE = "config.properties";
    private Properties properties;

    public SettingsManager() {
        properties = new Properties();
        loadSettings();
    }

    /**
     * Loads settings from the config file, if it exists.
     */
    public void loadSettings() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                properties.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves current properties to the config file.
     */
    public void saveSettings() {
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            properties.store(out, "Random Area Auto Clicker Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Property Getters and Setters

    public String getMinDelay() {
        return properties.getProperty("minDelay", "1.0");
    }

    public void setMinDelay(String minDelay) {
        properties.setProperty("minDelay", minDelay);
    }

    public String getMaxDelay() {
        return properties.getProperty("maxDelay", "5.0");
    }

    public void setMaxDelay(String maxDelay) {
        properties.setProperty("maxDelay", maxDelay);
    }

    public int getAreaX() {
        return Integer.parseInt(properties.getProperty("areaX", "0"));
    }

    public void setAreaX(int x) {
        properties.setProperty("areaX", String.valueOf(x));
    }

    public int getAreaY() {
        return Integer.parseInt(properties.getProperty("areaY", "0"));
    }

    public void setAreaY(int y) {
        properties.setProperty("areaY", String.valueOf(y));
    }

    public int getAreaWidth() {
        return Integer.parseInt(properties.getProperty("areaWidth", "0"));
    }

    public void setAreaWidth(int width) {
        properties.setProperty("areaWidth", String.valueOf(width));
    }

    public int getAreaHeight() {
        return Integer.parseInt(properties.getProperty("areaHeight", "0"));
    }

    public void setAreaHeight(int height) {
        properties.setProperty("areaHeight", String.valueOf(height));
    }

    /**
     * Helper to check if a valid area was previously saved.
     */
    public boolean hasSavedArea() {
        return getAreaWidth() > 0 && getAreaHeight() > 0;
    }
}
