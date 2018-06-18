package org.rightnow.logging.events.components;

/**
 * Created by kirsten on 8/15/17.
 */
public class SystemEventName {

    private String CallingApp;
    private String PlatformName;
    private String Controller;
    private String Action;
    private String Version;
    private String ResponseStatusCode;

    public SystemEventName(String eventName){
        String[] eventNameParts = eventName.split(":", -1);
        this.CallingApp = eventNameParts[0];
        this.PlatformName = eventNameParts[1];
        this.Controller = eventNameParts[2];
        this.Action = eventNameParts[3];
        this.Version = eventNameParts[4];
        this.ResponseStatusCode = eventNameParts[5];
    }

    public String getCallingApp() {
        return CallingApp;
    }

    public String getPlatformName() {
        return PlatformName;
    }

    public String getController() {
        return Controller;
    }

    public String getAction() {
        return Action;
    }

    public String getVersion() {
        return Version;
    }

    public String getResponseStatusCode() {
        return ResponseStatusCode;
    }


}
