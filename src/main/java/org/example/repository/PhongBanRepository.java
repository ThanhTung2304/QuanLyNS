package org.example.repository;

import org.example.entity.PhongBan;

import java.util.List;
import java.util.Optional;

public interface PhongBanRepository {

    List<PhongBan> findAll();

    Optional<PhongBan> findById(String maPhong);

    PhongBan save(PhongBan phongBan);

    boolean update(PhongBan phongBan);

    boolean deleteById(String maPhong);

    boolean existsById(String maPhong);
}