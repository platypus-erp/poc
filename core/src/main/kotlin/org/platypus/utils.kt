package org.platypus

import org.jetbrains.exposed.sql.SizedIterable

/**
 * @author chmuchme
 * @since 0.1
 * on 02/10/17.
 */
fun <T> MutableCollection<T>.addIf(condiftion: Boolean, lazyMessage: () -> T) {
    if (!condiftion) {
        this.add(lazyMessage())
    }
}

val <T : Any> SizedIterable<T>.notEmpty: Boolean
    get() = !this.empty()