import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dmitry on 15.12.2016.
 */
public class Card implements Serializable {

    int suit;
    String face;
    int score;

    public Card(int suit, String face, int score) {
        this.suit = suit;
        this.face = face;
        this.score=score;
    }
}
