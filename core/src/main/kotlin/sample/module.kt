package platypus.entity

import org.platypus.core.orm.InheritModel
import org.platypus.core.orm.Model
import sample.Partner
import sample.PartnerCategorie
import sample.PartnerTags
import sample.PartnerTitle

/**
 * @author chmuchme
 * @since 0.1
 * on 07/09/17.
 */
val allRootModel:() -> Set<Model<*>> = {setOf<Model<*>>(Partner, PartnerTags, PartnerCategorie, PartnerTitle)}
val allRootTable = {setOf(PartnerTable, PartnerTagsTable, PartnerCategorieTable, PartnerTitleTable)}


