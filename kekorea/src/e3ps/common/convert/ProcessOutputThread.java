package e3ps.common.convert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessOutputThread extends Thread {
	private InputStream is;
	private StringBuffer msg;

	public ProcessOutputThread(InputStream is, StringBuffer msg) {
		this.is = is;
		this.msg = msg;
	}

	public void run() {
		try {
			msg.append(getStreamString(is));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String getStreamString(InputStream is) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is));
			StringBuffer out = new StringBuffer();
			String stdLine;
			while ((stdLine = reader.readLine()) != null) {
				out.append(stdLine);
			}
			return out.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}