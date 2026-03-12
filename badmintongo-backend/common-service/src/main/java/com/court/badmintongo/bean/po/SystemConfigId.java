package com.court.badmintongo.bean.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigId implements Serializable {
    private String typeCode;
    private String itemKey;

}
