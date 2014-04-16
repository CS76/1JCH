<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="nwchemInput" scope="request"
	class="org.openscience.jch.servlet.NWChemInput"></jsp:useBean>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>1JCH</title>
<link href="css/style.css" rel="stylesheet" type="text/css">
<link href='http://fonts.googleapis.com/css?family=Marcellus'
	rel='stylesheet' type='text/css'>
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
<script src="JSmol.min.js"></script>
<script src="//code.jquery.com/jquery-1.9.1.js"></script>
<script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
</head>
<body>
	<div id="mainWrapper">
		<div id="miniMenuBar">&nbsp;</div>
		<div id="header">
			<div id="globalTitle"><a href="/1JCH">Prediction of One Bond Coupling Constants - 1JCH</a> </div>
			<div id="localTitle">
				<p>NWChem Input Generator <a style="float: right; padding-right: 10px;" href="/1JCH"><img alt="Home" src="images/home.png" height=" 20px;" width="20px;"></a><a style="float: right; padding-right: 10px;" href="/1JCH/PredictionInput.jsp"><img alt="Home" src="images/process.png" height=" 20px;" width="20px;"></a></p> 
			</div>
		</div>
		<div id="dataContainer" style="height: 180%">
			<form method="post" action="/1JCH/NWChemInputHandler"
				onsubmit=" return validateForm();">
				<table width="100%" height="35%" border="0">
					<tr height="7%" style="background-color: #CCC">
						<td colspan="2" style="padding-left: 5px">Structure Input</td>
					</tr>
					<tr height="84%">
						<td width="60%" height="80%" style="background-color: #DDD">
							<table width="100%" height="100%" border="0">
								<tr height="10%">
									<td width="35%"><input type="radio" id="smilesR"
										name="structureInput" value="smiles" checked="checked"
										onChange="iFSelected()"><a class="subHeader">SMILES</a><br></td>
									<td><input type="text" id="sSmiles" name="sSmiles"
										style="width: 85%; height: 70%"
										value='<jsp:getProperty property="SMILES" name="nwchemInput"/>'>&nbsp;
										<button type="button" id="smilesB"
											onClick="fetchStructure('smiles','CT')">&#8594;</button></td>
								</tr>
								<tr height="70%" valign="top">
									<td><input type="radio" id="structureR"
										name="structureInput" value="structure"
										onChange="iFSelected()"><a class="subHeader">SDF/MOL</a><br></td>
									<td><textarea id="sStructure" name="sStructure" rows="4"
											style="width: 85%; height: 90%; resize: none;"
											readonly="readonly"><jsp:getProperty
												property="MOlString" name="nwchemInput" /></textarea>&nbsp;
										<button type="button" id="structureB" disabled="disabled"
											onClick="fetchStructure('structure','CT')">&#8594;</button></td>
								</tr>
								<tr height="10%">
									<td><input type="radio" id="stringR" name="structureInput"
										value="stringSearch" onChange="iFSelected()"><a
										class="subHeader">Common Name/IUPAC</a><br></td>
									<td><input id="sString" type="text" name="sString"
										style="width: 85%; height: 70%"
										value='<jsp:getProperty property="IUPAC" name="nwchemInput"/>'
										readonly="readonly" onChange="explicitlyLoadMolecule()">&nbsp;
										<button type="button" id="stringB" disabled="disabled"
											onClick="fetchStructure('string','CT')">&#8594;</button></td>
								</tr>
								<tr height="10%">
									<td><button type="button" onClick="openEditor()"
											id="editB" style="height: 30px; width: 30px">&nbsp;</button></td>
								</tr>
							</table>
						</td>
						<td width="40%" height="80%" style="background-color: #000">
							<div id="jsmolContainer">
								<script type="text/javascript">
									var jmolApplet0;
									var use = "HTML5";
									var s = document.location.search;
									var mydiv = document
											.getElementById("jsmolContainer");
									var curr_width = mydiv.style.width;
									var curr_height = $(window).height() * (0.35) * (1.78) * (0.8);
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
									jmol = Jmol.getApplet("jmolApplet0", Info);
								</script>
							</div>
						</td>
					</tr>
					<tr height="9%">
						<td colspan="2" style="background-color: #DDD" align="left">
							<%
								String status;
								if (request.getAttribute("status") != null) {
									status = (String) request.getAttribute("status");
								} else {
									status = "";
								}
							%> <input type="submit" name="save" value="saveStructure"
							style="width: 100px">
							<button type="button" style="width: 80px"
								onClick='clearJSMol();clearField(["sStructure","sSmiles","sString"]);'>Clear</button>
							<a><jsp:getProperty property="structureStatus"
									name="nwchemInput" /></a>
						</td>
					</tr>
				</table>
			</form>
			<!-- NWChem Input Parameters-->
			<br>
			<form method="post" action="/1JCH/NWChemInputHandler"
				onsubmit="return validateNWChemParametersForm(<jsp:getProperty property="structureStatus"
			name="nwchemInput" />);">
				<table width="100%" height="60%" border="0">
					<tr height="2%" style="background-color: #CCC">
						<td colspan="3" style="padding-left: 5px">NWChem Input
							Parameters</td>
					</tr>
					<tr height="2%" style="background-color: #DDD">
						<td width="25%" align="right">START</td>
						<td width="55%" align="center"><input type="text"
							id="startParm" name="startParam" style="width: 95%; height: 70%"
							value='<%=((nwchemInput.getSTART_PARM() == null) ? "" : nwchemInput
					.getSTART_PARM())%>'></td>
						<td><button type="button" id="opener"
								onclick="displayHelp('START')">?</button></td>
						<div id="START" style="display: none">
							START <br />(START) [{string file_prefix default
							input_file_prefix}] \ <br /> [rtdb {string rtdb_file_name
							default file_prefix.db}]
						</div>
					</tr>
					<tr height="2%" style="background-color: #DDD">
						<td align="right">TITLE</td>
						<td align="center"><input type="text" name="titleParam"
							style="width: 95%; height: 70%"
							value='<%=((nwchemInput.getTITLE_PARM() == null) ? "" : nwchemInput
					.getTITLE_PARM())%>'></td>
						<td><button type="button" onclick="displayHelp('TITLE')">?</button></td>
						<div id="TITLE" style="display: none;">
							TITLE <br />TITLE {string title} <br /> <br />title "This is
							the title of my NWChem job"
						</div>
					</tr>
					<tr height="2%" style="background-color: #DDD">
						<td align="right">MEMORY</td>
						<td align="center"><input type="text" name="memoryParam"
							value='<%=((nwchemInput.getMEMORY_PARM() == null)
					? ""
					: nwchemInput.getMEMORY_PARM())%>'
							style="width: 59%; height: 70%">&nbsp; <select
							style="width: 35%; height: 70%">
								<option value="megaByte">mb</option>
								<option value="kiloByte">kb</option>
								<option value="gigaByte">gb</option>
						</select></td>
						<td><button type="button" onclick="displayHelp('MEMORY')">?</button></td>
						<div id="MEMORY" style="display: none;">
							MEMORY <br />MEMORY [[total] {integer total_size}] \ <br />
							[stack {integer stack_size}] \ <br /> [heap {integer heap_size}]
							\ <br /> [global {integer global_size}] \ <br /> [units {string
							units default real}] \ <br /> [(verify||noverify)] \ <br />
							[(nohardfail||hardfail)] <br /> <br />memory 8 mb <br />memory
							total 8 stack 2 heap 2 global 4 mb <br />memory stack 2 heap 2
							global 4 mb
						</div>
					</tr>
					<tr height="2%" style="background-color: #DDD">
						<td align="right">CHARGE</td>
						<td align="center"><input type="text" name="chargeParam"
							value='<%=((nwchemInput.getCHARGE_PARM() == null)
					? ""
					: nwchemInput.getCHARGE_PARM())%>'
							style="width: 95%; height: 70%"></td>
						<td valign="top"><button type="button"
								onclick="displayHelp('CHARGE')">?</button></td>
						<div id="CHARGE" style="display: none">
							CHARGE (http://www.nwchem-sw.org/index.php/Release62:Charge) <br />CHARGE
							{real charge default 0}
						</div>
					</tr>
					<tr height="15%" style="background-color: #DDD">
						<td valign="top" align="right">GEOMETRY</td>
						<td align="center"><textarea name="geomParam" rows="4"
								style="width: 95%; height: 90%; resize: none;"><%=((nwchemInput.getGEOM__PARM() == null) ? "" : nwchemInput
					.getGEOM__PARM())%></textarea></td>
						<td valign="top"><button type="button"
								onclick="displayHelp('GEOMETRY')">?</button></td>
						<div id="GEOMETRY" style="display: none;">
							GEOMETRY (http://www.nwchem-sw.org/index.php/Release62:Geometry)
							<br />GEOMETRY [{string name default geometry}] \ <br /> [units
							{string units default angstroms}] \ <br /> [bqbq] \ <br />
							[print [xyz] || noprint] \ <br /> [center || nocenter] \ <br />
							[autosym [real tol default 1d-2] || noautosym] \ <br /> [autoz
							|| noautoz] \ <br /> [adjust] \ <br /> [(nuc || nucl ||
							nucleus) {string nucmodel}] <br /> <br />GEOMETRY [units
							{string units default angstroms}] \
						</div>
					</tr>
					<tr height="10%" style="background-color: #DDD">
						<td valign="top" align="right">Basis sets</td>
						<td align="center"><select style="width: 95%;" id="basisSet"
							onChange="basisSetChange()">
								<option value="0">3-21++G</option>
								<option value="1">3-21++G*</option>
								<option value="2">3-21G</option>
								<option value="3">3-21G*</option>
								<option value="4">3-21G* Polarization</option>
								<option value="5">3-21GSP</option>
								<option value="6">4-22GSP</option>
								<option value="7">4-31G</option>
								<option value="9">6-31++G</option>
								<option value="10">6-31++G*</option>
								<option value="11">6-31++G**</option>
								<option value="12">6-31+G*</option>
								<option value="13">6-311++G(2d,2p)</option>
								<option value="14">6-311++G(3df,3pd)</option>
								<option value="15">6-311++G**</option>
								<option value="16">6-311+G*</option>
								<option value="17">6-311G</option>
								<option value="18">6-311G(2df,2pd)</option>
								<option value="19">6-311G*</option>
								<option value="20">6-311G* Polarization</option>
								<option value="21">6-311G**</option>
								<option value="22">6-311G** Polarization</option>
								<option value="23">6-31G</option>
								<option value="24">6-31G(3df,3pd)</option>
								<option value="26">6-31G*</option>
								<option value="27">6-31G* Polarization</option>
								<option value="28">6-31G**</option>
								<option value="29">6-31G** Polarization</option>
								<option value="30">6-31G*-Blaudeau</option>
								<option value="31">6-31G-Blaudeau</option>
								<option value="37">Ahlrichs Coulomb Fitting</option>
								<option value="38">Ahlrichs Polarization</option>
								<option value="39">Ahlrichs pVDZ</option>
								<option value="40">Ahlrichs TZV</option>
								<option value="41">Ahlrichs VDZ</option>
								<option value="42">Ahlrichs VTZ</option>
								<option value="43">ANO-RCC</option>
								<option value="47">apr-cc-pV(Q+d)Z</option>
								<option value="58">aug-cc-pCV5Z</option>
								<option value="60">aug-cc-pCVDZ</option>
								<option value="61">aug-cc-pCVDZ-DK</option>
								<option value="62">aug-cc-pCVQZ</option>
								<option value="63">aug-cc-pCVQZ-DK</option>
								<option value="64">aug-cc-pCVTZ</option>
								<option value="65">aug-cc-pCVTZ-DK</option>
								<option value="66">aug-cc-pV(5+d)Z</option>
								<option value="67">aug-cc-pV(5+d)Z Diffuse</option>
								<option value="68">aug-cc-pV(6+d)Z</option>
								<option value="69">aug-cc-pV(6+d)Z Diffuse</option>
								<option value="70">aug-cc-pV(D+d)Z</option>
								<option value="71">aug-cc-pV(D+d)Z Diffuse</option>
								<option value="72">aug-cc-pV(Q+d)Z</option>
								<option value="73">aug-cc-pV(Q+d)Z Diffuse</option>
								<option value="74">aug-cc-pV(T+d)Z</option>
								<option value="75">aug-cc-pV(T+d)Z Diffuse</option>
								<option value="76">aug-cc-pV5Z</option>
								<option value="77">aug-cc-pV5Z Diffuse</option>
								<option value="78">aug-cc-pV5Z-DK</option>
								<option value="79">aug-cc-pV5Z-DK Diffuse</option>
								<option value="85">aug-cc-pV5Z-PP</option>
								<option value="86">aug-cc-pV5Z-PP Diffuse</option>
								<option value="88">aug-cc-pV5Z-PP MP2 Fitting</option>
								<option value="96">aug-cc-pV5Z-PP-RI Diffuse</option>
								<option value="97">aug-cc-pV5Z-PP_OPTRI</option>
								<option value="98">aug-cc-pV5Z-RI diffuse</option>
								<option value="99">aug-cc-pV5Z_OPTRI</option>
								<option value="100">aug-cc-pV6Z</option>
								<option value="101">aug-cc-pV6Z Diffuse</option>
								<option value="102">aug-cc-pV6Z-RI diffuse</option>
								<option value="103">aug-cc-pVDZ</option>
								<option value="104">aug-cc-pVDZ Diffuse</option>
								<option value="105">aug-cc-pVDZ-DK</option>
								<option value="106">aug-cc-pVDZ-DK Diffuse</option>
								<option value="110">aug-cc-pVDZ-PP</option>
								<option value="112">aug-cc-pVDZ-PP Diffuse</option>
								<option value="114">aug-cc-pVDZ-PP MP2 Fitting</option>
								<option value="120">aug-cc-pVDZ-PP-RI Diffuse</option>
								<option value="122">aug-cc-pVDZ-PP_OPTRI</option>
								<option value="123">aug-cc-pVDZ-RI diffuse</option>
								<option value="124">aug-cc-pVDZ_OPTRI</option>
								<option value="125">aug-cc-pVQZ</option>
								<option value="126">aug-cc-pVQZ Diffuse</option>
								<option value="127">aug-cc-pVQZ-DK</option>
								<option value="128">aug-cc-pVQZ-DK Diffuse</option>
								<option value="132">aug-cc-pVQZ-PP</option>
								<option value="134">aug-cc-pVQZ-PP Diffuse</option>
								<option value="137">aug-cc-pVQZ-PP MP2 Fitting</option>
								<option value="142">aug-cc-pVQZ-PP-RI Diffuse</option>
								<option value="144">aug-cc-pVQZ-PP_OPTRI</option>
								<option value="145">aug-cc-pVQZ-RI diffuse</option>
								<option value="146">aug-cc-pVQZ_OPTRI</option>
								<option value="147">aug-cc-pVTZ</option>
								<option value="148">aug-cc-pVTZ Diffuse</option>
								<option value="149">aug-cc-pVTZ MP2 Fitting</option>
								<option value="151">aug-cc-pVTZ-DK</option>
								<option value="152">aug-cc-pVTZ-DK Diffuse</option>
								<option value="154">aug-cc-pVTZ-J</option>
								<option value="165">aug-cc-pVTZ-PP</option>
								<option value="166">aug-cc-pVTZ-PP Diffuse</option>
								<option value="168">aug-cc-pVTZ-PP MP2 Fitting</option>
								<option value="175">aug-cc-pVTZ-PP-RI Diffuse</option>
								<option value="176">aug-cc-pVTZ-PP_OPTRI</option>
								<option value="177">aug-cc-pVTZ-RI diffuse</option>
								<option value="178">aug-cc-pVTZ_OPTRI</option>
								<option value="179">aug-cc-pwCV5Z</option>
								<option value="180">aug-cc-pwCV5Z-DK</option>
								<option value="182">aug-cc-pwCV5Z-NR</option>
								<option value="183">aug-cc-pwCV5Z-PP_OPTRI</option>
								<option value="184">aug-cc-pwCVDZ</option>
								<option value="185">aug-cc-pwCVDZ-PP_OPTRI</option>
								<option value="186">aug-cc-pwCVQZ</option>
								<option value="187">aug-cc-pwCVQZ-DK</option>
								<option value="188">aug-cc-pwCVQZ-NR</option>
								<option value="189">aug-cc-pwCVQZ-PP_OPTRI</option>
								<option value="190">aug-cc-pwCVTZ</option>
								<option value="191">aug-cc-pwCVTZ-DK</option>
								<option value="193">aug-cc-pwCVTZ-NR</option>
								<option value="194">aug-cc-pwCVTZ-PP_OPTRI</option>
								<option value="195">aug-mcc-pV5Z</option>
								<option value="196">aug-mcc-pV6Z</option>
								<option value="197">aug-mcc-pV7Z</option>
								<option value="198">aug-mcc-pV8Z</option>
								<option value="199">aug-mcc-pVQZ</option>
								<option value="200">aug-mcc-pVTZ</option>
								<option value="201">aug-pc-0</option>
								<option value="205">aug-pc-1</option>
								<option value="206">aug-pc-2</option>
								<option value="212">aug-pc-3</option>
								<option value="213">aug-pc-4</option>
								<option value="216">aug-pcJ-0</option>
								<option value="217">aug-pcJ-0_2006</option>
								<option value="218">aug-pcJ-1</option>
								<option value="219">aug-pcJ-1_2006</option>
								<option value="220">aug-pcJ-2</option>
								<option value="221">aug-pcJ-2_2006</option>
								<option value="222">aug-pcJ-3</option>
								<option value="223">aug-pcJ-3_2006</option>
								<option value="224">aug-pcJ-4</option>
								<option value="225">aug-pcJ-4_2006</option>
								<option value="226">aug-pcS-0</option>
								<option value="227">aug-pcS-1</option>
								<option value="228">aug-pcS-2</option>
								<option value="229">aug-pcS-3</option>
								<option value="230">aug-pcS-4</option>
								<option value="231">aug-pV7Z</option>
								<option value="232">aug-pV7Z Diffuse</option>
								<option value="233">B2 basis set for Zn</option>
								<option value="234">Bauschlicher ANO</option>
								<option value="237">Binning-Curtiss (1d) Polarization</option>
								<option value="238">Binning-Curtiss (df) Polarization</option>
								<option value="239">Binning/Curtiss SV</option>
								<option value="240">Binning/Curtiss SVP</option>
								<option value="241">Binning/Curtiss VTZ</option>
								<option value="242">Binning/Curtiss VTZP</option>
								<option value="243">Blaudeau Polarization</option>
								<option value="245">cc-pCV5Z</option>
								<option value="247">cc-pCV6Z</option>
								<option value="249">cc-pCV6Z(old)</option>
								<option value="250">cc-pCVDZ</option>
								<option value="251">cc-pCVDZ(old)</option>
								<option value="252">cc-pCVDZ-F12</option>
								<option value="255">cc-pCVDZ-F12_OPTRI</option>
								<option value="256">cc-pCVQZ</option>
								<option value="257">cc-pCVQZ(old)</option>
								<option value="258">cc-pCVQZ-F12</option>
								<option value="262">cc-pCVQZ-F12_OPTRI</option>
								<option value="263">cc-pCVTZ</option>
								<option value="264">cc-pCVTZ(old)</option>
								<option value="266">cc-pCVTZ-F12</option>
								<option value="268">cc-pCVTZ-F12_OPTRI</option>
								<option value="269">cc-pV(5+d)Z</option>
								<option value="270">cc-pV(5+d)Z-RI</option>
								<option value="271">cc-pV(6+d)Z</option>
								<option value="272">cc-pV(6+d)Z-RI</option>
								<option value="273">cc-pV(D+d)Z</option>
								<option value="274">cc-pV(D+d)Z-RI</option>
								<option value="275">cc-pV(Q+d)Z</option>
								<option value="276">cc-pV(Q+d)Z-RI</option>
								<option value="277">cc-pV(T+d)Z</option>
								<option value="280">cc-pV(T+d)Z-RI</option>
								<option value="282">cc-pV5Z</option>
								<option value="283">cc-pV5Z(fi/sf/fw)</option>
								<option value="284">cc-pV5Z(fi/sf/lc)</option>
								<option value="285">cc-pV5Z(fi/sf/sc)</option>
								<option value="286">cc-pV5Z(pt/sf/fw)</option>
								<option value="287">cc-pV5Z(pt/sf/lc)</option>
								<option value="288">cc-pV5Z(pt/sf/sc)</option>
								<option value="289">cc-pV5Z-DK</option>
								<option value="293">cc-pV5Z-PP</option>
								<option value="296">cc-pV5Z-PP MP2 Fitting</option>
								<option value="302">cc-pV5Z-PP-RI</option>
								<option value="304">cc-pV5Z-RI</option>
								<option value="306">cc-pV6Z</option>
								<option value="307">cc-pV6Z-RI</option>
								<option value="309">cc-pV8Z</option>
								<option value="310">cc-pV9Z</option>
								<option value="311">cc-pVDZ</option>
								<option value="312">cc-pVDZ(fi/sf/fw)</option>
								<option value="313">cc-pVDZ(fi/sf/lc)</option>
								<option value="314">cc-pVDZ(fi/sf/sc)</option>
								<option value="315">cc-pVDZ(pt/sf/fw)</option>
								<option value="316">cc-pVDZ(pt/sf/lc)</option>
								<option value="317">cc-pVDZ(pt/sf/sc)</option>
								<option value="318">cc-pVDZ(seg-opt)</option>
								<option value="319">cc-pVDZ-DK</option>
								<option value="320">cc-pVDZ-F12</option>
								<option value="323">cc-pVDZ-F12_OPTRI</option>
								<option value="324">cc-pVDZ-fit2-1</option>
								<option value="327">cc-pVDZ-PP</option>
								<option value="330">cc-pVDZ-PP MP2 Fitting</option>
								<option value="340">cc-pVDZ-PP-RI</option>
								<option value="342">cc-pVDZ-RI</option>
								<option value="343">cc-pVQZ</option>
								<option value="344">cc-pVQZ(fi/sf/fw)</option>
								<option value="345">cc-pVQZ(fi/sf/lc)</option>
								<option value="346">cc-pVQZ(fi/sf/sc)</option>
								<option value="347">cc-pVQZ(pt/sf/fw)</option>
								<option value="348">cc-pVQZ(pt/sf/lc)</option>
								<option value="349">cc-pVQZ(pt/sf/sc)</option>
								<option value="350">cc-pVQZ(seg-opt)</option>
								<option value="351">cc-pVQZ-DK</option>
								<option value="353">cc-pVQZ-F12</option>
								<option value="355">cc-pVQZ-F12_OPTRI</option>
								<option value="357">cc-pVQZ-PP</option>
								<option value="363">cc-pVQZ-PP MP2 Fitting</option>
								<option value="368">cc-pVQZ-PP-RI</option>
								<option value="369">cc-pVQZ-RI</option>
								<option value="370">cc-pVTZ</option>
								<option value="371">cc-pVTZ MP2 Fitting</option>
								<option value="372">cc-pVTZ(fi/sf/fw)</option>
								<option value="373">cc-pVTZ(fi/sf/lc)</option>
								<option value="374">cc-pVTZ(fi/sf/sc)</option>
								<option value="375">cc-pVTZ(pt/sf/fw)</option>
								<option value="376">cc-pVTZ(pt/sf/lc)</option>
								<option value="377">cc-pVTZ(pt/sf/sc)</option>
								<option value="378">cc-pVTZ(seg-opt)</option>
								<option value="380">cc-pVTZ-DK</option>
								<option value="382">cc-pVTZ-F12</option>
								<option value="385">cc-pVTZ-F12_OPTRI</option>
								<option value="386">cc-pVTZ-fit2-1</option>
								<option value="388">cc-pVTZ-PP</option>
								<option value="392">cc-pVTZ-PP MP2 Fitting</option>
								<option value="398">cc-pVTZ-PP-RI</option>
								<option value="401">cc-pVTZ-RI</option>
								<option value="402">cc-pwCV5Z</option>
								<option value="403">cc-pwCV5Z Core Set</option>
								<option value="404">cc-pwCV5Z Tight</option>
								<option value="406">cc-pwCV5Z-DK</option>
								<option value="407">cc-pwCV5Z-NR</option>
								<option value="410">cc-pwCV5Z-PP</option>
								<option value="413">cc-pwCV5Z-PP MP2 Fitting</option>
								<option value="415">cc-pwCV5Z-RI</option>
								<option value="416">cc-pwCV5Z-RI tight</option>
								<option value="417">cc-pwCVDZ</option>
								<option value="418">cc-pwCVDZ Tight</option>
								<option value="422">cc-pwCVDZ-PP</option>
								<option value="425">cc-pwCVDZ-PP MP2 Fitting</option>
								<option value="428">cc-pwCVDZ-RI</option>
								<option value="429">cc-pwCVDZ-RI tight</option>
								<option value="430">cc-pwCVQZ</option>
								<option value="431">cc-pwCVQZ Tight</option>
								<option value="432">cc-pwCVQZ-DK</option>
								<option value="433">cc-pwCVQZ-NR</option>
								<option value="437">cc-pwCVQZ-PP</option>
								<option value="440">cc-pwCVQZ-PP MP2 Fitting</option>
								<option value="443">cc-pwCVQZ-RI</option>
								<option value="444">cc-pwCVQZ-RI tight</option>
								<option value="445">cc-pwCVTZ</option>
								<option value="446">cc-pwCVTZ Tight</option>
								<option value="447">cc-pwCVTZ-DK</option>
								<option value="449">cc-pwCVTZ-NR</option>
								<option value="453">cc-pwCVTZ-PP</option>
								<option value="457">cc-pwCVTZ-PP MP2 Fitting</option>
								<option value="460">cc-pwCVTZ-RI</option>
								<option value="461">cc-pwCVTZ-RI tight</option>
								<option value="462">ccemd-2</option>
								<option value="463">ccemd-3</option>
								<option value="464">ccJ-pV5Z</option>
								<option value="465">ccJ-pVDZ</option>
								<option value="466">ccJ-pVQZ</option>
								<option value="467">ccJ-pVTZ</option>
								<option value="470">Chipman DZP + Diffuse</option>
								<option value="471">coemd-2</option>
								<option value="472">coemd-3</option>
								<option value="473">coemd-4</option>
								<option value="474">coemd-ref</option>
								<option value="475">Cologne DKH2</option>
								<option value="476">Core/valence Functions (cc-pCV5Z)</option>
								<option value="477">Core/valence Functions
									(cc-pCV6Z(old))</option>
								<option value="478">Core/valence Functions (cc-pCV6Z)</option>
								<option value="479">Core/valence Functions
									(cc-pCVDZ(old))</option>
								<option value="480">Core/valence Functions (cc-pCVDZ)</option>
								<option value="481">Core/valence Functions
									(cc-pCVQZ(old))</option>
								<option value="482">Core/valence Functions (cc-pCVQZ)</option>
								<option value="483">Core/valence Functions
									(cc-pCVTZ(old))</option>
								<option value="484">Core/valence Functions (cc-pCVTZ)</option>
								<option value="485">CRENBL ECP</option>
								<option value="486">CRENBL ECP</option>
								<option value="487">CRENBS ECP</option>
								<option value="488">CRENBS ECP</option>
								<option value="489">CVTZ</option>
								<option value="490">d-aug-cc-pV5Z</option>
								<option value="491">d-aug-cc-pV5Z Diffuse</option>
								<option value="492">d-aug-cc-pV6Z</option>
								<option value="493">d-aug-cc-pV6Z Diffuse</option>
								<option value="494">d-aug-cc-pVDZ</option>
								<option value="495">d-aug-cc-pVDZ Diffuse</option>
								<option value="496">d-aug-cc-pVQZ</option>
								<option value="497">d-aug-cc-pVQZ Diffuse</option>
								<option value="498">d-aug-cc-pVTZ</option>
								<option value="499">d-aug-cc-pVTZ Diffuse</option>
								<option value="500">Def2-ECP</option>
								<option value="501">Def2-QZVP</option>
								<option value="502">Def2-QZVPD</option>
								<option value="503">Def2-QZVPP</option>
								<option value="504">Def2-QZVPPD</option>
								<option value="505">Def2-SV(P)</option>
								<option value="506">Def2-SVP</option>
								<option value="507">Def2-SVPD</option>
								<option value="508">Def2-TZVP</option>
								<option value="509">Def2-TZVPD</option>
								<option value="510">Def2-TZVPP</option>
								<option value="511">Def2-TZVPPD</option>
								<option value="512">DeMon Coulomb Fitting</option>
								<option value="513">DGauss A1 DFT Coulomb Fitting</option>
								<option value="514">DGauss A1 DFT Exchange Fitting</option>
								<option value="515">DGauss A2 DFT Coulomb Fitting</option>
								<option value="516">DGauss A2 DFT Exchange Fitting</option>
								<option value="517">dhf-ECP</option>
								<option value="518">dhf-QZVP</option>
								<option value="523">dhf-QZVPP</option>
								<option value="526">dhf-SV(P)</option>
								<option value="532">dhf-SVP</option>
								<option value="537">dhf-TZVP</option>
								<option value="539">dhf-TZVPP</option>
								<option value="541">DHMS Polarization</option>
								<option value="542">Dunning-Hay Diffuse</option>
								<option value="543">Dunning-Hay Double Rydberg</option>
								<option value="544">Dunning-Hay Rydberg</option>
								<option value="545">DZ (Dunning)</option>
								<option value="546">DZ + Double Rydberg (Dunning-Hay)</option>
								<option value="547">DZ + Rydberg (Dunning)</option>
								<option value="549">DZP (Dunning)</option>
								<option value="550">DZP + Diffuse (Dunning)</option>
								<option value="551">DZP + Rydberg (Dunning)</option>
								<option value="552">DZQ</option>
								<option value="554">DZVP (DFT Orbital)</option>
								<option value="555">DZVP2 (DFT Orbital)</option>
								<option value="562">Feller Misc. CVDZ</option>
								<option value="563">Feller Misc. CVQZ</option>
								<option value="564">Feller Misc. CVTZ</option>
								<option value="565">GAMESS PVTZ</option>
								<option value="566">GAMESS VTZ</option>
								<option value="567">Glendening Polarization</option>
								<option value="568">Hay-Wadt MB (n+1) ECP</option>
								<option value="569">Hay-Wadt VDZ (n+1) ECP</option>
								<option value="570">HAY/WADT (N-1) ECP</option>
								<option value="573">HONDO7 Polarization</option>
								<option value="574">Huzinaga Polarization</option>
								<option value="575">IGLO-II</option>
								<option value="576">IGLO-III</option>
								<option value="581">jul-cc-pV(D+d)Z</option>
								<option value="582">jul-cc-pV(Q+d)Z</option>
								<option value="587">jul-cc-pV(T+d)Z</option>
								<option value="598">jun-cc-pV(D+d)Z</option>
								<option value="604">jun-cc-pV(Q+d)Z</option>
								<option value="609">jun-cc-pV(T+d)Z</option>
								<option value="612">LANL08</option>
								<option value="613">LANL08(f)</option>
								<option value="614">LANL08+</option>
								<option value="615">LANL08d</option>
								<option value="616">Lanl2-[10s8p7d3f2g]</option>
								<option value="617">Lanl2-[5s4p4d2f]</option>
								<option value="618">Lanl2-[6s4p4d2f]</option>
								<option value="619">LANL2DZ ECP</option>
								<option value="620">LANL2DZ ECP</option>
								<option value="621">Lanl2DZ+1d1f</option>
								<option value="622">Lanl2DZ+2s2p2d2f</option>
								<option value="623">LANL2DZdp ECP</option>
								<option value="624">LANL2DZdp ECP Polarization</option>
								<option value="625">LANL2TZ</option>
								<option value="626">LANL2TZ(f)</option>
								<option value="627">LANL2TZ+</option>
								<option value="636">m6-31G</option>
								<option value="637">m6-31G*</option>
								<option value="646">maug-cc-pV(D+d)Z</option>
								<option value="647">maug-cc-pV(Q+d)Z</option>
								<option value="648">maug-cc-pV(T+d)Z</option>
								<option value="649">maug-cc-pVDZ</option>
								<option value="650">maug-cc-pVQZ</option>
								<option value="651">maug-cc-pVTZ</option>
								<option value="654">may-cc-pV(Q+d)Z</option>
								<option value="656">may-cc-pV(T+d)Z</option>
								<option value="657">may-cc-pV(T+d)Z</option>
								<option value="658">McLean/Chandler VTZ</option>
								<option value="659">MG3S</option>
								<option value="660">MIDI (Huzinaga)</option>
								<option value="661">MIDI!</option>
								<option value="662">MINI (Huzinaga)</option>
								<option value="663">MINI (Scaled)</option>
								<option value="664">modified LANL2DZ</option>
								<option value="666">NASA Ames ANO</option>
								<option value="667">NASA Ames ANO2</option>
								<option value="668">NASA Ames cc-pCV5Z</option>
								<option value="669">NASA Ames cc-pCVQZ</option>
								<option value="670">NASA Ames cc-pCVTZ</option>
								<option value="671">NASA Ames cc-pV5Z</option>
								<option value="672">NASA Ames cc-pVQZ</option>
								<option value="673">NASA Ames cc-pVTZ</option>
								<option value="676">Partridge Uncontracted 1</option>
								<option value="677">Partridge Uncontracted 2</option>
								<option value="678">Partridge Uncontracted 3</option>
								<option value="679">Partridge Uncontracted 4</option>
								<option value="681">pc-0</option>
								<option value="685">pc-1</option>
								<option value="690">pc-2</option>
								<option value="691">pc-3</option>
								<option value="695">pc-4</option>
								<option value="699">pcemd-2</option>
								<option value="700">pcemd-3</option>
								<option value="701">pcemd-4</option>
								<option value="702">pcJ-0</option>
								<option value="703">pcJ-0_2006</option>
								<option value="704">pcJ-1</option>
								<option value="705">pcJ-1_2006</option>
								<option value="706">pcJ-2</option>
								<option value="707">pcJ-2_2006</option>
								<option value="708">pcJ-3</option>
								<option value="709">pcJ-3_2006</option>
								<option value="710">pcJ-4</option>
								<option value="711">pcJ-4_2006</option>
								<option value="712">pcS-0</option>
								<option value="713">pcS-1</option>
								<option value="714">pcS-2</option>
								<option value="715">pcS-3</option>
								<option value="716">pcS-4</option>
								<option value="717">Pople (2d,2p) Polarization</option>
								<option value="718">Pople (2df,2pd) Polarization</option>
								<option value="719">Pople (3df,3pd) Polarization</option>
								<option value="720">Pople-style Diffuse</option>
								<option value="721">pSBKJC</option>
								<option value="722">Pt - mDZP</option>
								<option value="723">pV6Z</option>
								<option value="724">pV7Z</option>
								<option value="726">Roos Augmented Double Zeta ANO</option>
								<option value="727">Roos Augmented Triple Zeta ANO</option>
								<option value="731">s3-21G</option>
								<option value="738">s3-21G*</option>
								<option value="749">s6-31G</option>
								<option value="764">s6-31G*</option>
								<option value="768">Sadlej pVTZ</option>
								<option value="773">SARC-DKH</option>
								<option value="774">SARC-ZORA</option>
								<option value="778">SBKJC ECP</option>
								<option value="779">SBKJC Polarized (p,2d) - LFK</option>
								<option value="780">SBKJC VDZ ECP</option>
								<option value="781">SDB RLC ECP</option>
								<option value="782">SDB-aug-cc-pVQZ</option>
								<option value="783">SDB-aug-cc-pVQZ Diffuse</option>
								<option value="784">SDB-aug-cc-pVTZ</option>
								<option value="785">SDB-aug-cc-pVTZ Diffuse</option>
								<option value="786">SDB-cc-pVQZ</option>
								<option value="787">SDB-cc-pVTZ</option>
								<option value="788">STO-2G</option>
								<option value="789">STO-3G</option>
								<option value="790">STO-3G*</option>
								<option value="791">STO-3G* Polarization</option>
								<option value="792">STO-6G</option>
								<option value="793">Stuttgart RLC ECP</option>
								<option value="794">Stuttgart RLC ECP</option>
								<option value="795">Stuttgart RSC 1997 ECP</option>
								<option value="796">Stuttgart RSC 1997 ECP</option>
								<option value="797">Stuttgart RSC ANO/ECP</option>
								<option value="799">Stuttgart RSC Segmented/ECP</option>
								<option value="800">Stuttgart-Koeln MCDHF RSC ECP</option>
								<option value="801">SV (Dunning-Hay)</option>
								<option value="802">SV + Double Rydberg (Dunning-Hay)</option>
								<option value="803">SV + Rydberg (Dunning-Hay)</option>
								<option value="804">SVP (Dunning-Hay)</option>
								<option value="805">SVP + Diffuse (Dunning-Hay)</option>
								<option value="806">SVP + Diffuse + Rydberg</option>
								<option value="807">SVP + Rydberg (Dunning-Hay)</option>
								<option value="810">TZ (Dunning)</option>
								<option value="812" selected>TZVP (DFT Orbital)</option>
								<option value="815">UGBS</option>
								<option value="816">un-ccemd-ref</option>
								<option value="817">un-pcemd-ref</option>
								<option value="818">Wachters+f</option>
								<option value="819">Weigend Coulomb Fitting</option>
								<option value="820">WTBS</option>
						</select> <br> <textarea rows="4" id="basisSetParam"
								name="basisSetParam"
								style="width: 95%; height: 70%; resize: none;"><%=((nwchemInput.getBASIS_SETS_PARAM() == null)
					? ""
					: nwchemInput.getBASIS_SETS_PARAM())%></textarea></td>
						<td valign="top"><button type="button"
								onclick="displayHelp('basisSets')">?</button></td>
						<div id="basisSets" style="display: none;">
							Basis sets(http://www.nwchem-sw.org/index.php/Release62:Basis) <br />complete
							list of basis sets and associated ECPs in the NWChem library
							(http://www.nwchem-sw.org/index.php/Release62:AvailableBasisSets)
							<br />EMSL Basis set exchange (https://bse.pnl.gov/bse/portal) <br />
							<br />BASIS [{string name default "ao basis"}] \ <br />
							[(spherical || cartesian) default cartesian] \ <br /> [(segment
							|| nosegment) default segment] \ <br /> [(print || noprint)
							default print] <br /> [rel] <br /> {string tag} library
							[{string tag_in_lib}] \ <br /> {string standard_set} [file
							{filename}] \ <br /> [except {string tag list}] [rel] <br />
							... <br /> {string tag} {string shell_type} [rel] <br /> {real
							exponent} {real list_of_coefficients} <br /> ... <br /> END <br />
							<br /> TZVP (DFT Orbital): H C N O F Al Si P S Cl Ar <br /> *
							library "TZVP (DFT Orbital)"
						</div>
					</tr>
					<tr height="10%" style="background-color: #DDD">
						<td valign="top" align="right">Quantum Mechanical Methods</td>
						<td align="center"><select style="width: 95%;">
								<option value="dft">Density Functional Theory</option>
						</select> <br> <textarea name="qmmParam" rows="4"
								style="width: 95%; height: 70%; resize: none;"><%=((nwchemInput.getQMM__PARM() == null) ? "" : nwchemInput
					.getQMM__PARM())%></textarea></td>
						<td valign="top"><button type="button"
								onclick="displayHelp('QMM')">?</button></td>
						<div id="QMM" style="display: none;">
							Density Functional Theory <br /> <br />DFT <br /> ... <br />END
							<br /> <br />TASK DFT <br /> <br />dft <br /> odft <br />
							xc b3lyp <br />end <br />-----------------------------------------------------------------------------------------------
							<br /> VECTORS [[input] ({string input_movecs default atomic})
							|| \ <br /> (project {string basisname} {string filename})] \ <br />
							[swap [alpha||beta] {integer vec1 vec2} ...] \ <br /> [output
							{string output_filename default input_movecs}] \ <br /> XC
							[[acm] [b3lyp] [beckehandh] [pbe0]\ <br /> [becke97] [becke97-1]
							[becke97-2] [becke97-3] [becke97-d] [becke98] \ <br /> [hcth]
							[hcth120] [hcth147]\ <br /> [hcth407] [becke97gga1] [hcth407p]\
							<br /> [mpw91] [mpw1k] [xft97] [cft97] [ft97] [op] [bop]
							[pbeop]\ <br /> [xpkzb99] [cpkzb99] [xtpss03] [ctpss03]
							[xctpssh]\ <br /> [b1b95] [bb1k] [mpw1b95] [mpwb1k] [pw6b95]
							[pwb6k] [m05] [m05-2x] [vs98] \ <br /> [m06] [m06-hf] [m06-L]
							[m06-2x] \ <br /> [HFexch {real prefactor default 1.0}] \ <br />
							[becke88 [nonlocal] {real prefactor default 1.0}] \ <br />
							[xperdew91 [nonlocal] {real prefactor default 1.0}] \ <br />
							[xpbe96 [nonlocal] {real prefactor default 1.0}] \ <br />
							[gill96 [nonlocal] {real prefactor default 1.0}] \ <br /> [lyp
							{real prefactor default 1.0}] \ <br /> [perdew81 {real prefactor
							default 1.0}] \ <br /> [perdew86 [nonlocal] {real prefactor
							default 1.0}] \ <br /> [perdew91 [nonlocal] {real prefactor
							default 1.0}] \ <br /> [cpbe96 [nonlocal] {real prefactor
							default 1.0}] \ <br /> [pw91lda {real prefactor default 1.0}] \
							<br /> [slater {real prefactor default 1.0}] \ <br /> [vwn_1
							{real prefactor default 1.0}] \ <br /> [vwn_2 {real prefactor
							default 1.0}] \ <br /> [vwn_3 {real prefactor default 1.0}] \ <br />
							[vwn_4 {real prefactor default 1.0}] \ <br /> [vwn_5 {real
							prefactor default 1.0}] \ <br /> [vwn_1_rpa {real prefactor
							default 1.0}] \ <br /> [xtpss03 [nonlocal] {real prefactor
							default 1.0}] \ <br /> [ctpss03 [nonlocal] {real prefactor
							default 1.0}] \ <br /> [bc95 [nonlocal] {real prefactor default
							1.0}] \ <br /> [xpw6b95 [nonlocal] {real prefactor default 1.0}]
							\ <br /> [xpwb6k [nonlocal] {real prefactor default 1.0}] \ <br />
							[xm05 [nonlocal] {real prefactor default 1.0}] \ <br /> [xm05-2x
							[nonlocal] {real prefactor default 1.0}] \ <br /> [cpw6b95
							[nonlocal] {real prefactor default 1.0}] \ <br /> [cpwb6k
							[nonlocal] {real prefactor default 1.0}] \ <br /> [cm05
							[nonlocal] {real prefactor default 1.0}] \ <br /> [cm05-2x
							[nonlocal] {real prefactor default 1.0}]] \ <br /> [xvs98
							[nonlocal] {real prefactor default 1.0}]] \ <br /> [cvs98
							[nonlocal] {real prefactor default 1.0}]] \ <br /> [xm06-L
							[nonlocal] {real prefactor default 1.0}]] \ <br /> [xm06-hf
							[nonlocal] {real prefactor default 1.0}]] \ <br /> [xm06
							[nonlocal] {real prefactor default 1.0}]] \ <br /> [xm06-2x
							[nonlocal] {real prefactor default 1.0}]] \ <br /> [cm06-L
							[nonlocal] {real prefactor default 1.0}]] \ <br /> [cm06-hf
							[nonlocal] {real prefactor default 1.0}]] \ <br /> [cm06
							[nonlocal] {real prefactor default 1.0}]] \ <br /> [cm06-2x
							[nonlocal] {real prefactor default 1.0}]] <br /> CONVERGENCE
							[[energy {real energy default 1e-7}] \ <br /> [density {real
							density default 1e-5}] \ <br /> [gradient {real gradient default
							5e-4}] \ <br /> [dampon {real dampon default 0.0}] \ <br />
							[dampoff {real dampoff default 0.0}] \ <br /> [diison {real
							diison default 0.0}] \ <br /> [diisoff {real diisoff default
							0.0}] \ <br /> [levlon {real levlon default 0.0}] \ <br />
							[levloff {real levloff default 0.0}] \ <br /> [ncydp {integer
							ncydp default 2}] \ <br /> [ncyds {integer ncyds default 30}] \
							<br /> [ncysh {integer ncysh default 30}] \ <br /> [damp
							{integer ndamp default 0}] [nodamping] \ <br /> [diis [nfock
							{integer nfock default 10}]] \ <br /> [nodiis] [lshift {real
							lshift default 0.5}] \ <br /> [nolevelshifting] \ <br />
							[hl_tol {real hl_tol default 0.1}] \ <br /> [rabuck [n_rabuck
							{integer n_rabuck default 25}]] <br /> GRID
							[(xcoarse||coarse||medium||fine||xfine) default medium] \ <br />
							[(gausleg||lebedev ) default lebedev ] \ <br />
							[(becke||erf1||erf2||ssf) default erf1] \ <br />
							[(euler||mura||treutler) default mura] \ <br /> [rm {real rm
							default 2.0}] \ <br /> [nodisk] <br /> TOLERANCES [[tight]
							[tol_rho {real tol_rho default 1e-10}] \ <br /> [accCoul
							{integer accCoul default 8}] \ <br /> [radius {real radius
							default 25.0}]] <br /> [(LB94||CS00 {real shift default none})]
							<br /> DECOMP <br /> ODFT <br /> DIRECT <br /> SEMIDIRECT
							[filesize {integer filesize default disksize}] <br /> [memsize
							{integer memsize default available}] <br /> [filename {string
							filename default $file_prefix.aoints$}] <br /> INCORE <br />
							ITERATIONS {integer iterations default 30} <br /> MAX_OVL <br />
							CGMIN <br /> RODFT <br /> MULLIKEN <br /> DISP <br /> MULT
							{integer mult default 1} <br /> NOIO <br /> PRINT||NOPRINT <br />
							----------------------------------------------------------------------------------------------
							<br /> Setting up common exchange-correlation functionals <br />
							B3LYP: xc b3lyp <br /> PBE0: xc pbe0 <br /> PBE96: xc xpbe96
							cpbe96 <br /> PW91: xc xperdew91 perdew91 <br /> BHLYP: xc
							bhlyp <br /> Becke Half and Half: xc beckehandh <br /> BP86: xc
							becke88 perdew86 <br /> BP91: xc becke88 perdew91 <br /> BLYP:
							xc becke88 lyp
						</div>
					</tr>
					<tr height="10%" style="background-color: #DDD">
						<td valign="top" align="right">Geometry Optimization</td>
						<td align="center"><select style="width: 95%;">
								<option value="driver">DRIVER</option>
						</select> <br> <textarea name="geoOptParam" rows="4"
								style="width: 95%; height: 70%; resize: none;"><%=((nwchemInput.getGEO_OPT_PARM() == null)
					? ""
					: nwchemInput.getGEO_OPT_PARM())%></textarea></td>
						<td valign="top"><button type="button"
								onclick="displayHelp('GOPT')">?</button></td>
						<div id="GOPT" style="display: none;">
							Geometry Optimization with DRIVER (default)/ STEPPER <br /> <br />Available
							precision <br />EPREC {real eprec default 1e-7} <br /> <br />
							DRIVER <br /> (LOOSE || DEFAULT || TIGHT) <br /> GMAX {real
							value} <br /> GRMS {real value} <br /> XMAX {real value} <br />
							XRMS {real value} <br /> EPREC {real eprec default 1e-7} <br />
							TRUST {real trust default 0.3} <br /> SADSTP {real sadstp
							default 0.1} <br /> CLEAR <br /> REDOAUTOZ <br /> INHESS
							{integer inhess default 0} <br /> (MODDIR || VARDIR) {integer
							dir default 0} <br /> (FIRSTNEG || NOFIRSTNEG) <br /> MAXITER
							{integer maxiter default 20} <br /> BSCALE {real BSCALE default
							1.0} <br /> ASCALE {real ASCALE default 0.25} <br /> TSCALE
							{real TSCALE default 0.1} <br /> HSCALE {real HSCALE default
							1.0} <br /> PRINT ... <br /> XYZ {string xyz default
							file_prefix}] <br /> NOXYZ <br /> END <br /> <br />
							Convergence criteria <br /> <br /> (LOOSE || DEFAULT || TIGHT)
							<br /> GMAX {real value} <br /> GRMS {real value} <br /> XMAX
							{real value} <br /> XRMS {real value} <br /> <br /> LOOSE
							DEFAULT TIGHT <br /> GMAX 0.00450 0.00045 0.000015 <br /> GRMS
							0.00300 0.00030 0.00001 <br /> XMAX 0.01800 0.00180 0.00006 <br />
							XRMS 0.01200 0.00120 0.00004 <br /> <br /> Maximum number of
							steps <br /> <br /> MAXITER {integer maxiter default 20} <br />
							<br />driver <br /> maxiter 90 <br />end

						</div>
					</tr>
					<tr height="10%" style="background-color: #DDD">
						<td valign="top" align="right">PROPERTY</td>
						<td align="center"><textarea name="propertyParam" rows="4"
								style="width: 95%; height: 90%; resize: none;"><%=((nwchemInput.getPROPERTY_PARM() == null)
					? ""
					: nwchemInput.getPROPERTY_PARM())%></textarea></td>
						<td valign="top"><button type="button"
								onclick="displayHelp('PROPERTY')">?</button></td>
						<div id="PROPERTY" style="display: none;">
							property <br /> mulliken <br />end
						</div>
					</tr>
					<tr height="10%" style="background-color: #DDD">
						<td align="right">TASK</td>
						<td align="center"><textarea name="taskParam" rows="4"
								style="width: 95%; height: 90%; resize: none;"><%=((nwchemInput.getTASKS_PARM() == null) ? "" : nwchemInput
					.getTASKS_PARM())%></textarea></td>
						<td valign="top"><button type="button"
								onclick="displayHelp('TASK')">?</button></td>
						<div id="TASK" style="display: none">
							TASK <br /> <br />task dft optimize <br /> <br />property <br />
							mulliken <br />end <br /> <br />TASK <br />task dft property
						</div>
					</tr>

				</table>
				<div align="right" style="padding-top: 7px; padding-right: 10px;">
					<input type="hidden" name="nwchemID" value="${nwchemID}" /> <input
						type="submit" style="width: 200px" name="save"
						value="saveNWChemParameters">
					<button type="button" style="width: 150px"
						onClick="copyToClipboard()">Copy to Clipboard</button>
					<button type="button" style="width: 50px"
						onClick="openDisplayWindow()">view</button>
					<button type="button"
						onclick="location.href = 'WebController?initialSelector=NWChemInputGenerator';">New InputFile</button>
					<a id="export" class="myButton" download="" href="#"></a>
				</div>
			</form>
			<div id="hiddenIpData" style="display: none;"><%=((nwchemInput.getNwchemInputData() == null)
					? ""
					: nwchemInput.getNwchemInputData())%></div>
		</div>
		<div id="footer"></div>
	</div>
	</div>
	<div id="dialog-form" title="Select Specific Tautomer"
		style="font-size: 10px;"></div>
	<div id="dialog" title="help" style="font-size: 10px;">
		<p></p>
	</div>
</body>
<script type="text/javascript">
	$(function() {
		$("#dialog").dialog({
			autoOpen : false,
			height : 300,
			width : 400,
			show : {
				effect : "blind",
				duration : 1000
			},
			hide : {
				effect : "blind",
				duration : 1000
			},
			buttons : {
				"OK" : function() {
					$(this).dialog("close");
				}
			}
		});

		$("#dialog-form").dialog({
			autoOpen : false,
			height : 600,
			width : 700,
			modal : true,
			buttons : {
				"Select Tautomer" : function() {
					setTautomer($('input[name="tautomer"]:checked').val());
					$(this).dialog("close");
				}
			}
		});
	});

	function displayHelp(data) {
		var content = document.getElementById(data).innerHTML;
		document.getElementById("dialog").innerHTML = content;
		$("#dialog").dialog("open");
	}

	function setTautomer(data) {
		document.getElementById("sSmiles").value = data;
		fetchStructure('smiles', 'NCT');
	}

	function iFSelected() {
		if (document.getElementById('smilesR').checked) {
			console.log("Smiles");
			document.getElementById('sSmiles').removeAttribute("readonly");
			document.getElementById('smilesB').disabled = false;
			document.getElementById('sStructure').setAttribute("readonly",
					"readonly");
			document.getElementById('structureB').disabled = true;
			document.getElementById('sString').setAttribute("readonly",
					"readonly");
			document.getElementById('stringB').disabled = true;

		} else if (document.getElementById('structureR').checked) {
			document.getElementById('sSmiles').setAttribute("readonly",
					"readonly");
			document.getElementById('smilesB').disabled = true;
			document.getElementById('sStructure').removeAttribute("readonly");
			document.getElementById('structureB').disabled = false;
			document.getElementById('sString').setAttribute("readonly",
					"readonly");
			document.getElementById('stringB').disabled = true;

		} else if (document.getElementById('stringR').checked) {
			document.getElementById('sSmiles').setAttribute("readonly",
					"readonly");
			document.getElementById('smilesB').disabled = true;
			document.getElementById('sStructure').setAttribute("readonly",
					"readonly");
			document.getElementById('structureB').disabled = true;
			document.getElementById('sString').removeAttribute("readonly");
			document.getElementById('stringB').disabled = false;
		}
	}

	function fetchStructure(data, check) {
		if (check === 'CT') {
			if (data === "smiles") {
				var elmnt = document.getElementById("sSmiles");
				if (elmnt.value == "") {
					alert("No Input")
				} else {
					var molData = getSDF(elmnt.value);
					getTautomerData("smiles", elmnt.value);
					document.getElementById("sStructure").value = molData;
					document.getElementById("sString").value = getIPUAC(elmnt.value);
					loadMolecule(molData);
				}
			} else if (data === "string") {
				var elmnt = document.getElementById("sString");
				if (elmnt.value == "") {
					alert("No Input")
				} else {
					var molData = getSDF(elmnt.value);
					getTautomerData("string", elmnt.value);
					document.getElementById("sStructure").value = getSDF(elmnt.value);
					document.getElementById("sSmiles").value = getSmiles(elmnt.value);
					loadMolecule(molData);
				}
			} else {
				var elmnt = document.getElementById("sStructure");
				if (elmnt.value == "") {
					alert("No Input")
				}
			}
		} else {
			if (data === "smiles") {
				var elmnt = document.getElementById("sSmiles");
				if (elmnt.value == "") {
					alert("No Input")
				} else {
					var molData = getSDF(elmnt.value);
					document.getElementById("sStructure").value = molData;
					document.getElementById("sString").value = getIPUAC(elmnt.value);
					loadMolecule(molData);
				}
			} else if (data === "string") {
				var elmnt = document.getElementById("sString");
				if (elmnt.value == "") {
					alert("No Input")
				} else {
					var molData = getSDF(elmnt.value);
					document.getElementById("sStructure").value = getSDF(elmnt.value);
					document.getElementById("sSmiles").value = getSmiles(elmnt.value);
					loadMolecule(molData);
				}
			} else {
				var elmnt = document.getElementById("sStructure");
				if (elmnt.value == "") {
					alert("No Input")
				}
			}
		}
	}

	function getSmiles(data) {
		var query = data;
		var url = "http://cactus.nci.nih.gov/chemical/structure/" + query
				+ "/smiles";
		var xmlHttp = null;
		xmlHttp = new XMLHttpRequest();
		xmlHttp.open("GET", url, false);
		xmlHttp.send(null);
		return isDataAvailable(xmlHttp.responseText);
	}
	function getSDF(data) {
		var query = data.replace("#","%23");
		var url = "http://cactus.nci.nih.gov/chemical/structure/" + query
				+ "/file?format=mol&get3d=True";
		var xmlHttp = null;
		xmlHttp = new XMLHttpRequest();
		xmlHttp.open("GET", url, false);
		xmlHttp.send(null);
		return isDataAvailable(xmlHttp.responseText);
	}
	function getIPUAC(data) {
		var query = data.replace("#","%23");;
		var url = "http://cactus.nci.nih.gov/chemical/structure/" + query
				+ "/iupac_name";
		var xmlHttp = null;
		xmlHttp = new XMLHttpRequest();
		xmlHttp.open("GET", url, false);
		xmlHttp.send(null);
		return isDataAvailable(xmlHttp.responseText);
	}

	function getTautomerData(data, query) {
		var url;
		var query = query.replace("#","%23");
		
		if (data === 'smiles') {
			url = "http://cactus.nci.nih.gov/chemical/structure/tautomers:"
					+ query + "/smiles";
		} else {
			url = "http://cactus.nci.nih.gov/chemical/structure/tautomers:"
					+ query + "/smiles";
		}
		
		var xmlHttp = null;
		xmlHttp = new XMLHttpRequest();
		xmlHttp.open("GET", url, false);
		xmlHttp.send(null);
		if (isDataAvailable(xmlHttp.responseText) === "~ Data not available ~") {
			console.log("Tautomer data not available");
		} else {
			var tautomerData = xmlHttp.responseText.split("\n");
			if (tautomerData.length > 1) {
				addRadioButtonToForm(tautomerData);
			}
		}
	}

	function addRadioButtonToForm(data) {
		var innerHTML = '';
		innerHTML = innerHTML + '<form action="">';

		for ( var i = 0; i < data.length; i++) {
			var taut = data[i];
			if (i === 0) {
				innerHTML = innerHTML
						+ '<input type="radio" name="tautomer" value="'+taut+'" checked>'
						+ taut + '<br>';
			} else {
				innerHTML = innerHTML
						+ '<input type="radio" name="tautomer" value="'+taut+'">'
						+ taut + '<br>';
			}
		}
		innerHTML = innerHTML + '</form>';
		document.getElementById('dialog-form').innerHTML = innerHTML;
		$("#dialog-form").dialog("open");

	}

	function clearField(data) {
		for ( var s in data) {
			document.getElementById(data[s]).value = "";
		}
	}
	function openEditor() {
		window.open('jme_window.html', 'JME',
				'width=450,height=450,scrollbars=no,resizable=yes');
	}
	function fromEditor(smiles, jme) {
		if (smiles == "") {
			alert("no molecule submitted");
			return;
		}
		document.getElementById("sSmiles").value = smiles;
		fetchStructure("smiles");
	}

	function getMolData() {
		return document.getElementById("sStructure").value;
	}
	function loadMolecule(molData) {
		var s = 'load inline "' + molData + '"; ';
		javascript: Jmol.script(jmolApplet0, s);
	}
	function clearJSMol() {
		javascript: Jmol.script(jmolApplet0, 'zap;');
	}
	function isDataAvailable(data) {
		if (data.indexOf("Page not found") >= 0) {
			return "~ Data not available ~";
		} else {
			return data;
		}
	}
	function basisSetChange() {
		var basisSetSelect = document.getElementById("basisSet");
		var bs = "basis\n"
				+ "  * library \""
				+ basisSetSelect.options[basisSetSelect.selectedIndex].innerHTML
				+ "\"\n" + "end\n";
		console.log(bs);
		document.getElementById("basisSetParam").value = bs;
	}

	function getInputFileData() {
		console.log(document.getElementById("hiddenIpData").innerHTML);
		return document.getElementById("hiddenIpData").innerHTML;
	}

	window.onload = function() {
		var data = document.getElementById("sStructure").value;
		if (data.length > 0) {
			loadMolecule(data);
		}

		var basisSetData = document.getElementById("basisSetParam").value;
		if (basisSetData.length != 0) {
			var Fst = basisSetData.indexOf('"') + 1;
			var Snd = basisSetData.indexOf('"', Fst + 1);
			var basisSet = basisSetData.substring(Fst, Snd);
			basisSetList = document.getElementById("basisSet");
			for ( var i = 0; i < basisSetList.length; i++) {
				if (basisSetList.options[i].text === basisSet) {
					basisSetList.options[i].selected = true;
				}
			}
		}

		if (document.getElementById("hiddenIpData").innerHTML !== "") {
			genDL(document.getElementById("hiddenIpData").innerHTML);
		}
	}
	function openDisplayWindow() {
		window.open('display_window.html', 'NWCHEM INPUT FILE CONTENTS',
				'width=450,height=450,scrollbars=no,resizable=yes');
	}
	function copyToClipboard(data) {
			var textToCopy = ""
			if (document.getElementById("hiddenIpData").innerHTML !== "") {
				textToCopy = document.getElementById("hiddenIpData").innerHTML;
	
			}
	
			window.prompt("Copy to clipboard: Ctrl+C, Enter", textToCopy);
		}
	
	function createDownloadLink(anchorSelector, str, fileName) {
		var blobObject = null;
		if (window.navigator.msSaveOrOpenBlob) {
			var fileData = [ str ];
			blobObject = new Blob(fileData);
			$(anchorSelector).click(function() {
				window.navigator.msSaveOrOpenBlob(blobObject, fileName);
			});
		} else {
			var name = fileName + ".nw";
			var url = "data:text/plain;charset=utf-8,"
					+ encodeURIComponent(str);
			$(anchorSelector).attr("href", url);
			$(anchorSelector).attr("download", name);
		}
	}

	function genDL(data) {
		var str = data;
		document.getElementById("export").innerHTML = "export";
		createDownloadLink("#export", str,
				document.getElementById("startParm").value);
	}

	function validateForm() {
		var x = document.getElementById("sStructure").value;
		if (x == null || x == "") {
			alert("No Structure input or Coordinates found");
			return false;
		}
	}
	function validateNWChemParametersForm(data) {
		if (data == null || data == "") {
			alert("No Structure Input or Coordinates saved.\nPlease save the structure details");
			return false;
		}
	}
</script>
</html>