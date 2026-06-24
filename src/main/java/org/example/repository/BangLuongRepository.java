package org.example.repository;

import org.example.entity.BangLuong;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BangLuongRepository {
    void saveAll(List<BangLuong> bangLuongList);
    boolean existsByMaNvAndThangAndNam(String maNv, int thang, int nam);
    List<BangLuong> findByThangAndNam(int thang, int nam);
    Optional<BangLuong> findByMaNvAndThangAndNam(String maNv, int thang, int nam);
    void deleteByThangAndNam(int thang, int nam);

    // --- Methods for Dashboard ---
    BigDecimal sumTotalSalaryForMonth(int thang, int nam);
    BigDecimal sumTotalBonusForMonth(int thang, int nam);
    BigDecimal sumTotalDeductionForMonth(int thang, int nam);
}