package com.shiptrack.service;

import com.shiptrack.entity.Shipment;
import com.shiptrack.entity.TrackingHistory;
import com.shiptrack.repository.ShipmentRepository;
import com.shiptrack.repository.TrackingHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private TrackingHistoryRepository trackingHistoryRepository;

    @InjectMocks
    private ShipmentService shipmentService;

    @Test
    void createShipmentSkipsExistingTrackingNumberAndUsesNextAvailable() {
        Shipment latestShipment = mock(Shipment.class);
        Shipment newShipment = new Shipment();

        doReturn(0L).when(latestShipment).getId();

        when(shipmentRepository.findTopByOrderByIdDesc())
                .thenReturn(Optional.of(latestShipment));
        when(shipmentRepository.existsByTrackingNumber("SHIP20260001"))
                .thenReturn(true);
        when(shipmentRepository.existsByTrackingNumber("SHIP20260002"))
                .thenReturn(false);
        when(shipmentRepository.save(newShipment))
                .thenAnswer(invocation -> {
                    Shipment shipment = invocation.getArgument(0);
                    return shipment;
                });
        when(trackingHistoryRepository.save(any(TrackingHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Shipment createdShipment = shipmentService.createShipment(newShipment);

        assertEquals("SHIP20260002", createdShipment.getTrackingNumber());
        verify(trackingHistoryRepository).save(any(TrackingHistory.class));
    }

    @Test
    void deleteShipmentRemovesTrackingHistoryBeforeShipment() {
        Shipment shipment = new Shipment();

        when(shipmentRepository.existsById(1L))
                .thenReturn(true);

        shipmentService.deleteShipment(1L);

        verify(shipmentRepository)
                .deleteTrackingHistoryByShipmentIdNative(1L);
        verify(shipmentRepository)
                .deleteByIdNative(1L);
    }

    @Test
    void deleteShipmentThrowsWhenShipmentDoesNotExist() {
        when(shipmentRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> shipmentService.deleteShipment(99L));

        verify(shipmentRepository, never())
                .deleteTrackingHistoryByShipmentIdNative(99L);
    }
}
