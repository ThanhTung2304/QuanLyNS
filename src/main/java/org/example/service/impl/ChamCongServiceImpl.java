package org.example.service.impl;

import org.example.entity.ChamCong;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.repository.ChamCongRepository;
import org.example.repository.impl.ChamCongRepositoryImpl;
import org.example.service.CauHinhService;
import org.example.service.ChamCongService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class ChamCongServiceImpl implements ChamCongService {

    private final ChamCongRepository chamCongRepository;
    private final CauHinhService cauHinhService; // Thêm service cấu hình

    public ChamCongServiceImpl() {
        this.chamCongRepository = new ChamCongRepositoryImpl();
        this.cauHinhService = new CauHinhServiceImpl(); // Khởi tạo
    }

    @Override
    public List<ChamCong> getChamCongByMaNv(String maNv) {
        return chamCongRepository.findByMaNv(maNv);
    }

    @Override
    public Optional<ChamCong> getChamCongById(Integer maCc) {
        return chamCongRepository.findById(maCc);
    }

    @Override
    public ChamCong createChamCong(ChamCong chamCong) throws ValidationException, BusinessException {
        validate(chamCong);
        return chamCongRepository.save(chamCong);
    }

    @Override
    public ChamCong updateChamCong(ChamCong chamCong) throws ValidationException, BusinessException {
        validate(chamCong);
        if (chamCong.getMaCc() == null || chamCongRepository.findById(chamCong.getMaCc()).isEmpty()) {
            throw new BusinessException("Không tìm thấy dữ liệu chấm công để cập nhật.");
        }
        chamCongRepository.update(chamCong);
        return chamCong;
    }

    @Override
    public void deleteChamCong(Integer maCc) throws BusinessException {
        if (maCc == null || chamCongRepository.findById(maCc).isEmpty()) {
            throw new BusinessException("Không tìm thấy dữ liệu chấm công để xóa.");
        }
        chamCongRepository.deleteById(maCc);
    }

    @Override
    public ChamCong checkIn(String maNv) throws BusinessException {
        LocalDate today = LocalDate.now();
        Optional<ChamCong> todayChamCongOpt = chamCongRepository.findByMaNvAndNgay(maNv, today);

        if (todayChamCongOpt.isPresent()) {
            throw new BusinessException("Bạn đã check-in hôm nay rồi.");
        }

        LocalTime now = LocalTime.now();
        ChamCong newChamCong = new ChamCong();
        newChamCong.setMaNv(maNv);
        newChamCong.setNgay(today);
        newChamCong.setGioVao(now);

        // Lấy cấu hình và xác định trạng thái ĐI_LAM hay ĐI_MUỘN
        LocalTime gioVaoQuyDinh = cauHinhService.getGioVaoSang();
        int soPhutDiMuonChoPhep = cauHinhService.getSoPhutDiMuonChoPhep();
        LocalTime thoiGianMuonToiDa = gioVaoQuyDinh.plusMinutes(soPhutDiMuonChoPhep);

        if (now.isAfter(thoiGianMuonToiDa)) {
            newChamCong.setTrangThai(ChamCong.TrangThaiChamCong.DI_MUON);
        } else {
            newChamCong.setTrangThai(ChamCong.TrangThaiChamCong.DI_LAM);
        }

        return chamCongRepository.save(newChamCong);
    }

    @Override
    public ChamCong checkOut(String maNv) throws BusinessException {
        LocalDate today = LocalDate.now();
        ChamCong todayChamCong = chamCongRepository.findByMaNvAndNgay(maNv, today)
                .orElseThrow(() -> new BusinessException("Bạn chưa check-in hôm nay."));

        if (todayChamCong.getGioRa() != null) {
            throw new BusinessException("Bạn đã check-out hôm nay rồi.");
        }

        todayChamCong.setGioRa(LocalTime.now());
        chamCongRepository.update(todayChamCong);
        return todayChamCong;
    }

    @Override
    public Optional<ChamCong> getTodayChamCong(String maNv) {
        return chamCongRepository.findByMaNvAndNgay(maNv, LocalDate.now());
    }

    private void validate(ChamCong chamCong) throws ValidationException {
        if (chamCong.getMaNv() == null || chamCong.getMaNv().isBlank()) {
            throw new ValidationException("Mã nhân viên không được để trống.");
        }
        if (chamCong.getNgay() == null) {
            throw new ValidationException("Ngày chấm công không được để trống.");
        }
        if (chamCong.getGioVao() != null && chamCong.getGioRa() != null) {
            if (chamCong.getGioRa().isBefore(chamCong.getGioVao())) {
                throw new ValidationException("Giờ ra không thể trước giờ vào.");
            }
        }
    }
}