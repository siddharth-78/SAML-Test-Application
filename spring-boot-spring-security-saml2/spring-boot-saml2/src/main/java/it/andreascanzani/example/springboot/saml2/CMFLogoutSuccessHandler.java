package it.andreascanzani.example.springboot.saml2;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CMFLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler  {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        System.out.println("MR BOW BOW");
    }
}
