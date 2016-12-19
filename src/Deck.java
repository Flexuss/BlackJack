import java.util.ArrayList;

/**
 * Created by Dmitry on 15.12.2016.
 */
public class Deck {

    String[] faces = {"2","3","4","5","6","7","8","9","10","Jack","Queen","King","Ace"};
    ArrayList<Card> deck=new ArrayList<Card>();

    public Deck() {
        for(int i=0;i<faces.length;i++){
            for(int j=1;j<=4;j++){
                    if(i<=8){
                        deck.add(new Card(j,faces[i],i+2));
                    }else if(i==12){
                        deck.add(new Card(j,faces[i],11));
                    }else deck.add(new Card(j,faces[i],10));
            }
        }
    }

    void shuffle(){
        Card card;
        for(int i=0;i<deck.size();i++){
            int num=(int) (Math.random()*52);
            card=deck.get(i);
            deck.set(i, deck.get(num));
            deck.set(num,card);
        }
    }
    synchronized Card takeCard(){
        Card card=deck.get(1);
        deck.remove(1);
        return card;
    }
    public int getScore(ArrayList<Card> hand){
        int aces=0;
        int score=0;
            for(int j=0;j<hand.size();j++){
                score=score+hand.get(j).score;
                if(hand.get(j).score==11){
                    aces++;
                }
            }
            if(score>21){
                for(int i=1;i<=aces;i++){
                    if(score-aces*10<=21){
                        return score-aces*10;
                    }
                }
            }
        return score;
    }

    public ArrayList<Card> getDeck() {
        shuffle();
        return deck;
    }
}
