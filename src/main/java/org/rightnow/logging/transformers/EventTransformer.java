package org.rightnow.logging.transformers;

import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang.SerializationException;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;


/**
 * Created by kirsten on 4/3/18.
 */
public class EventTransformer {

    private static final Logger logger = LogManager.getLogger(EventTransformer.class);


    public String getJson(byte[] bytes) {
        try {
            String json = DecompressBytesToString(bytes);
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


}
