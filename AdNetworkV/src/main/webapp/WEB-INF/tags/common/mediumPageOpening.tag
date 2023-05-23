<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!DOCTYPE html>
<html lang="${html_lang}" class="default-style layout-navbar-fixed">
	<common:head />
	<body>
		<div class="page-loader">
			<div class="bg-primary"></div>
		</div>
        

		<!-- Layout wrapper -->
<c:choose>
<c:when test="${menuTotCnt gt 1}">
		<div class="layout-wrapper layout-2">
</c:when>
<c:otherwise>
		<div class="layout-wrapper layout-1">
</c:otherwise>
</c:choose>
			<div class="layout-inner">

<c:if test="${menuTotCnt gt 1}">

				<!-- Layout sidenav -->
				<div id="layout-sidenav" class="layout-sidenav sidenav sidenav-vertical bg-sidenav-theme">

					<!-- Brand -->
					<div class="app-brand logo">
						<span class="app-brand-logo logo-frame">
							<img src="${logoTopPathFile}" alt>
						</span>
						<a href="/userhome" class="app-brand-text logo-text sidenav-text font-weight-normal ml-2">
							${logoTitleText}
						</a>
						<a href="javascript:void(0)" class="layout-sidenav-toggle sidenav-link text-large ml-auto">
							<i class="fas fa-bars"></i>
						</a>
					</div>

					<!-- Topmost divider -->
					<div class="sidenav-divider mt-0 mb-2"></div>

					<!-- Links -->
					<ul class="sidenav-inner py-1">


<c:if test="${fn:length(main_quick_link) > 0 }">

						<!--  Quick link menus -->
						
	<c:forEach var="item" items="${main_quick_link}">
	
						<li class="${item.bootstrapTreeClassLI}">
							<a
		<c:choose>
			<c:when test="${empty item.custom2}">
								href="javascript:void(0)"
			</c:when>
			<c:otherwise>
								href="${item.custom2}"
			</c:otherwise>
		</c:choose>
								class="sidenav-link"
							>
								<i class="sidenav-icon ${item.icon}"></i>
								<div>${item.text}</div>
							</a>
						</li>
	</c:forEach>
	
	<c:if test="${fn:length(main_quick_link) < fn:length(main_menu_data) and not isOnlyQuickLinkMode}">
						<li class="sidenav-divider mb-1"></li>
	</c:if>
						<!--  / Quick link menus -->
</c:if>


<c:if test="${not isOnlyQuickLinkMode}">
<c:forEach var="item" items="${main_menu_data}">
	<c:if test="${item.childrenCount gt 0 or empty item.custom2}">

						<li class="${item.bootstrapTreeClassLI}">
							<a
		<c:choose>
			<c:when test="${empty item.custom2}">
								href="javascript:void(0)"
			</c:when>
			<c:otherwise>
								href="${item.custom2}"
			</c:otherwise>
		</c:choose>
								class="${item.bootstrapTreeClassA}"
							>
								<i class="sidenav-icon ${item.icon}"></i>
								<div>
									${item.text}
								</div>
							</a>
							
		<c:if test="${item.childrenCount gt 0}">
							<ul class="sidenav-menu">
							
			<c:forEach var="sub1" items="${item.items}">
								<li class="${sub1.bootstrapTreeClassLI}">
									<a
				<c:choose>
					<c:when test="${empty sub1.custom2}">
										href="javascript:void(0)"
					</c:when>
					<c:otherwise>
										href="${sub1.custom2}"
					</c:otherwise>
				</c:choose>
										class="${sub1.bootstrapTreeClassA}"
									>
										<div>
											${sub1.text}
										</div>
									</a>

				<c:if test="${sub1.childrenCount gt 0}">
									<ul class="sidenav-menu">

					<c:forEach var="sub2" items="${sub1.items}">
										<li class="${sub2.bootstrapTreeClassLI}">
											<a
						<c:choose>
							<c:when test="${empty sub2.custom2}">
												href="javascript:void(0)"
							</c:when>
							<c:otherwise>
												href="${sub2.custom2}"
							</c:otherwise>
						</c:choose>
												class="sidenav-link"
											>
												<div>
													${sub2.text}
												</div>
											</a>
										</li>
					</c:forEach>
									</ul>
    		
				</c:if>
								</li>
			</c:forEach>
							</ul>
		</c:if>
						</li>
	</c:if>
</c:forEach>
</c:if>

					</ul>
				</div>
				<!-- / Layout sidenav -->

</c:if>

				<!-- Layout container -->
				<div class="layout-container">

					<!-- Layout navbar -->
					<nav class="layout-navbar navbar navbar-expand align-items-center container-p-x bg-navbar-theme" id="layout-navbar">

<c:choose>
<c:when test="${menuTotCnt gt 1}">
						<!-- Brand -->
						<div class="d-none d-sm-block">
							<div class="d-flex align-items-center navbar-brand app-brand logo d-lg-none py-0 mr-4">
								<span class="app-brand-logo">
									<img src="${logoTopPathFile}" alt>
								</span>
								<a href="/userhome" class="app-brand-text logo-text text-white font-weight-normal ml-2">
									${logoTitleText}
								</a>
							</div>
						</div>

						<!-- Sidenav toggle -->
						<div class="layout-sidenav-toggle navbar-nav d-lg-none align-items-lg-center mr-auto">
							<a class="nav-item nav-link px-0 mr-lg-4" href="javascript:void(0)">
								<i class="fas fa-bars fa-lg"></i>
							</a>
						</div>
</c:when>
<c:otherwise>
						<!-- Brand -->
						<div class="d-none d-sm-block">
							<div class="navbar-brand app-brand logo d-lg-none py-0 mr-4"">
								<span class="app-brand-logo">
									<img src="${logoTopPathFile}" alt>
								</span>
								<a href="/userhome" class="app-brand-text logo-text text-white font-weight-normal ml-2">
									${logoTitleText}
								</a>
							</div>
						</div>

						<!-- Sidenav toggle -->
</c:otherwise>
</c:choose>

						<div class="navbar-nav align-items-md-center ml-auto">
							<div class="navbar-user nav-item dropdown">
								<a class="nav-link dropdown-toggle" href="#" data-toggle="dropdown">
									<span class="d-inline-flex flex-row-reverse align-items-center align-middle">
										<span class="px-1 mr-1 ml-2 ml-0 user-info">
											<c:out value="${sessionScope['loginUser'].shortName}"></c:out> ( <c:out value="${sessionScope['loginUser'].name}"></c:out> )
										</span>
									</span>
								</a>
							
								<div class="dropdown-menu dropdown-menu-right">
								
									<h6 class="dropdown-header">version<span class="pl-3">${server_version}</span></h6>
									<div class="dropdown-divider"></div>
									
									<a href="javascript:void(0)" class="dropdown-item" id="pwd-update-btn">
										<i class="fa-regular fa-key text-light"></i> &nbsp; 패스워드 변경</a>
									<div class="dropdown-divider"></div>
									<a href="javascript:void(0)" class="dropdown-item" id="logout-btn">
										<i class="fa-regular fa-sign-out-alt text-danger"></i> &nbsp; 로그아웃</a>
								</div>
							</div>
						</div>
					</nav>
					<!-- / Layout navbar -->

					<!-- Layout content -->
					<div class="layout-content">

						<!-- Content -->
						<div class="container-fluid flex-grow-1 pb-3 pt-2">
							<div class="d-flex align-items-center justify-content-end" style="min-height: 26px;">
							
<c:if test="${not empty sessionScope['loginUser'] && sessionScope['loginUser'].mediumSwitcherAvailable}">

								<!--  Medium switcher -->
								<div class="btn-group">
									<button class="btn btn-default btn-sm dropdown-toggle" type="button" data-toggle="dropdown">
										<span class="fa-regular fa-earth-asia"></span>
										<span class="pl-1" id="curr-medium-short-name">${sessionScope['loginUser'].dispName}</span>
									</button>
									<div class="dropdown-menu scrollable-dropdown-menu">
									
<c:forEach var="item" items="${sessionScope['loginUser'].availMediumList}">
	<c:choose>
		<c:when test="${sessionScope['currMediumId'] eq item.id}">

										<a class="dropdown-item active" href="javascript:changeMedium(${item.id},'${item.shortName}')">
											<span class="fa-regular fa-earth-asia"></span>
											<span class="pl-1">${item.shortName}</span>
											<span class='small pl-3'>${item.name}</span>
										</a>

		</c:when>
		<c:otherwise>

										<a class="dropdown-item" href="javascript:changeMedium('${item.id}','${item.shortName}')">
											<span class="fa-regular fa-earth-asia"></span>
											<span class="pl-1">${item.shortName}</span>
											<span class='small text-muted pl-3'>${item.name}</span>
										</a>

		</c:otherwise>
	</c:choose>
</c:forEach>
				
									</div>
								</div>
								<!--  / Site switcher -->

</c:if>

							
<c:if test="${isViewSwitcherMode && not empty sessionScope['loginUser'] && sessionScope['loginUser'].viewSwitcherShown}">

								<!--  View switcher -->
								<div class="btn-group pl-2">
									<button class="btn btn-default btn-sm dropdown-toggle" type="button" data-toggle="dropdown">
										<span class="fas fa-filter"></span>
										<span class="pl-1" id="curr-view-name">${sessionScope['loginUser'].dispViewName}</span>
									</button>
									<div class="dropdown-menu scrollable-dropdown-menu">
									
<c:forEach var="item" items="${sessionScope['loginUser'].userViews}">
	<c:choose>
		<c:when test="${sessionScope['currentViewId'] eq item.value}">

										<a class="dropdown-item active" href="javascript:changeView('${item.value}','${item.text}')">
											<span class="${item.typeImageCssKeyword}" style="margin-left:${item.indentWidth}px"></span>
											<span class="pl-1">${item.text}</span>
										</a>

		</c:when>
		<c:otherwise>

										<a class="dropdown-item" href="javascript:changeView('${item.value}','${item.text}')">
											<span class="${item.typeImageCssKeyword}" style="margin-left:${item.indentWidth}px"></span>
											<span class="pl-1">${item.text}</span>
										</a>

		</c:otherwise>
	</c:choose>
</c:forEach>
				
									</div>
								</div>
								<!--  / View switcher -->

</c:if>

							</div>

<!--  Scripts -->

<script>

function changeMedium(id, name) {
	
	$("#curr-medium-short-name").text(name);
	
	showWaitModal();
	location.href = "/changemedium?mediumId=" + id + "&uri=" + window.location.pathname;
}


function changeView(id, name) {
	
	$("#curr-view-name").text(name);
	
	showWaitModal();
	location.href = "/changeview?viewId=" + id + "&uri=" + window.location.pathname;
}

</script>

<!--  / Scripts -->
