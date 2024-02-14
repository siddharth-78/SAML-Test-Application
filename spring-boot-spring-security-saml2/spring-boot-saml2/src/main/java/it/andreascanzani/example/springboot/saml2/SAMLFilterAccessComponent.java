package it.andreascanzani.example.springboot.saml2;

import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationRequestFilter;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationFilter;

import static it.andreascanzani.example.springboot.saml2.Application.ctx;

public class SAMLFilterAccessComponent {

    public static Saml2WebSsoAuthenticationRequestFilter findSaml2WebSsoAuthenticationRequestFilter() {
        FilterChainProxy filterChainProxy = ctx.getBean(FilterChainProxy.class);
        for (SecurityFilterChain chain : filterChainProxy.getFilterChains()) {
            for (Filter filter : chain.getFilters()) {
                if (filter instanceof Saml2WebSsoAuthenticationRequestFilter) {
                    return (Saml2WebSsoAuthenticationRequestFilter) filter;
                }
            }
        }
        return null; // Filter not found
    }

    public static Saml2WebSsoAuthenticationFilter findSaml2WebSsoAuthenticationFilter() {
        FilterChainProxy filterChainProxy = ctx.getBean(FilterChainProxy.class);
        for (SecurityFilterChain chain : filterChainProxy.getFilterChains()) {
            for (Filter filter : chain.getFilters()) {
                if (filter instanceof Saml2WebSsoAuthenticationFilter) {
                    return (Saml2WebSsoAuthenticationFilter) filter;
                }
            }
        }
        return null; // Filter not found
    }
}
