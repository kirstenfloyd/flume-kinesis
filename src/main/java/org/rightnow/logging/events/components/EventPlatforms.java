package org.rightnow.logging.events.components;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirsten on 8/15/17.
 */
public class EventPlatforms {

    public static final String Media = "media";
    public static final String AtWork = "atwork";
    public static final String API = "api";
    public static final String Curator = "curator";
    public static final String Orator = "orator";
    public static final String Receptor = "receptor";
    public static final String CRM = "crm";
    public static final String AccountService = "accountservice";
    public static final String CommunicationService = "communicationservice";
    public static final String ContentService = "contentservice";
    public static final String MQService = "mqservice";
    public static final String SalesService = "salesservice";
    public static final String SearchService = "searchservice";
    public static final String SubscriptionService = "subscriptionservice";
    public static final String Postman = "postman";
    public static final String MonitorSampleApp = "monitorsampleapp";
    public static final String ServiceBus = "servicebus";
    public static final String Scheduler = "scheduler";
    public static final String Arbiter = "arbiter";
    public static final String ContactService = "contactservice";
    public static final String EnvironmentalService = "environmentalservice";
    public static final String ADManagementService = "admanagement";
    public static final String DialerService = "dialerservice";
    public static final String IdentityServer = "identityserver";
    public static final String Projector = "projector";
    public static final String WAWRetreat = "wawretreat";
    public static final String Store = "store";
    public static final String AdminTools = "admintools";
    public static final String ArbiterPublic = "arbiter_public";
    public static final String ArbiterPrivate = "arbiter_private";
    public static final String MessageConsumer = "messageconsumer";


    public static List<String> GetPlatformNames(){
        List<String> eventPlatformList = new ArrayList<>();

        eventPlatformList.add(Media);
        eventPlatformList.add(AtWork);
        eventPlatformList.add(API);
        eventPlatformList.add(Curator);
        eventPlatformList.add(Orator);
        eventPlatformList.add(Receptor);
        eventPlatformList.add(AccountService);
        eventPlatformList.add(CommunicationService);
        eventPlatformList.add(ContentService);
        eventPlatformList.add(MQService);
        eventPlatformList.add(SalesService);
        eventPlatformList.add(SearchService);
        eventPlatformList.add(SubscriptionService);
        eventPlatformList.add(Postman);
        eventPlatformList.add(MonitorSampleApp);
        eventPlatformList.add(CRM);
        eventPlatformList.add(ServiceBus);
        eventPlatformList.add(Scheduler);
        eventPlatformList.add(Arbiter);
        eventPlatformList.add(ContactService);
        eventPlatformList.add(EnvironmentalService);
        eventPlatformList.add(ADManagementService);
        eventPlatformList.add(DialerService);
        eventPlatformList.add(IdentityServer);
        eventPlatformList.add(Projector);
        eventPlatformList.add(WAWRetreat);
        eventPlatformList.add(Store);
        eventPlatformList.add(AdminTools);
        eventPlatformList.add(ArbiterPublic);
        eventPlatformList.add(ArbiterPrivate);
        eventPlatformList.add(MessageConsumer);

        return eventPlatformList;
    }

    public static boolean IsValidPlatformName(String platformName){
        boolean isValid = false;

        if(platformName != null && !platformName.isEmpty()){
            List<String> eventPlatformList = GetPlatformNames();
            isValid = eventPlatformList.contains(platformName);
        }

        return isValid;
    }
}
