package blackjack.model;

import java.io.Serializable;

/**
 * Represents a single playing card.
 * Implements Serializable to be part of the game state saved to file.
 */
public class Card implements Serializable {
    private final Suit suit;
    private final Rank rank;

    /**
     * Constructs a Card with a specified suit and rank.
     * @param suit The suit of the card.
     * @param rank The rank of the card.
     */
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Returns the initial point value of the card based on its rank.
     * @return The initial integer value (e.g., 10 for King, 11 for Ace).
     */
    public int getValue() {
        return rank.getValue();
    }

    /**
     * Returns the rank of the card.
     * @return The Rank enum value.
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Returns the suit of the card.
     * @return The Suit enum value.
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Provides a string representation of the card, including a Unicode symbol for the suit.
     * This is used for display in the GUI.
     * @return The card name and its suit symbol (e.g., "ACE ♥").
     */
    @Override
    public String toString() {
        String symbol;

        switch (suit) {
            case HEARTS:
                symbol = "♥";
                break;

            case DIAMONDS:
                symbol = "♦";
                break;

            case CLUBS:
                symbol = "♣";
                break;

            case SPADES:
                symbol = "♠";
                break;

            default:
                symbol = "";
                break;
        }
        return rank.name() + " " + symbol;
    }
}