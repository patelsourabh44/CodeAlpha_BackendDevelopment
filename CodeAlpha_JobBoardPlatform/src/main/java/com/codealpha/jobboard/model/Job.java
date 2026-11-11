package com.codealpha.jobboard.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "jobs")
@Getter @Setter @NoArgsConstructor
public class Job {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String location;

    /** e.g. FULL_TIME, PART_TIME, INTERNSHIP, CONTRACT */
    @Column(nullable = false)
    private String jobType;

    /** Annual salary (INR). 0 = not disclosed. */
    private long salary;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User employer;

    @Column(nullable = false)
    private Instant postedAt = Instant.now();

    private boolean active = true;
}
