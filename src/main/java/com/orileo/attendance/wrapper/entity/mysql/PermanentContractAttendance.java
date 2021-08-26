package com.orileo.attendance.wrapper.entity.mysql;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "permanent_contract_attendance")
public class PermanentContractAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "marked_on")
    private Date markedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", length = 45)
    private AttendanceStatus attendanceStatus;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "recorded_time")
    private Date recordedTime;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "IST")
    @Column(name = "in_time")
    private Date inTime;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "IST")
    @Column(name = "out_time")
    private Date outTime;

    @Column(name = "worked_hours")
    private String workedHours;

    @Column(name = "over_time")
    private String overTime;

    @Column(name = "emp_id")
    private long empId;

    @Column(name = "extra_time")
    private double extraTime;

    @Column(name = "payable_amount")
    private double payableAmount;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "validation")
    private String validation;

    private double payment;

    @Column(name = "img_id")
    private long imgId;

    @Column(name = "entry_body_temperature")
    private long entryBodyTemperature;

    @Column(name = "exit_body_temperature")
    private long exitBodyTemperature;

    @Column(name = "entry_gate_number")
    private String entryGateNumber;

    @Column(name = "exit_gate_number")
    private String exitGateNumber;

    @Column(name = "mask_wearing")
    private boolean maskWearing;
}
