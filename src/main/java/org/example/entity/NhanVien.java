package org.example.entity;

import java.time.LocalDate;
import java.util.Objects;

public class NhanVien {

    public enum GioiTinh {
        NAM, NU, KHAC
    }

    public enum TrangThaiLamViec {
        DANG_LAM_VIEC,
        DA_NGHI_VIEC
    }

    private String maNv;
    private String hoTen;
    private LocalDate ngaySinh;
    private GioiTinh gioiTinh;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private String maPb; // Khớp với cột ma_phong trong DB
    private String maChucVu; // Đã sửa: Khớp với cột ma_chuc_vu trong DB
    private LocalDate ngayVaoLam;
    private TrangThaiLamViec trangThai;

    public NhanVien() {
    }

    // Getters and Setters

    public String getMaNv() {
        return maNv;
    }

    public void setMaNv(String maNv) {
        this.maNv = maNv;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public GioiTinh getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(GioiTinh gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMaPb() {
        return maPb;
    }

    public void setMaPb(String maPb) {
        this.maPb = maPb;
    }

    public String getMaChucVu() {
        return maChucVu;
    }

    public void setMaChucVu(String maChucVu) {
        this.maChucVu = maChucVu;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public TrangThaiLamViec getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiLamViec trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhanVien nhanVien = (NhanVien) o;
        return Objects.equals(maNv, nhanVien.maNv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNv);
    }
}