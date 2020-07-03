package chat;

// Connection – класс соединения между клиентом и сервером

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable{
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    
    //5.2
    public Connection(Socket socket) throws IOException{
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }
    
    //5.3
    public void send(Message message) throws IOException{
        synchronized(out){
            out.writeObject(message);
        }
    }
    
    //5.4
    public Message receive() throws IOException, ClassNotFoundException{
        synchronized(in){
            return (Message)in.readObject();
        }
    }
    
    //5.5
    public SocketAddress getRemoteSocketAddress(){
        return socket.getRemoteSocketAddress();
    }
    
    //5.6 закрываем все ресурсы класса
    @Override
    public void close() throws IOException{
        in.close();
        out.close();
        socket.close();
    }
    
}
