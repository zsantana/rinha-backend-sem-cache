package org.acme;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/")
public class PessoaResource {

    private static final Logger logger = LoggerFactory.getLogger(PessoaResource.class);

    @GET
    @Path("/pessoas")
    @Produces(MediaType.APPLICATION_JSON)
    @WithSession
    public Uni<Response> findTop50(@QueryParam("t") String termo) {
        //logger.info("### Buscando pessoa: {} ", termo);

        if (termo == null || termo.isBlank()) {
            return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
        }

        return Pessoa.<Pessoa>find("busca like '%' || ?1 || '%'", termo).page(0, 50).list()
                    .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                    .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND)::build);
       
    }

    @POST
    @Path("/pessoas")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> create(Pessoa pessoa) {

        try {

            String apelido = pessoa.getApelido();
            String nome = pessoa.getNome();

            if (apelido == null || apelido.isBlank() || apelido.length() > 32
                    || nome == null || nome.isBlank() || nome.length() > 100 
                    || invalidStack(pessoa.getStack())
                    ) {
                //logger.info("### Apelido fora do padrao permitido");
                return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
            }

            var uuid = UUID.randomUUID();
            pessoa.setId(uuid);

            return pessoaByApelidoExists(apelido)
                        .flatMap(result -> {
                            if (result) {
                                return Uni.createFrom().item(Response.status(422).build());
                            } else {
                                return Panache.withTransaction(() -> pessoa.persist())
                                        .replaceWith(
                                                Response.status(Status.CREATED)
                                                        .entity(pessoa)
                                                        .header("Location", "/pessoas/" + uuid.toString())
                                                        .build()
                                        );
                            }
                        });
                    
        } catch (Exception e) {
            logger.error("### ERRO: {}", e.getMessage());
            return Uni.createFrom().item(Response.status(Status.INTERNAL_SERVER_ERROR).build());
        }                    
    }

    
    @GET
    @Path("/pessoas/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @WithSession
    public Uni<Response> get(@PathParam("id") String id) {

        try {
        
            return Pessoa.<Pessoa>findById(UUID.fromString(id))
                        .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                        .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND)::build);

        } catch (Exception e) {
            logger.error("### ERRO: {}", e.getMessage());
            return Uni.createFrom().item(Response.status(Status.INTERNAL_SERVER_ERROR).build());
        }
        
    }

   
    @GET
    @Path("/contagem-pessoas")
    @WithSession
    public Uni<Long> count() {
        return Pessoa.count();
    }

    private Uni<Boolean> pessoaByApelidoExists(String apelido) {
        return Pessoa.count("apelido", apelido)
                .onItem().transform(count -> count > 0);
    }

    public List<String> convertToEntityAttribute(String string) {
        return string != null ? Arrays.asList(string.split(";")) : emptyList();
    }


    private boolean invalidStack(List<String> stack) {
        if (stack == null) {
            return false;
        }

        for (String s : stack) {
            if (s.length() > 32) {
                return true;
            }
        }

        return false;
    }

}
