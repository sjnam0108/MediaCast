<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


						</div>
						<!-- / Content -->

						<!-- Layout footer -->
						<nav class="layout-footer footer bg-footer-theme">
							<div class="container-fluid d-flex flex-wrap justify-content-end" style="height:50px;">
								<div class="align-self-center">
									<span class="footer-text small">Copyright 2022-2023 Ad Net. All Rights Reserved.</span>
<c:if test="${requestScope.watermarkDisplayed}">
									<img src="/resources/shared/images/logo/logo_app.png" class="ml-3 d-none d-sm-inline-block" alt/>
</c:if>
								</div>
							</div>
						</nav>
						<!-- / Layout footer -->

					</div>
					<!-- Layout content -->

				</div>
				<!-- / Layout container -->

			</div>

			<!-- Overlay -->
			<div class="layout-overlay layout-sidenav-toggle"></div>
			
		</div>
		<!-- / Layout wrapper -->

		<!-- Core scripts -->
		<script src="/resources/vendor/lib/popper/popper.js"></script>
		<script src="/resources/vendor/js/bootstrap.js"></script>
		<script src="/resources/vendor/js/sidenav.js"></script>

		<!-- Libs -->
		<script src="/resources/vendor/lib/perfect-scrollbar/perfect-scrollbar.js"></script>
		<script src="/resources/vendor/lib/toastr/toastr.js"></script>
		<script src="/resources/vendor/lib/bootbox/bootbox.js"></script>
		<script src="/resources/vendor/lib/bootstrap-select/bootstrap-select.js"></script>
	</body>
</html>