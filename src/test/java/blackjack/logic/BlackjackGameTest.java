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
     * Tests the game state at initialization.
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

        assertFalse(game.isGameOver(), "The game should not be over initially (unless dealer also has blackjack, but logic handles round start).");
    }
    
    /**
     * Tests the result when the Player busts (score > 21).
     */
    @Test
    void testPlayerBusts() {
        BlackjackGame game = new BlackjackGame("BustTester", 1);
        // Set player hand to a high, non-bust score (e.g., 20)
        game.getPlayer().clearHand();
        game.getPlayer().addCard(new Card(Suit.HEARTS, Rank.TEN));
        game.getPlayer().addCard(new Card(Suit.HEARTS, Rank.TEN)); 
        
        // Note: We cannot deterministically force a bust (if Ace is drawn), but we test the game flow logic
        game.playerHit(); 
        
        if (game.getPlayer().getScore() > 21) {
            assertTrue(game.isGameOver(), "Game should be over if player busts.");

            assertTrue(game.getGameResult().contains("You lost (You went over:"), "Player bust result message is incorrect.");
        }
    }

    /**
     * Tests the result when the Dealer busts.
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
            
            assertTrue(game.getGameResult().contains("You won (Dealer went over:"), "Player should win if dealer busts.");
        }
    }

    /**
     * Tests the result when the Player wins by higher score (Player > Dealer, both <= 21).
     */
    @Test
    void testPlayerWinsByScore() {
        BlackjackGame game = new BlackjackGame("Winner", 1);
        game.getPlayer().clearHand();
        game.getDealer().clearHand();
        
        // Player: 10 + 10 = 20
        game.getPlayer().addCard(new Card(Suit.HEARTS, Rank.TEN));
        game.getPlayer().addCard(new Card(Suit.HEARTS, Rank.TEN));
        
        // Dealer: 10 + 7 = 17 (forced stand)
        game.getDealer().addCard(new Card(Suit.CLUBS, Rank.TEN));
        game.getDealer().addCard(new Card(Suit.CLUBS, Rank.SEVEN));
        
        game.playerStand();
        
        assertEquals("You won!", game.getGameResult(), "Player should win on 20 vs 17.");
    }
    
    /**
     * Tests history recording after a round is complete.
     */
    @Test
    void testResultHistoryRecording() {
        BlackjackGame game = new BlackjackGame("HistoryTester", 1);
        
        // Simulate three rounds
        for (int i = 0; i < 3; i++) {
            game.startNewRound();
            game.playerStand(); 
        }
        
        assertEquals(3, game.getResultsHistory().size(), "History should contain 3 recorded rounds.");
    }
}