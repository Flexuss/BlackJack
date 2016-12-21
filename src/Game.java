import java.util.ArrayList;

/**
 * Created by Dmitry on 15.12.2016.
 */
class Game extends Thread {

    private ArrayList<ClientThread> clients =new ArrayList<>();
    private ArrayList<ClientThread> waitingClients=new ArrayList<>();
    Deck deck;
    Dealer dealer;
    boolean isStarted=false;

    private void setStarted() {
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
        deck=new Deck();
        dealer=new Dealer(deck);
        deck.shuffle();
        dealer.start();
            for(int i=0;i<clients.size();i++){
                synchronized (clients.get(i)) {
                    clients.get(i).notify();
                }
            }
        System.out.println("Game is Started");
        setStarted();
        while(!allIsFinish()&&!clients.isEmpty()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(int i=0;i<clients.size();i++){
            synchronized (clients.get(i)) {
                clients.get(i).notify();
            }
        }
        isStarted=false;
        while(clients.size()<=6&&!waitingClients.isEmpty()){
            this.addPlayer(waitingClients.get(0));
            waitingClients.remove(0);
        }
        this.run();
    }

    void addPlayer(ClientThread player){
        clients.add(player);
        clients.get(clients.size()-1).setGame(this);
        clients.get(clients.size()-1).sendSuccess();
    }
    void addWaitingPlayer(ClientThread player){
        waitingClients.add(player);
        if(isFull()){
            waitingClients.get(waitingClients.size()-1).sendFull();
        }else waitingClients.get(waitingClients.size()-1).sendWait();
    }

    private boolean allIsReady(){
        for(int i=0;i<clients.size();i++){
            if(!clients.get(i).isReady){
                return false;
            }
        }
        return true;
    }


    private boolean allIsFinish(){
        for(int i=0;i<clients.size();i++){
            if(!clients.get(i).isFinish){
                return false;
            }
        }
        return true;
    }

    void removePlayer(ClientThread player){
        if(clients.contains(player)) {
            clients.remove(player);
        }else if(waitingClients.contains(player)){
            waitingClients.remove(player);
        }
    }

    private boolean isFull(){
        return clients.size() + waitingClients.size() - 1 > 6;
    }
}
