package org.example.service.impl;

import org.example.entity.HopDong;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.repository.HopDongRepository;
import org.example.repository.impl.HopDongRepositoryImpl;
import org.example.service.HopDongService;

import java.util.List;
import java.util.Optional;

public class HopDongServiceImpl implements HopDongService {

    private final HopDongRepository hopDongRepository;

    public HopDongServiceImpl() {
        this.hopDongRepository = new HopDongRepositoryImpl();
    }

    @Override
    public List<HopDong> getHopDongByMaNv(String maNv) {
        return hopDongRepository.findByMaNv(maNv);
    }

    @Override
    public Optional<HopDong> getHopDongById(String maHd) {
        return hopDongRepository.findById(maHd);
    }

    @Override
    public HopDong createHopDong(HopDong hopDong) throws ValidationException, BusinessException {
        validate(hopDong);
        if (hopDongRepository.existsById(hopDong.getMaHd())) {
            throw new BusinessException("Mã hợp đồng đã tồn tại.");
        }
        return hopDongRepository.save(hopDong);
    }

    @Override
    public HopDong updateHopDong(HopDong hopDong) throws ValidationException, BusinessException {
        validate(hopDong);
        if (!hopDongRepository.existsById(hopDong.getMaHd())) {
            throw new BusinessException("Không tìm thấy hợp đồng để cập nhật.");
        }
        hopDongRepository.update(hopDong);
        return hopDong;
    }

    @Override
    public void deleteHopDong(String maHd) throws BusinessException {
        if (!hopDongRepository.existsById(maHd)) {
            throw new BusinessException("Không tìm thấy hợp đồng để xóa.");
        }
        hopDongRepository.deleteById(maHd);
    }

    private void validate(HopDong hopDong) throws ValidationException {
        if (hopDong.getMaHd() == null || hopDong.getMaHd().isBlank()) {
            throw new ValidationException("Mã hợp đồng không được để trống.");
        }
        if (hopDong.getMaNv() == null || hopDong.getMaNv().isBlank()) {
            throw new ValidationException("Mã nhân viên không được để trống.");
        }
        if (hopDong.getNgayBatDau() == null) {
            throw new ValidationException("Ngày bắt đầu không được để trống.");
        }
        if (hopDong.getNgayKetThuc() != null && hopDong.getNgayKetThuc().isBefore(hopDong.getNgayBatDau())) {
            throw new ValidationException("Ngày kết thúc không được trước ngày bắt đầu.");
        }
        if (hopDong.getLuongCoBan() == null) {
            throw new ValidationException("Lương cơ bản không được để trống.");
        }
    }
}