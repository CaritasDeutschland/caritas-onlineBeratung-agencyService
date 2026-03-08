package de.caritas.cob.agencyservice.api.service;

import de.caritas.cob.agencyservice.api.tenant.TenantContext;

import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import de.caritas.cob.agencyservice.topicservice.generated.web.model.TopicDTO;

@Service
@RequiredArgsConstructor
public class TopicService {

  private final @NonNull TopicCachingService topicCachingService;


  public List<TopicDTO> getAllTopics() {
    return topicCachingService.getAllTopics(TenantContext.getCurrentTenant());
  }

}
