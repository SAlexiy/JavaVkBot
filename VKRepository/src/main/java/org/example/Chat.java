package org.example;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.*;
import org.example.model.User;

import java.util.List;

public class Chat extends Thread{
    private final YDBConnection ydbConnection;
    private final Messenger messenger;
    public boolean isActive;

    public Chat(Messenger messenger, YDBConnection ydbConnection) throws ClientException, ApiException {

        this.ydbConnection = ydbConnection;

        this.messenger = messenger;

        this.isActive = false;
    }

    public Chat(YDBConnection ydbConnection) throws ClientException, ApiException {

        this.ydbConnection = ydbConnection;

        this.messenger = new Messenger(new VkConnection());
        this.isActive = true;
    }

    public void close() throws Exception {
        this.isActive = false;
    }

    @Override
    public void run() {
        try {
            startChat();
        } catch (ClientException e) {
            throw new RuntimeException(e);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void startChat() throws ClientException, ApiException, InterruptedException {
        isActive = true;

        while (isActive){
            List<Message> messages = messenger.getMessages();

            if (!messages.isEmpty()){

                messages.forEach(message -> {
                    if(message.getFromId() != -messenger.vkConnection.getAPP_ID()){
                        try {
                            User user = messenger.getUser(message);

                            String messageText = message.getText().toLowerCase();

                            switch (messageText) {
                                case "начать" -> {
                                    messenger.sendMessage("Приветствую", user.getId());
                                }
                                case "подписаться" -> {
                                    messenger.sendMessage("Вы подписались", user.getId());
                                    UpsertData upsertData = new UpsertData(user, ydbConnection);
                                    upsertData.run();
                                }
                                case "отписаться" -> {
                                    messenger.sendMessage("Вы отписалмсь", user.getId());
                                    DeleteData deleteData = new DeleteData(user, ydbConnection);
                                    deleteData.run();
                                }
                                default -> messenger.sendMessage("Я тебя не понял", user.getId());
                            }

                        } catch (ApiException | ClientException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            messenger.setTs(messenger.getTs());
        }
    }
}
