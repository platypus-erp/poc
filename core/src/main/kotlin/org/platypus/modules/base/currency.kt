package org.platypus.modules.base

import platypus.entity.CurrencyEntity
import platypus.entity.CurrencyRateEntity
import org.platypus.core.SelectionExtend
import org.platypus.core.SelectionRoot
import org.platypus.core.orm.Model
import org.platypus.core.orm.fields.SelectionRegistry.choice
import org.platypus.core.orm.fields.SelectionType
import org.platypus.core.orm.fields.createNewSelection
import org.jetbrains.exposed.sql.Column
import java.time.LocalDateTime

/**
 * @author chmuchme
 * @since 0.1
 * on 09/09/17.
 */

@SelectionRoot
object CurrencyPosition : SelectionType() {
    val after = choice("AFTER", "After Amount")
    val before = choice("BEFORE", "Before Amount")
}

@SelectionExtend
val between  = createNewSelection(CurrencyPosition, "BETWEEN", "between decimal")



object Currency : Model<CurrencyEntity>() {

    val name = string("name", "Currency", maxSize = 3, required = true, help = "Currency Code (ISO 4217)")
    val symbol = string("symbol", "Symbol", maxSize = 4, help = "Currency sign, to be used when printing amounts.")
    //    val rate = function(_get_current_rate, stringColumn="Current Rate", digits=(12,6), help="The rate of the currency to the currency of rate 1.")
    val rate = decimal("rate", string = "Current Rate", precision = 12, scale = 6, help = "The rate of the currency to the currency of rate 1.")
    val rate_ids = one2many("rate_ids", "Rates") of CurrencyRate.currency_id
    val rounding = decimal("Rounding Factor", precision = 12, scale = 6)
    //    val decimal_places = function(_decimal_places, stringColumn="Decimal Places", type="integer")
    val decimal_places = integer("decimal_places", string = "Decimal Places")
    val active = boolean("active", "Active")
    val position = selection("position", CurrencyPosition, "Symbol Position", help = "Determines where the currency symbol should be placed after or before the amount.")
//_defaults = {
//    "active": 1,
//    "position" : "after",
//    "rounding": 0.01,
//}
}

object CurrencyRate : Model<CurrencyRateEntity>() {
    override val order: Array<Column<*>>
        get() = arrayOf(name)
    //desc

    val name = datetime("Date", required = true, defaultValue = LocalDateTime.now())
    val rate = decimal("Rate", precision = 12, scale = 6, help = "The rate of the currency to the currency of rate 1")
    val currency_id = many2one("currency_id", "Currency", readonly = true) of Currency
    val company_id = many2one("company_id", "Company") of Company

}

