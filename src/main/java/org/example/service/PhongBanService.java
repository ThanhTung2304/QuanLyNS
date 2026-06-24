package org.example.service;

import org.example.entity.PhongBan;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface PhongBanService {

    List<PhongBan> getAllPhongBan();

    Optional<PhongBan> getPhongBanById(String maPhong);

    PhongBan createPhongBan(PhongBan phongBan) throws ValidationException, BusinessException;

    PhongBan updatePhongBan(PhongBan phongBan) throws ValidationException, BusinessException;

    void deletePhongBan(String maPhong) throws BusinessException;
    
    void assignTruongPhong(String maPhong, String maNvTruongPhongMoi) throws BusinessException;
}