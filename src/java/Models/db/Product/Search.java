package Models.db.Product;

import Models.Product.Categories.Genders;
import Models.Product.Categories.Seasons;
import Models.Product.Categories.Types;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Search {
    private Genders gender;
    private Seasons season;
    private Types type;
    private int size;
}