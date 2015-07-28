package com.hikvision.parentdotworry.utils;


import java.util.regex.Pattern;

/**
 * Author: tang_botao
 * DateTime: 13-10-18 上午10:50
 * Explain:数据校验工具24.
 */
public class ValidateUtils {

	private static final Pattern NUMBER_PATTERN = Pattern.compile("^[-\\+]?[0-9]+(\\.[0-9]+)?$");
	private static final Pattern POSITIVE_NUMBER_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
	private static final Pattern INTEGER_PATTERN = Pattern.compile("^[-\\+]?\\d+$");
	private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("^\\d+$");
	private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("^((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))$");
	private static final Pattern TIME_PATTERN = Pattern.compile("^(([01][0-9])|(2[0-3])):[0-5][0-9]:[0-5][0-9]$");
	private static final Pattern NORMAL_CHAR = Pattern.compile("^[A-Za-z0-9_]+$");
	private static final Pattern SPECIAL_CHAR = Pattern.compile("[^A-Za-z0-9_]+");
	private static final Pattern CAN_SPLIT_NUM = Pattern.compile("^\\d+(\\,\\d+)*$");
	private static final Pattern TELEPHONE_MOBILE_PHONE_PATTERN = Pattern.compile("(^(\\d{3,4}-)?\\d{7,8})(-\\d+)?$|^((\\+86)|(86))?(1)\\d{10}$");


	/**
	 * 检验是否为合法的电话号码或手机号码
	 *
	 * @param phoneNo 目测字符
	 * @return 检验结果
	 */
	public static boolean isTelephoneOrMobilePhoneNo(String phoneNo) {
		return matches(TELEPHONE_MOBILE_PHONE_PATTERN, phoneNo);
	}

	public static boolean isComputerPort(Integer port) {
		return port != null && port >= 0 && port <= 65535;
	}

	public static boolean isIpAddress(String ipAddress) {
		return matches(IP_ADDRESS_PATTERN, ipAddress);
	}

	/**
	 * 验证一个字符串为空(null 或者 空串)
	 *
	 * @param str 目标字符串
	 * @return 验证结果
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * 验证一个字符串不为空(null 或者 空串)
	 *
	 * @param str 目标字符串
	 * @return 验证结果
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 验证为数字，包含小数点，正负号
	 *
	 * @param str 目标字符串
	 * @return 验证结果
	 */
	public static boolean isNumber(String str) {
		return matches(NUMBER_PATTERN, str);
	}

	/**
	 * 验证为正数数字，包含小数点，正负号
	 *
	 * @param str 目标字符串
	 * @return 验证结果
	 */
	public static boolean isPositiveNumber(String str) {
		return matches(POSITIVE_NUMBER_PATTERN, str);
	}

	/**
	 * 验证为整数，包含正负号
	 *
	 * @param str 目标字符串
	 * @return 验证结果
	 */
	public static boolean isInteger(String str) {
		return matches(INTEGER_PATTERN, str);
	}

	/**
	 * 验证为正整数，包含正号，包含了0，+0，-0不包括
	 *
	 * @param str 目标字符串
	 * @return 验证结果
	 */
	public static boolean isPositiveInteger(String str) {
		return matches(POSITIVE_INTEGER_PATTERN, str);
	}

	/**
	 * @param str
	 * @return
	 * @throws
	 * @Title: isTime
	 * @Description: 签证 13:32:43这类格式的字符串
	 * @returnType boolean
	 * @date 2014-6-24
	 */
	public static boolean isTime(String str) {
		return matches(TIME_PATTERN, str);
	}

	/**
	 * 正则校验基础方法
	 *
	 * @param pattern 正则对象
	 * @param str     被校验字符串
	 * @return 结果
	 */
	public static boolean matches(Pattern pattern, String str) {
		return isNotEmpty(str) && pattern.matcher(str.trim()).matches();
	}

	/**
	 * 校验是否有特殊符号
	 *
	 * @return
	 * @throws
	 * @Title: hasSpecialSymbol
	 * @Description: TODO
	 * @returnType boolean
	 * @date 2014-6-27
	 */
	public static boolean hasSpecialChar(String str) {
		return !matches(NORMAL_CHAR, str);
	}

	public static boolean canSplitNums(String str) {
		return matches(CAN_SPLIT_NUM, str);
	}

	/**
	 * @param str
	 * @return
	 * @throws
	 * @Title: clearSpecialChar
	 * @Description: 清除特殊符号
	 * @returnType boolean
	 * @date 2014-6-27
	 */
	public static String clearSpecialChar(String str) {
		return str.replaceAll("[^A-Za-z0-9_]+", "");
	}


	public static void main(String... args) {

		String[] testStrings = {"", null, "   				", " a.11 ", "1.11   11", "1.11", "0.11", ".11", "1a.11", "11.111a",
				"11", "-a.11", "+1.11", "-1.11", "-11", "+11", "-0", "+0", "-a", "和", "-1.1.11111"
				, "12345678909876125", "0"};

		for (String str : testStrings) {
			System.out.println("测试字符串:###" + str + "###,测试结果：" + isPositiveNumber(str));
		}

		System.out.println(isIpAddress("0.0.0.0"));
		System.out.println(isIpAddress("0.0.0.1"));
		System.out.println(isIpAddress("255.255.255.255"));
		System.out.println(isIpAddress("192.9.100.198"));
		System.out.println(isIpAddress("192.9.100.256"));
		System.out.println(isIpAddress("192.9.100.00"));
		System.out.println(isIpAddress("192.9.100.000"));


	}
}
