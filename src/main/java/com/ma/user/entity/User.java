package com.ma.user.entity;

import com.ma.task.entity.Task;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable=false , length=100)
    private String username;
    @OneToMany(mappedBy="user" , fetch=FetchType.LAZY , cascade=CascadeType.ALL)
    private List<Task> tasks;
}
