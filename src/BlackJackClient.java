import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Created by Dmitry on 05.12.2016.
 */
public class BlackJackClient extends Application {

    TextArea textArea=new TextArea();
    Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    int score=0;
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 500, 500);
    Button startgame = new Button("Начать игру");
    Button hit=new Button("Взять");
    Button stay=new Button("Оставить");
    Button ready = new Button("Готов");
    Button restart=new Button("Новая игра");
    Button quite=new Button("Выход");

    public static void main(String[] args) {
        launch(args);
//        try {
//            Socket socket=new Socket("localhost", 1234);
//            InputStream inputStream=socket.getInputStream();
//            int fc=inputStream.read();
//            byte buf[] = new byte[64*1024];
//            int r = inputStream.read(buf);
//            String fcf = new String(buf, 0, r);
//            int sc=inputStream.read();
//            r = inputStream.read(buf);
//            String scf = new String(buf, 0, r);
//            int score=inputStream.read();
//            System.out.println(fc+" "+fcf);
//            System.out.println(sc+" "+scf);
//            System.out.print(score);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

//    public BlackJackClient(Socket socket, OutputStream outputStream){
//        this.outputStream=outputStream;
//        this.socket=socket;
//
//    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("BlackJack");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    outputStream.writeObject("Exit");
                    socket.close();
                    inputStream.close();
                    outputStream.close();
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        textArea.setEditable(false);
        startgame.setPrefWidth(scene.getWidth());
        startgame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ready.setPrefWidth(scene.getWidth());
                ready.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            outputStream.writeObject("Ready");
                            outputStream.flush();
                            play();
                            hit.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    getCard();
                                }
                            });
                            stay.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    stayhit();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stay.setPrefWidth(scene.getWidth());
                        hit.setPrefWidth(scene.getWidth());
                        stay.setDisable(false);
                        hit.setDisable(false);
                        root.setBottom(hit);
                        root.setTop(stay);
                        root.getChildren().remove(ready);
                    }
                });
                connectToGame();
            }
        });
        root.setBottom(startgame);
        root.setCenter(textArea);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void play(){
        try{
        Card firstCard= (Card) inputStream.readObject();
            outCard(firstCard);
            Card secondCard= (Card) inputStream.readObject();
            outCard(secondCard);
            score= (int) inputStream.readObject();
            textArea.appendText("Ваш счет: "+score+" \n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void getCard(){
        try {
            outputStream.writeObject("Hit");
            outputStream.flush();
            Card card= (Card) inputStream.readObject();
            outCard(card);
            score= (int) inputStream.readObject();
            textArea.appendText("Ваш счет: "+score+" \n");
            String response = (String) inputStream.readObject();
            if (response.equals("Stop")){
                stayhit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void stayhit(){
        textArea.appendText("Ожидание других игроков. \n");
        hit.setDisable(true);
        stay.setDisable(true);
        int dealerScore=0;
        String result="";
        try {
            outputStream.writeObject("Stay");
            outputStream.flush();
            dealerScore = (int) inputStream.readObject();
            result = (String) inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        getGameResult(dealerScore);
        textArea.appendText(result+" \n");
    }

    void connectToGame() {
        try {
            socket = new Socket("localhost", 1234);
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            textArea.appendText("Удачное подключение к серверу \n");
            String response= (String) inputStream.readObject();
            if(response.equals("Full")){
                textArea.appendText("Сервер заполнен. Подключитесь позже \n");
            }else if(response.equals("Wait")){
                textArea.appendText("Удачное подключение к игре. Возможно, игра уже идет, подождите \n");
                startgame.setDisable(true);
                response= (String) inputStream.readObject();
                if(response.equals("Success")){
                    textArea.appendText("Вы присоединились с игре. Удачной игры! \n");
                    root.setBottom(ready);
                    root.getChildren().remove(startgame);
                }
            }else{
                textArea.appendText("Вы присоединились с игре. Удачной игры! \n");
                root.setBottom(ready);
                root.getChildren().remove(startgame);
            }
        } catch (ConnectException e) {
            textArea.appendText("Подключение к серверу не удалось \n");
        } catch (IOException e) {
            textArea.appendText("IOExrption \n");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void getGameResult(int response){
        textArea.appendText("Счет дилера: "+response+" \n");
        restart.setPrefWidth(scene.getWidth());
        quite.setPrefWidth(scene.getWidth());
        restart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    outputStream.writeObject("Restart");
                    outputStream.flush();
                    textArea.clear();
                    textArea.appendText("Начало новой игры \n");
                    root.getChildren().remove(restart);
                    root.getChildren().remove(quite);
                    root.setBottom(ready);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        quite.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    outputStream.writeObject("Exit");
                    outputStream.flush();
                    socket.close();
                    inputStream.close();
                    outputStream.close();
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        root.getChildren().remove(hit);
        root.getChildren().remove(stay);
        root.setBottom(restart);
        root.setTop(quite);
    }

    void outCard(Card card){
        String suit="";
        switch (card.suit){
            case 1: suit="Черви";
                break;
            case 2: suit="Буби";
                break;
            case 3: suit="Пики";
                break;
            case 4: suit="Крести";
                break;
        }
        textArea.appendText("Вы получили "+card.face+" "+suit+" \n");
    }
}
