import java.util.ArrayList;

/**
 * Created by Dmitry on 15.12.2016.
 */
public class Game extends Thread {

    public ArrayList<ClientThread> clients =new ArrayList<>();
    public ArrayList<ClientThread> waitingClients=new ArrayList<>();
    Deck deck=new Deck();
    boolean isStarted=false;

    public void setStarted() {
        isStarted = true;
    }

    @Override
    public void run() {
        while(!allIsReady()||clients.isEmpty()){
            for (int i=0;i<clients.size();i++){
                if(!clients.get(i).isAlive()) {
                    clients.get(i).start();
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        deck.shuffle();
            for(int i=0;i<clients.size();i++){
                synchronized (clients.get(i)) {
                    clients.get(i).notify();
                }
            }
        System.out.println("Game is Started");
        setStarted();
    }

    void addPlayer(ClientThread player){
        clients.add(player);
        clients.get(clients.size()-1).setDeck(this.deck);
    }
    void addWaitingPlayer(ClientThread player){
        waitingClients.add(player);
    }

    boolean allIsReady(){
        for(int i=0;i<clients.size();i++){
            if(!clients.get(i).isReady){
                return false;
            }
        }
        return true;
    }
}
