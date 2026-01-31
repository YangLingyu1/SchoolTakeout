package api.cssc.ciallo.games.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "openid", nullable = false, length = 100, unique = true)
    private String openid;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "is_rider", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isRider = false;

    @Column(name = "rider_status", length = 20, columnDefinition = "ENUM('not_applied', 'pending', 'approved', 'rejected') DEFAULT 'not_applied'")
    private String riderStatus = "not_applied";

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getIsRider() {
        return isRider;
    }

    public void setIsRider(Boolean isRider) {
        this.isRider = isRider;
    }

    public String getRiderStatus() {
        return riderStatus;
    }

    public void setRiderStatus(String riderStatus) {
        this.riderStatus = riderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
