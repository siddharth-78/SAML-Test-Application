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

    public static Filter findSaml2WebSsoAuthenticationRequestFilter() {
        FilterChainProxy filterChainProxy = ctx.getBean(FilterChainProxy.class);
        for (SecurityFilterChain chain : filterChainProxy.getFilterChains()) {
            for (Filter filter : chain.getFilters()) {
                if (filter.getClass().getName().contains("Saml2WebSsoAuthenticationRequestFilter")) {
                    return filter;
                }
            }
        }
        return null; // Filter not found
    }

    public static Filter findSaml2WebSsoAuthenticationFilter() {
        FilterChainProxy filterChainProxy = ctx.getBean(FilterChainProxy.class);
        for (SecurityFilterChain chain : filterChainProxy.getFilterChains()) {
            for (Filter filter : chain.getFilters()) {
                if (filter.getClass().getName().contains("Saml2WebSsoAuthenticationFilter")) {
                    return filter;
                }
            }
        }
        return null; // Filter not found
    }
}
