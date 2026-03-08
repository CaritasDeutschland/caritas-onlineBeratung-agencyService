package de.caritas.cob.agencyservice.api.service;

import static org.mockito.Mockito.verify;

import de.caritas.cob.agencyservice.api.tenant.TenantContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

  @Mock
  private TopicCachingService topicCachingService;

  @Test
  void getTopics_Should_CallTopicCachingService() {
    // given
    TopicService topicService = new TopicService(topicCachingService);

    // when
    topicService.getAllTopics();

    // then
    verify(topicCachingService).getAllTopics(TenantContext.getCurrentTenant());
  }
}