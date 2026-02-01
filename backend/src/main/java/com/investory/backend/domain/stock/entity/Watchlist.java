package com.investory.backend.domain.stock.entity;

import com.investory.backend.domain.user.entity.User;
import com.investory.backend.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "watchlists", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "stock_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Watchlist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(length = 50)
    @Builder.Default
    private String groupName = "기본";

    @Column
    @Builder.Default
    private Integer sortOrder = 0;

    public void updateGroup(String groupName) {
        this.groupName = groupName;
    }

    public void updateSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
