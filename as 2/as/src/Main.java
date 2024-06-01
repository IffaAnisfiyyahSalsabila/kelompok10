import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        boolean ulang = true;
        Scanner scanner = new Scanner(System.in);
        while (ulang) {
            System.out.println("Selamat Datang! Pilih salah satu opsi:");
            System.out.println("1. Masuk");
            System.out.println("2. Daftar");
            System.out.println("3. Exit");
            System.out.print("Masukkan pilihan Anda: ");
            int pilihan = scanner.nextInt();
            scanner.nextLine();

            if (pilihan == 1) {
                masuk(scanner);
            } else if (pilihan == 2) {
                boolean terdaftar = daftar(scanner);
                if (terdaftar) {
                    System.out.println("Pendaftaran berhasil. Masuk...");
                    masuk(scanner); // Masuk langsung setelah pendaftaran berhasil
                } else {
                    System.out.println("Pendaftaran gagal. Username mungkin sudah ada.");
                }
            } else if (pilihan == 3) {
                ulang = false;
            } else {
                System.out.println("Pilihan tidak valid.");
            }

            if (scanner.hasNextLine()) {
                scanner.nextLine(); // consume the remaining newline character
            }
        }
    }

    private static void masuk(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        String peran = LoginService.authenticate(username, password);

        if (peran == null) {
            System.out.println("Username atau password salah.");
        } else if (peran.equals("admin")) {
            App.main(null);
        } else if (peran.equals("user")) {
            Userr.main(null);
        }
    }

    private static boolean daftar(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM tbuser WHERE username = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setString(1, username);
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int jumlah = resultSet.getInt(1);

            if (jumlah > 0) {
                System.out.println("Username sudah ada");
                return false;
            }

            String insertSql = "INSERT INTO tbuser (username, password, role) VALUES (?, ?, 'user')";
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
            insertStatement.setString(1, username);
            insertStatement.setString(2, password);

            int barisDimasukkan = insertStatement.executeUpdate();
            return barisDimasukkan > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}