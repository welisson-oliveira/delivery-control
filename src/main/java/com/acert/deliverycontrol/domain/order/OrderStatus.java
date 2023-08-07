package com.acert.deliverycontrol.domain.order;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum OrderStatus {
    CREATED {
        @Override
        public List<OrderStatus> nextStatus() {
            return Arrays.asList(OrderStatus.CANCELED, OrderStatus.IN_PROGRESS);
        }
    }, IN_PROGRESS {
        @Override
        public List<OrderStatus> nextStatus() {
            return Arrays.asList(OrderStatus.CANCELED, OrderStatus.DONE);
        }
    }, CANCELED {
        @Override
        public List<OrderStatus> nextStatus() {
            return Collections.emptyList();
        }
    }, DONE {
        @Override
        public List<OrderStatus> nextStatus() {
            return Collections.emptyList();
        }
    };

    public abstract List<OrderStatus> nextStatus();
}
