package blackjack.logic;

import blackjack.model.Card;
import blackjack.model.Rank;
import blackjack.model.Suit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the core game flow logic and winner determination.
 */
class BlackjackGameTest {

    /**
     * Verifies the initial state of the game, including card counts and turn logic.
     */
    @Test
    void testGameInitialization() {
        BlackjackGame game = new BlackjackGame("Gamer", 1);
        
        assertEquals(4, game.getPlayer().getHand().size() + game.getDealer().getHand().size(), "Four cards must be dealt initially.");
        assertEquals(1, game.getNumberOfDecks(), "The number of decks should be 1.");

        if (game.getPlayer().getScore() == 21) {
            assertFalse(game.isPlayerTurn(), "If player is dealt a Blackjack (21), turn should pass automatically.");
        } else {
            assertTrue(game.isPlayerTurn(), "The game should start with the player's turn (if score < 21).");
        }

        assertFalse(game.isGameOver(), "The game should not be over initially (unless dealer also has blackjack).");
    }
    
    /**
     * Tests the scenario where the player's score exceeds 21, resulting in a loss.
     */
    @Test
    void testPlayerBusts() {
        BlackjackGame game = new BlackjackGame("BustTester", 1);
        // Set player hand to a high, non-bust score (e.g., 20)
        game.getPlayer().clearHand();
        game.getPlayer().addCard(new Card(Suit.HEARTS, Rank.TEN));
        game.getPlayer().addCard(new Card(Suit.HEARTS, Rank.TEN)); 
        
        // Force a hit that will logic flow check
        game.playerHit(); 
        
        if (game.getPlayer().getScore() > 21) {
            assertTrue(game.isGameOver(), "Game should be over if player busts.");
            assertTrue(game.getGameResult().contains("You lost"), "Player bust result message should indicate loss.");
        }
    }

    /**
     * Tests the scenario where the dealer's score exceeds 21, resulting in a player win.
     */
    @Test
    void testDealerBusts() {
        BlackjackGame game = new BlackjackGame("WinTester", 1);
        
        // Set player score high (e.g., 19) to win if dealer busts
        game.getPlayer().clearHand();
        game.getPlayer().addCard(new Card(Suit.HEARTS, Rank.TEN));
        game.getPlayer().addCard(new Card(Suit.HEARTS, Rank.NINE)); 
        
        // Set dealer score low to force hitting (e.g., 12). 
        game.getDealer().clearHand();
        game.getDealer().addCard(new Card(Suit.CLUBS, Rank.SEVEN));
        game.getDealer().addCard(new Card(Suit.CLUBS, Rank.FIVE)); 
        
        game.playerStand(); 
        
        if (game.getDealer().getScore() > 21) {
            assertTrue(game.isGameOver(), "Game should be over after stand.");
            assertTrue(game.getGameResult().contains("You won"), "Player should win if dealer busts.");
        }
    }

    /**
     * Tests the scenario where the player wins by having a higher score than the dealer without busting.
     */
    @Test
    void testPlayerWinsByScore() {
        BlackjackGame game = new BlackjackGame("Winner", 1);
        game.getPlayer().clearHand();
        game.getDealer().clearHand();
        
        // Player: 20
        game.getPlayer().addCard(new Card(Suit.HEARTS, Rank.TEN));
        game.getPlayer().addCard(new Card(Suit.HEARTS, Rank.TEN));
        
        // Dealer: 17 (Must stand)
        game.getDealer().addCard(new Card(Suit.CLUBS, Rank.TEN));
        game.getDealer().addCard(new Card(Suit.CLUBS, Rank.SEVEN));
        
        game.playerStand();
        
        assertTrue(game.getGameResult().contains("You won"), "Player should win with 20 against 17.");
    }
    
    /**
     * Verifies that completed rounds are correctly added to the results history.
     */
    @Test
    void testResultHistoryRecording() {
        BlackjackGame game = new BlackjackGame("HistoryTester", 1);
        
        // Simulate rounds
        game.startNewRound();
        game.playerStand(); 
        
        game.startNewRound();
        game.playerStand();
        
        assertTrue(game.getResultsHistory().size() >= 1, "History should record rounds.");
    }
}