package e3ps.loader.service;

import wt.method.RemoteInterface;

@RemoteInterface
public interface LoaderService {

	public abstract void loaderMak(String mak, String detail) throws Exception;

}
