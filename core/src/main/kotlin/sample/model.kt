package sample

import org.platypus.core.orm.Model
import org.platypus.core.orm.methods.OnChangeResult
import org.platypus.modules.base.Partner
import org.platypus.modules.base.ResLang
import platypus.entity.PartnerCategorieEntity
import platypus.entity.PartnerEntity
import platypus.entity.PartnerTagsEntity
import platypus.entity.PartnerTitleEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 04/10/17.
 */
//Code to write
object PartnerTags : Model<PartnerTagsEntity>() {
    val name = string()
}

object Partner : Model<PartnerEntity>() {
    val name = string()
    val displayName = string()
    val date = date()
    val title = many2one("title") of PartnerTitle
    val parent = many2one("parent") of Partner
    val parentName = string("Parent name", readonly = true/*, related= arrayOf("parent", "name")*/)
    val email = string("Email", maxSize = 64, required = true)
    val childIds = one2many("childIds") of Partner.id
    val ref = string(string = "Internal Reference")
    val lang = many2one("lang", "Language") of ResLang


    val onChangeName = name.onChange { e, ctx ->
    }

    init {
        name.getter { e, c -> c.Super(e) }
    }

    val onChangeMultiProperty = onChangeGroup(displayName, email).onChange(this::onChangeTest)


    private fun onChangeTest(e: PartnerEntity, ctx: OnChangeResult<PartnerEntity>) {

    }
}

object PartnerCategorie : Model<PartnerCategorieEntity>() {
    val name = string("Category Name", required = true, translate = true)
    val color = integer("color", "Color Index")
    val parent_id = many2one("parent_id", "Parent Tag") of PartnerCategorie
    //    complete_name = function(_name_get_fnc, type="char", string="Full Name"),
    val complete_name = string(string = "Full Name")
    val child_ids = one2many("parent_id", "Child Tag") of PartnerCategorie.id
    val active = boolean(help = "The active choice allows you to hide the category without removing it.")
    val parent_left = integer()
    val parent_right = integer()
//    partner_id s= many2many("res.partner", id1="category_id", id2="partner_id", string="Partners"),
}

object PartnerTitle : Model<PartnerTitleEntity>() {
    val name = string("name")
    val sortcut = string("sortcut")

}
