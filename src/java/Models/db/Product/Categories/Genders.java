package Models.Product.Categories;

public enum Genders {
    BOY("мальчика"), GIRL("девочку");

    private String title;

    Genders(String title) {
        this.title = title;
    }

    @Override
    public String toString(){
        return this.title;
    }

    public String getNormalString(){
        return this == BOY ? "Мальчик" : "Девочка";
    }

}
