package it.andreascanzani.example.springboot.saml2;

import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.*;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2LogoutRequestRepository;
import org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2LogoutResponseFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    static boolean isSamlEnabled = true;

    @Autowired(required = false)
    private RelyingPartyRegistrationRepository relyingPartyRegistrationRepository;

//    @Autowired(required = false)
//    private SAMLAuthenticationProvider samlAuthenticationProvider;

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) {
//        if(isSamlEnabled) {
//            auth.authenticationProvider(cmfsamlAuthenticationProvider);
//        }
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        System.out.println("Meow");
        if (isSamlEnabled) {
            http
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                        .logout()
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
//                        .logoutSuccessHandler(new CMFLogoutSuccessHandler())
                    .and()
                        .saml2Login()
                    .and()
                        .saml2Logout();
                    //.successHandler(new SAML2AuthenticationSuccessHandler());
                    //.authenticationManager(samlAuthenticationManager());

            Converter<HttpServletRequest, RelyingPartyRegistration> relyingPartyRegistrationResolver = new DefaultRelyingPartyRegistrationResolver(relyingPartyRegistrationRepository);
            Saml2MetadataFilter filter = new Saml2MetadataFilter(relyingPartyRegistrationResolver, new OpenSamlMetadataResolver());
            http.addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class);
        } else {
            System.out.println("SAML not enabled...");
        }
    }

//    @Bean
//    public AuthenticationManager samlAuthenticationManager() {
//        return new ProviderManager(Arrays.asList(samlAuthenticationProvider));
//    }

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


        String entityId = "http://www.okta.com/exkergz3wavtE4dNl5d7";
        String sso = "https://dev-74229794.okta.com/app/dev-74229794_cmsamltest_2/exkergz3wavtE4dNl5d7/sso/saml";
        String idpSlo = "https://dev-74229794.okta.com/app/dev-74229794_cmsamltest_2/exkergz3wavtE4dNl5d7/slo/saml";
        String idpCertificatePath = "classpath:saml-certificate/okta.crt";
        String spPrivateKeyPath = "file:/Users/sbaranidharan/Desktop/saml-certs/sha1/private.key";
        String spCertificatePath = "file:/Users/sbaranidharan/Desktop/saml-certs/sha1/certificate.crt";

        try {

            X509Certificate idpCertificate = loadCertificate(idpCertificatePath);
            Saml2X509Credential verificationCredential = Saml2X509Credential.verification(idpCertificate);

            PrivateKey spPrivateKey = loadPrivateKey(spPrivateKeyPath);
            X509Certificate spCertificate = loadCertificate(spCertificatePath);


            RelyingPartyRegistration relyingPartyRegistration = RelyingPartyRegistration
                    .withRegistrationId("okta-saml") // done
                    .signingX509Credentials((c) -> c.add(Saml2X509Credential.signing(spPrivateKey, spCertificate)))
                    .decryptionX509Credentials((c) -> c.add(Saml2X509Credential.decryption(spPrivateKey, spCertificate)))
                    .singleLogoutServiceLocation("{baseUrl}/logout/saml2/slo")
                    .assertingPartyDetails(party -> party
                            .entityId(entityId) // done
                            .singleSignOnServiceLocation(sso) // done
                            .singleLogoutServiceLocation(idpSlo)
                            .verificationX509Credentials(c -> c.add(verificationCredential))
                            .wantAuthnRequestsSigned(true)
                            .encryptionX509Credentials((c) -> Saml2X509Credential.encryption(idpCertificate))
                    ).build();

            return new InMemoryRelyingPartyRegistrationRepository(relyingPartyRegistration);

        } catch (Exception e) {
            throw new RuntimeException("342474328322353 Error configuring SAML ", e);
        }
    }

    public PrivateKey loadPrivateKey(String privateKeyPath) {

        Resource resource = resourceLoader.getResource(privateKeyPath);
        try (InputStream inputStream = resource.getInputStream()) {
            return RsaKeyConverters.pkcs8().convert(inputStream);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private X509Certificate loadCertificate(String certificatePath) throws Exception {

        Resource resource = resourceLoader.getResource(certificatePath);
        InputStream inputStream = resource.getInputStream();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(inputStream);

        return certificate;
    }
}