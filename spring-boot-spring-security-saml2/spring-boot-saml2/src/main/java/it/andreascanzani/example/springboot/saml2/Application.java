package it.andreascanzani.example.springboot.saml2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.Saml2WebSsoAuthenticationRequestFilter;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.Filter;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Controller
public class Application {

	public static ApplicationContext ctx;
	public static void main(String[] args) throws InterruptedException {
		ctx = SpringApplication.run(Application.class, args);
		System.out.println("TopG");
		System.out.println("Trial Over");

		RelyingPartyRegistrationRepository relyingPartyRegistrationRepository = ctx.getBean(RelyingPartyRegistrationRepository.class);

		FilterChainProxy filterChainProxy = ctx.getBean(FilterChainProxy.class);
		try {
			Field filterChainField = FilterChainProxy.class.getDeclaredField("filterChains");
			filterChainField.setAccessible(true);

			List<DefaultSecurityFilterChain> existingFilterChains =
					new ArrayList<>((List<DefaultSecurityFilterChain>) filterChainField.get(filterChainProxy));

			existingFilterChains.add(0, FilterAccess.getSAMLDefaultSecurityFilterChain());
			// existingFilterChains.remove(1); // DANGER

			filterChainField.set(filterChainProxy, existingFilterChains);

		}
		catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	private static void removeTheFilters(List<Filter> filter) {

		for(int i = 0; i < filter.size(); i++) {

			if(filter.get(i) instanceof Saml2WebSsoAuthenticationRequestFilter ||
			filter.get(i) instanceof Saml2WebSsoAuthenticationFilter) {
				filter.remove(i);
				i--;
			}

			System.out.println("will i work :(");

		}
	}

	@RequestMapping("/")
	public String index() {
		return "home";
	}

	@RequestMapping("/secured/hello")
	public String hello(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal, Model model) {
		model.addAttribute("name", principal.getName());
		return "hello";
	}

	// SP Initiated works with SAML disabled!
	// IDP Initiated does NOT WORK.

//	@RequestMapping("/secured/hello")
//	public String hello() {
//		//model.addAttribute("name", principal.getName());
//		return "hello";
//	}

//	@RequestMapping(value="/cmf/saml2/logout", method = RequestMethod.GET)
//	public void cmfSamlLogout(HttpServletRequest request, HttpServletResponse response) {
//
//		System.out.println("Itss mez");
//
//		HttpSession session = request.getSession(false);
//
//		String logoutUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
//				.path("/logout")
//				.build()
//				.toUriString();
//
//		// Create an instance of RestTemplate
//		RestTemplate restTemplate = new RestTemplate();
//
//		// Create an instance of HttpHeaders
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//		// Set the session ID in the headers
//		String sessionId = session.getId();
//		headers.set("Cookie", "JSESSIONID=" + sessionId);
//
//		// Get the CSRF token from the session
//		CsrfToken csrfToken = (CsrfToken) session.getAttribute("org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN");
//
//		if (csrfToken != null) {
//			// Set the CSRF token as a form parameter
//			requestBody.add("_csrf", csrfToken.getToken());
//		}
//
//		// Create a MultiValueMap to hold the request parameters
//		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
//		// Add any necessary request parameters to the requestBody
//
//		// Create an HttpEntity with the requestBody and headers
//		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
//
//		try {
//			// Send the POST request to the /logout endpoint
//			ResponseEntity<String> responseEntity = restTemplate.postForEntity(logoutUrl, requestEntity, String.class);
//
//			// Check the response status code
//			if (responseEntity.getStatusCode().is2xxSuccessful()) {
//				// Redirect to the appropriate page after successful logout
//				response.sendRedirect("/logout-success");
//			} else {
//				// Handle the error case
//				response.sendRedirect("/logout-error");
//			}
//		} catch (Exception e) {
//			// Handle any exceptions that occurred during the request
//			System.out.println("WE messed up :/");
//		}
//	}

	@RequestMapping(value="/cmf/saml2/logout", method = RequestMethod.GET)
	public void cmfSamlLogout(HttpServletRequest request, HttpServletResponse response) throws Exception {

		RequestDispatcher dispatcher = request.getRequestDispatcher("/logout");
		dispatcher.forward(request, response);
	}

	@RequestMapping(value="/logout", method = RequestMethod.POST)
	public void handleInternalLogout(HttpServletRequest request, HttpServletResponse response) {
		// Here, implement the logic that you would have executed on receiving a direct POST request to /logout

		// Since this is an internal forward, the original security context is preserved
	}


}
