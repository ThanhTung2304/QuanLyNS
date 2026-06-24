package org.example.repository;

import org.example.entity.ChamCong;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChamCongRepository {
    List<ChamCong> findByMaNv(String maNv);
    Optional<ChamCong> findById(Integer maCc);
    Optional<ChamCong> findByMaNvAndNgay(String maNv, LocalDate ngay);
    int countCongNgay(String maNv, int thang, int nam);
    ChamCong save(ChamCong chamCong);
    boolean update(ChamCong chamCong);
    boolean deleteById(Integer maCc);

    // --- Methods for Dashboard ---
    long countCheckInsToday();
    long countLateCheckInsToday();
}