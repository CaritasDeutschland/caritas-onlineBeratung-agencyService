package de.caritas.cob.agencyservice.api.authorization;

import static de.caritas.cob.agencyservice.api.authorization.Authority.AGENCY_ADMIN;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class RoleAuthorizationAuthorityMapperTest {

  private final RoleAuthorizationAuthorityMapper roleAuthorizationAuthorityMapper =
      new RoleAuthorizationAuthorityMapper();

  @Test
  public void mapAuthorities_Should_returnGrantedAgencyAdminAuthority_When_authorityIsAgencyAdmin() {
    List<GrantedAuthority> grantedAuthorities = Stream.of("agency-admin")
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    Collection<? extends GrantedAuthority> mappedAuthorities = this.roleAuthorizationAuthorityMapper
        .mapAuthorities(grantedAuthorities);

    assertThat(mappedAuthorities, hasSize(1));
    assertThat(mappedAuthorities.iterator().next().getAuthority(), is(AGENCY_ADMIN.getAuthority()));
  }

  @Test
  public void mapAuthorities_Should_returnGrantedAgencyAdminAuthority_When_authoritiesContainsAgencyAdmin() {
    List<GrantedAuthority> grantedAuthorities = Stream.of("a", "v", "agency-admin", "c")
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    Collection<? extends GrantedAuthority> mappedAuthorities = this.roleAuthorizationAuthorityMapper
        .mapAuthorities(grantedAuthorities);

    assertThat(mappedAuthorities, hasSize(1));
    assertThat(mappedAuthorities.iterator().next().getAuthority(), is(AGENCY_ADMIN.getAuthority()));
  }

  @Test
  public void mapAuthorities_Should_returnEmptyCollection_When_authorityIsEmpty() {
    Collection<? extends GrantedAuthority> mappedAuthorities = this.roleAuthorizationAuthorityMapper
        .mapAuthorities(emptyList());

    assertThat(mappedAuthorities, hasSize(0));
  }

  @Test
  public void mapAuthorities_Should_returnEmptyCollection_When_authoritiesAreNotProvided() {
    List<GrantedAuthority> grantedAuthorities = Stream.of("a", "v", "b", "c")
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    Collection<? extends GrantedAuthority> mappedAuthorities = this.roleAuthorizationAuthorityMapper
        .mapAuthorities(grantedAuthorities);

    assertThat(mappedAuthorities, hasSize(0));
  }

}
