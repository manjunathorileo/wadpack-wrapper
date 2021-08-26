package com.orileo.attendance.wrapper.resource;


import com.orileo.attendance.wrapper.entity.mssql.RealBioAttendance;
import com.orileo.attendance.wrapper.entity.mysql.*;
import com.orileo.attendance.wrapper.repository.mysqlrepository.PermanentContractAttendanceRepo;
import com.orileo.attendance.wrapper.repository.mysqlrepository.PermanentContractRepo;
import com.orileo.attendance.wrapper.service.EmployeeAttendanceService;
import com.orileo.attendance.wrapper.service.EmployeeService;
import com.orileo.attendance.wrapper.service.EmployeeShiftAssignmentService;
import com.orileo.attendance.wrapper.service.RealBioService;
import com.orileo.attendance.wrapper.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.orileo.attendance.wrapper.util.DateUtil.convertToDate;
import static com.orileo.attendance.wrapper.util.DateUtil.getDoubleDigits;

@RestController
@Configuration
@EnableScheduling
public class AttendanceController {

    private RealBioService realBioService;
    private EmployeeService employeeService;
    private EmployeeAttendanceService employeeAttendanceService;
    private EmployeeShiftAssignmentService employeeShiftAssignmentService;

    @Autowired
    PermanentContractAttendanceRepo permanentContractAttendanceRepo;
    @Autowired
    PermanentContractRepo permanentContractRepo;

    @Autowired
    public AttendanceController(RealBioService realBioService,
                                EmployeeService employeeService, EmployeeAttendanceService employeeAttendanceService,
                                EmployeeShiftAssignmentService employeeShiftAssignmentService) {
        this.realBioService = realBioService;
        this.employeeService = employeeService;
        this.employeeAttendanceService = employeeAttendanceService;
        this.employeeShiftAssignmentService = employeeShiftAssignmentService;
    }

    @GetMapping("/real-bio")
    public ResponseEntity<List<RealBioAttendance>> realBioData() {
        List<RealBioAttendance> realBioData = realBioService.getPunchedAttendance(DateUtil.getTodayDate());
        return new ResponseEntity<>(realBioData, HttpStatus.OK);
    }

    @GetMapping("/real-bio-conversion")
    @Scheduled(fixedRate = 120000)
    public ResponseEntity<List<EmployeeAttendance>> realBioConversionData() throws ParseException {
        List<EmployeeAttendance> employeesAttendance = new ArrayList<>();
        List<RealBioAttendance> realBioData = realBioService.getPunchedAttendance(DateUtil.getTodayDate());
        System.out.println("Bande" + realBioData.size());
        Optional<EmployeeShiftAssignment> employeeShiftAssignment = null;
        if (realBioData != null && realBioData.size() > 0) {
            List<RealBioAttendance> realBioAttendancesById = null;
            for (RealBioAttendance realBioAttendance : realBioData) {
                System.out.println("id: " + realBioAttendance.getUserId());
                if (realBioAttendance != null && realBioAttendance.getUserId() != null) {
                    EmployeeAttendance employeeAttendance = new EmployeeAttendance();
                    realBioAttendancesById = realBioService.getPunchedAttendanceByUserId(realBioAttendance.getUserId(), DateUtil.getTodayDate());
                    Employee employeeObj = employeeService.getByRefId(realBioAttendance.getUserId());
                    if (employeeObj == null) {
                        String code0 = 0 + realBioAttendance.getUserId();
                        employeeObj = employeeService.getByRefId(code0);
                    }
                    if (employeeObj != null) {
                        employeeShiftAssignment = employeeShiftAssignmentService.getCurrentShiftByEmployeeId(employeeObj.getId(), DateUtil.getTodayDate());
                        Shift shift = null;
//                        if (employeeShiftAssignment.isPresent()) {
//                            shift = employeeShiftAssignment.get().getShift();
//                            employeeAttendance.setShift(shift);
//                        } else {
//                            employeeAttendance.setShift(null);
//                        }
                        employeeAttendance.setEmployee(employeeObj);
                        employeeAttendance.setMarkedOn(DateUtil.getTodayDate());
                        employeeAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                        employeeAttendance.setRecordedTime(realBioAttendancesById.get(0).getLogDate());
                        employeeAttendance.setInTime(realBioAttendancesById.get(0).getLogDate());
                        employeeAttendance.setId(0);
                        if (shift != null) {
                            employeeAttendance = attendanceLateEntry(employeeAttendance, shift);
                        }
                        employeesAttendance.add(employeeAttendance);
                    } else {
                        System.out.println("Employee left company : " + realBioAttendance.getUserId());
                    }
                }
            }
            if (employeesAttendance != null && employeesAttendance.size() > 0) {
                for (EmployeeAttendance employeeAttendance : employeesAttendance) {
                    if (employeeAttendance != null) {
                        EmployeeAttendance alreadyMarked = employeeAttendanceService.getTodayMarkedEmployeeAttendance(DateUtil.getTodayDate(), employeeAttendance.getEmployee()/*,AttendanceStatus.PRESENT*/);
                        if (alreadyMarked != null) {
                            realBioAttendancesById = realBioService.getPunchedAttendanceByUserId(String.valueOf(employeeAttendance.getEmployee().getEmployeeCode()), DateUtil.getTodayDate());
                            if (realBioAttendancesById != null && realBioAttendancesById.size() > 1) {
                                alreadyMarked.setInTime(realBioAttendancesById.get(0).getLogDate());
                                Collections.reverse(realBioAttendancesById);
                                System.out.println("out time :" + realBioAttendancesById.get(0).getLogDate());
                                alreadyMarked.setOutTime(realBioAttendancesById.get(0).getLogDate());
                                alreadyMarked.setAttendanceStatus(AttendanceStatus.PRESENT);
                                employeeAttendanceService.createEmployeeAttendance(alreadyMarked);
                            } else
                                System.out.println("*******Only Entry Recorded*********");
                            employeeAttendanceService.createEmployeeAttendance(alreadyMarked);
                            System.out.println("******** Attendance Entry and Exit already marked for Id: *************** : " + employeeAttendance.getEmployee().getId());
                        } else {
                            EmployeeAttendance markedAttendance = employeeAttendanceService.createEmployeeAttendance(employeeAttendance);
                        }
                    }
                }
            }
        } else {
//            throw new EntityNotFoundException("Attendance data not found in RealBio Machine: " + DateUtil.getTodayDate());
            System.out.println("Attendance data not found in RealBio Machine: " + DateUtil.getTodayDate());
        }
        return new ResponseEntity<>(employeesAttendance, HttpStatus.OK);
    }


    public String convert(long miliSeconds) {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
        //int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
        return String.format("%02d:%02d", hrs, min/*:%2d, sec*/);
    }

    private EmployeeAttendance updateWorkedHrs(EmployeeAttendance attendance, Shift shift) throws ParseException {
        attendance.setAttendanceStatus(AttendanceStatus.PRESENT);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        if (attendance.getInTime() == null) {
            attendance.setInTime(shift.getStartTime());

        }
        if (attendance.getOutTime() == null) {
            attendance.setOutTime(shift.getEndTime());
        }
        String inTime = timeFormat.format(attendance.getInTime());
        String outTime = timeFormat.format(attendance.getOutTime());

        long attendanceInTime = 0;
        try {
            attendanceInTime = timeFormat.parse(inTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long attendanceOutTime = 0;
        try {
            attendanceOutTime = timeFormat.parse(outTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Time attTime = new Time(attendanceTime);
        String shiftStartTime = timeFormat.format(shift.getStartTime());
        long shiftStart = timeFormat.parse(shiftStartTime).getTime();
        double workedMinutes = 0;
        long workedMillis;
        if (attendanceInTime > shiftStart) {
            long lateEntry = attendanceInTime - shift.getStartTime().getTime();
            String lateEntryTime = convert(lateEntry);
            attendance.setLateEntry(lateEntryTime);
            workedMillis = attendanceOutTime - attendanceInTime;
        } else {
            attendance.setLateEntry("00:00");
            workedMillis = attendanceOutTime - shiftStart;
        }
        workedMinutes = TimeUnit.MILLISECONDS.toMinutes(workedMillis);
        double workedHrs = workedMinutes / 60;
        attendance.setWorkedHours(String.valueOf(workedHrs));
        if (workedMinutes > 660) {
            double extraHour = workedMinutes - 660;
            double overTimeHrs = (extraHour + 150) / 60;
            attendance.setOverTime(String.format("%.2f", overTimeHrs));
            double effectiveExtraHour = overTimeHrs * 0.7;
            attendance.setEffectiveOverTime(String.format("%.2f", effectiveExtraHour));
        } else {
            attendance.setOverTime(String.valueOf(0.00));
            attendance.setEffectiveOverTime(String.valueOf(0.00));
        }
        return attendance;
    }

    private EmployeeAttendance attendanceLateEntry(EmployeeAttendance attendance, Shift shift) throws ParseException {
        attendance.setAttendanceStatus(AttendanceStatus.PRESENT);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String inTime = timeFormat.format(attendance.getInTime());
        long attendanceInTime = 0;
        try {
            attendanceInTime = timeFormat.parse(inTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String shiftStartTime = timeFormat.format(shift.getStartTime());
        long shiftStart = timeFormat.parse(shiftStartTime).getTime();
        if (attendanceInTime > shiftStart) {
            long lateEntry = attendanceInTime - shift.getStartTime().getTime();
            String lateEntryTime = convert(lateEntry);
            attendance.setLateEntry(lateEntryTime);
        } else {
            attendance.setLateEntry("00:00");
        }
        return attendance;
    }

//    @GetMapping("/real-bio-conversion/auto-sync")
//    public ResponseEntity<List<EmployeeAttendance>> realBioConversionDataTillToday() throws ParseException {
//        List<EmployeeAttendance> employeesAttendance = new ArrayList<>();
//        Date stDate = getStartDate();
//        List<Date> dates = getDaysBetweenDates(stDate, new Date());
//        for (Date date : dates) {
//            System.out.println("reading data from realbio machine");
//            List<RealBioAttendance> realBioData = realBioService.getPunchedAttendance(date);
//            System.out.println("size and date: " + realBioData.size() + "date: " + date);
//
//            Optional<EmployeeShiftAssignment> employeeShiftAssignment = null;
//            if (realBioData != null && realBioData.size() > 0) {
//                List<RealBioAttendance> realBioAttendancesById = null;
//                for (RealBioAttendance realBioAttendance : realBioData) {
//                    if (realBioAttendance != null && realBioAttendance.getUserId() != null) {
//                        EmployeeAttendance employeeAttendance = new EmployeeAttendance();
//                        realBioAttendancesById = realBioService.getPunchedAttendanceByUserId(realBioAttendance.getUserId(), date);
//                        Employee employeeObj = employeeService.getByRefId(realBioAttendance.getUserId());
//                        if (employeeObj == null) {
//                            String code0 = 0 + realBioAttendance.getUserId();
//                            employeeObj = employeeService.getByRefId(code0);
//                        }
//                        if (employeeObj != null) {
//                            employeeShiftAssignment = employeeShiftAssignmentService.getCurrentShiftByEmployeeId(employeeObj.getId(), date);
//                            Shift shift = null;
//                            if (employeeShiftAssignment.isPresent()) {
//                                shift = employeeShiftAssignment.get().getShift();
//                                employeeAttendance.setShift(shift);
//                            } else {
//                                employeeAttendance.setShift(null);
//                            }
//                            employeeAttendance.setEmployee(employeeObj);
//                            employeeAttendance.setMarkedOn(date);
//                            employeeAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
//                            employeeAttendance.setRecordedTime(realBioAttendancesById.get(0).getLogDate());
//                            employeeAttendance.setInTime(realBioAttendancesById.get(0).getLogDate());
//                            employeeAttendance.setId(0);
//                            if (shift != null) {
//                                employeeAttendance = attendanceLateEntry(employeeAttendance, shift);
//                            }
//                            employeesAttendance.add(employeeAttendance);
//                        } else {
//                            System.out.println("Employee left company : " + realBioAttendance.getUserId());
//                        }
//                    }
//                }
//                if (employeesAttendance != null && employeesAttendance.size() > 0) {
//                    for (EmployeeAttendance employeeAttendance : employeesAttendance) {
//                        if (employeeAttendance != null) {
//                            EmployeeAttendance alreadyMarked = employeeAttendanceService.getTodayMarkedEmployeeAttendance(date, employeeAttendance.getEmployee()/*,AttendanceStatus.PRESENT*/);
//                            if (alreadyMarked != null) {
//                                realBioAttendancesById = realBioService.getPunchedAttendanceByUserId(String.valueOf(employeeAttendance.getEmployee().getEmployeeCode()), date);
//                                if (realBioAttendancesById != null && realBioAttendancesById.size() > 1) {
//                                    alreadyMarked.setInTime(realBioAttendancesById.get(0).getLogDate());
//                                    Collections.reverse(realBioAttendancesById);
//                                    System.out.println("out time :" + realBioAttendancesById.get(0).getLogDate());
//                                    alreadyMarked.setOutTime(realBioAttendancesById.get(0).getLogDate());
//                                    if (employeeAttendance.getShift() != null && employeeShiftAssignment.isPresent()) {
//                                        alreadyMarked = updateWorkedHrs(alreadyMarked, employeeShiftAssignment.get().getShift());
//                                    }
//                                    employeeAttendanceService.createEmployeeAttendance(alreadyMarked);
//                                } else
//                                    System.out.println("*******Only Entry Recorded*********");
//                                employeeAttendanceService.createEmployeeAttendance(alreadyMarked);
//                                System.out.println("******** Attendance Entry and Exit already marked for Id: *************** : " + employeeAttendance.getEmployee().getId());
//                            } else {
//                                EmployeeAttendance markedAttendance = employeeAttendanceService.createEmployeeAttendance(employeeAttendance);
//                            }
//                        }
//                    }
//                }
//            }
//           /* else
//            {
//                throw new EntityNotFoundException("Attendance data not found in RealBio Machine: "+date);
//            }*/
//        }
//
//        return new ResponseEntity<>(employeesAttendance, HttpStatus.OK);
//    }


    @GetMapping("whats-date")
    public Date getStartDateNumberOfMonths(long number) {
        Date date = new Date();
        LocalDate now = LocalDate.now();
        LocalDate earlier = now.minusMonths(number);
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date syncFrom = Date.from(earlier.atStartOfDay(defaultZoneId).toInstant());
        System.out.println(syncFrom);
        return syncFrom;
    }

    private Date yesterday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    @GetMapping("/real-bio-conversion/auto-sync")
    @Scheduled(fixedRate = 1200000)
    public void realBioAutoSync() throws ParseException {
        realBioConversionDataTillToday(7);
    }

    public ResponseEntity<List<EmployeeAttendance>> realBioConversionDataTillToday(long number) throws ParseException {

        Date stDate = getStartDateNumberOfMonths(number);
        List<Date> dates = getDaysBetweenDates(stDate, new Date());
        for (Date date : dates) {
            List<EmployeeAttendance> employeesAttendance = new ArrayList<>();
            System.out.println("reading data from realbio machine");
            List<RealBioAttendance> realBioData = realBioService.getPunchedAttendance(date);
            System.out.println("size and date: " + realBioData.size() + "date: " + date);

            Optional<EmployeeShiftAssignment> employeeShiftAssignment = null;
            if (realBioData != null && realBioData.size() > 0) {
                List<RealBioAttendance> realBioAttendancesById = null;
                for (RealBioAttendance realBioAttendance : realBioData) {
                    if (realBioAttendance != null && realBioAttendance.getUserId() != null) {
                        EmployeeAttendance employeeAttendance = new EmployeeAttendance();
                        realBioAttendancesById = realBioService.getPunchedAttendanceByUserId(realBioAttendance.getUserId(), date);
                        Employee employeeObj = employeeService.getByRefId(realBioAttendance.getUserId());
                        if (employeeObj != null) {
                            employeeShiftAssignment = employeeShiftAssignmentService.getCurrentShiftByEmployeeId(employeeObj.getId(), date);
                            Shift shift = null;
                            employeeAttendance.setEmployee(employeeObj);
                            employeeAttendance.setMarkedOn(realBioAttendancesById.get(0).getLogDate());
                            employeeAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                            employeeAttendance.setRecordedTime(realBioAttendancesById.get(0).getLogDate());
                            employeeAttendance.setInTime(realBioAttendancesById.get(0).getLogDate());
                            employeeAttendance.setId(0);
                            if (shift != null) {
                                employeeAttendance = attendanceLateEntry(employeeAttendance, shift);
                            }
                            employeesAttendance.add(employeeAttendance);
                        } else {
                            System.out.println("Employee left company : " + realBioAttendance.getUserId());
                        }
                    }
                }
                if (employeesAttendance != null && employeesAttendance.size() > 0) {
                    for (EmployeeAttendance employeeAttendance : employeesAttendance) {
                        if (employeeAttendance != null) {
                            EmployeeAttendance alreadyMarked = employeeAttendanceService.getTodayMarkedEmployeeAttendance(employeeAttendance.getMarkedOn(), employeeAttendance.getEmployee());
                            if (alreadyMarked != null) {
                                realBioAttendancesById = realBioService.getPunchedAttendanceByUserId(String.valueOf(employeeAttendance.getEmployee().getEmployeeCode()), date);
                                if (realBioAttendancesById != null && realBioAttendancesById.size() > 1) {
                                    System.out.println("------------Both In and out marked-----------");

                                    if (realBioAttendancesById.get(0).getFlag().equalsIgnoreCase("O")) {
                                        alreadyMarked.setOutTime(realBioAttendancesById.get(0).getLogDate());
                                    } else if (realBioAttendancesById.get(0).getFlag().equalsIgnoreCase("I")) {
                                        alreadyMarked.setInTime(realBioAttendancesById.get(0).getLogDate());
                                    }

                                    Collections.reverse(realBioAttendancesById);

                                    if (realBioAttendancesById.get(0).getFlag().equalsIgnoreCase("I")) {
                                        alreadyMarked.setInTime(realBioAttendancesById.get(0).getLogDate());
                                    } else if (realBioAttendancesById.get(0).getFlag().equalsIgnoreCase("O")) {
                                        alreadyMarked.setOutTime(realBioAttendancesById.get(0).getLogDate());
                                    }

                                    //------------------MAKE PRESENT ALREADY MARKED-------------//
                                    alreadyMarked.setAttendanceStatus(AttendanceStatus.PRESENT);
                                    if(alreadyMarked.getOutTime()!=null && alreadyMarked.getInTime()!=null) {
                                        if (alreadyMarked.getOutTime().before(alreadyMarked.getInTime())) {
                                            Date yesterdayDate = yesterday(alreadyMarked.getOutTime());
                                            EmployeeAttendance employeeAttendanceYesterdays = employeeAttendanceService.getTodayMarkedEmployeeAttendance(yesterdayDate, alreadyMarked.getEmployee());
                                            if (employeeAttendanceYesterdays != null) {
                                                employeeAttendanceYesterdays.setOutTime(alreadyMarked.getOutTime());
                                                employeeAttendanceYesterdays.setShift(null);
                                                employeeAttendanceService.createEmployeeAttendance(employeeAttendanceYesterdays);
                                            }
                                            alreadyMarked.setOutTime(null);
                                        }
                                    }
                                    employeeAttendanceService.createEmployeeAttendance(alreadyMarked);
                                    //------------------MAKE PRESENT ALREADY MARKED-------------//
                                } else {
                                    System.out.println("*******Only Entry or In time Recorded*********");
                                    if (!realBioAttendancesById.isEmpty()) {

                                        if (realBioAttendancesById.get(0).getFlag().equalsIgnoreCase("I")) {
                                            alreadyMarked.setInTime(realBioAttendancesById.get(0).getLogDate());
                                        } else if (realBioAttendancesById.get(0).getFlag().equalsIgnoreCase("O")) {
                                            alreadyMarked.setOutTime(realBioAttendancesById.get(0).getLogDate());
                                        }
                                        alreadyMarked.setAttendanceStatus(AttendanceStatus.PRESENT);
                                    }
                                    employeeAttendanceService.createEmployeeAttendance(alreadyMarked);
                                    System.out.println("******** Attendance Entry and Exit already marked for Id: *************** : " + employeeAttendance.getEmployee().getId());
                                }
                            } else {
                                alreadyMarked = employeeAttendance;
                                realBioAttendancesById = realBioService.getPunchedAttendanceByUserId(String.valueOf(employeeAttendance.getEmployee().getEmployeeCode()), date);
                                if (realBioAttendancesById != null && realBioAttendancesById.size() > 1) {
                                    System.out.println("------------Both In and out marked-----------");

                                    if (realBioAttendancesById.get(0).getFlag().equalsIgnoreCase("O")) {
                                        alreadyMarked.setOutTime(realBioAttendancesById.get(0).getLogDate());
                                    } else if (realBioAttendancesById.get(0).getFlag().equalsIgnoreCase("I")) {
                                        alreadyMarked.setInTime(realBioAttendancesById.get(0).getLogDate());
                                    }

                                    Collections.reverse(realBioAttendancesById);

                                    if (realBioAttendancesById.get(0).getFlag().equalsIgnoreCase("I")) {
                                        alreadyMarked.setInTime(realBioAttendancesById.get(0).getLogDate());
                                    } else if (realBioAttendancesById.get(0).getFlag().equalsIgnoreCase("O")) {
                                        alreadyMarked.setOutTime(realBioAttendancesById.get(0).getLogDate());
                                    }

                                    //------------------MAKE PRESENT ALREADY MARKED-------------//
                                    alreadyMarked.setAttendanceStatus(AttendanceStatus.PRESENT);
                                    if(alreadyMarked.getOutTime()!=null && alreadyMarked.getInTime()!=null) {
                                        if (alreadyMarked.getOutTime().before(alreadyMarked.getInTime())) {
                                            Date yesterdayDate = yesterday(alreadyMarked.getOutTime());
                                            EmployeeAttendance employeeAttendanceYesterdays = employeeAttendanceService.getTodayMarkedEmployeeAttendance(yesterdayDate, alreadyMarked.getEmployee());
                                            if (employeeAttendanceYesterdays != null) {
                                                employeeAttendanceYesterdays.setOutTime(alreadyMarked.getOutTime());
                                                employeeAttendanceYesterdays.setShift(null);
                                                employeeAttendanceService.createEmployeeAttendance(employeeAttendanceYesterdays);
                                            }
                                            alreadyMarked.setOutTime(null);
                                        }
                                    }
                                    employeeAttendanceService.createEmployeeAttendance(alreadyMarked);
                                    //------------------MAKE PRESENT ALREADY MARKED-------------//
                                } else {
                                    System.out.println("*******Only Entry or In time Recorded*********");
                                    if (!realBioAttendancesById.isEmpty()) {

                                        if (realBioAttendancesById.get(0).getFlag().equalsIgnoreCase("I")) {
                                            alreadyMarked.setInTime(realBioAttendancesById.get(0).getLogDate());
                                        } else if (realBioAttendancesById.get(0).getFlag().equalsIgnoreCase("O")) {
                                            alreadyMarked.setOutTime(realBioAttendancesById.get(0).getLogDate());
                                        }
                                        alreadyMarked.setAttendanceStatus(AttendanceStatus.PRESENT);
                                    }
                                    employeeAttendanceService.createEmployeeAttendance(alreadyMarked);
                                    System.out.println("******** Attendance Entry and Exit already marked for Id: *************** : " + employeeAttendance.getEmployee().getId());
                                }
                                EmployeeAttendance markedAttendance = employeeAttendanceService.createEmployeeAttendance(alreadyMarked);
                            }
                        }
                    }
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public static List<Date> getDaysBetweenDates(Date startdate, Date enddate) {
        List<Date> dates = new ArrayList<Date>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        Date currentDate = null;
        String dateStr = "";
        String day = "";
        String month = "";
        int year = 0;

        while (calendar.getTime().before(enddate)) {
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            day = getDoubleDigits(dd);

            int mm = calendar.get(Calendar.MONTH) + 1;
            month = getDoubleDigits(mm);

            year = calendar.get(Calendar.YEAR);
            dateStr = day + "/" + month + "/" + year;
            currentDate = convertToDate(dateStr);
            dates.add(currentDate);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }


    @GetMapping("wadpack-live")
    @Scheduled(initialDelay = 1000, fixedRate = 60000)
    public List<RealBioAttendance> getAllTodays() {
        List<EmpPermanentContract> empPermanentContractList = permanentContractRepo.findAll();

        for (EmpPermanentContract empPermanentContract : empPermanentContractList) {
            long lCode = Long.parseLong(empPermanentContract.getEmployeeCode());
            String code = String.valueOf(lCode);
            List<RealBioAttendance> realBioAttendances = realBioService.getPunchedAttendanceByUserId(code, DateUtil.getTodayDate());

            if (!realBioAttendances.isEmpty()) {
                RealBioAttendance firstEntry = realBioAttendances.get(0);
                Collections.reverse(realBioAttendances);
                RealBioAttendance lastEntry = realBioAttendances.get(0);

                PermanentContractAttendance permanentContractAttendance = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeId(DateUtil.getTodayDate(), empPermanentContract.getId());
                if (permanentContractAttendance == null) {
                    PermanentContractAttendance permanentContractAttendanceNew = new PermanentContractAttendance();
                    permanentContractAttendanceNew.setEmployeeCode(empPermanentContract.getEmployeeCode());
                    permanentContractAttendanceNew.setEmployeeName(empPermanentContract.getFirstName());
                    permanentContractAttendanceNew.setMarkedOn(DateUtil.getTodayDate());
                    permanentContractAttendanceNew.setAttendanceStatus(AttendanceStatus.PRESENT);
                    permanentContractAttendanceNew.setRecordedTime(firstEntry.getLogDate());
                    permanentContractAttendanceNew.setInTime(firstEntry.getLogDate());
                    permanentContractAttendanceNew.setEntryBodyTemperature(0);
                    permanentContractAttendanceNew.setExitBodyTemperature(0);
                    permanentContractAttendanceNew.setEmpId(empPermanentContract.getId());
                    permanentContractAttendanceRepo.save(permanentContractAttendanceNew);
                } else {
                    Date startDate = firstEntry.getLogDate();
                    Date endDate = lastEntry.getLogDate();

                    System.out.println("Dates: " + startDate.getTime() + " -" + endDate.getTime());
                    long duration = endDate.getTime() - startDate.getTime();
                    long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
                    System.out.println("hrs: " + diffInHours);
                    if (diffInHours > 1) {
                        permanentContractAttendance.setEmployeeCode(empPermanentContract.getEmployeeCode());
                        permanentContractAttendance.setEmployeeName(empPermanentContract.getFirstName());
                        permanentContractAttendance.setMarkedOn(DateUtil.getTodayDate());
                        permanentContractAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                        permanentContractAttendance.setRecordedTime(firstEntry.getLogDate());
                        permanentContractAttendance.setOutTime(firstEntry.getLogDate());
                        permanentContractAttendance.setEntryBodyTemperature(0);
                        permanentContractAttendance.setExitBodyTemperature(0);
                        permanentContractAttendance.setEmpId(empPermanentContract.getId());
                        permanentContractAttendanceRepo.save(permanentContractAttendance);
                    }
                }

            } else {
//                System.out.println("Dint punch today");
            }

        }
        return null;
    }


    @PostMapping("contract-sync")
    public List<RealBioAttendance> getAllByDateSyncButton(@RequestBody RealBioDto realBioDto) {
        List<EmpPermanentContract> empPermanentContractList = permanentContractRepo.findAll();

        for (EmpPermanentContract empPermanentContract : empPermanentContractList) {
            long lCode = Long.parseLong(empPermanentContract.getEmployeeCode());
            String code = String.valueOf(lCode);
            List<RealBioAttendance> realBioAttendances = realBioService.getPunchedAttendanceByUserId(code, realBioDto.startDate);

            if (!realBioAttendances.isEmpty()) {
                RealBioAttendance firstEntry = realBioAttendances.get(0);
                Collections.reverse(realBioAttendances);
                RealBioAttendance lastEntry = realBioAttendances.get(0);

                PermanentContractAttendance permanentContractAttendance = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeId(realBioDto.startDate, empPermanentContract.getId());
                if (permanentContractAttendance == null) {
                    PermanentContractAttendance permanentContractAttendanceNew = new PermanentContractAttendance();
                    permanentContractAttendanceNew.setEmployeeCode(empPermanentContract.getEmployeeCode());
                    permanentContractAttendanceNew.setEmployeeName(empPermanentContract.getFirstName());
                    permanentContractAttendanceNew.setMarkedOn(realBioDto.startDate);
                    permanentContractAttendanceNew.setAttendanceStatus(AttendanceStatus.PRESENT);
                    permanentContractAttendanceNew.setRecordedTime(firstEntry.getLogDate());
                    permanentContractAttendanceNew.setInTime(firstEntry.getLogDate());
                    permanentContractAttendanceNew.setEntryBodyTemperature(0);
                    permanentContractAttendanceNew.setExitBodyTemperature(0);
                    permanentContractAttendanceNew.setEmpId(empPermanentContract.getId());
                    permanentContractAttendanceRepo.save(permanentContractAttendanceNew);
                } else {
                    Date startDate = firstEntry.getLogDate();
                    Date endDate = lastEntry.getLogDate();

                    System.out.println("Dates: " + startDate.getTime() + " -" + endDate.getTime());
                    long duration = endDate.getTime() - startDate.getTime();
                    long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
                    System.out.println("hrs: " + diffInHours);
                    if (diffInHours > 1) {
                        permanentContractAttendance.setEmployeeCode(empPermanentContract.getEmployeeCode());
                        permanentContractAttendance.setEmployeeName(empPermanentContract.getFirstName());
                        permanentContractAttendance.setMarkedOn(realBioDto.startDate);
                        permanentContractAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                        permanentContractAttendance.setRecordedTime(firstEntry.getLogDate());
                        permanentContractAttendance.setOutTime(firstEntry.getLogDate());
                        permanentContractAttendance.setEntryBodyTemperature(0);
                        permanentContractAttendance.setExitBodyTemperature(0);
                        permanentContractAttendance.setEmpId(empPermanentContract.getId());
                        permanentContractAttendanceRepo.save(permanentContractAttendance);
                    }
                }

            } else {
//                System.out.println("Dint punch today");
            }

        }
        return null;
    }

}