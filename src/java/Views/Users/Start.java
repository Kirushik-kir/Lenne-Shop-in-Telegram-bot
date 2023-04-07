package Views.Users;

import Models.db.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public class Start {
    public static String startText(User user, int newUsersToday, int oldUsersToday, int actionsMade, int usersInDataBase) {
        if (user.isAdmin())
            return "Это интернет магазин lenne-shop в telegram." + "\n" + "Здесь все, как в интернет-магазине, только удобнее" + "\n\n" +
                    "Статистика:" + "\n" +
                    "Новые покупатели: " + newUsersToday + "\n" +
                    "Старые покупатели: " + oldUsersToday + "\n" +
                    "Переходов по страницам: " + actionsMade + "\n" +
                    "Всего пользователей: " + usersInDataBase;
        else
            return "Это интернет магазин lenne-shop в telegram." + "\n" + "Здесь все, как в интернет-магазине, только удобнее";
    }

    public static InlineKeyboardMarkup startButtons(User user) {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Каталог").callbackData("/catalog"),
                new InlineKeyboardButton("Оформить заказ").callbackData("/buy")
        );

        markupInline.addRow(new InlineKeyboardButton("Рекомендации").callbackData("/recommendations"));

        markupInline.addRow(new InlineKeyboardButton("Контакты").callbackData("/contacts"),
                new InlineKeyboardButton("Сайт").url("http://www.lenne-shop.com")
        );
        markupInline.addRow(new InlineKeyboardButton("Корзина (" + user.getBasket().getProductList().size() + ")").callbackData("/basket")
        );

        if (user.isAdmin()) {
            markupInline.addRow(new InlineKeyboardButton("Добавить").callbackData("/add_new_product"),
                    new InlineKeyboardButton("Архив").callbackData("/return_product"));
            markupInline.addRow(new InlineKeyboardButton("Изменить рекомендации").callbackData("/set_recommendations"));
        }

        return markupInline;
    }

    public static InlineKeyboardMarkup backToMainMenuButton() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

}
