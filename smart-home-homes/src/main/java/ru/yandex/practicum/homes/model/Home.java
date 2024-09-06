package ru.yandex.practicum.homes.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "homes")
@Data
public class Home {

    @Id
    @Column(name = "home_id", nullable = false)
    private String homeId;

    @Column(name = "home_name", nullable = false)
    private String homeName;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @ElementCollection
    @CollectionTable(name = "shared_users", joinColumns = @JoinColumn(name = "home_id"))
    @Column(name = "user_id")
    private List<String> sharedUserIds;

    public boolean hasAccess(String userId) {
        return this.ownerId.equals(userId) || this.sharedUserIds.contains(userId);
    }
}
