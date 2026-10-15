package main.java.com.ultimarisada.service;

import main.java.com.ultimarisada.model.Palhaco;
import main.java.com.ultimarisada.repository.PalhacoRepository;
import main.java.com.ultimarisada.repository.ContratoRepository;
import main.java.com.ultimarisada.exceptions.PalhacoEmUsoException;
import java.util.List;

public class PalhacoService {
    private PalhacoRepository palhacoRepository = new PalhacoRepository();
    private ContratoRepository contratoRepository = new ContratoRepository();
    
    public void cadastrar(Palhaco p) {
        palhacoRepository.salvar(p);
    }

    public void editar(Palhaco p) {
        palhacoRepository.atualizar(p);
    }
    
    public void remover(int id) throws PalhacoEmUsoException {
        // Lança exceção para impedir a exclusão se o palhaço tiver contratos pendentes
        if(contratoRepository.existeContratoPendenteDoPalhaco(id)) {
            throw new PalhacoEmUsoException("O palhaço possui contratos pendentes e não pode ser excluído.");
        }
        palhacoRepository.remover(id);
    }

    public Palhaco buscarPorId(int id) {
        return palhacoRepository.buscarPorId(id);
    }

    public List<Palhaco> listarTodos() {
        return palhacoRepository.listarTodos();
    }
    public List<Palhaco> buscarPalhaco(String nome) {
        return palhacoRepository.buscarPorNome(nome);
    }

    public List<Palhaco> buscarPalhaco(String personalidade, double cacheMaximo) {
        return palhacoRepository.buscarPorPersonalidadeECacheMaximo(personalidade, cacheMaximo);
    } 

    public List<Palhaco> listarOrdenadoPorNota() {
        return palhacoRepository.listarOrdenadoPorNota();
    }
}
