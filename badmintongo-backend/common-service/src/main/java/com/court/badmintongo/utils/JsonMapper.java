package com.court.badmintongo.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * JsonMapper 轉換
 */
public class JsonMapper {
    private static final Logger logger = LoggerFactory.getLogger(JsonMapper.class);

    private static ObjectMapper mapper;

    /** 初始化ObjectMapper **/
    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 將任何的object透過ObjectMapper轉換成JSON字串
     *
     * @param obj any Object
     * @return JSON string
     */
    public static String toJSON(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonGenerationException e) {
            logger.error("無法將{}轉換成JSON字串：{}", obj, e.getMessage());
        } catch (JsonMappingException e) {
            logger.error("無法將{}轉換成JSON字串：{}", obj, e.getMessage());
        } catch (IOException e) {
            logger.error("無法將{}轉換成JSON字串：{}", obj, e.getMessage());
        }
        return "{}";
    }

    /**
     * 將JSON字串轉換成物件(忽略多餘的欄位)
     * @param jsonString 要轉換的JSON字串
     * @param resultClass 要轉換的物件類別
     * @return 轉換後的物件
     */
    public static <T> T fromJSON(String jsonString, Class<T> resultClass) {
        T result = null;
        try {
            result = mapper.readValue(jsonString, resultClass);
        } catch (JsonParseException e) {
            logger.error("無法將JSON字串轉換成{}：{}", resultClass, e.getMessage());
        } catch (JsonMappingException e) {
            logger.error("無法將JSON字串轉換成{}：{}", resultClass, e.getMessage());
        } catch (IOException e) {
            logger.error("無法將JSON字串轉換成{}：{}", resultClass, e.getMessage());
        }
        return result;
    }

    public static List<JsonNode> getNodeValues(String jsonString, String nodeName) {
        try {
            JsonNode jsonNode = mapper.readTree(jsonString);
            return jsonNode.findValues(nodeName);
        } catch (IOException e) {
            logger.error("該JSON字串無{}節點：{}", nodeName, e.getMessage());
        }
        return null;
    }

    public static Boolean isJsonFormat(String jsonString) {
        try {
            mapper.readTree(jsonString);
            return true;
        } catch (IOException e) {
            logger.error("非法JSON格式：{}", e.getMessage());
            return false;
        }
    }
}
