package org.rightnow.logging.models;

/**
 * Created by kirsten on 8/21/17.
 */
public class GenericEventOutputModel {

    private String event_initiator;
    private String event_name;
    private String event_id;
    private String correlated_event_id;
    private String session_id;
    private int user_id;
    private int org_id;
    private int staff_id;
    private String ip_address;
    private String aws_id;
    private long timestamp;
    private long elapsed_time;
    private String event_details;
    private String event_date;

    public String getEventInitiator() {
        return event_initiator;
    }

    public void setEventInitiator(String event_initiator) {
        this.event_initiator = event_initiator;
    }

    public String getEventName() {
        return event_name;
    }

    public void setEventName(String event_name) {
        this.event_name = event_name;
    }

    public String getEventId() {
        return event_id;
    }

    public void setEventId(String event_id) {
        this.event_id = event_id;
    }

    public String getCorrelatedEventId() {
        return correlated_event_id;
    }

    public void setCorrelatedEventId(String correlated_event_id) {
        this.correlated_event_id = correlated_event_id;
    }

    public String getSessionId() {
        return session_id;
    }

    public void setSessionId(String session_id) {
        this.session_id = session_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public int getOrgId() {
        return org_id;
    }

    public void setOrgId(int org_id) {
        this.org_id = org_id;
    }

    public int getStaffId() {
        return staff_id;
    }

    public void setStaffId(int staff_id) {
        this.staff_id = staff_id;
    }

    public String getIpAddress() {
        return ip_address;
    }

    public void setIpAddress(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getAwsId() {
        return aws_id;
    }

    public void setAwsId(String aws_id) {
        this.aws_id = aws_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getElapsedTime() {
        return elapsed_time;
    }

    public void setElapsedTime(long elapsed_time) {
        this.elapsed_time = elapsed_time;
    }

    public String getEventDetails() {
        return event_details;
    }

    public void setEventDetails(String event_details) {
        this.event_details = event_details;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }
}
