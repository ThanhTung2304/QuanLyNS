package org.example.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class NghiPhep {

    public enum TrangThaiNghiPhep {
        CHO_DUYET,
        DA_DUYET,
        TU_CHOI
    }

    private Integer maNp;
    private String maNv;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String lyDo;
    private TrangThaiNghiPhep trangThai;
    private String nguoiDuyet;
    private LocalDateTime ngayTao;

    public NghiPhep() {
    }

    public Integer getMaNp() {
        return maNp;
    }

    public void setMaNp(Integer maNp) {
        this.maNp = maNp;
    }

    public String getMaNv() {
        return maNv;
    }

    public void setMaNv(String maNv) {
        this.maNv = maNv;
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

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public TrangThaiNghiPhep getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiNghiPhep trangThai) {
        this.trangThai = trangThai;
    }

    public String getNguoiDuyet() {
        return nguoiDuyet;
    }

    public void setNguoiDuyet(String nguoiDuyet) {
        this.nguoiDuyet = nguoiDuyet;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NghiPhep nghiPhep = (NghiPhep) o;
        return Objects.equals(maNp, nghiPhep.maNp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNp);
    }
}