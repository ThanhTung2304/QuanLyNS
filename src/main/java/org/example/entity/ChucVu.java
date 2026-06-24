package org.example.entity;

import java.util.Objects;

/**
 * Đại diện cho một chức vụ trong hệ thống.
 */
public class ChucVu {

    private String maChucVu;
    private String tenChucVu;
    private String moTa;

    public ChucVu() {
    }

    public ChucVu(String maChucVu, String tenChucVu, String moTa) {
        this.maChucVu = maChucVu;
        this.tenChucVu = tenChucVu;
        this.moTa = moTa;
    }

    public String getMaChucVu() {
        return maChucVu;
    }

    public void setMaChucVu(String maChucVu) {
        this.maChucVu = maChucVu;
    }

    public String getTenChucVu() {
        return tenChucVu;
    }

    public void setTenChucVu(String tenChucVu) {
        this.tenChucVu = tenChucVu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChucVu chucVu = (ChucVu) o;
        return Objects.equals(maChucVu, chucVu.maChucVu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maChucVu);
    }

    @Override
    public String toString() {
        return tenChucVu;
    }
}