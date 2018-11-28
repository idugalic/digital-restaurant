package com.drestaurant.documentation

import cc.catalysts.structurizr.ViewProvider
import cc.catalysts.structurizr.kotlin.addComponent
import cc.catalysts.structurizr.kotlin.addContainer
import cc.catalysts.structurizr.kotlin.addSoftwareSystem
import com.structurizr.model.Model
import com.structurizr.view.ViewSet
import org.springframework.stereotype.Component


@Component
class SoftwareSystemContextC4Model(model: Model, personas: Personas) : ViewProvider {

    val softwareSystem = model.addSoftwareSystem("Digital restaurant system (monolith-websockets)", "software system") {
        usedBy(personas.customer, "order food and track the order status", "WebSockets")
        usedBy(personas.courier, "claim the order and deliver", "WebSockets")
        usedBy(personas.managerOfRestaurant, "prepare the order and track the status", "WebSockets")
    }

    val database = softwareSystem.addContainer("Database", "in-memory database", "H2") {
        withTags(MyTags.Database)
    }

    val backend = softwareSystem.addContainer("Backend", "", "Spring Boot, Embedded Tomcat") {
        uses(database, "store the data", "SQL")
        usedBy(personas.customer, "order food and track the order status", "WebSockets")
        usedBy(personas.courier, "claim the order and deliver", "WebSockets")
        usedBy(personas.managerOfRestaurant, "prepare food and track the kitchen order status", "WebSockets")
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
        usedBy(personas.customer, "order food", "WebSockets")
        usedBy(personas.courier, "claim the order", "WebSockets")
        usedBy(personas.managerOfRestaurant, "prepare the order", "WebSockets")
    }

    val webQueryComponent = backend.addComponent("Web - Query side", "web component / adapter", "Spring Data REST, Axon") {
        uses(customerComponent, "listen", "events")
        uses(courierComponent, "listen", "events")
        uses(restaurantComponent, "listen", "events")
        uses(orderComponent, "listen", "events")
        uses(database, "projections store", "SQL")
        usedBy(personas.customer, "read customer projections and subscribe to updates", "WebSockets")
        usedBy(personas.courier, "read courier projections and subscribe to updates", "WebSockets")
        usedBy(personas.managerOfRestaurant, "read restaurant projections and subscribe to updates", "WebSockets")
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
