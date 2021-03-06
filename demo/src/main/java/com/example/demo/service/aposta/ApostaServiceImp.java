package com.example.demo.service.aposta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Aposta;
import com.example.demo.model.Cliente;
import com.example.demo.model.Sorteio;
import com.example.demo.service.ApostaService;
import com.example.demo.service.ClienteService;
import com.example.demo.service.SorteioService;
import com.example.demo.service.aposta.proxy.ProxyServiceAposta;
import com.example.demo.service.protorype.ValorAposta;

@Service
public class ApostaServiceImp implements ApostaService {

    private final ClienteService clienteService;

    private final ProxyServiceAposta apostaRepository;

    private final SorteioService sorteioService;

    private final ValorAposta valorAposta;

    @Autowired
    public ApostaServiceImp(ProxyServiceAposta apostaRepository, ClienteService clienteService,
            SorteioService sorteioService, ValorAposta valorAposta) {
        this.apostaRepository = apostaRepository;
        this.clienteService = clienteService;
        this.sorteioService = sorteioService;
        this.valorAposta = valorAposta;
    }

    private void validationNumber(Integer value) throws Exception {
        if (value < 6) {
            throw new Exception("A quantidade mínima para fazer uma Sorteio é 6");
        } else if (value > 10) {
            throw new Exception("A quantidade máxima para fazer uma Sorteio é 10");
        }
    }

    private List<String> numbersAposta(List<String> numbers) {
        while (numbers.remove(null))
            ;
        numbers.remove(0);

        return numbers;
    }

    private BigDecimal valor(Integer value, Cliente cliente) throws Exception {
        BigDecimal result = valorAposta.get(value);

        try {
            clienteService.debitarCliente(cliente.getUser().getUsername(), result);
        } catch (Exception e) {
            System.out.println("******* erro ******" + e.getMessage());
            throw new Exception(e.getMessage());
        }
        return result;
    }

    @Override
    public Aposta saveAposta(Aposta aposta, Cliente cliente) throws Exception {
        try {
            aposta.setNumeros(numbersAposta(aposta.getNumeros()));

            validationNumber(aposta.getNumeros().size());
            aposta.setSorteio(sorteioService.SorteioEnabled().get(0));
            aposta.setAposta(valor(aposta.getNumeros().size(), cliente));
            aposta.setCliente(cliente);

            apostaRepository.save(aposta);

        } catch (Exception e) {
            throw e;
        }
        return aposta;
    }

    public List<Aposta> getMinhasApostas(Cliente cliente) {
        return apostaRepository.findAllByAposta(cliente.getUser().getUsername());
    }

    public List<Aposta> findAllBySorteio(Sorteio sorteio) {
        return apostaRepository.findAllBySorteio(sorteio);

    }

    @Override
    public Optional<Aposta> favoritar(Integer id) throws Exception {
        Optional<Aposta> aposta = apostaRepository.findById(id);
        if (!aposta.isPresent()) {
            throw new Exception("Aposta não existe!");
        }

        Aposta apostaFav = aposta.get();
        apostaFav.setFavorito(true);
        apostaRepository.save(apostaFav);
        return aposta;
    }

    @Override
    public Optional<Aposta> JogarNovamente(Integer id) throws Exception {
        Optional<Aposta> aposta = apostaRepository.findById(id);
        if (!aposta.isPresent()) {
            throw new Exception("Aposta não existe!");
        }

        try {
            List<String> numeros = new ArrayList<String>();
            numeros.addAll(aposta.get().getNumeros());
            Aposta apostaNova = new Aposta();
            apostaNova.setAposta(aposta.get().getAposta());
            apostaNova.setNumeros(numeros);
            apostaNova.setCliente(aposta.get().getCliente());
            apostaNova.setSorteio(sorteioService.SorteioEnabled().get(0));
            apostaRepository.save(apostaNova);

        } catch (Exception e) {
            throw e;
        }

        return aposta;
    }

    @Override
    public Aposta updateAposta(Aposta aposta) {
        return apostaRepository.save(aposta);
    }

}
