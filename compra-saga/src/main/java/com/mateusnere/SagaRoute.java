package com.mateusnere;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.CamelSagaService;
import org.apache.camel.saga.InMemorySagaService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SagaRoute extends RouteBuilder {

    @Inject
    PedidoService pedidoService;

    @Inject
    CreditoService creditoService;

    @Override
    public void configure() throws Exception {

        CamelSagaService sagaService = new InMemorySagaService();
        getContext().addService(sagaService);

        //SAGA
        from("direct:saga").saga().propagation(SagaPropagation.REQUIRES_NEW).log("Iniciando transação")
                .to("direct:novoPedido").log("Pedido ${header.id} criado. Saga ${body}.")
                .to("direct:realizaDebito").log("Débito no valor de ${header.valor} agendado para o pedido ${header.id}. Saga ${body}.")
                .to("direct:finaliza").log("Pedido finalizado!");

        //Pedido Service
        from("direct:novoPedido").saga().propagation(SagaPropagation.MANDATORY)
                .compensation("direct:cancelarPedido")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(pedidoService, "novoPedido").log("Criando novo pedido com id ${header.id}.");

        from("direct:cancelarPedido")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(pedidoService, "cancelarPedido").log("Pedido ${body} compensado.");

        //Credito Service
        from("direct:realizaDebito").saga().propagation(SagaPropagation.MANDATORY)
                .compensation("direct:realizaEstorno")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(creditoService, "realizaDebito").log("Agendando o debito!");

        from("direct:realizaEstorno")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(creditoService, "realizaEstorno").log("Credito compensado para a saga ${body}");

        from("direct:finaliza").saga().propagation(SagaPropagation.MANDATORY)
                .choice()
                .end();
    }
}
