package com.autoclicker;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main GUI window for the Random Area Auto Clicker.
 * Improved modern UI version.
 */
public class MainFrame extends JFrame implements NativeKeyListener {

    private SettingsManager settingsManager;
    private AutoClickerService autoClickerService;
    private Rectangle selectedArea;

    private JTextField minDelayField;
    private JTextField maxDelayField;
    private JLabel statusLabel;
    private JLabel coordsLabel;
    private JLabel clickCountLabel;
    private JLabel lastClickPosLabel;
    private JLabel nextDelayLabel;
    private JLabel countdownLabel;
    private JButton startBtn;
    private JButton stopBtn;
    private JButton selectAreaBtn;

    // Colors
    private static final Color PRIMARY_GREEN = new Color(46, 204, 113);
    private static final Color PRIMARY_RED = new Color(231, 76, 60);
    private static final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private static final Color BG_COLOR = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(220, 225, 230);

    public MainFrame() {
        settingsManager = new SettingsManager();
        autoClickerService = new AutoClickerService(this);
        initUI();
        loadSettings();
        initGlobalHotkeys();
    }

    private void initUI() {
        setTitle("Random Area Auto Clicker");
        
        try {
            java.net.URL iconUrl = getClass().getResource("/icon.png");
            if (iconUrl != null) {
                setIconImage(Toolkit.getDefaultToolkit().getImage(iconUrl));
            } else {
                System.err.println("Icon file not found at /icon.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(440, 520);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 12));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // ========== HEADER / STATUS ==========
        JPanel headerPanel = createCardPanel();
        headerPanel.setLayout(new BorderLayout(10, 8));

        statusLabel = new JLabel("● Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setForeground(PRIMARY_BLUE);
        headerPanel.add(statusLabel, BorderLayout.WEST);

        JLabel hotkeyHint = new JLabel("F6 Start  •  F7 Stop");
        hotkeyHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hotkeyHint.setForeground(new Color(120, 130, 140));
        headerPanel.add(hotkeyHint, BorderLayout.EAST);

        // ========== SETTINGS CARD ==========
        JPanel settingsCard = createCardPanel();
        settingsCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel settingsTitle = new JLabel("Settings");
        settingsTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        settingsTitle.setForeground(new Color(80, 90, 100));
        settingsCard.add(settingsTitle, gbc);

        // Min Delay
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.4;
        settingsCard.add(createLabel("Min Delay (sec)"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        minDelayField = createTextField();
        minDelayField.setToolTipText("Minimum delay between clicks in seconds");
        settingsCard.add(minDelayField, gbc);

        // Max Delay
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0.4;
        settingsCard.add(createLabel("Max Delay (sec)"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        maxDelayField = createTextField();
        maxDelayField.setToolTipText("Maximum delay between clicks in seconds");
        settingsCard.add(maxDelayField, gbc);

        // Area Selection
        gbc.gridy = 3; gbc.gridx = 0; gbc.weightx = 0.4;
        settingsCard.add(createLabel("Click Area"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        selectAreaBtn = new JButton("Select Area");
        styleSecondaryButton(selectAreaBtn);
        selectAreaBtn.setToolTipText("Click and drag to select the area where clicks will occur");
        selectAreaBtn.addActionListener(e -> startAreaSelection());
        settingsCard.add(selectAreaBtn, gbc);

        // ========== INFO CARD ==========
        JPanel infoCard = createCardPanel();
        infoCard.setLayout(new GridLayout(5, 1, 0, 6));

        JLabel infoTitle = new JLabel("Live Status");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoTitle.setForeground(new Color(80, 90, 100));
        infoCard.add(infoTitle);

        coordsLabel = createInfoLabel("Area: None");
        clickCountLabel = createInfoLabel("Clicks: 0");
        lastClickPosLabel = createInfoLabel("Last Click: None");
        nextDelayLabel = createInfoLabel("Next Delay: N/A");
        countdownLabel = createInfoLabel("Countdown: N/A");

        infoCard.add(coordsLabel);
        infoCard.add(clickCountLabel);
        infoCard.add(lastClickPosLabel);
        infoCard.add(nextDelayLabel);
        infoCard.add(countdownLabel);

        // ========== CONTROL BUTTONS ==========
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        controlPanel.setOpaque(false);

        startBtn = new JButton("▶  Start (F6)");
        stylePrimaryButton(startBtn, PRIMARY_GREEN);
        startBtn.addActionListener(e -> startClicker());

        stopBtn = new JButton("■  Stop (F7)");
        stylePrimaryButton(stopBtn, PRIMARY_RED);
        stopBtn.setEnabled(false);
        stopBtn.addActionListener(e -> stopClicker());

        JButton resetBtn = new JButton("Reset");
        styleSecondaryButton(resetBtn);
        resetBtn.addActionListener(e -> resetSettings());

        controlPanel.add(startBtn);
        controlPanel.add(stopBtn);
        controlPanel.add(resetBtn);

        // Assemble
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(settingsCard);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(infoCard);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveSettings();
            }
        });
    }

    // ========== UI HELPERS ==========

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(70, 80, 90));
        return label;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(50, 60, 70));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        field.setPreferredSize(new Dimension(120, 32));
        return field;
    }

    private void stylePrimaryButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(130, 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setBackground(new Color(236, 240, 245));
        btn.setForeground(new Color(60, 70, 80));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        btn.setPreferredSize(new Dimension(110, 32));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ========== EXISTING LOGIC (unchanged) ==========

    private void loadSettings() {
        minDelayField.setText(settingsManager.getMinDelay());
        maxDelayField.setText(settingsManager.getMaxDelay());
        if (settingsManager.hasSavedArea()) {
            selectedArea = new Rectangle(
                    settingsManager.getAreaX(),
                    settingsManager.getAreaY(),
                    settingsManager.getAreaWidth(),
                    settingsManager.getAreaHeight()
            );
            updateCoordsLabel();
        }
    }

    private void saveSettings() {
        settingsManager.setMinDelay(minDelayField.getText());
        settingsManager.setMaxDelay(maxDelayField.getText());
        if (selectedArea != null) {
            settingsManager.setAreaX(selectedArea.x);
            settingsManager.setAreaY(selectedArea.y);
            settingsManager.setAreaWidth(selectedArea.width);
            settingsManager.setAreaHeight(selectedArea.height);
        }
        settingsManager.saveSettings();
    }

    private void resetSettings() {
        minDelayField.setText("1.0");
        maxDelayField.setText("5.0");
        selectedArea = null;
        updateCoordsLabel();
        clickCountLabel.setText("Clicks: 0");
        lastClickPosLabel.setText("Last Click: None");
        nextDelayLabel.setText("Next Delay: N/A");
        countdownLabel.setText("Countdown: N/A");
        statusLabel.setText("● Ready");
        statusLabel.setForeground(PRIMARY_BLUE);
    }

    private void startAreaSelection() {
        AreaSelector selector = new AreaSelector(this);
        selector.startSelection();
    }

    public void onAreaSelected(Rectangle area) {
        if (area != null) {
            this.selectedArea = area;
            updateCoordsLabel();
        }
    }

    private void updateCoordsLabel() {
        if (selectedArea != null) {
            coordsLabel.setText(String.format("Area: X:%d  Y:%d  W:%d  H:%d",
                    selectedArea.x, selectedArea.y, selectedArea.width, selectedArea.height));
        } else {
            coordsLabel.setText("Area: None");
        }
    }

    private void startClicker() {
        if (autoClickerService.isRunning()) return;

        if (selectedArea == null) {
            JOptionPane.showMessageDialog(this, "Please select an area first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double minDelay = Double.parseDouble(minDelayField.getText());
            double maxDelay = Double.parseDouble(maxDelayField.getText());
            if (minDelay <= 0 || maxDelay <= 0) {
                JOptionPane.showMessageDialog(this, "Delays must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (minDelay > maxDelay) {
                JOptionPane.showMessageDialog(this, "Min Delay cannot be greater than Max Delay.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            autoClickerService.setParameters(minDelay, maxDelay, selectedArea);
            autoClickerService.start();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for delays.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopClicker() {
        if (autoClickerService.isRunning()) {
            autoClickerService.stop();
        }
    }

    // ========== CALLBACKS ==========

    public void onServiceStarted() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("● Running");
            statusLabel.setForeground(PRIMARY_GREEN);
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            minDelayField.setEnabled(false);
            maxDelayField.setEnabled(false);
            selectAreaBtn.setEnabled(false);
        });
    }

    public void onServiceStopped() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("● Stopped");
            statusLabel.setForeground(PRIMARY_RED);
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            minDelayField.setEnabled(true);
            maxDelayField.setEnabled(true);
            selectAreaBtn.setEnabled(true);
            countdownLabel.setText("Countdown: N/A");
            nextDelayLabel.setText("Next Delay: N/A");
        });
    }

    public void onNextDelayScheduled(double delaySeconds) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("● Waiting...");
            statusLabel.setForeground(new Color(241, 196, 15)); // yellow/orange
            nextDelayLabel.setText(String.format("Next Delay: %.2f sec", delaySeconds));
        });
    }

    public void onCountdownTick(double remainingSeconds) {
        SwingUtilities.invokeLater(() -> {
            countdownLabel.setText(String.format("Countdown: %.2f sec", remainingSeconds));
        });
    }

    public void onClickPerformed(int count, int x, int y) {
        SwingUtilities.invokeLater(() -> {
            clickCountLabel.setText("Clicks: " + count);
            lastClickPosLabel.setText(String.format("Last Click: X:%d, Y:%d", x, y));
        });
    }

    // ========== HOTKEYS ==========

    private void initGlobalHotkeys() {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            logger.setUseParentHandlers(false);

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            ex.printStackTrace();
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F6) {
            SwingUtilities.invokeLater(this::startClicker);
        } else if (e.getKeyCode() == NativeKeyEvent.VC_F7) {
            SwingUtilities.invokeLater(this::stopClicker);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {}

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}