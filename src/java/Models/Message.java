package Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@ToString
public class Message {
    private int messageId; //ид сообщения
    private long chatId; //ид чата
    private long senderId; //ид отправителя
    private String text; //текст, отправленный пользователем ИЛИ ДАТА CallBackQuery
    private String buttonText; //необязательное поле - текст над кнопкой
    private Object[] image; //необязательное поле - id картинки
    private Timestamp time; //время, когда написали это сообщение

    public Message(int messageId, long chatId, long senderId, String text, Timestamp time) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.text = text;
        this.time = time;
    }

    public Message(int messageId, long chatId, long senderId, String text, String buttonText, Timestamp time) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.text = text;
        this.buttonText = buttonText;
        this.time = time;
    }

    public Message(int messageId, long chatId, long senderId, String text, Object[] image, Timestamp time) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.text = text;
        this.image = image;
        this.time = time;
    }
}