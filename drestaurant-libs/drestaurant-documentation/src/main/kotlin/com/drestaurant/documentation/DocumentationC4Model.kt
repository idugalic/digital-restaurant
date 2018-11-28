package com.drestaurant.documentation

import com.structurizr.Workspace
import com.structurizr.io.plantuml.PlantUMLWriter
import com.structurizr.model.Model
import com.structurizr.model.Tags
import com.structurizr.view.Shape
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.io.StringWriter


/**
 * Prints the PlantUML 'C4 arch model' UML diagram in the console
 * https://c4model.com/
 * Copy-paste PlantUML diagram to http://www.plantuml.com/plantuml to visualize your architecture.
 */
@Component
@Order(1)
class DocumentationPlantUMLRenderer(val workspace: Workspace) : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val stringWriter = StringWriter()
        stringWriter.append("#####################################################################\n")
        stringWriter.append("## Copy-paste PlantUML diagram to http://www.plantuml.com/plantuml ##\n")
        stringWriter.append("######################### start #####################################\n")
        val plantUMLWriter = PlantUMLWriter()
        plantUMLWriter.write(workspace, stringWriter)
        stringWriter.append("########################## end #####################################\n")
        println(stringWriter.toString())
    }
}

object MyTags {
    const val Database: String = "Database"
}

@Component
class ViewConfigurer(workspace: Workspace) {

    init {
        with(workspace.views.configuration.styles) {
            addElementStyle(Tags.PERSON).shape(Shape.Person)
            addElementStyle(MyTags.Database).shape(Shape.Cylinder)
            addElementStyle(Tags.COMPONENT).shape(Shape.Hexagon)
        }
    }
}

@Component
class Personas(model: Model) {
    val customer = model.addPerson("Customer", "orders food")
    val courier = model.addPerson("Courier", "hero, who delivers food")
    val managerOfRestaurant = model.addPerson("Restaurant manager", "takes the order and organizes preparation")

}
