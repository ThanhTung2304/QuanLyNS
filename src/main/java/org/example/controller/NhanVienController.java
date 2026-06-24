package org.example.controller;

import org.example.entity.NhanVien;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.service.NhanVienService;
import org.example.service.impl.NhanVienServiceImpl;

import java.util.List;
import java.util.Optional;

public class NhanVienController {

    private final NhanVienService nhanVienService;

    public NhanVienController() {
        this.nhanVienService = new NhanVienServiceImpl();
    }

    public List<NhanVien> getAllNhanVien() {
        return nhanVienService.getAllNhanVien();
    }

    // PHƯƠNG THỨC CÒN THIẾU ĐÃ ĐƯỢC BỔ SUNG
    public Optional<NhanVien> getNhanVienById(String maNv) {
        return nhanVienService.getNhanVienById(maNv);
    }

    public NhanVien createNhanVien(NhanVien nhanVien) throws ValidationException, BusinessException {
        return nhanVienService.createNhanVien(nhanVien);
    }

    public NhanVien updateNhanVien(NhanVien nhanVien) throws ValidationException, BusinessException {
        return nhanVienService.updateNhanVien(nhanVien);
    }

    public void deleteNhanVien(String maNv) throws BusinessException {
        nhanVienService.deleteNhanVien(maNv);
    }

    public List<NhanVien> searchNhanVien(String keyword) {
        return nhanVienService.searchNhanVien(keyword);
    }
}