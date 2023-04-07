package Views.Users.Branches;


import Models.db.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public class Authorization {
    public static String askFirst_nameText() {
        return """
                1/3
                               
                Введите ваше имя:
                """;
    }

    public static String askEmailText() {
        return """
                2/3
                               
                Введите ваш email:
                """;
    }

    public static String askPhoneText() {
        return """
                3/3
                               
                Введите ваш номер телефона:
                """;
    }

    public static String askCorrectDataText(User user) {
        return "Ваши данные: " + "\n\n" +
                "имя: " + user.getFirst_name() + "\n" +
                "email: " + user.getEmail() + "\n" +
                "телефон: " + user.getPhone() + "\n\n" +
                "Все верно?";
    }

    public static String askWhatToEdit() {
        return "Выберите, что изменить: ";
    }

    public static InlineKeyboardMarkup editsDataButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Изменить имя").callbackData("/edit_first_name"));
        markupInline.addRow(new InlineKeyboardButton("Изменить почту").callbackData("/edit_email"));
        markupInline.addRow(new InlineKeyboardButton("Изменить телефон").callbackData("/edit_phone"));
        markupInline.addRow(new InlineKeyboardButton("Назад").callbackData("/buy"));
        return markupInline;
    }
}
