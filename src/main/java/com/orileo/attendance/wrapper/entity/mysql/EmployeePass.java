package com.orileo.attendance.wrapper.entity.mysql;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "employee_pass")
public class EmployeePass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "start_date")
    private Date StartDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "emp_id")
    private long empId;
    private boolean valid;
}
