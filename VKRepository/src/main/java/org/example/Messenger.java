package org.example;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import org.example.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Messenger {
    final VkConnection vkConnection;
    final VkApiClient vk;
    private final GroupActor groupActor;
    private final Keyboard keyboard;
    private int ts;
    private final Random random;

    public Messenger(VkConnection vkConnection) throws ClientException, ApiException {
        this.vkConnection = vkConnection;

        this.vk = vkConnection.getVk();
        this.groupActor = vkConnection.getGroupActor();

        this.keyboard = new Keyboard();

        this.ts = vk.messages().getLongPollServer(groupActor).execute().getTs();

        this.random = new Random();
    }

    public Keyboard setKeyboard(){
        List<List<KeyboardButton>> allKey = new ArrayList<>();
        List<KeyboardButton> line1 = new ArrayList<>();
        line1.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("Подписаться").setType(TemplateActionTypeNames.TEXT)).setColor(KeyboardButtonColor.POSITIVE));
        line1.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("Отписаться").setType(TemplateActionTypeNames.TEXT)).setColor(KeyboardButtonColor.POSITIVE));
        allKey.add(line1);
        keyboard.setButtons(allKey);

        return keyboard;
    }

    public List<Message> getMessages() throws ClientException, ApiException {
        MessagesGetLongPollHistoryQuery historyQuery =  vk.messages().getLongPollHistory(groupActor).ts(ts);
        return historyQuery.execute().getMessages().getItems();
    }

    public Integer getTs() throws ClientException, ApiException {
        return vk.messages().getLongPollServer(groupActor).execute().getTs();
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public User getUser(Message message) throws ClientException, ApiException {

        List<GetResponse> getInfo = vk.users()
                .get(new UserActor(message.getFromId(), vkConnection.getAPP_CODE()))
                .userIds(message.getFromId().toString())
                .execute();

        return new User(getInfo.get(0).getId(), getInfo.get(0).getFirstName(),  getInfo.get(0).getLastName());
    }

    public void sendMessage(String text, Integer id) throws ClientException, ApiException {
        vk.messages().send(groupActor).message(text).userId(id).randomId(random.nextInt(10000)).keyboard(setKeyboard()).execute();
    }
}
