package Exceptions;

public class AccessException extends Exception implements Describable{

    @Override
    public String getDescription(){
        return "Здесь вас не должно быть!";
    }

}
