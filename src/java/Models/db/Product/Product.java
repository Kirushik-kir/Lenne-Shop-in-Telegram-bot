package Models.db.Product;

import Models.Product.Categories.Genders;
import Models.Product.Categories.Seasons;
import Models.Product.Categories.Types;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private short id;
    private String title;
    private String description;
    private String image_id;
    private Genders gender;
    private Seasons season;
    private Types type;
    private int size;
    private int price;

    public static ArrayList<Integer> getSizes(Search search, Connection connection) throws SQLException {
        ArrayList<Integer> sizes = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement("select size from products where gender = ? and season = ? and type = ? and time_bought is null");
        statement.setString(1, search.getGender().name());
        statement.setString(2, search.getSeason().name());
        statement.setString(3, search.getType().name());

        ResultSet result = statement.executeQuery();

        while (result.next()) {
            int size = result.getInt("size");
            System.out.println(size);
            if (!sizes.contains(size)) sizes.add(size);
        }
        Collections.sort(sizes);
        Collections.reverse(sizes);
        return sizes;
    }

    @Override
    public String toString(){
        return this.title + "\n\n" + this.description + "\n\n" + "Размер: " + this.size + "\n\n" + "Цена: " + this.price + " руб." + "\n\n";
    }

    public String showInOrder(){
        String colour = "Цвет: не указано";
        String[] paragraphs = this.description.split("\n");
        for (String paragraph : paragraphs){
            if (paragraph.startsWith("Цвет")) {
                colour = paragraph;
                break;
            }
        }
        return this.title + "\n\n" + colour + "\n" + "Размер: " + this.size + "\n" + "Цена: " + this.price + " руб.";
    }
}
