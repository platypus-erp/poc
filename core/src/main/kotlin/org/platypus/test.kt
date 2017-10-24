package org.platypus

import org.jetbrains.exposed.dao.EntityID
import org.platypus.core.orm.Model
import org.platypus.core.orm.PlatypusEntity
import org.platypus.core.orm.methods.*

data class MethodParamsTest1(var s: String) : MultiMethodParams, OneMethodParams, StaticMethodParams
data class MethodReturnTest1(var s: String) : MultiMethodReturn, OneMethodReturn, StaticMethodReturn

class PartnerEntityTest(id: EntityID<Long>) : PlatypusEntity(id)
object PartnerTest : Model<PartnerEntityTest>() {

    val method1 = newMethod.one { entity, param: MethodParamsTest1, res->
        println("method1 $param")
    }

    val method2 = method1.extend { entity, param: MethodParamsTest1, res->
        println("method2 $param")
    }

    val method3 = method1.extend { entity, param: MethodParamsTest1, res->
        println("method3 $param")
    }
}