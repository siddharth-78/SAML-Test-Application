package it.andreascanzani.example.springboot.saml2;

import com.sun.istack.internal.NotNull;
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
import org.springframework.security.web.SecurityFilterChain;
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

@SpringBootApplication
@Controller
public class Application {

	public static ApplicationContext ctx;
	public static void main(String[] args) {
		ctx = SpringApplication.run(Application.class, args);

		System.out.println("Trial Code");
		// i want to access a bean here
		RelyingPartyRegistrationRepository relyingPartyRegistrationRepository =
				ctx.getBean(RelyingPartyRegistrationRepository.class);



		List<Filter> filtersToAdd = new ArrayList<>();

		Saml2WebSsoAuthenticationRequestFilter t = SAMLFilterAccessComponent.findSaml2WebSsoAuthenticationRequestFilter();

		Converter<HttpServletRequest, RelyingPartyRegistration> relyingPartyRegistrationResolver =
				new DefaultRelyingPartyRegistrationResolver(relyingPartyRegistrationRepository);
		Saml2MetadataFilter saml2MetadataFilter = new Saml2MetadataFilter(relyingPartyRegistrationResolver, new OpenSamlMetadataResolver());

		Saml2WebSsoAuthenticationFilter x = SAMLFilterAccessComponent.findSaml2WebSsoAuthenticationFilter();

		filtersToAdd.add(t);
		filtersToAdd.add(saml2MetadataFilter);
		filtersToAdd.add(x);


		DefaultSecurityFilterChain defaultSecurityFilterChain =
				new DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE,filtersToAdd);

		FilterChainProxy fcp = (FilterChainProxy) ctx.getBean(FilterChainProxy.class);

		try {
			Field filterChainField = FilterChainProxy.class.getDeclaredField("filterChains");
			filterChainField.setAccessible(true);

			// Assuming fcp is your FilterChainProxy instance
			List<SecurityFilterChain> existingFilterChains = new ArrayList<>((List<SecurityFilterChain>) filterChainField.get(fcp));
			existingFilterChains.add(defaultSecurityFilterChain);

			filterChainField.set(fcp, existingFilterChains); // Set the modified list back
		}
		catch (NoSuchFieldException | IllegalAccessException e) {
			//LOG.error("Failed while trying to modify the List<Filters> of FilterChainProxy");
			e.printStackTrace();
		}

		System.out.println("Trial Over");
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
