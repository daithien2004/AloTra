package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userID;

    @Column(name = "FullName", nullable = false, columnDefinition = "TEXT") // sửa tại đây
    private String fullName;

    @Column(name = "Date", nullable = false)
    private Date date;

    @Column(name = "Address", nullable = false, columnDefinition = "TEXT") // sửa tại đây
    private String address;

    @Column(name = "Phone", nullable = false, length = 10)
    private String phone;

    @Column(name = "Email", nullable = false, columnDefinition = "TEXT") // sửa tại đây
    private String email;

    @Column(name = "Username", nullable = false, columnDefinition = "TEXT") // sửa tại đây
    private String userName;

    @Column(name = "Password", nullable = false, columnDefinition = "TEXT") // sửa tại đây
    private String password;

    @Column(name = "Active", nullable = false)
    private boolean active;

    @Column(name = "Image", columnDefinition = "TEXT") // sửa tại đây
    private String image;

    @Column(name = "RoleID", nullable = false)
    private int roleID;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany(mappedBy = "user")
    private List<Rate> rates;

    @OneToMany(mappedBy = "user")
    private List<Shipper> shippers;

    @ManyToOne
    @JoinColumn(name = "RoleID", referencedColumnName = "roleID", insertable = false, updatable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Rate> userMilkTeaRates;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Like> userMilkTeaLikes;

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
