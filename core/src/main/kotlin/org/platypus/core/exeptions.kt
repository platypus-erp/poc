package org.platypus.core

import org.platypus.core.orm.fields.SelectionType

/**
 * @author chmuchme
 * @since 0.1
 * on 07/09/17.
 */
open class NotFoundException(prefix: String, suffix: String = "") : RuntimeException("$prefix not found $suffix")

class ModelNotFoundException(modelName: String) : NotFoundException("Model[$modelName]")
class EntityNotFoundException(modelName: String, id: Int) : NotFoundException("Entity[$modelName] with id=$id")


abstract class ViewNotFoundException(viewType: String, modelName: String, viewId: String) : NotFoundException("$viewType with the id[$viewId]")
class FormViewNotFoundException(modelName: String, viewId: String) : ViewNotFoundException("Form", modelName, viewId)
class TreeViewNotFoundException(modelName: String, viewId: String) : ViewNotFoundException("Tree", modelName, viewId)
class KanbanViewNotFoundException(modelName: String, viewId: String) : ViewNotFoundException("Kanban", modelName, viewId)
class ChartViewNotFoundException(modelName: String, viewId: String) : ViewNotFoundException("Chart", modelName, viewId)
class PivotTableViewNotFoundException(modelName: String, viewId: String) : ViewNotFoundException("Pivot table", modelName, viewId)
class CalendarViewNotFoundException(modelName: String, viewId: String) : ViewNotFoundException("Calendar", modelName, viewId)

class ViewTypeNotFound(name: String) : NotFoundException("ViewType[$name]")

class RequestParameterNotFound(paramName: String, query: String) : NotFoundException("The parameter $paramName", "inside the httpRequest $query")


class UserError(message:String):RuntimeException(message)
class UserWarning(message:String):RuntimeException(message)
class UserInfo(message:String):RuntimeException(message)

class SelectionValueNotFound(t:SelectionType, v:Any?) : NotFoundException("$v not found in the type ${t::class.simpleName}")