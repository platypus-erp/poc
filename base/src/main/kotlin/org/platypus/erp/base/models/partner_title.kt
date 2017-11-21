package org.platypus.erp.base.models

import org.platypus.core.orm.Model


object PartnerTitle : Model<PartnerTitleEntity>() {
    val name = field.string("Title", required =true, translate = true, unique=true)
    val sortcut = field.string("Abbreviation", translate = true)
}