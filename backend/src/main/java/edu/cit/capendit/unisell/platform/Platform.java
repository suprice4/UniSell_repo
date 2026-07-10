package edu.cit.capendit.unisell.platform;

import edu.cit.capendit.unisell.auth.User;
import jakarta.persistence.*;

@Entity
@Table(name = "platforms", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "vendor_id"})
})
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private User vendor;

    public Platform() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public User getVendor() { return vendor; }
    public void setVendor(User vendor) { this.vendor = vendor; }
}