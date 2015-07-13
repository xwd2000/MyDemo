package com.hikvision.test;

import java.util.HashMap;
import java.util.Map;

import com.hikvision.parentdotworry.dataprovider.httpdata.bean.RequestParam;
import com.hikvision.parentdotworry.dataprovider.httpdata.util.ParamUtils;

public class SignTest {

	/**
	 * @param args
	 */
	public static void test() {
		RequestParam rp = new RequestParam();
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("childId", 1);
		params.put("code", 123456);
		rp.setParams(params);
		rp = ParamUtils.signRequest(rp);
		System.out.println(rp);
	}

}
