package com.drestaurant.admin

import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.EventProcessor
import org.axonframework.eventhandling.EventTrackerStatus
import org.axonframework.eventhandling.TrackingEventProcessor
import org.springframework.stereotype.Component
import java.util.*

@Component
class AxonAdministration(private val eventProcessingConfiguration: EventProcessingConfiguration) {

    fun resetTrackingEventProcessor(processingGroup: String) = eventProcessingConfiguration
            .eventProcessorByProcessingGroup(processingGroup, TrackingEventProcessor::class.java)
            .ifPresent {
                it.shutDown()
                it.resetTokens()
                it.start()
            }

    fun getTrackingEventProcessors(): List<EventProcessor> = eventProcessingConfiguration.eventProcessors().values.filterIsInstance(TrackingEventProcessor::class.java)

    fun getEventProcessor(processingGroup: String): Optional<EventProcessor> = eventProcessingConfiguration.eventProcessorByProcessingGroup(processingGroup)

    // Returns a map where the key is the segment identifier, and the value is the event processing status. Based on this status we can determine whether the Processor is caught up and/or is replaying
    fun getTrackingEventProcessorStatus(processingGroup: String): Map<Int, EventTrackerStatus> {
        val trackingEventProcessor: Optional<TrackingEventProcessor> = eventProcessingConfiguration.eventProcessorByProcessingGroup(processingGroup)
        return trackingEventProcessor.map { it.processingStatus() }.orElseGet { Collections.emptyMap() }
    }

}