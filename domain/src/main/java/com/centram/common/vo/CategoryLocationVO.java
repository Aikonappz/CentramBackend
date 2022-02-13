package com.centram.common.vo;


import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CategoryLocationVO implements Serializable {
    private static final long serialVersionUID = -6554121568157662441L;
    private BigInteger categoryId;
    private String categoryName;
    private BigInteger subCategoryId;
    private String subCategoryName;
    private BigInteger locationId;
    private String locationName;
}