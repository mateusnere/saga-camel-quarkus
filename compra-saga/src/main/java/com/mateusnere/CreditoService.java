package com.mateusnere;

import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class CreditoService {

    private int creditoTotal;
    private Map<Long, Integer> pedido_valor = new HashMap<>();

    public CreditoService() {
        this.creditoTotal = 100;
    }

    public void realizaDebito(@Header("id") Long pedidoId, @Header("valor")  int valor) {
        if(valor > creditoTotal) {
            throw new IllegalStateException("Saldo insuficiente");
        }

        creditoTotal -= valor;
        pedido_valor.put(pedidoId, valor);
    }

    public void realizaEstorno(@Header("id") Long id) {
        System.out.println("PedidoValor falhou! Iniciando cancelamento do pedido.");
    }

    public int getCreditoTotal() {
        return this.creditoTotal;
    }
}