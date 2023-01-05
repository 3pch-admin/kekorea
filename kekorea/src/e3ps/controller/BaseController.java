package e3ps.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class BaseController {

	/**
	 * return success
	 */
	protected final String SUCCESS = "SUCCESS";

	/**
	 * return fail
	 */
	protected final String FAIL = "FAIL";

	/**
	 * 에러페이지 주소
	 */
	protected final String ERROR_PAGE_URL = "/Windchill/plm/common/errorPage";
}
