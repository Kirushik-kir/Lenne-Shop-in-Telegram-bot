package Views.Owners.Branches;

import Models.db.Product.ProductShablon;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public class AddProduct {

    public static String askProductGenderText() {
        return "Выберите пол";
    }

    public static InlineKeyboardMarkup askProductGenderButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Мальчик").callbackData("BOY"));
        markupInline.addRow(new InlineKeyboardButton("Девочка").callbackData("GIRL"));
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static InlineKeyboardMarkup changeProductGenderButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Мальчик").callbackData("BOY"));
        markupInline.addRow(new InlineKeyboardButton("Девочка").callbackData("GIRL"));
        markupInline.addRow(new InlineKeyboardButton("отмена").callbackData("/ask_correct_adding_data"));
        return markupInline;
    }

    public static String askProductSeasonText() {
        return "На какой сезон одежда?";
    }

    public static InlineKeyboardMarkup askProductSeasonButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Зима").callbackData("WINTER"));
        markupInline.addRow(new InlineKeyboardButton("Осень-весна").callbackData("AUTUMN_SPRING"));
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static InlineKeyboardMarkup changeProductSeasonButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Зима").callbackData("WINTER"));
        markupInline.addRow(new InlineKeyboardButton("Осень-весна").callbackData("AUTUMN_SPRING"));
        markupInline.addRow(new InlineKeyboardButton("отмена").callbackData("/ask_correct_adding_data"));
        return markupInline;
    }

    public static String askProductTypeText() {
        return "Что это будет?";
    }

    public static InlineKeyboardMarkup askProductTypeButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Куртку / Пальто / Парку").callbackData("JACKETS"));
        markupInline.addRow(new InlineKeyboardButton("Комплект").callbackData("COMPLETES"));
        markupInline.addRow(new InlineKeyboardButton("Комбинезон").callbackData("OVERALLS"));
        markupInline.addRow(new InlineKeyboardButton("Штаны").callbackData("PANTS"));
        markupInline.addRow(new InlineKeyboardButton("Шапочку").callbackData("HATS"));
        markupInline.addRow(new InlineKeyboardButton("Краги").callbackData("LEGGINGS"));
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static InlineKeyboardMarkup changeProductTypeButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Куртку / Пальто / Парку").callbackData("JACKETS"));
        markupInline.addRow(new InlineKeyboardButton("Комплект").callbackData("COMPLETES"));
        markupInline.addRow(new InlineKeyboardButton("Комбинезон").callbackData("OVERALLS"));
        markupInline.addRow(new InlineKeyboardButton("Штаны").callbackData("PANTS"));
        markupInline.addRow(new InlineKeyboardButton("Шапочку").callbackData("HATS"));
        markupInline.addRow(new InlineKeyboardButton("Краги").callbackData("LEGGINGS"));
        markupInline.addRow(new InlineKeyboardButton("отмена").callbackData("/ask_correct_adding_data"));
        return markupInline;
    }

    public static String askProductTitleText() {
        return "Как товар будет называться?";
    }

    public static String askProductImageText() {
        return "Пришлите изображение товара";
    }

    public static String askProductDescriptionText() {
        return "Добавьте описание вашему товару";
    }

    public static String askProductSizeText() {
        return "Укажите ВСЕ размеры этого товара через запятую." + "\n" +
                "Например: 1, 1, 3, 2, 1";
    }

    public static String askProductPriceText() {
        return "Какая цена будет у товара?" + "\n" +
                "Введите просто число без каких-либо других символов";
    }

    public static String askProductCorrectDataText(ProductShablon product) {
        return product + "\n" + "Все верно?";
    }

    public static InlineKeyboardMarkup askProductCorrectDataButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Добавить").callbackData("/post"));
        markupInline.addRow(new InlineKeyboardButton("Изменить пол").callbackData("/add_product_change_gender"));
        markupInline.addRow(new InlineKeyboardButton("Изменить сезон").callbackData("/add_product_change_season"));
        markupInline.addRow(new InlineKeyboardButton("Изменить вид").callbackData("/add_product_change_type"));
        markupInline.addRow(new InlineKeyboardButton("Изменить название").callbackData("/add_product_change_title"));
        markupInline.addRow(new InlineKeyboardButton("Изменить картинку").callbackData("/add_product_change_image_id"));
        markupInline.addRow(new InlineKeyboardButton("Изменить описание").callbackData("/add_product_change_description"));
        markupInline.addRow(new InlineKeyboardButton("Изменить размеры").callbackData("/add_product_change_size"));
        markupInline.addRow(new InlineKeyboardButton("Изменить цену").callbackData("/add_product_change_price"));
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static String addedProductText() {
        return "Товар был добавлен!";
    }

    public static InlineKeyboardMarkup addNewOrBackButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.addRow(new InlineKeyboardButton("Добавить еще").callbackData("/add_new_product"));
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static InlineKeyboardMarkup backToAskProductCorrectDataButton() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("отмена").callbackData("/ask_correct_adding_data"));
        return markupInline;
    }
}
