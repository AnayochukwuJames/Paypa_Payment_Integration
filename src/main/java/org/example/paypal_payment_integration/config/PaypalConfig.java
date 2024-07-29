package org.example.paypal_payment_integration.config;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfig {
    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secrete}")
    private String clientSecrete;

    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public APIContext APIContext(){
        return new APIContext(clientId, clientSecrete, mode);
    }
}
