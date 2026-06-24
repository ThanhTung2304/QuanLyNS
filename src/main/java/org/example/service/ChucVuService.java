package org.example.service;

import org.example.entity.ChucVu;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface ChucVuService {
    List<ChucVu> getAllChucVu();
    Optional<ChucVu> getChucVuById(String maChucVu);
    ChucVu createChucVu(ChucVu chucVu) throws ValidationException, BusinessException;
    ChucVu updateChucVu(ChucVu chucVu) throws ValidationException, BusinessException;
    void deleteChucVu(String maChucVu) throws BusinessException;
}