package org.example.service;

import org.example.entity.BangLuong;
import org.example.exception.BusinessException;

import java.util.List;
import java.util.Optional;

public interface LuongService {
    List<BangLuong> calculatePayroll(int thang, int nam) throws BusinessException;
    void finalizePayroll(List<BangLuong> bangLuongList) throws BusinessException;
    List<BangLuong> findPayrollHistory(int thang, int nam);
    Optional<BangLuong> findMyPayroll(String maNv, int thang, int nam); // Phương thức mới
    void reopenPayroll(int thang, int nam) throws BusinessException;
}