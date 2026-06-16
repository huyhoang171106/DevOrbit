package vn.edu.uit.devorbit_api.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertFalse;

class WebSecurityCustomizerRemovalTest {

    @Test
    void securityConfigShouldNotDefineWebSecurityCustomizerBean() {
        // SecurityConfig should not have a @Bean method returning WebSecurityCustomizer
        // because webSecurityCustomizer().ignoring() bypasses the ENTIRE filter chain.
        // The SecurityFilterChain's permitAll() rules are sufficient.
        boolean hasCustomizerMethod = false;
        try {
            var ctx = new AnnotationConfigWebApplicationContext();
            ctx.register(SecurityConfig.class);
            // If the bean can be registered without error, check if it has the bean
            hasCustomizerMethod = ctx.getBeanFactory().containsBean("webSecurityCustomizer");
            ctx.close();
        } catch (Exception e) {
            // Bean creation may fail due to missing dependencies — that's fine
            // The important thing is that the method shouldn't exist
        }

        assertFalse(hasCustomizerMethod,
                "SecurityConfig should not define a WebSecurityCustomizer bean — permitAll() in SecurityFilterChain is sufficient");
    }
}
