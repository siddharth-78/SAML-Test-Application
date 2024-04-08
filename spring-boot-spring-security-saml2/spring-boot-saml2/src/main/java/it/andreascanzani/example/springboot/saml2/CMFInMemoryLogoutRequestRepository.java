package it.andreascanzani.example.springboot.saml2;

import org.springframework.security.saml2.provider.service.authentication.logout.Saml2LogoutRequest;
import org.springframework.security.saml2.provider.service.web.authentication.logout.HttpSessionLogoutRequestRepository;
import org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2LogoutRequestRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CMFInMemoryLogoutRequestRepository implements Saml2LogoutRequestRepository {

    private static Saml2LogoutRequest storeLogoutRequest;
    //private Saml2LogoutRequestRepository defaultLogoutRequestRepository = new HttpSessionLogoutRequestRepository();
    @Override
    public Saml2LogoutRequest loadLogoutRequest(HttpServletRequest request) {
        System.out.println("Custom Load method");
        //defaultLogoutRequestRepository.loadLogoutRequest(request);
        return storeLogoutRequest;
    }

    @Override
    public void saveLogoutRequest(Saml2LogoutRequest logoutRequest, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Custom Save method");
       // defaultLogoutRequestRepository.saveLogoutRequest(logoutRequest, request, response);
        storeLogoutRequest = logoutRequest;

    }

    @Override
    public Saml2LogoutRequest removeLogoutRequest(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Custom Remove method");
       // defaultLogoutRequestRepository.removeLogoutRequest(request, response);
        return loadLogoutRequest(request);
    }
}
