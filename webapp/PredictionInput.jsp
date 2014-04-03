<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>1JCH</title>
<link href="css/style.css" rel="stylesheet" type="text/css">
<link href='http://fonts.googleapis.com/css?family=Marcellus'
	rel='stylesheet' type='text/css'>

</head>
<body>
	<div id="mainWrapper">
		<div id="miniMenuBar">&nbsp;</div>
		<div id="header">
			<div id="globalTitle">Prediction of One Bond Coupling Constants
				- 1JCH</div>
			<div id="localTitle">&nbsp;</div>
		</div>
		<div id="dataContainer" style="height: 84%;">
			&nbsp;
			<table width="100%" height="100%" border="0">
				<tr height="25%">
					<td width="25%"></td>
					<td width="50%">
						<div id="apps" align="left" style="">
							<form action="/1JCH/PredictionHandler" method="post"
								enctype="multipart/form-data" onSubmit="return validate()">
								<input style="margin: 20px" type="file" name="file" size="50"
									id="fileUpload" /> <br />
								<p style="padding-left: 20px; margin: 0">Select Prediction
									Model</p>
								<select style="width: 60%; margin-left: 20px;"
									name="Select Model">
									<option value="0">MVLR</option>
									<option value="1">PLSR</option>
								</select><br /> <input type="submit" value="Upload File"
									style="margin-left: 20px; margin-top: 10px" /> <br /> <a>${requestScope.fileUploadStatus}</a>
								<p id="refreshWarning" style="padding-left: 10px; font-size: 14px;" ></p>
							</form>
						</div>
					</td>
					<td width="25%"></td>
				</tr>
				<tr height="60%">
					<td width="25%"></td>
					<td width="50%" valign="top"><a id="toggleDisplay"
						style="font-size: 15px; cursor: pointer"
						onClick="changeOpDisplay()">Show file content</a>
						<div id="outputDisplayContainer" style="display: none;">
							<textarea id="outputDisplay" rows="10" cols="60"
								style="resize: none; margin-left: 20px"></textarea>
						</div></td>
					<td width="25%"></td>
				</tr>
			</table>

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
			message = "Please <b style=\"color: #F00;\">dont refresh</b> while the page loads";
			if (document.getElementById("fileUpload").value == '') {
				// your validation error action
				valid = false;
				message = "<a style=\"color: #F00;\">Please select a NWChem log file to process</a>";
			}
			document.getElementById("refreshWarning").innerHTML = message;
			return valid 
		}
		
		function setCursorByID(id, cursorStyle) {
			var elem;
			if (document.getElementById && (elem = document.getElementById(id))) {
				if (elem.style)
					elem.style.cursor = cursorStyle;
			}
		}
	</script>
</body>
</html>