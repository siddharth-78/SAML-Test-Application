package it.andreascanzani.example.springboot.saml2;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import java.util.List;

@Component
@DependsOn("springSecurityFilterChain") // This ensures that this bean is initialized after the filter chain
@Order(Ordered.HIGHEST_PRECEDENCE) // This is to ensure that this component is one of the first to be initialized
public class FilterAccess implements InitializingBean {

    @Autowired
    private FilterChainProxy filterChainProxy;

    // Instance variables to hold your filters
//    private Saml2WebSsoAuthenticationRequestFilter saml2WebSsoAuthenticationRequestFilter;
//    private Saml2WebSsoAuthenticationFilter saml2WebSsoAuthenticationFilter;

//    @Override
//    public void afterPropertiesSet() {
//        // This method is called after the bean is initialized
//        List<Filter> filters = filterChainProxy.getFilters("/"); // Assuming the default filter chain
//        for (Filter filter : filters) {
//            if (filter instanceof Saml2WebSsoAuthenticationRequestFilter) {
//                saml2WebSsoAuthenticationRequestFilter = (Saml2WebSsoAuthenticationRequestFilter) filter;
//            } else if (filter instanceof Saml2WebSsoAuthenticationFilter) {
//                saml2WebSsoAuthenticationFilter = (Saml2WebSsoAuthenticationFilter) filter;
//            }
//        }
//
//        // At this point, you have your filters stored in the instance variables
//        // You can now do whatever you need with them
//    }

    private static DefaultSecurityFilterChain SAMLDefaultSecurityFilterChain;
    @Override
    public void afterPropertiesSet() {
        // This method is called after the bean is initialized

        List<SecurityFilterChain> currentSecurityFilterChainList = filterChainProxy.getFilterChains();
        for(SecurityFilterChain securityFilterChain : currentSecurityFilterChainList) {
            if(securityFilterChain instanceof  DefaultSecurityFilterChain) {
                DefaultSecurityFilterChain defaultSecurityFilterChain = (DefaultSecurityFilterChain)securityFilterChain;

                if(defaultSecurityFilterChain.getRequestMatcher() instanceof AnyRequestMatcher) {
                    System.out.println("YEP! I WORK");
                    SAMLDefaultSecurityFilterChain = new DefaultSecurityFilterChain(new RegexRequestMatcher(".*/saml2/.*", null),
                            defaultSecurityFilterChain.getFilters());
                    break;
                }
            }
        }

    }

    // Getters for the filters or additional methods
    public static DefaultSecurityFilterChain getSAMLDefaultSecurityFilterChain() {
        return SAMLDefaultSecurityFilterChain;
    }
}
