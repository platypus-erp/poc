package org.platypus.core.modules

import org.platypus.core.orm.InheritModel
import org.platypus.core.orm.Model

/**
 * @author chmuchme
 * @since 0.1
 * on 07/10/17.
 */
abstract class PlatypusModule{

    abstract val depends:Set<PlatypusModule>

    abstract val allRootModel:() -> Set<Model<*>>
    abstract val allInheritModel:() -> Set<InheritModel<*>>

    fun build(){
        for (dep in depends){
            dep.build()
            dep.allRootModel()
            dep.allInheritModel()
        }
    }

}