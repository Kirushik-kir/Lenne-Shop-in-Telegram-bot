package Models.Product.Categories;

public enum Types {

    HATS("Шапочки"), COMPLETES("Комплекты"), OVERALLS("Комбинезоны"), LEGGINGS("Краги"), JACKETS("Куртки"), PANTS("Штаны");

    private String title;

    Types(String title) {
        this.title = title;
    }

    @Override
    public String toString(){
        return this.title;
    }
}
