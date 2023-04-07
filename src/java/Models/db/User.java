package Models.db;

import MessageHandlers.Statuses.Statuses;
import Models.Basket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Addable {
    private long id;
    private String telegram_first_name;
    private String telegram_last_name;
    private String username;
    private boolean isAdmin;

    private Statuses status;

    private Basket basket; // в бд не идет

    private String first_name;
    private String email;
    private String phone;

    private Timestamp time_adding;
    private Timestamp last_visit;

    public User(long id, String telegram_first_name, String telegram_last_name, String username, Basket basket) {
        this.id = id;
        this.telegram_first_name = telegram_first_name;
        this.telegram_last_name = telegram_last_name;
        this.username = username;
        this.basket = basket;
    }

    public static User get() {
        return new User();
    }

    @Override
    public void addToDatabase(Connection connection, Timestamp time) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("insert into users (id, telegram_first_name, telegram_last_name, username, time_adding) values (?, ?, ?, ?, ?)");
        statement.setLong(1, this.getId());
        statement.setString(2, this.getTelegram_first_name());
        statement.setString(3, this.getTelegram_last_name());
        statement.setString(4, this.getUsername());
        statement.setTimestamp(5, time);
        statement.executeUpdate();
    }

    public void setStatus(Statuses status, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("update users set status = ? where id = ?");
        statement.setString(1, status.toString());
        statement.setLong(2, this.getId());
        statement.executeUpdate();
        this.status = status;
    }

    public void setFirst_name(String first_name, Connection connection) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("update users set first_name = ? where id = ?");
        statement.setString(1, first_name);
        statement.setLong(2, this.getId());
        statement.executeUpdate();
        this.first_name = first_name;
    }

    public void setEmail(String email, Connection connection) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("update users set email = ? where id = ?");
        statement.setString(1, email);
        statement.setLong(2, this.getId());
        statement.executeUpdate();
        this.email = email;
    }

    public void setPhone(String phone, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("update users set phone = ? where id = ?");
        statement.setString(1, phone);
        statement.setLong(2, this.getId());
        statement.executeUpdate();
        this.phone = phone;
    }

        public void setLast_visit(Timestamp time, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("update users set last_visit = ? where id = ?");
        statement.setTimestamp(1, time);
        statement.setLong(2, this.getId());
        statement.executeUpdate();
        this.last_visit = time;
    }

    @Override
    public String toString() {
        return "тг имя - " + this.getTelegram_first_name() + "\n" +
                "тг имя - " + this.getTelegram_last_name() + "\n" +
                "тг имя - " + this.getUsername();
    }
}
