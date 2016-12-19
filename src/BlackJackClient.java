import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Dmitry on 05.12.2016.
 */
public class BlackJackClient extends Application {

    TextArea textArea=new TextArea();
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 500, 500);
    Button startgame = new Button("Начать игру");
    Button hit=new Button("Взять");
    Button stay=new Button("Оставить");
    Button ready = new Button("Готов");

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
        startgame.setPrefWidth(scene.getWidth());
        startgame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ready.setPrefWidth(scene.getWidth());
                ready.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            outputStream.write("Ready".getBytes());
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
                            play();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        root.setLeft(hit);
                        root.setRight(stay);
                        root.getChildren().remove(ready);
                    }
                });
                connectToGame();
                root.setBottom(ready);
                root.getChildren().remove(startgame);
            }
        });
        root.setBottom(startgame);
        root.setCenter(textArea);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void connectToGame(){
        try {
            socket=new Socket("localhost", 1234);
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void play(){
        try{
        int fc=inputStream.read();
            byte buf[] = new byte[64*1024];
            int r = inputStream.read(buf);
            String fcf = new String(buf, 0, r);
            int sc=inputStream.read();
            r = inputStream.read(buf);
            String scf = new String(buf, 0, r);
            int score=inputStream.read();
            textArea.appendText(fc+" "+fcf+" \n");
            textArea.appendText(sc+" "+scf+" \n");
            textArea.appendText(score+" \n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void getCard(){
        try {
            outputStream.write("Hit".getBytes());
            byte buf[] = new byte[64*1024];
            int r = inputStream.read(buf);
            String response = new String(buf, 0, r);
            if (response.equals("Stop")){
                r = inputStream.read(buf);
                response = new String(buf, 0, r);
                String[] strings=response.split(" ");
                textArea.appendText(strings[0]+" "+strings[1]+" \n");
                textArea.appendText(strings[2]+" \n");
                stayhit();
            }else{
                String[] strings=response.split(" ");
                textArea.appendText(strings[0]+" "+strings[1]+" \n");
                textArea.appendText(strings[2]+" \n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void stayhit(){
        try {
            outputStream.write("Stay".getBytes());
            byte buf[] = new byte[64*1024];
            int r = inputStream.read(buf);
            String response = new String(buf, 0, r);
            hit.setDisable(true);
            stay.setDisable(true);
            getGameResult(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void getGameResult(String response){
        textArea.appendText(response);
    }
}
