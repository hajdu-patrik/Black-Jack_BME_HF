package blackjack.model;

public class Dealer extends Player {

    public Dealer() {
        super("Dealer");
    }

    /**
     * Dealer rule: must hit under 17 points.
     * @return true if should hit, false to stand.
     */
    public boolean shouldHit() {
        return getScore() < 17;
    }
}