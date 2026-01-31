package api.cssc.ciallo.games.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rider_applications")
public class RiderApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @Column(name = "real_name", nullable = false, length = 64)
    private String realName;
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;
    @Column(name = "password", nullable = false, length = 100)
    private String password;
    @Column(name = "id_card", nullable = false, length = 20)
    private String idCard;

    @Column(name = "id_photo", nullable = false, columnDefinition = "LONGTEXT")
    private String idPhoto;
    
    @Column(name = "id_photo_back", nullable = false, columnDefinition = "LONGTEXT")
    private String idPhotoBack;

    @Column(name = "status", columnDefinition = "ENUM('pending', 'approved', 'rejected') DEFAULT 'pending'")
    private String status = "pending";

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public RiderApplication() {
    }

    public RiderApplication(Integer id, Integer userId, User user, String realName, String phone, String password, String idCard, String idPhoto, String idPhotoBack, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.user = user;
        this.realName = realName;
        this.phone = phone;
        this.password = password;
        this.idCard = idCard;
        this.phone = phone;
        this.idPhoto = idPhoto;
        this.idPhotoBack = idPhotoBack;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdPhoto() {
        return idPhoto;
    }

    public void setIdPhoto(String idPhoto) {
        this.idPhoto = idPhoto;
    }

    public String getIdPhotoBack() {
        return idPhotoBack;
    }

    public void setIdPhotoBack(String idPhotoBack) {
        this.idPhotoBack = idPhotoBack;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return "RiderApplication{id = " + id + ", userId = " + userId + ", user = " + user + ", realName = " + realName + ", phone = " + phone + ", password = " + password + ", idCard = " + idCard + ", phone = " + phone + ", idPhoto = " + idPhoto + ", idPhotoBack = " + idPhotoBack + ", status = " + status + ", createdAt = " + createdAt + "}";
    }
}
