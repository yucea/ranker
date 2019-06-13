package kr.co.esjee.ranker.crawler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.Cookie;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NaverLoginTask {

	public static Map<String, String> cookies;
	private static final String URL_LOGIN = "https://nid.naver.com/nidlogin.login";

	private boolean isLogin;
	private WebClient webClient;
	private HtmlPage currPage;

	public NaverLoginTask() {
		
		try {
			String id = "";
			String pw = "";

			webClient = new WebClient(BrowserVersion.CHROME);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setCssEnabled(false);
			
			if (!login(id, pw)) {
				isLogin = false;
				throw new Exception("cannot login with the id and pw");
			} else {
				isLogin = true;
			}
		} catch (Exception e) {
			log.warn("login fail : " + e.getLocalizedMessage());
		}
   	}

	public Map<String, String> getCookies() {
		return makeLoginCookie();
	}

	public boolean isLogin() {
		return isLogin;
	}

	private Map<String, String> makeLoginCookie() {
		cookies = new ConcurrentHashMap<String, String>();
		CookieManager cookieManager = webClient.getCookieManager();
		java.util.Set<Cookie> cookieSet = cookieManager.getCookies();
		
		for (Cookie c : cookieSet) {
			cookies.put(c.getName(), c.getValue());
		}
		
		return cookies;
	}

	private boolean login(String naverId, String naverPw) throws Exception {		
		currPage = webClient.getPage(URL_LOGIN);
		
		HtmlForm form = currPage.getFormByName("frmNIDLogin");
		
		HtmlTextInput inputId = form.getInputByName("id");
		HtmlPasswordInput inputPw = (HtmlPasswordInput) form.getInputByName("pw");
		Object button = form.getByXPath("//*[@id=\"frmNIDLogin\"]/fieldset/input").get(0);
		
		inputId.setValueAttribute(naverId);
		inputPw.setValueAttribute(naverPw);
		
		currPage = (HtmlPage) ((DomElement) button).click();
		
		return !currPage.asText().contains("Naver Sign in");
	}
}