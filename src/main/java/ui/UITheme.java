package ui;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Centralized styling and theme management for the application.
 * Provides modern, fun UI with gradient backgrounds and custom colors.
 */
public class UITheme {

    // Primary Color Palette
    public static final Color PRIMARY_BLUE = new Color(66, 135, 245);      // Bright blue
    public static final Color SECONDARY_PURPLE = new Color(142, 68, 173);   // Purple accent
    public static final Color ACCENT_CYAN = new Color(52, 211, 153);        // Teal/Cyan
    public static final Color DARK_BG = new Color(26, 32, 46);              // Dark navy
    public static final Color LIGHT_BG = new Color(240, 244, 250);          // Light blue-gray
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color TEXT_DARK = new Color(33, 41, 60);            // Dark text
    public static final Color TEXT_LIGHT = new Color(200, 210, 226);        // Light text
    public static final Color SUCCESS_GREEN = new Color(34, 197, 94);       // Green
    public static final Color ERROR_RED = new Color(239, 68, 68);           // Red
    public static final Color WARNING_YELLOW = new Color(251, 146, 60);     // Orange

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 10);

    /**
     * Create a styled button with modern appearance
     */
    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(darken(bgColor, 0.2f));
                } else if (getModel().isArmed() || getModel().isRollover()) {
                    g2.setColor(lighten(bgColor, 0.1f));
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                super.paintComponent(g);
            }
        };
        button.setFont(BUTTON_FONT);
        button.setForeground(WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 45));

        return button;
    }

    /**
     * Create a primary action button
     */
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY_BLUE);
    }

    /**
     * Create a secondary action button
     */
    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, SECONDARY_PURPLE);
    }

    /**
     * Create a success action button
     */
    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS_GREEN);
    }

    /**
     * Create a danger/back button
     */
    public static JButton createDangerButton(String text) {
        return createStyledButton(text, ERROR_RED);
    }

    /**
     * Create a gradient panel background
     */
    public static JPanel createGradientPanel(Color color1, Color color2) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                    0, 0, color1,
                    0, getHeight(), color2
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    /**
     * Create a styled header panel
     */
    public static JPanel createHeaderPanel(String title) {
        JPanel panel = createGradientPanel(PRIMARY_BLUE, SECONDARY_PURPLE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 100));

        JLabel label = new JLabel(title);
        label.setFont(TITLE_FONT);
        label.setForeground(WHITE);
        panel.add(label, BorderLayout.WEST);

        return panel;
    }

    /**
     * Create a styled text field
     */
    public static JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(REGULAR_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        field.setBackground(WHITE);
        field.setForeground(TEXT_DARK);
        return field;
    }

    /**
     * Create a styled label
     */
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(REGULAR_FONT);
        label.setForeground(TEXT_DARK);
        return label;
    }

    /**
     * Create a styled heading label
     */
    public static JLabel createHeadingLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADING_FONT);
        label.setForeground(PRIMARY_BLUE);
        return label;
    }

    /**
     * Create a button panel with proper spacing
     */
    public static JPanel createButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        for (JButton btn : buttons) {
            panel.add(btn);
        }
        return panel;
    }

    /**
     * Create a content panel with padding
     */
    public static JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    /**
     * Lighten a color
     */
    private static Color lighten(Color color, float factor) {
        int r = Math.min((int) (color.getRed() + 255 * factor), 255);
        int g = Math.min((int) (color.getGreen() + 255 * factor), 255);
        int b = Math.min((int) (color.getBlue() + 255 * factor), 255);
        return new Color(r, g, b);
    }

    /**
     * Darken a color
     */
    private static Color darken(Color color, float factor) {
        int r = Math.max((int) (color.getRed() * (1 - factor)), 0);
        int g = Math.max((int) (color.getGreen() * (1 - factor)), 0);
        int b = Math.max((int) (color.getBlue() * (1 - factor)), 0);
        return new Color(r, g, b);
    }
}

