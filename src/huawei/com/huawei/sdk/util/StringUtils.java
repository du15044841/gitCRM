package com.huawei.sdk.util;

import java.io.IOException;
import java.io.StringWriter;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * <p>Title: String Processing Tools</p>
 * <p>Description: String Processing Tools</p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @author j00204006
 * @version V1.0 2014年9月2日
 * @since
 */
public class StringUtils
{
    /**
     * Determine whether the string is null or empty string (no spaces).
     * @param str String input
     * @return true/false
     */
    public static boolean isNullOrEmpty(String str)
    {
        return str == null || str.isEmpty();
    }
    
    /**
     * Determine whether the string is null or empty string (including spaces).
     * @param str String Input
     * @return true/false
     */
    public static boolean isNullOrBlank(String str)
    {
        return str == null || str.trim().isEmpty();
    }
    
    
    /**
     * Object to json
     * @param object object
     * @return json Json String
     * @throws IOException 
     */
    public static String beanToJson(Object object) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(writer);
        mapper.writeValue(gen, object);
        gen.close();
        String json = writer.toString();
        writer.close();
        return json;
    }
    
}
