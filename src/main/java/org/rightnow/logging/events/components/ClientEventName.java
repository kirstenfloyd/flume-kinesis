package org.rightnow.logging.events.components;

/**
 * Created by kirsten on 8/15/17.
 */
public class ClientEventName {


    private String ClientName;
    private String PlatformName;
    private String PageName;
    private String SectionName;
    private String ComponentName;
    private String ElementName;
    private String ActionName;

    public ClientEventName(String eventName){
        String[] eventNameParts = eventName.split(":", -1);
        this.ClientName = eventNameParts[0];
        this.PlatformName = eventNameParts[1];
        this.PageName = eventNameParts[2];
        this.SectionName = eventNameParts[3];
        this.ComponentName = eventNameParts[4];
        this.ElementName = eventNameParts[5];
        this.ActionName = eventNameParts[6];
    }

    public String getClientName() {
        return ClientName;
    }

    public String getPlatformName() {
        return PlatformName;
    }

    public String getPageName() {
        return PageName;
    }

    public String getSectionName() {
        return SectionName;
    }

    public String getComponentName() {
        return ComponentName;
    }

    public String getElementName() {
        return ElementName;
    }

    public String getActionName() {
        return ActionName;
    }

}
