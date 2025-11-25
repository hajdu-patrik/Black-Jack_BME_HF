package blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the Player class, focusing on hand management and Ace score logic.
 */
class PlayerTest {

    /**
     * Tests the score calculation for cards with fixed values.
     */
    @Test
    void testBasicScoreCalculation() {
        Player player = new Player("TestPlayer");
        player.addCard(new Card(Suit.HEARTS, Rank.TEN));
        player.addCard(new Card(Suit.DIAMONDS, Rank.FIVE));
        assertEquals(15, player.getScore());
    }

    /**
     * Tests that an Ace is counted as 11 when it does not cause a bust.
     */
    @Test
    void testSoftAceScoreCalculation() {
        Player player = new Player("TestPlayer");
        player.addCard(new Card(Suit.SPADES, Rank.ACE)); // 11
        player.addCard(new Card(Suit.DIAMONDS, Rank.SIX)); // 6
        assertEquals(17, player.getScore());
    }

    /**
     * Tests that an Ace is dynamically adjusted to 1 to prevent busting.
     */
    @Test
    void testHardAceScoreAdjustment() {
        Player player = new Player("TestPlayer");
        player.addCard(new Card(Suit.HEARTS, Rank.ACE)); // 11 -> 1
        player.addCard(new Card(Suit.CLUBS, Rank.TEN)); 
        player.addCard(new Card(Suit.DIAMONDS, Rank.EIGHT)); 
        // 1 + 10 + 8 = 19
        assertEquals(19, player.getScore());
    }
}