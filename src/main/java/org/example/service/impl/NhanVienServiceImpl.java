package org.example.service.impl;

import org.example.entity.NhanVien;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.repository.NhanVienRepository;
import org.example.repository.impl.NhanVienRepositoryImpl;
import org.example.service.NhanVienService;

import java.util.List;
import java.util.Optional;

public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository nhanVienRepository;

    public NhanVienServiceImpl() {
        this.nhanVienRepository = new NhanVienRepositoryImpl();
    }

    public NhanVienServiceImpl(NhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }

    @Override
    public List<NhanVien> getAllNhanVien() {
        return nhanVienRepository.findAll();
    }

    @Override
    public Optional<NhanVien> getNhanVienById(String maNv) {
        return nhanVienRepository.findById(maNv);
    }

    @Override
    public List<NhanVien> getNhanVienByPhongBan(String maPhong) {
        return nhanVienRepository.findByMaPhong(maPhong);
    }

    @Override
    public NhanVien createNhanVien(NhanVien nhanVien) throws ValidationException, BusinessException {
        validateNhanVien(nhanVien);
        if (nhanVienRepository.existsById(nhanVien.getMaNv())) {
            throw new BusinessException("Mã nhân viên đã tồn tại: " + nhanVien.getMaNv());
        }
        return nhanVienRepository.save(nhanVien);
    }

    @Override
    public NhanVien updateNhanVien(NhanVien nhanVien) throws ValidationException, BusinessException {
        validateNhanVien(nhanVien);
        if (!nhanVienRepository.existsById(nhanVien.getMaNv())) {
            throw new BusinessException("Không tìm thấy nhân viên để cập nhật: " + nhanVien.getMaNv());
        }
        nhanVienRepository.update(nhanVien);
        return nhanVien;
    }

    @Override
    public void deleteNhanVien(String maNv) throws BusinessException {
        if (!nhanVienRepository.existsById(maNv)) {
            throw new BusinessException("Không tìm thấy nhân viên để xóa: " + maNv);
        }
        nhanVienRepository.deleteById(maNv);
    }

    @Override
    public List<NhanVien> searchNhanVien(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllNhanVien();
        }
        return nhanVienRepository.findByNameContaining(keyword.trim());
    }

    private void validateNhanVien(NhanVien nhanVien) throws ValidationException {
        if (nhanVien == null) {
            throw new ValidationException("Thông tin nhân viên không được để trống");
        }
        if (nhanVien.getMaNv() == null || nhanVien.getMaNv().isBlank()) {
            throw new ValidationException("Mã nhân viên không được để trống");
        }
        if (nhanVien.getHoTen() == null || nhanVien.getHoTen().isBlank()) {
            throw new ValidationException("Họ tên không được để trống");
        }
    }
}