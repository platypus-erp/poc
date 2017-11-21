package org.platypus.erp.base.models

import org.platypus.core.orm.Model
import org.platypus.core.orm.fields.SelectionType

/**
 * @author chmuchme
 * @since 0.1
 * on 04/10/17.
 */

enum class PartnerType : SelectionType {
    CONTACT, INVOICE, DELIVRERY, SHIPPING, OTHER
}

enum class CompanyType : SelectionType {

}

object Partner : Model<PartnerEntity>(PartnerEntity) {
    //Simple String Field
    val name = field.string(unique = true)
    val date = field.date(index = true)
    val title = field.many2one("title") of PartnerTitle
    val parent = field.many2one("parent") of Partner
    val parentName = field.string("Parent name", index = true)
    val children = field.one2many("childIds") of Partner.id
    val ref = computeStore(field.string(string = "Internal Reference", unique = true))
    val lang = field.many2one("Language") of ResLang
    val tz = field.string("Timezone",
            help = """The partner's timezone, used to output proper date and time values
inside printed reports. It is important to set a value for this field.
You should use the same timezone that is otherwise used to pick and
render date and time values: your computer's timezone.""")
    val tzOffset = compute(field.string("Timezone Offset"))
    val user = field.many2one("Salesperson",
            help = "The internal user that is in charge of communicating with this contact if any.")

    val vat = field.string("TIN", help = """Tax Identification Number.
Fill it if the company is subjected to taxes.
Used by the some of the legal statements.""")

    val banks = field.one2many("Bank Accounts") of BankAccount.partner
    val website = field.string("Website of Partner or Company")
    val comment = field.text("Notes")
    val categories = field.many2many("Tags") of PartnerCategorie
    val creditlimit = field.decimal()
    val barecode = field.string()
    val active = field.boolean(defaultValue = true)
    val customer = field.boolean("Is a Customer", "Check this box if this contact is a customer.", defaultValue = true)
    val supplier = field.boolean("Is a Supplier", """Check this box if this contact is a vendor.
If it's not checked, purchase people will not see it when encoding a purchase order.""")
    val employee = field.boolean("is a employee", "Check this box if this contact is an Employee.")
    val function = field.string("Job position")
    val type = field.selection("Type", PartnerType::class)
    val street = field.string()
    val street2 = field.string()
    val zip = field.string()
    val city = field.string()
    val state = field.many2one() of CountryState
    val country = field.many2one() of Country
    val email = field.string("Email", maxSize = 64, required = true, unique = true)
    val emailFormatted = compute(field.string())
    val phone = field.string()
    val fax = field.string()
    val mobile = field.string()
    val iscompany = field.boolean()
    val companyType = field.selection(selection = CompanyType::class)
    val company = field.many2one() of Company


    //Compute String Field
    val displayName = compute(field.string(unique = true))


}
