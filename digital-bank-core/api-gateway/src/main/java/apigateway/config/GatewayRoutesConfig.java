package apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

  @Bean
  RouteLocator routes(RouteLocatorBuilder builder) {
    // No Java-based routes; all configured in application.yml
    return builder.routes().build();
  }
}
