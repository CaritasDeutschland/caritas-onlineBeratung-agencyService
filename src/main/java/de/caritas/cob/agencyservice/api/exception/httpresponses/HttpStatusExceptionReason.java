package de.caritas.cob.agencyservice.api.exception.httpresponses;

public enum HttpStatusExceptionReason {
  INVALID_POSTCODE,
  INVALID_DIOCESE,
  INVALID_CONSULTING_TYPE,
  INVALID_OFFLINE_STATUS,
  LOCKED_CONSULTING_TYPE,
  AGENCY_CONTAINS_CONSULTANTS,
  AGENCY_CONTAINS_NO_CONSULTANTS,
  AGENCY_IS_ALREADY_TEAM_AGENCY,
  AGENCY_IS_ALREADY_DEFAULT_AGENCY,
  AGENCY_GROUP_CHAT_IS_LOCKED_TO_SET_OFFLINE
}
