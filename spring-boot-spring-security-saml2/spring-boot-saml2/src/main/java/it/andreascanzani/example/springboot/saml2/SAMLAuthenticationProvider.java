package it.andreascanzani.example.springboot.saml2;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


 @Component
public class SAMLAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (!supports(authentication.getClass())) {
            return null;
        }

        Saml2AuthenticationToken token = (Saml2AuthenticationToken) authentication;

        // Customizing the UserDetails instance
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User("Itachi Uchiha", "", authorities);

        // Constructing the authentication object with a custom principal
        DefaultSaml2AuthenticatedPrincipal principal = new DefaultSaml2AuthenticatedPrincipal("Itachi Uchiha", Collections.emptyMap());
        Saml2Authentication auth = new Saml2Authentication(principal, token.getSaml2Response(), userDetails.getAuthorities());

        return auth;

//        UserDetails samlUserDetails = loadUserBySAML(token);
//        return new Saml2Authentication((DefaultSaml2AuthenticatedPrincipal)(samlUserDetails),
//                token.getSaml2Response(), samlUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Saml2AuthenticationToken.class.isAssignableFrom(authentication);
    }

//    private UserDetails loadUserBySAML(Saml2AuthenticationToken token) {
//        // Assume it has correct implementaion for now.
//        return new User("username", "password",
//                true, true, true, true, null);
//    }
}

