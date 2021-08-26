package com.orileo.attendance.wrapper.entity.mysql;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Setter
@Getter
@Table(name="shift")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @Column(name="start_time")
    private Time startTime;

    @Column(name="end_time")
    private Time endTime;
}