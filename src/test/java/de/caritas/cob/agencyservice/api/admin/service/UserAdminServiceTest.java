package de.caritas.cob.agencyservice.api.admin.service;

import static de.caritas.cob.agencyservice.useradminservice.generated.web.model.AgencyTypeDTO.AgencyTypeEnum.TEAM_AGENCY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.agencyservice.api.service.TenantHeaderSupplier;
import de.caritas.cob.agencyservice.api.service.securityheader.SecurityHeaderSupplier;
import de.caritas.cob.agencyservice.api.tenant.TenantContext;
import de.caritas.cob.agencyservice.config.apiclient.UserAdminServiceApiControllerFactory;
import de.caritas.cob.agencyservice.useradminservice.generated.ApiClient;
import de.caritas.cob.agencyservice.useradminservice.generated.web.AdminUserControllerApi;
import de.caritas.cob.agencyservice.useradminservice.generated.web.model.AgencyTypeDTO;
import de.caritas.cob.agencyservice.useradminservice.generated.web.model.ConsultantFilter;
import de.caritas.cob.agencyservice.useradminservice.generated.web.model.ConsultantSearchResultDTO;
import de.caritas.cob.agencyservice.useradminservice.generated.web.model.Sort;
import de.caritas.cob.agencyservice.useradminservice.generated.web.model.Sort.FieldEnum;
import de.caritas.cob.agencyservice.useradminservice.generated.web.model.Sort.OrderEnum;
import java.util.List;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

  @InjectMocks
  private UserAdminService userAdminService;

  @Mock
  private AdminUserControllerApi adminUserControllerApi;

  @Mock
  private SecurityHeaderSupplier securityHeaderSupplier;

  @Mock
  private TenantHeaderSupplier tenantHeaderSupplier;

  @Mock
  private UserAdminServiceApiControllerFactory userAdminServiceApiControllerFactory;

  @Mock
  private ApiClient apiClient;

  private final HttpHeaders httpHeaders = new EasyRandom().nextObject(HttpHeaders.class);

  @BeforeEach
  void setup() {
    when(this.securityHeaderSupplier.getKeycloakAndCsrfHttpHeaders())
        .thenReturn(this.httpHeaders);
  }

  @Test
  void adaptRelatedConsultantsForChange_Should_callServicesCorrectly() {
    Long agencyId = 1L;
    when(userAdminServiceApiControllerFactory.createControllerApi()).thenReturn(adminUserControllerApi);
    this.userAdminService.adaptRelatedConsultantsForChange(agencyId, TEAM_AGENCY.getValue());

    verify(this.adminUserControllerApi, times(1)).changeAgencyType(agencyId,
        new AgencyTypeDTO().agencyType(TEAM_AGENCY));
    verify(this.apiClient, times(this.httpHeaders.size())).addDefaultHeader(any(), any());
  }

  @Test
  void getConsultantsOfAgency_Should_callServicesCorrectly() {
    Long agencyId = 1L;
    int currentPage = 1;
    int perPage = 1;
    when(userAdminServiceApiControllerFactory.createControllerApi()).thenReturn(adminUserControllerApi);
    when(this.adminUserControllerApi.getConsultants(any(), any(), any(), any()))
        .thenReturn(new EasyRandom().nextObject(ConsultantSearchResultDTO.class));
    this.userAdminService.getConsultantsOfAgency(agencyId, currentPage, perPage);

    verify(this.adminUserControllerApi, times(1))
        .getConsultants(eq(currentPage), eq(perPage),
            eq(new ConsultantFilter().agencyId(agencyId)), any());
    verify(this.apiClient, times(this.httpHeaders.size())).addDefaultHeader(any(), any());
  }

  @Test
  void addTenantHeader_WhenMultitenacy_Enabled() {
    TenantContext.setCurrentTenant(1L);
    ApiClient apiClient = new ApiClient();
    TenantHeaderSupplier tenantHeaderSupplier = new TenantHeaderSupplier();
    ReflectionTestUtils.setField(tenantHeaderSupplier, "multitenancy", true);
    ReflectionTestUtils
        .setField(this.userAdminService, "tenantHeaderSupplier", tenantHeaderSupplier);
    this.userAdminService.addDefaultHeaders(apiClient);
    HttpHeaders httpHeaders = (HttpHeaders) ReflectionTestUtils
        .getField(apiClient, "defaultHeaders");
    List<String> tenantId = httpHeaders.get("tenantId");
    assertEquals(tenantId.get(0), TenantContext.getCurrentTenant().toString());
    TenantContext.clear();
  }


  private Sort getSort() {
    Sort sortBy = new Sort();
    sortBy.setField(FieldEnum.LASTNAME);
    sortBy.setOrder(OrderEnum.ASC);
    return sortBy;
  }
}
