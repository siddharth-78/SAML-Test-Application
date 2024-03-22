//package it.andreascanzani.example.springboot.saml2;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
//import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Collections;
//import java.util.List;
//
//public class SAML2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        if (authentication instanceof Saml2Authentication) {
//            // Example modification: adding a custom attribute
//            // Note: This is just a conceptual example. You might want to enrich the authentication
//            // with actual meaningful modifications based on your application's requirements.
//
//            // Cast to Saml2Authentication if needed to access specific methods
//            Saml2Authentication saml2Authentication = (Saml2Authentication) authentication;
//
//            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
//            UserDetails userDetails = new User("Itachi Uchiha", "", authorities);
//
//            DefaultSaml2AuthenticatedPrincipal principal = new DefaultSaml2AuthenticatedPrincipal("Itachi Uchiha", Collections.emptyMap());
////            saml2Authentication = new Saml2Authentication((DefaultSaml2AuthenticatedPrincipal)saml2Authentication.getPrincipal(),
////                    saml2Authentication.getSaml2Response(), userDetails.getAuthorities());
//            saml2Authentication = new Saml2Authentication(principal, saml2Authentication.getSaml2Response(), userDetails.getAuthorities());
//
//
//            // Assuming you want to modify or replace the Saml2Authentication object,
//            // for example, to add custom authorities or details.
//            // You would typically fetch additional details here and create a new Authentication object.
//
//            // Set the updated authentication object to the security context
//            SecurityContextHolder.getContext().setAuthentication(saml2Authentication);
//
//            // Since we're not redirecting the user here, Spring Security's default success handler
//            // will proceed with the redirection to the default URL or the one saved in the session.
//            response.sendRedirect("/");
//        }
//    }
//}
