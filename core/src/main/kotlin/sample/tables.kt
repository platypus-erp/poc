package platypus.entity

import org.platypus.core.orm.Model
import org.platypus.core.orm.PlatypusTable
import org.platypus.modules.base.Partner
import org.platypus.modules.base.ResLang
import org.platypus.modules.base.Users
import sample.PartnerCategorie
import sample.PartnerTitle

/**
 * @author chmuchme
 * @since 0.1
 * on 03/10/17.
 */
object PartnerTagsTable : PlatypusTable("partner_tags") {
//    val name = string("name")
//    val sortcut = string("sortcut")

}
object PartnerTable : PlatypusTable("partner") {
//    val name = string("name")
//    val displayName = string("displayName")
//    val date = date("date")
//    val title = many2one("title") of PartnerTitle
//    val parent = many2one("parent") of Partner
//    val parentName = string("Parent name", readonly = true/*, related= arrayOf("parent", "name")*/)
//    val email = string("email", "Email", maxSize = 64, required = true)
//    val childIds = one2many("childIds") of Partner.id
//    val ref = string("ref", string = "Internal Reference")
//    val lang = many2one("lang", "Language") of ResLang
}
object PartnerCategorieTable : PlatypusTable("partner_categorie") {
//    val name = string("name", "Category Name", required = true, translate = true)
//    val color = integer("color", "Color Index")
//    val parent_id = many2one("parent_id", "Parent Tag") of PartnerCategorie
//        complete_name = function(_name_get_fnc, type="char", string="Full Name"),
//    val complete_name = string("complete_name", string = "Full Name")
//    val child_ids = one2many("parent_id", "Child Tag") of PartnerCategorie.id
//    val active = boolean("Active", help = "The active choice allows you to hide the category without removing it.")
//    val parent_left = integer("Left parent")
//    val parent_right = integer("Right parent")
//    partner_id s= many2many("res.partner", id1="category_id", id2="partner_id", string="Partners"),
}
object PartnerTitleTable : PlatypusTable("partner_title") {
//    val name = string("name")
//    val sortcut = string("sortcut")

}