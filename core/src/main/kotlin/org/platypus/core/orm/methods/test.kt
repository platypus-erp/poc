package org.platypus.core.orm.methods

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.platypus.core.orm.PlatypusEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 04/10/17.
 */
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