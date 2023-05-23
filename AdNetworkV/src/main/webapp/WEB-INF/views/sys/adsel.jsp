<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/sys/adsel/readHourly" var="readHourlyUrl" />
<c:url value="/sys/adsel/readMinly" var="readMinlyUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>





<!-- Page body -->


<!-- Java(optional)  -->

<%
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
	String dateTemplate = net.doohad.utils.Util.getSmartDate("date", false, true);
			
	String checkDateTemplate = net.doohad.utils.Util.getSmartDate("checkDate", false, true);
	String creationDateTemplate = net.doohad.utils.Util.getSmartDate("whoCreationDate");
%>


<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#hourly">
			<i class="mr-1 fa-light fa-expand"></i>
			시간(hr) 단위
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#minutely">
			<i class="mr-1 fa-light fa-compress"></i>
			분(min) 단위
		</a>
	</li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="hourly">
	
	
	
	

<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-hourly" pageable="true" filterable="false" sortable="false" scrollable="true" resizable="true" selectable="multiple" >
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="시간" field="date" template="<%= dateTemplate %>" filterable="false" />
		<kendo:grid-column title="전체" field="count" filterable="false" template="#= countDisp #" />
		<kendo:grid-column title="${mediumTitle1}" field="sub1" filterable="false" template="#= sub1Disp #" />
		<kendo:grid-column title="${mediumTitle2}" field="sub2" filterable="false" template="#= sub2Disp #" />
		<kendo:grid-column title="${mediumTitle3}" field="sub3" filterable="false" template="#= sub3Disp #" />
		<kendo:grid-column title="${mediumTitle4}" field="sub4" filterable="false" template="#= sub4Disp #" />
		<kendo:grid-column title="${mediumTitle5}" field="sub5" filterable="false" template="#= sub5Disp #" />
		<kendo:grid-column title="기타" field="sub0" filterable="false"  template="#= sub0Disp #"/>
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readHourlyUrl}" dataType="json" type="POST" contentType="application/json"/>
			<kendo:dataSource-transport-parameterMap>
				<script>
					function parameterMap(options,type) {
						return JSON.stringify(options);	
					}
				</script>
			</kendo:dataSource-transport-parameterMap>
		</kendo:dataSource-transport>
		<kendo:dataSource-schema>
			<kendo:dataSource-schema-model id="date">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="count" type="number" />
					<kendo:dataSource-schema-model-field name="date" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->
	



	
	</div>
	<div class="tab-pane" id="minutely">
	
	
	
	

<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-minutely" pageable="true" filterable="false" sortable="false" scrollable="true" resizable="true" selectable="multiple" >
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="시간" field="date" template="<%= dateTemplate %>" filterable="false" />
		<kendo:grid-column title="전체" field="count" filterable="false" template="#= countDisp #" />
		<kendo:grid-column title="${mediumTitle1}" field="sub1" filterable="false" template="#= sub1Disp #" />
		<kendo:grid-column title="${mediumTitle2}" field="sub2" filterable="false" template="#= sub2Disp #" />
		<kendo:grid-column title="${mediumTitle3}" field="sub3" filterable="false" template="#= sub3Disp #" />
		<kendo:grid-column title="${mediumTitle4}" field="sub4" filterable="false" template="#= sub4Disp #" />
		<kendo:grid-column title="${mediumTitle5}" field="sub5" filterable="false" template="#= sub5Disp #" />
		<kendo:grid-column title="기타" field="sub0" filterable="false"  template="#= sub0Disp #"/>
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readMinlyUrl}" dataType="json" type="POST" contentType="application/json"/>
			<kendo:dataSource-transport-parameterMap>
				<script>
					function parameterMap(options,type) {
						return JSON.stringify(options);	
					}
				</script>
			</kendo:dataSource-transport-parameterMap>
		</kendo:dataSource-transport>
		<kendo:dataSource-schema>
			<kendo:dataSource-schema-model id="date">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="count" type="number" />
					<kendo:dataSource-schema-model-field name="date" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->
	



	
	</div>
</div>


<!--  Scripts -->

<script>
$(document).ready(function() {


	
});	
</script>


<script>


</script>

<!--  / Scripts -->


<!--  Forms -->


<style>

/* 선택 체크박스를 포함하는 필터 패널을 보기 좋게 */
.k-filter-selected-items {
	font-weight: 500;
	margin: 0.5em 0;
}
.k-filter-menu .k-button {
	width: 47%;
	margin: 0.5em 1% 0.25em;
}


/* Vertical Scrollbar 삭제 */
.k-grid .k-grid-header
{
   padding: 0 !important;
}
.k-grid .k-grid-content
{
   overflow-y: visible;
   min-height: 90px;
}


/* 그리드 행의 높이 지정 */
.k-grid tbody tr, .k-grid tbody tr td
{
    height: 40px;
}


/* 글자 크게 */
.large-text {
	font-size: 150%;
}


/* 글자 희미하게 */
.text-dim {
	color: rgba(24,28,33,0.1);
}


/* 글자 가늘게 */
.weight-300 {
	font-weight: 300;
}

</style>


<!--  / Forms -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
