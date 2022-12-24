package org.example;

import org.example.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.example.CSV.readCSV;
import static org.example.CSV.readUsers;

public class Main {
    static VkConnection vkConnection;
    static YDBConnection ydbConnection;
    static Messenger messenger;
    static Chat chat;
    static  Mailing mailing;

    static Scanner reader;
    public static void main(String[] args) throws Exception {
        vkConnection = new VkConnection();
        ydbConnection = new YDBConnection();

        messenger = new Messenger(vkConnection);
        chat = new Chat(messenger, ydbConnection);

        reader = new Scanner(System.in);


        startApp();
    }

    private static void startApp() throws Exception {
        String input;
        String commands = "1 - help\n2 - start chat\n3 - stop chat\n4 - send mail\n";

        System.out.print("Choose command" + commands);

        do {
            input = reader.nextLine();

            switch (input) {
                case "1", "help" -> System.out.println(commands);
                case "2", "start chat" -> startChat();
                case "3", "stop chat" -> stopChat();
                case "4", "send mail" -> sendMailing();
                default -> System.out.print("Not correct command\n" + commands);
            }


        }while (!input.equals("stop"));

        System.out.println("Bye");
    }

    private static void startChat() throws Exception {
        if(!chat.isActive){
            chat = new Chat(messenger, ydbConnection);
            chat.start();

            System.out.println("Chat start");
            startApp();
        }else {
            System.out.println("So the chat works");
            startApp();
        }

    }

    private static void stopChat() throws Exception {
        if(chat.isActive){
            chat.close();
            System.out.println("Chat stopped");
            startApp();
        }else {
            System.out.println("So the chat is turned off");
            startApp();
        }
    }

    private static void sendMailing() throws Exception {
        String input;
        String commands = "1 - DB\n2 - CSV\n";

        System.out.print("Select the data source\n" + commands);

        do {
            input = reader.nextLine();

            switch (input) {
                case "1", "DB" -> MailingByDb();
                case "2", "CSV" -> MailingByCSV();
                default -> System.out.print("Not correct command\n" + commands);
            }
        }while (!input.equals("stop"));

        System.out.println("Bye");
    }

    private static void MailingByDb() throws Exception {
        String input;
        String commands = "1 - Get All\n2 - Custom query\n";

        System.out.print("Select the data source\n" + commands);

        do {
            input = reader.nextLine();

            switch (input) {
                case "1", "get all" -> {
                    List<User> users = getDBUsers();

                    System.out.print("Write message: ");
                    mailing = new Mailing(messenger, users, reader.nextLine());
                    mailing.start();

                    startApp();
                }

                case "2", "custom query" -> {
                    System.out.print("Write query: ");
                    List<User> users = getDBUsers(reader.nextLine());

                    System.out.print("Write message: ");
                    mailing = new Mailing(messenger, users, reader.nextLine());
                    mailing.start();

                    startApp();
                }
                default -> System.out.print("Not correct command\n" + commands);
            }
        }while (!input.equals("stop"));

        System.out.println("Bye");
    }

    private static List<User> getDBUsers(){
        SelectData selectData = new SelectData(ydbConnection);
        selectData.run();
        return selectData.getAnswer();
    }

    private static List<User> getDBUsers(String query){
        SelectData selectData = new SelectData(ydbConnection, query);
        selectData.run();
        return selectData.getAnswer();
    }

    private static void MailingByCSV() throws Exception {
        String input;

        System.out.print("Write absolute path: \n");

        do {
            input = reader.nextLine();

            try {
                List<String> strings = readCSV(input);
                ArrayList<User> users = readUsers(strings);

                System.out.print("Write message: ");
                mailing = new Mailing(messenger, users, reader.nextLine());
                mailing.start();

                startApp();
            }catch (Exception e){
                System.out.print("Not correct path\n");
            }
        }while (!input.equals("stop"));

        System.out.println("Bye");
    }

}