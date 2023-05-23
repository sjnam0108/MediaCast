package net.doohad.models;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import net.doohad.info.WebAppInfo;
import net.doohad.utils.Util;

@Component
public class MessageManager {
    
	@Autowired
	private MessageSource messages;

	
	public MessageManager() { }
	
	public String message(String code, Locale locale) {
		return messages.getMessage(code, null, code, locale);
	}

	public String message(String code, Object[] args, Locale locale) {
		return messages.getMessage(code, args, code, locale);
	}
	
	public void addViewMessages(Model model, Locale locale, Message[] msgs) {
		if (msgs != null) {
			for(Message msg : msgs) {
				model.addAttribute(msg.getIdInView(), message(msg.getCode(), msg.getArgs(), locale));
			}
		}
	}
	
	public void addCommonMessages(Model model, Locale locale, HttpSession session, HttpServletRequest request) {
    	addViewMessages(model, locale,
    			new Message[] {
       				new Message("html_lang", Util.htmlLang(locale)),
    				new Message("kendoLangCountryCode", Util.kendoLangCountryCode(locale)),
    				
    				new Message("msg_delCurrentConfirm", "common.msg.delCurrentConfirm"),
    				new Message("msg_delConfirm", "common.msg.delConfirm"),
    				new Message("msg_deleteFailure", "common.msg.deleteFailure"),
    				new Message("msg_deleteSuccess", "common.msg.deleteSuccess"),
    				new Message("msg_operationFailure", "common.msg.operationFailure"),
    				new Message("msg_operationSuccess", "common.msg.operationSuccess"),
    				new Message("msg_readFailure", "common.msg.readFailure"),
    				new Message("msg_saveFailure", "common.msg.saveFailure"),
    				new Message("msg_saveSuccess", "common.msg.saveSuccess"),
    				
    				new Message("url_login", "common.server.msg.loginUrl"),
    				new Message("url_login2", "common.server.msg.loginForcedLogoutUrl"),
    				new Message("msg_sessionExpired", "common.server.msg.sessionExpired"),
    				
    				
    				new Message("cmd_add", "grid.cmd.add"),
    				new Message("cmd_delete", "grid.cmd.delete"),
    				new Message("cmd_excel", "grid.cmd.excel"),
    				new Message("cmd_edit", "grid.cmd.edit"),
    				new Message("cmd_upload", "grid.cmd.upload"),
    				new Message("cmd_import", "grid.cmd.import"),
    				new Message("cmd_addBatch", "grid.cmd.addBatch"),
    				new Message("cmd_unlock", "form.cmd.unlock"),
    				
    				new Message("grid_selectValue", "grid.selectValue"),
    				new Message("grid_customFilterInfoSelector", "grid.customFilter.info.selector"),
    				
    				new Message("tmpl_count", "template.count"),
    				
					new Message("treeview_msg_expandFirst", "treeview.msg.expandFirst"),
    				new Message("treeview_retry", "treeview.retry"),
    				new Message("treeview_requestFailed", "treeview.requestFailed"),
    				
    				new Message("filter_selectedItems", "filter.selectedItems"),

    				new Message("control_noRows", "control.noRows"),
    				new Message("control_noneResultsText", "control.noneResultsText"),

    				
    				new Message("form_add", "form.add"),
    				new Message("form_edit", "form.edit"),
    				new Message("form_cancel", "form.cancel"),
    				new Message("form_save", "form.save"),
    				new Message("form_close", "form.close"),
    				new Message("form_search", "form.search"),

    				new Message("form_startVal", "form.startVal"),
    				new Message("form_incVal", "form.incVal"),
    				new Message("form_count", "form.count"),
    				
    				new Message("form_all", "form.all"),
    				new Message("form_selectReq", "form.selectReq"),
    				new Message("form_required", "form.required"),
    				new Message("form_advOption", "form.advOption"),
    				new Message("form_option", "form.option"),

    				new Message("alert_title", "alert.title"),
    				new Message("alert_ok", "alert.ok"),
    				
    				new Message("confirm_title", "confirm.title"),
    				new Message("confirm_ok", "confirm.ok"),
    				new Message("confirm_cancel", "confirm.cancel"),
    				
    				new Message("server_version", WebAppInfo.SERVER_VERSION),
    				
    				new Message("navbar_passwordUpdate", "navbar.passwordUpdate"),
    				new Message("navbar_logout", "navbar.logout"),
    				
    				new Message("wait_plaseWait", "wait.plaseWait"),
    				new Message("wait_loading", "wait.loading"),
    			});
    	
    	model.addAttribute("pageSizesNormal", new String[] {"All", "10", "25", "50", "100", "500", "1000"});
    	
    	// 모바일에서의 접근 여부: 모바일에서의 접근 시 다른 모양이나 형태 지정이 가능토록
    	model.addAttribute("isMobileMode", !Util.isFromComputer(request));
    	
    	// 하이브리드 앱에서의 접근 코드
    	model.addAttribute("appMode", Util.getAppModeFromRequest(request));
	}
}
