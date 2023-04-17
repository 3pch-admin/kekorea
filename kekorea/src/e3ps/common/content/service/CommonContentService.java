package e3ps.common.content.service;

import java.io.File;

import wt.fv.uploadtocache.CacheDescriptor;
import wt.fv.uploadtocache.CachedContentDescriptor;
import wt.method.RemoteInterface;

@RemoteInterface
public interface CommonContentService {

	/**
	 * 첨부 파일 파일볼트 업로드
	 */
	public abstract CachedContentDescriptor doUpload(CacheDescriptor localCacheDescriptor, File file) throws Exception;

}
