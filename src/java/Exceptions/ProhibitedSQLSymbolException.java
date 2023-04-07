package Exceptions;

public class ProhibitedSQLSymbolException extends Exception implements Describable{

    @Override
    public String getDescription() {
        return "Введите сообщение без символа '";
    }

}
