package it.uninsubria.operatore;

import java.util.Objects;

public class Operatore {

    private String nome;
    private String cognome;
    private String codFiscale;
    private String email;

    private String userID;
    private String password;
    private String centroID;


    public static final String generalSep = ";";
    public static final String terminatingChar = ",";

    public Operatore(String nome, String cognome, String codFiscale, String email, String userID,
                     String password, String centroID){
        this.nome = nome;
        this.cognome = cognome;
        this.codFiscale = codFiscale;
        this.email = email;
        this.userID = userID;
        this.password = password;
        this.centroID = centroID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodFiscale() {
        return codFiscale;
    }

    public void setCodFiscale(String codFiscale) {
        this.codFiscale = codFiscale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID(){return this.userID;}
    public void setUserID(String userID){this.userID = userID;}
    public String getPassword(){return this.password;}
    public void setPassword(String password){this.password = password;}
    public String getCentroID(){return this.centroID;}
    public void setCentroID(String centroID){this.centroID = centroID;}

    @Override
    public int hashCode() {
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        return "";
    }

}
