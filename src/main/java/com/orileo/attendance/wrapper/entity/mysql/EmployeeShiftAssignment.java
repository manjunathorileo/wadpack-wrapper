package com.orileo.attendance.wrapper.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "employee_shift_assignment")
public class EmployeeShiftAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private Employee employee;

    @OneToOne
    private Shift shift;

    @Column(name="week_no")
    private int weekNo;

    @CreationTimestamp
    @JsonFormat(pattern ="yyyy-MM-dd")
    @Column(name="created_on")
    private Date createdOn;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern ="yyyy-MM-dd")
    @Column(name="from_date")
    private Date fromDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern ="yyyy-MM-dd")
    @Column(name="to_date")
    private Date toDate;
}
