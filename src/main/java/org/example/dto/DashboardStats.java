package org.example.dto;

import java.math.BigDecimal;

/**
 * Một lớp DTO (Data Transfer Object) để chứa tất cả các số liệu thống kê cho Dashboard.
 */
public class DashboardStats {
    private long totalActiveEmployees;
    private long newEmployeesThisMonth;
    private long resignedEmployeesThisMonth;
    private long checkInsToday;
    private long lateCheckInsToday;
    private long pendingLeaveRequests;
    private BigDecimal totalSalaryThisMonth = BigDecimal.ZERO;
    private BigDecimal totalBonusThisMonth = BigDecimal.ZERO;
    private BigDecimal totalDeductionThisMonth = BigDecimal.ZERO;

    // Getters and Setters
    public long getTotalActiveEmployees() { return totalActiveEmployees; }
    public void setTotalActiveEmployees(long totalActiveEmployees) { this.totalActiveEmployees = totalActiveEmployees; }
    public long getNewEmployeesThisMonth() { return newEmployeesThisMonth; }
    public void setNewEmployeesThisMonth(long newEmployeesThisMonth) { this.newEmployeesThisMonth = newEmployeesThisMonth; }
    public long getResignedEmployeesThisMonth() { return resignedEmployeesThisMonth; }
    public void setResignedEmployeesThisMonth(long resignedEmployeesThisMonth) { this.resignedEmployeesThisMonth = resignedEmployeesThisMonth; }
    public long getCheckInsToday() { return checkInsToday; }
    public void setCheckInsToday(long checkInsToday) { this.checkInsToday = checkInsToday; }
    public long getLateCheckInsToday() { return lateCheckInsToday; }
    public void setLateCheckInsToday(long lateCheckInsToday) { this.lateCheckInsToday = lateCheckInsToday; }
    public long getPendingLeaveRequests() { return pendingLeaveRequests; }
    public void setPendingLeaveRequests(long pendingLeaveRequests) { this.pendingLeaveRequests = pendingLeaveRequests; }
    public BigDecimal getTotalSalaryThisMonth() { return totalSalaryThisMonth; }
    public void setTotalSalaryThisMonth(BigDecimal totalSalaryThisMonth) { this.totalSalaryThisMonth = totalSalaryThisMonth; }
    public BigDecimal getTotalBonusThisMonth() { return totalBonusThisMonth; }
    public void setTotalBonusThisMonth(BigDecimal totalBonusThisMonth) { this.totalBonusThisMonth = totalBonusThisMonth; }
    public BigDecimal getTotalDeductionThisMonth() { return totalDeductionThisMonth; }
    public void setTotalDeductionThisMonth(BigDecimal totalDeductionThisMonth) { this.totalDeductionThisMonth = totalDeductionThisMonth; }
}