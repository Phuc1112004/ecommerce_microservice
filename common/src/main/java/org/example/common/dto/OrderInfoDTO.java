    package org.example.common.dto;

    import lombok.Data;

    @Data
    public class OrderInfoDTO {
        private Long orderId;
        private Long userId;
        private Long totalAmount;
        private String status;
        private String receiver;
        private String shippingAddress;
    }
