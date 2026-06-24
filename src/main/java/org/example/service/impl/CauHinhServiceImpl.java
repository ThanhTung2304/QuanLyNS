package org.example.service.impl;

import org.example.entity.CauHinh;
import org.example.repository.CauHinhRepository;
import org.example.repository.impl.CauHinhRepositoryImpl;
import org.example.service.CauHinhService;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CauHinhServiceImpl implements CauHinhService {

    private final CauHinhRepository cauHinhRepository;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public CauHinhServiceImpl() {
        this.cauHinhRepository = new CauHinhRepositoryImpl();
    }

    @Override
    public LocalTime getGioVaoSang() {
        return cauHinhRepository.findByKey("GIO_VAO_SANG")
                .map(cauHinh -> LocalTime.parse(cauHinh.getSettingValue(), timeFormatter))
                .orElse(LocalTime.of(8, 0)); // Giá trị mặc định nếu không có trong DB
    }

    @Override
    public void setGioVaoSang(LocalTime time) {
        cauHinhRepository.saveOrUpdate(new CauHinh("GIO_VAO_SANG", time.format(timeFormatter), "Giờ bắt đầu làm việc buổi sáng"));
    }

    @Override
    public LocalTime getGioRaChieu() {
        return cauHinhRepository.findByKey("GIO_RA_CHIEU")
                .map(cauHinh -> LocalTime.parse(cauHinh.getSettingValue(), timeFormatter))
                .orElse(LocalTime.of(17, 30));
    }

    @Override
    public void setGioRaChieu(LocalTime time) {
        cauHinhRepository.saveOrUpdate(new CauHinh("GIO_RA_CHIEU", time.format(timeFormatter), "Giờ kết thúc làm việc buổi chiều"));
    }

    @Override
    public int getSoPhutDiMuonChoPhep() {
        return cauHinhRepository.findByKey("SO_PHUT_DI_MUON_CHO_PHEP")
                .map(cauHinh -> Integer.parseInt(cauHinh.getSettingValue()))
                .orElse(15);
    }

    @Override
    public void setSoPhutDiMuonChoPhep(int minutes) {
        cauHinhRepository.saveOrUpdate(new CauHinh("SO_PHUT_DI_MUON_CHO_PHEP", String.valueOf(minutes), "Số phút tối đa được phép đi muộn (phút)"));
    }
}