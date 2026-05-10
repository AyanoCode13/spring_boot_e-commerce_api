package com.e_commerce.app.data.entities.category;

import com.e_commerce.app.data.entities.product.ProductEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CategoryEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryEntity parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<CategoryEntity> subCategories;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<ProductEntity> products;
}