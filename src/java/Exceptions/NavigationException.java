package Exceptions;

public class NavigationException extends RuntimeException implements Describable{

    @Override
    public String getDescription() {
        return "Пожалуйста, пользуйтесь кнопками для навигации по магазину!";
    }

}
