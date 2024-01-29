package ru.stenyaev.Main;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class JSONClient {

    private String host;
    private int port;
    private Socket socket;
    private final String DEFAULT_HOST = "localhost";

    public void connect(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        socket = new Socket(host, port);
        System.out.println("Клиент подключен..");
        System.out.println("Ожидание команды..");
    }

    public Object receiveJSON() throws IOException {
        InputStream in = socket.getInputStream();
        ObjectInputStream i = new ObjectInputStream(in);

        String line = null;
        try {
            line = (String) i.readObject();

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(line);
            System.out.println("Валидация запроса прошла успешно");
            System.out.println("Получено от сервера на порту " + socket.getPort() + ": " + jsonObject.toString());
            return jsonObject;
        } catch (JSONException e) {
            System.out.println("Валидация не пройдена");
            return line;
        }
    }

    public void sendJSON(String jsonObject) throws IOException {
        OutputStream out = socket.getOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(out);
        o.writeObject(jsonObject.toString());
        out.flush();
        System.out.println("Отправлено на сервер: " + jsonObject.toString());
    }

    public static void main(String[] args) {
        JSONClient client = new JSONClient();
        try{
            client.connect("localhost", 7777);
            Scanner sc = new Scanner(System.in);
            String jsonObject = sc.nextLine();

            client.sendJSON(jsonObject);
            client.receiveJSON();
        }

        catch (ConnectException e) {
            System.err.println(client.host + " Подключение оборвано");
            return;
        }

        catch(UnknownHostException e){
            System.err.println(client.host + " Неизвестный host");
            client.host = client.DEFAULT_HOST;
            return;
        }

        catch (NoRouteToHostException e) {
            System.err.println(client.host + " host недоступен");
            return;

        }

        catch (IllegalArgumentException e){
            System.err.println(client.host + " Неверный port");
            return;
        }

        catch(IOException e){
            System.err.println(client.host + ' ' + e.getMessage());
            System.err.println(e);
        }

        finally {
            try {
                client.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}


