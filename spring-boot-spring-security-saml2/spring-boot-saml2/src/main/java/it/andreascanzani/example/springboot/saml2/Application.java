package it.andreascanzani.example.springboot.saml2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.convert.converter.Converter;
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
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.ApplicationContext;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
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
		System.out.println("Trial Over");

		// TimeUnit.SECONDS.sleep(30);
		RelyingPartyRegistrationRepository relyingPartyRegistrationRepository = ctx.getBean(RelyingPartyRegistrationRepository.class);

		// Get the 3 Filters Required
		Saml2WebSsoAuthenticationRequestFilter saml2WebSsoAuthenticationRequestFilter =
			//new Saml2WebSsoAuthenticationRequestFilter(relyingPartyRegistrationRepository);
		SAMLFilterAccessComponent.findSaml2WebSsoAuthenticationRequestFilter();

		Converter<HttpServletRequest, RelyingPartyRegistration> relyingPartyRegistrationResolver = new DefaultRelyingPartyRegistrationResolver(relyingPartyRegistrationRepository);
		Saml2MetadataFilter saml2MetadataFilter = new Saml2MetadataFilter(relyingPartyRegistrationResolver, new OpenSamlMetadataResolver());

		Saml2WebSsoAuthenticationFilter saml2WebSsoAuthenticationFilter =
				//new Saml2WebSsoAuthenticationFilter(relyingPartyRegistrationRepository);
				SAMLFilterAccessComponent.findSaml2WebSsoAuthenticationFilter();


		FilterChainProxy filterChainProxy = ctx.getBean(FilterChainProxy.class);

		try {
			Field filterChainField = FilterChainProxy.class.getDeclaredField("filterChains");
			filterChainField.setAccessible(true);

			List<DefaultSecurityFilterChain> existingFilterChains =
					new ArrayList<>((List<DefaultSecurityFilterChain>) filterChainField.get(filterChainProxy));

			for(int i = 0; i < existingFilterChains.size(); i++) {

				DefaultSecurityFilterChain chain = existingFilterChains.get(i);
				if(chain.getRequestMatcher() instanceof AnyRequestMatcher) {
					List<Filter> updatedFilters = new ArrayList<>(chain.getFilters());

					removeTheFilters(updatedFilters);

					updatedFilters.add(5, saml2WebSsoAuthenticationRequestFilter);
					updatedFilters.add(6, saml2MetadataFilter);
					updatedFilters.add(7, saml2WebSsoAuthenticationFilter);

					DefaultSecurityFilterChain updatedFilterChain =
							new DefaultSecurityFilterChain(chain.getRequestMatcher(), updatedFilters);
					existingFilterChains.set(i, updatedFilterChain);
					break;
				}
			}

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

}
