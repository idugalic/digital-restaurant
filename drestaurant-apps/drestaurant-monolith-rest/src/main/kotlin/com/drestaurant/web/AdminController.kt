package com.drestaurant.web

import com.drestaurant.admin.AxonAdministration
import org.axonframework.eventhandling.EventProcessor
import org.axonframework.eventhandling.EventTrackerStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class AdminController(private val axonAdministration: AxonAdministration) {

    @RequestMapping(value = ["/administration/eventprocessors"], method = [RequestMethod.GET])
    fun getEventProcessors(): List<EventProcessor> = axonAdministration.getTrackingEventProcessors()

    @RequestMapping(value = ["/administration/eventprocessors/{groupName}"], method = [RequestMethod.GET])
    fun getEventProcessor(@PathVariable groupName: String): Optional<EventProcessor> = axonAdministration.getEventProcessor(groupName)

    /*
    Returns a map where the key is the segment identifier, and the value is the event processing status.
    Based on this status we can determine whether the Processor is caught up and/or is replaying.
    This can be used to implement Blue-Green deployment. You don't want to send queries to 'view model' if Processor is not caught up and/or is replaying.
    */
    @RequestMapping(value = ["/administration/eventprocessors/{groupName}/status"], method = [RequestMethod.GET])
    fun getEventProcessorStatus(@PathVariable groupName: String): Map<Int, EventTrackerStatus> = axonAdministration.getTrackingEventProcessorStatus(groupName)

    @RequestMapping(value = ["/administration/eventprocessors/{groupName}/reply"], method = [RequestMethod.POST])
    fun replyEventProcessor(@PathVariable groupName: String): ResponseEntity<Any> {
        axonAdministration.resetTrackingEventProcessor(groupName)
        return ResponseEntity.accepted().build()
    }

}