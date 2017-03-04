/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DronHackContext;

import dao.*;

/**
 *
 * @author Jirka
 */
public class Context {

    public static BednaDao bednaDao;
    public static MaterialDao materialDao;
    public static PoziceDao poziceDao;

    
    
    public Context(String serverUrl, int port, String databaseName, String user, String password) {
        bednaDao = new BednaDao(serverUrl, port, databaseName, user, password);
        materialDao = new MaterialDao(serverUrl, port, databaseName, user, password);
        poziceDao = new PoziceDao(serverUrl, port, databaseName, user, password);
    }

    public Context(String dronhackdatabasewindowsnet, int i, String sqladmindronhack, String abcDEF1234) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
