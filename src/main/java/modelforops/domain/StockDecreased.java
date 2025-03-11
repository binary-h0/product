package modelforops.domain;

import java.util.*;
import lombok.*;
import modelforops.domain.*;
import modelforops.infra.AbstractEvent;

@Data
@ToString
public class StockDecreased extends AbstractEvent {

    private Long id;

    public StockDecreased(Inventory aggregate) {
        super(aggregate);
    }

    public StockDecreased() {
        super();
    }
}
