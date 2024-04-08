package it.andreascanzani.example.springboot.saml2;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2LogoutResponseFilter;
import org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2RelyingPartyInitiatedLogoutSuccessHandler;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.*;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import java.lang.reflect.Field;
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

                    RequestMatcher genericSamlMatcher = new RegexRequestMatcher(".*/saml2/.*", null);
                    RequestMatcher samlLogoutMatcher = new RegexRequestMatcher("/logout", null);
                    RequestMatcher combinedSamlMatcher = new OrRequestMatcher(genericSamlMatcher, samlLogoutMatcher);

                    SAMLDefaultSecurityFilterChain = new DefaultSecurityFilterChain(combinedSamlMatcher,
                            defaultSecurityFilterChain.getFilters());
                    break;
                }
            }
        }

        setCMFInMemoryLogoutRequestRepository();

    }

    // Getters for the filters or additional methods
    public static DefaultSecurityFilterChain getSAMLDefaultSecurityFilterChain() {
        return SAMLDefaultSecurityFilterChain;
    }

    public void modifyLogoutFilterMatcher(LogoutFilter logoutFilter) throws NoSuchFieldException, IllegalAccessException{

        // Access the current logoutRequestMatcher
        Field logoutRequestMatcherField = LogoutFilter.class.getDeclaredField("logoutRequestMatcher");
        logoutRequestMatcherField.setAccessible(true);
        RequestMatcher currentLogoutRequestMatcher = (RequestMatcher) logoutRequestMatcherField.get(logoutFilter);

        // Create a new RequestMatcher for the GET logout URL for CM
        RequestMatcher getLogoutRequestMatcher = new AntPathRequestMatcher("/saml2/logout", "GET");

        // Combine the current and new RequestMatcher with OR logic
        RequestMatcher combinedMatcher = new OrRequestMatcher(currentLogoutRequestMatcher, getLogoutRequestMatcher);

        // Set the combined RequestMatcher back to the LogoutFilter
        logoutRequestMatcherField.set(logoutFilter, combinedMatcher);
    }

    private void setCMFInMemoryLogoutRequestRepository() {

        for(Filter filter : SAMLDefaultSecurityFilterChain.getFilters()) {
            if(filter instanceof Saml2LogoutResponseFilter) {
                ((Saml2LogoutResponseFilter) filter).setLogoutRequestRepository(new CMFInMemoryLogoutRequestRepository());
            }
            else if(filter instanceof LogoutFilter) {

                try {
                    Field logoutSuccessHandlerField = LogoutFilter.class.getDeclaredField("logoutSuccessHandler");
                    logoutSuccessHandlerField.setAccessible(true);
                    LogoutSuccessHandler logoutSuccessHandler = (LogoutSuccessHandler) logoutSuccessHandlerField.get(filter);

                    if (logoutSuccessHandler instanceof Saml2RelyingPartyInitiatedLogoutSuccessHandler) {
                        Saml2RelyingPartyInitiatedLogoutSuccessHandler saml2LogoutSuccessHandler = (Saml2RelyingPartyInitiatedLogoutSuccessHandler) logoutSuccessHandler;
                        saml2LogoutSuccessHandler.setLogoutRequestRepository(new CMFInMemoryLogoutRequestRepository());

                        modifyLogoutFilterMatcher((LogoutFilter)filter);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
