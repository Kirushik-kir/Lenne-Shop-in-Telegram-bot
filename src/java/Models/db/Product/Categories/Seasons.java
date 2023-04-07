package Models.Product.Categories;

public enum Seasons {
    WINTER("Зима"), AUTUMN_SPRING("Осень-весна");

    private String title;

    Seasons(String title) {
        this.title = title;
    }

    @Override
    public String toString(){
        return this.title;
    }
}
