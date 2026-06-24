package org.example.entity;

import java.util.Objects;

/**
 * Đại diện cho một phòng ban trong hệ thống.
 */
public class PhongBan {

    private String maPhong;
    private String tenPhong;
    private String moTa;

    // Các trường phụ trợ cho việc hiển thị (lấy từ bảng nhan_vien)
    private String tenTruongPhong;
    private int soLuongNhanVien;

    public PhongBan() {
    }

    public PhongBan(String maPhong, String tenPhong, String moTa) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.moTa = moTa;
    }

    // Getters and Setters
    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTenTruongPhong() {
        return tenTruongPhong;
    }

    public void setTenTruongPhong(String tenTruongPhong) {
        this.tenTruongPhong = tenTruongPhong;
    }

    public int getSoLuongNhanVien() {
        return soLuongNhanVien;
    }

    public void setSoLuongNhanVien(int soLuongNhanVien) {
        this.soLuongNhanVien = soLuongNhanVien;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhongBan phongBan = (PhongBan) o;
        return Objects.equals(maPhong, phongBan.maPhong);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhong);
    }

    @Override
    public String toString() {
        return tenPhong;
    }
}