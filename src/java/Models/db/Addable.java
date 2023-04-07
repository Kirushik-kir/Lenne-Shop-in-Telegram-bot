package Models.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface Addable{

    void addToDatabase(Connection connection, Timestamp time) throws SQLException;
}
