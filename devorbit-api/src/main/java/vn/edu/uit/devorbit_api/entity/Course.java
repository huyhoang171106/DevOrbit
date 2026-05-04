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

    @Column(name = "sotc")
    private int soTC;

    private int lt;
    private int th;

    @Column(name = "loaimonhoc", nullable = false, length = 50)
    private String loaiMonHoc;
}

