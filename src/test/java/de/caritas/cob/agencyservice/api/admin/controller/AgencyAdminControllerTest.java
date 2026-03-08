package de.caritas.cob.agencyservice.api.admin.controller;

import static de.caritas.cob.agencyservice.testHelper.PathConstants.AGENCY_POSTCODE_RANGE_PATH;
import static de.caritas.cob.agencyservice.testHelper.PathConstants.AGENCY_SEARCH_PATH;
import static de.caritas.cob.agencyservice.testHelper.PathConstants.CHANGE_AGENCY_TYPE_PATH;
import static de.caritas.cob.agencyservice.testHelper.PathConstants.CREATE_AGENCY_PATH;
import static de.caritas.cob.agencyservice.testHelper.PathConstants.GET_AGENCY_PATH;
import static de.caritas.cob.agencyservice.testHelper.PathConstants.PAGE_PARAM;
import static de.caritas.cob.agencyservice.testHelper.PathConstants.PER_PAGE_PARAM;
import static de.caritas.cob.agencyservice.testHelper.PathConstants.ROOT_PATH;
import static de.caritas.cob.agencyservice.testHelper.PathConstants.UPDATE_DELETE_AGENCY_PATH;
import static de.caritas.cob.agencyservice.testHelper.PathConstants.UPDATE_DELETE_AGENCY_PATH_INVALID_ID;
import static de.caritas.cob.agencyservice.testHelper.TestConstants.AGENCY_ID;
import static de.caritas.cob.agencyservice.testHelper.TestConstants.CONSULTING_TYPE_PREGNANCY;
import static de.caritas.cob.agencyservice.testHelper.TestConstants.VALID_POSTCODE;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import de.caritas.cob.agencyservice.AgencyServiceApplication;
import de.caritas.cob.agencyservice.api.admin.service.AgencyAdminService;
import de.caritas.cob.agencyservice.api.admin.service.agency.AgencyAdminSearchService;
import de.caritas.cob.agencyservice.api.admin.service.agencypostcoderange.AgencyPostcodeRangeAdminService;
import de.caritas.cob.agencyservice.api.admin.validation.AgencyValidator;
import de.caritas.cob.agencyservice.api.authorization.RoleAuthorizationAuthorityMapper;
import de.caritas.cob.agencyservice.api.exception.httpresponses.InvalidConsultingTypeException;
import de.caritas.cob.agencyservice.api.exception.httpresponses.InvalidOfflineStatusException;
import de.caritas.cob.agencyservice.api.exception.httpresponses.InvalidPostcodeException;
import de.caritas.cob.agencyservice.api.manager.consultingtype.ConsultingTypeManager;
import de.caritas.cob.agencyservice.api.model.AgencyAdminFullResponseDTO;
import de.caritas.cob.agencyservice.api.model.AgencyDTO;
import de.caritas.cob.agencyservice.api.model.AgencyTypeRequestDTO;
import de.caritas.cob.agencyservice.api.model.AgencyTypeRequestDTO.AgencyTypeEnum;
import de.caritas.cob.agencyservice.api.model.DemographicsDTO;
import de.caritas.cob.agencyservice.api.model.PostcodeRangeDTO;
import de.caritas.cob.agencyservice.api.model.UpdateAgencyDTO;
import de.caritas.cob.agencyservice.api.repository.agency.AgencyRepository;
import de.caritas.cob.agencyservice.api.service.TenantHeaderSupplier;
import de.caritas.cob.agencyservice.api.service.securityheader.SecurityHeaderSupplier;
import de.caritas.cob.agencyservice.config.apiclient.UserAdminServiceApiControllerFactory;
import de.caritas.cob.agencyservice.config.security.AuthorisationService;
import de.caritas.cob.agencyservice.config.security.JwtAuthConverter;
import de.caritas.cob.agencyservice.config.security.JwtAuthConverterProperties;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = AgencyServiceApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(
    locations = "classpath:application-testing.properties")
class AgencyAdminControllerTest {

  static final int AGE_FROM = 25;
  static final int AGE_TO = 100;
  @Autowired
  private MockMvc mvc;
  @MockBean
  private AgencyAdminService agencyAdminService;
  @MockBean
  private AgencyValidator agencyValidator;
  @MockBean
  private AgencyAdminSearchService agencyAdminFullResponseDTO;
  @MockBean
  private AgencyPostcodeRangeAdminService agencyPostCodeRangeAdminService;
  @MockBean
  private LinkDiscoverers linkDiscoverers;
  @MockBean
  private RoleAuthorizationAuthorityMapper roleAuthorizationAuthorityMapper;

  @MockBean
  private JwtAuthConverter jwtAuthConverter;

  @MockBean
  private AuthorisationService authorisationService;

  @MockBean
  private JwtAuthConverterProperties jwtAuthConverterProperties;

  @MockBean
  private UserAdminServiceApiControllerFactory adminServiceApiControllerFactory;

  @MockBean
  private SecurityHeaderSupplier securityHeaderSupplier;

  @MockBean
  private TenantHeaderSupplier tenantHeaderSupplier;

  @MockBean
  private ConsultingTypeManager consultingTypeManager;


  @MockBean
  @Qualifier("agencyTenantAwareRepository")
  private AgencyRepository agencyRepository;


  @Test
  void searchAgencies_Should_returnBadRequest_When_requiredPaginationParamsAreMissing()
      throws Exception {
    this.mvc.perform(get(AGENCY_SEARCH_PATH)).andExpect(status().isBadRequest());
  }

  @Test
  void getRoot_Should_returnExpectedRootDTO() throws Exception {
    this.mvc
        .perform(get(ROOT_PATH))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links").exists())
        .andExpect(jsonPath("$._links.self").exists())
        .andExpect(jsonPath("$._links.self.href", endsWith("/agencyadmin")))
        .andExpect(jsonPath("$._links.agencies").exists())
        .andExpect(
            jsonPath(
                "$._links.agencies.href", endsWith("/agencyadmin/agencies?page=1&perPage=20{&q}")));
  }

  @Test
  void searchAgencies_Should_returnOk_When_requiredPaginationParamsAreGiven()
      throws Exception {
    this.mvc
        .perform(get(AGENCY_SEARCH_PATH).param(PAGE_PARAM, "0").param(PER_PAGE_PARAM, "1"))
        .andExpect(status().isOk());

    Mockito.verify(this.agencyAdminFullResponseDTO, Mockito.times(1))
        .searchAgencies(any(), eq(0), eq(1), any());
  }

  @Test
  @WithMockUser(authorities = {"AUTHORIZATION_AGENCY_ADMIN"})
  void createAgency_Should_returnCreated_When_AgencyDtoIsGiven() throws Exception {
    AgencyDTO agencyDTO = new AgencyDTO();
    agencyDTO.setName("Test Agency");
    agencyDTO.setDescription("Test Description");
    agencyDTO.setPostcode("12345");
    agencyDTO.setCity("Test City");
    agencyDTO.setTeamAgency(false);
    agencyDTO.setConsultingType(0);
    agencyDTO.setDemographics(new DemographicsDTO());
    agencyDTO.setTopicIds(Lists.newArrayList());
    agencyDTO.setPostcode(VALID_POSTCODE);
    agencyDTO.setExternal(true);
    agencyDTO.setConsultingType(CONSULTING_TYPE_PREGNANCY);
    setValidDemographics(agencyDTO.getDemographics());

    EasyRandom easyRandom = createEasyRandomGeneratingValidData();
    AgencyAdminFullResponseDTO agencyAdminFullResponseDTO =
        easyRandom.nextObject(AgencyAdminFullResponseDTO.class);

    when(agencyAdminService.createAgency(agencyDTO)).thenReturn(agencyAdminFullResponseDTO);

    this.mvc
        .perform(
            post(CREATE_AGENCY_PATH)
                .content(new ObjectMapper().writeValueAsString(agencyDTO))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  private void setValidDemographics(DemographicsDTO demographics) {
    demographics.setAgeFrom(AGE_FROM);
    demographics.setAgeTo(AGE_TO);
  }

  @Test
  void createAgency_Should_ReturnBadRequest_WhenAgencyDtoIsMissing() throws Exception {
    this.mvc
        .perform(post(CREATE_AGENCY_PATH).content("").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(authorities = {"AUTHORIZATION_AGENCY_ADMIN"})
  void createAgency_Should_ReturnBadRequest_WhenAgencyConsultingType_IsInvalid()
      throws Exception {

    EasyRandom easyRandom = createEasyRandomGeneratingValidData();
    AgencyDTO agencyDTO = easyRandom.nextObject(AgencyDTO.class);
    agencyDTO.setPostcode(VALID_POSTCODE);
    agencyDTO.setConsultingType(CONSULTING_TYPE_PREGNANCY);
    setValidDemographics(agencyDTO.getDemographics());
    agencyDTO.setDataProtection(null);
    doThrow(new InvalidConsultingTypeException()).when(agencyValidator).validate(agencyDTO);
    this.mvc
        .perform(
            post(CREATE_AGENCY_PATH)
                .content(new ObjectMapper().writeValueAsString(agencyDTO))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(header().string("X-Reason", "INVALID_CONSULTING_TYPE"));
  }

  @Test
  @WithMockUser(authorities = {"AUTHORIZATION_AGENCY_ADMIN"})
  void createAgency_Should_ReturnBadRequest_WhenAgencyPostcode_IsInvalid() throws Exception {

    EasyRandom easyRandom = createEasyRandomGeneratingValidData();
    AgencyDTO agencyDTO = easyRandom.nextObject(AgencyDTO.class);
    agencyDTO.setPostcode("invalid postcode");
    agencyDTO.setConsultingType(CONSULTING_TYPE_PREGNANCY);
    setValidDemographics(agencyDTO.getDemographics());
    doThrow(new InvalidPostcodeException()).when(agencyValidator).validate(agencyDTO);
    this.mvc
        .perform(
            post(CREATE_AGENCY_PATH)
                .content(new ObjectMapper().writeValueAsString(agencyDTO))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getAgencyPostCodeRanges_Should_returnOk()
      throws Exception {
    this.mvc
        .perform(get(AGENCY_POSTCODE_RANGE_PATH))
        .andExpect(status().isOk());

    Mockito.verify(this.agencyPostCodeRangeAdminService, Mockito.times(1))
        .findPostcodeRangesForAgency(1L);
  }

  @Test
  void deleteAgencyPostCodeRange_Should_returnOk()
      throws Exception {
    this.mvc.perform(delete(AGENCY_POSTCODE_RANGE_PATH))
        .andExpect(status().isOk());

    Mockito.verify(this.agencyPostCodeRangeAdminService, Mockito.times(1))
        .deleteAgencyPostcodeRange(1L);
  }

  @Test
  void deleteAgencyPostCodeRange_Should_returnBadRequest_When_requiredParamIsWrong()
      throws Exception {
    this.mvc
        .perform(delete(AGENCY_POSTCODE_RANGE_PATH + "aaa"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateAgency_Should_returnOk_When_UpdateAgencyDtoIsGiven() throws Exception {

    EasyRandom easyRandom = createEasyRandomGeneratingValidData();
    UpdateAgencyDTO updateAgencyDTO = easyRandom.nextObject(UpdateAgencyDTO.class);
    updateAgencyDTO.setPostcode(VALID_POSTCODE);
    updateAgencyDTO.setConsultingType(CONSULTING_TYPE_PREGNANCY);
    setValidDemographics(updateAgencyDTO.getDemographics());
    updateAgencyDTO.setDataProtection(null);
    AgencyAdminFullResponseDTO agencyAdminFullResponseDTO =
        easyRandom.nextObject(AgencyAdminFullResponseDTO.class);

    when(agencyAdminService.updateAgency(AGENCY_ID, updateAgencyDTO))
        .thenReturn(agencyAdminFullResponseDTO);

    this.mvc
        .perform(
            put(UPDATE_DELETE_AGENCY_PATH)
                .content(new ObjectMapper().writeValueAsString(updateAgencyDTO))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void updateAgency_Should_ReturnBadRequest_WhenUpdateAgencyDtoIsMissing() throws Exception {
    this.mvc
        .perform(put(UPDATE_DELETE_AGENCY_PATH).content("").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateAgency_Should_ReturnBadRequest_WhenAgencyOfflineStatus_IsInvalid()
      throws Exception {

    EasyRandom easyRandom = createEasyRandomGeneratingValidData();
    UpdateAgencyDTO updateAgencyDTO = easyRandom.nextObject(UpdateAgencyDTO.class);
    updateAgencyDTO.setPostcode(VALID_POSTCODE);
    updateAgencyDTO.setName("name");
    updateAgencyDTO.setConsultingType(CONSULTING_TYPE_PREGNANCY);
    updateAgencyDTO.setDataProtection(null);
    setValidDemographics(updateAgencyDTO.getDemographics());
    doThrow(new InvalidOfflineStatusException())
        .when(agencyValidator)
        .validate(1L, updateAgencyDTO);
    this.mvc
        .perform(
            put(UPDATE_DELETE_AGENCY_PATH)
                .content(new ObjectMapper().writeValueAsString(updateAgencyDTO))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(header().string("X-Reason", "INVALID_OFFLINE_STATUS"));
  }

  @Test
  void updateAgency_Should_ReturnBadRequest_WhenAgencyPostcode_IsInvalid() throws Exception {

    EasyRandom easyRandom = createEasyRandomGeneratingValidData();
    UpdateAgencyDTO updateAgencyDTO = easyRandom.nextObject(UpdateAgencyDTO.class);
    updateAgencyDTO.setPostcode(VALID_POSTCODE);
    updateAgencyDTO.setConsultingType(CONSULTING_TYPE_PREGNANCY);
    updateAgencyDTO.setDataProtection(null);
    setValidDemographics(updateAgencyDTO.getDemographics());
    doThrow(new InvalidPostcodeException()).when(agencyValidator).validate(1L, updateAgencyDTO);
    this.mvc
        .perform(
            put(UPDATE_DELETE_AGENCY_PATH)
                .content(new ObjectMapper().writeValueAsString(updateAgencyDTO))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(header().string("X-Reason", "INVALID_POSTCODE"));
  }

  @Test
  void createAgencyPostcodeRange_Should_returnCreated_When_AllParamsAreValid()
      throws Exception {
    var postcodeRangeDTO = new PostcodeRangeDTO().postcodeRanges("12345-23456");

    this.mvc
        .perform(
            post(AGENCY_POSTCODE_RANGE_PATH)
                .content(new ObjectMapper().writeValueAsString(postcodeRangeDTO))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  @Test
  void updateAgencyPostcodeRange_Should_returnOk_When_AllParamsAreValid() throws Exception {
    var postcodeRangeDTO = new PostcodeRangeDTO().postcodeRanges("12345-23456");

    this.mvc
        .perform(
            put(AGENCY_POSTCODE_RANGE_PATH)
                .content(new ObjectMapper().writeValueAsString(postcodeRangeDTO))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void changeAgencyType_Should_returnOk_When_AllParamsAreValid() throws Exception {
    var agencyTypeDTO = new AgencyTypeRequestDTO().agencyType(AgencyTypeEnum.TEAM_AGENCY);

    this.mvc
        .perform(
            post(CHANGE_AGENCY_TYPE_PATH)
                .content(new ObjectMapper().writeValueAsString(agencyTypeDTO))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void changeAgencyType_Should_returnBadRequest_When_teamAgencyIsNull() throws Exception {
    var agencyTypeDTO = new AgencyTypeRequestDTO().agencyType(null);

    this.mvc
        .perform(
            post(CHANGE_AGENCY_TYPE_PATH)
                .content(new ObjectMapper().writeValueAsString(agencyTypeDTO))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deleteAgency_Should_returnOk_When_AllParamsAreValid() throws Exception {
    this.mvc
        .perform(delete(UPDATE_DELETE_AGENCY_PATH).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void deleteAgency_Should_returnBadRequest_When_teamAgencyIsInvalid() throws Exception {
    this.mvc
        .perform(
            delete(UPDATE_DELETE_AGENCY_PATH_INVALID_ID).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getAgency_Should_returnOk_When_AllParamsAreValid() throws Exception {
    this.mvc
        .perform(get(GET_AGENCY_PATH + "/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getAgency_Should_returnBadRequest_When_agncyIdIsInvalid() throws Exception {
    this.mvc
        .perform(get(GET_AGENCY_PATH + "/ab").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  private static EasyRandom createEasyRandomGeneratingValidData() {
    EasyRandomParameters parameters = new EasyRandomParameters()
        // Ensure valid strings
        .stringLengthRange(1, 5)

        // Ensure valid collection sizes
        .collectionSizeRange(1, 5)

        // Set range for integers
        .randomize(Integer.class, () -> ThreadLocalRandom.current().nextInt(1, 100))

        // Set range for dates
        .dateRange(LocalDate.of(2000, 1, 1), LocalDate.of(2023, 12, 31));

    return new EasyRandom(parameters);
  }
}
