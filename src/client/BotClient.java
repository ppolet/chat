package client;

//18 Бот-клиент

import chat.ConsoleHelper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BotClient extends Client {
    //19
    public class BotSocketThread extends Client.SocketThread{

        //19.1
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        //19.2
        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            //19.2.2
            if (!message.contains(": ")) return;

            String[] st = message.split(": ", 2);  //st[0] - name; st[1] - message
            st[1] = st[1].toLowerCase().trim();

            String dateTimeFormat;
            switch (st[1]) {
                case "дата":
                    dateTimeFormat = "d.MM.YYYY";
                    break;
                case "date":
                    dateTimeFormat = "d.MM.YYYY";
                    break;
                case "день":
                    dateTimeFormat = "d";
                    break;
                case "месяц":
                    dateTimeFormat = "MMMM";
                    break;
                case "год":
                    dateTimeFormat = "YYYY";
                    break;
                case "время":
                    dateTimeFormat = "H:mm:ss";
                    break;
                case "час":
                    dateTimeFormat = "H";
                    break;
                case "минуты":
                   dateTimeFormat = "m";
                    break;
                case "секунды":
                    dateTimeFormat = "s";
                    break;
                default:
                    dateTimeFormat = null;
            }

            if (dateTimeFormat != null){
                //Информация для Боб: 12:30:47
                Calendar calendar = new GregorianCalendar();
                String msg = new SimpleDateFormat(dateTimeFormat).format(calendar.getTime());

                sendTextMessage("Информация для " + st[0] + ": " + msg);
            }
        }
        
        
        
    }

    //18.3.1
    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    //18.3.2
    @Override
    protected boolean shouldSentTextFromConsole() {
        return false;
    }

    //Новое имя бота
    @Override
    protected String getUserName() {
        Calendar calendar = new GregorianCalendar();
        String time = new SimpleDateFormat("HH.mm.ss").format(calendar.getTime());
        return "Date_Bot_" + time;
    }

    //18.4
    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
    
}
