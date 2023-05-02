package com.mateusnere;

import org.apache.camel.CamelContext;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("compra-camel")
public class CompraCamelResource {

    @Inject
    CamelContext context;

    @GET
    @Path("novo-pedido")
    @Produces(MediaType.TEXT_PLAIN)
    public Response novoPedido() {
        try {
            Long id = 0L;

            comprar(++id, 20);
            comprar(++id, 25);
            comprar(++id, 30);
            comprar(++id, 35);

            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    private void comprar(Long idPedido, Integer valor) {
        System.out.println("Pedido " + idPedido + " - Valor: " + valor);

        try {
            context.createFluentProducerTemplate()
                    .to("direct:saga")
                    .withHeader("id", idPedido)
                    .withHeader("valor", valor)
                    .request();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
