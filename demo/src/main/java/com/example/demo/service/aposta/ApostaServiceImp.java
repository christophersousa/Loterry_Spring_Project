package com.example.demo.service.aposta;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Aposta;
import com.example.demo.model.Cliente;
import com.example.demo.model.Sorteio;
import com.example.demo.service.ApostaService;
import com.example.demo.service.ClienteService;
import com.example.demo.service.SorteioService;
import com.example.demo.service.aposta.proxy.ProxyServiceAposta;

@Service
public class ApostaServiceImp implements ApostaService {

    private final ClienteService clienteService;

    private final ProxyServiceAposta apostaRepository;

    private final SorteioService sorteioService;

    @Autowired
    public ApostaServiceImp(ProxyServiceAposta apostaRepository, ClienteService clienteService,
            SorteioService sorteioService) {
        this.apostaRepository = apostaRepository;
        this.clienteService = clienteService;
        this.sorteioService = sorteioService;
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
        BigDecimal result = new BigDecimal(0);

        if (value == 6) {
            result = result.valueOf(3.00);
        } else if (value == 7) {
            result = result.valueOf(15.00);
        } else if (value == 8) {
            result = result.valueOf(90.00);
        } else if (value == 9) {
            result = result.valueOf(300.00);
        } else {
            result = result.valueOf(1200.00);
        }

        try {
            System.out.println(clienteService.debitarCliente(cliente.getUser().getUsername(), result));
        } catch (Exception e) {
            throw e;
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

}