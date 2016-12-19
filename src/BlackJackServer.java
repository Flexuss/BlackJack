import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Dmitry on 05.12.2016.
 */
public class BlackJackServer {

    public static void main(String[] args) {
        Game game=new Game();
        game.start();
        try {
            ServerSocket server =new ServerSocket(1234);
            System.out.println("Server is Started");
            int count= 0;
            while(true){
                Socket client = server.accept();
                if(game.isStarted){
                    game.addWaitingPlayer(new ClientThread(count,client));
                }else game.addPlayer(new ClientThread(count,client));
                System.out.println("Player "+count+" is connected");
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
