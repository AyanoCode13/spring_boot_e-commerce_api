package com.e_commerce.app.data.entities.category;

import com.e_commerce.app.data.entities.product.ProductEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryEntity parent;


    @JsonManagedReference
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<CategoryEntity> subCategories;

    @JsonBackReference
    @OneToMany(mappedBy = "category")
    private List<ProductEntity> products;
}