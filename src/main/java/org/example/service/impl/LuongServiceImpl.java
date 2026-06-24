package org.example.service.impl;

import org.example.entity.*;
import org.example.exception.BusinessException;
import org.example.repository.*;
import org.example.repository.impl.*;
import org.example.service.LuongService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LuongServiceImpl implements LuongService {

    private final NhanVienRepository nhanVienRepository;
    private final HopDongRepository hopDongRepository;
    private final ChamCongRepository chamCongRepository;
    private final BangLuongRepository bangLuongRepository;

    public LuongServiceImpl() {
        this.nhanVienRepository = new NhanVienRepositoryImpl();
        this.hopDongRepository = new HopDongRepositoryImpl();
        this.chamCongRepository = new ChamCongRepositoryImpl();
        this.bangLuongRepository = new BangLuongRepositoryImpl();
    }

    @Override
    public List<BangLuong> calculatePayroll(int thang, int nam) throws BusinessException {
        List<BangLuong> payroll = new ArrayList<>();
        List<NhanVien> activeEmployees = nhanVienRepository.findAll();

        for (NhanVien nv : activeEmployees) {
            if (bangLuongRepository.existsByMaNvAndThangAndNam(nv.getMaNv(), thang, nam)) {
                continue;
            }

            BangLuong bl = new BangLuong();
            bl.setMaNv(nv.getMaNv());
            bl.setHoTen(nv.getHoTen());
            bl.setThang(thang);
            bl.setNam(nam);

            Optional<HopDong> hopDongOpt = hopDongRepository.findLatestByMaNv(nv.getMaNv());
            BigDecimal luongCb = hopDongOpt.map(HopDong::getLuongCoBan).orElse(BigDecimal.ZERO);
            bl.setLuongCb(luongCb);

            bl.setPhuCap(BigDecimal.ZERO);
            bl.setThuong(BigDecimal.ZERO);
            bl.setKhauTru(BigDecimal.ZERO);

            int soNgayCong = chamCongRepository.countCongNgay(nv.getMaNv(), thang, nam);
            bl.setSoNgayCong(soNgayCong);

            BigDecimal luongTheoNgayCong = luongCb.divide(new BigDecimal(26), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(soNgayCong));
            BigDecimal thucLinh = luongTheoNgayCong.add(bl.getPhuCap()).add(bl.getThuong()).subtract(bl.getKhauTru());
            bl.setThucLinh(thucLinh);

            payroll.add(bl);
        }
        return payroll;
    }

    @Override
    public void finalizePayroll(List<BangLuong> bangLuongList) throws BusinessException {
        if (bangLuongList == null || bangLuongList.isEmpty()) {
            throw new BusinessException("Không có dữ liệu lương để lưu.");
        }
        bangLuongRepository.saveAll(bangLuongList);
    }

    @Override
    public List<BangLuong> findPayrollHistory(int thang, int nam) {
        return bangLuongRepository.findByThangAndNam(thang, nam);
    }

    @Override
    public Optional<BangLuong> findMyPayroll(String maNv, int thang, int nam) {
        return bangLuongRepository.findByMaNvAndThangAndNam(maNv, thang, nam);
    }

    @Override
    public void reopenPayroll(int thang, int nam) throws BusinessException {
        if (findPayrollHistory(thang, nam).isEmpty()) {
            throw new BusinessException("Không có bảng lương nào được chốt cho tháng " + thang + "/" + nam + " để mở lại.");
        }
        bangLuongRepository.deleteByThangAndNam(thang, nam);
    }
}