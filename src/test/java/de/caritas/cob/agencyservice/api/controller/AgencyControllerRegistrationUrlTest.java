package de.caritas.cob.agencyservice.api.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.agencyservice.api.model.RegistrationUrlDTO;
import de.caritas.cob.agencyservice.api.service.AgencyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

/** CARITAS-976 tests for the registration redirect endpoints of {@link AgencyController}. */
@RunWith(MockitoJUnitRunner.class)
public class AgencyControllerRegistrationUrlTest {

  private static final Long AGENCY_ID = 98L;
  private static final String CONSULTANT_ID = "aadc0ecf-c048-4bfc-857d-8c9b2e425500";
  private static final String URL = "https://caritas-onlineberatung.de/registration/agency";

  @InjectMocks private AgencyController agencyController;

  @Mock private AgencyService agencyService;

  @Test
  public void setAgencyRegistrationUrl_Should_delegateAndReturnNoContent() {
    var dto = new RegistrationUrlDTO().registrationUrl(URL).addedBy(CONSULTANT_ID);

    var response = agencyController.setAgencyRegistrationUrl(AGENCY_ID, dto);

    verify(agencyService).setRegistrationUrl(AGENCY_ID, URL, CONSULTANT_ID);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  public void deleteAgencyRegistrationUrl_Should_delegateAndReturnNoContent() {
    var response = agencyController.deleteAgencyRegistrationUrl(AGENCY_ID);

    verify(agencyService).deleteRegistrationUrl(AGENCY_ID);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  public void redirectAgencyRegistration_Should_return301WithLocation() {
    when(agencyService.resolveRegistrationRedirectTarget(AGENCY_ID)).thenReturn(URL);

    var response = agencyController.redirectAgencyRegistration(AGENCY_ID);

    assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
    assertEquals(URL, response.getHeaders().getLocation().toString());
  }
}
