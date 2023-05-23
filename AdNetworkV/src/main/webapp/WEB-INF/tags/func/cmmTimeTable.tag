<%@ tag pageEncoding="UTF-8"%>

<style>

/* 시간 설정 테이블 */
table#time-table {
	border-spacing: 0px;
}
table#time-table th {
	font-weight: 400;
}
table#time-table td {
	border: 1px solid #fff;
	width: 28px;
	height: 30px;
	margin: 10px;
	background-color: rgba(24,28,33,0.06) !important;
	cursor: default;
	padding: 0px;
}
table#time-table td.selected {
	background-color: #02a96b !important;
}
table#time-table td.rselected {
	background-color: #02BC77 !important;
}
table#time-table td.nzselected {
	background-color: rgba(2, 188, 119, 0.5) !important;
}

</style>


<script>

var timeTable = $("#time-table");
var isTimeMouseDown = false;
var isTimeMouseDown2 = false;
var timeStartRowIndex = null;
var timeStartCellIndex = null;

var timeTableIds = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];


function ttSelectTo(cell) {

	var row = cell.parent();    
	var cellIndex = cell.index() - 1;
	var rowIndex = row.index();
	    
	var rowStart, rowEnd, cellStart, cellEnd;

	if (rowIndex < timeStartRowIndex) {
		rowStart = rowIndex;
		rowEnd = timeStartRowIndex;
	} else {
		rowStart = timeStartRowIndex;
		rowEnd = rowIndex;
	}

	if (cellIndex < timeStartCellIndex) {
		cellStart = cellIndex;
		cellEnd = timeStartCellIndex;
	} else {
		cellStart = timeStartCellIndex;
		cellEnd = cellIndex;
	}               

	for (var i = rowStart; i <= rowEnd; i++) {
		var rowCells = timeTable.find("tr").eq(i).find("td");
		for (var j = cellStart; j <= cellEnd; j++) {
			rowCells.eq(j).addClass("selected");
			timeTableIds.splice(rowCells.eq(j).attr('id').substr(2),1,1);
		}        
	}
}

  
function ttSelectTo2(cell) {

	var row = cell.parent();    
	var cellIndex = cell.index() - 1;
	var rowIndex = row.index();

	var rowStart, rowEnd, cellStart, cellEnd;

	if (rowIndex < timeStartRowIndex) {
		rowStart = rowIndex;
		rowEnd = timeStartRowIndex;
	} else {
		rowStart = timeStartRowIndex;
		rowEnd = rowIndex;
	}

	if (cellIndex < timeStartCellIndex) {
		cellStart = cellIndex;
		cellEnd = timeStartCellIndex;
	} else {
		cellStart = timeStartCellIndex;
		cellEnd = cellIndex;
	}        

	for (var i = rowStart; i <= rowEnd; i++) {
		var rowCells = timeTable.find("tr").eq(i).find("td");
		for (var j = cellStart; j <= cellEnd; j++) {
			rowCells.eq(j).removeClass("rselected");
			rowCells.eq(j).addClass("nselected");
			timeTableIds.splice(rowCells.eq(j).attr('id').substr(2),1,0);
		}        
	}
}


function ttShowValues() {

	for(var i = 0; i < timeTableIds.length; i++) {
		if (timeTableIds[i] == 1) {
			$("#tt" + i).addClass("selected");
		} else {
			$("#tt" + i).removeClass("selected rselected");
		}
	}
}


function ttGetValueStr() {

	// 가져가기 전 UI의 class와 모델의 값을 일치
	for(var i = 0; i < 168; i++) {
		if ($("#tt" + i).hasClass('rselected') || $("#tt" + i).hasClass('nzselected')) {
			timeTableIds[i] = 1;
		} else {
			timeTableIds[i] = 0;
		}
	}
	
	var s = "";
	for(var i = 0; i < timeTableIds.length; i++) {
		s += timeTableIds[i];
	}

	return s;
}


function ttSetValueStr(val, display) {

	if (val.length == 168) {
		for(var i = 0; i < 168; i++) {
			timeTableIds[i] = Number(val.substr(i, 1));
		}
		
		if (display) {
			for(var i = 0; i < timeTableIds.length; i++) {
				$("#tt" + i).removeClass("selected rselected nselected");
				if (timeTableIds[i] == 1) {
					$("#tt" + i).addClass("rselected");
				}
			}
		}
	}
}


$(document).mouseup(function () {
	
	isTimeMouseDown = false;
	isTimeMouseDown2 = false;
	
	timeTable.find(".selected").addClass("rselected");
	timeTable.find(".selected").removeClass("selected");
	timeTable.find(".nselected").removeClass("nselected");
	
	if (typeof mouseUpAct != "undefined") {
		mouseUpAct();
	}
});

</script>
