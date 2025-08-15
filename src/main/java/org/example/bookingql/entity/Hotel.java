package org.example.bookingql.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE hotels SET isDeleted = true WHERE idHotels = ?")
@Where(clause = "isDeleted = false")
@Table(name = "hotels")
public class Hotel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "idHotels", length = 36)
    private UUID idHotels;

    @Column(name = "hotelName", length = 255)
    private String hotelName;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "price")
    private Float price;

    /**
     * Cột hotelImages dạng JSON.
     * Hibernate chưa có mapping JSON native cho MariaDB/MySQL,
     * bạn có thể dùng String để lưu JSON text,
     * hoặc dùng @Convert nếu muốn parse sang object.
     */
    @Column(name = "hotelImages", columnDefinition = "json")
    private String hotelImages;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "isDeleted")
    private Boolean isDeleted;
}
