package org.platypus.erp.base.models

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SizedIterable
import org.platypus.core.orm.Model
import org.platypus.core.orm.SearchByNameParam
import org.platypus.core.orm.methods.CTX
import org.platypus.core.orm.methods.StaticMethodResultWithReturn

object PartnerCategorie : Model<PartnerCategorieEntity>(PartnerCategorieEntity) {
    val name = field.string("Tag Name", required = true, translate = true, unique = true)
    val color = field.integer("Color Index")
    val parent = field.many2one("Parent", index = true, onDelete = ReferenceOption.CASCADE) of PartnerCategorie
    val children = field.one2many("Parent", "Child Tag") of PartnerCategorie.parent
    val active = field.boolean(help = "The active choice allows you to hide the category without removing it.", defaultValue = true)
    val partner = field.many2many("Partners") of Partner

    val checkParent = newMethod.one(
            fun(entity: PartnerCategorieEntity, p: Nothing, env: CTX<PartnerCategorieEntity, Nothing, Unit>) {
                return env.Super(entity, p)
            })

    private fun iterOverParent(e: PartnerCategorieEntity, f: (e: PartnerCategorieEntity) -> Unit) {
        f(e)
        if (e.parent != null) {
            iterOverParent(e, f)
        }
    }

    init {
        nameGet.extend(
                fun(entity: PartnerCategorieEntity, p: Nothing, env: CTX<PartnerCategorieEntity, Nothing, String>): String {
                    if (env.ctx.asString("partner_category_display") == "short") {
                        return entity.name
                    }
                    val names = ArrayList<String>()
                    iterOverParent(entity, { names.add(it.name) })
                    return names.joinToString(" / ")
                }
        )

        searchByName.extend(
                fun(p: SearchByNameParam, env: StaticMethodResultWithReturn<PartnerCategorieEntity, SearchByNameParam, SizedIterable<PartnerCategorieEntity>>): SizedIterable<PartnerCategorieEntity> {
                    if (p.value.isNotBlank() && p.value.contains(" / ")) {
                        p.value = p.value.split(" / ").last()
                    }
                    return env.Super(p)
                }
        )
    }
}