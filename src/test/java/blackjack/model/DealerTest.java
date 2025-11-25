package blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the specific hitting rules for the Dealer.
 */
class DealerTest {

    /**
     * Verifies that the dealer hits when their score is 16.
     */
    @Test
    void testDealerMustHitOnSixteen() {
        Dealer dealer = new Dealer();
        dealer.addCard(new Card(Suit.HEARTS, Rank.TEN));
        dealer.addCard(new Card(Suit.DIAMONDS, Rank.SIX)); 
        assertTrue(dealer.shouldHit());
    }

    /**
     * Verifies that the dealer stands when their score is 17.
     */
    @Test
    void testDealerMustStandOnSeventeen() {
        Dealer dealer = new Dealer();
        dealer.addCard(new Card(Suit.HEARTS, Rank.TEN));
        dealer.addCard(new Card(Suit.DIAMONDS, Rank.SEVEN)); 
        assertFalse(dealer.shouldHit());
    }
}