package org.platypus.modules.base

import platypus.entity.PartnerCategorieEntity
import platypus.entity.PartnerEntity
import platypus.entity.PartnerTitleEntity
import org.platypus.core.orm.Model

/**
 * @author chmuchme
 * @since 0.1
 * on 09/09/17.
 */
object Partner : Model<PartnerEntity>() {
    val name = string("name")
    val name2 = string2 {
        ecripted = true
    }


    val displayName = string("displayName")
    val date = date("date")
    val title = many2one("title") of PartnerTitle
    val parent = many2one("parent") of Partner
    val parentName = string("Parent name", readonly = true/*, related= arrayOf("parent", "name")*/)
    val email = string("email", "Email", maxSize = 64, required = true)
    val childIds = one2many("childIds") of Partner.id
    val ref = string("ref", string = "Internal Reference")
    val lang = many2one("lang", "Language") of ResLang
}

object PartnerCategorie : Model<PartnerCategorieEntity>() {
    val name = string("name", "Category Name", required = true, translate = true)
    val color = integer("color", "Color Index")
    val parent_id = many2one("parent_id", "Parent Tag") of PartnerCategorie
    //    complete_name = function(_name_get_fnc, type="char", string="Full Name"),
    val complete_name = string("complete_name", string = "Full Name")
    val child_ids = one2many("parent_id", "Child Tag")  of PartnerCategorie.id
    val active = boolean("Active", help = "The active choice allows you to hide the category without removing it.")
    val parent_left = integer("Left parent")
    val parent_right = integer("Right parent")
//    partner_id s= many2many("res.partner", id1="category_id", id2="partner_id", string="Partners"),
}

object PartnerTitle : Model<PartnerTitleEntity>() {
    val name = string("name")
    val sortcut = string("sortcut")

}