package e3ps.bom.partlist.cache;

import java.util.HashMap;
import java.util.Map;

public class CacheProcessor {

	private HashMap<String, Map<String, Object>> cacheData;
	private long expirationTime;

	public CacheProcessor() {
		if (cacheData == null) {
			cacheData = new HashMap<>();
			// 캐시 데이터 저장 시간 하루로..
			this.expirationTime = 24 * 60 * 60;
		}
	}

	public Map<String, Object> getValue(String key) {
		return cacheData.get(key);
	}

	public void setValue(String key, Map<String, Object> value) {
		cacheData.put(key, value);
	}
}
