package modelforops.domain;

import java.util.*;
import lombok.*;
import modelforops.domain.*;
import modelforops.infra.AbstractEvent;

@Data
@ToString
public class DeliveryStarted extends AbstractEvent {

    private Long id;
    private Long productId;
    private String productName;
    private Integer qty;
    private Long orderId;
    private String address;
    private String status;    
}
