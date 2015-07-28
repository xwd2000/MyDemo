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
    
    
    public static RequestParam signRequest(RequestParam request,String method){
    	
    	Map<String,Object> params = request.getParams();
    	Map<String,Object> paramsWithMethod = new HashMap<String,Object>(params);
    	paramsWithMethod.put("method", method);//签名中添加method
    	String sortedParam = getSortedParam(paramsWithMethod);
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
 
    public static String generateEncryptedParam(Map<String,Object> businessParams,String key,String method){
    	RequestParam rp = new RequestParam();
    	rp.setParams(businessParams);
    	rp = signRequest(rp,method);
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
