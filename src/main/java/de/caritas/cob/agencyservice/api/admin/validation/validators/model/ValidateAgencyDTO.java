package de.caritas.cob.agencyservice.api.admin.validation.validators.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import de.caritas.cob.agencyservice.api.admin.validation.AgencyValidator;

/**
 * Validation dto for {@link AgencyValidator}.
 */
@Builder
@Getter
@Setter
public class ValidateAgencyDTO {

  private Long id;
  private Long dioceseId;
  private String postcode;
  private Integer consultingType;
  private Boolean offline;

}
