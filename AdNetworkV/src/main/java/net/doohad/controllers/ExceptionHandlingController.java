package net.doohad.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import net.doohad.exceptions.ServerOperationForbiddenException;

/**
 * 예외 처리 컨트롤러
 */
@ControllerAdvice
public class ExceptionHandlingController {
	
	/**
	 * 서버 예외 처리
	 */
    @ResponseStatus(value=HttpStatus.FORBIDDEN)
	@ExceptionHandler(ServerOperationForbiddenException.class)
	public @ResponseBody ModelAndView handleError(HttpServletRequest req, Exception exception) {
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		
		return new ModelAndView(jsonView, "error", exception.getMessage());
	}

}
