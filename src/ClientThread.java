import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Dmitry on 05.12.2016.
 */
public class ClientThread extends Thread {

    private int user;
    private Socket socket;
    private Game game;
    boolean isReady=false;
    boolean isFinish = false;
    private ArrayList<Card> hand=new ArrayList<>();
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    @Override
    public void run() {
        try {
            String ready= (String) inputStream.readObject();
            if(ready.equals("Exit")){
                exitGame();
            }
            if(ready.equals("Ready")){
                isFinish=false;
                isReady=true;
            }
            System.out.println("Player "+user+" is ready");
            synchronized (this) {
                this.wait();
            }
            hand.add(game.deck.takeCard());
            System.out.println(user+" take 1 card");
            System.out.println("Send "+user+" first card");
            outputStream.writeObject(hand.get(hand.size()-1));
            outputStream.flush();
            hand.add(game.deck.takeCard());
            System.out.println(user+" take 2 card");
            int score=game.deck.getScore(hand);
            System.out.println("Send "+user+" second card");
            outputStream.writeObject(hand.get(hand.size()-1));
            outputStream.flush();
            System.out.println("Send "+user+" score");
            outputStream.writeObject(score);
            outputStream.flush();
            String request="";
            while(!request.equals("Stay")) {
                request = (String) inputStream.readObject();
                if(request.equals("Exit")){
                    exitGame();
                }
                if (request.equals("Hit")) {
                    hand.add(game.deck.takeCard());
                    score = game.deck.getScore(hand);
                    System.out.println("Send " + user + " one more card");
                    outputStream.writeObject((hand.get(hand.size() - 1)));
                    outputStream.flush();
                    outputStream.writeObject(score);
                    outputStream.flush();
                    if(score>=21){
                        outputStream.writeObject("Stop");
                        outputStream.flush();
                    }else{ outputStream.writeObject("AllGood");
                    outputStream.flush();}
                } else {
                    isFinish=true;
                    synchronized (this){
                        this.wait();
                    }
                    getResult();
                    isReady=false;
                    hand.clear();
//                    int dealerScore=game.dealer.score;
//                    outputStream.writeObject(dealerScore);
//                    if(dealerScore<=21){
//                        if(score<=21) {
//                            if (score > dealerScore) {
//                                outputStream.writeObject("Вы выиграли");
//                            } else outputStream.writeObject("Вы проиграли");
//                            if(score==dealerScore){
//                                outputStream.writeObject("Ничья");
//                            }
//                        }else outputStream.writeObject("Вы проиграли");
//                    }else if(score<=21){
//                        outputStream.writeObject("Вы выиграли");
//                    }else outputStream.writeObject("Ничья");
                }
            }
            request= (String) inputStream.readObject();
            if(request.equals("Exit")){
                exitGame();
            }else if(request.equals("Restart")){
                restartGame();
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    ClientThread(int user, Socket socket) {
        this.user=user;
        this.socket=socket;
        try {
            outputStream=new ObjectOutputStream(socket.getOutputStream());
            inputStream=new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setGame(Game game) {
        this.game = game;
    }

    private void getResult(){
        int dealerScore=game.dealer.score;
        int score=game.deck.getScore(hand);
        try {
            outputStream.writeObject(dealerScore);
            outputStream.flush();
            if (dealerScore <= 21) {
                if (score <= 21) {
                    if (score > dealerScore) {
                        outputStream.writeObject("Вы выиграли");
                        outputStream.flush();
                    } else
                    if (score == dealerScore) {
                        outputStream.writeObject("Ничья");
                        outputStream.flush();
                    }else{ outputStream.writeObject("Вы проиграли");
                    outputStream.flush();}
                } else {outputStream.writeObject("Вы проиграли");outputStream.flush();}
            } else if (score <= 21) {
                outputStream.writeObject("Вы выиграли");
                outputStream.flush();
            } else{ outputStream.writeObject("Ничья");
                outputStream.flush();}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exitGame(){
        try {
            game.removePlayer(this);
            socket.close();
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restartGame(){
        this.run();
    }

    void sendFull(){
        try {
            outputStream.writeObject("Full");
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendWait(){
        try {
            outputStream.writeObject("Wait");
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendSuccess(){
        try {
            outputStream.writeObject("Success");
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
