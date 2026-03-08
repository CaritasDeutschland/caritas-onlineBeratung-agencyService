package de.caritas.cob.agencyservice.config.apiclient;

import de.caritas.cob.agencyservice.tenantservice.generated.ApiClient;
import de.caritas.cob.agencyservice.tenantservice.generated.web.TenantControllerApi;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class TenantServiceApiControllerFactory {

  private static final String DEFAULT_LANGUAGE = "de";
  private static final String LANGUAGE_COOKIE_NAME = "lang";
  @Value("${tenant.service.api.url}")
  private String tenantServiceApiUrl;

  @Autowired
  private RestTemplate restTemplate;

  public TenantControllerApi createControllerApi() {
    var apiClient = new ApiClient(restTemplate).setBasePath(this.tenantServiceApiUrl);
    apiClient.addDefaultCookie(LANGUAGE_COOKIE_NAME, getCurrentLanguageContext());
    return new TenantControllerApi(apiClient);
  }


  public String getCurrentLanguageContext() {
    HttpServletRequest currentRequest =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

    if (currentRequest.getCookies() != null) {
      Optional<Cookie> languageCookie = findLanguageCookie(currentRequest);
      if (languageCookie.isPresent()) {
        return languageCookie.get().getValue();
      }
    }
    return DEFAULT_LANGUAGE;
  }

  private static Optional<Cookie> findLanguageCookie(HttpServletRequest currentRequest) {
    return Arrays.stream(currentRequest.getCookies())
        .filter(cookie -> LANGUAGE_COOKIE_NAME.equals(cookie.getName()))
        .findFirst();
  }
}
