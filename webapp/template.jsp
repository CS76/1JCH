<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>1JCH</title>
<link href="css/style.css" rel="stylesheet" type="text/css">
<link href='http://fonts.googleapis.com/css?family=Marcellus'
	rel='stylesheet' type='text/css'>
<script src="JSmol.min.js"></script>
</head>
<body>
	<div id="mainWrapper">
		<div id="miniMenuBar">&nbsp;</div>
		<div id="header">
			<div id="globalTitle"><a href="/1JCH">Prediction of One Bond Coupling Constants - 1JCH</a> </div>
			<div id="localTitle">
				<p>Data Viewer</p>
			</div>
		</div>
		<div id="dataContainer" style="height: 80.5%">
			<div id="MenuBar">
				<ul>
					<li><a href="DataSetExp.html" target="if">Experimental Data</a></li>
					<li><a href="DataSetNWChem.html" target="if">NWChem Data</a></li>
				</ul>
			</div>
			<div id="iframeContainer" style="height: 93%"
				background-color: #099">
				<iframe src="DataSetNWChem.html" name="if" style="border: 0; height: 100%; width: 99.5%" seamless></iframe>
			</div>
		</div>
		<div id="footer"></div>
	</div>
</body>
</html>