package org.rightnow.logging.events.components;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirsten on 8/15/17.
 */
public class EventInitiator {

    public static final String ClientApp = "client_app";
    public static final String ClientUser = "client_user";
    public static final String ServerApp = "server_app";
    public static final String ServerUser = "server_user";

    public static List<String> GetEventInitiators(){
        List<String> eventInitiatorList = new ArrayList<>();

        eventInitiatorList.add(ClientApp);
        eventInitiatorList.add(ClientUser);
        eventInitiatorList.add(ServerApp);
        eventInitiatorList.add(ServerUser);

        return eventInitiatorList;
    }

    public static boolean IsValidEventInitiator(String eventInitiator){
        boolean isValid = false;

        if(eventInitiator != null && !eventInitiator.isEmpty()){
            List<String> eventInitiatorList = GetEventInitiators();
            isValid = eventInitiatorList.contains(eventInitiator);
        }

        return isValid;
    }
}
