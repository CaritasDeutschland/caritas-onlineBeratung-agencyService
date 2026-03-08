package de.caritas.cob.agencyservice.api.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;

import de.caritas.cob.agencyservice.AgencyServiceApplication;
import de.caritas.cob.agencyservice.api.service.securityheader.SecurityHeaderSupplier;
import de.caritas.cob.agencyservice.config.apiclient.TopicServiceApiControllerFactory;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import de.caritas.cob.agencyservice.topicservice.generated.web.model.TopicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest(classes = AgencyServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@AutoConfigureTestDatabase(replace = Replace.ANY)
class TopicCachingServiceTest {

  @MockBean
  TopicServiceApiControllerFactory topicServiceApiControllerFactory;

  @MockBean SecurityHeaderSupplier securityHeaderSupplier;

  @MockBean TenantHeaderSupplier tenantHeaderSupplier;

  @MockBean
  de.caritas.cob.agencyservice.topicservice.generated.web.TopicControllerApi topicControllerApi;

  @Autowired
  TopicCachingService topicCachingService;

  @Test
  void getAllTopics_Should_CallTopicControllerApiToGetTopics_AndCacheResponsePerTenant() {
    // given
    when(securityHeaderSupplier.getKeycloakAndCsrfHttpHeaders()).thenReturn(new HttpHeaders());
    when(topicServiceApiControllerFactory.createControllerApi()).thenReturn(topicControllerApi);
    var expectedTopics = Lists.newArrayList(new TopicDTO(), new TopicDTO());
    when(topicControllerApi.getAllTopics()).thenReturn(expectedTopics);

    // when
    var actualTopics = topicCachingService.getAllTopics(1L);
    topicCachingService.getAllTopics(1L);

    // then
    assertThat(actualTopics).isEqualTo(expectedTopics);
    Mockito.verify(topicControllerApi, Mockito.times(1)).getAllTopics();
  }
}