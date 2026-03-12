package com.court.badmintongo.bean.po;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "system_config")
@IdClass(SystemConfigId.class)
public class SystemConfigPo {

    @Id
    @Column(name = "type_code")
    private String typeCode;

    @Id
    @Column(name = "item_key")
    private String itemKey;

    @Column(name = "item_value")
    private String itemValue;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_enabled")
    private Boolean isEnabled;
}


