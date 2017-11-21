package org.platypus.core

import org.platypus.core.orm.PlatypusEntity

/**
 * @author chmuchme
 * @since 0.1
 * on 16/08/17.
 */

class AppCtx {
    val entity = AppEntity(this)

    val lang = ""
    val uid: Int = 0
}




enum class KassiopiaStep {
    BOOT, FIRST_INSTALL, UPDATE, INIT, RUN, SHUTDOWN
}

object KassiopiaErp {
    var currentStep = KassiopiaStep.BOOT
        private set

    fun boot() {

    }

    fun init() {

    }

    fun run() {

    }

    fun shutdown() {

    }
}

class AppEntity(internal val ctx: AppCtx)


interface MethodContext {
    val entity: AppEntity
    /**
     * This method should be call when you want to change/add some value to the [context]
     * <pre>
     *     ctx.withContext("key" keyOf value)
     * </pre>
     * The value can be any type, Int, Boolean, Entity
     * @return a new instance of MethodContext with the [context] altered with the [value]
     */
    fun withContext(vararg value: Pair<String, Any>): MethodContext
}



interface MutliMethodContext<E : PlatypusEntity<E>> : MethodContext {

    fun Super(entity: E)
    override fun withContext(vararg value: Pair<String, Any>): MutliMethodContext<E>


}

object Widgets {

    val HTML = HtmlWidget()
}

fun String.i18n(): String {
    return this
}

interface ColumnWidget {


}

class HtmlWidget : ColumnWidget