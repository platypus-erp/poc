package sample

import org.platypus.core.orm.Model
import org.platypus.core.orm.fields.PlatypusBooleanProperty
import org.platypus.core.orm.fields.PlatypusStringProperty
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
    val name = newfield.string()
}

object Partner : Model<PartnerEntity>() {
    val name = newfield.string()
    val displayName = newfield.string()
    val date = newfield.date()
    val title = newfield.many2one("title") of PartnerTitle
    val parent = newfield.many2one("parent") of Partner
    val parentName = newfield.string("Parent name", readonly = true/*, related= arrayOf("parent", "name")*/)
    val email = newfield.string("Email", maxSize = 64, required = true)
    val childIds = newfield.one2many("childIds") of Partner.id
    val ref = newfield.string(string = "Internal Reference")
    val lang = newfield.many2one("lang", "Language") of ResLang


    val onChangeName = name.onChange { e, ctx ->
        ctx.withContext("TOTO" to 5).Super(e)
    }

    val multiTest = newMethod.multi()

    init {

    }

    val compute_get_name = name.getter { e, c -> c.Super(e) }
    val compute_set_name = name.setter { e, v, c -> c.Super(e, v) }

    val onChangeMultiProperty = onChangeGroup(displayName, email).onChange(this::onChangeTest)


    private fun onChangeTest(e: PartnerEntity, ctx: OnChangeResult<PartnerEntity>) {

    }
}

object PartnerCategorie : Model<PartnerCategorieEntity>() {
    val name = newfield.string("Category Name", required = true, translate = true)
    val color = newfield.integer("color", "Color Index")
    val parent_id = newfield.many2one("parent_id", "Parent Tag") of PartnerCategorie
    //    complete_name = function(_name_get_fnc, type="char", string="Full Name"),
    val complete_name = newfield.string(string = "Full Name")
    val child_ids = newfield.one2many("parent_id", "Child Tag") of PartnerCategorie.id
    val active = newfield.boolean(help = "The active choice allows you to hide the category without removing it.")
    val parent_left = newfield.integer()
    val parent_right = newfield.integer()
//    partner_id s= many2many("res.partner", id1="category_id", id2="partner_id", string="Partners"),
}

object PartnerTitle : Model<PartnerTitleEntity>() {
    val name = newfield.string("name")
    val sortcut = newfield.string("sortcut")

}

val PartnerTitle.toto :PlatypusBooleanProperty<PartnerTitleEntity>
    get() = PartnerTitleExtend.toto

object PartnerTitleExtend : Model<PartnerTitleEntity>() {
     init {
         PartnerTitle.name
     }

    val toto = newfield.boolean()
}