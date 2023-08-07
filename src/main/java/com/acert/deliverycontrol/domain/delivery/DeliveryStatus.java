package com.acert.deliverycontrol.domain.delivery;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DeliveryStatus {
    WAITING {
        @Override
        public List<DeliveryStatus> nextStatus() {
            return Arrays.asList(DeliveryStatus.CANCELED, DeliveryStatus.IN_PROGRESS);
        }
    }, IN_PROGRESS {
        @Override
        public List<DeliveryStatus> nextStatus() {
            return Arrays.asList(DeliveryStatus.CANCELED, DeliveryStatus.DELIVERED);
        }
    }, CANCELED {
        @Override
        public List<DeliveryStatus> nextStatus() {
            return Collections.emptyList();
        }
    }, DELIVERED {
        @Override
        public List<DeliveryStatus> nextStatus() {
            return Collections.emptyList();
        }
    };

    public abstract List<DeliveryStatus> nextStatus();
}
