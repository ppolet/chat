package chat;

// Server – основной класс сервера

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    //7.1 - 7.2
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();
    
    //6.1 - 6.3
    private static class Handler extends Thread{
        private Socket socket;
        
        public Handler(Socket socket){
            this.socket = socket;
        }
        
        //11
        @Override
        public void run(){
            ConsoleHelper.writeMessage("Установлено новое соединение с адресом: " + socket.getRemoteSocketAddress());
            Connection connection = null;
            String clientName = null;
            try {
                connection = new Connection(socket);  //11.2
                clientName = serverHandshake(connection); //11.3
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, "+++ " + clientName + " присоединился в чат +++")); //11.4
                sendListOfUsers(connection, clientName); //11.5
                serverMainLoop(connection, clientName); //11.6
                
            } catch (IOException ex) {
                ConsoleHelper.writeMessage("Ошибка установки соединения с socket: " + ex.getMessage());
            } catch (ClassNotFoundException ex) {
                ConsoleHelper.writeMessage("Ошибка установки Рукопожатия с клиентом: " + ex.getMessage());
            } finally {
                //11.7
                if (connection != null){
                    try {
                        connection.close();
                    } catch (IOException ex) {
                        ConsoleHelper.writeMessage("Ошибка, не могу закрыть соединение: " + ex.getMessage());
                    }
                }
            }
            
            //11.9
            if(clientName != null){
                connectionMap.remove(clientName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, "--- " + clientName + " покинул чат ---"));
            }
            
            ConsoleHelper.writeMessage("Соединение с удаленным адресом " + socket.getRemoteSocketAddress() + " закрыто");
        }
        
        //10
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{
            while(true){ //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Сделать грамотное прерывание
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT){
                    String msg = userName + ": " + message.getData();
                    sendBroadcastMessage(new Message(message.getType(), msg));
                } else {
                    //НЕ ТЕКСТ, выводим сообщение об ошибке
                    ConsoleHelper.writeMessage("ERROR, Сообщение от " + userName + " не является текстом");
                }
            }
        }
        
        //9
        private void sendListOfUsers(Connection connection, String userName) throws IOException{
            for(String clientName: connectionMap.keySet()){
                if(clientName.equals(userName)) continue;
                connection.send(new Message(MessageType.USER_ADDED, clientName));
            }
        }

        //8
        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException{
            String clientName = null;
            boolean flagAccepted = false;
            do{
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();
                if (message.getType() == MessageType.USER_NAME) {
                    clientName = message.getData();
                    if (!clientName.isEmpty() && !connectionMap.containsKey(clientName)){
                        connectionMap.put(clientName, connection);
                        connection.send(new Message(MessageType.NAME_ACCEPTED));
                        flagAccepted = true;
                    }
                }
            } while(!flagAccepted);
            
            return clientName;
        }
    }
    
    public static void sendBroadcastMessage(Message message){
        for(String clientName: connectionMap.keySet()){
            try {
                connectionMap.get(clientName).send(message);
            } catch (IOException ex) {
                ConsoleHelper.writeMessage("Не смогли отправить сообщение (клиент: " + clientName + ")...");
            }
        }
    }
    
    //6.4
    public static void main(String[] args) {
        int port;
        do {
            ConsoleHelper.writeMessage("Введите порт сервера (рекомендуется 1024 - 65535): ");
            port = ConsoleHelper.readInt();
        } while (port<1024 || port >65535);
        
        //6.4.2
        try (ServerSocket server = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Сервер запущен (порт: " + port + ")");
            //6.4.4
            while (true){
                Socket clientSocket = server.accept();
                new Handler(clientSocket).start();
            }
        } catch (IOException ex) {
            ConsoleHelper.writeMessage("Server socket IOException: " + ex.getMessage());
        } 
        
        
    }
    
}
