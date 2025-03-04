package Model;

import java.util.Date;

public class Machine {
    private long id;                // Identifiant unique
    private String name;            // Nom de la machine
    private String processor;       // Processeur
    private int memory;             // Mémoire en Go
    private int storage;            // Stockage en Go
    private String operatingSystem; // Système d'exploitation
    private String gamesInstalled;  // Jeux installés (ex. liste sous forme de texte)
    private Date purchaseDate;      // Date d'achat
    private Date lastMaintenanceDate; // Date de la dernière maintenance
    private String status;          // Statut (ex. "active", "en panne")
    private Long userId;            // ID de l'utilisateur associé (peut être null)

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getGamesInstalled() {
        return gamesInstalled;
    }

    public void setGamesInstalled(String gamesInstalled) {
        this.gamesInstalled = gamesInstalled;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public Date getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    // Setters correspondants
    public void setStatus(String status) {
        this.status = status;
    }

    public void setLastMaintenanceDate(Date lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }
}