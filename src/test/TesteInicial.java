package test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.ultimarisada.exceptions.InstrumentoEmUsoException;
import com.ultimarisada.exceptions.PalhacoEmUsoException;
import com.ultimarisada.model.InstrumentoDeRisada;
import com.ultimarisada.model.Palhaco;
import com.ultimarisada.service.InstrumentoService;
import com.ultimarisada.service.PalhacoService;

public class TesteInicial {
    public static void main(String[] args) {
        PalhacoService palhacoService = new PalhacoService();
        InstrumentoService instrumentoService = new InstrumentoService();

        try {
            System.out.println("=== TESTANDO PALHAÇO SERVICE ===");

            // Cadastrar Palhaço
            Palhaco novoPalhaco = new Palhaco(
                "Pipoca", 
                LocalDate.of(1990, 5, 15), 
                "Explosivo", 
                150.0
            );
            
            palhacoService.cadastrar(novoPalhaco);
            System.out.println("Palhaço cadastrado com sucesso!");

            // Listar Todos os Palhaços
            System.out.println("\n--- Listando todos os palhaços ---");
            List<Palhaco> palhacos = palhacoService.listarTodos();
            for (Palhaco p : palhacos) {
                System.out.println("ID: " + p.getId() + " | " + p.exibirResumo() + " | Cachê: " + p.getCache());
            }

            // Podem haver mais de um palhaço com o mesmo nome
            List<Palhaco> buscaNome = palhacoService.buscarPalhaco("Pipoca");
            if (!buscaNome.isEmpty()) {
                Palhaco palhacoSalvo = buscaNome.get(0);
                int idPalhaco = palhacoSalvo.getId();

                // Buscar por ID
                System.out.println("\n--- Buscando palhaço por ID (" + idPalhaco + ") ---");
                Palhaco pBuscado = palhacoService.buscarPorId(idPalhaco);
                System.out.println("Encontrado: " + pBuscado.getNome());

                // Editar Palhaço
                System.out.println("\n--- Editando palhaço ---");
                pBuscado.setPersonalidade("Esquentadinho");
                palhacoService.editar(pBuscado);
                System.out.println("Personalidade atualizada para: " + palhacoService.buscarPorId(idPalhaco).getPersonalidade());

                // Outras buscas e listagens
                System.out.println("\n--- Buscando por personalidade e cachê máximo ---");
                List<Palhaco> porPers = palhacoService.buscarPalhaco("Esquentadinho", 200.0);
                System.out.println("Encontrados por critério: " + porPers.size());

                System.out.println("\n--- Listando ordenado por nota ---");
                List<Palhaco> porNota = palhacoService.listarOrdenadoPorNota();
                for (Palhaco p : porNota) {
                    System.out.println(p.exibirResumo());
                }

                System.out.println("\n=== TESTANDO INSTRUMENTO SERVICE ===");

                // Cadastrar Instrumento para o Palhaço
                InstrumentoDeRisada novoInstrumento = new InstrumentoDeRisada(
                    750.23, 
                    "Buzina de Automóvel em Alta Velocidade", 
                    "Te mata de rir com tanto barulho!"
                );
                
                instrumentoService.cadastrar(novoInstrumento, idPalhaco);
                System.out.println("Instrumento cadastrado e vinculado ao palhaço " + idPalhaco);

                // Listar Instrumentos por Palhaço
                System.out.println("\n--- Listando instrumentos do palhaço ---");
                List<InstrumentoDeRisada> instrumentos = instrumentoService.listarPorPalhaco(idPalhaco);
                System.out.println("Total de instrumentos do palhaço: " + instrumentos.size());

                if (!instrumentos.isEmpty()) {
                    int idInstrumento = instrumentos.get(0).getId();

                    // Buscar Instrumento por ID
                    System.out.println("\n--- Buscando instrumento por ID ---");
                    InstrumentoDeRisada instBuscado = instrumentoService.buscarPorId(idInstrumento);
                    System.out.println("Instrumento encontrado: " + instBuscado.getNome() + " - " + instBuscado.getDescricao());

                    // Tentar remover instrumento
                    try {
                        instrumentoService.remover(idInstrumento);
                        System.out.println("Instrumento removido com sucesso.");
                    } catch (InstrumentoEmUsoException e) {
                        System.out.println("Aviso ao remover instrumento: " + e.getMessage());
                    }
                }

                // Tentar remover palhaço
                try {
                    palhacoService.remover(idPalhaco);
                    System.out.println("Palhaço removido com sucesso.");
                } catch (PalhacoEmUsoException e) {
                    System.out.println("Aviso ao remover palhaço: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro de banco de dados durante os testes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/*
Essa é a saída do teste quando executado em uma máquina com o banco de dados funcionando. Note que, pelos métodos do ContratoRepository ainda não estarem implementados, estando sempre retornando true, as funções de remoção dos services sempre irão lançar as exceções.

=== TESTANDO PALHAÇO SERVICE ===
Palhaço cadastrado com sucesso!

--- Listando todos os palhaços ---
ID: 15 | Nome: Pipoca | Personalidade: Explosivo | Nota: 0.0 | Cachê: 150.0

--- Buscando palhaço por ID (15) ---
Encontrado: Pipoca

--- Editando palhaço ---
Personalidade atualizada para: Esquentadinho

--- Buscando por personalidade e cachê máximo ---
Encontrados por critério: 1

--- Listando ordenado por nota ---
Nome: Pipoca | Personalidade: Esquentadinho | Nota: 0.0

=== TESTANDO INSTRUMENTO SERVICE ===
Instrumento cadastrado e vinculado ao palhaço 15

--- Listando instrumentos do palhaço ---
Total de instrumentos do palhaço: 1

--- Buscando instrumento por ID ---
Instrumento encontrado: Buzina de Automóvel em Alta Velocidade - Te mata de rir com tanto barulho!
Aviso ao remover instrumento: Não é possível remover o instrumento, pois ele está vinculado a um contrato PENDENTE.
Aviso ao remover palhaço: O palhaço possui contratos pendentes e não pode ser excluído.
*/
