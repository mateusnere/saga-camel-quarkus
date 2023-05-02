package com.mateusnere;

import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class PedidoService {

    private Set<Long> pedidos = new HashSet<>();

    public void novoPedido(@Header("id") Long id) {
        pedidos.add(id);
    }

    public void cancelarPedido(@Header("id") Long id) {
        pedidos.remove(id);
    }
}