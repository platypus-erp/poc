package org.platypus.core.orm

import org.platypus.core.MutliMethodContext
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.platypus.core.orm.methods.MultiMethodResult
import org.platypus.core.orm.methods.OnChangeResult

/**
 * @author chmuchme
 * @since 0.1
 * on 27/08/17.
 */
interface Method


class PlatypusMethodsFactory<ENTITY:PlatypusEntity>{

    fun <PARAM> multi(comp: (entity: ENTITY, param: PARAM, res: MultiMethodResult<ENTITY, PARAM>) -> Unit){}
    fun one(){}
    fun model(){}
}

enum class ComputeType {
    GET, SET, UNKNOW
}

interface ComputeMethod<E : PlatypusEntity> {
    operator fun invoke(entity: E)
}


infix fun Column<*>.with(next: Column<EntityID<Int>>): PathColumn {
    return PathColumn(this, PathColumn(next))
}

class PathColumn(val col: Column<*>?, val previous: PathColumn? = null) {
    infix fun with(next: Column<EntityID<Int>>): PathColumn {
        return PathColumn(next, this)
    }

    val Column<*>.toPath: PathColumn
        get() = PathColumn(this)
}

class MultiMethodDef<E : PlatypusEntity>(val comp: (entity: E, ctx: MutliMethodContext<E>) -> Unit)



class ComputeMethodDef<E : PlatypusEntity> private constructor(
        internal val depends: MutableSet<Column<*>>,
        internal val type: ComputeType,
        internal val comp: (entity: E, Super: ComputeMethod<E>) -> Unit,
        internal val previous: ComputeMethodDef<E>?
) : Method, ComputeMethod<E> {

    constructor(
            depends: MutableSet<Column<*>>, type: ComputeType, comp: (entity: E, Super: ComputeMethod<E>) -> Unit
    ) : this(depends, type, comp, null)

    fun replace(comp: (entity: E, Super: ComputeMethod<E>) -> Unit): ComputeMethodDef<E> {
        return ComputeMethodDef(depends, type, comp)
    }

    fun extend(comp: (entity: E, Super: ComputeMethod<E>) -> Unit): ComputeMethodDef<E> {
        return ComputeMethodDef(depends, type, comp, this)
    }

    fun deleteDepends(vararg dependsToDelete: Column<*>): ComputeMethodDef<E> {
        dependsToDelete.forEach {
            this.depends.remove(it)
        }
        return this
    }

    fun addDepends(vararg dependsToAdd: Column<*>): ComputeMethodDef<E> {
        dependsToAdd.forEach {
            this.depends.add(it)
        }
        return this
    }

    fun replaceDepends(vararg newDepends: Column<*>): ComputeMethodDef<E> {
        this.depends.clear()
        newDepends.forEach {
            this.depends.add(it)
        }
        return this
    }

    override operator fun invoke(entity: E) {
        this.previous?.let { this.comp(entity, it) }
    }
}