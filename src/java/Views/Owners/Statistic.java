package Views.Owners;

import java.sql.*;

public class Statistic {

    public static Date getTheLastDay(Connection connection) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("select refreshed_time from statistic order by refreshed_time desc");
        ResultSet result = statement.executeQuery();
        if (result.next()) return result.getDate("refreshed_time");
        return new Date(0);
    }

    public static void startNewDay(Connection connection) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("insert into statistic(refreshed_time) value (?)");
        statement.setDate(1, new Date(System.currentTimeMillis()));
        statement.executeUpdate();
    }

    public static void increaseNewUsersToday(Connection connection) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("update statistic set new_users_today = new_users_today + 1 where refreshed_time = ?");
        statement.setDate(1, new Date(System.currentTimeMillis()));
        statement.executeUpdate();
    }

    public static void increaseOldUsersToday(Connection connection) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("update statistic set old_users_today = old_users_today + 1 where refreshed_time = ?");
        statement.setDate(1, new Date(System.currentTimeMillis()));
        statement.executeUpdate();
    }

    public static void increaseActionsMade(Connection connection) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("update statistic set actions_made = actions_made + 1 where refreshed_time = ?");
        statement.setDate(1, new Date(System.currentTimeMillis()));
        statement.executeUpdate();
    }

    public static int getUsersInDataBase(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select * from users", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet result = statement.executeQuery();
        result.last();
        return result.getRow();
    }

    public static int getNewUsersToday(Connection connection) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("select new_users_today from statistic where refreshed_time = ?");
        statement.setDate(1, new Date(System.currentTimeMillis()));
        ResultSet result = statement.executeQuery();
        if (result.next()) return result.getInt("new_users_today");
        return 0;
    }

    public static int getOldUsersToday(Connection connection) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("select old_users_today from statistic where refreshed_time = ?");
        statement.setDate(1, new Date(System.currentTimeMillis()));
        ResultSet result = statement.executeQuery();
        if (result.next()) return result.getInt("old_users_today");
        return 0;
    }

    public static int getActionsMade(Connection connection) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("select actions_made from statistic where refreshed_time = ?");
        statement.setDate(1, new Date(System.currentTimeMillis()));
        ResultSet result = statement.executeQuery();
        if (result.next()) return result.getInt("actions_made");
        return 0;
    }
}
