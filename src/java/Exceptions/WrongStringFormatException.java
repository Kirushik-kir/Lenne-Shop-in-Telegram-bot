package Exceptions;

public class WrongStringFormatException extends Exception implements Describable{

    @Override
    public String getDescription() {
        return "Введите данные без лишних символов, пожалуйста!";
    }

}
