function createCDialogWindow(dialogURL, dialogName, w, h, statusBar, scrollBar) {
	if( typeof(_use_wvs_cookie) != "undefined" ) {
		var cookie_value = document.cookie;
		if( cookie_value != null ) {
			var loc = cookie_value.indexOf("wvs_ContainerOid=");
			if( loc >= 0 ) {
				var subp = cookie_value.substring(loc+4);
				loc = subp.indexOf(";");
				if( loc >= 0 ) subp = subp.substring(0, loc);
				dialogURL += "&" + subp;
			}
		}
	}else {
		var vm_url = "" + top.document.location;
		if( vm_url != null ) {
			var loc = vm_url.indexOf("ContainerOid=");
			if( loc < 0 ) {
				loc = vm_url.indexOf("/listFiles.jsp?oid=");
				if( loc >= 0 ) {
					vm_url = "ContainerOid=" + vm_url.substring(loc+19);
					loc = 0;}}if( loc >= 0 ) {
						var subp = vm_url.substring(loc);
						loc = subp.indexOf("&");
						if( loc >= 0 ) subp = subp.substring(0, loc);
						if( dialogURL.indexOf("/blank.jsp?")>0 ) {
							loc = dialogURL.indexOf("fname=");
							if( loc >= 0 ) {
								var fname = dialogURL.substring(loc+6);
								loc = fname.indexOf("&");
								if( loc >= 0 ) fname = fname.substring(0, loc);
								try {
									if( document.forms[fname].action.indexOf("ContainerOid=")<0 ) {
										document.forms[fname].action += "&" + subp;
									}
								} catch(e) {}
							}
						}
						dialogURL += "&" + subp;
					}
				}
			}
			createDialogWindow(dialogURL, dialogName, w, h, statusBar, scrollBar);
		}
		function createDialogWindow(dialogURL, dialogName, w, h, statusBar, scrollBar) {
			if( statusBar == null ) statusBar = 0;
			if( scrollBar == null ) scrollBar = 1;
			var opts = "toolbar=0,location=0,directory=0,status=" + statusBar + ",menubar=0,scrollbars="+scrollBar+",resizable=1,width=" + w + ",height=" + h;
			createDialogWindowOptions(dialogURL, dialogName, opts);
		}
		function createDialogWindowOptions(dialogURL, dialogName, opts) {
			if ( opts == "" ) {
				if (navigator.userAgent.indexOf("Mozilla/4.") >= 0 && navigator.userAgent.indexOf("MSIE") == -1) {
					opts = "toolbar=1,location=1,directory=1,status=1,menubar=1,scrollbars=1,resizable=1";
				}
			}
			if( dialogName.indexOf("VisNav") == 0 ) opts = opts + ",top=0,left=0";
			var newwin = wfWindowOpen(dialogURL, dialogName, opts);
			if( newwin != null ) newwin.focus();
		}
		function closeDialogWindow() { wfWindowClose();}
		function evalJspResult(aEvent,aURL) {
			var mOid = null; 
			var mContainerOid = null; 
			var mRowOid = null; 
			var mRowContainerOid = null;
			mOid = extractParamValue(window.location.href,"oid");
			if ((mOid == null) || (mOid.length < 1)) {mOid = extractParamValue(window.location.href,"objref");}
			mContainerOid = extractParamValue(window.location.href,"ContainerOid");
			var mEvent = (aEvent != null) ? aEvent : event;
			var mTarget = (mEvent.target) ? mEvent.target : mEvent.srcElement;
			if (mTarget) {
				var mTRnode = getParentNodeByTag(mTarget,"TR");
				if (mTRnode && (mTRnode.nodeName == "TR")) {
					var mAtt = mTRnode.getAttribute("onclick");
					if (mAtt) {
						var mOnclick = mAtt.toString();
						var mItems = mOnclick.split(",");
						for (var i = 0; (i < mItems.length) && ((mRowOid == null) || (mRowContainerOid == null)); i++) {
							var mItem = trimString(mItems[i]);
							if (mItem == "'oid'") {
								i++;
								var tmp = trimString(mItems[i]);
								mRowOid = tmp.substring(1,tmp.length-1);
							} else if (mItem == "'ContainerOid'") {
								i++;var tmp = trimString(mItems[i]);
								mRowContainerOid = tmp.substring(1,tmp.length-1);
							}
						}
					}
				}
			}
			var mDelim = "?";
			if (aURL.indexOf(mDelim) >= 0) {mDelim = "&";}
			if ((mOid != null) && (mOid.length > 0)) {aURL += mDelim + "oid=" + mOid;mDelim = "&";}
			if ((mContainerOid != null) && (mContainerOid.length > 0)) {aURL += mDelim + "ContainerOid=" + mContainerOid;mDelim = "&";}
			if ((mRowOid != null) && (mRowOid.length > 0)) {aURL += mDelim + "rowOid=" + mRowOid;mDelim = "&";}
			if ((mRowContainerOid != null) && (mRowContainerOid.length > 0)) {aURL += mDelim + "rowContainerOid=" + mRowContainerOid;mDelim = "&";}
			if (getMainForm()) {if(getMainForm().changeItemRef) {aURL += mDelim + "changeItem=" + getMainForm().changeItemRef.value;mDelim = "&";}
			if(getMainForm().changeableRef) {aURL += mDelim + "changeable=" + getMainForm().changeableRef.value;mDelim = "&";}
		}
		if (requestHandler) {
			var mOpts = {asynchronous: false};
			var mReq = requestHandler.doRequest(aURL,mOpts);
			if (mReq && mReq.responseText) {
				var mResp = trimString(mReq.responseText);
				if (mResp.length > 0) {eval(mResp);
			}
		}
	}
	return false;
}

function copyToCommonClipboard(copyToClipboardURL){
	
	var ContainerOid = "";
	if (typeof(_use_wvs_cookie) != "undefined") {
		var documentCookie = document.cookie;
		if (documentCookie != null) {
			var start = documentCookie.indexOf("wvs_ContainerOid=");
			if (start >= 0) {
				start += 4;
				var end = documentCookie.indexOf(";",start + 1);
				ContainerOid = (end > start) ? documentCookie.substring(start,end) : cookie_value.substring(start);
			}
		}
	}
	var url = copyToClipboardURL;
	
	if (ContainerOid.length > 0) {url += "&" + ContainerOid;}
	var xmlhttp = newHttp();
	xmlhttp.open("GET", url, false);
	xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
	xmlhttp.send(null);
	
	if (xmlhttp.responseText && xmlhttp.responseText.length > 0) {
		var msg = trimString(xmlhttp.responseText);
		if (msg && msg.length > 0) {
			wfalert(msg);
		}
	}
}

function trimString(str) {str = this != window? this : str;return str.replace(/^\s+/g, '').replace(/\s+$/g, '');}
	
function wfWindowOpen(url, name, opts) {
	var popupWin = window.open(url, name, opts);
	setTimeout(function() {
		popupWin.focus();
	}, 1);
	return popupWin;
}