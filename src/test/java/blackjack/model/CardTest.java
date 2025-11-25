package blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the Card class properties and methods.
 */
class CardTest {
    
    /**
     * Verifies the correct point values for different card ranks.
     */
    @Test
    void testCardValues() {
        Card card = new Card(Suit.HEARTS, Rank.FIVE);
        assertEquals(5, card.getValue());
        
        Card faceCard = new Card(Suit.SPADES, Rank.KING);
        assertEquals(10, faceCard.getValue());
        
        Card ace = new Card(Suit.DIAMONDS, Rank.ACE);
        assertEquals(11, ace.getValue());
    }

    /**
     * Checks the string representation of a card.
     */
    @Test
    void testCardToString() {
        Card card = new Card(Suit.CLUBS, Rank.TEN);
        assertNotNull(card.toString());
        assertTrue(card.toString().contains("TEN"));
    }
}