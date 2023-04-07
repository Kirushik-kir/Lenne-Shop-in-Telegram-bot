package Views.Owners;

import Models.db.Product.Product;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public class ArchiveActions {

    public static String emptyArchiveText() {
        return "В архиве пока ничего нет";
    }

    public static InlineKeyboardMarkup archiveModsButtons(Product product) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Вернуть в поиск").callbackData("/set_visible_" + product.getId()));
        markupInline.addRow(new InlineKeyboardButton("Удалить навсегда").callbackData("/restore_" + product.getId()));
        return markupInline;
    }

    public static InlineKeyboardMarkup archiveModsAndBackButtons(Product product) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Вернуть в поиск").callbackData("/set_visible_" + product.getId()));
        markupInline.addRow(new InlineKeyboardButton("Удалить навсегда").callbackData("/delete_" + product.getId()));
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static String wasDeleted(){
        return "Товар был удален навсегда";
    }
}
