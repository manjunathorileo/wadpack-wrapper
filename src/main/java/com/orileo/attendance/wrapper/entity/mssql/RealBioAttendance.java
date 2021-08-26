package com.orileo.attendance.wrapper.entity.mssql;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "GISLogs")
public class RealBioAttendance {

    private String flag;

    @Column(name = "employee_id")
    private String userId;

    @Column(name = "transaction_date")
//    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date logDate;

    @Column(name = "log_date")
    private Date transactionDate;

    @Id
    @Column(name = "log_time")
    private Date logTime;

}