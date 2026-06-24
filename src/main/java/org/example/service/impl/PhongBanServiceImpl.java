package org.example.service.impl;

import org.example.entity.NhanVien;
import org.example.entity.PhongBan;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.repository.NhanVienRepository;
import org.example.repository.PhongBanRepository;
import org.example.repository.impl.NhanVienRepositoryImpl;
import org.example.repository.impl.PhongBanRepositoryImpl;
import org.example.service.PhongBanService;

import java.util.List;
import java.util.Optional;

public class PhongBanServiceImpl implements PhongBanService {

    private final PhongBanRepository phongBanRepository;
    private final NhanVienRepository nhanVienRepository;

    private static final String MA_CHUC_VU_TRUONG_PHONG = "CV01";
    private static final String MA_CHUC_VU_NHAN_VIEN = "CV02";

    public PhongBanServiceImpl() {
        this.phongBanRepository = new PhongBanRepositoryImpl();
        this.nhanVienRepository = new NhanVienRepositoryImpl();
    }

    @Override
    public List<PhongBan> getAllPhongBan() {
        return phongBanRepository.findAll();
    }

    @Override
    public Optional<PhongBan> getPhongBanById(String maPhong) {
        return phongBanRepository.findById(maPhong);
    }

    @Override
    public PhongBan createPhongBan(PhongBan phongBan) throws ValidationException, BusinessException {
        validatePhongBan(phongBan);
        if (phongBanRepository.existsById(phongBan.getMaPhong())) {
            throw new BusinessException("Mã phòng ban đã tồn tại.");
        }
        return phongBanRepository.save(phongBan);
    }

    @Override
    public PhongBan updatePhongBan(PhongBan phongBan) throws ValidationException, BusinessException {
        validatePhongBan(phongBan);
        if (!phongBanRepository.existsById(phongBan.getMaPhong())) {
            throw new BusinessException("Không tìm thấy phòng ban để cập nhật.");
        }
        phongBanRepository.update(phongBan);
        return phongBan;
    }

    @Override
    public void deletePhongBan(String maPhong) throws BusinessException {
        if (!phongBanRepository.existsById(maPhong)) {
            throw new BusinessException("Không tìm thấy phòng ban để xóa.");
        }
        phongBanRepository.deleteById(maPhong);
    }

    @Override
    public void assignTruongPhong(String maPhong, String maNvTruongPhongMoi) throws BusinessException {
        // 1. Tìm trưởng phòng cũ (nếu có)
        nhanVienRepository.findByMaPhong(maPhong).stream()
                .filter(nv -> MA_CHUC_VU_TRUONG_PHONG.equals(nv.getMaChucVu()))
                .findFirst()
                .ifPresent(truongPhongCu -> {
                    // 2. Hạ cấp trưởng phòng cũ thành nhân viên
                    nhanVienRepository.updateChucVu(truongPhongCu.getMaNv(), MA_CHUC_VU_NHAN_VIEN);
                });

        // 3. Bổ nhiệm trưởng phòng mới
        nhanVienRepository.updateChucVu(maNvTruongPhongMoi, MA_CHUC_VU_TRUONG_PHONG);
    }

    private void validatePhongBan(PhongBan phongBan) throws ValidationException {
        if (phongBan.getMaPhong() == null || phongBan.getMaPhong().isBlank()) {
            throw new ValidationException("Mã phòng ban không được để trống.");
        }
        if (phongBan.getTenPhong() == null || phongBan.getTenPhong().isBlank()) {
            throw new ValidationException("Tên phòng ban không được để trống.");
        }
    }
}