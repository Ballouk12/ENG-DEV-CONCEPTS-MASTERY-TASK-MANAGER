package com.ma.task.entity;

import com.ma.task.enums.TaskStatus;
import com.ma.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false , length=100)
    private String title;
    @Column(length = 1000)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TaskStatus status;
    @Column(name="created_at" , nullable=false , updatable = false)
    private Instant createdAt;
    @Version
    private Long version;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}
