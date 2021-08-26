package com.orileo.attendance.wrapper.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "employee_attendance")
public class EmployeeAttendance implements Serializable {

	private static final long serialVersionUID = 6331902193138497864L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Temporal(TemporalType.DATE)
	@Column(name="marked_on")
	private Date markedOn;

	@Column(name="recorded_time")
	private Date recordedTime;

	@Column(name="out_time")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm:ss", timezone="IST")
	private Date outTime;

	@Column(name="in_time")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm:ss", timezone="IST")
	private Date inTime;

	@Column(name="worked_hours")
	private String workedHours;

	@Enumerated(EnumType.STRING)
	@Column(name="attendance_status", length = 45)
	private AttendanceStatus attendanceStatus;

	@OneToOne
	private Shift shift;

	@Column(name="late_entry")
	private String lateEntry;

	@Column(name="over_time")
	private String overTime;

	@Column(name="effective_over_time")
	private String effectiveOverTime;

	@Column(name = "is_week_off_present")
	private boolean isWeekOffPresent;

	@OneToOne
	private Employee employee;

	@Column(name = "data_processed")
	private boolean dataProcessed;
}