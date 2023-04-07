package Exceptions;

import Models.db.User;

public class AddPersonToDatabaseException implements Describable{

    public String getDescription() {
        return "Не удалось добавить пользователя в базу";
    }

    public String getDescription(User user) {
        return "Не удалось добавить пользователя в базу: " + "\n" + user;
    }

}
