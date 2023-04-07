package Models.db.Product;

import Models.Product.Categories.Seasons;
import Models.Product.Categories.Genders;
import Models.Product.Categories.Types;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductShablon {
    private short id;
    private String title;
    private String description;
    private String image_id;
    private Genders gender;
    private Seasons season;
    private Types type;
    private String size;
    private int price;

    @Override
    public String toString(){
        return "Пол: " + this.gender.getNormalString() + "\n" + "Сезон: " + this.season + "\n" + "Вид одежды: " + this.type + "\n\n" + this.title + "\n\n" + this.description + "\n\n" + "Размеры: " + this.size + "\n\n" + "Цена: " + this.price + " руб." + "\n\n";
    }
}