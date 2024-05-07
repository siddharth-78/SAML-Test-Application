package it.andreascanzani.example.springboot.saml2;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.saml2.provider.service.authentication.*;
import org.springframework.stereotype.Component;


 @Component
public class SAMLAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (!supports(authentication.getClass())) {
            return null;
        }

        Saml2AuthenticationToken token = (Saml2AuthenticationToken) authentication;
        String saml2Response = token.getSaml2Response();

        OpenSamlAuthenticationProvider delegate = new OpenSamlAuthenticationProvider();
        Authentication processedAuthentication = delegate.authenticate(authentication);

        /*
            My Actual application logic makes use of the processedAuthentication obect to create a UserDetails instance
            which will have all the information about the user in it.

            For the sake of simplicity we are just returning the processedAuthentication object and skipping the custom
            logic.
        * */

//        UserDetails samlUserDetails = loadUserBySAML(processedAuthentication);
//
//        Saml2Authentication customAuthentication = new Saml2Authentication(
//                (DefaultSaml2AuthenticatedPrincipal)processedAuthentication.getPrincipal(),
//                saml2Response, samlUserDetails.getAuthorities());
//
//        return customAuthentication;

        return processedAuthentication;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Saml2AuthenticationToken.class.isAssignableFrom(authentication);
    }

     /**
      * Loads and maps a user by processing a SAML authentication object. This method extracts the
      * user's identity and attributes from the SAML assertion and applies application-specific logic
      * to map these to a UserDetails object. This includes resolving the user's identity and roles
      * based on the application's configuration and the information contained in the SAML assertion.
      *
      * @param processedAuthentication the Authentication object obtained from processing the SAML response
      * @return a UserDetails object representing the authenticated user, including roles and other attributes
      */
    private UserDetails loadUserBySAML(Authentication processedAuthentication) {

        // Assume it has correct implementaion for now.
        return new User("username", "password",
                true, true, true, true, null);
    }
}

