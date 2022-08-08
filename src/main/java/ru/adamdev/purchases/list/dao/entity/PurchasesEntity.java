package ru.adamdev.purchases.list.dao.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "purchases")
public class PurchasesEntity {

    @Id
    @SequenceGenerator(name = "SEQ_PURCHASES", sequenceName = "SEQ_PURCHASES")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PURCHASES")
    private Long id;
    @OneToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "id")
    private BuyerEntity buyer;
    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private ProductsEntity product;
    @Column(name = "date_of_purchase")
    private LocalDateTime dateOfPurchase;
}
