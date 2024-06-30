package it.climatemonitoring.request;

public class MalformedRequestException extends Exception{

    private final String message;
    public MalformedRequestException(String message) {
        this.message = message;
    }
    public String getMessage(){ return this.message;}

}
