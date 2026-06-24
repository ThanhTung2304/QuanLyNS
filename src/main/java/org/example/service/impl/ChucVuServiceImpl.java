package org.example.service.impl;

import org.example.entity.ChucVu;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.repository.ChucVuRepository;
import org.example.repository.impl.ChucVuRepositoryImpl;
import org.example.service.ChucVuService;

import java.util.List;
import java.util.Optional;

public class ChucVuServiceImpl implements ChucVuService {

    private final ChucVuRepository chucVuRepository;

    public ChucVuServiceImpl() {
        this.chucVuRepository = new ChucVuRepositoryImpl();
    }

    @Override
    public List<ChucVu> getAllChucVu() {
        return chucVuRepository.findAll();
    }

    @Override
    public Optional<ChucVu> getChucVuById(String maChucVu) {
        return chucVuRepository.findById(maChucVu);
    }

    @Override
    public ChucVu createChucVu(ChucVu chucVu) throws ValidationException, BusinessException {
        validate(chucVu);
        if (chucVuRepository.existsById(chucVu.getMaChucVu())) {
            throw new BusinessException("Mã chức vụ đã tồn tại.");
        }
        return chucVuRepository.save(chucVu);
    }

    @Override
    public ChucVu updateChucVu(ChucVu chucVu) throws ValidationException, BusinessException {
        validate(chucVu);
        if (!chucVuRepository.existsById(chucVu.getMaChucVu())) {
            throw new BusinessException("Không tìm thấy chức vụ để cập nhật.");
        }
        chucVuRepository.update(chucVu);
        return chucVu;
    }

    @Override
    public void deleteChucVu(String maChucVu) throws BusinessException {
        if (!chucVuRepository.existsById(maChucVu)) {
            throw new BusinessException("Không tìm thấy chức vụ để xóa.");
        }
        // TODO: Kiểm tra xem chức vụ có đang được sử dụng bởi nhân viên nào không trước khi xóa
        chucVuRepository.deleteById(maChucVu);
    }

    private void validate(ChucVu chucVu) throws ValidationException {
        if (chucVu.getMaChucVu() == null || chucVu.getMaChucVu().isBlank()) {
            throw new ValidationException("Mã chức vụ không được để trống.");
        }
        if (chucVu.getTenChucVu() == null || chucVu.getTenChucVu().isBlank()) {
            throw new ValidationException("Tên chức vụ không được để trống.");
        }
    }
}