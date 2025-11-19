package blackjack.logic;

import blackjack.model.Deck;
import blackjack.model.Player;
import blackjack.model.Dealer;
import blackjack.model.Card;
import java.io.Serializable;

public class BlackjackGame implements Serializable {
    private Deck deck;
    private Player player;
    private Dealer dealer;
    private boolean isGameOver;

    public BlackjackGame(String playerName) {
        this.deck = new Deck();
        this.player = new Player(playerName);
        this.dealer = new Dealer();
        startNewRound();
    }

    public void startNewRound() {
        isGameOver = false;
        deck = new Deck(); // New deck each round (or only when depleted)
        player.clearHand();
        dealer.clearHand();

        // Initial dealing
        player.addCard(deck.dealCard());
        dealer.addCard(deck.dealCard());
        player.addCard(deck.dealCard());
        dealer.addCard(deck.dealCard());
    }

    public void playerHit() {
        if (!isGameOver) {
            player.addCard(deck.dealCard());
            if (player.getScore() > 21) {
                isGameOver = true; // Bust (player busted)
            }
        }
    }

    public void playerStand() {
        // Dealer köre
        while (dealer.shouldHit()) {
            dealer.addCard(deck.dealCard());
        }
        isGameOver = true;
    }

    // Getters for the GUI
    public Player getPlayer() { return player; }
    public Dealer getDealer() { return dealer; }
    public boolean isGameOver() { return isGameOver; }
    
    public String getGameResult() {
        if (!isGameOver) return "Game in progress";
        int pScore = player.getScore();
        int dScore = dealer.getScore();

        if (pScore > 21) return "You lost (Too much)!";
        if (dScore > 21) return "You won (Dealer busted)!";
        if (pScore > dScore) return "You won!";
        if (pScore < dScore) return "You lost!";
        return "Tie!";
    }
}