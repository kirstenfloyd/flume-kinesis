package org.rightnow.logging.events.components;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirsten on 8/15/17.
 */
public class EventAction {

    public static final String Click = "click";
    public static final String Tap = "tap";
    public static final String LongPress = "long-press";
    public static final String Swipe = "swipe";
    public static final String Drop = "drop";
    public static final String Ended = "ended";

    public static final String AutoPlay = "autoplay";
    public static final String Playing = "playing";
    public static final String Drag = "drag";
    public static final String Blur = "blur";
    public static final String Focus = "focus";
    public static final String Scroll = "scroll";
    public static final String DoubleClick = "double-click";
    public static final String MouseEnter = "mouse-enter";
    public static final String MouseLeave = "mouse-leave";
    public static final String Change = "change";
    public static final String Select = "select";
    public static final String Submit = "submit";
    public static final String KeyDown = "key-down";
    public static final String KeyUp = "key-up";
    public static final String Error = "error";
    public static final String Hover = "hover";
    public static final String Seek = "seek";
    public static final String Seeked = "seeked";
    public static final String Prompt = "prompt";
    public static final String End = "end";
    public static final String Unload = "unload";
    public static final String Expand = "expand";
    public static final String Autoselect = "autoselect";
    public static final String Collapse = "collapse";
    public static final String Searching = "searching";
    public static final String AutoPause = "autopause";

    public static List<String> GetEventActions(){
        List<String> eventActionList = new ArrayList<>();

        eventActionList.add(Click);
        eventActionList.add(Tap);
        eventActionList.add(LongPress);
        eventActionList.add(Swipe);
        eventActionList.add(Drop);
        eventActionList.add(Ended);
        eventActionList.add(AutoPlay);
        eventActionList.add(Playing);
        eventActionList.add(Drag);
        eventActionList.add(Blur);
        eventActionList.add(Focus);
        eventActionList.add(Scroll);
        eventActionList.add(DoubleClick);
        eventActionList.add(MouseEnter);
        eventActionList.add(MouseLeave);
        eventActionList.add(Change);
        eventActionList.add(Select);
        eventActionList.add(Submit);
        eventActionList.add(KeyDown);
        eventActionList.add(KeyUp);
        eventActionList.add(Error);
        eventActionList.add(Hover);
        eventActionList.add(Seek);
        eventActionList.add(Seeked);
        eventActionList.add(Prompt);
        eventActionList.add(End);
        eventActionList.add(Unload);
        eventActionList.add(Expand);
        eventActionList.add(Autoselect);
        eventActionList.add(Collapse);
        eventActionList.add(Searching);
        eventActionList.add(AutoPause);

        return eventActionList;
    }

    public static boolean IsValidAction(String action){
        boolean isValid = false;

        if(action != null && !action.isEmpty()){
            List<String> eventActionList = GetEventActions();
            isValid = eventActionList.contains(action);
        }

        return isValid;
    }


}
