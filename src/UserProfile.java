// Kullanıcı profil bilgilerini yöneten sınıf
public class UserProfile {
    String username;
    String email;
    int volume;       // 0-100 arası
    boolean darkMode;

    public UserProfile(String username, String email) {
        this.username = username;
        this.email = email;
        this.volume = 50;
        this.darkMode = false;
    }

    public void increaseVolume() {
        if (volume < 100) {
            volume += 5;
            System.out.println("Ses artırıldı: " + volume);
        } else {
            System.out.println("Ses zaten maksimumda (100).");
        }
    }

    public void decreaseVolume() {
        if (volume > 0) {
            volume -= 5;
            System.out.println("Ses azaltıldı: " + volume);
        } else {
            System.out.println("Ses zaten minimumda (0).");
        }
    }

    public void setVolume(int value) {
        if (value >= 0 && value <= 100) {
            this.volume = value;
            System.out.println("Ses " + value + " olarak ayarlandı.");
        } else {
            System.out.println("Geçersiz ses değeri. 0-100 arasında giriniz.");
        }
    }

    public void toggleDarkMode() {
        darkMode = !darkMode;
        System.out.println("Tema değiştirildi: " + (darkMode ? "Karanlık Mod" : "Aydınlık Mod"));
    }

    public void setUsername(String username) {
        this.username = username;
        System.out.println("Kullanıcı adı güncellendi: " + username);
    }

    public void setEmail(String email) {
        this.email = email;
        System.out.println("E-posta güncellendi: " + email);
    }

    public void printProfile() {
        System.out.println("\n=== Profil Bilgileri ===");
        System.out.println("Kullanıcı Adı : " + username);
        System.out.println("E-posta        : " + email);
        System.out.println("Ses Seviyesi   : " + volume + "/100");
        System.out.println("Tema           : " + (darkMode ? "Karanlık Mod" : "Aydınlık Mod"));
    }
}
