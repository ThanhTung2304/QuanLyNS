package org.example.repository;

import org.example.entity.NhanVien;

import java.util.List;
import java.util.Optional;

public interface NhanVienRepository {

    Optional<NhanVien> findById(String maNv);
    List<NhanVien> findAll();
    List<NhanVien> findByMaPhong(String maPhong);
    NhanVien save(NhanVien nhanVien);
    boolean update(NhanVien nhanVien);
    boolean updateChucVu(String maNv, String maChucVuMoi);
    boolean deleteById(String maNv);
    List<NhanVien> findByNameContaining(String name);
    boolean existsById(String maNv);

    // --- Methods for Dashboard ---
    long countActiveEmployees();
    long countNewEmployeesInCurrentMonth();
    long countResignedEmployeesInCurrentMonth();
}