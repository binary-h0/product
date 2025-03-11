package modelforops.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import modelforops.ProductApplication;
import modelforops.domain.StockDecreased;

@Entity
@Table(name = "Inventory_table")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productName;

    private Integer stock;

    @PostPersist
    public void onPostPersist() {
        StockDecreased stockDecreased = new StockDecreased(this);
        stockDecreased.publishAfterCommit();
    }

    public static InventoryRepository repository() {
        InventoryRepository inventoryRepository = ProductApplication.applicationContext.getBean(
            InventoryRepository.class
        );
        return inventoryRepository;
    }

    public static void decreaseStock(DeliveryStarted deliveryStarted) {
        
       
        repository().findById(deliveryStarted.getProductId()).ifPresent(inventory->{
            
            //inventory // do something
            inventory.setStock(inventory.getStock() - deliveryStarted.getQty());
            repository().save(inventory);

            StockDecreased stockDecreased = new StockDecreased(inventory);
            stockDecreased.publishAfterCommit();

         });


    }
}
