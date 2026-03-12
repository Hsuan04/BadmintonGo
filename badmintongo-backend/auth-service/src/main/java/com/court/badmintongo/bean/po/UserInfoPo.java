package com.court.badmintongo.bean.po;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user_info")
@Data
public class UserInfoPo {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String source = "LOCAL";

    private boolean enabled = true;

    // 多對多關聯：一個使用者可以有多個角色，一個角色也可以給多個使用者
    @ManyToMany(fetch = FetchType.EAGER) // EAGER 代表撈使用者時順便把角色撈出來
    @JoinTable(
            name = "user_roles", // 中間表名稱
            joinColumns = @JoinColumn(name = "user_id"), // 本表在中間表的外鍵
            inverseJoinColumns = @JoinColumn(name = "role_id") // 對方表在中間表的外鍵
    )
    private Set<RolePo> roles = new HashSet<>();
}
