package sample

import org.platypus.core.orm.Model
import org.platypus.core.orm.fields.PlatypusBooleanProperty
import org.platypus.core.orm.methods.*
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
    val shorcut = newfield.string()
}

data class MethodParamsTest(var s: String) : MultiMethodParams, OneMethodParams, StaticMethodParams
data class MethodReturnTest(var s: String) : MultiMethodReturn, OneMethodReturn, StaticMethodReturn

object Partner : Model<PartnerEntity>() {
    //Simple String Field
    val name = newfield.string()
    //Compute String Field
    val displayName = compute(newfield.string())
    val ref = computeStore(newfield.string(string = "Internal Reference"))
    val date = newfield.date()
    val title = newfield.many2one("title") of PartnerTitle
    val parent = newfield.many2one("parent") of Partner
    val parentName = newfield.string("Parent name", readonly = true/*, related= arrayOf("parent", "name")*/)
    val email = newfield.string("Email", maxSize = 64, required = true)
    val childIds = newfield.one2many("childIds") of Partner.id
    val lang = newfield.many2one("Language") of ResLang


    val onChangeName = name.onChange { e, ctx ->
        ctx.withContext("TOTO" to 5).Super(e)
    }

    val onChangeMultiProperty = newMethod.group(displayName, email).onChange(this::onChangeTest)

    private fun onChangeTest(e: PartnerEntity, ctx: OnChangeResult<PartnerEntity>) {

    }


    val multiNoReturn = newMethod.multi { e, p: MethodParamsTest, s ->
        s.Super(e, p)
    }

    val multiWithReturn = newMethod.multi(MethodReturnTest::class) { e, p: MethodParamsTest, s ->
        s.Super(e, p) // implicit return
    }

    val oneNoReturn = newMethod.one(MethodParamsTest::class) { e, p, s ->
        s.Super(e, p)
    }

    val oneWithReturn = newMethod.one(MethodParamsTest::class, MethodReturnTest::class) { e, p, s ->
        s.Super(e, p) // implicit return
    }

    val staticNoReturn = newMethod.static(MethodParamsTest::class) { p, s ->
        s.Super(p)
    }

    val staticWithReturn = newMethod.static(MethodParamsTest::class, MethodReturnTest::class) { p, s ->
        s.Super(p) // implicit return
    }

    val compute_get_name = displayName.getter { e, c -> c.Super(e) }
    val compute_set_name = displayName.setter { e, v, c -> c.Super(e, v) }

    val compute_get_ref = ref.getter { e, c -> c.Super(e) }
    val compute_set_ref = ref.setter { e, v, c -> c.Super(e, v) }


}

object PartnerCategorie : Model<PartnerCategorieEntity>() {
    val name = newfield.string("Category Name", required = true, translate = true)
    val color = newfield.integer("color", "Color Index")
    val parent_id = newfield.many2one("parent_id", "Parent Tag") of PartnerCategorie
    //    complete_name = function(_name_get_fnc, type="char", stringColumn="Full Name"),
    val complete_name = newfield.string(string = "Full Name")
    val child_ids = newfield.one2many("parent_id", "Child Tag") of PartnerCategorie.id
    val active = newfield.boolean(help = "The active choice allows you to hide the category without removing it.")
    val parent_left = newfield.integer()
    val parent_right = newfield.integer()
//    partner_id s= many2many("res.partner", id1="category_id", id2="partner_id", stringColumn="Partners"),
}

object PartnerTitle : Model<PartnerTitleEntity>() {
    val name = newfield.string("name")
    val sortcut = newfield.string("sortcut")

}

val PartnerTitle.toto: PlatypusBooleanProperty<PartnerTitleEntity>
    get() = PartnerTitleExtend.toto

object PartnerTitleExtend : Model<PartnerTitleEntity>() {
    init {
        PartnerTitle.name
    }

    val toto = newfield.boolean()
}