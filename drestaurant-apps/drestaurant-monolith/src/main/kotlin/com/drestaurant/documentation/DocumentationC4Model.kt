package com.drestaurant.documentation

import cc.catalysts.structurizr.ViewProvider
import cc.catalysts.structurizr.kotlin.addComponent
import cc.catalysts.structurizr.kotlin.addContainer
import cc.catalysts.structurizr.kotlin.addSoftwareSystem
import com.structurizr.Workspace
import com.structurizr.io.plantuml.PlantUMLWriter
import com.structurizr.model.Model
import com.structurizr.model.Tags
import com.structurizr.view.Shape
import com.structurizr.view.ViewSet
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
class ViewConfigurer(workspace: Workspace){

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


@Component
class SoftwareSystem(model: Model, personas: Personas) : ViewProvider {

    val softwareSystem = model.addSoftwareSystem("Digital restaurant system (monolith)", "software system") {
        usedBy(personas.customer, "order food and track/query the order", "REST/HTTP")
        usedBy(personas.courier, "claim the order", "REST/HTTP")
        usedBy(personas.managerOfRestaurant, "prepare and query the order", "REST/HTTP")
    }

    val database = softwareSystem.addContainer("Database", "in-memory database", "H2") {
        withTags(MyTags.Database)
    }

    val backend = softwareSystem.addContainer("Backend", "", "Spring Boot, Embedded Tomcat") {
        uses(database, "store the data", "SQL")
        usedBy(personas.customer, "order food and track/query the order", "REST/HTTP")
        usedBy(personas.courier, "claim the order", "REST/HTTP")
        usedBy(personas.managerOfRestaurant, "prepare and query the order", "REST/HTTP")
    }
    //TODO Consider implementing strategy to discover components automatically ...
    val courierComponent = backend.addComponent("Courier", "domain component", "Spring, Axon") {
        uses(database, "event/saga/token store", "SQL")
    }

    val customerComponent = backend.addComponent("Customer", "domain component", "Spring, Axon") {
        uses(database, "event/saga/token store", "SQL")
    }

    val restaurantComponent = backend.addComponent("Restaurant", "domain component", "Spring, Axon") {
        uses(database, "event/saga/token store", "SQL")
    }

    val orderComponent = backend.addComponent("Order", "domain component", "Spring, Axon") {
        uses(database, "event/saga/token store", "SQL")
        uses(customerComponent, "send", "commands")
        uses(customerComponent, "listen", "events")
        uses(courierComponent, "send", "commands")
        uses(courierComponent, "listen", "events")
        uses(restaurantComponent, "send", "commands")
        uses(restaurantComponent, "listen", "events")
    }

    val webCommandComponent = backend.addComponent("Web - Command side", "web component / adapter", "Spring MVC, Axon") {
        uses(customerComponent, "send", "commands")
        uses(courierComponent, "send", "commands")
        uses(restaurantComponent, "send", "commands")
        uses(orderComponent, "send", "commands")
        usedBy(personas.customer, "order food", "REST/HTTP")
        usedBy(personas.courier, "claim the order", "REST/HTTP")
        usedBy(personas.managerOfRestaurant, "prepare the order", "REST/HTTP")
    }

    val webQueryComponent = backend.addComponent("Web - Query side", "web component / adapter", "Spring Data REST, Axon") {
        uses(customerComponent, "listen", "events")
        uses(courierComponent, "listen", "events")
        uses(restaurantComponent, "listen", "events")
        uses(orderComponent, "listen", "events")
        uses(database, "projections store", "SQL")
        usedBy(personas.customer, "read customer projections", "REST/HTTP")
        usedBy(personas.courier, "read courier projections", "REST/HTTP")
        usedBy(personas.managerOfRestaurant, "read restaurant projections", "REST/HTTP")
    }

    override fun createViews(viewSet: ViewSet) {
        viewSet.createSystemContextView(softwareSystem, "drestaurant-monolith-context", "").also {
            it.addAllPeople()
        }
        viewSet.createContainerView(softwareSystem, "drestaurant-monolith-containers", "").also {
            it.addAllContainers()
            it.addAllInfluencers()
        }
        viewSet.createComponentView(backend, "drestaurant-monolith-backend-components", "").also {
            it.addAllComponents()
            it.addAllPeople()
        }
    }
}
