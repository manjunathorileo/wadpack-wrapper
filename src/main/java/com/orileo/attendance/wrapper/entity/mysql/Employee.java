package com.orileo.attendance.wrapper.entity.mysql;
/*
 * @author Ashvini B
 */

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name="employee")
public class Employee implements Serializable{

	private static final long serialVersionUID = -3781887609609178736L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name="reference_Id")
	private String referenceId;

	@Column(name="employee_code")
	private String employeeCode;

	@Column(name="first_name")
	private String firstName;

	@Column(name="middle_name")
	private String middleName;

	@Column(name="last_name")
	private String lastName;

	@Column
	private Boolean status;

	@Column(name = "employee_type")
	private String employeeType;

	@Column(name = "rfid")
	private String rfid;

	@Column(name = "department_name")
	private String departmentName;

	@Column(name = "epf")
	private boolean epf;

	@Column(name = "safety_vest" )
	private boolean safetyVest;

	@Column(name = "ot_required")
	private boolean otRequired;

	@Column(name = "flexi")
	private boolean flexi;

	@Column(name = "family_pic_id")
	private long familyPicId;

	@Column(name = "profile_pic_id")
	private long profilePicId;

	@Column(name = "upload_for")
	private long uploadFor;

	@Column(name = "site_id")
	private long siteId;

	@Column(name = "first_week_off")
	private long firstWeekOff;

	@Column(name = "second_week_off")
	private long secondWeekOff;

	@Column(name = "mins")
	private double mins;

	@Column(name = "offered_salary")
	private double offeredSalary;

	public Employee(){}
}