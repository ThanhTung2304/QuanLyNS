package org.example.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class HopDong {

    public enum LoaiHopDong {
        CHINH_THUC,
        THU_VIEC,
        BAN_THOI_GIAN,
        KINH_DOANH_KPI
    }

    public enum TrangThaiHopDong {
        CON_HIEU_LUC,
        HET_HIEU_LUC,
        DA_CHAM_DUT
    }

    private String maHd;
    private String maNv;
    private LoaiHopDong loaiHd;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private BigDecimal luongCoBan;
    private TrangThaiHopDong trangThai;

    public HopDong() {
    }

    // Getters and Setters
    public String getMaHd() {
        return maHd;
    }

    public void setMaHd(String maHd) {
        this.maHd = maHd;
    }

    public String getMaNv() {
        return maNv;
    }

    public void setMaNv(String maNv) {
        this.maNv = maNv;
    }

    public LoaiHopDong getLoaiHd() {
        return loaiHd;
    }

    public void setLoaiHd(LoaiHopDong loaiHd) {
        this.loaiHd = loaiHd;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public BigDecimal getLuongCoBan() {
        return luongCoBan;
    }

    public void setLuongCoBan(BigDecimal luongCoBan) {
        this.luongCoBan = luongCoBan;
    }

    public TrangThaiHopDong getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiHopDong trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HopDong hopDong = (HopDong) o;
        return Objects.equals(maHd, hopDong.maHd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHd);
    }
}