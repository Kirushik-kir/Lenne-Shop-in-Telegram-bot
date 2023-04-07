package Models;

import Models.db.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private User user;
    private String comment;
    private Timestamp OrderTime;
}
