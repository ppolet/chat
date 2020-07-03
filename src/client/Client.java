package client;

import chat.Connection;
import chat.ConsoleHelper;
import chat.Message;
import chat.MessageType;
import java.io.IOException;
import java.net.Socket;

public class Client {
    protected Connection connection;  //12.4
    private volatile boolean clientConnected = false;  //12.5
    
    //15
    public class SocketThread extends Thread{
        //15.1
        protected void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);
        }
        
        //15.2
        protected void informAboutAddingNewUser(String userName){
            ConsoleHelper.writeMessage("+++ Участник " + userName + " присоединился к чату +++");
        }
        
        //15.3
        protected void informAboutDeletingNewUser(String userName){
            ConsoleHelper.writeMessage("--- Участник " + userName + " покинул чат ---");
        }
        
        //15.4
        protected void notifyConnectionStatusChanged(boolean clientConnected){
            Client.this.clientConnected = clientConnected;
            synchronized(Client.this){
                Client.this.notify();
            }
        }
        
        //16.1 --- Этот метод будет представлять клиента серверу.
        protected void clientHandshake() throws IOException, ClassNotFoundException{
            while(true){ //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                Message msg = connection.receive();
                if (msg.getType() == MessageType.NAME_REQUEST){
                    String userName = getUserName();
                    connection.send(new Message(MessageType.USER_NAME, userName));
                } else if (msg.getType() == MessageType.NAME_ACCEPTED){
                    notifyConnectionStatusChanged(true);
                    break; // выходим из цикла и метода
                } else {
                    throw new IOException("Unexpected MessageType");  //16.1.4
                }
            }
        }
        
        //16.2 --- Этот метод будет реализовывать главный цикл обработки сообщений сервера.
        protected void clientMainLoop() throws IOException, ClassNotFoundException{
            while (!currentThread().isInterrupted()){       //16.2.6
                Message msg = connection.receive();
                switch (msg.getType()){
                    case TEXT:
                        processIncomingMessage(msg.getData());
                        break;
                    case USER_ADDED:
                        informAboutAddingNewUser(msg.getData());
                        break;
                    case USER_REMOVED:
                        informAboutDeletingNewUser(msg.getData());
                        break;
                    default:
                        throw new IOException("Unexpected MessageType");
                }
            }
        }
        
        //17
        @Override
        public void run(){
            String address = getServerAddress();
            int port = getServerPort();
            //17.2
            try {
                Socket socket = new Socket(address, port);   //17.2
                connection = new Connection(socket);  //17.3
                //System.out.println("---Connection, socket: " + connection + ",   " + socket);
                clientHandshake();                              //17.4
                clientMainLoop();                               //17.5
            } catch (IOException | ClassNotFoundException ex) {
                notifyConnectionStatusChanged(false);
            }
            
        }
    }
    
    //14.1
    public void run() {
        SocketThread socketThread = getSocketThread(); //14.1.1
        socketThread.setDaemon(true);  //14.1.2
        socketThread.start();  //14.1.3
        //14.1.4
        synchronized(this){
            try {
                this.wait();
            } catch (InterruptedException ex) {
                ConsoleHelper.writeMessage("ОШИБКА! Выход из программы: " + ex.getMessage());
                return;
            }
        }
        
        //14.1.5
        if (clientConnected){
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
        } else {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        }
        
        //14.1.6
        String text = null;
        do {
            text = ConsoleHelper.readString();
            //14.1.7
            if (shouldSentTextFromConsole()){
                sendTextMessage(text);
            }
        } while (!text.equals("exit"));
        
    }
    
    //13.1
    protected String getServerAddress(){
        ConsoleHelper.writeMessage("Введите адрес сервера (ip, если клиент и сервер запущен на разных машинах, иначе ‘localhost’)");
        String st = ConsoleHelper.readString();
        return st;
    }
    
    //13.2
    protected int getServerPort(){
        int port;
        do {
            ConsoleHelper.writeMessage("Введите порт сервера (1024 - 65535): ");
            port = ConsoleHelper.readInt();
        } while (port<1024 || port >65535);
        return port;
    }
    
    //13.3
    protected String getUserName(){
        String st = null;
        do {
            ConsoleHelper.writeMessage("Введите ваше имя в чате: ");
            st = ConsoleHelper.readString();
        } while(st == null || st.equals(""));
        return st;
    }
    
    //13.4
    protected boolean shouldSentTextFromConsole(){
        return true;
    }
    
    //13.5
    protected SocketThread getSocketThread(){
        return new SocketThread();
    }
    
    //13.6
    protected void sendTextMessage(String text){
        try{
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException ex) {
            ConsoleHelper.writeMessage("Ошибка. Не удается отправить сообщение, соединение будет закрыто: " + ex.getMessage());
            clientConnected = false;
        }
    }
    
    //14.2
    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
