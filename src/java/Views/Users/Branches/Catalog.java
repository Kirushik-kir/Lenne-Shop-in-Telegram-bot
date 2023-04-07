package Views.Users.Branches;

import Models.db.Product.Search;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.ArrayList;

public class Catalog {
    public static String gendersQuestionText() {
        return """
                1/4
                               
                Кому вы выбираете одежду?
                """;
    }

    public static InlineKeyboardMarkup gendersButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Мальчику").callbackData("/select_boy"));
        markupInline.addRow(new InlineKeyboardButton("Девочке").callbackData("/select_girl"));
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static String seasonsQuestionText() {
        return """
                2/4
                               
                На какой сезон вы присматриваете одежду?
                """;
    }

    public static InlineKeyboardMarkup seasonsButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Зима").callbackData("/select_winter"));
        markupInline.addRow(new InlineKeyboardButton("Осень-весна").callbackData("/select_autumn_spring"));
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static String typeQuestionText() {
        return """
                3/4
                               
                Что вы ищете?
                """;
    }

    public static InlineKeyboardMarkup typesButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.addRow(new InlineKeyboardButton("Куртку / Пальто / Парку").callbackData("/select_jackets"));
        markupInline.addRow(new InlineKeyboardButton("Комплект").callbackData("/select_completes"));
        markupInline.addRow(new InlineKeyboardButton("Комбинезон").callbackData("/select_overalls"));
        markupInline.addRow(new InlineKeyboardButton("Штаны").callbackData("/select_pants"));
        markupInline.addRow(new InlineKeyboardButton("Шапочку").callbackData("/select_hats"));
        markupInline.addRow(new InlineKeyboardButton("Краги").callbackData("/select_leggings"));
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static String sizeQuestionText() {
        return """
                4/4
                               
                Размеры в наличии:
                """;
    }

    public static InlineKeyboardMarkup sizeQuestionButtons(ArrayList<Integer> list) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        for (Integer size : list) {
            markupInline.addRow(new InlineKeyboardButton(size.toString()).callbackData("/size_" + size));
        }
        markupInline.addRow(new InlineKeyboardButton("назад").callbackData("/start"));
        return markupInline;
    }

    public static String noSizesFoundedText() {
        return "Ничего не найдено";
    }

    public static String noProductsFoundText(Search search) {
        return "Извините, но по вашему запросу ничего не найдено: " + "\n" +
                search.getType() + " на " + search.getGender() + " с размером " + search.getSize() + "\n\n" +
                "Пока вы выбирали, эту вещь уже купили";
    }
}
