package Views.Users.Branches;

import Models.Basket;
import Models.db.Product.Product;
import Models.db.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public class Buy {

    public static InlineKeyboardMarkup buyProductButton(Product product) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Купить").callbackData("/buy_" + product.getId()));
        return markupInline;
    }

    public static InlineKeyboardMarkup buyProductOrBackButtons(Product product) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Купить").callbackData("/buy_" + product.getId()));
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static String sendCommentText() {
        return "Напишите комментарий";
    }

    public static String yourOrderText(Basket basket, String comment, User user) {
        StringBuilder orders = new StringBuilder("Ваш заказ: " + "\n\n");
        for (Product product : basket.getProductList()) {
            orders.append(product.showInOrder()).append("\n\n\n");
        }
        orders.append("Имя: ").append(user.getFirst_name()).append("\n");
        orders.append("Почта: ").append(user.getEmail()).append("\n");
        orders.append("Телефон: ").append(user.getPhone()).append("\n\n");
        orders.append("Комментарий: ").append(comment == null ? "без комментария" : comment).append("\n\n");
        orders.append("Сумма вашего заказа: ").append(basket.getSum()).append(" руб.");
        return orders.toString();
    }

    public static InlineKeyboardMarkup yourOrderButtons(User user) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Оформить заказ").callbackData("/finish_an_order"));
        markupInline.addRow(new InlineKeyboardButton("Корзина (" + user.getBasket().getProductList().size() + ")").callbackData("/basket"));
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static InlineKeyboardMarkup askCorrectDataButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Да, все верно").callbackData("/write_comment"));
        markupInline.addRow(new InlineKeyboardButton("Нет, изменить данные").callbackData("/edit_data"));
        markupInline.addRow(new InlineKeyboardButton("Назад").callbackData("/start"));
        return markupInline;
    }

    public static InlineKeyboardMarkup backToChangeDataButton() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Отмена").callbackData("/ask_correct_data"));
        return markupInline;
    }

    public static InlineKeyboardMarkup toArrangeAnOrderButton() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Продолжить без комментариев").callbackData("/arrange_an_order"));
        return markupInline;
    }

    public static String sendOrderText(int orderId) {
        return "Ваш заказ принят." + "\n" +
               "Номер вашего заказа: " + orderId + "\n\n" +
               "Скоро с вами свяжутся из магазина, если вы правильно ввели данные";
    }

    public static InlineKeyboardMarkup sendOrderButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("в главное меню").callbackData("/start"));
        return markupInline;
    }
}
