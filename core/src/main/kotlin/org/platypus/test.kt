package org.platypus

import org.platypus.core.FormViewNotFoundException
import org.platypus.core.ModelNotFoundException
import org.platypus.core.RequestParameterNotFound
import org.platypus.core.orm.TableRegistry
import org.platypus.core.ui.ViewTypeRegistry
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.*

/**
 * @author chmuchme
 * @since 0.1
 * on 04/09/17.
 */
fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8080) {
        routing {
            route("/onchange") {
                get("{modelName}") {

                }
                put("{modelName}/{choice}") {

                }

            }
            route("/model") {
                get("{modelName}/{id}") {
                    val modelName = call.parameters["modelName"] as String
                    val id = call.parameters["id"]?.toInt() ?: throw RequestParameterNotFound("id", call.request.toString())
                    val model = TableRegistry.tables[modelName] ?: throw ModelNotFoundException(modelName)
                    val entity = model[id]
                    call.respondText(entity.toJson().toJsonString(), ContentType.Text.Html)
                }
                get("/{modelName}/") {
                    val modelName = call.parameters["modelName"] as String
                    val limit = call.parameters["limit"]?.toInt() ?: 80
                    val fields = call.parameters["fields"]
                    val groubBy = call.parameters["groupBy"]
                    val customFilter = call.parameters["customFilter"]
                    val filter = call.parameters["filter"]
//                    val model = TableRegistry.tables[modelName] ?: throw ModelNotFoundException(modelName)
//                    val entity = model[id]
                    call.respondText("$call : $modelName limit=$limit fields=$fields groubBy=$groubBy customFilter=$customFilter filter=gcf$filter", ContentType.Text.Html)
                }
            }
            route("/view") {
                get("{modelName}/{viewType}/{idForm}") {
                    val modelName = call.parameters["modelName"] as String
                    val idForm = call.parameters["idForm"] as String
                    val viewType = ViewTypeRegistry[call.parameters["viewType"] as String]
                    val model = TableRegistry.tables[modelName]?.model ?: throw ModelNotFoundException(modelName)
                    val form = model.views[viewType, idForm] ?: throw FormViewNotFoundException(modelName, idForm)
                    call.respondText(form.compiledView, ContentType.Text.Html)
                }
            }
            route("/test"){
                get {
                    val modelName = call.parameters["limit"] as String?
                    call.respondText("Test ${modelName ?: "no limit"}", ContentType.Text.Html)
                }
            }

        }
    }
    server.start(wait = true)
}


