package e3ps.erp.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import e3ps.common.myBatis.service.QueryDao;

@Service
public class ErpSendService {

	@Inject
	private QueryDao queryDao;

	public List<Map<String, Object>> partListItemValue(Map<String, Object> params) throws Exception {
		System.out.println("queryDao=" + queryDao);
		List<Map<String, Object>> list = queryDao.find("erp.partListItemValue", params);
		System.out.println(list);
		for (int i = 0; i < list.size(); i++) {

		}
		return null;
	}
}
