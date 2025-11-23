package blackjack.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the deck(s) of cards, including initialization, shuffling, and dealing.
 * The deck can be configured to contain 1 or 2 standard 52-card decks.
 * Implements Serializable for game saving functionality.
 */
public class Deck implements Serializable {
    private List<Card> cards;
    private final int numberOfDecks;

    /** The maximum number of decks allowed. */
    private static final int MAX_DECKS = 2;
    
    /** The default number of decks for a single game. */
    private static final int DEFAULT_DECKS = 1;

    /**
     * Constructs a new Deck with the default number of decks (1).
     */
    public Deck() {
        this(DEFAULT_DECKS);
    }

    /**
     * Constructs a new Deck and immediately initializes it with the specified number of decks (max 2).
     * @param count The desired number of decks (1 or 2). Invalid count defaults to 1.
     */
    public Deck(int count) {
        // Ensure the count is valid: 1 or 2. If not, default to 1.
        if (count < 1 || count > MAX_DECKS) {
            this.numberOfDecks = DEFAULT_DECKS;
        } else {
            this.numberOfDecks = count;
        }
        this.cards = new ArrayList<>();
        initializeDeck();
    }

    /**
     * Populates the deck with the specified number of standard 52-card decks and shuffles them.
     */
    private void initializeDeck() {
        cards.clear();
        // Create cards for the specified number of decks
        for (int i = 0; i < numberOfDecks; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    this.cards.add(new Card(suit, rank));
                }
            }
        }
        shuffle();
    }

    /**
     * Randomly shuffles the cards in the deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Deals (removes and returns) the top card from the deck.
     * The card is removed from the list, thus it cannot be dealt again until the deck is reset.
     * @return The Card object dealt.
     * @throws IllegalStateException if the deck is empty.
     */
    public Card dealCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty! A new round must be started.");
        }
        return cards.remove(0);
    }

    /**
     * Returns the current number of cards remaining in the deck.
     * @return The count of cards.
     */
    public int getCardCount() {
        return cards.size();
    }
}