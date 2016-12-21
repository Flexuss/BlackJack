import java.util.ArrayList;

/**
 * Created by Dmitry on 19.12.2016.
 */
class Dealer extends Thread {

    private Deck deck;
    private ArrayList<Card> hand=new ArrayList<>();
    int score=0;

    Dealer(Deck deck) {
        this.deck=deck;
    }

    @Override
    public void run() {
        hand.add(deck.takeCard());
        hand.add(deck.takeCard());
        score=deck.getScore(hand);
        System.out.println("Dealer has "+score+" points");
        while(score<17){
            hand.add(deck.takeCard());
            score=deck.getScore(hand);
            System.out.println("Dealer has "+score+" points");
        }
    }
}
