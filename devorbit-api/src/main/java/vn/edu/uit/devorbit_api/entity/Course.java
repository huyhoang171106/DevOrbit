package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "courses")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stt")
    private Long id;

    @Column(name = "mamh", unique = true, nullable = false, length = 20)
    private String maMH;

    @Column(name = "tenmh", nullable = false, length = 255)
    private String tenMH;

    @Column(name = "tenmh_en", length = 255)
    private String tenMH_EN;

    @Column(name = "sotc")
    private int soTC;

    private int lt;
    private int th;

    @Column(name = "loaimonhoc", nullable = false, length = 50)
    private String loaiMonHoc;

    private Integer semester;

    @Column(name = "is_open")
    private boolean isOpen;

    @Column(name = "management_unit", length = 100)
    private String managementUnit;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "mamh_old", length = 50)
    private String maMH_Old;

    @Column(name = "equivalent_mh")
    private String equivalentMH;

    @Column(name = "prerequisite_mh")
    private String prerequisiteMH;

    @Column(name = "previous_mh")
    private String previousMH;
}
