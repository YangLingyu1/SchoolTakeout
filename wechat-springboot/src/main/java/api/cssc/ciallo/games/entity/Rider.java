package api.cssc.ciallo.games.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "riders")
public class Rider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @Column(name = "real_name", nullable = false, length = 64)
    private String realName;

    @Column(name = "id_card", nullable = false, length = 20)
    private String idCard;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "status", columnDefinition = "ENUM('active', 'inactive', 'suspended') DEFAULT 'active'")
    private String status = "active";

    @Column(name = "level", columnDefinition = "ENUM('normal', 'silver', 'gold') DEFAULT 'normal'")
    private String level = "normal";

    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "total_orders")
    private Integer totalOrders = 0;

    @Column(name = "total_earnings", precision = 10, scale = 2)
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @Column(name = "payment_code", length = 255)
    private String paymentCode;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Rider() {
    }

    public Rider(Integer id, Integer userId, User user, String realName, String idCard, String phone, String password, String status, String level, BigDecimal balance, Integer totalOrders, BigDecimal totalEarnings, String paymentCode, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.user = user;
        this.realName = realName;
        this.idCard = idCard;
        this.phone = phone;
        this.password = password;
        this.status = status;
        this.level = level;
        this.balance = balance;
        this.totalOrders = totalOrders;
        this.totalEarnings = totalEarnings;
        this.paymentCode = paymentCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
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

    @Override
    public String toString() {
        return "Rider{id = " + id + ", userId = " + userId + ", user = " + user + ", realName = " + realName + ", idCard = " + idCard + ", phone = " + phone + ", status = " + status + ", level = " + level + ", balance = " + balance + ", totalOrders = " + totalOrders + ", totalEarnings = " + totalEarnings + ", paymentCode = " + paymentCode + ", createdAt = " + createdAt + ", updatedAt = " + updatedAt + "}";
    }
}
