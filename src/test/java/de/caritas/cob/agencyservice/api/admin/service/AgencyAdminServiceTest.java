package de.caritas.cob.agencyservice.api.admin.service;

import static de.caritas.cob.agencyservice.api.exception.httpresponses.HttpStatusExceptionReason.AGENCY_IS_ALREADY_DEFAULT_AGENCY;
import static de.caritas.cob.agencyservice.api.exception.httpresponses.HttpStatusExceptionReason.AGENCY_IS_ALREADY_TEAM_AGENCY;
import static de.caritas.cob.agencyservice.api.model.AgencyTypeRequestDTO.AgencyTypeEnum.DEFAULT_AGENCY;
import static de.caritas.cob.agencyservice.api.model.AgencyTypeRequestDTO.AgencyTypeEnum.TEAM_AGENCY;
import static de.caritas.cob.agencyservice.testHelper.TestConstants.AGENCY_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;
import static org.hamcrest.Matchers.is;

import de.caritas.cob.agencyservice.api.admin.validation.DeleteAgencyValidator;
import de.caritas.cob.agencyservice.api.exception.httpresponses.ConflictException;
import de.caritas.cob.agencyservice.api.exception.httpresponses.NotFoundException;
import de.caritas.cob.agencyservice.api.model.AgencyTypeRequestDTO;
import de.caritas.cob.agencyservice.api.model.UpdateAgencyDTO;
import de.caritas.cob.agencyservice.api.repository.agency.Agency;
import de.caritas.cob.agencyservice.api.repository.agency.AgencyRepository;
import de.caritas.cob.agencyservice.api.service.LogService;
import java.util.Optional;
import org.jeasy.random.EasyRandom;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AgencyAdminServiceTest {

  @InjectMocks
  AgencyAdminService agencyAdminService;

  @Mock
  AgencyRepository agencyRepository;

  @Mock
  UserAdminService userAdminService;

  @Mock
  DeleteAgencyValidator deleteAgencyValidator;

  @Mock
  private Logger logger;

  private EasyRandom easyRandom;

  @Before
  public void setup() {
    setInternalState(LogService.class, "LOGGER", logger);
    this.easyRandom = new EasyRandom();
  }

  @Test(expected = NotFoundException.class)
  public void updateAgency_Should_ThrowNotFoundException_WhenAgencyIsNotFound() {
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.empty());

    UpdateAgencyDTO updateAgencyDTO = this.easyRandom.nextObject(UpdateAgencyDTO.class);
    agencyAdminService.updateAgency(AGENCY_ID, updateAgencyDTO);
  }

  @Test
  public void updateAgency_Should_SaveAgencyChanges_WhenAgencyIsFound() {
    Agency agency = this.easyRandom.nextObject(Agency.class);
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));
    when(agencyRepository.save(any())).thenReturn(agency);

    UpdateAgencyDTO updateAgencyDTO = this.easyRandom.nextObject(UpdateAgencyDTO.class);
    agencyAdminService.updateAgency(AGENCY_ID, updateAgencyDTO);

    verify(this.agencyRepository, times(1)).save(any());
  }

  @Test(expected = NotFoundException.class)
  public void findAgencyById_Should_ThrowNotFoundException_WhenAgencyIsNotFound() {
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.empty());

    agencyAdminService.findAgencyById(AGENCY_ID);
  }

  @Test(expected = NotFoundException.class)
  public void changeAgencyType_Should_throwNotFoundException_When_agencyWasNotFound() {
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.empty());

    agencyAdminService.changeAgencyType(AGENCY_ID, mock(AgencyTypeRequestDTO.class));
  }

  @Test
  public void changeAgencyType_Should_throwConflictExceptionWithCorrectReason_When_agencyHasAlreadyTypeTeamAgency() {
    Agency agency = this.easyRandom.nextObject(Agency.class);
    agency.setTeamAgency(true);
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));
    AgencyTypeRequestDTO requestDTO = new AgencyTypeRequestDTO().agencyType(TEAM_AGENCY);

    try {
      agencyAdminService.changeAgencyType(AGENCY_ID, requestDTO);
      fail("ConflictException not thrown");
    } catch (ConflictException exception) {
      assertThat(AGENCY_IS_ALREADY_TEAM_AGENCY, is(exception.getHttpStatusExceptionReason()));
    }
  }

  @Test
  public void changeAgencyType_Should_throwConflictExceptionWithCorrectReason_When_agencyHasAlreadyTypeDefault() {
    Agency agency = this.easyRandom.nextObject(Agency.class);
    agency.setTeamAgency(false);
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));
    AgencyTypeRequestDTO requestDTO = new AgencyTypeRequestDTO().agencyType(DEFAULT_AGENCY);

    try {
      agencyAdminService.changeAgencyType(AGENCY_ID, requestDTO);
      fail("ConflictException not thrown");
    } catch (ConflictException exception) {
      assertThat(AGENCY_IS_ALREADY_DEFAULT_AGENCY, is(exception.getHttpStatusExceptionReason()));
    }
  }

  @Test
  public void changeAgencyType_Should_callUserAdminServiceAndSaveChangedAgency_When_agencyCanBeChanged() {
    Agency agency = this.easyRandom.nextObject(Agency.class);
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));
    AgencyTypeRequestDTO requestDTO = new AgencyTypeRequestDTO().agencyType(DEFAULT_AGENCY);

    agencyAdminService.changeAgencyType(AGENCY_ID, requestDTO);

    verify(this.userAdminService, times(1)).adaptRelatedConsultantsForChange(eq(AGENCY_ID),
        eq(requestDTO.getAgencyType().getValue()));
    verify(this.agencyRepository, times(1)).save(any());
  }

  @Test(expected = NotFoundException.class)
  public void deleteAgency_Should_ThrowNotFoundException_WhenAgencyIsNotFound() {
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.empty());

    agencyAdminService.deleteAgency(AGENCY_ID);
  }

  @Test
  public void deleteAgency_Should_callDeleteAgencyValidatorAndSaveChangedAgency_When_AgencyIsFound() {
    Agency agency = this.easyRandom.nextObject(Agency.class);
    when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));

    agencyAdminService.deleteAgency(AGENCY_ID);

    verify(this.deleteAgencyValidator, times(1)).validate(agency);
    verify(this.agencyRepository, times(1)).save(any());
  }

}
