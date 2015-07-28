package com.hikvision.parentdotworry.dataprovider.httpdata.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.dataprovider.httpdata.ezbean.EzRequestParam;
import com.hikvision.parentdotworry.dataprovider.httpdata.ezbean.EzSystemParam;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.MD5;
import com.hikvision.parentdotworry.utils.StringUtils;
import com.videogo.util.MD5Util;

public class EzParamUtils {

	private static String PROTOCOL_VERSION=AppApplication.NET_EZ_PROTOCOL_VERION;
	private String APP_KEY=AppApplication.NET_EZ_APP_KEY;
	private String APP_SECRET=AppApplication.NET_EZ_SECRET_KEY;
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
    
    private EzSystemParam generateunSignedEzRequestParam(){
    	EzSystemParam ezSystemParam = new EzSystemParam();
    	ezSystemParam.setKey(APP_KEY);
    	ezSystemParam.setTime(System.currentTimeMillis() / 1000);
    	ezSystemParam.setVer(PROTOCOL_VERSION);
    	return ezSystemParam;
    }
    
    private EzRequestParam signEzRequestParam(EzRequestParam requestParam){
    	EzSystemParam systemParam=requestParam.getSystem();
    	
    	String method = requestParam.getMethod();
    	Long time = systemParam.getTime();
    	String appSecret = APP_SECRET;
    	Map<String,Object> param = requestParam.getParams();
    	
    	String sign = getSign(method,time,appSecret,param);
    	systemParam.setSign(sign);
    	
    	return requestParam;
    }
    
    public String getParam(EzRequestParam unSignedRequestParam){
    	return new Gson().toJson(signEzRequestParam(unSignedRequestParam));
    }
    
    private String getSign(String method,Long  time,String appSecret,Map<String,Object> param){
    	String sortedParam = getSortedParam(param);
    	StringBuilder paramString = new StringBuilder(sortedParam);
    	paramString.append(",");
		paramString.append("method").append(":").append(method).append(",");
		paramString.append("time").append(":").append(time).append(",");
		paramString.append("secret").append(":").append(appSecret);
		String sign = MD5.md5(paramString.toString().trim()).toLowerCase(Locale.getDefault());
		return sign;
    }
    
    
    
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
}
