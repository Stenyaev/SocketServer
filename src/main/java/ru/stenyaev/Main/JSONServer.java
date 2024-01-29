package ru.stenyaev.Main;

import org.json.JSONException;
import org.json.JSONObject;
import ru.stenyaev.Main.enums.Resp;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class JSONServer {

    private ServerSocket serverSocket;
    private int port;
    public static int clients = 0;

    public void establish(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        System.out.println("JSONServer подключен к порту: " + port);

    }

    public void accept() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            Runnable r = new MyThreadHandler(socket);
            Thread t = new Thread(r);
            t.start();
        }
    }

    private static class MyThreadHandler implements Runnable {
        private Socket socket;

        MyThreadHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            clients++;
            System.out.println(clients + " JSONClient(s) подключен к порту : " + socket.getPort());

            try {
                // For JSON Protocol
                JSONObject jsonObject = receiveJSON();
                sendJSON(jsonObject);

            } catch (IOException e) {

            } finally {
                try {
                    closeSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void closeSocket() throws IOException {
            socket.close();
        }

        public JSONObject receiveJSON() throws IOException {
            InputStream in = socket.getInputStream();
            ObjectInputStream i = new ObjectInputStream(in);

            String line = null;
            try {
                line = (String) i.readObject();

            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JSONObject jsonObject = new JSONObject(line);
            System.out.println("Получено от клиента на порту " + socket.getPort() + ": " + jsonObject.toString());
            return jsonObject;
        }



        public String validator(JSONObject jsonObject) throws JSONException{

            try {
                String addressType = jsonObject
                        .getJSONObject("getEvState")
                        .getJSONObject("address")
                        .getString("addressType");
                String value = jsonObject
                        .getJSONObject("getEvState")
                        .getJSONObject("address")
                        .getString("value");

                if (   (addressType == (("STANDART")) || (addressType == ("UNIT")) || (addressType == ("GROUP"))) &&
                        ((0<=Integer.parseInt(value) && Integer.parseInt(value)<=64) ||
                                ((value == "CCS") || (value == "GBT") || (value == "CHADEMO"))) ) {

                    System.out.println("Валидация запроса прошла успешно");
                    System.out.println(addressType);

                    return Resp.RESPONSE_GET_EV_STATE.getResp();
                } else {
                    return ("Невалидный запрос");
                }

            } catch (JSONException e1) {
                try {
                    String addressType = jsonObject
                            .getJSONObject("getParameters")
                            .getJSONObject("address")
                            .getString("addressType");
                    String value = jsonObject
                            .getJSONObject("getParameters")
                            .getJSONObject("address")
                            .getString("value");

                    if (   (addressType == (("STANDART")) || (addressType == ("UNIT")) || (addressType == ("GROUP"))) &&
                            ((0<=Integer.parseInt(value) && Integer.parseInt(value)<=64) ||
                                    ((value == "CCS") || (value == "GBT") || (value == "CHADEMO"))) ) {

                        System.out.println("Валидация запроса прошла успешно");
                        System.out.println(addressType);

                        return Resp.RESPONSE_GET_PARAMETER.getResp();
                    } else {
                        return ("Невалидный запрос");
                    }

                } catch (JSONException e2) {
                    try {
                        String addressType = jsonObject
                                .getJSONObject("getState")
                                .getJSONObject("address")
                                .getString("addressType");
                        String value = jsonObject
                                .getJSONObject("getState")
                                .getJSONObject("address")
                                .getString("value");

                        if (   (addressType == (("STANDART")) || (addressType == ("UNIT")) || (addressType == ("GROUP"))) &&
                                ((0<=Integer.parseInt(value) && Integer.parseInt(value)<=64) ||
                                        ((value == "CCS") || (value == "GBT") || (value == "CHADEMO"))) ) {

                            System.out.println("Валидация запроса прошла успешно");
                            System.out.println(addressType);

                            return Resp.RESPONSE_GET_STATE.getResp();
                        } else {
                            return ("Невалидный запрос");
                        }


                    } catch (JSONException e3) {
//                        System.out.println("Невалидный запрос");
                        return ("Невалидный запрос");
                    }
                }
            }
        }

        public void sendJSON(JSONObject jsonObject) throws IOException {

            OutputStream out = socket.getOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(out);

            if (validator(jsonObject) == "Невалидный запрос") {
                    o.writeObject(validator(jsonObject));
                    System.out.println("Клиенту направлено сообщение о невалидности запроса: " + validator(jsonObject));
            } else {
                o.writeObject(validator(jsonObject));
                System.out.println("Отправлено с сервера клиенту: " + validator(jsonObject));
            }
            out.flush();
        }
    }

    public void start(int port) throws IOException{
        establish(port);
        accept();
    }

    public static void main(String[] args) {
        JSONServer server = new JSONServer();

        try {
            server.start(7777);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println(e);
        }
    }
}
