package com.orileo.attendance.wrapper.service;

import com.orileo.attendance.wrapper.entity.mssql.RealBioAttendance;
import com.orileo.attendance.wrapper.repository.mssqlrepository.AttendanceMsSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RealBioServiceImpl implements RealBioService {

    @Autowired
    private AttendanceMsSqlRepository attendanceMsSqlRepository;

    @Override
    public List<RealBioAttendance> getPunchedAttendance() {
        return attendanceMsSqlRepository.findAll();
    }

    @Override
    public List<RealBioAttendance> getPunchedAttendance(Date loggedDate) {
        return attendanceMsSqlRepository.findByLogDate(loggedDate);
    }

    @Override
    public List<RealBioAttendance> getPunchedAttendanceByUserId(String userId, Date loggedDate) {
        return attendanceMsSqlRepository.findByUserIdAndLogDate(userId,loggedDate);
    }
}