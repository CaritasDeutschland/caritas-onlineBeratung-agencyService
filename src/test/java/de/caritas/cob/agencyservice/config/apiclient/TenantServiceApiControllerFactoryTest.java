package de.caritas.cob.agencyservice.config.apiclient;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

class TenantServiceApiControllerFactoryTest {

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void createControllerApi_Should_CreateApiClientWithoutExceptions() {
    // given
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addHeader("Authorization", "Bearer token");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

    var tenantServiceApiControllerFactory = new TenantServiceApiControllerFactory();

    // when
    var tenantControllerApi = tenantServiceApiControllerFactory.createControllerApi();

    // then
    assertThat(tenantControllerApi).isNotNull();
  }
}