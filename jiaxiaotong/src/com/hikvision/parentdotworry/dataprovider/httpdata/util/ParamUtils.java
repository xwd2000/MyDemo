package com.hikvision.parentdotworry.dataprovider.httpdata.util;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.dataprovider.httpdata.bean.RequestParam;
import com.hikvision.parentdotworry.dataprovider.httpdata.bean.SystemParam;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.QEncodeUtil;
import com.hikvision.parentdotworry.utils.StringUtils;
import com.videogo.util.MD5Util;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParamUtils {
	private static Logger logger = Logger.getLogger(ParamUtils.class);
	/**
	 * 获取签名
	 * @param params
	 * @return
	 */
	public static String getSignedRequestString(Map<String,Object> params){
		 Map<String, Object> resultMap = paramsInit("c279ded87d3f4fdca7658f95fb5f1d9e",
	                "b097e53bb9627e7e32b7a8001c701151", "msg/get", params);
	        JSONObject signJson;
	        try {
	            signJson = setJosn(resultMap);
	            return signJson.toString();
	        } catch (Exception e) {
	            e.printStackTrace();
	        };
	        return null;
	}

    public static String getGetSmsCodeSign(String phone) {
        Map<String, Object> map = new HashMap<String, Object>();
        {
            map.put("type", 1);
            map.put("userId", "654321");
            map.put("phone", phone);
        }
        Map<String, Object> resultMap = paramsInit("c279ded87d3f4fdca7658f95fb5f1d9e",
                "b097e53bb9627e7e32b7a8001c701151", "msg/get", map);
        JSONObject signJson;
        try {
            signJson = setJosn(resultMap);
            return signJson.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
        return null;
    }
    
    public static String getGetAccessTokenSign(String phone) {
        Map<String, Object> map = new HashMap<String, Object>();
        {
            map.put("userId", "654321");
            map.put("phone", phone);
        }
        Map<String, Object> resultMap = paramsInit("c279ded87d3f4fdca7658f95fb5f1d9e",
                "b097e53bb9627e7e32b7a8001c701151", "token/accessToken/get", map);
        JSONObject signJson;
        try {
            signJson = setJosn(resultMap);
            return signJson.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
        return null;
    }
    
    /**
     * <p>
     * 参数初始化
     * </p>
     * 
     * @author pengxiongwei 2014年12月11日 下午3:29:03
     * @param appKey
     * @param appSecret
     * @param method
     * @param paramsMap
     * @return
     */
    public static Map<String, Object> paramsInit(String appKey, String appSecret, String method, Map<String, Object> paramsMap) {
        Map<String, Object> map = new HashMap<String, Object>();
        long time = System.currentTimeMillis() / 1000;
        StringBuilder paramString = new StringBuilder();
        if (paramsMap != null && !paramsMap.isEmpty()) {
            List<String> paramList = new ArrayList<String>();
            for (Iterator<String> it = paramsMap.keySet().iterator(); it.hasNext();) {
                String key1 = it.next();
                String param = key1 + ":" + paramsMap.get(key1);
                paramList.add(param);
            }
            String[] params = paramList.toArray(new String[paramList.size()]);
            Arrays.sort(params);
            for (String param : params) {
                paramString.append(param).append(",");
            }
        }
        paramString.append("method").append(":").append(method).append(",");
        paramString.append("time").append(":").append(time).append(",");
        paramString.append("secret").append(":").append(appSecret);
        String sign = MD5Util.getMD5String(paramString.toString().trim());

        Map<String, Object> systemMap = new HashMap<String, Object>();
        systemMap.put("ver", "1.0");
        systemMap.put("sign", sign);
        systemMap.put("key", appKey);
        systemMap.put("time", time);

        map.put("system", systemMap);
        map.put("method", method);
        map.put("params", paramsMap);
        map.put("id", "123456");
        return map;
    }
    
    /**
     * 将map数据解析出来，并拼接成json字符串
     * 
     * @param map
     * @return
     */
    public static JSONObject setJosn(Map map) throws Exception {
            JSONObject json = null;
            StringBuffer temp = new StringBuffer();
            if (!map.isEmpty()) {
                    temp.append("{");
                    // 遍历map
                    Set<?> set = map.entrySet();
                    Iterator<?> i = set.iterator();
                    while (i.hasNext()) {
                            Map.Entry entry = (Map.Entry) i.next();
                            String key = (String) entry.getKey();
                            Object value = entry.getValue();
                            temp.append("\"" + key + "\":");
                            if (value instanceof Map<?, ?>) {
                                    temp.append(setJosn((Map<String, Object>) value) + ",");
                            } else if (value instanceof List<?>) {
                                    temp.append(setList((List<Map<String, Object>>) value)
                                                    + ",");
                            } else {
                                    temp.append("\"" + value + "\"" + ",");
                            }
                    }
                    if (temp.length() > 1) {
                            temp = new StringBuffer(temp.substring(0, temp.length() - 1));
                    }
                    temp.append("}");
                    json = new JSONObject(temp.toString());
            }
            return json;
    }

    /**
     * 将单个list转成json字符串
     * 
     * @param list
     * @return
     * @throws Exception
     */
    public static String setList(List<Map<String, Object>> list)
                    throws Exception {
            String jsonL = "";
            StringBuffer temp = new StringBuffer();
            temp.append("[");
            for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> m = list.get(i);
                    if (i == list.size() - 1) {
                            temp.append(setJosn(m));
                    } else {
                            temp.append(setJosn(m) + ",");
                    }
            }
            if (temp.length() > 1) {
                    temp = new StringBuffer(temp.substring(0, temp.length()));
            }
            temp.append("]");
            jsonL = temp.toString();
            return jsonL;
    }

    /**
     * 将整个json字符串解析，并放置到map<String,Object>中
     * 
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> getJosn(String jsonStr) throws Exception {
            Map<String, Object> map = new HashMap<String, Object>();
            if (!TextUtils.isEmpty(jsonStr)) {
                    JSONObject json = new JSONObject(jsonStr);
                    Iterator<?> i = json.keys();
                    while (i.hasNext()) {
                            String key = (String) i.next();
                            String value = json.getString(key);
                            if (value.indexOf("{") == 0) {
                                    map.put(key.trim(), getJosn(value));
                            } else if (value.indexOf("[") == 0) {
                                    map.put(key.trim(), getList(value));
                            } else {
                                    map.put(key.trim(), value.trim());
                            }
                    }
            }
            return map;
    }

    /**
     * 将单个json数组字符串解析放在list中
     * 
     * @param jsonStr
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> getList(String jsonStr)
                    throws Exception {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            JSONArray ja = new JSONArray(jsonStr);
            for (int j = 0; j < ja.length(); j++) {
                    String jm = ja.get(j) + "";
                    if (jm.indexOf("{") == 0) {
                            Map<String, Object> m = getJosn(jm);
                            list.add(m);
                    }
            }
            return list;
    }
    
    /**
     * 返回排序的参数字符串
     * @param requestParam
     * @return
     */
    public static String getSortedParam(Map<String,Object> requestParam){
    	if (!EmptyUtil.isEmpty(requestParam)) {
            List<String> paramList = new ArrayList<String>();
            for (Iterator<String> it = requestParam.keySet().iterator(); it.hasNext();) {
                String key1 = it.next();
                String param = key1 + ":" + requestParam.get(key1);
                paramList.add(param);
            }
            String[] params = paramList.toArray(new String[paramList.size()]);
            Arrays.sort(params);
            return StringUtils.join(params);
        }
    	return "";
    }
    
    
    public static RequestParam signRequest(RequestParam request){
    	
    	Map<String,Object> params = request.getParams();
    	String sortedParam = getSortedParam(params);
    	String sign = getSign(sortedParam);
    	
    	SystemParam systemParam = request.getSystem();
    	if(systemParam==null){
    		systemParam = new SystemParam();
    		request.setSystem(systemParam);
    	}
    	
    	systemParam.setSign(sign);
    	return request;
    }
    
    public static String getSign(String sortedParam){
    	return MD5Util.getMD5String(sortedParam);
    }
 
    public static String generateEncryptedParam(Map<String,Object> businessParams,String key){
    	RequestParam rp = new RequestParam();
    	rp.setParams(businessParams);
    	rp = signRequest(rp);
    	String param = new Gson().toJson(rp);
    	logger.debug("请求参数明文:"+param);
    	try {
			return QEncodeUtil.encrypt(param, key);
		} catch (Exception e) {
			logger.error("密文加密失败:"+e.getMessage());
		}
    	return null;
    }
    
}