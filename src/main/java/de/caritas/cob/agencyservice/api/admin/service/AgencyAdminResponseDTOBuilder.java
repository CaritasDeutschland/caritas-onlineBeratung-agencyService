package de.caritas.cob.agencyservice.api.admin.service;

import static java.util.Collections.emptyList;
import static org.springframework.util.CollectionUtils.isEmpty;

import de.caritas.cob.agencyservice.api.model.AgencyAdminResponseDTO;
import de.caritas.cob.agencyservice.api.model.PostCodeRangeDTO;
import de.caritas.cob.agencyservice.api.repository.agency.Agency;
import de.caritas.cob.agencyservice.api.repository.agency.AgencyPostCodeRange;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AgencyAdminResponseDTOBuilder {

  private final @NonNull Agency agency;

  public AgencyAdminResponseDTO fromAgency() {
    return new AgencyAdminResponseDTO()
        .agencyId(this.agency.getId())
        .dioceseId(this.agency.getDioceseId())
        .name(this.agency.getName())
        .city(this.agency.getCity())
        .consultingType(this.agency.getConsultingType().getValue())
        .description(this.agency.getDescription())
        .postcode(this.agency.getPostCode())
        .teamAgency(this.agency.isTeamAgency())
        .offline(this.agency.isOffline())
        .createDate(String.valueOf(this.agency.getCreateDate()))
        .updateDate(String.valueOf(this.agency.getUpdateDate()))
        .deleteDate(String.valueOf(this.agency.getDeleteDate()))
        .postCodeRanges(buildAgencyPostCodeRanges());
  }

  private List<PostCodeRangeDTO> buildAgencyPostCodeRanges() {
    if (isEmpty(this.agency.getAgencyPostCodeRanges())) {
      return emptyList();
    }
    return this.agency.getAgencyPostCodeRanges()
        .stream()
        .map(this::fromAgencyPostCodeRange)
        .collect(Collectors.toList());
  }

  private PostCodeRangeDTO fromAgencyPostCodeRange(AgencyPostCodeRange agencyPostCodeRange) {
    return new PostCodeRangeDTO()
        .postcodeFrom(agencyPostCodeRange.getPostCodeFrom())
        .postcodeTo(agencyPostCodeRange.getPostCodeTo());
  }

}