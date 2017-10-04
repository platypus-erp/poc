package platypus.entity

import org.jetbrains.exposed.dao.EntityID
import org.platypus.core.orm.PlatypusEntity
import org.platypus.core.orm.PlatypusEntityClass
import org.platypus.modules.base.Partner
import sample.PartnerCategorie
import sample.PartnerTags
import sample.PartnerTitle

//object GroupsusersUsersm2mRel : Many2ManyModel() {
//    val users = ref("users", Users)
//    val m2m = ref("m2m", Groups)
//}
//object UsersgroupsGroupsm2mRel : Many2ManyModel() {
//    val groups = ref("groups", Groups)
//    val m2m = ref("m2m", Users)
//}
/**
 * @author kassiopiaGenerator
 * Generated Class for model inside
 */


class PartnerTagsEntity(id: EntityID<Long>) : PlatypusEntity(id) {
    companion object : PlatypusEntityClass<PartnerTagsEntity>(PartnerTags, PartnerTagsTable)
}

class PartnerEntity(id: EntityID<Long>) : PlatypusEntity(id) {
    companion object : PlatypusEntityClass<PartnerEntity>(Partner, PartnerTable)
}

class PartnerCategorieEntity(id: EntityID<Long>) : PlatypusEntity(id) {
    companion object : PlatypusEntityClass<PartnerCategorieEntity>(PartnerCategorie, PartnerCategorieTable)
}

class PartnerTitleEntity(id: EntityID<Long>) : PlatypusEntity(id) {
    companion object : PlatypusEntityClass<PartnerTitleEntity>(PartnerTitle, PartnerTitleTable)
}