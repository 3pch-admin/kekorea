//
// javascript functions to support embedding PTC Creo View.
//


// //////////////////////////////////////////////////////////////
// determine which browser we are running in - this global
// value _isIE is used elsewhere in the pvcadview scripts
// //////////////////////////////////////////////////////////////
var _isIE = false;
var _is64bitIE = false;
var _isFirefox = false;
var _isChrome = false;

var ver = navigator.userAgent;
if (ver.indexOf("MSIE") != -1) { // MSIE has been removed from IE 11
    _isIE = true;
    if (ver.indexOf("Win64") != -1) {
        _is64bitIE = true;
    }
} else if (ver.indexOf("Trident") != -1) { // Trident exists from IE8 >
    _isIE=true;
    if (ver.indexOf("Win64") != -1) {
        _is64bitIE = true;
    }
} else if (ver.toLowerCase().indexOf('firefox') > -1) {
    _isFirefox = true
} else if (ver.toLowerCase().indexOf('chrome') > -1) {
    _isChrome = true
}

// //////////////////////////////////////////////////////////////
// document-specific functions
// //////////////////////////////////////////////////////////////
var s_docType = "application/x-pvlite9-ed";
var s_docPluginVersion = "10.2.20.23";   // version string of the form '1.2.3.4'
var s_downloadDir = ""; // If set should end with a /
var s_basePvUrl = "";
var _visNavEvents = null;

var g_verCheckInstall = false;
var g_docLoadedAction = 0;
var g_installingVerCheck = false;
var g_pvlaunchInitialised = false;
var g_username = "";
var g_useremail = "";
var g_usertelno = "";
var g_heading = null;
var g_consumerInstall = false; // Setting to true will automatically prompt to install Creo View Consumer
var g_configUrl = "";

var _local_UpgradeTitle = "A new version of PTC Creo View is available for download";
var _local_UpgradeContinue = "Use currently installed viewer";
var _local_UpgradeInstall = "Upgrade viewer";
var _local_Reinstall = "PTC Creo View requires repair or reinstall";
var _local_Install = "Install PTC Creo View";
var _local_Enable_SoftwareUpdate = "You must enable Software Update to install this plugin";
var _local_Install_Failed = "PTC Creo View installation failed";
var _local_Unsupported_Browser = "Unsupported browser platform";
var _local_InitialisePlugin_Error = "An error occured initialising the PTC Creo View plugin";
var _local_Restart_Needed = "You need to restart your browser to complete the PTC Creo View installation"
var _local_nocheck = "Stop Checking for new versions"

if (!g_pvliteInstanceArray) {
    var g_pvliteInstance = 0;
    var g_pvliteInstanceArray = new Array();
}

function POINT3D(x, y, z) {
    this.x = x;
    this.y = y;
    this.z = z;
}

function SetupPView() {
    // Disable F1 Help provided by the browser when running in full screen mode(onhelp is ie only and ignored by Firefox).
    document.body.onhelp = function keyhit() { event.returnValue = false; };
    document.body.onmousewheel = function mousewheel() { event.returnValue = false; };

    var browser_platform = "";

    if (navigator.platform == "Win32") {
        if (_is64bitIE)
            browser_platform = "x86e_win64";
        else
            browser_platform = "i486_nt";
    } else if (navigator.platform == "Win64") {
        browser_platform = "x86e_win64";
    }

    if (browser_platform == "") {
        if (typeof _pvliteString_Unsupported_Browser != "undefined")
            _local_Unsupported_Browser = _pvliteString_Unsupported_Browser;
        window.alert(_local_Unsupported_Browser + "\n" + navigator.platform);
        return;
    }

    if (_isFirefox) {
        var s = unescape(document.URL);
        var p = s.lastIndexOf("/");
        s_downloadDir = s.substring(0, p + 1);
        g_docPluginURL = s_basePvUrl + browser_platform + "_ns/npvverck.xpi";
    } else if (_isIE) {
        var s = document.URL;
        if (s.indexOf("http") == 0) {
            var p = s.lastIndexOf("/");
            s_downloadDir = document.URL.substring(0, p + 1);
            g_docPluginURL = s_basePvUrl + browser_platform;
            g_docPluginURL += "_ie\\pvvercheck_usr.cab";
        } else {
            var p = s.lastIndexOf("\\");
            s_downloadDir = document.URL.substring(0, p + 1);
            g_docPluginURL = s_basePvUrl + browser_platform;
            g_docPluginURL += "_ie\\pvvercheck_usr.cab";
        }
    } else if (_isChrome) {
        var s = unescape(document.URL);
        var p = s.lastIndexOf("/");
        s_downloadDir = s.substring(0, p + 1);
    }

    if (typeof _pvliteString_Unsupported_Browser != "undefined")
        _local_Unsupported_Browser = _pvliteString_Unsupported_Browser;

    if (browser_platform == "") {
        window.alert(_local_Unsupported_Browser + "\n" + navigator.platform);
        return;
    }

    if (s_downloadDir.indexOf("file:///") == 0) {
        // Check so it can be ignored.
    } else if (s_downloadDir.indexOf("file://") == 0) {
        s_downloadDir = s_downloadDir.substring(7, s_downloadDir.length);
        s_downloadDir = "file:///" + s_downloadDir;
    }

    // version numbers in ie are comma seperated
    if (_isIE)
        s_docPluginVersion = s_docPluginVersion.replace(/\./g, ",");
}

function isRelativeUrl(inputUrl) {
    if (inputUrl.indexOf("http") == 0) {
        return false;
    } else if (inputUrl.indexOf("__pvbnfs") == 0) {
        return false;
    } else if (inputUrl.indexOf("file") == 0) {
        return false;
    } else if (inputUrl.indexOf(":") == 1) {
        return false;
    } else {
        return true;
    }
}

function LoadModel(sourceUrl, markupUrl, modifymarkupurl, uiconfigUrl) {
    document.getElementById(this.pvCtl).renderannotation = "";
    document.getElementById(this.pvCtl).renderviewable = "";

    var baseUrl;
    var newSourceUrl = fixPath(sourceUrl);

    if (isRelativeUrl(newSourceUrl)) {
        var newBaseUrl = fixPath(document.URL);
        loc = newBaseUrl.lastIndexOf("/");
        if (loc != -1)
            baseUrl = newBaseUrl.substring(0, loc);

        loc = newSourceUrl.lastIndexOf('/');
        if (loc != -1) {
            baseUrl += "/";
            baseUrl += newSourceUrl.substring(0, loc + 1);
        }
    } else {
        loc = newSourceUrl.lastIndexOf('/');
        if (loc != -1)
            baseUrl = newSourceUrl.substring(0, loc + 1);
    }

    baseUrl = checkFileURL(baseUrl);
    document.getElementById(this.pvCtl).urlbase = baseUrl;

    if (this.thumbnail) {
        if (isRelativeUrl(sourceUrl)) {
            document.getElementById(this.pvCtl).pvt = s_downloadDir + sourceUrl;
        } else {
            document.getElementById(this.pvCtl).modifymarkupurl = modifymarkupurl;
            document.getElementById(this.pvCtl).pvt = sourceUrl;
        }
    } else {
        if (isRelativeUrl(sourceUrl)) {
            document.getElementById(this.pvCtl).modifymarkupurl = modifymarkupurl;
            document.getElementById(this.pvCtl).edurl = s_downloadDir + sourceUrl;
        } else {
            document.getElementById(this.pvCtl).modifymarkupurl = modifymarkupurl;
            document.getElementById(this.pvCtl).edurl = sourceUrl;
        }
    }
}

function SetBackgroundColor(pBgColor) {
    document.getElementById(this.pvCtl).backgroundcolor = pBgColor;
}

function SetPvBaseUrl(baseUrl) {
    s_basePvUrl = baseUrl;
}

function SetViewingMode(pViewMode) {
    document.getElementById(this.pvCtl).SetViewOrientation(pViewMode);
}

function HideInstance(id) {
    document.getElementById(this.pvCtl).HideInstance(id);
}

function ShowInstance(id) {
    document.getElementById(this.pvCtl).ShowInstance(id);
}

function CaptureScreen(imageType, width, height) {
    if (!width)
        width = -1;
    if (!height)
        height = -1;
    document.getElementById(this.pvCtl).CaptureScreen(imageType, width, height);
}

function CaptureView(imageType, width, height, url, callback) {
    document.getElementById(this.pvCtl).CaptureView(imageType, width, height, url, callback);
}

function GetPropertyValue(id, group, property) {
    return document.getElementById(this.pvCtl).GetPropertyValue(id, group, property);
}

function ListPropertyGroups() {
    document.getElementById(this.pvCtl).ListPropertyGroups();
}

function LoadPropertyGroup(group) {
    document.getElementById(this.pvCtl).LoadPropertyGroup(group);
}

function FindInstancesWithProperty(group, property, value) {
    document.getElementById(this.pvCtl).FindInstancesWithProperty(group, property, value);
}

function FindInstancesWithProperties(allGroups, group, property, values) {
    document.getElementById(this.pvCtl).FindInstancesWithProperties(allGroups, group, property, values);
}

function GetStepName(stepIndex) {
    return document.getElementById(this.pvCtl).GetStepName(stepIndex);
}

function SetInstanceColor(instance, instanceColor) {
    document.getElementById(this.pvCtl).SetInstanceColor(instance, instanceColor);
}

function SetInstancesColor(instance, descendants, instanceColor) {
    document.getElementById(this.pvCtl).SetInstancesColor(instance, descendants, instanceColor);
}

function RestoreInstanceColor(instIds) {
    document.getElementById(this.pvCtl).RestoreInstanceColor(instIds);
}

function SetInstanceTransparency(componentName, transparency) {
    document.getElementById(this.pvCtl).SetInstanceTransparency(componentName, Number(transparency));
}

function SetTransparency(id, transparency, descendants) {
    document.getElementById(this.pvCtl).SetTransparency(id, Number(transparency), descendants);
}

function LoadInstance(instance) {
    document.getElementById(this.pvCtl).LoadInstance(instance);
}

function UnloadInstance(instance) {
    document.getElementById(this.pvCtl).UnloadInstance(instance);
}

function ListInstances() {
    document.getElementById(this.pvCtl).ListInstances();
}

function GetNumOfAnnotations() {
    return document.getElementById(this.pvCtl).GetNumOfAnnotations();
}

function GetNumOfViewables() {
    return document.getElementById(this.pvCtl).GetNumOfViewables();
}

function GetViewableName(theIndex) {
    return document.getElementById(this.pvCtl).GetViewableName(Number(theIndex));
}

function LoadViewable(theIndex) {
    return document.getElementById(this.pvCtl).LoadViewable(Number(theIndex));
}

function LoadAnnotation(annoName) {
    return document.getElementById(this.pvCtl).LoadAnnotation(annoName);
}

function GetAnnotationName(name) {
    return document.getElementById(this.pvCtl).GetAnnotationName(Number(name));
}

function ZoomToAllTime(zoomDelay) {
    return document.getElementById(this.pvCtl).ZoomToAllTime(Number(zoomDelay));
}

function ZoomToAll(zoomDelay) {
    if (zoomDelay == null)
        zoomDelay = 0;
    return document.getElementById(this.pvCtl).ZoomToAll(Number(zoomDelay));
}

function ZoomToSelected(zoomDelay) {
    if (zoomDelay == null)
        zoomDelay = 0;
    return document.getElementById(this.pvCtl).ZoomToSelected(Number(zoomDelay));
}

function ZoomToSelectedTime(zoomDelay) {
    return document.getElementById(this.pvCtl).ZoomToSelectedTime(Number(zoomDelay));
}

function SelectAll() {
    return document.getElementById(this.pvCtl).SelectAll();
}

function DeSelectAll() {
    return document.getElementById(this.pvCtl).DeSelectAll();
}

function AddAnnotation(annotype) {
    document.getElementById(this.pvCtl).AddAnnotation(annotype, "");
}

function AddAnnotationNote(annovalue, fontSize, fontColor, bgColor) {
    document.getElementById(this.pvCtl).AddAnnotationLabel(annovalue, Number(fontSize), fontColor, bgColor);
}

function DeleteAnnotation(annoName) {
    document.getElementById(this.pvCtl).DeleteAnnotation(annoName);
}

function DeleteSelectedAnnotations() {
    document.getElementById(this.pvCtl).DeleteSelectedAnnotations();
}

function CreateAnnotation(annoName, author, telephone, email, comment) {
    document.getElementById(this.pvCtl).CreateAnnotation(annoName, author, telephone, email, comment);
}

function RenameAnnotation(currentName, newName) {
    document.getElementById(this.pvCtl).RenameAnnotation(currentName, newName);
}

function SaveAnnotation(annoName) {
    document.getElementById(this.pvCtl).SaveAnnotation(annoName);
}

function SelectInstance(instance) {
    document.getElementById(this.pvCtl).SelectInstance(instance);
}

function DeSelectInstance(instance) {
    document.getElementById(this.pvCtl).DeSelectInstance(instance);
}

function Select(selXml, descendants) {
    document.getElementById(this.pvCtl).Select(selXml, descendants);
}

function DeSelect(selXml, descendants) {
    document.getElementById(this.pvCtl).DeSelect(selXml, descendants);
}

function ShowAll() {
    document.getElementById(this.pvCtl).ShowAll();
}

function IsolateSelected(instance) {
    document.getElementById(this.pvCtl).IsolateSelected(instance);
}

function IsolateInstances(instances) {
    document.getElementById(this.pvCtl).IsolateInstances(instances);
}

function CopyToClipboard() {
    document.getElementById(this.pvCtl).CopyToClipboard();
}

function GetInstanceLocation(instance) {
    return document.getElementById(this.pvCtl).GetInstanceLocation(instance);
}
function GetInstanceName(instance) {
    return document.getElementById(this.pvCtl).GetInstanceName(instance);
}

function GetOrthographicWidth() {
    return document.getElementById(this.pvCtl).GetOrthographicWidth();
}

function GetPerspectiveHFOV() {
    return document.getElementById(this.pvCtl).GetPerspectiveHFOV();
}

function SetOrthographicWidth(orthoWidth) {
    document.getElementById(this.pvCtl).SetOrthographicWidth(Number(orthoWidth));
}

function SetPerspectiveHFOV(hfov) {
    document.getElementById(this.pvCtl).SetPerspectiveHFOV(Number(hfov));
}

function GetViewLocation() {
    return document.getElementById(this.pvCtl).GetViewLocation();
}

function SetViewLocation(location) {
    document.getElementById(this.pvCtl).SetViewLocation(location);
}

function SetSpinCenter(point) {
    document.getElementById(this.pvCtl).SetSpinCenter(Number(point.x), Number(point.y), Number(point.z));
}

function GetSpinCenter() {
    var spinCenter = document.getElementById(this.pvCtl).GetSpinCenter();

    var index1 = spinCenter.indexOf(" ");
    var index2 = spinCenter.indexOf(" ", index1 + 1);

    var point3d = new POINT3D;
    if (index1 != -1 && index2 != -1) {
        point3d.x = spinCenter.substr(0, index1);
        point3d.y = spinCenter.substr(index1, index2 - index1);
        point3d.z = spinCenter.substr(index2, spinCenter.length - index2);
    } else {
        return null;
    }
    return point3d;
}

function SetNavMethod(navMethod) {
    document.getElementById(this.pvCtl).SetNavMethod(navMethod);
}

function MoveUp(step) {
    document.getElementById(this.pvCtl).MoveUp(Number(step));
}

function MoveDown(step) {
    document.getElementById(this.pvCtl).MoveDown(Number(step));
}

function MoveLeft(step) {
    document.getElementById(this.pvCtl).MoveLeft(Number(step));
}

function MoveRight(step) {
    document.getElementById(this.pvCtl).MoveRight(Number(step));
}

function MoveForward(step) {
    document.getElementById(this.pvCtl).MoveForward(Number(step));
}

function MoveBackward(step) {
    document.getElementById(this.pvCtl).MoveBackward(Number(step));
}

function RotateUp(step) {
    document.getElementById(this.pvCtl).RotateUp(Number(step));
}

function RotateDown(step) {
    document.getElementById(this.pvCtl).RotateDown(Number(step));
}

function RotateLeft(step) {
    document.getElementById(this.pvCtl).RotateLeft(Number(step));
}

function RotateRight(step) {
    document.getElementById(this.pvCtl).RotateRight(Number(step));
}

function RotateClockwise(step) {
    document.getElementById(this.pvCtl).RotateClockwise(Number(step));
}

function RotateCounterClockwise(step) {
    document.getElementById(this.pvCtl).RotateCounterClockwise(Number(step));
}

function SetRenderMode(renderMode) {
    document.getElementById(this.pvCtl).SetRenderMode(renderMode);
}

function GetNumberOfSheets() {
    return document.getElementById(this.pvCtl).GetNumberOfSheets();
}

function GetCurrentSheet() {
    return document.getElementById(this.pvCtl).GetCurrentSheet();
}

function SetCurrentSheet(sheetNumber) {
    document.getElementById(this.pvCtl).SetCurrentSheet(Number(sheetNumber));
}

function GetOrientations() {
    return document.getElementById(this.pvCtl).GetOrientations();
}

function ListViewStates() {
    return document.getElementById(this.pvCtl).ListViewStates();
}

function CancelPendingDownloads() {
    document.getElementById(this.pvCtl).CancelPendingDownloads();
}

function CreateBoundingBox(xMin, yMin, zMin, xMax, yMax, zMax, color, transparency, draggable) {
    return document.getElementById(this.pvCtl).CreateBoundingBox(Number(xMin), Number(yMin), Number(zMin), Number(xMax), Number(yMax), Number(zMax), color, Number(transparency), draggable);
}

function UpdateBoundingBox(id, xMin, yMin, zMin, xMax, yMax, zMax, color, transparency) {
    document.getElementById(this.pvCtl).UpdateBoundingBox(Number(id), Number(xMin), Number(yMin), Number(zMin), Number(xMax), Number(yMax), Number(zMax), color, Number(transparency));
}

function DeleteBoundingBox(id) {
    document.getElementById(this.pvCtl).DeleteBoundingBox(Number(id));
}

function CalculateBoundingBox(instanceIds) {
    return document.getElementById(this.pvCtl).CalculateBoundingBox(instanceIds);
}

function CreateSphere(x, y, z, radius, color, transparency, draggable) {
    return document.getElementById(this.pvCtl).CreateSphere(Number(x), Number(y), Number(z), Number(radius), color, Number(transparency), draggable);
}

function UpdateSphere(id, x, y, z, radius, color, transparency) {
    return document.getElementById(this.pvCtl).UpdateSphere(Number(id), Number(x), Number(y), Number(z), Number(radius), color, Number(transparency));
}

function DeleteSphere(id) {
    document.getElementById(this.pvCtl).DeleteSphere(Number(id));
}

function CalculateBoundingSphere(instanceIds) {
    return document.getElementById(this.pvCtl).CalculateBoundingSphere(instanceIds);
}

function RegisterAnnotationEvents(annotationEventObj) {
    document.getElementById(this.pvCtl).RegisterAnnotationEvents(annotationEventObj);
}

function HideInstanceAndDescendants(instanceIds) {
    document.getElementById(this.pvCtl).HideInstanceAndDescendants(instanceIds);
}

function ShowInstanceAndDescendants(instanceIds) {
    document.getElementById(this.pvCtl).ShowInstanceAndDescendants(instanceIds);
}

function SetViewState(name, type) {
    return document.getElementById(this.pvCtl).SetViewState(name, type);
}

function SetShapeMarkupColor(instanceId, markupId, color) {
    document.getElementById(this.pvCtl).SetShapeMarkupColor(instanceId, markupId, color);
}

function SetInstanceLocation(instance, loc) {
    document.getElementById(this.pvCtl).SetInstanceLocation(instance, loc);
}

function SetInstancesLocation(ids, locations) {
    document.getElementById(this.pvCtl).SetInstancesLocation(ids, locations);
}

function ResetInstanceLocation(instance) {
    document.getElementById(this.pvCtl).ResetInstanceLocation(instance);
}

function RestoreAllLocations() {
    document.getElementById(this.pvCtl).RestoreAllLocations();
}

function SetMode(mode) {
    document.getElementById(this.pvCtl).SetMode(mode);
}

function StartAnimation(fromBeginning) {
    document.getElementById(this.pvCtl).StartAnimation(fromBeginning);
}

function StopAnimation() {
    document.getElementById(this.pvCtl).StopAnimation();
}

function SetAnimationOffset(offset) {
    document.getElementById(this.pvCtl).SetAnimationOffset(offset);
}

function SetAnimationLoop(loop) {
    document.getElementById(this.pvCtl).SetAnimationLoop(loop);
}

function SetAnimationPlaybackSpeed(times) {
    document.getElementById(this.pvCtl).SetAnimationPlaybackSpeed(times);
}

function GetLoadProgress() {
    document.getElementById(this.pvCtl).GetLoadProgress();
}

function InsertBranchLink(node, name, src, mapSrc, wvsInfo, autoExpand, loadGeom) {
    document.getElementById(this.pvCtl).InsertBranchLink(node, name, src, mapSrc, wvsInfo, autoExpand, loadGeom);
}

function GetNumberOfItems() {
    return document.getElementById(this.pvCtl).GetNumberOfItems();
}

function GetItemNumber(index) {
    return document.getElementById(this.pvCtl).GetItemNumber(index);
}

function GetItemNameTag(index) {
    return document.getElementById(this.pvCtl).GetItemNameTag(index);
}

function GetItemQty(index) {
    return document.getElementById(this.pvCtl).GetItemQty(index);
}

function GetItemFromCalloutId(calloutId) {
    return document.getElementById(this.pvCtl).GetItemFromCalloutId(calloutId);
}

function GetItemsFromCalloutId(calloutId) {
    return document.getElementById(this.pvCtl).GetItemsFromCalloutId(calloutId);
}

function HasAnimation() {
    return document.getElementById(this.pvCtl).HasAnimation();
}

function GetItemFromInstance(id) {
    return document.getElementById(this.pvCtl).GetItemFromInstance(id);
}

function SelectItemsListItem(index) {
    document.getElementById(this.pvCtl).SelectItemsListItem(index);
}

function SelectCallout(index) {
    document.getElementById(this.pvCtl).SelectCallout(index);
}

function DeleteAnnotations(type) {
    document.getElementById(this.pvCtl).DeleteAnnotations(type);
}

function ZoomToInstance(ids) {
    document.getElementById(this.pvCtl).ZoomToInstance(ids);
}

function GetNumSequenceSteps() {
    return document.getElementById(this.pvCtl).GetNumSequenceSteps();
}

function PlaySequence(includeCam, startIndex, endIndex) {
    document.getElementById(this.pvCtl).PlaySequence(includeCam, startIndex, endIndex);
}

function PlayStep(includeCam, stepIndex) {
    document.getElementById(this.pvCtl).PlayStep(includeCam, stepIndex);
}

function PauseSequence() {
    document.getElementById(this.pvCtl).PauseSequence();
}

function ResetSequence(includeCam) {
    document.getElementById(this.pvCtl).ResetSequence(includeCam);
}

function ShowNotesOnScreen(show, titleOnly) {
    document.getElementById(this.pvCtl).ShowNotesOnScreen(show, titleOnly);
}

function GetCurrentStepIndex() {
    return document.getElementById(this.pvCtl).GetCurrentStepIndex();
}

function GetStepResourceCount(stepIndex) {
    return document.getElementById(this.pvCtl).GetStepResourceCount(stepIndex);
}

function GetStepResource(stepIndex, resIndex) {
    return document.getElementById(this.pvCtl).GetStepResource(stepIndex, resIndex);
}

function AcknowledgeStep(step, option) {
    return document.getElementById(this.pvCtl).AcknowledgeStep(step, option);
}

function SetSequenceStep(stepIndex) {
    return document.getElementById(this.pvCtl).SetSequenceStep(stepIndex);
}

function SetSequenceStepPause(milliseconds) {
    return document.getElementById(this.pvCtl).SetSequenceStepPause(milliseconds);
}

function InsertComps(srcs, names, filePaths, mapSrcs, loadBranchLink, autoLoad) {
    return document.getElementById(this.pvCtl).InsertComps(srcs, names, filePaths, mapSrcs, loadBranchLink, autoLoad);
}

function MoveComps(ids, parent, newIds) {
    return document.getElementById(this.pvCtl).MoveComps(ids, parent, newIds);
}

function RemoveComps(ids) {
    return document.getElementById(this.pvCtl).RemoveComps(ids);
}

function GetProperty(xml) {
    return document.getElementById(this.pvCtl).GetProperty(xml);
}

function CreateComponent(name, type, shapeSource, boundingBox) {
    return document.getElementById(this.pvCtl).CreateComponent(name, type, shapeSource, boundingBox);
}

function SetPropertyValue(ids, names, groups, values) {
    document.getElementById(this.pvCtl).SetPropertyValue(ids, names, groups, values);
}

function AddComponentNode(idRef, id, newId, name) {
    document.getElementById(this.pvCtl).AddComponentNode(idRef, id, newId, name);
}

function IsolateInstances(ids) {
    document.getElementById(this.pvCtl).IsolateInstances(ids);
}

function GetProperties(ids, name, group) {
    document.getElementById(this.pvCtl).GetProperties(ids, name, group);
}

function SetTransparencies(ids, transparency, descendants) {
    document.getElementById(this.pvCtl).SetTransparencies(ids, transparency, descendants);
}

function HideInstancesAndDescendants(ids) {
    document.getElementById(this.pvCtl).HideInstancesAndDescendants(ids);
}

function ShowInstancesAndDescendants(ids) {
    document.getElementById(this.pvCtl).ShowInstancesAndDescendants(ids);
}

function SelectInstances(ids) {
    document.getElementById(this.pvCtl).SelectInstances(ids);
}

function DeSelectInstances(ids) {
    document.getElementById(this.pvCtl).DeSelectInstances(ids);
}

function ShowMsgDialog(title, message, msgType) {
    document.getElementById(this.pvCtl).ShowMsgDialog(title, message, msgType);
}

function PvLiteApi(pvId) {
    // Public java script calls into PV
    this.LoadModel = LoadModel;

    this.GetNumOfAnnotations = GetNumOfAnnotations;
    this.LoadAnnotation = LoadAnnotation;
    this.GetAnnotationName = GetAnnotationName;
    this.AddAnnotation = AddAnnotation;
    this.AddAnnotationNote = AddAnnotationNote;
    this.DeleteAnnotation = DeleteAnnotation;
    this.CreateAnnotation = CreateAnnotation;
    this.RenameAnnotation = RenameAnnotation;
    this.SaveAnnotation = SaveAnnotation;
    this.DeleteSelectedAnnotations = DeleteSelectedAnnotations;

    this.ZoomToAll = ZoomToAll;
    this.ZoomToSelected = ZoomToSelected;
    this.SelectAll = SelectAll;
    this.DeSelectAll = DeSelectAll;
    this.SetBackgroundColor = SetBackgroundColor;
    this.SetViewingMode = SetViewingMode;
    this.IsolateSelected = IsolateSelected;
    this.IsolateInstances = IsolateInstances;
    this.ShowAll = ShowAll;
    this.CopyToClipboard = CopyToClipboard;

    this.GetNumOfViewables = GetNumOfViewables;
    this.GetViewableName = GetViewableName;
    this.LoadViewable = LoadViewable;
    this.SetMode = SetMode;

    this.ListInstances = ListInstances;
    this.SelectInstance = SelectInstance;
    this.DeSelectInstance = DeSelectInstance;
    this.Select = Select;
    this.DeSelect = DeSelect;
    this.HideInstance = HideInstance;
    this.ShowInstance = ShowInstance;
    this.GetInstanceLocation = GetInstanceLocation;
    this.SetInstanceLocation = SetInstanceLocation;
    this.SetInstancesLocation = SetInstancesLocation;
    this.ResetInstanceLocation = ResetInstanceLocation;
    this.RestoreAllLocations = RestoreAllLocations;
    this.SetInstanceColor = SetInstanceColor;
    this.SetInstancesColor = SetInstancesColor;
    this.RestoreInstanceColor = RestoreInstanceColor;
    this.SetInstanceTransparency = SetInstanceTransparency;
    this.SetTransparency = SetTransparency;
    this.LoadInstance = LoadInstance;
    this.UnloadInstance = UnloadInstance;
    this.CaptureScreen = CaptureScreen;
    this.CaptureView = CaptureView;
    this.GetPropertyValue = GetPropertyValue;
    this.ListPropertyGroups = ListPropertyGroups;
    this.LoadPropertyGroup = LoadPropertyGroup;
    this.FindInstancesWithProperty = FindInstancesWithProperty;
    this.FindInstancesWithProperties = FindInstancesWithProperties;
    this.GetInstanceName = GetInstanceName;
    this.GetOrthographicWidth = GetOrthographicWidth;
    this.GetPerspectiveHFOV = GetPerspectiveHFOV;
    this.SetOrthographicWidth = SetOrthographicWidth;
    this.SetPerspectiveHFOV = SetPerspectiveHFOV;
    this.GetViewLocation = GetViewLocation;
    this.SetViewLocation = SetViewLocation;
    this.SetSpinCenter = SetSpinCenter;
    this.GetSpinCenter = GetSpinCenter;
    this.SetNavMethod = SetNavMethod;
    this.MoveUp = MoveUp;
    this.MoveDown = MoveDown;
    this.MoveLeft = MoveLeft;
    this.MoveRight = MoveRight;
    this.MoveForward = MoveForward;
    this.MoveBackward = MoveBackward;

    this.RotateUp = RotateUp;
    this.RotateDown = RotateDown;
    this.RotateLeft = RotateLeft;
    this.RotateRight = RotateRight;
    this.RotateClockwise = RotateClockwise;
    this.RotateCounterClockwise = RotateCounterClockwise;
    this.SetRenderMode = SetRenderMode;
    this.GetNumberOfSheets = GetNumberOfSheets;
    this.GetCurrentSheet = GetCurrentSheet;
    this.SetCurrentSheet = SetCurrentSheet;
    this.SetShapeMarkupColor = SetShapeMarkupColor;
    this.GetOrientations = GetOrientations;
    this.ListViewStates = ListViewStates;
    this.SetViewState = SetViewState;
    this.CancelPendingDownloads = CancelPendingDownloads;
    this.CreateBoundingBox = CreateBoundingBox;
    this.UpdateBoundingBox = UpdateBoundingBox;
    this.DeleteBoundingBox = DeleteBoundingBox;
    this.CalculateBoundingBox = CalculateBoundingBox;
    this.CreateSphere = CreateSphere;
    this.UpdateSphere = UpdateSphere;
    this.DeleteSphere = DeleteSphere;
    this.CalculateBoundingSphere = CalculateBoundingSphere;
    this.RegisterAnnotationEvents = RegisterAnnotationEvents;
    this.HideInstanceAndDescendants = HideInstanceAndDescendants;
    this.ShowInstanceAndDescendants = ShowInstanceAndDescendants;
    this.StartAnimation = StartAnimation;
    this.StopAnimation = StopAnimation;
    this.SetAnimationOffset = SetAnimationOffset;
    this.SetAnimationLoop = SetAnimationLoop;
    this.SetAnimationPlaybackSpeed = SetAnimationPlaybackSpeed;
    this.GetLoadProgress = GetLoadProgress;
    this.InsertBranchLink = InsertBranchLink;
    this.GetNumberOfItems = GetNumberOfItems;
    this.GetItemNumber = GetItemNumber;
    this.GetItemNameTag = GetItemNameTag;
    this.GetItemQty = GetItemQty;
    this.GetItemFromCalloutId = GetItemFromCalloutId;
    this.GetItemsFromCalloutId = GetItemsFromCalloutId;
    this.HasAnimation = HasAnimation;
    this.GetItemFromInstance = GetItemFromInstance;
    this.SelectItemsListItem = SelectItemsListItem;
    this.SelectCallout = SelectCallout;
    this.DeleteAnnotations = DeleteAnnotations;
    this.ZoomToInstance = ZoomToInstance;
    this.GetNumSequenceSteps = GetNumSequenceSteps;
    this.PlaySequence = PlaySequence;
    this.PlayStep = PlayStep;
    this.PauseSequence = PauseSequence;
    this.ResetSequence = ResetSequence;
    this.ShowNotesOnScreen = ShowNotesOnScreen;
    this.GetCurrentStepIndex = GetCurrentStepIndex;
    this.GetStepResourceCount = GetStepResourceCount;
    this.GetStepResource = GetStepResource;
    this.AcknowledgeStep = AcknowledgeStep;
    this.SetSequenceStep = SetSequenceStep;
    this.SetSequenceStepPause = SetSequenceStepPause;
    this.InsertComps = InsertComps;
    this.MoveComps = MoveComps;
    this.RemoveComps = RemoveComps;
    this.GetProperty = GetProperty;
    this.CreateComponent = CreateComponent;
    this.SetPropertyValue = SetPropertyValue;
    this.AddComponentNode = AddComponentNode;
    this.IsolateInstances = IsolateInstances;
    this.GetProperties = GetProperties;
    this.GetStepName = GetStepName;
    this.SetTransparencies = SetTransparencies;
    this.HideInstancesAndDescendants = HideInstancesAndDescendants;
    this.ShowInstancesAndDescendants = ShowInstancesAndDescendants;
    this.SelectInstances = SelectInstances;
    this.DeSelectInstances = DeSelectInstances;
    this.ShowMsgDialog = ShowMsgDialog;

    // Public java script callbacks from the plugin
    this.OnDoAction;
    this.OnLoadComplete;
    this.OnSetStatusText;
    this.OnCloseWindow;
    this.OnSelectInstance;
    this.OnDeSelectInstance;
    this.OnSelectAnnotation;
    this.OnSelect;
    this.OnDeSelect;
    this.OnDeSelectAll;
    this.OnBeginSelect;
    this.OnEndSelect;
    this.OnSelectItem;
    this.OnSaveScreenShot;
    this.OnBeginGroupProperties;
    this.OnPropertyGroup;
    this.OnEndGroupProperties;
    this.OnPropertyGroupLoaded;
    this.OnBeginFindInstance;
    this.OnFindInstance;
    this.OnFindInstances;
    this.OnEndFindInstance;
    this.OnAnnotationEvent;
    this.OnBeginInstance;
    this.OnInstance;
    this.OnEndInstance;
    this.OnPreSelectedInstance;
    this.OnClearPreSelection;
    this.OnBeginViewState;
    this.OnEndViewState;
    this.OnAddViewState;
    this.OnRMB;
    this.OnLoadProgress;
    this.OnViewableLoaded;
    this.OnStepEnter;
    this.OnStepExit;
    this.OnStepAcknowledgment;
    this.OnMouseHover;
    this.OnAnimationProgress;
    this.OnAnimationStateChanged;
    this.OnPropertyValues;
    this.OnAnnotationsLoaded;

    // Private attributes
    this.pvId = pvId;
    this.pvVerCtl = "pvVerCtl" + pvId;
    this.pvCtl = "pvctl" + pvId;
    this.runpview = "runpview" + pvId;
    this.upgradepview = "upgradepview" + pvId;
    this.checkpview = "checkpview" + pvId;
    this.params;
}

function isLocal(theUrl) {
    if (theUrl.indexOf("http") == 0) {
        return false;
    } else if (theUrl.indexOf("__pvbnfs") == 0) {
        return false;
    } else if (theUrl.indexOf("file") == 0) {
        return true;
    } else if (theUrl.indexOf(":") == 1) {
        return true;
    } else {
        return false;
    }
}

function fixPath(s) {
    return s.replace(/\\/g, "/");
}

function checkFileURL(theUrl) {
    var loc = theUrl.indexOf("file:///", 0);
    if (loc == -1) {
        loc = theUrl.indexOf("file://", 0);
        if (loc == -1) {
            loc = theUrl.indexOf(":", 0);
            if (loc == 1) {
                theNewUrl = "file:///";
                theNewUrl += theUrl;
                return theNewUrl;
            }
        } else {
            theNewUrl = "file:///";
            theNewUrl += theUrl.substring(7, theUrl.length);
            return theNewUrl;
        }
    }
    return theUrl;
}

function addLoadEvent(func) {
    var oldonload = window.onload;
    if (typeof window.onload != 'function') {
        window.onload = func;
    } else {
        window.onload = function() {
            if (oldonload) {
                oldonload();
            }
            func();
        }
    }
}

function ProductView(edition, sourceUrl, markupUrl, modifymarkupurl, loadAnnotation, loadViewable, uiconfig, configOptions) {
    var cookies = document.cookie;
    var pvVerChecked = cookies.indexOf("pvlite_version_checked");

    if (g_pvlaunchInitialised == false) {
        g_pvlaunchInitialised = true;
        SetupPView();
    }

    pvApi = new PvLiteApi("pvctl" + g_pvliteInstance);

    var pvParams;
    if (edition) {
        pvParams += " edition='" + edition + "' ";
    } else {
        pvApi.thumbnail = true;
        pvParams = " thumbnailView='true' ";
    }

    if (sourceUrl) {
        pvParams += "edurl='";
        if (isRelativeUrl(sourceUrl)) {
            pvParams += s_downloadDir;
        }
        pvParams += sourceUrl + "'";
        pvParams += " renderatstartup='true'";
    }
    if (uiconfig) {
        pvParams += " uiconfigurl='";
        if (isRelativeUrl(uiconfig))
            pvParams += s_downloadDir;
        pvParams += uiconfig + "'";
    }

    if (configOptions)
        configOptions += " lmbClick=\"false\"";
    else
        configOptions = "lmbClick=\"false\"";

    pvParams += " hosttype='webserver'";
    if (modifymarkupurl) { pvParams += " modifymarkupurl='" + modifymarkupurl + "'"; }
    if (loadAnnotation) { pvParams += " renderannotation='" + loadAnnotation + "'"; }
    if (loadViewable) { pvParams += " renderviewable='" + loadViewable + "'"; }
    if (configOptions) { pvParams += " configoptions='" + configOptions + "'"; }
    if (g_username) { pvParams += " username='" + g_username + "'"; }
    if (g_useremail) { pvParams += " useremail='" + g_useremail + "'"; }
    if (g_usertelno) { pvParams += " usertelno='" + g_usertelno + "'"; }
    if (g_heading) { pvParams += " heading='" + g_heading + "'"; }
    if (g_configUrl) { pvParams += " configurl='" + g_configUrl + "'"; }

    var baseUrl;
    var newSourceUrl = fixPath(sourceUrl);

    if (isRelativeUrl(newSourceUrl)) {
        var newBaseUrl = fixPath(document.URL);

        loc = newBaseUrl.lastIndexOf("/");
        if (loc != -1)
            baseUrl = newBaseUrl.substring(0, loc);

        loc = newSourceUrl.lastIndexOf('/');
        if (loc != -1) {
            baseUrl += "/";
            baseUrl += newSourceUrl.substring(0, loc + 1);
        }
    } else {
        loc = newSourceUrl.lastIndexOf('/');
        if (loc != -1)
            baseUrl = newSourceUrl.substring(0, loc + 1);
    }

    baseUrl = checkFileURL(baseUrl);
    pvParams += " urlbase='" + baseUrl + "'";

    pvApi.params = pvParams;
    g_pvliteInstanceArray[g_pvliteInstance] = pvApi;

    addLoadEvent(docLoaded);
    IECallbackEvents(pvApi);
    document.write('<div style="position:relative;top:0;left:0;border-width:0px;border-style:none;width:100%;height:100%">');
    // Upgrade div
    if (g_consumerInstall == false) {
        document.write('<div align=center id=' + pvApi.upgradepview + ' style="position:relative;top:0;left:0;border-width:0px;border-style:none">');
        document.write('</div>');
        if (s_docPluginVersion && pvVerChecked == "-1") {
            document.write('<div id=' + pvApi.checkpview + ' style="visibility:hidden;position:absolute;top:2;left:2;zIndex=-1;width:0;height:0;border-style:none">');
            document.write(GetPvCheckHtml(pvApi));
            document.write('</div>');
            g_docLoadedAction = 1; // Test client
        }
    }
    // PTC Creo View plugin div
    document.write('<div id=' + pvApi.runpview + ' style="visibility:hidden;position:absolute;top:0;left:0;width:100%;height:100%;zIndex=-1;border-width:0px;border-style:none">');
    document.write('</div>');
    document.write('</div>');

    g_pvliteInstance += 1;

    return pvApi;
}

function SetUserDetails(username, telephone, email) {
    g_username = username;
    g_useremail = email;
    g_usertelno = telephone;
}

function SetHeading(heading) {
    g_heading = heading;
}

function docLoaded() {
    if (g_installingVerCheck)
        return; // Only allow one pvvercheck installer to run.

    if (g_docLoadedAction == 0) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            StartPview(g_pvliteInstanceArray[i]);
        }
        return;
    }

    if (g_docLoadedAction == 1) {
        try {
            for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
                var myObj = g_pvliteInstanceArray[i];

                var is_chrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;

                if (is_chrome)
                    StartPview(myObj);
                else if (document.getElementById(myObj.pvVerCtl).ReadyState != 4) {
                    StartPview(myObj);
                } else {
                    var isInstalled = document.getElementById(myObj.pvVerCtl).CheckPview(s_docPluginVersion);
                    var vi = document.getElementById(myObj.pvVerCtl).GetInstalledVersion();
                    if (isInstalled == 0) { // Pview Not installed so need to install it
                        if (typeof _pvliteString_Install != "undefined")
                            _local_Install = _pvliteString_Install;
                        document.getElementById(myObj.upgradepview).innerHTML += '<A class=wizardlabel HREF="javascript:void(DoInstall())"  TITLE="' + s_docPluginVersion + '">' + _local_Install + '</A>';
                    } else if (isInstalled == 1) { //pview is installed and up to date so just run
                        StartPview(myObj);
                    } else if (isInstalled == 2) { //pview is installed But can be upgraded
                        if (typeof _pvliteString_UpgradeContinue != "undefined")
                            _local_UpgradeContinue = _pvliteString_UpgradeContinue;
                        if (typeof _pvliteString_UpgradeTitle != "undefined")
                            _local_UpgradeTitle = _pvliteString_UpgradeTitle;
                        if (typeof _pvliteString_UpgradeInstall != "undefined")
                            _local_UpgradeInstall = _pvliteString_UpgradeInstall;
                        document.getElementById(myObj.upgradepview).innerHTML += '<A class=wizardlabel HREF="javascript:void(DoInstall())"  TITLE="' + s_docPluginVersion + '">' + _local_UpgradeInstall + '</A><BR><A class=wizardlabel HREF="javascript:void(DoNotInstall())" TITLE="' + vi + '">' + _local_UpgradeContinue + '</A><BR>';
                    }
                }
            }
        } catch (e) {
            alert("Caught an exception here: " + e);
        }
    }
}

function OnInstalledFinished(name, result) {
    if (result != 0)
        alert("Download failed");
}

function StartPview(myApi) {
    try {
        document.getElementById(myApi.runpview).innerHTML = GetPviewHtml(myApi);
        document.getElementById(myApi.runpview).style.visibility = 'visible';
    } catch (e) {
        alert("caught exception: " + e);
        return;
    }

    try {
        document.getElementById(myApi.pvCtl).IsRunning();
    } catch (e) {
        document.getElementById(myApi.runpview).style.visibility = 'hidden';
        if (g_consumerInstall == false) {
            document.getElementById(myApi.upgradepview).style.visibility = 'visible';
            document.getElementById(myApi.upgradepview).innerHTML = "";
        }
        return;
    }

    if ( (_isFirefox || _isChrome) && _visNavEvents != null) {
        eval(_visNavEvents);
    } else if (_isFirefox || _isChrome) {
        var cb = new NS6callback();
        cb.pvObj = myApi;
        try {
            var retVal = document.getElementById(myApi.pvCtl).SetNSCallback(cb);
        } catch (e) {
            window.alert("exception thrown doing SetNSCallback");
        }
    }
}

function ConvertNSParamsToIE(nsParams) {
    var params = "";

    if (!nsParams)
        return params;

    var startloc = 0;
    while (true) {
        var loc = nsParams.indexOf("=", startloc);
        if (loc == -1)
            break;
        params += "<param name='";
        var nameStartLoc = nsParams.lastIndexOf(' ', loc);
        params += nsParams.substring(nameStartLoc + 1, loc);

        params += "' value=";
        var loc1 = nsParams.indexOf("'", startloc);
        var loc2 = nsParams.indexOf("'", loc1 + 1);
        params += nsParams.substring(loc1, loc2 + 1);
        params += ">";
        startloc = loc2 + 1;
    }
    return params;
}

function GetPviewHtml(pvApi) {
    var htmlString = "";
    if (_isIE) {
        var ieParams = ConvertNSParamsToIE(pvApi.params);

        if (_is64bitIE) {
            htmlString += '<object classid="CLSID:F1BFCEEA-892D-405c-945F-19F87338A17F" id=' + pvApi.pvCtl;
            if (g_consumerInstall == true) {
                htmlString += ' codebase=pview/consumer/consumer_64.cab#Version=10.2.20.23';
            }
        } else {
            htmlString += '<object classid="CLSID:F07443A6-02CF-4215-9413-55EE10D509CC" id=' + pvApi.pvCtl;
            if (g_consumerInstall == true) {
                htmlString += ' codebase=pview/consumer/consumer.cab#Version=10.2.20.23';
            }
        }

        if (s_docType)
            htmlString += ' type="' + s_docType + '"';
        htmlString += ' width=100%';
        htmlString += ' height=100%';
        htmlString += '>\n';
        htmlString += ieParams;
        htmlString += '</object>\n';
    } else {
        htmlString += '<embed name=' + pvApi.pvCtl;
        htmlString += ' type="application/x-pvlite9-ed" ';
        htmlString += ' id=' + pvApi.pvCtl;
        htmlString += ' width=100%';
        htmlString += ' height=100% ';
        htmlString += pvApi.params;
        htmlString += '>\n';
    }
    return htmlString;
}

function CompareVersion(downloadVersion, installedVersion) {
    var loc1 = 0;
    var loc2 = 0;
    for (i = 0; i < 4; i++) {
        var val1, val2;
        var locEnd = downloadVersion.indexOf('.', loc1);

        if (locEnd != -1)
            val1 = eval(downloadVersion.substring(loc1, locEnd));
        else
            val1 = eval(downloadVersion.substring(loc1));

        loc1 = locEnd + 1;
        locEnd = installedVersion.indexOf('.', loc2);

        if (locEnd != -1)
            val2 = eval(installedVersion.substring(loc2, locEnd));
        else
            val2 = eval(installedVersion.substring(loc2));

        loc2 = locEnd + 1;
        if (val1 > val2)
            return false;

        if (val1 < val2)
            return true;
    }
    return true
}

function GetPvCheckHtml(pvApi) {
    var htmlString = "";
    if (_isChrome) {
	    // do nothing for Google Chrome
	} else if (_isFirefox) {
        var usePlugin = false;
        var verCheckPlugin = navigator.plugins['Creo View Version Checker'];
        if (verCheckPlugin !== undefined) {
            var versionLoc = verCheckPlugin.description.lastIndexOf(' ');
            var instVer = verCheckPlugin.description.substring(versionLoc + 1);
            usePlugin = CompareVersion(s_docPluginVersion, instVer);
        }
        if (usePlugin) {
            htmlString += '<embed name=' + pvApi.pvVerCtl;
            htmlString += ' id=' + pvApi.pvVerCtl;

            if (g_docPluginURL)
                htmlString += ' pluginurl=' + s_downloadDir + g_docPluginURL;
            htmlString += ' pluginspage="' + s_downloadDir + g_docPluginURL + '"';
            htmlString += ' type="application/x-pvlite9-ver" ';
            htmlString += ' hidden="true"';
            htmlString += ' autostart="true"';
            htmlString += '>\n';
        } else {
            xpi = { 'XPInstall Creo View Version Checker': g_docPluginURL };
            if (!g_installingVerCheck) {
                g_installingVerCheck = true;
                InstallTrigger.install(xpi, OnInstalledFinished);
            }
        }
    } else if (_isIE) {
        htmlString += '<object classid="CLSID:AA34B0DE-D0FE-4587-8B31-0BB687A9EF0B" id=' + pvApi.pvVerCtl;
        if (s_docType)
            htmlString += ' type="' + s_docType + '"';
        if (s_downloadDir)
            htmlString += ' codebase="' + s_downloadDir + g_docPluginURL;
        if (s_docPluginVersion)
            htmlString += '#version=' + s_docPluginVersion;
        if (g_docPluginURL)
            htmlString += '"';
        htmlString += 'height=0 width=0>';
        htmlString += '</object>\n';
    } else {
        // Unsupported browser
    }
    return htmlString;
}

function DoNotInstall() {
    document.cookie = "pvlite_version_checked=true;path=/";
    url = window.location;
    window.location.href = url;
}

function installDone() {
    if (_isFirefox)
        navigator.plugins.refresh(false);
    url = window.location;
    window.location.href = url;
}

function installCancel(installMode) {
    if (installMode == "upgrade") {
        // If cancel upgrade, run current version
        document.cookie = "pvlite_version_checked=true;path=/";
    }
    url = window.location;
    window.location.href = url;
}

function DoNotCheck() {
    document.cookie = "pvlite_version_checked=true;path=/";
    for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
        StartPview(g_pvliteInstanceArray[i]);
    }
}

function DoInstall() {
    var opts = "dependent=yes,toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=0,resizable=1,width=760,height=520,pagezoom=0";
    window.open(s_basePvUrl + "download_pvl.html", "installWindow", opts);
}

function IECallbackEvents(pvApi) {
    if (_isIE) {
        if (_visNavEvents != null) {
            eval(_visNavEvents);
        } else {
            document.write("<script for='" + pvApi.pvCtl + "' event='OnDoAction()'>NSDoAction('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnLoadComplete()'>NSLoadComplete('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnSetStatusText(text)'>NSSetStatusText(text,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnCloseWindow()'>NSCloseWindow('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnSelectInstance(text)'>NSSelectInstance(text,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnDeSelectInstance(text)'>NSDeSelectInstance(text,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnSelect(text)'>NSSelect(text,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnDeSelect(text)'>NSDeSelect(text,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnDeSelectAll()'>NSDeSelectAll('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnBeginSelect()'>NSBeginSelect('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnEndSelect()'>NSEndSelect('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnSelectItem(text)'>NSSelectItem(text,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnSetFocus()'>NSSetFocus()</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnSaveScreenShot(status, filename)'>NSSaveScreenShot(status,filename,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnBeginGroupProperties()'>NSBeginGroupProperties('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnPropertyGroup(group, loaded)'>NSPropertyGroup(group, loaded,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnEndGroupProperties()'>NSEndGroupProperties('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnPropertyGroupLoaded(group, loaded)'>NSPropertyGroupLoaded(group, loaded,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnBeginFindInstance()'>NSBeginFindInstance('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnFindInstance(id)'>NSFindInstance(id,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnFindInstances(ids)'>NSFindInstances(ids,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnEndFindInstance()'>NSEndFindInstance('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnAnnotationEvent(operation, name, status)'>NSAnnotationEvent(operation, name, status,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnBeginInstance()'>NSBeginInstance('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnInstance(id, name, parent)'>NSInstance(id, name, parent,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnEndInstance()'>NSEndInstance('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnPreSelectedInstance(text)'>NSPreSelectedInstance(text,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnClearPreSelection()'>NSClearPreSelection('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnBeginViewState()'>NSBeginViewState('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnEndViewState()'>NSEndViewState('" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnAddViewState(name, type)'>NSAddViewState(name, type, '" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnBoundingBoxUpdate(id, xMin, yMin, zMin, xMax, yMax, zMax)'>NSBoundingBoxUpdate(id, xMin, yMin, zMin, xMax, yMax, zMax, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnSphereUpdate(id, x, y, z, radius)'>NSSphereUpdate(id, x, y, z, radius, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnRMB(x, y)'>NSRMB(x, y, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnLoadProgress(progress)'>NSLoadProgress(progress, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnViewableLoaded(index, loaded)'>NSViewableLoaded(index, loaded, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnStepEnter(step)'>NSStepEnter(step, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnStepExit(step)'>NSStepExit(step, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnStepAcknowledgment(step)'>NSStepAcknowledgment(step, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnSelectAnnotation(id)'>NSSelectAnnotation(id,'" + pvApi.pvId + "')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnMouseHover(x, y, xml)'>NSMouseHover(x, y, xml, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnAnimationProgress(progress)'>NSAnimationProgress(progress, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnAnimationStateChanged(state)'>NSAnimationStateChanged(state, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnPropertyValues(xmlProp)'>NSPropertyValues(xmlProp, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnAnnotationsLoaded(loaded)'>NSAnnotationsLoaded(loaded, '"+pvApi.pvId+"')</script>\n");
            document.write("<script for='" + pvApi.pvCtl + "' event='OnStructureEditComplete(loaded)'>NSStructureEditComplete(loaded, '"+pvApi.pvId+"')</script>\n");
        }
    }
}

function NSDoAction(pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnDoAction)
                    g_pvliteInstanceArray[i].OnDoAction();
            }
        }
    } else {
        if (this.pvObj.OnDoAction)
            this.pvObj.OnDoAction();
    }
}

function NSLoadComplete(pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnLoadComplete)
                    g_pvliteInstanceArray[i].OnLoadComplete();
            }
        }
    } else {
        if (this.pvObj.OnLoadComplete)
            this.pvObj.OnLoadComplete();
    }
}

function NSSetStatusText(text, pvApi) {

    var handled = false;
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnSetStatusText)
                {
                    handled = true;
                    g_pvliteInstanceArray[i].OnSetStatusText(text);
                }
            }
        }
    } else {
        if (this.pvObj.OnSetStatusText)
        {
            handled = true;
            this.pvObj.OnSetStatusText(text);
        }
    }

    if (!handled)
    {
        if (typeof decodeURIComponent == "function") {
            try {
                text = decodeURIComponent(text);
                window.defaultStatus = text;
            } catch (e) {
            }
        }
    }
}

function NSCloseWindow() {
    window.close();
}

function NSSelectInstance(text, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnSelectInstance)
                    g_pvliteInstanceArray[i].OnSelectInstance(text);
            }
        }
    } else {
        if (this.pvObj.OnSelectInstance)
            this.pvObj.OnSelectInstance(text);
    }
}

function NSSelect(text, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnSelect)
                    g_pvliteInstanceArray[i].OnSelect(text);
            }
        }
    } else {
        if (this.pvObj.OnSelect)
            this.pvObj.OnSelect(text);
    }
}

function NSSelectItem(text, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnSelectItem)
                    g_pvliteInstanceArray[i].OnSelectItem(text);
            }
        }
    } else {
        if (this.pvObj.OnSelectItem)
            this.pvObj.OnSelectItem(text);
    }
}

function NSSaveScreenShot(status, filename, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnSaveScreenShot)
                    g_pvliteInstanceArray[i].OnSaveScreenShot(status, filename);
            }
        }
    } else {
        if (this.pvObj.OnSaveScreenShot)
            this.pvObj.OnSaveScreenShot(status, filename);
    }
}

function NSDeSelectInstance(text, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnDeSelectInstance)
                    g_pvliteInstanceArray[i].OnDeSelectInstance(text);
            }
        }
    } else {
        if (this.pvObj.OnDeSelectInstance)
            this.pvObj.OnDeSelectInstance(text);
    }
}

function NSSelectAnnotation(id, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnSelectAnnotation)
                    g_pvliteInstanceArray[i].OnSelectAnnotation(id);
            }
        }
    } else {
        if (this.pvObj.OnSelectAnnotation)
            this.pvObj.OnSelectAnnotation(id);
    }
}
function NSDeSelect(text, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnDeSelect)
                    g_pvliteInstanceArray[i].OnDeSelect(text);
            }
        }
    } else {
        if (this.pvObj.OnDeSelect)
            this.pvObj.OnDeSelect(text);
    }
}

function NSDeSelectAll(pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnDeSelectAll)
                    g_pvliteInstanceArray[i].OnDeSelectAll();
            }
        }
    } else {
        if (this.pvObj.OnDeSelectAll)
            this.pvObj.OnDeSelectAll();
    }
}

function NSBeginSelect(pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnBeginSelect)
                    g_pvliteInstanceArray[i].OnBeginSelect();
            }
        }
    } else {
        if (this.pvObj.OnBeginSelect)
            this.pvObj.OnBeginSelect();
    }
}

function NSBeginGroupProperties(pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnBeginGroupProperties)
                    g_pvliteInstanceArray[i].OnBeginGroupProperties();
            }
        }
    } else {
        if (this.pvObj.OnBeginGroupProperties)
            this.pvObj.OnBeginGroupProperties();
    }
}

function NSPropertyGroup(group, loaded, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnPropertyGroup)
                    g_pvliteInstanceArray[i].OnPropertyGroup(group, loaded);
            }
        }
    } else {
        if (this.pvObj.OnPropertyGroup)
            this.pvObj.OnPropertyGroup(group, loaded);
    }
}

function NSEndGroupProperties(pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnEndGroupProperties)
                    g_pvliteInstanceArray[i].OnEndGroupProperties();
            }
        }
    } else {
        if (this.pvObj.OnEndGroupProperties)
            this.pvObj.OnEndGroupProperties();
    }
}

function NSPropertyGroupLoaded(group, loaded, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnPropertyGroupLoaded)
                    g_pvliteInstanceArray[i].OnPropertyGroupLoaded(group, loaded);
            }
        }
    } else {
        if (this.pvObj.OnPropertyGroupLoaded)
            this.pvObj.OnPropertyGroupLoaded(group, loaded);
    }
}

function NSBeginFindInstance(pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnBeginFindInstance)
                    g_pvliteInstanceArray[i].OnBeginFindInstance();
            }
        }
    } else {
        if (this.pvObj.OnBeginFindInstance)
            this.pvObj.OnBeginFindInstance();
    }
}

function NSFindInstance(id, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnFindInstance)
                    g_pvliteInstanceArray[i].OnFindInstance(id);
            }
        }
    } else {
        if (this.pvObj.OnFindInstance)
            this.pvObj.OnFindInstance(id);
    }
}

function NSFindInstances(ids, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnFindInstances)
                    g_pvliteInstanceArray[i].OnFindInstances(ids);
            }
        }
    } else {
        if (this.pvObj.OnFindInstances)
            this.pvObj.OnFindInstances(ids);
    }
}

function NSEndFindInstance(pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnEndFindInstance)
                    g_pvliteInstanceArray[i].OnEndFindInstance();
            }
        }
    } else {
        if (this.pvObj.OnEndFindInstance)
            this.pvObj.OnEndFindInstance();
    }
}

function NSAnnotationEvent(operation, name, status, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnAnnotationEvent)
                    g_pvliteInstanceArray[i].OnAnnotationEvent(operation, name, status);
            }
        }
    } else {
        if (this.pvObj.OnAnnotationEvent)
            this.pvObj.OnAnnotationEvent(operation, name, status);
    }
}

function NSBeginInstance(pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnBeginInstance)
                    g_pvliteInstanceArray[i].OnBeginInstance();
            }
        }
    } else {
        if (this.pvObj.OnBeginInstance)
            this.pvObj.OnBeginInstance();
    }
}

function NSInstance(id, name, parent, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnInstance)
                    g_pvliteInstanceArray[i].OnInstance(id, name, parent);
            }
        }
    } else {
        if (this.pvObj.OnInstance)
            this.pvObj.OnInstance(id, name, parent);
    }
}

function NSEndInstance(pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnEndInstance)
                    g_pvliteInstanceArray[i].OnEndInstance();
            }
        }
    } else {
        if (this.pvObj.OnEndInstance)
            this.pvObj.OnEndInstance();
    }
}

function NSPreSelectedInstance(text, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnPreSelectedInstance)
                    g_pvliteInstanceArray[i].OnPreSelectedInstance(text);
            }
        }
    } else {
        if (this.pvObj.OnPreSelectedInstance)
            this.pvObj.OnPreSelectedInstance(text);
    }
}

function NSClearPreSelection(pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnClearPreSelection)
                    g_pvliteInstanceArray[i].OnClearPreSelection();
            }
        }
    } else {
        if (this.pvObj.OnClearPreSelection)
            this.pvObj.OnClearPreSelection();
    }
}

function NSBeginViewState() {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnBeginViewState)
                    g_pvliteInstanceArray[i].OnBeginViewState();
            }
        }
    } else {
        if (this.pvObj.OnBeginViewState)
            this.pvObj.OnBeginViewState();
    }
}

function NSEndViewState() {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnEndViewState)
                    g_pvliteInstanceArray[i].OnEndViewState();
            }
        }
    } else {
        if (this.pvObj.OnEndViewState)
            this.pvObj.OnEndViewState();
    }
}

function NSAddViewState(name, type, pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnAddViewState)
                    g_pvliteInstanceArray[i].OnAddViewState(name, type);
            }
        }
    } else {
        if (this.pvObj.OnAddViewState)
            this.pvObj.OnAddViewState(name, type);
    }
}

function NSBoundingBoxUpdate(id, xMin, yMin, zMin, xMax, yMax, zMax, pvApi) {
    if(_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnBoundingBoxUpdate)
                    g_pvliteInstanceArray[i].OnBoundingBoxUpdate(id, xMin, yMin, zMin ,xMax, yMax, zMax);
            }
        }
    } else {
        if(this.pvObj.OnBoundingBoxUpdate)
            this.pvObj.OnBoundingBoxUpdate(id, xMin, yMin, zMin ,xMax, yMax, zMax);
    }
}

function NSSphereUpdate(id, x, y, z, radius, pvApi) {
    if(_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnSphereUpdate)
                    g_pvliteInstanceArray[i].OnSphereUpdate(id, x, y, z ,radius);
            }
        }
    } else {
        if(this.pvObj.OnSphereUpdate)
            this.pvObj.OnSphereUpdate(id, x, y, z, radius);
    }
}

function NSRMB(x, y, pvApi) {
    if(_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnRMB)
                    g_pvliteInstanceArray[i].OnRMB(x, y);
            }
        }
    } else {
        if(this.pvObj.OnRMB)
            this.pvObj.OnRMB(x, y);
    }
}

function NSLoadProgress(progress, pvApi) {
    if(_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnLoadProgress)
                    g_pvliteInstanceArray[i].OnLoadProgress(progress);
            }
        }
    } else {
        if(this.pvObj.OnLoadProgress)
            this.pvObj.OnLoadProgress(progress);
    }
}

function NSViewableLoaded(index, loaded, pvApi) {
    if(_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnViewableLoaded)
                    g_pvliteInstanceArray[i].OnViewableLoaded(index, loaded);
            }
        }
    } else {
        if(this.pvObj.OnViewableLoaded)
            this.pvObj.OnViewableLoaded(index, loaded);
    }
}

function NSStepEnter(step, pvApi) {
    if (_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnStepEnter)
                    g_pvliteInstanceArray[i].OnStepEnter(step);
            }
        }
    } else {
        if(this.pvObj.OnStepEnter)
            this.pvObj.OnStepEnter(step);
    }
}

function NSStepExit(step, pvApi) {
    if (_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnStepExit)
                    g_pvliteInstanceArray[i].OnStepExit(step);
            }
        }
    } else {
        if(this.pvObj.OnStepExit)
            this.pvObj.OnStepExit(step);
    }
}

function NSStepAcknowledgment(step, pvApi) {
    if (_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnStepAcknowledgment)
                    g_pvliteInstanceArray[i].OnStepAcknowledgment(step);
            }
        }
    } else {
        if(this.pvObj.OnStepAcknowledgment)
            this.pvObj.OnStepAcknowledgment(step);
    }
}

function NSMouseHover(x, y, xml, pvApi) {
    if (_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnMouseHover)
                    g_pvliteInstanceArray[i].OnMouseHover(x, y, xml);
            }
        }
    } else {
        if(this.pvObj.OnMouseHover)
            this.pvObj.OnMouseHover(x, y, xml);
    }
}

function NSAnimationProgress(progress, pvApi) {
    if (_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnAnimationProgress)
                    g_pvliteInstanceArray[i].OnAnimationProgress(progress);
            }
        }
    } else {
        if(this.pvObj.OnAnimationProgress)
            this.pvObj.OnAnimationProgress(progress);
    }
}

function NSAnimationStateChanged(state, pvApi) {
    if (_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnAnimationStateChanged)
                    g_pvliteInstanceArray[i].OnAnimationStateChanged(state);
            }
        }
    } else {
        if(this.pvObj.OnAnimationStateChanged)
            this.pvObj.OnAnimationStateChanged(state);
    }
}

function NSPropertyValues(xmlProp, pvApi) {
    if (_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnPropertyValues)
                    g_pvliteInstanceArray[i].OnPropertyValues(xmlProp);
            }
        }
    } else {
        if(this.pvObj.OnPropertyValues)
            this.pvObj.OnPropertyValues(xmlProp);
    }
}

function NSAnnotationsLoaded(loaded, pvApi) {
    if(_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnAnnotationsLoaded)
                    g_pvliteInstanceArray[i].OnAnnotationsLoaded(loaded);
            }
        }
    } else {
        if(this.pvObj.OnAnnotationsLoaded)
            this.pvObj.OnAnnotationsLoaded(loaded);
    }
}

function NSStructureEditComplete(loaded, pvApi) {
    if(_isIE) {
        for(i=0;i<g_pvliteInstanceArray.length;++i) {
            if(pvApi == g_pvliteInstanceArray[i].pvId) {
                if(g_pvliteInstanceArray[i].OnStructureEditComplete)
                    g_pvliteInstanceArray[i].OnStructureEditComplete(loaded);
            }
        }
    } else {
        if(this.pvObj.OnStructureEditComplete)
            this.pvObj.OnStructureEditComplete(loaded);
    }
}

function pvloaded() {
    // Notify us that the plugin has loaded
}

function NSEndSelect(pvApi) {
    if (_isIE) {
        for (i = 0; i < g_pvliteInstanceArray.length; ++i) {
            if (pvApi == g_pvliteInstanceArray[i].pvId) {
                if (g_pvliteInstanceArray[i].OnEndSelect)
                    g_pvliteInstanceArray[i].OnEndSelect();
            }
        }
    } else {
        if (this.pvObj.OnEndSelect)
            this.pvObj.OnEndSelect();
    }
}

function NSLaunchUrl(text, target) {
    // Firefox implementation, the only way to guarantee to launch a new url in a seperate window.
    if (target == "_blank" || target == "_BLANK")
        window.open(text, target, "menubar=yes,location=yes,toolbar=yes,status=yes,resizable=yes,scrollbars=yes,minimizable=yes,close=yes,titlebar=yes ");
    else
        window.open(text, target, "dialog");
}

function NSSetFocus() {
    window.focus();
}

function NS6callback() {
    this.OnDoAction = NSDoAction;
    this.OnLoadComplete = NSLoadComplete;
    this.OnSetStatusText = NSSetStatusText;
    this.OnCloseWindow = NSCloseWindow;
    this.OnSelectInstance = NSSelectInstance;
    this.OnDeSelectInstance = NSDeSelectInstance;
    this.OnSelectAnnotation = NSSelectAnnotation;
    this.OnDeSelectAll = NSDeSelectAll;
    this.OnBeginSelect = NSBeginSelect;
    this.OnEndSelect = NSEndSelect;
    this.OnSelectItem = NSSelectItem;
    this.OnSelect = NSSelect;
    this.OnDeSelect = NSDeSelect;
    this.OnSaveScreenShot = NSSaveScreenShot;
    this.OnBeginGroupProperties = NSBeginGroupProperties;
    this.OnPropertyGroup = NSPropertyGroup;
    this.OnEndGroupProperties = NSEndGroupProperties;
    this.OnPropertyGroupLoaded = NSPropertyGroupLoaded;
    this.OnBeginFindInstance = NSBeginFindInstance;
    this.OnFindInstance = NSFindInstance;
    this.OnFindInstances = NSFindInstances;
    this.OnEndFindInstance = NSEndFindInstance;
    this.OnAnnotationEvent = NSAnnotationEvent;
    this.OnBeginInstance = NSBeginInstance;
    this.OnInstance = NSInstance;
    this.OnEndInstance = NSEndInstance;
    this.OnLaunchUrl = NSLaunchUrl;
    this.OnPreSelectedInstance = NSPreSelectedInstance;
    this.OnClearPreSelection = NSClearPreSelection;
    this.OnBeginViewState = NSBeginViewState;
    this.OnEndViewState = NSEndViewState;
    this.OnAddViewState = NSAddViewState;
    this.OnBoundingBoxUpdate = NSBoundingBoxUpdate;
    this.OnSphereUpdate = NSSphereUpdate;
    this.OnRMB = NSRMB;
    this.OnLoadProgress = NSLoadProgress;
    this.OnViewableLoaded = NSViewableLoaded;
    this.OnStepEnter = NSStepEnter;
    this.OnStepExit = NSStepExit;
    this.OnStepAcknowledgment = NSStepAcknowledgment;
    this.OnMouseHover = NSMouseHover;
    this.OnAnimationProgress = NSAnimationProgress;
    this.OnAnimationStateChanged = NSAnimationStateChanged;
    this.OnPropertyValues = NSPropertyValues;
    this.OnAnnotationsLoaded = NSAnnotationsLoaded;
    this.OnStructureEditComplete = NSStructureEditComplete;
}
