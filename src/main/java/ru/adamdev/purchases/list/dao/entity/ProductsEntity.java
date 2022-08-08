package ru.adamdev.purchases.list.dao.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "products")
public class ProductsEntity {

    @Id
    @SequenceGenerator(name = "SEQ_PRODUCTS", sequenceName = "SEQ_PRODUCTS")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRODUCTS")
    private Long id;
    private String name;
    private BigDecimal price;
}
