import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Dmitry on 05.12.2016.
 */
public class ClientThread extends Thread {

    int user;
    Socket socket;
    Deck deck;
    boolean isReady=false;
    ArrayList<Card> hand=new ArrayList<>();

    @Override
    public void run() {
        try {
            InputStream inputStream=socket.getInputStream();
            OutputStream outputStream=socket.getOutputStream();
            byte buf[] = new byte[64*1024];
            int r=inputStream.read(buf);
            String ready=new String(buf,0,r);
            if(ready.equals("Ready")){
                isReady=true;
            }
            System.out.println("Player "+user+" is ready");
            synchronized (this) {
                wait();
            }
            hand.add(deck.takeCard());
            System.out.println(user+" take 1 card");
            System.out.println("Send "+user+" first suit");
            outputStream.write(hand.get(hand.size()-1).suit);
            System.out.println("Send "+user+" first face");
            outputStream.write(hand.get(hand.size()-1).face.getBytes());
            hand.add(deck.takeCard());
            System.out.println(user+" take 2 card");
            int score=deck.getScore(hand);
            System.out.println("Send "+user+" second suit");
            outputStream.write(hand.get(hand.size()-1).suit);
            System.out.println("Send "+user+" second face");
            outputStream.write(hand.get(hand.size()-1).face.getBytes());
            System.out.println("Send "+user+" score");
            outputStream.write(score);
            String request="";
            while(!request.equals("Stay")) {
                r = inputStream.read(buf);
                request = new String(buf, 0, r);
                if (request.equals("Hit")) {
                    hand.add(deck.takeCard());
                    score = deck.getScore(hand);
                    if(score>=21){
                        outputStream.write("Stop".getBytes());
                    }
                    System.out.println("Send " + user + " one more card");
                    outputStream.write((hand.get(hand.size() - 1).suit + " " + hand.get(hand.size() - 1).face + " " + score).getBytes());
                } else {
                    outputStream.write("12345679".getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public ClientThread(int user, Socket socket) {
        this.user=user;
        this.socket=socket;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }
}
