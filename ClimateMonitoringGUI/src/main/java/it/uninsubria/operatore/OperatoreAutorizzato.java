package it.uninsubria.operatore;

public class OperatoreAutorizzato {

    private String codFiscale;
    private String email;
    public OperatoreAutorizzato(String codFiscale, String email){
        this.codFiscale = codFiscale;
        this.email = email;
    }

    public String getCodFiscale(){
        return this.codFiscale;
    }

    public String getEmail(){
        return this.email;
    }

    public String toString(){
        return this.codFiscale + ":" + this.email;
    }

}
