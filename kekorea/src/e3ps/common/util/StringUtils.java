package e3ps.common.util;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @author jhkim
 */
public class StringUtils {

	/**
	 * 기본 공백 변수
	 */
	private static final String EPMTY_CHAR = "";

	/**
	 * 객체 생성 방지
	 */
	private StringUtils() {

	}

	/**
	 * @param value : parameter String 값
	 * @return boolean : String 값이 null 일 경우 true, not null 일 경우 false
	 *         <p>
	 *         String 값이 null 인지 체크 하는 함수
	 *         </p>
	 */
	public static boolean isNull(String value) {
		boolean isNull = false;
		if (value == null || value.trim().length() == 0 || value.trim().equalsIgnoreCase("null")) {
			isNull = true;
		}
		return isNull;
	}

	/**
	 * @param obj : parameter Object 객체
	 * @return boolean : Object 객체가 null 일 경우 true, not null 일 경우 false
	 *         <p>
	 *         Object 객체가 null 인지 체크 하는 함수
	 *         </p>
	 */
	public static boolean isNull(Object obj) {
		boolean isNull = false;

		if (obj instanceof List) {
			List list = (List) obj;
			isNull = isArrayNull(list);
		} else if (obj instanceof Map) {
			Map map = (Map) obj;
			isNull = isMapNull(map);
		} else {
			if (obj == null) {
				isNull = true;
			}
		}
		return isNull;
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
	 * @param value : parameter String 값
	 * @return String
	 *         <p>
	 *         String 값이 null 일 경우 공백 값 return <br>
	 *         String 값이 not null 일 경우 parameter로 들어온 String 값 return
	 *         </p>
	 */
	public static String getParameter(String value) {
		if (isNull(value)) {
			return EPMTY_CHAR;
		}
		return value.trim();
	}

	/**
	 * @param value  : parameter String 값
	 * @param dValue : parameter value가 null 일 경우 return 될 String 값
	 * @return String
	 *         <p>
	 *         parameter String 값이 null 일 parameter dValue 값 return <br>
	 *         parameter String 값이 not null 일 경우 parameter로 들어온 String 값 return
	 *         </p>
	 */
	public static String getParameter(String value, String dValue) {
		if (isNull(value)) {
			return dValue.trim();
		}
		return value.trim();
	}

	/**
	 * @param value : String 값
	 * @return String
	 *         <p>
	 *         parameter String 값이 null 일 경우 공백 return
	 *         </p>
	 */
	public static String replaceToValue(String value) {
		return replaceToValue(value, EPMTY_CHAR);
	}

	/**
	 * @param value  : String 값
	 * @param dValue : String 값이 null 일 경우 return 될 값
	 * @return String
	 *         <p>
	 *         parameter String 값이 null 일 경우 parameter dValue 값 return
	 *         </p>
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
