// Şarkı bilgilerini tutan temel sınıf
public class Song {
    String title;
    String artist;
    String genre;
    int duration;    // saniye cinsinden
    int likeCount;
    String filePath;

    public Song(String title, String artist, String genre, int duration, int likeCount, String filePath) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.duration = duration;
        this.likeCount = likeCount;
        this.filePath = filePath;
    }

    // Şarkıyı beğen
    public void like() {
        likeCount++;
    }

    // Süreyi "dakika:saniye" formatında döndür
    public String getFormattedDuration() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    // Şarkı bilgilerini ekrana yazdır
    public void printSong() {
        System.out.println(title + " - " + artist + " | " + genre + " | " + getFormattedDuration() + " | Likes: " + likeCount);
    }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}
