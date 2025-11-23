package blackjack.gui;

import blackjack.logic.BlackjackGame;
import blackjack.io.SaveManager;
import blackjack.model.Card;
import blackjack.model.Player;
import blackjack.logic.RoundResult;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Swing-based graphical user interface (View) for the Blackjack game.
 * It handles displaying the game state, user input (Hit/Stand), and menu actions (Save/Load/New Game).
 */
public class GameFrame extends JFrame {
    // The main game logic/model
    private BlackjackGame game;

    // Logger for debugging and error messages
    private static final Logger LOGGER = Logger.getLogger(GameFrame.class.getName());
    
    // UI components
    private JPanel dealerPanel;
    private JPanel playerPanel;
    private JLabel dealerScoreLabel;
    private JLabel playerScoreLabel;
    private JLabel statusLabel;
    private JButton hitButton;
    private JButton standButton;
    private StatisticsFrame statsWindow;

    // Constants for repeated strings
    private static final String DECK_SIZE_TITLE = "Deck Size";
    private static final String ERROR_TITLE = "Error";
    private static final String GAME_OVER_TITLE = "Game Over";
    private static final String DEFAULT_PLAYER_NAME = "Player";

    /**
     * Constructs the main game window. Prompts the user for a player name and initializes the UI.
     */
    public GameFrame() {
        // We initialize UI first so the main frame exists (even if invisible) as a parent
        initUI();
        
        // Now we show the setup dialogs ON TOP of everything
        createNewGame();
        
        // Finally update the UI with the new game data
        updateUI();
    }

    /**
     * Initializes all Swing components, sets layout, dimensions, and adds event listeners.
     */
    private void initUI() {
        setTitle("Blackjack - 21");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Icon setup ---
        loadApplicationIcon();

        // --- Menu ---
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // --- Info Panel (North) ---
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        dealerScoreLabel = new JLabel("Dealer score: ?", SwingConstants.CENTER);
        playerScoreLabel = new JLabel("Player score: ?", SwingConstants.CENTER);
        statusLabel = new JLabel("Game status: Starting...", SwingConstants.CENTER);

        Font scoreFont = new Font("SansSerif", Font.BOLD, 14);
        dealerScoreLabel.setFont(scoreFont);
        playerScoreLabel.setFont(scoreFont);
        statusLabel.setFont(scoreFont);

        infoPanel.add(dealerScoreLabel);
        infoPanel.add(playerScoreLabel);
        infoPanel.add(statusLabel);

        add(infoPanel, BorderLayout.NORTH);

        // --- Game Panel (Center) ---
        JPanel gamePanel = new JPanel(new GridLayout(2, 1, 10, 10));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Dealer Panel (Game Panel ---> Top)
        dealerPanel = new JPanel();
        dealerPanel.setBorder(BorderFactory.createTitledBorder("Dealer Cards"));
        dealerPanel.setBackground(new Color(20, 100, 50)); // Green table
        gamePanel.add(dealerPanel);

        // Player Panel (Game Panel ---> Bottom)
        playerPanel = new JPanel();
        playerPanel.setBorder(BorderFactory.createTitledBorder("Player Cards"));
        playerPanel.setBackground(new Color(20, 100, 50));
        gamePanel.add(playerPanel);

        add(gamePanel, BorderLayout.CENTER);

        // --- Control Panel (South) ---
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");

        // Button event handlers
        hitButton.addActionListener(e -> handleHit());
        standButton.addActionListener(e -> handleStand());

        controlPanel.add(hitButton);
        controlPanel.add(standButton);
        add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads the application icon from resources.
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
            LOGGER.log(Level.WARNING, "Error loading icon: {0}", e.getMessage());
        }
    }

    /**
     * Sets the taskbar icon if supported by the OS (e.g., macOS).
     * @param image The image to set.
     */
    private void setTaskbarIcon(Image image) {
        if (Taskbar.isTaskbarSupported()) {
            try {
                Taskbar.getTaskbar().setIconImage(image);
            } catch (UnsupportedOperationException e) {
                // System tray not supported on this platform, ignore quietly
                LOGGER.log(Level.FINE, "Taskbar icon not supported: {0}", e.getMessage());
            } catch (SecurityException e) {
                LOGGER.log(Level.WARNING, "Security exception setting taskbar icon: {0}", e.getMessage());
            }
        }
    }

    /**
     * Creates a new game instance using custom JDialogs to ensure they appear Always-On-Top.
     */
    private void createNewGame() {
        // 1. Player Name Input with Always-On-Top
        JOptionPane namePane = new JOptionPane(
            "Please enter your name!",
            JOptionPane.PLAIN_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            null,
            null);
        namePane.setWantsInput(true);
        namePane.setInitialSelectionValue(DEFAULT_PLAYER_NAME);
        
        onTop(namePane, DECK_SIZE_TITLE, ABORT);
        
        Object nameInput = namePane.getInputValue();
        String playerName = (nameInput != null && nameInput != JOptionPane.UNINITIALIZED_VALUE) ? (String) nameInput : DEFAULT_PLAYER_NAME;
        if (playerName.trim().isEmpty())
            playerName = DEFAULT_PLAYER_NAME;

        // 2. Deck Size Selection with Always-On-Top
        Object[] options = {"1 deck", "2 decks"};
        JOptionPane deckPane = new JOptionPane(
            "How many decks would you like to play with?", 
            JOptionPane.QUESTION_MESSAGE, 
            JOptionPane.YES_NO_OPTION, 
            null, 
            options, 
            options[0]
        );

        onTop(deckPane, "Game settings", JOptionPane.QUESTION_MESSAGE);

        Object selectedValue = deckPane.getValue();
        int deckCount = 1;
        if (selectedValue != null && selectedValue.equals(options[1])) {
            deckCount = 2;
        }
        
        this.game = new BlackjackGame(playerName, deckCount);
    }

    /**
     * Displays a JOptionPane dialog that is always on top of other windows.
     * @param panel The JOptionPane to display.
     * @param title The title of the dialog.
     * @param messageType The type of message (e.g., INFORMATION_MESSAGE).
     */
    public void onTop(JOptionPane panel, String title, int messageType) {
        JDialog dialog = panel.createDialog(this, title);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        dialog.dispose();
    }

    /**
     * Creates and configures the JMenuBar with New Game, Save, Load, and Exit items.
     * @return The configured JMenuBar.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Menu");
        JMenuItem newGame = new JMenuItem("New Game");
        JMenuItem deckSizeItem = new JMenuItem(DECK_SIZE_TITLE);
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem loadItem = new JMenuItem("Load");
        JMenuItem exitItem = new JMenuItem("Exit");

        JMenu statsMenu = new JMenu("Statistics");
        JMenuItem statsItem = new JMenuItem("Last (max 10) rounds statistics");
        
        // Event handling for menu items
        newGame.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            game.startNewRound();
            updateUI(); 
        }));

        statsItem.addActionListener(e -> showStatistics());
        deckSizeItem.addActionListener(e -> selectDeckSize());
        saveItem.addActionListener(e -> saveGame());
        loadItem.addActionListener(e -> loadGame());
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(newGame);
        gameMenu.add(deckSizeItem);
        gameMenu.addSeparator();
        gameMenu.add(saveItem);
        gameMenu.add(loadItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        menuBar.add(gameMenu);
        
        statsMenu.add(statsItem);
        menuBar.add(statsMenu);

        return menuBar;
    }

    /**
     * Displays a message dialog that is always on top of other windows.
     * @param message The message to display.
     * @param title The title of the dialog.
     * @param messageType The type of message (e.g., INFORMATION_MESSAGE).
     */
    private void showAlwaysOnTopMessage(String message, String title, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType);
        JDialog dialog = pane.createDialog(this, title);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        dialog.dispose();
    }
    
    /**
     * Handles the player's 'Hit' action, dealing a card and updating the UI.
     * If the game ends (player busts), displays the result message.
     */
    private void handleHit() {
        game.playerHit();
        updateUI();
        
        if (game.isGameOver()) {
            showAlwaysOnTopMessage(game.getGameResult(), GAME_OVER_TITLE, JOptionPane.INFORMATION_MESSAGE);
            setControls(false); // Disable buttons
        }
    }

    /**
     * Handles the player's 'Stand' action, initiating the Dealer's turn and ending the game.
     * Displays the final result message.
     */
    private void handleStand() {
        game.playerStand();
        updateUI();
        
        JOptionPane.showMessageDialog(this, game.getGameResult(), GAME_OVER_TITLE, JOptionPane.INFORMATION_MESSAGE);
        setControls(false); // Disable buttons
    }

    /**
     * Updates the entire GUI state (cards, scores, controls) based on the current BlackjackGame model.
     */
    private void updateUI() {
        if(game == null)
            return;

        // Update Player and Dealer hands
        // Dealer's second card is hidden until the game is over
        displayHand(dealerPanel, game.getDealer(), !game.isPlayerTurn() || game.isGameOver());
        displayHand(playerPanel, game.getPlayer(), true); 

        // Update scores and status
        updateScores();
        
        // Enable/disable buttons based on turn and game state
        setControls(game.isPlayerTurn() && !game.isGameOver());

        // Update the player panel title if the player name was loaded
        ((javax.swing.border.TitledBorder)playerPanel.getBorder()).setTitle(game.getPlayer().getName() + " Cards");
        
        // Ensure the layout manager recalculates and repaints for the new cards
        revalidate();
        repaint();
    }
    
    /**
     * Clears a JPanel and draws the current set of cards for a player.
     * This method handles hiding the Dealer's second card if the game is in progress.
     * @param panel The JPanel where the cards will be displayed (Dealer or Player panel).
     * @param player The Player or Dealer whose hand is displayed.
     * @param showAll If true, all cards are shown; otherwise, only the first card is visible (for the Dealer).
     */
    private void displayHand(JPanel panel, Player player, boolean showAll) {
        panel.removeAll();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        List<Card> hand = player.getHand();
        int cardsToShow;
        if (showAll) {
            cardsToShow = hand.size();
        } else if (hand.isEmpty()) {
            cardsToShow = 0;
        } else {
            cardsToShow = 1;
        }

        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            String cardText = (i < cardsToShow || showAll) ? card.toString() : "Back";
            
            JLabel cardLabel = new JLabel(cardText, SwingConstants.CENTER);
            cardLabel.setPreferredSize(new Dimension(80, 110));
            cardLabel.setOpaque(true);
            
            Color textColor = (card.getSuit() == blackjack.model.Suit.HEARTS || card.getSuit() == blackjack.model.Suit.DIAMONDS) ? Color.RED : Color.BLACK;

            if (i < cardsToShow || showAll) {
                cardLabel.setBackground(Color.WHITE);
                cardLabel.setForeground(textColor);
                cardLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                cardLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
            } else {
                cardLabel.setBackground(new Color(0, 51, 153));
                cardLabel.setForeground(Color.YELLOW);
                cardLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                cardLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
                cardLabel.setText("BLACKJACK");
            }
            
            panel.add(cardLabel);
        }
    }
    
    /**
     * Updates the score labels and the general status message at the top of the frame.
     */
    private void updateScores() {
        // Dealer's score is only shown when the game is over
        String dealerScore = game.isGameOver() ? String.valueOf(game.getDealer().getScore()) : "?";
        dealerScoreLabel.setText("Dealer score: " + dealerScore);
        
        // Player's score is always shown
        playerScoreLabel.setText(game.getPlayer().getName() + " score: " + game.getPlayer().getScore());
        
        // Update status message
        statusLabel.setText("Game status: " + getStatusMessage());
    }

    /**
     * Generates a status message based on the current game state.
     * @return A string representing the current status of the game.
     */
    private String getStatusMessage() {
        if (game.isGameOver())
            return game.getGameResult();
        return game.isPlayerTurn() ? "Your turn!" : "Dealer's turn!";
    }
    
    /**
     * Enables or disables the Hit and Stand buttons.
     * @param enabled true to enable the buttons, false to disable them.
     */
    private void setControls(boolean enabled) {
        hitButton.setEnabled(enabled);
        standButton.setEnabled(enabled);
    }

    /**
     * Checks if the history contains at least 3 rounds and displays the statistics window (JList).
     * Otherwise, shows a warning message.
     */
    private void showStatistics() {
        List<RoundResult> history = game.getResultsHistory();
        if (history.size() < 3) {
            showAlwaysOnTopMessage(
                "At least 3 rounds are required to view statistics. Current number of rounds: " + history.size(),
                "Missing Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (statsWindow != null)
            statsWindow.dispose();

        StatisticsFrame statsFrame = new StatisticsFrame(this, history);
        statsFrame.setAlwaysOnTop(true);
        statsFrame.setVisible(true);

        this.statsWindow = statsFrame;
    }

    /**
     * The user to select the number of decks (1 or 2) and updates the game model accordingly.
     * Restarts the current round with the new deck size.
     */
    private void selectDeckSize() {
        Object[] options = {"1 deck", "2 decks"};
        int current = Math.clamp(game.getNumberOfDecks(), 1, 2);
        int defaultIndex = (current == 2) ? 1 : 0;

        JOptionPane pane = new JOptionPane(
            "Select number of decks (current: " + current + "):",
            JOptionPane.QUESTION_MESSAGE,
            JOptionPane.YES_NO_OPTION,
            null,
            options,
            options[defaultIndex]
        );
        
        onTop(pane, DECK_SIZE_TITLE, defaultIndex);
        
        Object selectedValue = pane.getValue();
        if (selectedValue == null) return;

        int selectedDecks = selectedValue.equals(options[1]) ? 2 : 1;
        game.setNumberOfDecks(selectedDecks);

        // Apply the change to the game model
        game.setNumberOfDecks(selectedDecks);

        showAlwaysOnTopMessage("Deck size set to " + selectedDecks + " deck(s).", DECK_SIZE_TITLE, JOptionPane.INFORMATION_MESSAGE);
        game.startNewRound();
        updateUI();
    }

    /**
     * Initiates the game saving process using SaveManager and displays the result to the user.
     */
    private void saveGame() {
        try {
            SaveManager.saveGame(game);
            // Using File().getAbsolutePath() to show the user where the file was saved
            showAlwaysOnTopMessage("Game state saved to: " + new File("saves/gamestate.dat").getAbsolutePath(), "Save successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showAlwaysOnTopMessage("Error during saving: " + e.getMessage(), ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initiates the game loading process using SaveManager, replaces the current game state, and updates the UI.
     */
    private void loadGame() {
        try {
            BlackjackGame loadedGame = SaveManager.loadGame();
            this.game = loadedGame;
        
            if (statsWindow != null && statsWindow.isVisible()) {
                statsWindow.dispose();
                statsWindow = null;
            
                showAlwaysOnTopMessage(
                    "Game state loaded! Statistics window was closed to reflect new data. Please reopen it.", 
                    "Loading successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                showAlwaysOnTopMessage("Game state loaded!", "Loading successful", JOptionPane.INFORMATION_MESSAGE);
            }
        
            setTitle("Blackjack - 21 (" + game.getPlayer().getName() + ")");
            setControls(!game.isGameOver()); 
            updateUI();
        } catch (FileNotFoundException e) {
            showAlwaysOnTopMessage("Saved file not found at 'saves/gamestate.dat'.", ERROR_TITLE, JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            showAlwaysOnTopMessage("Error during loading: " + e.getMessage(), ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}