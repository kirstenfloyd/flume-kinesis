package org.rightnow.logging.models;

/**
 * Created by kirsten on 8/21/17.
 */
public class ErrorModel {

    private String field;
    private String reason;

    public ErrorModel(String field, String reason){
        this.field = field;
        this.reason = reason;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
