package org.example.controller;

import org.example.entity.NhanVien;
import org.example.entity.PhongBan;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.service.NhanVienService;
import org.example.service.PhongBanService;
import org.example.service.impl.NhanVienServiceImpl;
import org.example.service.impl.PhongBanServiceImpl;

import java.util.List;
import java.util.Optional;

public class PhongBanController {

    private final PhongBanService phongBanService;
    private final NhanVienService nhanVienService;

    public PhongBanController() {
        this.phongBanService = new PhongBanServiceImpl();
        this.nhanVienService = new NhanVienServiceImpl();
    }

    public List<PhongBan> getAllPhongBan() {
        return phongBanService.getAllPhongBan();
    }

    public Optional<PhongBan> getPhongBanById(String maPhong) {
        return phongBanService.getPhongBanById(maPhong);
    }

    public PhongBan createPhongBan(PhongBan phongBan) throws ValidationException, BusinessException {
        return phongBanService.createPhongBan(phongBan);
    }

    public PhongBan updatePhongBan(PhongBan phongBan) throws ValidationException, BusinessException {
        return phongBanService.updatePhongBan(phongBan);
    }

    public void deletePhongBan(String maPhong) throws BusinessException {
        phongBanService.deletePhongBan(maPhong);
    }
    
    // Các phương thức hỗ trợ cho việc gán trưởng phòng
    public List<NhanVien> getNhanVienByPhongBan(String maPhong) {
        return nhanVienService.getNhanVienByPhongBan(maPhong);
    }

    public void assignTruongPhong(String maPhong, String maNvTruongPhongMoi) throws BusinessException {
        phongBanService.assignTruongPhong(maPhong, maNvTruongPhongMoi);
    }
}