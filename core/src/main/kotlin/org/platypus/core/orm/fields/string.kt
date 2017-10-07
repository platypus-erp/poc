package org.platypus.core.orm.fields

import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.StringColumnType
import org.platypus.addIf
import org.platypus.core.orm.PlatypusEntity
import org.platypus.core.orm.methods.*

/**
 * @author chmuchme
 * @since 0.1
 * on 06/10/17.
 */
open class PlatypusStringProperty<E : PlatypusEntity>(
        string: String,
        help: String,
        var regexp: Regex?,
        var minSize: Int,
        var maxSize: Int,
        var defaultValue: String,
        var prefix: String,
        var suffix: String,
        var required: Boolean,
        var allowBlank: Boolean,
        var readonly: Boolean,
        var trim: TrimType,
        var ecripted: Boolean
) : PlatypusProperty(string, help) {

    fun onChange(comp: (entity: E, param: OnChangeResult<E>) -> Unit) = OnChangeMethodDef(comp)

}

class ComputePlatypusStringProperty<E : PlatypusEntity>(prop: PlatypusStringProperty<E>) : PlatypusStringProperty<E>(
        prop.string, prop.help, prop.regexp, prop.minSize, prop.maxSize,
        prop.defaultValue, prop.prefix, prop.suffix, prop.required,
        prop.allowBlank, prop.readonly, prop.trim, prop.ecripted) {

    fun getter(comp: (entity: E, param: ComputeGetStringResult<E>) -> String) = ComputeGetStringMethodDef(comp)

    fun setter(comp: (entity: E, value: String, param: ComputeSetStringResult<E>) -> Unit) = ComputeSetStringMethodDef(comp)
}

class ComputeStorePlatypusStringProperty<E : PlatypusEntity>(prop: PlatypusStringProperty<E>) : PlatypusStringProperty<E>(
        prop.string, prop.help, prop.regexp, prop.minSize, prop.maxSize,
        prop.defaultValue, prop.prefix, prop.suffix, prop.required,
        prop.allowBlank, prop.readonly, prop.trim, prop.ecripted) {

    fun getter(comp: (entity: E, param: ComputeGetStringResult<E>) -> String) = ComputeGetStringMethodDef(comp)

    fun setter(comp: (entity: E, value: String, param: ComputeSetStringResult<E>) -> Unit) = ComputeSetStringMethodDef(comp)
}

class PlatypusStringColumnType(
        private val tableHolderName: String,
        private val fieldName: String,
        private val regexp: Regex?,
        private val minSize: Int,
        private val maxSize: Int,
        private val defaultValue: String,
        private val prefix: String,
        private val suffix: String,
        private val required: Boolean,
        private val allowBlank: Boolean,
        private val readonly: Boolean,
        private val trim: TrimType,
        private val ecripted: Boolean,
        private val exposedType: StringColumnType = StringColumnType(maxSize)
) : IColumnType by exposedType, PlatypusColumnType<String>(!required) {

    override fun asType(v: Any?): String? = v as String?

    override fun validateNullableValue(valueNull: String?): Set<String> {
        val errors = mutableSetOf<String>()
        errors.addIf(required && valueNull == null) {
            "${tableHolderName}.${fieldName} : the choice is required but don't have default value"
        }
        return errors
    }

    override fun validateNonNullValue(valueNull: String): Set<String> {
        val errors = mutableSetOf<String>()
        val value = valueNull ?: defaultValue
        val valueTrim = when (trim) {
            TrimType.NONE -> value
            TrimType.START -> value.trimStart()
            TrimType.END -> value.trimEnd()
            TrimType.BOTH -> value.trim()
        }
        errors.addIf(valueTrim.length < minSize) {
            "${tableHolderName}.${fieldName} : the choice min size is $minSize " +
                    "and the default value with prefix and sufix is ${valueTrim.length}"
        }

        errors.addIf(valueTrim.length > maxSize) {
            "${tableHolderName}.${fieldName} : the choice max size is $maxSize " +
                    "and the default value with prefix and suffix is ${valueTrim.length}"
        }
        errors.addIf(valueTrim.isBlank() && !allowBlank) {
            "${tableHolderName}.${fieldName} : the choice is blank but the column don't allow blank value"
        }
        errors.addIf(regexp != null && regexp.matches(valueTrim)) {
            "${tableHolderName}.${fieldName} : $valueTrim doesn't match ${regexp!!.pattern}"
        }
        return errors
    }

    override fun notNullValueToDB(value: Any): Any {
        if (value is String) {
            validateNonNullValue(value)
        }
        //TODO ecript data, How to ?
        return exposedType.notNullValueToDB(prefix + value + suffix)
    }


    override fun valueToDB(value: Any?): Any? {
        val valueDefault = value ?: defaultValue
        return exposedType.valueToDB(valueDefault)
    }
}