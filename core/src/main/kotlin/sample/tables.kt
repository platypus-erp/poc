package platypus.entity

import org.platypus.core.orm.PlatypusTable
import sample.Partner
import sample.PartnerTags

/**
 * @author chmuchme
 * @since 0.1
 * on 03/10/17.
 */
object PartnerTagsTable : PlatypusTable("partner_tags") {
    val name = stringColumn("name", PartnerTags.name)
    val sortcut = stringColumn("sortcut", PartnerTags.shorcut)

}

object PartnerTable : PlatypusTable("partner") {
    val name = stringColumn("name", Partner.name)
    val displayName = stringColumn("displayName", Partner.displayName)
    val date = dateColumn("date", Partner.date)
    val title = many2oneColumn("title", Partner.title, PartnerTitleTable)

    val parent = many2oneColumn("parent", Partner.parent, PartnerTable)
    val email = stringColumn("email", Partner.email)
    val childIds = one2manyColumn("childIds", Partner.id, PartnerTable)



    val ref = stringColumn("ref", Partner.ref)
//    val lang = many2oneColumn("lang", Partner.lang, ) of ResLang
}

object PartnerCategorieTable : PlatypusTable("partner_categorie") {
//    val name = stringColumn("name", "Category Name", required = true, translate = true)
//    val color = integer("color", "Color Index")
//    val parent_id = many2oneColumn("parent_id", "Parent Tag") of PartnerCategorie
//        complete_name = function(_name_get_fnc, type="char", stringColumn="Full Name"),
//    val complete_name = stringColumn("complete_name", stringColumn = "Full Name")
//    val child_ids = one2manyColumn("parent_id", "Child Tag") of PartnerCategorie.id
//    val active = boolean("Active", help = "The active choice allows you to hide the category without removing it.")
//    val parent_left = integer("Left parent")
//    val parent_right = integer("Right parent")
//    partner_id s= many2many("res.partner", id1="category_id", id2="partner_id", stringColumn="Partners"),
}

object PartnerTitleTable : PlatypusTable("partner_title") {
//    val name = stringColumn("name")
//    val sortcut = stringColumn("sortcut")

}