package org.example;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.example.model.User;

import java.util.List;

public class Mailing extends Thread implements AutoCloseable{
    private final Messenger messenger;
    private List<User> users;
    private String text;

    public Mailing(Messenger messenger, List<User> users, String text) {
        this.messenger = messenger;
        this.users = users;
        this.text = text;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void sendMailing(List<User> users, String textMail){
        users.forEach( user -> {
            try {
                messenger.sendMessage(textMail,user.getId());
            } catch (ClientException | ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void run() {
        try {
            sendMailing(users,text);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {

    }
}
