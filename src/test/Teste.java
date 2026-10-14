package test;

import java.sql.SQLException;
import java.time.LocalDate;
import main.java.com.ultimarisada.model.Palhaco;
import main.java.com.ultimarisada.repository.PalhacoRepository;

public class Teste {
    public static void main(String[] args) {
        PalhacoRepository palhacoRepository = new PalhacoRepository();
        try {
            //Palhaco penn = new Palhaco(0, "Pennywise", LocalDate.of(0001, 01, 01), "Aterrorizante", 1000000.98, 4.89);
            //palhacoRepository.salvar(penn);
            palhacoRepository.atualizarNota(4, 4.89);
            Palhaco penn = palhacoRepository.buscarPorId(4);
            System.out.println(penn.exibirResumo());
        } catch (SQLException e) {
            System.out.println("SQL EXCEPTION!");
        }
    }
}
