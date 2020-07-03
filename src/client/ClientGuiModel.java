package client;

//20 --- класс, отвечающий за модель (Model)

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientGuiModel {
    private final Set<String> allUserNames = new HashSet<>();
    private String newMessage;

    public Set<String> getAllUserNames() {
        return Collections.unmodifiableSet(allUserNames); //20.4 запрет модификации возвращаемого множества
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }
    
    //20.6
    public void addUser(String newUserName){
        allUserNames.add(newUserName);
    }
    
    //20.7
    public void deleteUser(String userName){
        allUserNames.remove(userName);
    }
    
}
