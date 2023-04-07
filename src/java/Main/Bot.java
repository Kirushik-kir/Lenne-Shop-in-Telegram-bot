package Main;

import Exceptions.*;
import MessageHandlers.Statuses.administration.OwnerStatuses;
import MessageHandlers.Statuses.users.UserStatuses;
import Models.*;
import Models.db.Product.Product;
import Models.db.Product.ProductShablon;
import Models.db.Product.Search;
import Models.db.User;
import Models.Product.Categories.Genders;
import Models.Product.Categories.Seasons;
import Models.Product.Categories.Types;
import Views.Owners.ArchiveActions;
import Views.Owners.Branches.AddProduct;
import Views.Owners.NewOrder;
import Views.Owners.SetRecommendations;
import Views.Owners.Statistic;
import Views.Users.BasketActions;
import Views.Users.Branches.Authorization;
import Views.Users.Branches.Buy;
import Views.Users.Branches.Catalog;
import Views.Users.Recommendations;
import Views.Users.Start;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.sql.*;
import java.util.*;

import static MessageHandlers.Statuses.administration.OwnerStatuses.*;
import static MessageHandlers.Statuses.users.UserStatuses.*;
import static Models.Product.Categories.Types.HATS;
import static Models.Product.Categories.Types.LEGGINGS;
import static Views.Owners.ArchiveActions.archiveModsAndBackButtons;
import static Views.Owners.ArchiveActions.archiveModsButtons;
import static Views.Owners.Branches.AddProduct.*;
import static Views.Owners.NewOrder.finishedOrderButtons;
import static Views.Users.BasketActions.*;
import static Views.Users.Branches.Authorization.editsDataButtons;
import static Views.Users.Branches.Buy.*;
import static Views.Users.Branches.Catalog.*;
import static Views.Users.Contacts.contactsText;
import static Views.Users.Start.backToMainMenuButton;
import static Views.Users.Start.startButtons;

@SpringBootApplication
public class Bot {

    private static final String TOKEN = "";
    private static final String URL = "jdbc:mysql://localhost/lenne_shop?serverTimezone=Europe/Moscow&useSSL=false&useUnicode=true&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static final long ADMIN = 1814103166;
    private static final long OWNER = 1814103166;

    private static User user = new User();
    private static Message message = new Message();
    private final static HashMap<Long, Basket> baskets = new HashMap<>();
    private final static HashMap<Long, Search> searches = new HashMap<>();
    private final static HashMap<Long, ProductShablon> addingProducts = new HashMap<>();
    private final static HashMap<Long, String> comments = new HashMap<>();

    private static final TelegramBot bot = new TelegramBot(TOKEN); //регаем бота

    private static Connection connection;
    private static Statement statement;

    //открываем бд
    static {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            statement = connection.createStatement();

            System.out.println("Подключение прошло успешно");
        } catch (SQLException e) {
            System.out.println("Подключение не удалось");
            bot.execute(new SendMessage(ADMIN, "Ошибка подключения к базе данных"));
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

//        Properties properties = new Properties();
//
//        String host = "mail.smtp.host";
//        String port = "465";
//        String mailFrom = "";
//        String password = "";
//
//        properties.put("mail.smtp.host", host);
//        properties.put("mail.smtp.port", port);
//        properties.put("spring.mail.from", mailFrom);
//        properties.put("spring.mail.password", password);
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.starttls.enable", "true");
//        //properties.put("mail.smtp.ssl.trust","mail.man.com");
//
//        Session session = Session.getInstance(properties);
//
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//
//        MimeMessage mail = new MimeMessage(session);
//        try {
//            mail.setRecipients(MimeMessage.RecipientType.TO, "velid5@mail.ru");
//            mail.setSubject("subject");
//            mail.setFrom("velid5@mail.ru");
//            mail.setText("Hello world!");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//
//        mailSender.send(mail);

        addingProducts.put(ADMIN, new ProductShablon());
        addingProducts.put(OWNER, new ProductShablon());

        bot.setUpdatesListener(updates -> {
            updates.forEach(System.out::println);
            updates.forEach(update -> {

                try {

                    if (update.message() == null) {
                        if (update.callbackQuery() != null) {
                            message = new Message(update.callbackQuery().message().messageId(),
                                    update.callbackQuery().message().chat().id(),
                                    update.callbackQuery().from().id(),
                                    update.callbackQuery().data(),
                                    update.callbackQuery().message().text(),
                                    new Timestamp((long) update.callbackQuery().message().date() * 1000)
                            );
                        }

                    } else {
                        if (update.message().photo() != null) {
                            message = new Message(update.message().messageId(),
                                    update.message().chat().id(),
                                    update.message().from().id(),
                                    null,
                                    Arrays.stream(update.message().photo()).toArray(),
                                    new Timestamp((long) update.message().date() * 1000)
                            );
                        } else
                            message = new Message(update.message().messageId(),
                                    update.message().chat().id(),
                                    update.message().from().id(),
                                    update.message().text(),
                                    new Timestamp((long) update.message().date() * 1000)
                            );
                        System.out.println(message);
                    }

                    try {
                        if (Statistic.getTheLastDay(connection).getDate() != message.getTime().getDate()){
                            try {
                                Statistic.startNewDay(connection);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                bot.execute(new SendMessage(ADMIN, "Не получилось запустить новый день в базе данных статистики - SQLException"));
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        bot.execute(new SendMessage(ADMIN, "Не получилось узнать последний день статистики - SQLException"));
                    }

                    try {
                        Statistic.increaseActionsMade(connection);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        bot.execute(new SendMessage(ADMIN, "Не получилось добавить действие - SQLException"));
                    }

                    boolean[] isCommand = new boolean[1];

                    isCommand[0] = message.getText() != null;

                    if (message != null) {

                        if (!isInDataBase(message.getChatId(), statement)) {
                            if (update.message() != null) {
                                user = new User(message.getSenderId(),
                                        update.message().chat().firstName(),
                                        update.message().chat().lastName(),
                                        update.message().chat().username(),
                                        new Basket());
                            } else {
                                user = new User(message.getSenderId(),
                                        update.callbackQuery().from().firstName(),
                                        update.callbackQuery().from().lastName(),
                                        update.callbackQuery().from().username(),
                                        new Basket());
                            }
                            try {
                                user.addToDatabase(connection, message.getTime());
                                baskets.put(user.getId(), new Basket(user.getId(), new ArrayList<>()));
                            } catch (CommunicationsException e){
                                initializeConnection();
                            } catch (SQLException e) {
                                bot.execute(new SendMessage(ADMIN, new AddPersonToDatabaseException().getDescription(user)));
                                initializeConnection();
                            }
                            System.out.println("пользователь был добавлен " + user.getId());
                            try {
                                Statistic.increaseNewUsersToday(connection);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                bot.execute(new SendMessage(ADMIN, "Не получилось увеличить значение счетчика новых пользователей - SQLException"));
                            }
                        } else {
                            user = findPerson(message.getSenderId(), statement);
                            System.out.println("он уже в базе");
                            if(user.getLast_visit() != null)
                            if(new java.sql.Date(user.getLast_visit().getTime()).getDate() != new java.sql.Date(message.getTime().getTime()).getDate()){
                                try {
                                    Statistic.increaseOldUsersToday(connection);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    bot.execute(new SendMessage(ADMIN, "Не получилось увеличить значение счетчика старых пользователей - SQLException"));
                                }
                            }
                            try {
                                user.setLast_visit(message.getTime(), connection);
                            } catch (CommunicationsException e){
                                initializeConnection();
                            }catch (SQLException e) {
                                e.printStackTrace();
                                bot.execute(new SendMessage(ADMIN, new AddPersonToDatabaseException().getDescription(user)));
                            }
                        }

                        if (message.getText() != null) {

                            if ((message.getText().contains("/buy_")) || (message.getText().contains("/remove_")) || (message.getText().contains("/set_invisible_")) || (message.getText().contains("/set_visible_")) || (message.getText().contains("/delete_")) || (message.getText().contains("/size_"))) {

                                if (message.getText().contains("/buy_")) {
                                    deleteCurrentMessage();
                                    Product product = findProduct(Short.parseShort(message.getText().replace("/buy_", "")), statement);
                                    baskets.get(user.getId()).getProductList().add(product);
                                    bot.execute(new SendMessage(message.getChatId(), BasketActions.productAddedIntoBasketText()).replyMarkup(productAddedButtons(user)));
                                }

                                if (message.getText().contains("/remove_")) {
                                    deleteCurrentMessage();
                                    Product product = findProduct(Short.parseShort(message.getText().replace("/remove_", "")), statement);
                                    baskets.get(user.getId()).getProductList().remove(product);
                                    bot.execute(new SendMessage(message.getChatId(), BasketActions.productDeletedText()).replyMarkup(productAddedButtons(user)));
                                }

                                if (message.getText().contains("/set_invisible_")) {
                                    long id = Long.parseLong(message.getText().replace("/set_invisible_", ""));
                                    try {
                                        statement.executeUpdate("update products set time_bought = '" + message.getTime() + "' where id = " + id);
                                        bot.execute(new SendMessage(message.getChatId(), NewOrder.wasSetInvisible()).replyMarkup(Start.backToMainMenuButton()));
                                    } catch (SQLException e) {
                                        initializeConnection();
                                        e.printStackTrace();
                                    }
                                }

                                if (message.getText().contains("/set_visible_")) {
                                    long id = Long.parseLong(message.getText().replace("/set_visible_", ""));
                                    try {
                                        statement.executeUpdate("update products set time_bought = null where id = " + id);
                                        bot.execute(new SendMessage(message.getChatId(), NewOrder.wasSetVisible()).replyMarkup(Start.backToMainMenuButton()));
                                    } catch (SQLException e) {
                                        initializeConnection();
                                        e.printStackTrace();
                                    }
                                }

                                if (message.getText().contains("/delete_")) {
                                    long id = Long.parseLong(message.getText().replace("/delete_", ""));
                                    try {
                                        statement.executeUpdate("delete from products where id = " + id);
                                        bot.execute(new SendMessage(message.getChatId(), ArchiveActions.wasDeleted()).replyMarkup(Start.backToMainMenuButton()));
                                    } catch (SQLException e) {
                                        initializeConnection();
                                        e.printStackTrace();
                                    }
                                }

                                if (message.getText().contains("/size_")) {
                                    try {
                                        searches.get(user.getId()).setSize(Integer.parseInt((message.getText().replace("/size_", ""))));
                                        deleteLastMessage();
                                        deleteCurrentMessage();
                                        statement.executeUpdate("update users set status = 'NOTHING' where id = " + user.getId());
                                        ArrayList<Product> catalog = findClothes(searches.get(user.getId()), statement);
                                        if (!catalog.isEmpty()) {
                                            for (int i = 0; i < catalog.size() - 1; i++) {
                                                Product product = catalog.get(i);
                                                bot.execute(new SendPhoto(message.getChatId(), product.getImage_id()).caption(product.toString()).replyMarkup(buyProductButton(product)));
                                            }
                                            Product product = catalog.get(catalog.size() - 1);
                                            bot.execute(new SendPhoto(message.getChatId(), product.getImage_id()).caption(product.toString()).replyMarkup(buyProductOrBackButtons(product)));
                                        } else
                                            bot.execute(new SendMessage(message.getChatId(), Catalog.noProductsFoundText(searches.get(user.getId()))).replyMarkup(backToMainMenuButton()));
                                    } catch (SQLException e) {
                                        initializeConnection();
                                        e.printStackTrace();
                                        bot.execute(new SendMessage(ADMIN, "Не получилось выдать товары - SQLException"));
                                    }
                                }

                                isCommand[0] = true;

                            } else

                                try {

                                    switch (message.getText().toLowerCase(Locale.ROOT)) {

                                        case ("/start"): {
                                                deleteCurrentMessage();
                                            try {
                                                user.setStatus(NOTHING, connection);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                bot.execute(new SendMessage(message.getChatId(), Start.startText(user, Statistic.getNewUsersToday(connection), Statistic.getOldUsersToday(connection), Statistic.getActionsMade(connection), Statistic.getUsersInDataBase(connection))).replyMarkup(startButtons(user)));
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                                bot.execute(new SendMessage(ADMIN, "Не получилось показать статистику"));
                                            }
                                            break;
                                        }

                                        case ("/catalog"): {
                                            if (message.getButtonText() != null) {
                                                deleteCurrentMessage();
                                            }
                                            bot.execute(new SendMessage(message.getChatId(), Catalog.gendersQuestionText()).replyMarkup(gendersButtons()));
                                            searches.putIfAbsent(user.getId(), new Search());
                                            break;
                                        }

                                        case ("/recommendations"): {
                                            deleteCurrentMessage();
                                            try {
                                                bot.execute(new SendMessage(message.getChatId(), Recommendations.recommendationsText()).replyMarkup(backToMainMenuButton()));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            searches.putIfAbsent(user.getId(), new Search());
                                            break;
                                        }

                                        case ("/select_boy"),
                                                ("/select_girl"): {
                                            deleteCurrentMessage();
                                            searches.get(user.getId()).setGender(Genders.valueOf(message.getText().replace("/select_", "").toUpperCase(Locale.ROOT)));
                                            bot.execute(new SendMessage(message.getChatId(), Catalog.seasonsQuestionText()).replyMarkup(seasonsButtons()));
                                            break;
                                        }

                                        case ("/select_winter"),
                                                ("/select_autumn_spring"): {
                                            deleteCurrentMessage();

                                            searches.get(user.getId()).setSeason(Seasons.valueOf(message.getText().replace("/select_", "").toUpperCase(Locale.ROOT)));
                                            bot.execute(new SendMessage(message.getChatId(), Catalog.typeQuestionText()).replyMarkup(typesButtons()));
                                            break;
                                        }

                                        case ("/select_completes"),
                                                ("/select_overalls"),
                                                ("/select_jackets"),
                                                ("/select_pants"),
                                                ("/select_hats"),
                                                ("/select_leggings"): {
                                            deleteCurrentMessage();
                                            searches.get(user.getId()).setType(Types.valueOf(message.getText().replace("/select_", "").toUpperCase(Locale.ROOT)));
                                            try {
                                                ArrayList<Integer> sizes = Product.getSizes(searches.get(user.getId()), connection);
                                                if (sizes.isEmpty())
                                                    bot.execute(new SendMessage(message.getChatId(), Catalog.noSizesFoundedText()).replyMarkup(backToMainMenuButton()));
                                                else
                                                    bot.execute(new SendMessage(message.getChatId(), Catalog.sizeQuestionText()).replyMarkup(sizeQuestionButtons(sizes)));
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                                bot.execute(new SendMessage(ADMIN, "Не получить достать все размеры - SQLException"));
                                            }
                                            break;
                                        }

                                        case ("/buy"): {
                                            deleteCurrentMessage();
                                            if (!user.getBasket().getProductList().isEmpty()) {
                                                if (user.getPhone() == null) {
                                                    bot.execute(new SendMessage(message.getChatId(), Authorization.askFirst_nameText()).replyMarkup(backToMainMenuButton()));
                                                    try {
                                                        user.setStatus(FIRST_NAME, connection);
                                                    } catch (SQLException e) {
                                                        e.printStackTrace();
                                                    }
                                                } else
                                                    bot.execute(new SendMessage(message.getChatId(), Authorization.askCorrectDataText(user)).replyMarkup(askCorrectDataButtons()));
                                            } else
                                                bot.execute(new SendMessage(message.getChatId(), BasketActions.showEmptyBasketText()).replyMarkup(gettingBuyingWithoutProductsButtons()));
                                            break;
                                        }

                                        case ("/ask_correct_data"): {
                                            deleteCurrentMessage();
                                            bot.execute(new SendMessage(message.getChatId(), Authorization.askCorrectDataText(user)).replyMarkup(askCorrectDataButtons()));
                                            break;
                                        }

                                        case ("/edit_data"): {
                                            deleteCurrentMessage();
                                            bot.execute(new SendMessage(message.getChatId(), Authorization.askWhatToEdit()).replyMarkup(editsDataButtons()));
                                            break;
                                        }

                                        case ("/edit_first_name"): {
                                            deleteCurrentMessage();

                                            try {
                                                user.setStatus(EDIT_FIRST_NAME, connection);
                                            } catch (SQLException e) {
                                                initializeConnection();
                                                e.printStackTrace();
                                                bot.execute(new SendMessage(ADMIN, "Ошибка с изменением имени - SQLException"));
                                            }
                                            bot.execute(new SendMessage(message.getChatId(), Authorization.askFirst_nameText()).replyMarkup(backToChangeDataButton()));
                                            break;
                                        }

                                        case ("/edit_email"): {
                                            deleteCurrentMessage();
                                            try {
                                                user.setStatus(EDIT_EMAIL, connection);
                                            } catch (SQLException e) {
                                                initializeConnection();
                                                e.printStackTrace();
                                                bot.execute(new SendMessage(ADMIN, "Ошибка с изменением почты - SQLException"));
                                            }
                                            bot.execute(new SendMessage(message.getChatId(), Authorization.askEmailText()).replyMarkup(backToChangeDataButton()));
                                            break;
                                        }

                                        case ("/edit_phone"): {
                                            deleteCurrentMessage();
                                            try {
                                                user.setStatus(EDIT_PHONE, connection);
                                            } catch (SQLException e) {
                                                initializeConnection();
                                                e.printStackTrace();
                                                bot.execute(new SendMessage(ADMIN, "Ошибка с изменением телефона - SQLException"));
                                            }
                                            bot.execute(new SendMessage(message.getChatId(), Authorization.askPhoneText()).replyMarkup(backToChangeDataButton()));
                                            break;
                                        }

                                        case ("/write_comment"): {
                                            deleteCurrentMessage();
                                            try {
                                                user.setStatus(COMMENT, connection);
                                                bot.execute(new SendMessage(message.getChatId(), Buy.sendCommentText()).replyMarkup(toArrangeAnOrderButton()));
                                            } catch (SQLException e) {
                                                initializeConnection();
                                                e.printStackTrace();
                                                bot.execute(new SendMessage(ADMIN, "Ошибка с добавлением комментария - SQLException"));
                                            }
                                            break;
                                        }

                                        case ("/arrange_an_order"): {
                                            deleteCurrentMessage();
                                            bot.execute(new SendMessage(message.getChatId(), Buy.yourOrderText(user.getBasket(), comments.get(user.getId()), user)).replyMarkup(Buy.yourOrderButtons(user)));
                                            break;
                                        }

                                        case ("/finish_an_order"): {
                                            deleteCurrentMessage();
                                            int commentId = 0;
                                            try {
                                                statement.executeUpdate("insert into comments (comment, ordered_time) values ('" + comments.get(user.getId()) + "', '" + message.getTime() + "')");
                                                ResultSet result = statement.executeQuery("SELECT id FROM comments ORDER BY id DESC");
                                                if (result.next()) commentId = result.getInt("id");
                                                initializeConnection();
                                                for (Product product : user.getBasket().getProductList()) {
                                                    statement.executeUpdate("insert into orders(comment_id, user_id, product_id) values (" + commentId + ", " + user.getId() + ", " + product.getId() + ")");
                                                }
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                                bot.execute(new SendMessage(ADMIN, "Не получилось добавить заказ в базу данных - SQLException"));
                                            }
                                            Order order = new Order(user, comments.get(user.getId()), message.getTime());
                                            bot.execute(new SendMessage(ADMIN, NewOrder.newOrderText(order)).replyMarkup(finishedOrderButtons(order)));
                                            bot.execute(new SendMessage(OWNER, NewOrder.newOrderText(order)).replyMarkup(finishedOrderButtons(order)));
                                            bot.execute(new SendMessage(message.getChatId(), Buy.sendOrderText(commentId)).replyMarkup(Buy.sendOrderButtons()));
                                            baskets.replace(user.getId(), new Basket(user.getId(), new ArrayList<>()));
                                            comments.remove(user.getId());
                                            break;
                                        }

                                        case ("/contacts"): {
                                                deleteCurrentMessage();
                                            bot.execute(new SendMessage(message.getChatId(), contactsText()).replyMarkup(backToMainMenuButton()));
                                            break;
                                        }

                                        case ("/basket"): {
                                            deleteCurrentMessage();
                                            if (!user.getBasket().getProductList().isEmpty()) {
                                                for (int i = 0; i < user.getBasket().getProductList().size() - 1; i++) {
                                                    Product product = user.getBasket().getProductList().get(i);
                                                    bot.execute(new SendPhoto(message.getChatId(), product.getImage_id()).caption(product.toString()).replyMarkup(deleteProductButton(product)));
                                                }
                                                Product product = user.getBasket().getProductList().get(user.getBasket().getProductList().size() - 1);
                                                bot.execute(new SendPhoto(message.getChatId(), product.getImage_id()).caption(product.toString()).replyMarkup(deleteProductOrBackButtons(product)));
                                            } else
                                                bot.execute(new SendMessage(message.getChatId(), BasketActions.showEmptyBasketText()).replyMarkup(gettingBuyingWithoutProductsButtons()));
                                            break;
                                        }

                                        case ("/add_new_product"): {
                                            deleteCurrentMessage();
                                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                                bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductGenderText()).replyMarkup(askProductGenderButtons()));
                                                try {
                                                    user.setStatus(ADD_PRODUCT_GENDER, connection);
                                                } catch (SQLException e) {
                                                    initializeConnection();
                                                    e.printStackTrace();
                                                }
                                            } else
                                                throw new AccessException();
                                            break;
                                        }

                                        case ("/ask_correct_adding_data"): {
                                            if (message.getButtonText() != null) {
                                                deleteCurrentMessage();
                                            }
                                            bot.execute(new SendPhoto(message.getChatId(), addingProducts.get(user.getId()).getImage_id()).caption(AddProduct.askProductCorrectDataText(addingProducts.get(user.getId()))).replyMarkup(askProductCorrectDataButtons()));
                                            break;
                                        }

                                        case ("/add_product_change_gender"): {
                                            deleteCurrentMessage();
                                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                                bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductGenderText()).replyMarkup(changeProductGenderButtons()));
                                                try {
                                                    user.setStatus(EDIT_PRODUCT_GENDER, connection);
                                                } catch (SQLException e) {
                                                    initializeConnection();
                                                    e.printStackTrace();
                                                }
                                            } else
                                                throw new AccessException();
                                            break;
                                        }

                                        case ("/add_product_change_season"): {
                                            deleteCurrentMessage();
                                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                                bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductSeasonText()).replyMarkup(changeProductSeasonButtons()));
                                                try {
                                                    user.setStatus(EDIT_PRODUCT_SEASON, connection);
                                                } catch (SQLException e) {
                                                    initializeConnection();
                                                    e.printStackTrace();
                                                }
                                            } else
                                                throw new AccessException();
                                            break;
                                        }

                                        case ("/add_product_change_type"): {
                                            deleteCurrentMessage();
                                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                                bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductTypeText()).replyMarkup(changeProductTypeButtons()));
                                                try {
                                                    user.setStatus(EDIT_PRODUCT_TYPE, connection);
                                                } catch (SQLException e) {
                                                    initializeConnection();
                                                    e.printStackTrace();
                                                }
                                            } else
                                                throw new AccessException();
                                            break;
                                        }

                                        case ("/add_product_change_title"): {
                                            deleteCurrentMessage();
                                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                                bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductTitleText()).replyMarkup(backToAskProductCorrectDataButton()));
                                                try {
                                                    user.setStatus(EDIT_PRODUCT_TITLE, connection);
                                                } catch (SQLException e) {
                                                    initializeConnection();
                                                    e.printStackTrace();
                                                }
                                            } else
                                                throw new AccessException();
                                            break;
                                        }

                                        case ("/add_product_change_image"): {
                                            deleteCurrentMessage();
                                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                                bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductImageText()).replyMarkup(backToAskProductCorrectDataButton()));
                                                try {
                                                    user.setStatus(EDIT_PRODUCT_IMAGE, connection);
                                                } catch (SQLException e) {
                                                    initializeConnection();
                                                    e.printStackTrace();
                                                }
                                            } else
                                                throw new AccessException();
                                            break;
                                        }

                                        case ("/add_product_change_description"): {
                                            deleteCurrentMessage();
                                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                                bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductDescriptionText()).replyMarkup(backToAskProductCorrectDataButton()));
                                                try {
                                                    user.setStatus(EDIT_PRODUCT_DESCRIPTION, connection);
                                                } catch (SQLException e) {
                                                    initializeConnection();
                                                    e.printStackTrace();
                                                }
                                            } else
                                                throw new AccessException();
                                            break;
                                        }

                                        case ("/add_product_change_size"): {
                                            deleteCurrentMessage();
                                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                                bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductSizeText()).replyMarkup(backToAskProductCorrectDataButton()));
                                                try {
                                                    user.setStatus(EDIT_PRODUCT_SIZE, connection);
                                                } catch (SQLException e) {
                                                    initializeConnection();
                                                    e.printStackTrace();
                                                }
                                            } else
                                                throw new AccessException();
                                            break;
                                        }

                                        case ("/add_product_change_price"): {
                                            deleteCurrentMessage();
                                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                                bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductPriceText()).replyMarkup(backToAskProductCorrectDataButton()));
                                                try {
                                                    user.setStatus(EDIT_PRODUCT_PRICE, connection);
                                                } catch (SQLException e) {
                                                    initializeConnection();
                                                    e.printStackTrace();
                                                }
                                            } else
                                                throw new AccessException();
                                            break;
                                        }

                                        case ("/post"): {
                                            deleteCurrentMessage();
                                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                                postNewProduct(statement);
                                                bot.execute(new SendMessage(user.getId(), AddProduct.addedProductText()).replyMarkup(addNewOrBackButtons()));
                                            } else
                                                throw new AccessException();
                                            break;
                                        }

                                        case ("/return_product"): {
                                            deleteCurrentMessage();
                                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                                try {
                                                    ArrayList<Product> products = getAllBoughtProducts(statement);
                                                    if (!products.isEmpty()) {
                                                        System.out.println(products.size());
                                                        System.out.println(products.get(0));
                                                        System.out.println(products.get(products.size() - 1));
                                                        for (int i = 0; i < products.size() - 1; i++) {
                                                            Product product = products.get(i);
                                                            bot.execute(new SendPhoto(message.getChatId(), product.getImage_id()).caption(product.toString()).replyMarkup(archiveModsButtons(product)));
                                                        }
                                                        Product product = products.get(products.size() - 1);
                                                        bot.execute(new SendPhoto(message.getChatId(), product.getImage_id()).caption(product.toString()).replyMarkup(archiveModsAndBackButtons(product)));
                                                    } else
                                                        bot.execute(new SendMessage(message.getChatId(), ArchiveActions.emptyArchiveText()).replyMarkup(backToMainMenuButton()));
                                                } catch (SQLException e) {
                                                    initializeConnection();
                                                    e.printStackTrace();
                                                    bot.execute(new SendMessage(ADMIN, "не получилось выдать товары, ушедшие в архив - SQLException"));
                                                }
                                            } else
                                                throw new AccessException();
                                            break;
                                        }

                                        case ("/set_recommendations"): {
                                            deleteCurrentMessage();
                                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                                try {
                                                    bot.execute(new SendMessage(user.getId(), SetRecommendations.askRecommendationsText()).replyMarkup(backToMainMenuButton()));
                                                    statement.executeUpdate("update users set status = 'SET_RECOMMENDATIONS' where id = " + user.getId());
                                                } catch (SQLException e) {
                                                    initializeConnection();
                                                    e.printStackTrace();
                                                    bot.execute(new SendMessage(message.getChatId(), "не получилось выдать товары, ушедшие в архив - SQLException"));
                                                }
                                            } else
                                                throw new AccessException();
                                            break;
                                        }

                                        default: {
                                            if (user.getStatus() == NOTHING) {
                                                deleteCurrentMessage();
                                                deleteLastMessage();
                                                bot.execute(new SendMessage(message.getChatId(), new NavigationException().getDescription()).replyMarkup(backToMainMenuButton()));
                                            }
                                            isCommand[0] = false;
                                        }

                                    }

                                } catch (AccessException e) {
                                    if (message.getButtonText() != null) deleteCurrentMessage();
                                    bot.execute(new SendMessage(message.getChatId(), e.getDescription()).replyMarkup(backToMainMenuButton()));
                                    isCommand[0] = true;
                                }


                            if (!isCommand[0]) {
                                try {
                                    if (user.getStatus() instanceof UserStatuses) {
                                        try {
                                            switch ((UserStatuses) user.getStatus()) {

                                                case FIRST_NAME: {
                                                        deleteLastMessage();
                                                        deleteCurrentMessage();
                                                        user.setFirst_name(message.getText(), connection);
                                                        user.setStatus(EMAIL, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), Authorization.askEmailText()).replyMarkup(backToMainMenuButton()));
                                                    break;
                                                }

                                                case EMAIL: {
                                                        deleteLastMessage();
                                                        deleteCurrentMessage();
                                                        user.setEmail(message.getText(), connection);
                                                        user.setStatus(PHONE, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), Authorization.askPhoneText()).replyMarkup(backToMainMenuButton()));
                                                    break;
                                                }

                                                case PHONE: {
                                                        deleteLastMessage();
                                                        deleteCurrentMessage();
                                                        user.setPhone(message.getText(), connection);
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), Authorization.askCorrectDataText(user)).replyMarkup(askCorrectDataButtons()));
                                                    break;
                                                }

                                                case EDIT_FIRST_NAME: {
                                                        deleteLastMessage();
                                                        deleteCurrentMessage();
                                                        user.setFirst_name(message.getText(), connection);
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), Authorization.askCorrectDataText(user)).replyMarkup(askCorrectDataButtons()));
                                                    break;

                                                }

                                                case EDIT_EMAIL: {
                                                        deleteLastMessage();
                                                        deleteCurrentMessage();
                                                        user.setEmail(message.getText(), connection);
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), Authorization.askCorrectDataText(user)).replyMarkup(askCorrectDataButtons()));
                                                    break;
                                                }

                                                case EDIT_PHONE: {
                                                        deleteLastMessage();
                                                        deleteCurrentMessage();
                                                        user.setPhone(message.getText(), connection);
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), Authorization.askCorrectDataText(user)).replyMarkup(askCorrectDataButtons()));
                                                    break;
                                                }

                                                case COMMENT: {
                                                    deleteLastMessage();
                                                    deleteCurrentMessage();
                                                    comments.put(user.getId(), message.getText());
                                                    user.setStatus(NOTHING, connection);
                                                    bot.execute(new SendMessage(message.getChatId(), Buy.yourOrderText(user.getBasket(), comments.get(user.getId()), user)).replyMarkup(yourOrderButtons(user)));
                                                    break;
                                                }

                                                default: {

                                                }
                                            }
                                        } catch (SQLSyntaxErrorException e) {
                                            bot.execute(new SendMessage(message.getChatId(), new ProhibitedSQLSymbolException().getDescription()).replyMarkup(backToMainMenuButton()));
                                        }
                                    }
                                } catch (SQLException e) {
                                    initializeConnection();
                                    e.printStackTrace();
                                    bot.execute(new SendMessage(ADMIN, "Ошибка со статусами пользователя - SQLException"));
                                }
                            }

                        }
                    }
                    if (!isCommand[0]) {
                        if (user.getStatus() instanceof OwnerStatuses) {
                            if ((user.getId() == ADMIN) || (user.getId() == OWNER)) {
                                try {
                                    try {
                                        try {
                                            switch ((OwnerStatuses) user.getStatus()) {
                                                case ADD_PRODUCT_GENDER: {
                                                    try {
                                                        deleteCurrentMessage();
                                                        addingProducts.get(user.getId()).setGender(Genders.valueOf(Genders.valueOf(message.getText()).name()));
                                                        user.setStatus(ADD_PRODUCT_SEASON, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductSeasonText()).replyMarkup(askProductSeasonButtons()));

                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить пол - ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить пол - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case ADD_PRODUCT_SEASON: {
                                                    try {
                                                        deleteCurrentMessage();
                                                        addingProducts.get(user.getId()).setSeason(Seasons.valueOf(Seasons.valueOf(message.getText()).name()));
                                                        user.setStatus(ADD_PRODUCT_TYPE, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductTypeText()).replyMarkup(askProductTypeButtons()));
                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить сезон - ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить сезон - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case ADD_PRODUCT_TYPE: {
                                                    try {
                                                        deleteCurrentMessage();
                                                        addingProducts.get(user.getId()).setType(Types.valueOf(message.getText()));
                                                        user.setStatus(ADD_PRODUCT_TITLE, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductTitleText()).replyMarkup(backToMainMenuButton()));
                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить тип - ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить тип - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case ADD_PRODUCT_TITLE: {
                                                    try {
                                                        deleteLastMessage();
                                                        addingProducts.get(user.getId()).setTitle(message.getText());
                                                        user.setStatus(ADD_PRODUCT_IMAGE, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductImageText()).replyMarkup(backToMainMenuButton()));
                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить название - Ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить название - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case ADD_PRODUCT_IMAGE: {
                                                    try {
                                                        if (message.getImage() == null)
                                                            throw new WrongStringFormatException();
                                                        deleteLastMessage();
                                                        PhotoSize photo = (PhotoSize) message.getImage()[0];
                                                        addingProducts.get(user.getId()).setImage_id(photo.fileId());
                                                        user.setStatus(ADD_PRODUCT_DESCRIPTION, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductDescriptionText()).replyMarkup(backToMainMenuButton()));

                                                    } catch (WrongStringFormatException e) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить изображение - Ошибка при добавлении в базу данных." + e.getDescription()).replyMarkup(backToMainMenuButton()));
                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить изображение - Ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить изображение - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case ADD_PRODUCT_DESCRIPTION: {
                                                    try {
                                                        deleteLastMessage();
                                                        addingProducts.get(user.getId()).setDescription(message.getText());
                                                        user.setStatus(ADD_PRODUCT_SIZE, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductSizeText()).replyMarkup(backToMainMenuButton()));

                                                    }  catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить описание - Ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить описание - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case ADD_PRODUCT_SIZE: {
                                                    try {
                                                        deleteLastMessage();
                                                        addingProducts.get(user.getId()).setSize(message.getText());
                                                        user.setStatus(ADD_PRODUCT_PRICE, connection);
                                                        bot.execute(new SendMessage(message.getChatId(), AddProduct.askProductPriceText()).replyMarkup(backToMainMenuButton()));

                                                    } catch (IllegalArgumentException e) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить размер - Ошибка при добавлении в базу данных." + new WrongStringFormatException().getDescription()).replyMarkup(backToMainMenuButton()));
                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить размер - Ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить размер - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case ADD_PRODUCT_PRICE: {
                                                    try {
                                                        deleteLastMessage();
                                                        addingProducts.get(user.getId()).setPrice(Integer.parseInt(message.getText()));
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendPhoto(message.getChatId(), addingProducts.get(user.getId()).getImage_id()).caption(AddProduct.askProductCorrectDataText(addingProducts.get(user.getId()))).replyMarkup(askProductCorrectDataButtons()));

                                                    } catch (NumberFormatException e) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить цену - Ошибка при добавлении в базу данных." + new WrongStringFormatException().getDescription()).replyMarkup(backToMainMenuButton()));
                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить цену - Ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить цену - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case EDIT_PRODUCT_GENDER: {
                                                    try {
                                                        deleteCurrentMessage();
                                                        addingProducts.get(user.getId()).setGender(Genders.valueOf(message.getText()));
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendPhoto(message.getChatId(), addingProducts.get(user.getId()).getImage_id()).caption(AddProduct.askProductCorrectDataText(addingProducts.get(user.getId()))).replyMarkup(askProductCorrectDataButtons()));

                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить пол - ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить пол - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case EDIT_PRODUCT_SEASON: {
                                                    try {
                                                        deleteCurrentMessage();
                                                        addingProducts.get(user.getId()).setSeason(Seasons.valueOf(message.getText()));
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendPhoto(message.getChatId(), addingProducts.get(user.getId()).getImage_id()).caption(AddProduct.askProductCorrectDataText(addingProducts.get(user.getId()))).replyMarkup(askProductCorrectDataButtons()));

                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить сезон - ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить сезон - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case EDIT_PRODUCT_TYPE: {
                                                    try {
                                                        deleteCurrentMessage();
                                                        addingProducts.get(user.getId()).setType(Types.valueOf(message.getText()));
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendPhoto(message.getChatId(), addingProducts.get(user.getId()).getImage_id()).caption(AddProduct.askProductCorrectDataText(addingProducts.get(user.getId()))).replyMarkup(askProductCorrectDataButtons()));

                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить тип - ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить тип - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case EDIT_PRODUCT_TITLE: {
                                                    try {
                                                        deleteLastMessage();
                                                        addingProducts.get(user.getId()).setTitle(message.getText());
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendPhoto(message.getChatId(), addingProducts.get(user.getId()).getImage_id()).caption(AddProduct.askProductCorrectDataText(addingProducts.get(user.getId()))).replyMarkup(askProductCorrectDataButtons()));
                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить название - Ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить название - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case EDIT_PRODUCT_IMAGE: {
                                                    try {
                                                        if (message.getImage() == null)
                                                            throw new WrongStringFormatException();
                                                        deleteLastMessage();
                                                        addingProducts.get(user.getId()).setImage_id(message.getText());
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendPhoto(message.getChatId(), addingProducts.get(user.getId()).getImage_id()).caption(AddProduct.askProductCorrectDataText(addingProducts.get(user.getId()))).replyMarkup(askProductCorrectDataButtons()));

                                                    } catch (WrongStringFormatException e) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить изображение - Ошибка при добавлении в базу данных." + e.getDescription()).replyMarkup(backToMainMenuButton()));
                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить изображение - Ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить изображение - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case EDIT_PRODUCT_DESCRIPTION: {
                                                    try {
                                                        deleteLastMessage();
                                                        addingProducts.get(user.getId()).setDescription(message.getText());
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendPhoto(message.getChatId(), addingProducts.get(user.getId()).getImage_id()).caption(AddProduct.askProductCorrectDataText(addingProducts.get(user.getId()))).replyMarkup(askProductCorrectDataButtons()));

                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить описание - Ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить описание - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case EDIT_PRODUCT_SIZE: {
                                                    try {
                                                        deleteLastMessage();
                                                        addingProducts.get(user.getId()).setSize(message.getText());
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendPhoto(message.getChatId(), addingProducts.get(user.getId()).getImage_id()).caption(AddProduct.askProductCorrectDataText(addingProducts.get(user.getId()))).replyMarkup(askProductCorrectDataButtons()));

                                                    } catch (IllegalArgumentException e) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить размер - Ошибка при добавлении в базу данных." + new WrongStringFormatException().getDescription()).replyMarkup(backToMainMenuButton()));
                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить размер - Ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить размер - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case EDIT_PRODUCT_PRICE: {
                                                    deleteLastMessage();
                                                    try {
                                                        addingProducts.get(user.getId()).setPrice(Integer.parseInt(message.getText()));
                                                        user.setStatus(NOTHING, connection);
                                                        bot.execute(new SendPhoto(message.getChatId(), addingProducts.get(user.getId()).getImage_id()).caption(AddProduct.askProductCorrectDataText(addingProducts.get(user.getId()))).replyMarkup(askProductCorrectDataButtons()));

                                                    } catch (NumberFormatException e) {
                                                        bot.execute(new SendMessage(message.getChatId(), new WrongStringFormatException().getDescription()).replyMarkup(backToMainMenuButton()));
                                                    } catch (SQLException e) {
                                                        initializeConnection();
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить цену - Ошибка при добавлении в базу данных. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    } catch (Throwable t) {
                                                        bot.execute(new SendMessage(message.getChatId(), "Не получилось добавить цену - неизвестная ошибка. Обратитесь в поддержку и попробуйте добавить пока другой товар.").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                case SET_RECOMMENDATIONS: {

                                                    deleteLastMessage();
                                                    try {

                                                        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

                                                        /*
                                                        Только я знаю, что здесь был редкий и сложно-уловимый баг, который я смог пофиксить.
                                                        Только я скажу, что я молодец.
                                                        Я молодец.)
                                                         */
                                                        BufferedWriter writer = new BufferedWriter(new FileWriter(classLoader.getResource("Recommendations.txt").getFile().replaceAll("%20", " "), false));
                                                        writer.write(message.getText());
                                                        writer.close();

                                                        bot.execute(new SendMessage(user.getId(), Start.startText(user, Statistic.getNewUsersToday(connection), Statistic.getOldUsersToday(connection), Statistic.getActionsMade(connection), Statistic.getUsersInDataBase(connection))).replyMarkup(startButtons(user)));
                                                        user.setStatus(NOTHING, connection);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                        bot.execute(new SendMessage(ADMIN, "Не получилось вписать данные").replyMarkup(backToMainMenuButton()));
                                                    }
                                                    break;
                                                }

                                                default: {
                                                }

                                            }
                                        } catch (CommunicationsException e){
                                            initializeConnection();
                                        } catch (SQLSyntaxErrorException e) {
                                            bot.execute(new SendMessage(message.getChatId(), new ProhibitedSQLSymbolException().getDescription()).replyMarkup(backToMainMenuButton()));
                                        }
                                    } catch (SQLException e) {
                                        initializeConnection();
                                        e.printStackTrace();
                                        bot.execute(new SendMessage(message.getChatId(), "Ошибка с админскими статусами"));
                                    }
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                    bot.execute(new SendMessage(message.getChatId(), new WrongStringFormatException().getDescription()).replyMarkup(backToMainMenuButton()));
                                }
                            }
                        }
                    }

                } catch (NullPointerException e) {
                    deleteCurrentMessage();
                    bot.execute(new SendMessage(message.getChatId(), "Что-то пошло не так попробуйте начать все сначала").replyMarkup(backToMainMenuButton()));
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private static ArrayList<Product> getAllBoughtProducts(Statement statement) throws SQLException {
        ArrayList<Product> products = new ArrayList<>();
        ResultSet result = statement.executeQuery("select * from products where time_bought is not null");
        while (result.next()) {
            Statement statementTemporary = connection.createStatement();
            products.add(findBoughtProduct(result.getShort("id"), statementTemporary));
        }
        return products;
    }

    //удаляет сообщение если на него пришли по кнопкам
    private static void deleteCurrentMessage() {
        bot.execute(new DeleteMessage(message.getChatId(), message.getMessageId()));
    }

    private static void deleteLastMessage() {
        bot.execute(new DeleteMessage(message.getChatId(), message.getMessageId() - 1));
    }

    //проверяет наличие пользователя по айдишнику
    private static boolean isInDataBase(long id, Statement statement) {
        try {
            return statement.executeQuery("select * from users where id = " + id).next();
        } catch (SQLException e) {
            initializeConnection();
            bot.execute(new SendMessage(ADMIN, "Ошибка в методе проверки наличия пользователя в базе - SQLException"));
            e.printStackTrace();
            return false;
        }
    }

    //добавляет пользователя в бд
//    private static void addPersonToDataBase(User user, Statement statement) {
//        try {
//            int row = statement.executeUpdate("INSERT users(id, telegram_first_name, telegram_last_name, username) " +
//                    "VALUES (" + user.getId() + ", '" + user.getTelegram_first_name() + "', '" + user.getTelegram_last_name() + "', '" + user.getUsername() + "')");
//            baskets.put(user.getId(), new Basket(user.getId(), new ArrayList<>()));
//            System.out.println("мы добавили пользователя!");
//        } catch (SQLException e) {
//            bot.execute(new SendMessage(ADMIN, "Ошибка в методе добавления пользователя в базу - SQLException"));
//            e.printStackTrace();
//        }
//    }

    //ищет пользователя в бд и возвращает его
    private static User findPerson(long id, Statement statement) {
        try {
            ResultSet result = statement.executeQuery("select * from users where id = " + id);
            result.next();
            if (!baskets.containsKey(id)) baskets.put(id, new Basket(id, new ArrayList<Product>()));
            try {
                return new User(id, result.getString("telegram_first_name"), result.getString("telegram_last_name"),
                        result.getString("username"), ((id == ADMIN) || (id == OWNER)),
                        UserStatuses.valueOf(result.getString("status")), baskets.get(id),
                        result.getString("first_name"),
                        result.getString("email"), result.getString("phone"),
                        result.getTimestamp("time_adding"), result.getTimestamp("last_visit")
                );
            } catch (IllegalArgumentException e) {
                return new User(id, result.getString("telegram_first_name"), result.getString("telegram_last_name"),
                        result.getString("username"), ((id == ADMIN) || (id == OWNER)),
                        OwnerStatuses.valueOf(result.getString("status")), baskets.get(id),
                        result.getString("first_name"),
                        result.getString("email"), result.getString("phone"),
                        result.getTimestamp("time_adding"), result.getTimestamp("last_visit")
                );
            }

        } catch (SQLException e) {
            initializeConnection();
            bot.execute(new SendMessage(ADMIN, "Ошибка в методе с поиском человека в базе данных - SQLException"));
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static ArrayList<Product> findClothes(Search search, Statement statement) throws SQLException {

        Statement reserve = connection.createStatement();

        ArrayList<Product> products = new ArrayList<>();

        int difference = 0;

        if (search.getType() == HATS) difference = 1;
        if ((search.getType()) != HATS && (search.getType() != LEGGINGS)) difference = 5;

        String sql = "select * from products where gender = '" + search.getGender().name() + "' and season = '" + search.getSeason().name() + "' and type = '" + search.getType().name() + "' and size between " + (search.getSize() - difference) + " and " + (search.getSize() + difference) + " and time_bought is null";

        ResultSet result = statement.executeQuery(sql);

        while (result.next()) {
            products.add(findProduct(result.getShort("id"), reserve));
        }
        return products;
    }

    private static Product findProduct(short id, Statement statement) {
        try {
            ResultSet result = statement.executeQuery("select * from products where id = " + id + " and time_bought is null");
            result.next();
            return new Product(id, result.getString("title"),
                    result.getString("description"), result.getString("image_id"),
                    Genders.valueOf(result.getString("gender")), Seasons.valueOf(result.getString("season")),
                    Types.valueOf(result.getString("type")), result.getInt("size"),
                    result.getInt("price")
            );
        } catch (SQLException e) {
            initializeConnection();
            bot.execute(new SendMessage(ADMIN, "Ошибка в методе с поиском товара в базе данных - SQLException"));
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static Product findBoughtProduct(short id, Statement statement) {
        try {
            ResultSet result = statement.executeQuery("select * from products where id = " + id);
            result.next();
            return new Product(id, result.getString("title"),
                    result.getString("description"), result.getString("image_id"),
                    Genders.valueOf(result.getString("gender")), Seasons.valueOf(result.getString("season")),
                    Types.valueOf(result.getString("type")), result.getInt("size"),
                    result.getInt("price")
            );
        } catch (SQLException e) {
            initializeConnection();
            bot.execute(new SendMessage(ADMIN, "Ошибка в методе с поиском товара в базе данных - SQLException"));
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static void postNewProduct(Statement statement) {
        try {
            ProductShablon product = addingProducts.get(user.getId());
            String[] sizes = product.getSize().replaceAll("\s+", "").split(",");
            for (int i = 0; i < sizes.length; i++) {
                int row = statement.executeUpdate("INSERT into products(gender, season, type, title, image_id, description, size, price) " +
                        "VALUES ('" + product.getGender().name() + "', '" + product.getSeason().name() + "', '" + product.getType().name() + "', '" + product.getTitle() + "', '" + product.getImage_id() + "', '" +
                        product.getDescription() + "', " + sizes[i] + ", " + product.getPrice() + ")");
                baskets.put(user.getId(), new Basket(user.getId(), new ArrayList<>()));
                System.out.println("мы добавили товар!");
            }
            addingProducts.replace(user.getId(), new ProductShablon());
        } catch (SQLException e) {
            initializeConnection();
            bot.execute(new SendMessage(ADMIN, "Ошибка в методе добавления товара в базу - SQLException"));
            e.printStackTrace();
        }
    }

    private static void initializeConnection(){
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            statement = connection.createStatement();
            System.out.println("Переподключение прошло успешно");
        } catch (SQLException e) {
            initializeConnection();
            e.printStackTrace();
            bot.execute(new SendMessage(message.getChatId(), "Что-то пошло не так попробуйте начать все сначала").replyMarkup(backToMainMenuButton()));
        }
    }
}