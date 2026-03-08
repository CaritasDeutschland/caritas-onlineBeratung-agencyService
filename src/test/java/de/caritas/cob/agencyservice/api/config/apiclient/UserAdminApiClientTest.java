package de.caritas.cob.agencyservice.api.config.apiclient;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.caritas.cob.agencyservice.api.exception.httpresponses.InternalServerErrorException;
import de.caritas.cob.agencyservice.config.apiclient.UserAdminApiClient;
import de.caritas.cob.agencyservice.useradminservice.generated.web.model.ConsultantFilter;
import java.util.List;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MultiValueMap;

@ExtendWith(SpringExtension.class)
class UserAdminApiClientTest {

  private static final String FILTER_NAME = "filter";

  @InjectMocks
  UserAdminApiClient userAdminApiClient;

  @Test
  void parameterToMultiValueMap_Should_notAddNullValuesToQueryParamMap() {
    EasyRandom easyRandom = new EasyRandom();
    ConsultantFilter consultantFilter = easyRandom.nextObject(ConsultantFilter.class);
    consultantFilter.setEmail(null);

    MultiValueMap<String, String> result = userAdminApiClient.parameterToMultiValueMap(null, FILTER_NAME, consultantFilter);

    assertThat(result, not(hasEntry("email", null)));
  }

  @Test
  void parameterToMultiValueMap_Should_containAllNonNullValuesAsQueryParams() {
    ConsultantFilter consultantFilter = new ConsultantFilter()
        .lastname("lastname")
        .email("email")
        .agencyId(1L);

    MultiValueMap<String, String> result = userAdminApiClient.parameterToMultiValueMap(null, FILTER_NAME, consultantFilter);

    assertThat(result.size(), is(3));
    assertThat(result.get("lastname").get(0), is("lastname"));
    assertThat(result.get("email").get(0), is("email"));
    assertThat(result.get("agencyId").get(0), is("1"));
  }

  @Test
  void parameterToMultiValueMap_Should_returnValuesFormattedBySuperClass_When_providedWithInvalidQueryParams() {
    List<String> list = asList("lastname", "email");

    MultiValueMap<String, String> result = userAdminApiClient.parameterToMultiValueMap(null, "no_filter", list);

    assertThat(result.size(), is(1));
    assertThat(result.get("no_filter").get(0), is("lastname,email"));
  }

  class InvalidObject {

    String getValueWithoutProperty() {
      return "test";
    }
  }
}
