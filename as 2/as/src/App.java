import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public final class App {
    private final ArrayList<Kacamata> stockKacamata = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public ArrayList<Kacamata> getStockKacamata() {
        return stockKacamata;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public static void main(String[] args) {
        App fystics = new App();
        fystics.runApp();
    }

    private int getIntInput() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Masukkan harus berupa angka.");
                scanner.nextLine(); // Membersihkan input buffer
            }
        }
    }

    public final void runApp() {
        boolean appIsRunning = true;
        while (appIsRunning) {
            System.out.println("=== FYStics Optik ===");
            System.out.println("1. Tambah Stok Kacamata");
            System.out.println("2. Tampilkan Semua Stok Kacamata");
            System.out.println("3. Perbarui Stok Kacamata");
            System.out.println("4. Hapus Stok Kacamata");
            System.out.println("5. Keluar");
            System.out.print("Pilih menu: ");
            int menuChoice = scanner.nextInt();
            scanner.nextLine();

            switch (menuChoice) {
                case 1:
                    tambahStokKacamata();
                    break;
                case 2:
                    tampilkanSemuaStokKacamata();
                    break;
                case 3:
                    perbaruiStokKacamata();
                    break;
                case 4:
                    hapusStokKacamata();
                    break;
                case 5:
                    appIsRunning = false;
                    break;
                default:
                    System.out.println("Menu tidak valid.");
                    break;
            }
        }
    }

    private void tambahStokKacamata() {
        System.out.println("\nTambah Data Stok Kacamata");
        System.out.print("Masukkan nama kacamata: ");
        String namaKacamata = scanner.nextLine();
        System.out.print("Masukkan harga kacamata: ");
        double hargaKacamata = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Pilih jenis kacamata:");
        System.out.println("1. Anak");
        System.out.println("2. Dewasa");
        System.out.println("3. Fashion");
        System.out.print("Pilih jenis kacamata: ");
        int jenisChoice = getIntInput();
        scanner.nextLine();

        String kategori;
        switch (jenisChoice) {
            case 1:
                kategori = "Anak";
                break;
            case 2:
                kategori = "Dewasa";
                break;
            case 3:
                kategori = "Fashion";
                break;
            default:
                System.out.println("Pilihan tidak valid. Kacamata akan ditambahkan sebagai kacamata umum.");
                kategori = "Umum";
                break;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO tbkacamata (nama, harga, kategori) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, namaKacamata);
                pstmt.setDouble(2, hargaKacamata);
                pstmt.setString(3, kategori);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Gagal menambahkan stok kacamata. Terjadi kesalahan pada basis data.");
        }

        System.out.println("Stok kacamata berhasil ditambahkan.");
    }

    private void tampilkanSemuaStokKacamata() {
        System.out.println("\nSemua Data Stok Kacamata di FYStics Optik:");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM tbkacamata";
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    if (!rs.isBeforeFirst()) {
                        System.out.println("Stok kacamata kosong.");
                    } else {
                        while (rs.next()) {
                            String nama = rs.getString("nama");
                            double harga = rs.getDouble("harga");
                            String kategori = rs.getString("kategori");
                            System.out.println(". Nama: " + nama + ", Harga: Rp" + harga + ", Kategori: " + kategori);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Gagal menampilkan stok kacamata. Terjadi kesalahan pada basis data.");
        }
    }

    private void perbaruiStokKacamata() {
        System.out.println("\nPerbarui Data Stok Kacamata");
        System.out.print("Masukkan nomor urutan kacamata yang akan diperbarui: ");
        int nomorUrutan = getIntInput();
        scanner.nextLine();

        System.out.print("Masukkan nama kacamata baru: ");
        String newNama = scanner.nextLine();
        System.out.print("Masukkan harga kacamata baru: ");
        double newHarga = scanner.nextDouble();
        scanner.nextLine();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE tbkacamata SET nama = ?, harga = ? WHERE idkacamata = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newNama);
                pstmt.setDouble(2, newHarga);
                pstmt.setInt(3, nomorUrutan);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out
                            .println("Data stok kacamata dengan nomor urutan " + nomorUrutan + " berhasil diperbarui:");
                    System.out.println("Nama Kacamata: " + newNama);
                    System.out.println("Harga Kacamata: Rp" + newHarga);
                } else {
                    System.out.println("Nomor urutan kacamata tidak valid.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Gagal memperbarui stok kacamata. Terjadi kesalahan pada basis data.");
        }
    }

    private void hapusStokKacamata() {
        System.out.println("\nHapus Data Stok Kacamata");
        tampilkanSemuaStokKacamata();
        System.out.print("Masukkan nomor urutan kacamata yang akan dihapus: ");
        int nomorUrutan = getIntInput();
        scanner.nextLine();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM tbkacamata WHERE idkacamata = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, nomorUrutan);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Data stok kacamata dengan nomor urutan " + nomorUrutan + " berhasil dihapus.");
                } else {
                    System.out.println("Nomor urutan kacamata tidak valid.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Gagal menghapus stok kacamata. Terjadi kesalahan pada basis data.");
        }
    }

    public static abstract class Kacamata {
        private String namaKacamata;
        private double hargaKacamata;

        public Kacamata(String namaKacamata, double hargaKacamata) {
            this.namaKacamata = namaKacamata;
            this.hargaKacamata = hargaKacamata;
        }

        public String getNamaKacamata() {
            return namaKacamata;
        }

        public void setNamaKacamata(String namaKacamata) {
            this.namaKacamata = namaKacamata;
        }

        public double getHargaKacamata() {
            return hargaKacamata;
        }

        public void setHargaKacamata(double hargaKacamata) {
            this.hargaKacamata = hargaKacamata;
        }

        public abstract void displayInfo();
    }

    public static class KacamataDewasa extends Kacamata {
        public KacamataDewasa(String namaKacamata, double hargaKacamata) {
            super(namaKacamata, hargaKacamata);
        }

        @Override
        public void displayInfo() {
            System.out.println("Kacamata Dewasa: " + getNamaKacamata() + " (Rp" + getHargaKacamata() + ")");
        }
    }

    public static class KacamataFashion extends KacamataDewasa {
        public KacamataFashion(String namaKacamata, double hargaKacamata) {
            super(namaKacamata, hargaKacamata);
        }

        @Override
        public void displayInfo() {
            System.out.println("Kacamata Fashion: " + getNamaKacamata() + " (Rp" + getHargaKacamata() + ")");
        }
    }

    public static class KacamataAnak extends Kacamata {
        public KacamataAnak(String namaKacamata, double hargaKacamata) {
            super(namaKacamata, hargaKacamata);
        }

        @Override
        public void displayInfo() {
            System.out.println("Kacamata Anak-anak: " + getNamaKacamata() + " (Rp" + getHargaKacamata() + ")");
        }
    }

}
