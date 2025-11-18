    package org.example.common.dto;

    import lombok.Data;
    import org.example.common.dto.kafka.OrderItemDTO;

    import java.util.List;

    @Data
    public class OrderInfoDTO {
        private Long orderId;
        private Long userId;
        private Long totalAmount;
        private String status;
        private String receiver;
        private String shippingAddress;

        private List<OrderItemDTO> items;
    }
