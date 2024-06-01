import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Userr {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Selamat datang, Pengguna!");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean ulang = true;

        try {
            // Membuat koneksi ke database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dboptik", "root", "");

            // Menampilkan menu-menu dari database
            System.out.println("\n===== Menu Produk =====");
            String sql = "SELECT idkacamata, nama, harga FROM tbkacamata";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            // Menyimpan informasi produk ke dalam ArrayList
            ArrayList<String> daftarProduk = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("idkacamata");
                String namaProduk = rs.getString("nama");
                double hargaProduk = rs.getDouble("harga");
                daftarProduk.add(id + ". " + namaProduk + " - Rp" + hargaProduk);
                System.out.println(id + ". " + namaProduk + " - Rp" + hargaProduk);
            }

            // Meminta input id produk yang akan dipesan
            System.out.print("\nMasukkan ID produk yang ingin Anda pesan: ");
            int idPilihan;
            double totalHarga = 0;
            ArrayList<String> produkDibeli = new ArrayList<>();
            while (ulang) {

                if ((idPilihan = scanner.nextInt()) != 0) {
                    // Mengambil informasi produk berdasarkan id yang dipilih pengguna
                    sql = "SELECT nama, harga FROM tbkacamata WHERE idkacamata = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, idPilihan);
                    rs = pstmt.executeQuery();

                    // Menampilkan struk pembayaran
                    if (rs.next()) {
                        String namaProduk = rs.getString("nama");
                        double hargaProduk = rs.getDouble("harga");

                        System.out.println("\nProduk: " + namaProduk);
                        System.out.println("Harga: Rp" + hargaProduk);

                        // Menambahkan informasi produk ke daftar produk yang dibeli
                        produkDibeli.add(namaProduk + " - Rp" + hargaProduk);

                        // Menambahkan harga produk ke total harga
                        totalHarga += hargaProduk;
                    } else {
                        System.out.println("Produk dengan ID tersebut tidak ditemukan.");
                    }
                } else {
                    scanner.nextLine();
                    ulang = false;
                    return;
                }

                // Menampilkan struk pembayaran
                System.out.println("\n===== Struk Pembayaran =====");
                for (String produk : produkDibeli) {
                    System.out.println(produk);
                }
                System.out.println("Total Harga: Rp" + totalHarga);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Menutup koneksi dan resources
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
