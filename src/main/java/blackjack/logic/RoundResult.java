package blackjack.logic;

import java.io.Serializable;
import java.util.List;

/**
 * Stores the result and state of a single completed round for statistics.
 * Implements Serializable for game saving.
 */
public class RoundResult implements Serializable {
    private final String winner;
    private final int playerScore;
    private final int dealerScore;
    private final List<String> playerHand;
    private final List<String> dealerHand;

    /**
     * Constructs a new RoundResult.
     * @param winner The name of the winner ("Player" or "Dealer" or "Tie").
     * @param playerScore The final score of the player.
     * @param dealerScore The final score of the dealer.
     * @param playerHand The final hand of the player.
     * @param dealerHand The final hand of the dealer.
     */
    public RoundResult(String winner, int playerScore, int dealerScore, List<String> playerHand, List<String> dealerHand) {
        this.winner = winner;
        this.playerScore = playerScore;
        this.dealerScore = dealerScore;
        this.playerHand = playerHand;
        this.dealerHand = dealerHand;
    }

    
    /**
     * Returns the name of the winner.
     * @return The winner's name.
     */
    public String getWinner() { return winner; }

    /**
     * Returns the scores of the player.
     * @return The player's score.
     */
    public int getPlayerScore() { return playerScore; }

    /**
     * Returns the scores of the dealer.
     * @return The dealer's score.
     */
    public int getDealerScore() { return dealerScore; }

    /**
     * Returns the final hand of the player.
     * @return The player's hand as a list of strings.
     */
    public List<String> getPlayerHand() { return playerHand; }

    /**
     * Returns the final hand of the dealer.
     * @return The dealer's hand as a list of strings.
     */
    public List<String> getDealerHand() { return dealerHand; }
    
    /**
     * Provides a short, human-readable summary for display in the JList.
     * @return A concise string summary of the round.
     */
    @Override
    public String toString() {
        return String.format("Winner: %s | Player Score: %d | Dealer Score: %d", winner, playerScore, dealerScore);
    }
}