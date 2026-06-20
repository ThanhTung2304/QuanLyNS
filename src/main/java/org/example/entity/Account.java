package org.example.entity;

import java.time.LocalDateTime;

/**
 * Entity Account - tương ứng bảng `tai_khoan` trong database.
 * Quan hệ: NhanVien (1) ----- (1) TaiKhoan
 */
public class Account {

    // Vai trò tương ứng 4 Actor trong Use Case Diagram
    public enum Role {
        ADMIN,
        HR,
        TRUONG_PHONG,
        NHAN_VIEN
    }

    // Trạng thái tài khoản
    public enum Status {
        HOAT_DONG,
        KHOA
    }

    private Integer maTk;                  // ma_tk - PK, AUTO_INCREMENT
    private String maNv;                   // ma_nv - FK -> nhan_vien.ma_nv (UNIQUE, quan hệ 1-1)
    private String tenDangNhap;            // ten_dang_nhap - UNIQUE
    private String matKhau;                // mat_khau
    private Role vaiTro;                   // vai_tro
    private Status trangThai;              // trang_thai
    private LocalDateTime lanDangNhapCuoi;  // lan_dang_nhap_cuoi - nullable
    private LocalDateTime ngayTao;          // ngay_tao

    public Account() {
        this.vaiTro = Role.NHAN_VIEN;
        this.trangThai = Status.HOAT_DONG;
    }

    public Account(String maNv, String tenDangNhap, String matKhau, Role vaiTro) {
        this.maNv = maNv;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = (vaiTro != null) ? vaiTro : Role.NHAN_VIEN;
        this.trangThai = Status.HOAT_DONG;
    }

    public Account(Integer maTk, String maNv, String tenDangNhap, String matKhau,
                   Role vaiTro, Status trangThai, LocalDateTime lanDangNhapCuoi,
                   LocalDateTime ngayTao) {
        this.maTk = maTk;
        this.maNv = maNv;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.lanDangNhapCuoi = lanDangNhapCuoi;
        this.ngayTao = ngayTao;
    }

    // ===== Getters & Setters =====

    public Integer getMaTk() {
        return maTk;
    }

    public void setMaTk(Integer maTk) {
        this.maTk = maTk;
    }

    public String getMaNv() {
        return maNv;
    }

    public void setMaNv(String maNv) {
        this.maNv = maNv;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public Role getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(Role vaiTro) {
        this.vaiTro = vaiTro;
    }

    public Status getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Status trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getLanDangNhapCuoi() {
        return lanDangNhapCuoi;
    }

    public void setLanDangNhapCuoi(LocalDateTime lanDangNhapCuoi) {
        this.lanDangNhapCuoi = lanDangNhapCuoi;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    // ===== Helper methods =====

    public boolean isActive() {
        return Status.HOAT_DONG.equals(this.trangThai);
    }

    public boolean hasRole(Role role) {
        return this.vaiTro == role;
    }

    @Override
    public String toString() {
        return "Account{" +
                "maTk=" + maTk +
                ", maNv='" + maNv + '\'' +
                ", tenDangNhap='" + tenDangNhap + '\'' +
                ", vaiTro=" + vaiTro +
                ", trangThai=" + trangThai +
                ", lanDangNhapCuoi=" + lanDangNhapCuoi +
                '}';
        // Lưu ý: không in matKhau ra log/toString để tránh lộ thông tin nhạy cảm
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return maTk != null && maTk.equals(account.maTk);
    }

    @Override
    public int hashCode() {
        return maTk != null ? maTk.hashCode() : 0;
    }
}
