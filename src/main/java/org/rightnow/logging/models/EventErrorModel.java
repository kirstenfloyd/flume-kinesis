package org.rightnow.logging.models;

import java.util.List;

/**
 * Created by kirsten on 6/15/18.
 */
public class EventErrorModel {

    private List<ErrorModel> errors;
    private long event_timestamp;
    private long error_timestamp;
    private String error_date;
    private GenericEventModel original_event;

    public EventErrorModel(List<ErrorModel> errors, long event_timestamp, long error_timestamp, String error_date, GenericEventModel original_event) {
        this.errors = errors;
        this.event_timestamp = event_timestamp;
        this.error_timestamp = error_timestamp;
        this.error_date = error_date;
        this.original_event = original_event;
    }

    public List<ErrorModel> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorModel> errors) {
        this.errors = errors;
    }

    public long getEvent_timestamp() {
        return event_timestamp;
    }

    public void setEvent_timestamp(long event_timestamp) {
        this.event_timestamp = event_timestamp;
    }

    public long getError_timestamp() {
        return error_timestamp;
    }

    public void setError_timestamp(long error_timestamp) {
        this.error_timestamp = error_timestamp;
    }

    public String getError_date() {
        return error_date;
    }

    public void setError_date(String error_date) {
        this.error_date = error_date;
    }

    public GenericEventModel getOriginal_event() {
        return original_event;
    }

    public void setOriginal_event(GenericEventModel original_event) {
        this.original_event = original_event;
    }
}
