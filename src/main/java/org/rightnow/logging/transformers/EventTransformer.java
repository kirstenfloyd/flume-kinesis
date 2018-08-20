package org.rightnow.logging.transformers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang.SerializationException;
import org.apache.flume.Event;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rightnow.logging.events.validators.GenericEventValidator;
import org.rightnow.logging.models.EventErrorModel;
import org.rightnow.logging.models.GenericEventModel;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.zip.GZIPInputStream;


/**
 * Created by kirsten on 4/3/18.
 */
public class EventTransformer {

    private static final Logger logger = LogManager.getLogger(EventTransformer.class);

    public String getJson(byte[] bytes, Event event) {
        try {
            String json = DecompressBytesToString(bytes);

            GenericEventModel eventModel = ConvertJsonToGenericEvent(json);
            if(GenericEventValidator.HasError(eventModel)){
                Gson gson = new Gson();
                EventErrorModel errorModel = ConvertEventToErrorModel(eventModel);
                json = gson.toJson(errorModel);
            }
            else{
                json = GetFinalJsonToDeliver(json);
            }

            json += System.lineSeparator();
            return json;
        } catch (JsonSyntaxException e) {
            logger.log(Level.ERROR, e);
            throw new SerializationException(e);
        } catch (Exception e) {
            logger.log(Level.ERROR, e);
            throw new SerializationException(e);
        }

    }

    private String DecompressBytesToString(byte[] compressed) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is);
        InputStreamReader streamReader = new InputStreamReader(gis);
        BufferedReader reader = new BufferedReader(streamReader);

        String outStr = "";
        String line;
        while ((line=reader.readLine())!=null) {
            outStr += line;
        }
        return outStr;
    }

    private GenericEventModel ConvertJsonToGenericEvent(String json) {
        GenericEventModel model = null;
        try {
            Gson gson = new Gson();
            model = gson.fromJson(json, GenericEventModel.class);
        } catch (JsonSyntaxException e) {
            logger.log(Level.ERROR, e);
            throw new SerializationException(e);
        } catch (Exception e) {
            logger.log(Level.ERROR, e);
            throw new SerializationException(e);
        }

        return model;
    }

    private String GetFinalJsonToDeliver(String json){
        String finalJson;
        try {
            Gson gson = new Gson();

            long currentTimestamp =System.currentTimeMillis();
            GenericEventModel receivedModel = gson.fromJson(json, GenericEventModel.class);
            JsonElement jsonElement = gson.toJsonTree(receivedModel);
            jsonElement.getAsJsonObject().addProperty("received_timestamp", currentTimestamp);
            finalJson = gson.toJson(jsonElement);

        } catch (JsonSyntaxException e) {
            logger.log(Level.ERROR, e);
            throw new SerializationException(e);
        } catch (Exception e) {
            logger.log(Level.ERROR, e);
            throw new SerializationException(e);
        }

        return finalJson;
    }

    private EventErrorModel ConvertEventToErrorModel(GenericEventModel eventModel){

        long currentTimestamp =System.currentTimeMillis();

        EventErrorModel errorModel = new EventErrorModel(GenericEventValidator.GetErrorsForEvent(eventModel),
                eventModel.getTimestamp(), currentTimestamp,GetDateFromTimestamp(currentTimestamp), eventModel);

        return errorModel;
    }

    private String GetDateFromTimestamp(long timestamp){
        java.util.Date date = new java.util.Date(timestamp);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(date);
        return dateString;
    }


}
