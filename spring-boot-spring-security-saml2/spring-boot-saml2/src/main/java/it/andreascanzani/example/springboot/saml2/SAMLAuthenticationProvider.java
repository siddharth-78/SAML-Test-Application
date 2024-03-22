//package it.andreascanzani.example.springboot.saml2;
//
//import org.opensaml.saml.saml2.core.Response;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.saml2.provider.service.authentication.*;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.List;
//
//
// @Component
//public class SAMLAuthenticationProvider implements AuthenticationProvider {
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//
//        if (!supports(authentication.getClass())) {
//            return null;
//        }
//
//        Saml2AuthenticationToken token = (Saml2AuthenticationToken) authentication;
//        String saml2Response = token.getSaml2Response();
//
//        OpenSamlAuthenticationProvider delegate = new OpenSamlAuthenticationProvider();
//        Authentication processedAuthentication = delegate.authenticate(authentication);
//
//        // Customizing the UserDetails instance
//        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
//        UserDetails userDetails = new User("Itachi Uchiha", "", authorities);
//
//        // Constructing the authentication object with a custom principal
//        Saml2Authentication customAuthentication = new Saml2Authentication(
//                (DefaultSaml2AuthenticatedPrincipal)processedAuthentication.getPrincipal(),
//                saml2Response, userDetails.getAuthorities());
//
//        return customAuthentication;
//
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return Saml2AuthenticationToken.class.isAssignableFrom(authentication);
//    }
//
////    private UserDetails loadUserBySAML(Saml2AuthenticationToken token) {
////        // Assume it has correct implementaion for now.
////        return new User("username", "password",
////                true, true, true, true, null);
////    }
//}
//
