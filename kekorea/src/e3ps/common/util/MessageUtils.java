package e3ps.common.util;

import java.util.Locale;

import org.springframework.context.support.MessageSourceAccessor;

public class MessageUtils {

	private static MessageSourceAccessor msa = null;

	public void setMessageSourceAccessor(MessageSourceAccessor msa) {
		MessageUtils.msa = msa;
	}

	public static String getMessage(String code) {
		return msa.getMessage(code, Locale.getDefault());
	}

	public static String getMessage(String code, Object[] objs) {
		return msa.getMessage(code, objs, Locale.getDefault());
	}
}
