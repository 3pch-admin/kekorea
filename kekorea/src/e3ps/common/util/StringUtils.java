package e3ps.common.util;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class StringUtils {

	// 공백 상수 변수
	private static final String EPMTY_CHAR = "";

	// 객체 생성 방지
	private StringUtils() {

	}

	// start 2023 year code refactoring

	/**
	 * String 값이 null 인지 체크하는 함수
	 */
	public static boolean isNull(String value) {
		if (value == null || value.length() == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * String 변수의 값이 null 인지 체크 후 null 일 경우 공백, 값이 있을 경우 기존의 변수 값을 그대로 리턴하는 함수
	 * 
	 * @param arg : 체크 하려는 String 값
	 * @return String
	 */
	public static String getParameter(String arg) {
		if (isNull(arg)) {
			return EPMTY_CHAR;
		}
		return arg;
	}

	/**
	 * String 변수의 값이 null 인지 체크 후 null 일 경우 두 번째 인자의 값을 리턴, 값이 있을 경우 기존의 변수 값을 그대로
	 * 리턴하는 함수
	 * 
	 * @param arg  : 체크 하려는 String 값
	 * @param init : 체크 한 값이 null 일 경우 리턴할 값
	 * @return String
	 */
	public static String getParameter(String arg, String init) {
		if (isNull(arg)) {
			return EPMTY_CHAR;
		}
		return arg;
	}

	/**
	 * @param map : Map 객체
	 * @return boolean
	 *         <p>
	 *         Map객체가 null or size 가 0 일 경우 true
	 *         </p>
	 */
	private static boolean isMapNull(Map map) {
		boolean isNull = false;
		if (map == null || map.size() == 0) {
			isNull = true;
		}
		return isNull;
	}

	/**
	 * @param list : ArrayList 배열
	 * @return boolean
	 *         <p>
	 *         배열이 null or size 가 0 일 경우 true
	 *         </p>
	 */
	private static boolean isArrayNull(List list) {
		boolean isNull = false;
		if (list == null || list.size() == 0) {
			isNull = true;
		}
		return isNull;
	}

	/**
	 * 값 체크후 null 값이면 공백 리턴
	 */
	public static String replaceToValue(String value) {
		return replaceToValue(value, EPMTY_CHAR);
	}

	/**
	 * 값 체크후 null 값이면 두번째 파라미터 리턴, 아닐경우 원래 값 리턴
	 */
	public static String replaceToValue(String value, String dValue) {
		if (isNull(value)) {
			return dValue.trim();
		}
		return value.trim();
	}

	/**
	 * @param value : Boolean 값으로 return 할 String 값
	 * @return boolean
	 *         <p>
	 *         parameter String 값이 null or true가 아니면 false return <br>
	 *         그 외 모든 값은 true return
	 *         </p>
	 */
	public static boolean parseBoolean(String value) {
		boolean bool = true;
		if (isNull(value) || !value.equalsIgnoreCase("true")) {
			bool = false;
		}
		return bool;
	}

	public static String numberFormat(String param, String format) {
		if (param.indexOf(",") > -1) {
			param = param.replaceAll(",", "");
		}

		Double dd = Double.parseDouble(param);
		DecimalFormat df = new DecimalFormat(format);
		return df.format(dd);
	}

	public static String numberFormat(String param) {

		if (param.indexOf(",") > -1) {
			param = param.replaceAll(",", "");
		}

		Double dd = Double.parseDouble(param);
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(dd);
	}

	public static String numberFormat(double param, String format) {
		DecimalFormat df = new DecimalFormat(format);
		return df.format(param);
	}

	public static String numberFormat(double param) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(param);
	}

	public static String checkReplaceStr(String str, String defaultStr) {
		if (str == null || str.equals(""))
			return defaultStr;
		else
			return str.trim();
	}

	public static int parseInt(String value) {
		return parseInt(value, 0);
	}

	public static int parseInt(String value, int defaultValue) {
		if (isNull(value)) {
			return defaultValue;
		}
		return Integer.parseInt(value);
	}

	public static long parseLong(String value) {
		return parseLong(value, 0);
	}

	public static long parseLong(String value, long defaultValue) {
		if (isNull(value)) {
			return defaultValue;
		}
		return Long.parseLong(value);
	}
}
