package blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the Deck functionality, including initialization and dealing.
 */
class DeckTest {

    /**
     * Verifies that a single deck contains 52 cards.
     */
    @Test
    void testSingleDeckInitializationCount() {
        Deck deck = new Deck(1);
        assertEquals(52, deck.getCardCount());
    }
    
    /**
     * Verifies that a double deck contains 104 cards.
     */
    @Test
    void testDoubleDeckInitializationCount() {
        Deck deck = new Deck(2);
        assertEquals(104, deck.getCardCount());
    }

    /**
     * Ensures that dealing a card removes it from the deck and reduces the count.
     */
    @Test
    void testDealCardReducesSize() {
        Deck deck = new Deck(1);
        int initialSize = deck.getCardCount();
        assertNotNull(deck.dealCard());
        assertEquals(initialSize - 1, deck.getCardCount());
    }
}