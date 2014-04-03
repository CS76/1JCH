<%@page import="org.openscience.jch.utilities.GeneralUtilities"%>
<%@page import="org.openscience.jch.utilities.ChemUtilities"%>
<%@page import="java.util.Map"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="predictionData" scope="request"
	class="org.openscience.jch.servlet.ModelDataHolder"></jsp:useBean>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>1JCH</title>
<link href="css/style.css" rel="stylesheet" type="text/css">
<link href='http://fonts.googleapis.com/css?family=Gafata'
	rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Marcellus'
	rel='stylesheet' type='text/css'>
<script src="JSmol.min.js"></script>
<script src="//code.jquery.com/jquery-1.9.1.js"></script>
<script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
</head>
<body>
	<div id="mainWrapper">
		<div id="miniMenuBar">&nbsp;</div>
		<div id="header">
			<div id="globalTitle">Prediction of One Bond Coupling Constants
				- 1JCH</div>
			<div id="localTitle">
				<p>1JCH Predictor</p>
			</div>
		</div>
		<div id="dataContainer"
			style="height: 180%; width: 99%; padding: 5px;">
			<div id="subContainer1" style="height: 25%; padding-top: 10px">
				<div id="nwchemOutputParserPanel" style="height: 100%;">
					<table width="100%" height="100%" border="0">
						<tr style="background-color: #CCC; height: 10%">
							<td colspan="3" style="padding-left: 10px;"><b>NWChem
									Output Parser</b></td>
						</tr>
						<tr valign="top" height="100%">
							<td style="background-color: #DDD; width: 50%"><textarea
									name="molStructureData" rows="4" id="molStructureData"
									style="width: 99%; height: 99%; resize: none;"> <%=((predictionData.getMolecule() == null)
					? ""
					: ChemUtilities.iatomcontainerToString(predictionData
							.getMolecule()))%></textarea></td>
							<td style="background-color: #DDD; width: 25%">
								<div
									style="width: 100%; height: 40%; font-family: 'Gafata', sans-serif;">
									<div
										style="background-color: #ccc; width: 100%; font-size: 14px;">Data
										Extracted</div>
									<p style="padding-left: 5px;">
										<img src="images/tick.png" width="25" height="25">
										Mulliken Data
									</p>
									<p style="padding-left: 5px;">
										<img src="images/tick.png" width="25" height="25">
										Minimized Coordinates
									</p>
								</div>
								<div
									style="width: 100%; height: 60%; font-family: 'Gafata', sans-serif;">
									<div
										style="background-color: #ccc; width: 100%; font-size: 14px;">Tautomer
										Data</div>
									<div id="tautomerDiv" style="width: 100%; height: 90%">
										<%=((predictionData.getMolecule() == null)? "": predictionData.getSMILES())%>
									</div>
								</div>
							</td>
							<td style="height: 100%; width: 25%">
								<div id="miniJmolContainer"
									style="background-color: #654; height: 100%">
									<script type="text/javascript">
										var jmolApplet0;
										var use = "HTML5";
										var s = document.location.search;
										var mydiv = document
												.getElementById("miniJmolContainer");
										var curr_width = mydiv.style.width;
										var curr_height = mydiv.style.height;
										Jmol.debugCode = (s
												.indexOf("debugcode") >= 0);
										jmol_isReady = function(applet) {
											document.title = (applet._id + " is ready")
											Jmol._getElement(applet,
													"appletdiv").style.border = "none";
										}
										var myJmol;
										var Info = {
											width : curr_width,
											height : curr_height,
											color : "#000",
											serverURL : "http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php",
											use : "HTML5",
											readyFunction : null,
											// PubChem -- use $ for NCI
											bondWidth : 4,
											zoomScaling : 1.5,
											pinchScaling : 2.0,
											mouseDragFactor : 0.5,
											touchDragFactor : 0.15,
											multipleBondSpacing : 4,
											spinRateX : 0.2,
											spinRateY : 0.5,
											spinFPS : 20,
											spin : true,
											debug : false
										}
										jmol = Jmol.getApplet("jmolApplet0",
												Info);
									</script>
								</div>
							</td>
						</tr>
					</table>
				</div>
			</div>
			<div id="subContainer2" style="height: 25%; margin-top: 10px;">
				<div id="title"
					style="background-color: #CCC; padding-left: 10px; padding-top: 3px; height: 9%;">
					<a><b>Descriptors Calculated</b></a>
				</div>
				<div id="descriptorsTableContainer"
					style="width: 100%; height: 91%; overflow: auto; font-family: 'Gafata', sans-serif; padding-top: 5px">
					<table id="descriptorTable" width="100%"
						style="text-align: center; margin: 4px;">
						<thead>
							<tr>
								<%if (predictionData.getPropMap() != null) {
										Map<String,Map<String,String>> pmap = predictionData.getPropMap();
										out.println("<th scope=\"col\"><b>ATOM_ID</b></th>");
										for(String s: pmap.keySet()){
											for(String t: pmap.get(s).keySet()){
											out.println("<th scope=\"col\"><b>"+t+"</b></th>");
											}
											break;
										}
										out.println("</tr>");
										out.println("</thead>");
										out.println("<tbody>");
										for(String s: pmap.keySet()){
											out.println("<tr>");
											Map<String,String> smap = pmap.get(s);
											out.println("<td scope=\"col\">"+s+"H</td>");
											for(String t:smap.keySet()){
												out.println("<td scope=\"col\">"+smap.get(t)+"</td>");
											}
											out.println("</tr>");
											}
										}
									%>
							
						</tbody>
					</table>

				</div>
			</div>
			<div id="subContainer3"
				style="height: 50%; padding-top: 10px; margin-top: 10px;">
				<div id="title"
					style="background-color: #CCC; padding-left: 10px; padding-top: 3px; height: 4%;">
					<a><b>Predicted 1JCH</b></a>
				</div>
				<div id="modelTable"
					style="width: 59%; height: 80%; float: left; overflow: auto; margin-left: 4px; margin-right: 4px; font-family: 'Gafata', sans-serif;">

					<table id="descriptorTable"
						style="text-align: center; width: 99%; margin-top: 10px;">
						<thead>
							<tr>
								<%if (predictionData.getPropMap() != null) {
										Map<String,Map<String,String>> pmap = predictionData.getPropMap();
										String[] header = {"ATOM_ID","Predicted_1JCH","MODEL","EXPERIMENTAL_1JCH"};
										for(String s: header){
											out.println("<th scope=\"col\"><b>"+s+"</b></th>");
										}
										out.println("</tr>");
										out.println("</thead>");
										out.println("<tbody>");
										
										String[] val = {"H_JCH_modelPrediction"};
										for(String s: pmap.keySet()){
											out.println("<tr>");
											Map<String,String> smap = pmap.get(s);
											out.println("<td scope=\"col\">"+s+"H</td>");
											for(String d:val){
												out.println("<td scope=\"col\">"+smap.get(d)+"</td>");
											}
											out.println("<td scope=\"col\">MODEL</td>");
											out.println("<td scope=\"col\">EXPP</td>");
											out.println("</tr>");
											}
										}
									%>
							
						</tbody>
					</table>
				</div>
				<div id="mainJsmolContainer"
					style="background-color: #000; height: 80%; width: 40%; float: left;">
					<script type="text/javascript">
						var jmolApplet1;
						var use = "HTML5";
						var s = document.location.search;
						var mydiv = document
								.getElementById("mainJsmolContainer");
						var curr_width = mydiv.style.width * 0.6;
						var curr_height = window.innerHeight * 0.72;
						Jmol.debugCode = (s.indexOf("debugcode") >= 0);
						jmol_isReady = function(applet) {
							document.title = (applet._id + " is ready")
							Jmol._getElement(applet, "appletdiv").style.border = "none";
						}
						var myJmol;
						var Info = {
							width : curr_width,
							height : curr_height,
							color : "#000",
							serverURL : "http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php",
							use : "HTML5",
							readyFunction : null,
							// PubChem -- use $ for NCI
							bondWidth : 4,
							zoomScaling : 1.5,
							pinchScaling : 2.0,
							mouseDragFactor : 0.5,
							touchDragFactor : 0.15,
							multipleBondSpacing : 4,
							spinRateX : 0.2,
							spinRateY : 0.5,
							spinFPS : 20,
							spin : true,
							debug : false
						}
						jmol = Jmol.getApplet("jmolApplet1", Info);
					</script>
				</div>
			</div>
		</div>
		<div id="jsmolScriptContainer" style="display: none">
			<% if (predictionData.getPropMap() != null) {
										Map<String,Map<String,String>> pmap = predictionData.getPropMap();
										
										for(String s: pmap.keySet()){
											double b = Double.valueOf(pmap.get(s).get("H_JCH_modelPrediction"));
											out.print("select @"+s+";label "+GeneralUtilities.format_Double(b)+';');
										}
								//		'select *;label %a;';
								}
		%>
		</div>
		<div id="footer"></div>
	</div>
	<script type="text/javascript">
		function readSingleFile(file) {
			var f = file
			if (f) {
				var r = new FileReader();
				r.onload = function(e) {
					var contents = e.target.result;
					document.getElementById("outputDisplay").value = contents;
				}
				r.readAsText(f);
			} else {
				alert("Failed to load file");
			}
		}

		function changeOpDisplay(evt) {
			if (document.getElementById('fileUpload').files.length == 0) {
				alert("Select a file to display its content");
			} else {
				if (document.getElementById('outputDisplayContainer').style.display === 'none') {
					document.getElementById('outputDisplayContainer').style.display = 'inline';
					document.getElementById('toggleDisplay').innerHTML = "Hide file content";
					readSingleFile(document.getElementById('fileUpload').files[0]);
				} else {
					document.getElementById('outputDisplayContainer').style.display = 'none';
					document.getElementById('toggleDisplay').innerHTML = "Show file content";
				}

			}
		}

		function validate() {
			valid = true;

			if (document.getElementById("fileUpload").value == '') {
				// your validation error action
				valid = false;
				alert("Please select a NWChem Output file to process");
			}
			return valid //true or false
		}

		var readyStateCheckInterval = setInterval(
				function() {
					if (document.readyState === "complete") {
						loadMolecule(document
								.getElementById("molStructureData").value,
								jmolApplet0);
						loadMolecule(document
								.getElementById("molStructureData").value,
								jmolApplet1);
						clearInterval(readyStateCheckInterval);
						label1JCH(jmolApplet1);
						getTautomerData(document.getElementById("tautomerDiv").innerHTML);
					}
				}, 2);

		function loadMolecule(molData, appid) {
			var s = 'load inline "' + molData + '"; ';
			javascript: Jmol.script(appid, s);
			var d = 'select *;label %a;';
			javascript: Jmol.script(jmolApplet0, d);
		}

		function label1JCH(appid) {
			var d = document.getElementById("jsmolScriptContainer").innerHTML;
			javascript: Jmol.script(appid, d);
		}

		function getTautomerData(data) {
			var url;
			url = "http://cactus.nci.nih.gov/chemical/structure/tautomers:"
					+ data.trim() + "/smiles";
			var xmlHttp = null;
			xmlHttp = new XMLHttpRequest();
			xmlHttp.open("GET", url, false);
			xmlHttp.send(null);
			if (isDataAvailable(xmlHttp.responseText) === "~ Data not available ~") {
				console.log("Tautomer data not available");
				alert("here");
			} else {
				var tautomerData = xmlHttp.responseText.split("\n");
				if (tautomerData.length > 0) {
					addSelectOption(tautomerData);
				}
			}
		}

		function isDataAvailable(data) {
			if (data.indexOf("Page not found") >= 0) {
				return "~ Data not available ~";
			} else {
				return data;
			}
		}
		

		function addSelectOption(data) {
			var select;
			select = '<select name="smiles" id="tautomerSmiles" multiple  style="width: 100%; height: 100%;">';
			for ( var i = 0; i < data.length; i++) {
				var taut = data[i];
				select = select + '<option value="actualSMILES">'+taut+'</option>';
			}
			select = select + "</select>";
			document.getElementById("tautomerDiv").innerHTML = select;
		}
	</script>
</body>
</html>