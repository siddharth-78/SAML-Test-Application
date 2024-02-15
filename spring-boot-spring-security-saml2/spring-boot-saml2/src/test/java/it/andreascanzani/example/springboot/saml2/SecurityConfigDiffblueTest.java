package it.andreascanzani.example.springboot.saml2;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {SecurityConfig.class})
@ExtendWith(SpringExtension.class)
class SecurityConfigDiffblueTest {
    @Autowired
    private SecurityConfig securityConfig;

    /**
     * Method under test: {@link SecurityConfig#configure(HttpSecurity)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testConfigure() throws Exception {
        // TODO: Complete this test.
        //   Reason: E051 Current JVM does not support JDK which compiled the project.
        //   Diffblue Cover is running on JVM version 8, but your
        //   project was built with JDK version 11.
        //   Diffblue Cover needs to be executed with the same or a more recent Java
        //   version than the version with which the project was compiled.
        //   Classes compiled with wrong JDK version:
        //     org/springframework/security/saml2/provider/service/authentication/OpenSaml4AuthenticationProvider has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0
        //   For best results recompile the project and run Diffblue Cover with the same
        //   supported Java version: 8, 11 (but not 11.0.7), 17, and 21.
        //   See https://diff.blue/E051 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        HttpSecurity http = null;

        // Act
        this.securityConfig.configure(http);

        // Assert
        // TODO: Add assertions on result
    }
}
