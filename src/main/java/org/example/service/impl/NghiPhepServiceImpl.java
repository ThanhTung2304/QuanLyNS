package org.example.service.impl;

import org.example.entity.NghiPhep;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.repository.NghiPhepRepository;
import org.example.repository.impl.NghiPhepRepositoryImpl;
import org.example.service.NghiPhepService;

import java.util.List;

public class NghiPhepServiceImpl implements NghiPhepService {

    private final NghiPhepRepository nghiPhepRepository;

    public NghiPhepServiceImpl() {
        this.nghiPhepRepository = new NghiPhepRepositoryImpl();
    }

    @Override
    public List<NghiPhep> getByMaNv(String maNv) {
        return nghiPhepRepository.findByMaNv(maNv);
    }

    @Override
    public List<NghiPhep> getAll() {
        return nghiPhepRepository.findAll();
    }

    @Override
    public NghiPhep create(NghiPhep nghiPhep) throws ValidationException, BusinessException {
        validate(nghiPhep);
        nghiPhep.setTrangThai(NghiPhep.TrangThaiNghiPhep.CHO_DUYET);
        return nghiPhepRepository.save(nghiPhep);
    }

    @Override
    public void delete(Integer maNp) throws BusinessException {
        NghiPhep nghiPhep = nghiPhepRepository.findById(maNp)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn nghỉ phép."));
        if (nghiPhep.getTrangThai() != NghiPhep.TrangThaiNghiPhep.CHO_DUYET) {
            throw new BusinessException("Chỉ có thể xóa đơn nghỉ phép ở trạng thái 'Chờ duyệt'.");
        }
        nghiPhepRepository.deleteById(maNp);
    }

    @Override
    public void approve(Integer maNp, String maNguoiDuyet) throws BusinessException {
        NghiPhep nghiPhep = nghiPhepRepository.findById(maNp)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn nghỉ phép."));
        if (nghiPhep.getMaNv().equals(maNguoiDuyet)) {
            throw new BusinessException("Không thể tự duyệt đơn của chính mình.");
        }
        nghiPhep.setTrangThai(NghiPhep.TrangThaiNghiPhep.DA_DUYET);
        nghiPhep.setNguoiDuyet(maNguoiDuyet);
        nghiPhepRepository.update(nghiPhep);
    }

    @Override
    public void reject(Integer maNp, String maNguoiDuyet) throws BusinessException {
        NghiPhep nghiPhep = nghiPhepRepository.findById(maNp)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn nghỉ phép."));
        if (nghiPhep.getMaNv().equals(maNguoiDuyet)) {
            throw new BusinessException("Không thể tự duyệt đơn của chính mình.");
        }
        nghiPhep.setTrangThai(NghiPhep.TrangThaiNghiPhep.TU_CHOI);
        nghiPhep.setNguoiDuyet(maNguoiDuyet);
        nghiPhepRepository.update(nghiPhep);
    }

    private void validate(NghiPhep nghiPhep) throws ValidationException {
        if (nghiPhep.getNgayBatDau() == null || nghiPhep.getNgayKetThuc() == null) {
            throw new ValidationException("Ngày bắt đầu và kết thúc không được để trống.");
        }
        if (nghiPhep.getNgayKetThuc().isBefore(nghiPhep.getNgayBatDau())) {
            throw new ValidationException("Ngày kết thúc không được trước ngày bắt đầu.");
        }
        if (nghiPhep.getLyDo() == null || nghiPhep.getLyDo().isBlank()) {
            throw new ValidationException("Lý do không được để trống.");
        }
    }
}