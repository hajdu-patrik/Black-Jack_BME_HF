package blackjack.gui;

import blackjack.logic.BlackjackGame;
import blackjack.io.SaveManager;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    
    private BlackjackGame game;
    private JPanel mainPanel;
    private JLabel statusLabel;

    public GameFrame() {
        // Initialize the game (Model)
        this.game = new BlackjackGame("Player");
        
        initUI();
    }

    private void initUI() {
        setTitle("Blackjack - Java Swing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newItem = new JMenuItem("New Game");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem loadItem = new JMenuItem("Load");
        
        // Event handlers (Controller logic in the View, simplified)
        saveItem.addActionListener(e -> saveGame());
        loadItem.addActionListener(e -> loadGame());

        gameMenu.add(newItem);
        gameMenu.add(saveItem);
        gameMenu.add(loadItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        // Main panel
        mainPanel = new JPanel();
        statusLabel = new JLabel("Welcome to Blackjack!");
        mainPanel.add(statusLabel);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private void saveGame() {
        try {
            SaveManager.saveGame(game);
            JOptionPane.showMessageDialog(this, "Saved successfully!");
        } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage());
    }
}

private void loadGame() {
    try {
        this.game = SaveManager.loadGame();
            JOptionPane.showMessageDialog(this, "Game loaded!");
            // TODO: refresh the UI (repaint) here
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading: " + e.getMessage());
        }
    }
}