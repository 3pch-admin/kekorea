package e3ps.project.template.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface TemplateService {

	public abstract void create(Map<String, Object> params) throws Exception;

	public abstract void save(Map<String, Object> params) throws Exception;

	public abstract void saveUserLink(Map<String, Object> params) throws Exception;

}
