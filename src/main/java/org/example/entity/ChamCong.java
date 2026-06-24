package org.example.entity;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class ChamCong {

    public enum TrangThaiChamCong {
        DI_LAM,
        DI_MUON,
        VANG_MAT,
        NGHI_PHEP
    }

    private Integer maCc;
    private String maNv;
    private LocalDate ngay;
    private LocalTime gioVao;
    private LocalTime gioRa;
    private TrangThaiChamCong trangThai;
    private String ghiChu;

    public ChamCong() {
    }

    public Integer getMaCc() {
        return maCc;
    }

    public void setMaCc(Integer maCc) {
        this.maCc = maCc;
    }

    public String getMaNv() {
        return maNv;
    }

    public void setMaNv(String maNv) {
        this.maNv = maNv;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    public LocalTime getGioVao() {
        return gioVao;
    }

    public void setGioVao(LocalTime gioVao) {
        this.gioVao = gioVao;
    }

    public LocalTime getGioRa() {
        return gioRa;
    }

    public void setGioRa(LocalTime gioRa) {
        this.gioRa = gioRa;
    }

    public TrangThaiChamCong getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiChamCong trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    // Phương thức tự động tính số giờ làm
    public Double getSoGioLam() {
        if (gioVao != null && gioRa != null) {
            Duration duration = Duration.between(gioVao, gioRa);
            long minutes = duration.toMinutes();
            if (minutes > 0) {
                // Làm tròn đến 2 chữ số thập phân
                return Math.round((minutes / 60.0) * 100.0) / 100.0;
            }
        }
        return 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChamCong chamCong = (ChamCong) o;
        return Objects.equals(maCc, chamCong.maCc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCc);
    }
}