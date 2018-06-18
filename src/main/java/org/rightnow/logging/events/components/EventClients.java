package org.rightnow.logging.events.components;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirsten on 8/15/17.
 */
public class EventClients {

    public static final String iOS = "ios";
    public static final String Android = "android";
    public static final String Roku = "roku";
    public static final String tvOS = "tvos";
    public static final String AndroidTV = "androidtv";
    public static final String FireTV = "firetv";

    public static final String IosMedia = "ios-media";
    public static final String iosAtWork = "ios-atwork";
    public static final String AndroidMedia = "android-media";
    public static final String AndroidAtWork = "android-atwork";
    public static final String tvOsMedia = "tvos-media";
    public static final String tvOsAtWork = "tvos-atwork";
    public static final String FireTvMedia = "firetv-media";
    public static final String FireTvAtWork = "firetv-atwork";
    public static final String AndroidTvMedia = "androidtv-media";
    public static final String AndroidTvAtWork = "androidtv-atwork";
    public static final String RokuMedia = "roku-media";
    public static final String RokuAtWork = "roku-atwork";
    public static final String Chromecast = "chromecast";

    public static final String Desktop = "desktop";
    public static final String Mobile = "mobile";
    public static final String Other = "other";
    public static final String Bot = "bot";

    public static final String External = "external";
    public static final String Executable = "executable";

    public static List<String> GetClientNames(){
        List<String> clientNameList = new ArrayList<>();

        clientNameList.add(iOS);
        clientNameList.add(Android);
        clientNameList.add(Roku);
        clientNameList.add(tvOS);
        clientNameList.add(AndroidTV);
        clientNameList.add(FireTV);
        clientNameList.add(IosMedia);
        clientNameList.add(iosAtWork);
        clientNameList.add(AndroidMedia);
        clientNameList.add(AndroidAtWork);
        clientNameList.add(tvOsMedia);
        clientNameList.add(tvOsAtWork);
        clientNameList.add(FireTvMedia);
        clientNameList.add(FireTvAtWork);
        clientNameList.add(AndroidTvMedia);
        clientNameList.add(AndroidTvAtWork);
        clientNameList.add(RokuMedia);
        clientNameList.add(RokuAtWork);
        clientNameList.add(Desktop);
        clientNameList.add(Mobile);
        clientNameList.add(Other);
        clientNameList.add(External);
        clientNameList.add(Executable);
        clientNameList.add(Bot);
        clientNameList.add(Chromecast);

        return clientNameList;
    }

    public static boolean IsValidClientName(String clientName){
        boolean isValid = false;

        if(clientName != null && !clientName.isEmpty()){
            List<String> clientNameList = GetClientNames();
            isValid = clientNameList.contains(clientName);
        }

        return isValid;
    }

}
