@FilterDefs({
    @FilterDef(
        name = "tenantFilter",
        parameters = {@ParamDef(name = "tenantId", type = Long.class)}
    )
})
package de.caritas.cob.agencyservice.api.repository;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.ParamDef;