package org.example.repository;

import org.example.entity.ChucVu;

import java.util.List;
import java.util.Optional;

public interface ChucVuRepository {
    List<ChucVu> findAll();
    Optional<ChucVu> findById(String maChucVu);
    ChucVu save(ChucVu chucVu);
    boolean update(ChucVu chucVu);
    boolean deleteById(String maChucVu);
    boolean existsById(String maChucVu);
}