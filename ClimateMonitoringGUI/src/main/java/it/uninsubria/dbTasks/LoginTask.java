package it.uninsubria.dbTasks;

import it.uninsubria.operatore.Operatore;
import it.uninsubria.queryhandler.QueryHandler;
import javafx.concurrent.Task;

public class LoginTask extends Task<Boolean> {

    private final QueryHandler queryHandler;
    private final String userID;
    private final String password;
    public LoginTask(QueryHandler queryHandler, String userID, String password){
        this.queryHandler = queryHandler;
        this.userID = userID;
        this.password = password;
    }
    @Override
    protected Boolean call() throws Exception {
        Operatore o = queryHandler.executeLogin(userID, password);
        if(o != null){
            System.out.println("Login task result: " + o);
            updateValue(true);
        }else{
            updateValue(false);
        }
        System.out.println("Login task Value returned: "+getValue());
        return getValue();
    }
}
