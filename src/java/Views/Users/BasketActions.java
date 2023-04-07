package Views.Users;

import Models.db.Product.Product;
import Models.db.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public class BasketActions {
    public static String productAddedIntoBasketText() {
        return "Товар был добавлен";
    }

    public static InlineKeyboardMarkup productAddedButtons(User user) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Оформить заказ").callbackData("/buy"));
        markupInline.addRow(new InlineKeyboardButton("Корзина (" + user.getBasket().getProductList().size() + ")").callbackData("/basket"));
        markupInline.addRow(new InlineKeyboardButton("в главное меню").callbackData("/start"));
        return markupInline;
    }

    public static String showEmptyBasketText() {
        return "У вас пока пустая корзина, сначала положите в нее что-нибудь!";
    }

    public static InlineKeyboardMarkup gettingBuyingWithoutProductsButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Каталог").callbackData("/catalog"));
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static String productDeletedText() {
        return "Товар был удален";
    }

    public static InlineKeyboardMarkup deleteProductButton(Product product) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Убрать").callbackData("/remove_" + product.getId()));
        return markupInline;
    }

    public static InlineKeyboardMarkup deleteProductOrBackButtons(Product product) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Убрать").callbackData("/remove_" + product.getId()));
        markupInline.addRow(new InlineKeyboardButton("Оформить заказ").callbackData("/buy"));
        markupInline.addRow(new InlineKeyboardButton("в главное меню").callbackData("/start"));
        return markupInline;
    }
}
