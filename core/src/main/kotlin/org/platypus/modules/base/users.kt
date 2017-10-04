package org.platypus.modules.base


import platypus.entity.CompanyEntity
import platypus.entity.GroupsEntity
import platypus.entity.UsersEntity
import org.platypus.core.UserError
import org.platypus.core.MutliMethodContext
import org.platypus.core.Widgets
import org.platypus.core.orm.Model
import org.jetbrains.exposed.sql.Column

/**
 * @author chmuchme
 * @since 0.1
 * on 27/08/17.
 */


object Users : Model<UsersEntity>() {

    val partner = many2one("partner") of Partner
    val login = string("login", "Login", maxSize = 64, required = true)
    val password = string("password", "Password", maxSize = 64, ecripted = true, required = true)
    val newPassword = string("newPassword", "Password", maxSize = 64, ecripted = true, required = true)
    val signature = text("signature", wiget = Widgets.HTML)
    val groups = many2many("groups") of Groups.m2m("users")
    val current_company = many2one("compagny") of Company
    val available_company = many2one("compagny") of Company


    val onChangeLogin = onChange { entity, res ->
        if (entity.login.isBlank()) {
            entity.partner.email = entity.login
        }
    }

    private fun set_new_password(e: UsersEntity, value: String) {
        if (value.isBlank()) {
            // Do not update the password if no value is provided, ignore silently.
            // For example web client submits False values for all empty fields.
            return
        }
        if (e.id.value == e.ctx.uid) {
            // To change their own password users must use the client-specific change password wizard,
            // so that the new password is immediately used for further RPC requests, otherwise the user
            // will face unexpected 'Access Denied' exceptions.
            throw UserError("Please use the change password wizard (in User Preferences or User menu) to change your own password.")
        }
        e.password = value
    }

    private fun getPassword(es: List<UsersEntity>): List<UsersEntity> {
        return es.map {
            UsersEntity(it.id, it.ctx)
        }.toList()
    }
//    def _is_share(self, cr, uid, ids, name, args, context=None):
//    res = {}
//    for user in self.browse(cr, uid, ids, context=context):
//    res[user.id] = not self.has_group(cr, user.id, 'base.group_user')
//    return res

//    private fun isShare(es:List<UsersEntity>):List<Map<Int, Boolean>>{
//        return es.map { mapOf(Pair(it.id, it.hasGroup("base.user_group"))) }.toList()
//    }
//
//    private fun hasGroup(es:UsersEntity, param:String){
//        es.groups
//    }

}


object Groups : Model<GroupsEntity>() {
    //    _rec_name = 'full_name'
    override val description: String
        get() = "Access Groups"
    override val order: Array<Column<*>>
        get() = arrayOf(name)

//    _sql_constraints = [
//    ('name_uniq', 'unique (category_id, name)', 'The name of the group must be unique within an application!')
//    ]

    val name = string("name", required = true, translate = true)
    val users = many2many("users") of Users.m2m("groups")
    //    val model_access = One2Many('ir.model.access', 'group_id', 'Access Controls', copy=True),
//    val rule_groups = Many2Many('ir.rule', 'rule_group_rel', group_id', 'rule_group_id', 'Rules', domain=[('global', '=', False)]),
//    val menu_access = many2many('ir.ui.menu', 'ir_ui_menu_group_rel', 'gid', 'menu_id', 'Access Menu'),
//    val view_access = many2many('ir.ui.view', 'ir_ui_view_group_rel', 'group_id', 'view_id', 'Views'),
    val comment = text("Comment", length = 250, translate = true)
    val category = string("category", "Application")
    val color = integer("color", "Color Index")
    //    val full_name = function(_get_full_name, type='char', string='Group Name', fnct_search=_search_group),
    val full_name = string("full_name", string = "Group Name")
    val share = boolean("share", "Share Group",
            help = "Group created to set access rights for sharing data with some users.")

    private fun get_full_name(e: List<GroupsEntity>, Super: MutliMethodContext<GroupsEntity>): List<GroupsEntity> {
        e.forEach {
            if (it.category.isNotBlank()) {
                it.name = "${it.category} / ${it.name}"
            }
        }
        return e
    }

}

object Company : Model<CompanyEntity>() {


}

//@ModelRoot
//object UsersLogs : Model<UsersLogsEntity>() {
//    override val order: Array<Column<*>>
//        get() = arrayOf(id)
//}


