package it.andreascanzani.example.springboot.saml2;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateValidator {

    /**
     * Checks if the certificate at the given path is currently valid.
     *
     * @param certPath The path to the certificate file.
     * @return true if the certificate is valid, false otherwise.
     */
    public static boolean isCertificateValid(String certPath) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            FileInputStream fis = new FileInputStream(certPath);
            X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(fis);
            fis.close();

            certificate.checkValidity(new Date()); // Check if the certificate is valid at the current date
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //private static ResourceLoader resourceLoader = new DefaultResourceLoader();
    public static void main(String[] args) {

        String certPath = "/Users/sbaranidharan/Desktop/okta.crt";
      //  Resource resource = resourceLoader.getResource("file:" + certPath);

        if(isCertificateValid(certPath)) {
           System.out.println("Certificate validity is VALID!");
       }
       else {
           System.out.println("Certificate validity is NOT VALID! :(");
       }
    }
}

