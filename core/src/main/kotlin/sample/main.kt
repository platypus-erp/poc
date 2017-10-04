package platypus.entity

import org.platypus.core.orm.methods.OnChangeMethodDef
import org.platypus.core.orm.methods.OnChangeResult

/**
 * @author chmuchme
 * @since 0.1
 * on 08/09/17.
 */
fun testOnChangge(args: Array<String>) {
    val onchange = OnChangeMethodDef<MutableList<String>>(
            { entity, res ->
                println("a " + res.ctx)
                res.Super(entity)
                res.ctx.put("KEY2", 2)
                entity.add("hahah")
                println("a " + res.ctx)
            }
    )
    onchange.extend { entity, res  ->
        println("o "+res.ctx)
        res.ctx.put("KEY7", 2)
        res.ctx["KEY1"] = true
        res.withContext("KEY3" to 3).Super(entity)
        entity.add("ohohoh")
        println("o "+res.ctx)
    }

    onchange.extend { entity, res  ->
        println("I "+res.ctx)
        res.ctx.put("KEY4", 2)
        res.Super(entity)
        entity.add("ihihihi")
        println("I "+res.ctx)
    }

    val res = OnChangeResult(onchange)
    res.ctx.put("KEY1", 1)
    val l = mutableListOf<String>()
    res.Super(l)

    println(l)
    println(res.ctx)
}