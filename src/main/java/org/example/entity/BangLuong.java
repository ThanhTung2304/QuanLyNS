package org.example.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class BangLuong {

    // Các trường khớp với DB
    private Integer maBl;
    private String maNv;
    private int thang;
    private int nam;
    private BigDecimal luongCb;
    private BigDecimal phuCap;
    private BigDecimal khauTru;
    private BigDecimal thucLinh;
    private LocalDateTime ngayTao;

    // Các trường phụ trợ, không có trong DB, dùng để tính toán và hiển thị
    private String hoTen;
    private int soNgayCong;
    private BigDecimal thuong = BigDecimal.ZERO; // Mặc định thưởng là 0

    public BangLuong() {
    }

    // --- Getters and Setters ---

    public Integer getMaBl() {
        return maBl;
    }

    public void setMaBl(Integer maBl) {
        this.maBl = maBl;
    }

    public String getMaNv() {
        return maNv;
    }

    public void setMaNv(String maNv) {
        this.maNv = maNv;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public BigDecimal getLuongCb() {
        return luongCb;
    }

    public void setLuongCb(BigDecimal luongCb) {
        this.luongCb = luongCb;
    }

    public BigDecimal getPhuCap() {
        return phuCap;
    }

    public void setPhuCap(BigDecimal phuCap) {
        this.phuCap = phuCap;
    }

    public BigDecimal getKhauTru() {
        return khauTru;
    }

    public void setKhauTru(BigDecimal khauTru) {
        this.khauTru = khauTru;
    }

    public BigDecimal getThucLinh() {
        return thucLinh;
    }

    public void setThucLinh(BigDecimal thucLinh) {
        this.thucLinh = thucLinh;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public int getSoNgayCong() {
        return soNgayCong;
    }

    public void setSoNgayCong(int soNgayCong) {
        this.soNgayCong = soNgayCong;
    }

    public BigDecimal getThuong() {
        return thuong;
    }

    public void setThuong(BigDecimal thuong) {
        this.thuong = thuong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BangLuong bangLuong = (BangLuong) o;
        return Objects.equals(maBl, bangLuong.maBl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maBl);
    }
}