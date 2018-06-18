package org.rightnow.logging.events.validators;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rightnow.logging.events.components.*;
import org.rightnow.logging.models.ErrorModel;
import org.rightnow.logging.models.GenericEventModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirsten on 8/15/17.
 */
public class GenericEventValidator {

    private static final Logger LOGGER = LogManager.getLogger(GenericEventValidator.class);

    private static final String DefaultGuid = "00000000-0000-0000-0000-000000000000";

    private static final String EMPTY_ERROR = "Empty";
    private static final String INVALID_ERROR = "Invalid";

    public static boolean HasError(GenericEventModel eventModel){

        List<ErrorModel> eventErrorModels = GetErrorsForEvent(eventModel);

        return !eventErrorModels.isEmpty();
    }

    public static List<ErrorModel> GetErrorsForEvent(GenericEventModel eventModel){

        List<ErrorModel> errorModelList = new ArrayList<>();

        String platform = "";

        List<ErrorModel> eventInitiatorErrors = GetEventInitiatorErrors(eventModel);
        errorModelList.addAll(eventInitiatorErrors);

        if(eventInitiatorErrors.isEmpty()){

            errorModelList.addAll(GetEventNameErrors(eventModel));
        }

        errorModelList.addAll(GetSessionIdErrors(eventModel));

        errorModelList.addAll(GetEventIdErrors(eventModel));
        errorModelList.addAll(GetCorrelatedEventIdErrors(eventModel));
        errorModelList.addAll(GetIPAddressErrors(eventModel));
        errorModelList.addAll(GetAWSIdErrors(eventModel));
        errorModelList.addAll(GetTimestampErrors(eventModel.getTimestamp()));
        errorModelList.addAll(GetEventDetailsErrors(eventModel.getEventDetails()));

        return errorModelList;
    }

    private static List<ErrorModel> GetEventInitiatorErrors(GenericEventModel eventModel){
        List<ErrorModel> eventInitiatorErrorModels = new ArrayList<>();

        String eventInitiatorField = "event_initiator";

        String eventInitiator = eventModel.getEventInitiator();

        if(!EventInitiator.IsValidEventInitiator(eventInitiator)) {
            eventInitiatorErrorModels.add(new ErrorModel(eventInitiatorField, INVALID_ERROR));
        }

        return eventInitiatorErrorModels;
    }

    private static String GetPlatformName(GenericEventModel eventModel){
        String platform = "";

        try{
            if(IsClientEvent(eventModel.getEventInitiator())){
                ClientEventName clientEventName = new ClientEventName(eventModel.getEventName());

                platform = clientEventName.getPlatformName();
            }
            else if(IsSystemEvent(eventModel.getEventInitiator())){

                SystemEventName systemEventName = new SystemEventName(eventModel.getEventName());

                platform = systemEventName.getPlatformName();
            }
        }
        catch(Exception ex){
            platform = "";
        }

        return platform;

    }

    private static List<ErrorModel> GetEventNameErrors(GenericEventModel eventModel){
        List<ErrorModel> eventNameErrorModels = new ArrayList<>();

        String eventNameField = "event_name";

        if(eventModel.getEventName() == null || eventModel.getEventName().isEmpty()){
            eventNameErrorModels.add(new ErrorModel(eventNameField, EMPTY_ERROR));
        }

        if(IsClientEvent(eventModel.getEventInitiator())){

            int separators = StringUtils.countMatches(eventModel.getEventName(), ":");

            if(separators != 6){
                eventNameErrorModels.add(new ErrorModel(eventNameField, "Invalid number of separators"));
            }
            else{
                ClientEventName clientEventName = new ClientEventName(eventModel.getEventName());

                if(!EventClients.IsValidClientName(clientEventName.getClientName())){
                    eventNameErrorModels.add(new ErrorModel(eventNameField, "Invalid client name"));
                }
                if(!EventPlatforms.IsValidPlatformName(clientEventName.getPlatformName())){
                    eventNameErrorModels.add(new ErrorModel(eventNameField, "Invalid platform name"));
                }
                if(!EventAction.IsValidAction(clientEventName.getActionName())){
                    eventNameErrorModels.add(new ErrorModel(eventNameField, "Invalid action name"));
                }
            }
        }
        else if(IsSystemEvent(eventModel.getEventInitiator())){

            int separators = StringUtils.countMatches(eventModel.getEventName(), ":");

            if(separators != 5){
                eventNameErrorModels.add(new ErrorModel(eventNameField, "Invalid number of separators"));
            }
            else{
                SystemEventName systemEventName = new SystemEventName(eventModel.getEventName());

                if(eventModel.getEventInitiator().equals(EventInitiator.ServerUser)){
                    if(!EventClients.IsValidClientName(systemEventName.getCallingApp())){
                        eventNameErrorModels.add(new ErrorModel(eventNameField, "Invalid calling app name"));
                    }
                }
                else if (eventModel.getEventInitiator().equals(EventInitiator.ServerApp)){
                    if(!(EventPlatforms.IsValidPlatformName(systemEventName.getCallingApp()) || EventClients.IsValidClientName(systemEventName.getCallingApp()))
                            && !systemEventName.getCallingApp().equals(EventClients.External)
                            && !systemEventName.getCallingApp().equals(EventClients.Executable)){
                        eventNameErrorModels.add(new ErrorModel(eventNameField, "Invalid calling app name"));
                    }
                }
                if(!EventPlatforms.IsValidPlatformName(systemEventName.getPlatformName())){
                    eventNameErrorModels.add(new ErrorModel(eventNameField, "Invalid platform name"));
                }
            }
        }

        return eventNameErrorModels;
    }

    private static List<ErrorModel> GetSessionIdErrors(GenericEventModel eventModel){
        List<ErrorModel> sessionIdErrorModels = new ArrayList<>();

        String sessionIdField = "session_id";

        boolean sessionIdIsNotRequired = IsSessionIdNotRequired(eventModel);

        if(!sessionIdIsNotRequired){
            String sessionId = eventModel.getSessionId();
            if(sessionId == null || sessionId.isEmpty()){
                sessionIdErrorModels.add(new ErrorModel(sessionIdField, EMPTY_ERROR));
            }
            else if(sessionId.equals(DefaultGuid)){
                sessionIdErrorModels.add(new ErrorModel(sessionIdField, INVALID_ERROR));
            }
        }
        return sessionIdErrorModels;
    }

    private static boolean IsSessionIdNotRequired(GenericEventModel eventModel){
        boolean sessionIdIsNotRequired = false;

        try{
            if(IsSystemEvent(eventModel.getEventInitiator())){
                SystemEventName systemEventName = new SystemEventName(eventModel.getEventName());

                if(eventModel.getEventInitiator().equals(EventInitiator.ServerApp)){
                    if(systemEventName.getCallingApp().equals(EventPlatforms.Receptor) ||
                            systemEventName.getPlatformName().equals(EventPlatforms.Receptor) ||
                            systemEventName.getCallingApp().equals(EventPlatforms.IdentityServer)){
                        sessionIdIsNotRequired = true;
                    }
                }

            }

        }
        catch(Exception ex)
        {

        }

        return sessionIdIsNotRequired;
    }

    private static List<ErrorModel> GetEventIdErrors(GenericEventModel eventModel){
        List<ErrorModel> eventIdErrorModels = new ArrayList<>();

        String eventIdField = "event_id";

        String eventId = eventModel.getEventId();

        if(eventId == null || eventId.isEmpty()){
            eventIdErrorModels.add(new ErrorModel(eventIdField, EMPTY_ERROR));
        }
        else if(eventId.equals(DefaultGuid)){
            eventIdErrorModels.add(new ErrorModel(eventIdField, INVALID_ERROR));
        }

        return eventIdErrorModels;
    }

    private static List<ErrorModel> GetCorrelatedEventIdErrors(GenericEventModel eventModel){
        List<ErrorModel> correlatedEventIdErrorModels = new ArrayList<>();

        String correlatedEventId = eventModel.getCorrelatedEventId();

        if(correlatedEventId == null){
            correlatedEventIdErrorModels.add(new ErrorModel("correlated_event_id", EMPTY_ERROR));
        }

        return correlatedEventIdErrorModels;
    }

    private static List<ErrorModel> GetIPAddressErrors(GenericEventModel eventModel){
        List<ErrorModel> ipAddressErrorModels = new ArrayList<>();

        String ipAddress = eventModel.getIpAddress();

        if(ipAddress == null || ipAddress.isEmpty()){
            ipAddressErrorModels.add(new ErrorModel("ip_address", EMPTY_ERROR));
        }

        return ipAddressErrorModels;
    }

    private static List<ErrorModel> GetAWSIdErrors(GenericEventModel eventModel){
        List<ErrorModel> awsIdErrorModels = new ArrayList<>();

        String awsId = eventModel.getAwsId();

        if(awsId == null || awsId.isEmpty()){
            awsIdErrorModels.add(new ErrorModel("aws_id", EMPTY_ERROR));
        }

        return awsIdErrorModels;
    }

    private static List<ErrorModel> GetTimestampErrors(long timestamp){
        List<ErrorModel> timestampErrorModels = new ArrayList<>();

        if(timestamp == 0){
            timestampErrorModels.add(new ErrorModel("timestamp", INVALID_ERROR));
        }

        return timestampErrorModels;
    }

    private static List<ErrorModel> GetEventDetailsErrors(String eventDetails){
        List<ErrorModel> eventDetailsErrorModels = new ArrayList<>();

        if(eventDetails == null){
            eventDetailsErrorModels.add(new ErrorModel("event_details", EMPTY_ERROR));
        }

        return eventDetailsErrorModels;
    }

    private static boolean IsClientEvent(String eventInitiator){
        return eventInitiator.equals(EventInitiator.ClientApp) || eventInitiator.equals(EventInitiator.ClientUser);
    }

    private static boolean IsSystemEvent(String eventInitiator){
        return eventInitiator.equals(EventInitiator.ServerApp) || eventInitiator.equals(EventInitiator.ServerUser);
    }


}
