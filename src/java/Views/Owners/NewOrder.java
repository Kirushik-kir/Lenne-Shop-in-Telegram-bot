package Views.Owners;

import Models.db.Product.Product;
import Models.Order;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public class NewOrder {

    public static String newOrderText(Order order) {
        StringBuilder orders = new StringBuilder();
        for (Product product : order.getUser().getBasket().getProductList()) {
            orders.append(product.showInOrder()).append("\n");
        }
        return "Новый заказ от " + order.getUser().getFirst_name() + ":" + "\n\n" +
                orders + "\n" +

                "Имя в тг: " + order.getUser().getTelegram_first_name() + "\n" +
                "Фамилия в тг: " + order.getUser().getTelegram_last_name() + "\n" +
                "Ник в тг: " + order.getUser().getUsername() + "\n\n" +

                "Имя: " + order.getUser().getFirst_name() + "\n" +
                "Почта: " + order.getUser().getEmail() + "\n" +
                "Телефон: " + order.getUser().getPhone() + "\n" +
                "Сумма: " + order.getUser().getBasket().getSum() + " руб." + "\n\n" +
                "Комментарий: " + ((order.getComment() == null) ? "без комментария" : order.getComment());
    }

    public static InlineKeyboardMarkup finishedOrderButtons(Order order) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        short count = 0;
        for (Product product : order.getUser().getBasket().getProductList()) {
            count++;
            markupInline.addRow(new InlineKeyboardButton(count + " - Убрать из выдачи " + product.getTitle()).callbackData("/set_invisible_" + product.getId()));
            markupInline.addRow(new InlineKeyboardButton(count + " - Вернуть в поиск " + product.getTitle()).callbackData("/set_visible_" + product.getId()));
        }
        return markupInline;
    }

    public static String wasSetInvisible(){
        return "Товар был убран из выдачи";
    }

    public static String wasSetVisible(){
        return "Товар был возвращен в магазин";
    }
}
