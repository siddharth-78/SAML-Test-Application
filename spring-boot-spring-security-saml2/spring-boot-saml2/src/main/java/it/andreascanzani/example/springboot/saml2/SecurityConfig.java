package it.andreascanzani.example.springboot.saml2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    static boolean isSamlEnabled = true;

    @Autowired(required = false)
    private RelyingPartyRegistrationRepository relyingPartyRegistrationRepository;

    @Autowired(required = false)
    private SAMLAuthenticationProvider samlAuthenticationProvider;

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) {
//        if(isSamlEnabled) {
//            auth.authenticationProvider(cmfsamlAuthenticationProvider);
//        }
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        if (isSamlEnabled) {
            http
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .saml2Login()
                    .authenticationManager(samlAuthenticationManager());

            Converter<HttpServletRequest, RelyingPartyRegistration> relyingPartyRegistrationResolver = new DefaultRelyingPartyRegistrationResolver(relyingPartyRegistrationRepository);
            Saml2MetadataFilter filter = new Saml2MetadataFilter(relyingPartyRegistrationResolver, new OpenSamlMetadataResolver());
            http.addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class);
        } else {
            System.out.println("SAML not enabled...");
        }
    }

    @Bean
    public AuthenticationManager samlAuthenticationManager() {
        return new ProviderManager(Arrays.asList(samlAuthenticationProvider));
    }

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
        System.out.println("I am inside the bean.");
        if (isSamlEnabled) {
            return createRelyingPartyRegistrationRepository();
        } else {
            return null;
        }
    }

    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private RelyingPartyRegistrationRepository createRelyingPartyRegistrationRepository() {

        System.out.println("I attempt to create a relyingPartyRegistartion Repo...");


        String entityId = "http://www.okta.com/exkencydijGCmCVdZ5d7";
        String sso = "https://dev-74229794.okta.com/app/dev-74229794_samltestapplication_1/exkencydijGCmCVdZ5d7/sso/saml";
         //String certificatePath = "file:/Users/sbaranidharan/Desktop/okta.crt";
         String certificatePath = "classpath:saml-certificate/okta.crt";
        // String certificatePath = "/Users/sbaranidharan/Desktop/okta.crt"; - FAILS


        try {

            Resource resource = resourceLoader.getResource(certificatePath);
            InputStream inputStream = resource.getInputStream();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(inputStream);
            Saml2X509Credential credential = Saml2X509Credential.verification(certificate);


            RelyingPartyRegistration relyingPartyRegistration = RelyingPartyRegistration
                    .withRegistrationId("okta-saml")
                    .assertingPartyDetails(party -> party
                            .entityId(entityId)
                            .singleSignOnServiceLocation(sso)
                            .wantAuthnRequestsSigned(false)
                            .verificationX509Credentials(c -> c.add(credential))
                    ).build();

            return new InMemoryRelyingPartyRegistrationRepository(relyingPartyRegistration);

        } catch (Exception e) {
            throw new RuntimeException("342474328322353 Error configuring SAML ", e);
        }
    }
}