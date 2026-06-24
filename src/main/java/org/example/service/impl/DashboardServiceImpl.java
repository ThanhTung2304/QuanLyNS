package org.example.service.impl;

import org.example.dto.DashboardStats;
import org.example.repository.BangLuongRepository;
import org.example.repository.ChamCongRepository;
import org.example.repository.NhanVienRepository;
import org.example.repository.NghiPhepRepository;
import org.example.repository.impl.BangLuongRepositoryImpl;
import org.example.repository.impl.ChamCongRepositoryImpl;
import org.example.repository.impl.NhanVienRepositoryImpl;
import org.example.repository.impl.NghiPhepRepositoryImpl;
import org.example.service.DashboardService;

import java.time.LocalDate;

public class DashboardServiceImpl implements DashboardService {

    private final NhanVienRepository nhanVienRepository;
    private final ChamCongRepository chamCongRepository;
    private final NghiPhepRepository nghiPhepRepository;
    private final BangLuongRepository bangLuongRepository;

    public DashboardServiceImpl() {
        this.nhanVienRepository = new NhanVienRepositoryImpl();
        this.chamCongRepository = new ChamCongRepositoryImpl();
        this.nghiPhepRepository = new NghiPhepRepositoryImpl();
        this.bangLuongRepository = new BangLuongRepositoryImpl();
    }

    @Override
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        LocalDate today = LocalDate.now();

        // Nhân sự
        stats.setTotalActiveEmployees(nhanVienRepository.countActiveEmployees());
        stats.setNewEmployeesThisMonth(nhanVienRepository.countNewEmployeesInCurrentMonth());
        stats.setResignedEmployeesThisMonth(nhanVienRepository.countResignedEmployeesInCurrentMonth());

        // Chấm công
        stats.setCheckInsToday(chamCongRepository.countCheckInsToday());
        stats.setLateCheckInsToday(chamCongRepository.countLateCheckInsToday());

        // Công việc
        stats.setPendingLeaveRequests(nghiPhepRepository.countPendingRequests());

        // Lương (lấy của tháng hiện tại)
        stats.setTotalSalaryThisMonth(bangLuongRepository.sumTotalSalaryForMonth(today.getMonthValue(), today.getYear()));
        stats.setTotalBonusThisMonth(bangLuongRepository.sumTotalBonusForMonth(today.getMonthValue(), today.getYear()));
        stats.setTotalDeductionThisMonth(bangLuongRepository.sumTotalDeductionForMonth(today.getMonthValue(), today.getYear()));

        return stats;
    }
}