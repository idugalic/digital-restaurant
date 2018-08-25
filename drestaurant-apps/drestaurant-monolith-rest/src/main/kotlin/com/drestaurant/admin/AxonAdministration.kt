package com.drestaurant.admin

import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.EventProcessor
import org.axonframework.eventhandling.TrackingEventProcessor
import org.springframework.stereotype.Component
import java.util.*

@Component
class AxonAdministration(private val eventProcessingConfiguration: EventProcessingConfiguration) {

    fun resetTrackingEventProcessor(processingGroup: String) {
        eventProcessingConfiguration
                .eventProcessorByProcessingGroup(processingGroup, TrackingEventProcessor::class.java)
                .ifPresent {
                    it.shutDown()
                    it.resetTokens()
                    it.start()
                }
    }

    fun getTrackingEventProcessors(): List<EventProcessor> {
        return eventProcessingConfiguration.eventProcessors().values.filterIsInstance(TrackingEventProcessor::class.java)
    }

    fun getEventProcessor(processingGroup: String): Optional<EventProcessor> {
        return eventProcessingConfiguration.eventProcessorByProcessingGroup(processingGroup)
    }

}