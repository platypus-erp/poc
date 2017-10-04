package platypus.entity

import org.platypus.modules.base.CurrencyPosition
import org.platypus.modules.base.Groups
import org.platypus.modules.base.Users
import org.platypus.core.orm.Model
import org.platypus.core.orm.fields.SelectionType

/**
 * @author chmuchme
 * @since 0.1
 * on 07/09/17.
 */
val allRootModel = setOf<Model<*>>(Users, Groups)
val allRootSelection = setOf<SelectionType>(CurrencyPosition)