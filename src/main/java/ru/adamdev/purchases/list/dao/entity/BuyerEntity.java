package ru.adamdev.purchases.list.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "buyer")
public class BuyerEntity {

    @Id
    @SequenceGenerator(name = "SEQ_BUYER", sequenceName = "SEQ_BUYER")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BUYER")
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "second_name")
    private String secondName;

}
