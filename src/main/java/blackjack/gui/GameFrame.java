package blackjack.gui;

import blackjack.logic.BlackjackGame;
import blackjack.io.SaveManager;
import blackjack.model.Card;
import blackjack.model.Player;
import blackjack.logic.RoundResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main application window for the Blackjack game.
 * It manages the navigation between different views (Menu, Game, Statistics)
 * and handles the interaction between the user interface and the game logic.
 */
public class GameFrame extends JFrame {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(GameFrame.class.getName());

    // View Identifiers
    private static final String VIEW_MENU = "Menu";
    private static final String VIEW_GAME = "Game";
    private static final String VIEW_STATS = "Statistics";

    // UI Constants and Resources
    private static final String DEFAULT_PLAYER_NAME = "Player";
    private static final String DECK_SIZE_TITLE = "Deck Size";
    private static final String BACK_TO_MENU_TEXT = "Back to Menu";
    private static final String PLAY_AGAIN_TEXT = "Play Again";
    private static final String SAVE_GAME_TEXT = "Save Game";
    private static final String SANS_SERIF_FONT = "SansSerif";

    // Game Data Model
    private BlackjackGame game;
    private BlackjackGame mainSessionGame;
    private int selectedDeckSize = 1;

    // Layout Components
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Game View UI Components
    private JPanel dealerPanel;
    private JPanel playerPanel;
    private JLabel dealerScoreLabel;
    private JLabel playerScoreLabel;
    private JLabel statusLabel;
    private JButton hitButton;
    private JButton standButton;
    private JButton saveButton;

    /**
     * Constructs the main game window, initializes the frame properties,
     * sets up the views, and displays the main menu.
     */
    public GameFrame() {
        initMainFrame();
        initViews();
        showView(VIEW_MENU);

        toFront();
        requestFocus();
    }

    /**
     * Initializes the main JFrame properties such as title, size, location, and layout manager.
     */
    private void initMainFrame() {
        setTitle("Blackjack Pro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        loadApplicationIcon();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        add(cardPanel);
    }

    /**
     * Initializes and adds the primary views (Menu and Game) to the CardLayout.
     */
    private void initViews() {
        cardPanel.add(createMenuPanel(), VIEW_MENU);
        cardPanel.add(createGamePanel(), VIEW_GAME);
    }

    /**
     * Creates the main menu panel containing navigation buttons and game settings.
     * @return The constructed JPanel for the menu view.
     */
    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(40, 44, 52));
        menuPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("♠️ Blackjack Pro");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setForeground(new Color(97, 218, 251));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome! Press Start to play as '" + DEFAULT_PLAYER_NAME + "'.");
        welcomeLabel.setFont(new Font(SANS_SERIF_FONT, Font.PLAIN, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = createStyledButton("Start Game", new Color(76, 175, 80));
        startButton.addActionListener(e -> startGame());

        JButton deckButton = createStyledButton(DECK_SIZE_TITLE, new Color(33, 150, 243));
        deckButton.addActionListener(e -> changeDeckSize());

        JButton statsButton = createStyledButton(VIEW_STATS, new Color(255, 152, 0));
        statsButton.addActionListener(e -> showStatistics());

        JButton loadButton = createStyledButton("Load Game", new Color(156, 39, 176));
        loadButton.addActionListener(e -> loadGame());

        JButton exitButton = createStyledButton("Exit", new Color(244, 67, 54));
        exitButton.addActionListener(e -> System.exit(0));

        menuPanel.add(titleLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(welcomeLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        menuPanel.add(startButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(deckButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(statsButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(loadButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(exitButton);

        return menuPanel;
    }

    /**
     * Creates a standardized, styled button with specific font, color, and size properties.
     * @param text The text to display on the button.
     * @param bgColor The background color of the button.
     * @return The configured JButton instance.
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font(SANS_SERIF_FONT, Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(250, 50));
        return btn;
    }

    /**
     * Creates the main game interface panel, including the score board, card table, and controls.
     * @return The constructed JPanel for the game view.
     */
    private JPanel createGamePanel() {
        JPanel gameContainer = new JPanel(new BorderLayout(10, 10));
        gameContainer.setBackground(new Color(20, 100, 50));

        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 10, 0, 10));

        dealerScoreLabel = new JLabel("Dealer score: ?", SwingConstants.CENTER);
        playerScoreLabel = new JLabel(DEFAULT_PLAYER_NAME + " score: 0", SwingConstants.CENTER);
        statusLabel = new JLabel("Starting...", SwingConstants.CENTER);

        styleLabel(dealerScoreLabel);
        styleLabel(playerScoreLabel);
        styleLabel(statusLabel);

        infoPanel.add(dealerScoreLabel);
        infoPanel.add(playerScoreLabel);
        infoPanel.add(statusLabel);

        gameContainer.add(infoPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new GridLayout(2, 1, 10, 10));
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        dealerPanel = new JPanel();
        dealerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Dealer's Hand"));
        dealerPanel.setOpaque(false);
        ((javax.swing.border.TitledBorder)dealerPanel.getBorder()).setTitleColor(Color.WHITE);

        playerPanel = new JPanel();
        playerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Your Hand"));
        playerPanel.setOpaque(false);
        ((javax.swing.border.TitledBorder)playerPanel.getBorder()).setTitleColor(Color.WHITE);

        tablePanel.add(dealerPanel);
        tablePanel.add(playerPanel);

        gameContainer.add(tablePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controlPanel.setBackground(new Color(10, 50, 25));

        hitButton = new JButton("HIT");
        standButton = new JButton("STAND");
        saveButton = new JButton(SAVE_GAME_TEXT);

        JButton backToMenuButton = new JButton(BACK_TO_MENU_TEXT);

        hitButton.addActionListener(e -> handleHit());
        standButton.addActionListener(e -> handleStand());
        saveButton.addActionListener(e -> saveGame());

        backToMenuButton.addActionListener(e -> returnToMenu());

        controlPanel.add(hitButton);
        controlPanel.add(standButton);
        controlPanel.add(saveButton);
        controlPanel.add(backToMenuButton);

        gameContainer.add(controlPanel, BorderLayout.SOUTH);

        return gameContainer;
    }

    /**
     * Applies standard styling to status and score labels.
     * @param label The JLabel to style.
     */
    private void styleLabel(JLabel label) {
        label.setFont(new Font(SANS_SERIF_FONT, Font.BOLD, 18));
        label.setForeground(Color.WHITE);
    }

    /**
     * Switches the active view in the CardLayout container.
     * @param viewName The name of the view to switch to.
     */
    private void showView(String viewName) {
        cardLayout.show(cardPanel, viewName);
    }

    /**
     * Returns the user to the main menu.
     * If a main session is active, it restores that session, discarding any temporary loaded game state.
     */
    private void returnToMenu() {
        if (this.mainSessionGame != null) {
            this.game = this.mainSessionGame;
            this.selectedDeckSize = this.mainSessionGame.getNumberOfDecks();
        }
        showView(VIEW_MENU);
    }

    /**
     * Starts a new game round.
     * If a main session already exists, it continues using it to preserve history.
     * Otherwise, it initializes a new main session.
     * Checks for immediate Blackjack after dealing.
     */
    private void startGame() {
        if (this.mainSessionGame == null) {
            this.mainSessionGame = new BlackjackGame(DEFAULT_PLAYER_NAME, selectedDeckSize);
        } else {
            this.mainSessionGame.setNumberOfDecks(selectedDeckSize);
            this.mainSessionGame.startNewRound();
        }

        this.game = this.mainSessionGame;

        showView(VIEW_GAME);
        updateUI();

        if (this.game.isGameOver()) {
            SwingUtilities.invokeLater(this::handleGameOver);
        }
    }

    /**
     * Opens a dialog allowing the user to select the number of card decks (1 or 2).
     */
    private void changeDeckSize() {
        Object[] options = {"1 deck", "2 decks"};
        int defaultIndex = (selectedDeckSize == 2) ? 1 : 0;

        int choice = JOptionPane.showOptionDialog(this,
            "Select deck size (Current: " + selectedDeckSize + ")",
            "Deck Configuration",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null, options, options[defaultIndex]);

        if (choice == 0) selectedDeckSize = 1;
        if (choice == 1) selectedDeckSize = 2;
    }

    /**
     * Handles the player's action to hit (draw a card).
     * Updates the UI and checks if the game is over (e.g., bust).
     */
    private void handleHit() {
        if (game == null) return;
        game.playerHit();
        updateUI();

        if (game.isGameOver()) {
            handleGameOver();
        }
    }

    /**
     * Handles the player's action to stand.
     * This ends the player's turn, triggers the dealer's turn, and ends the game.
     */
    private void handleStand() {
        if (game == null) return;
        game.playerStand();
        updateUI();
        handleGameOver();
    }

    /**
     * Displays the game over dialog with options to play again, save the game, or return to the menu.
     * If "Save Game" is selected, the dialog reappears after saving to allow further actions.
     */
    private void handleGameOver() {
        setGameControlsEnabled(false);
        String result = game.getGameResult();

        JOptionPane pane = new JOptionPane(result + "\nWhat would you like to do?",
            JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.YES_NO_CANCEL_OPTION,
            null,
            new Object[]{PLAY_AGAIN_TEXT, SAVE_GAME_TEXT, BACK_TO_MENU_TEXT},
            PLAY_AGAIN_TEXT);

        JDialog dialog = pane.createDialog(this, "Game Over");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);

        Object selectedValue = pane.getValue();

        if (PLAY_AGAIN_TEXT.equals(selectedValue)) {
            game.startNewRound();
            setGameControlsEnabled(true);
            updateUI();

            if (game.isGameOver()) {
                SwingUtilities.invokeLater(this::handleGameOver);
            }
        } else if (SAVE_GAME_TEXT.equals(selectedValue)) {
            saveGame();
            handleGameOver();
        } else if (BACK_TO_MENU_TEXT.equals(selectedValue)) {
            returnToMenu();
        }
    }

    /**
     * Enables or disables the in-game control buttons (Hit/Stand).
     * @param enabled True to enable controls, false to disable.
     */
    private void setGameControlsEnabled(boolean enabled) {
        hitButton.setEnabled(enabled);
        standButton.setEnabled(enabled);
        saveButton.setEnabled(true);
    }

    /**
     * Updates the game UI components (scores, hands, status) to reflect the current game state.
     */
    private void updateUI() {
        if (game == null) return;

        ((javax.swing.border.TitledBorder)playerPanel.getBorder()).setTitle(game.getPlayer().getName() + "'s Hand");

        boolean showAllDealerCards = !game.isPlayerTurn() || game.isGameOver();

        displayHand(dealerPanel, game.getDealer(), showAllDealerCards);
        displayHand(playerPanel, game.getPlayer(), true);

        String dScore = game.isGameOver() ? String.valueOf(game.getDealer().getScore()) : "?";
        dealerScoreLabel.setText("Dealer score: " + dScore);

        playerScoreLabel.setText(game.getPlayer().getName() + " score: " + game.getPlayer().getScore());

        if (game.isGameOver())
            statusLabel.setText(game.getGameResult());
        else
            statusLabel.setText(game.isPlayerTurn() ? "Your Turn!" : "Dealer's Turn!");

        setGameControlsEnabled(game.isPlayerTurn() && !game.isGameOver());

        repaint();
        revalidate();
    }

    /**
     * Renders the cards for a specific player in the specified panel.
     * @param panel The JPanel where cards should be drawn.
     * @param player The player (or dealer) whose hand is being displayed.
     * @param showAll If true, all cards are revealed; if false, the dealer's hole card is hidden.
     */
    private void displayHand(JPanel panel, Player player, boolean showAll) {
        panel.removeAll();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        List<Card> hand = player.getHand();

        int limit;
        if (showAll) {
            limit = hand.size();
        } else if (hand.isEmpty()) {
            limit = 0;
        } else {
            limit = 1;
        }

        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            boolean isVisible = (i < limit) || showAll;
            panel.add(createCardLabel(card, isVisible));
        }
    }

    /**
     * Creates a visual component representing a playing card.
     * @param card The card object containing rank and suit data.
     * @param visible Whether the card face is visible or if it's face down.
     * @return A JLabel representing the card.
     */
    private JLabel createCardLabel(Card card, boolean visible) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(100, 140));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, 20));

        if (visible) {
            label.setText("<html><center>" + card.toString().replace(" ", "<br>") + "</center></html>");
            label.setBackground(Color.WHITE);
            boolean isRed = card.getSuit() == blackjack.model.Suit.HEARTS || card.getSuit() == blackjack.model.Suit.DIAMONDS;
            label.setForeground(isRed ? new Color(200, 0, 0) : Color.BLACK);
        } else {
            label.setText("?");
            label.setBackground(new Color(139, 0, 0));
            label.setForeground(Color.WHITE);
        }
        return label;
    }

    /**
     * Displays the statistics view with the history of the last rounds.
     * Requires at least one played round to function.
     */
    private void showStatistics() {
        if (game == null) {
             JOptionPane.showMessageDialog(this, "No active game found. Please play a game first.", "Stats Info", JOptionPane.INFORMATION_MESSAGE);
             return;
        }

        List<RoundResult> history = game.getResultsHistory();

        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Play at least 1 round to view statistics.",
                "Missing Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel statsContainer = new JPanel(new BorderLayout());
        statsContainer.setBackground(new Color(40, 44, 52));

        JLabel header = new JLabel("Match History (Last 10)");
        header.setForeground(Color.WHITE);
        header.setFont(new Font(SANS_SERIF_FONT, Font.BOLD, 24));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setBorder(new EmptyBorder(20, 0, 20, 0));
        statsContainer.add(header, BorderLayout.NORTH);

        DefaultListModel<RoundResult> listModel = new DefaultListModel<>();
        listModel.addAll(history);

        JList<RoundResult> list = new JList<>(listModel);
        list.setCellRenderer(new RoundResultRenderer());
        list.setFont(new Font("Monospaced", Font.PLAIN, 14));

        list.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        RoundResult r = list.getModel().getElementAt(index);
                        showRoundDetails(r);
                    }
                }
            }
        });

        statsContainer.add(new JScrollPane(list), BorderLayout.CENTER);

        JButton backButton = createStyledButton(BACK_TO_MENU_TEXT, new Color(33, 150, 243));
        backButton.addActionListener(e -> returnToMenu());

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(backButton);
        btnPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        statsContainer.add(btnPanel, BorderLayout.SOUTH);

        cardPanel.add(statsContainer, VIEW_STATS);
        showView(VIEW_STATS);
    }

    /**
     * Shows detailed information for a specific round selected from the statistics list.
     * @param result The RoundResult object containing the details.
     */
    private void showRoundDetails(RoundResult result) {
        String details = String.format(
            "<html>" +
            "<h2>Round Result Details</h2>" +
            "<b>Winner:</b> %s<br><br>" +
            "<b>Player Score:</b> %d<br>" +
            "<b>Player Hand:</b> %s<br><br>" +
            "<b>Dealer Score:</b> %d<br>" +
            "<b>Dealer Hand:</b> %s" +
            "</html>",
            result.getWinner(),
            result.getPlayerScore(),
            String.join(", ", result.getPlayerHand()),
            result.getDealerScore(),
            String.join(", ", result.getDealerHand())
        );
        JOptionPane.showMessageDialog(this, details, "Round Details", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Custom renderer for the statistics JList.
     * Color-codes the rows based on the game result (Win/Loss/Tie).
     */
    private static class RoundResultRenderer extends DefaultListCellRenderer {
        
        /**
         * Returns a component configured to display the specified value.
         * Sets text and background color based on the round result.
         */
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof RoundResult result) {
                String winner = result.getWinner();
                String text = String.format("Round %d: %s | Player: %d | Dealer: %d",
                        index + 1,
                        winner,
                        result.getPlayerScore(),
                        result.getDealerScore()
                );
                renderer.setText(text);

                if (!isSelected) {
                    if (winner.contains(DEFAULT_PLAYER_NAME) || (result.getPlayerScore() <= 21 && result.getDealerScore() > 21)) {
                        renderer.setBackground(new Color(150, 255, 150));
                    } else if (winner.contains("Dealer") || result.getPlayerScore() > 21) {
                        renderer.setBackground(new Color(255, 150, 150));
                    } else {
                        renderer.setBackground(new Color(255, 255, 150));
                    }
                }
            }
            return renderer;
        }
    }

    /**
     * Saves the current game state using the SaveManager.
     * Displays a success or error message via a popup dialog.
     */
    private void saveGame() {
        if (game == null) {
            JOptionPane.showMessageDialog(this, "No active game to save!");
            return;
        }
        try {
            SaveManager.saveGame(game);
            JOptionPane.showMessageDialog(this, "Game state saved to: " + new File("saves/gamestate.dat").getAbsolutePath());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Save failed", e);
            JOptionPane.showMessageDialog(this, "Error saving game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads a game state from the file system.
     * The loaded game is treated as a temporary session.
     * The main session (if any) is preserved and restored upon returning to the menu.
     */
    private void loadGame() {
        try {
            BlackjackGame loadedGame = SaveManager.loadGame();

            this.game = loadedGame;
            this.selectedDeckSize = loadedGame.getNumberOfDecks();

            showView(VIEW_GAME);
            updateUI();
            JOptionPane.showMessageDialog(this, "Game Loaded (Temporary Session). Returning to menu will restore your main session.");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Load failed", e);
            JOptionPane.showMessageDialog(this, "Could not load game (File missing or corrupt).", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Loads the application icon from the resources folder and sets it as the window icon.
     */
    private void loadApplicationIcon() {
        try {
            java.net.URL iconURL = getClass().getResource("/icon.png");
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                setIconImage(icon.getImage());
                setTaskbarIcon(icon.getImage());
            }
        } catch (Exception e) {
            LOGGER.warning("Icon load failed");
        }
    }

    /**
     * Sets the application icon in the system taskbar (OS-dependent).
     * @param image The image to use as the icon.
     */
    private void setTaskbarIcon(Image image) {
        if (Taskbar.isTaskbarSupported()) {
            try {
                Taskbar.getTaskbar().setIconImage(image);
            } catch (Exception e) {
                // Ignore platform specific errors
            }
        }
    }
}