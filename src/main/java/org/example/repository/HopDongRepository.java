package org.example.repository;

import org.example.entity.HopDong;
import java.util.List;
import java.util.Optional;

public interface HopDongRepository {
    List<HopDong> findByMaNv(String maNv);
    Optional<HopDong> findById(String maHd);
    Optional<HopDong> findLatestByMaNv(String maNv); // Phương thức mới
    HopDong save(HopDong hopDong);
    boolean update(HopDong hopDong);
    boolean deleteById(String maHd);
    boolean existsById(String maHd);
}