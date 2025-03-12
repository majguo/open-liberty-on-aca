package cafe.web.view;

import cafe.model.entity.Coffee;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.lang.invoke.MethodHandles;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class Cafe implements Serializable {

    private static final long serialVersionUID = 1L;

    private String baseUri;
    private transient Client client;

    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    private Double price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @PostConstruct
    private void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                .getRequest();
        baseUri = "http://localhost:9080" + request.getContextPath() + "/rest/coffees";
        initClient();
    }

    private void initClient() {
        client = ClientBuilder.newBuilder().build();
    }

    public List<Coffee> getCoffeeList() {
        return client.target(baseUri).path("/").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Coffee>>() {
                });
    }

    public void addCoffee() throws IOException {
        Coffee coffee = new Coffee(name, price);
        client.target(baseUri).request(MediaType.APPLICATION_JSON).post(Entity.json(coffee));
        FacesContext.getCurrentInstance().getExternalContext().redirect("");
    }

    public void removeCoffee(String coffeeId) throws IOException {
        client.target(baseUri).path(coffeeId).request().delete();
        FacesContext.getCurrentInstance().getExternalContext().redirect("");
    }

    // This method is called after deserialization to initialize transient fields.
    private Object readResolve() {
        initClient();
        return this;
    }
}
