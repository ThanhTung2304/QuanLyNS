package org.example.service;

import org.example.entity.NhanVien;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface NhanVienService {

    List<NhanVien> getAllNhanVien();

    Optional<NhanVien> getNhanVienById(String maNv);
    
    List<NhanVien> getNhanVienByPhongBan(String maPhong);

    NhanVien createNhanVien(NhanVien nhanVien) throws ValidationException, BusinessException;

    NhanVien updateNhanVien(NhanVien nhanVien) throws ValidationException, BusinessException;

    void deleteNhanVien(String maNv) throws BusinessException;

    List<NhanVien> searchNhanVien(String keyword);
}