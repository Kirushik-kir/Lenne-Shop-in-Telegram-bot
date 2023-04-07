package Models;

import Models.db.Product.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class Basket {
    private long id;
    private ArrayList<Product> productList;
    private int sum = 0;

    public Basket(long id, ArrayList<Product> productList) {
        this.id = id;
        this.productList = productList;
    }

    public int getSum() {
        if (productList == null) return 0;
        this.sum = 0;
        for (Product product : this.productList){
            this.sum += product.getPrice();
        }
        return this.sum;
    }
}
