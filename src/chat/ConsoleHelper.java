package chat;

// ConsoleHelper – вспомогательный класс, для чтения или записи в консоль

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    
    public static void writeMessage(String message){
        System.out.println(message);
    }
    
    // считываем строку с консоли
    public static String readString(){
        String st = "";
        while(st.isEmpty()){
            try {
                st = reader.readLine();
            } catch (IOException ex) {
                writeMessage("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }
        return st;
    }
    
    // считываем число
    public static int readInt(){
        String st;
        int res = 0;
        while(true){
            try{
                st = readString();
                res = Integer.parseInt(st);
                break; //выходим из цикла
            } catch (NumberFormatException ex) {
                writeMessage("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            }
        }
        return res;
    }
}
