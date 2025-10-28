# Strategy Design Pattern

The Strategy Pattern defines a family of algorithms, encapsulates each one, and makes them interchangeable. 
Strategy lets the algorithm vary independently from clients that use it.

## Using the strategy pattern to change object behavior

The strategy pattern can sometimes be called a policy pattern because it establishes precise steps for runtime execution in a particular situation or state. This pattern is a part of the GoF’s book.

## Motivation

The strategy pattern represents a family of algorithms where each one is properly encapsulated. It defines the interchangeability of algorithms to which a particular object can respond. This strategy allows the algorithm to change independently of the clients using it and allows the client to choose the most appropriate one on the fly. In other words, the code allows the client to attach various strategy objects that affect the behavior of the program.

## O que é
Strategy encapsula algoritmos intercambiáveis por trás de uma interface comum, permitindo selecionar o comportamento em tempo de execução sem mudar o cliente. Resolve a violação do OCP (Open/Closed) quando o “if-else/switch” prolifera.

## Quando usar 
- Há várias variações do mesmo objetivo (ex.: cálculo de frete, desconto, roteamento, serialização).
- Regras mudam com frequência e você quer adicionar novas sem tocar no cliente.
- Precisa selecionar a estratégia por configuração, feature flag, dados de entrada ou capability do ambiente.

## Benefícios arquiteturais
- adicionar novas estratégias sem alterar o cliente; cada estratégia com uma única razão de mudança.
- Testabilidade: mock do contrato e unit tests por estratégia.
- Observabilidade e governança: fácil medir e comparar desempenho de cada algoritmo.

# Trade-offs
- Granularidade: estratégias demais tornam o grafo complexo (custo cognitivo).
- Estado: estratégias devem ser stateless ou com estado imutável para segurança em concorrência.
- Overengineering: não use se há apenas 2 variações estáveis e raramente mudam.

´´´mermaid

classDiagram
    class Context {
      -Strategy strategy
      +execute(input): Output
      +setStrategy(Strategy)
    }
    class Strategy {
      <<interface>>
      +apply(input): Output
    }
    class ConcreteA {
      +apply(input): Output
    }
    class ConcreteB {
      +apply(input): Output
    }
    Context --> Strategy
    Strategy <|.. ConcreteA
    Strategy <|.. ConcreteB

´´´

## Quarkus cdi how implement stategy pattern

Implementing the Strategy Pattern in Quarkus using CDI involves defining an interface for the strategy, creating concrete implementations, and then using CDI qualifiers or a factory to select the appropriate strategy at injection time or runtime.

### Define the Strategy Interface:
First, create an interface that defines the contract for your different strategies.

public interface PaymentStrategy {
    void processPayment(double amount);
}

###  Implement Concrete Strategies:
Next, create concrete implementations of your strategy interface, each encapsulating a different algorithm or behavior.
```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named; // Or a custom @Qualifier

@ApplicationScoped
@Named("creditCard") // Use @Named or a custom @Qualifier
public class CreditCardPaymentStrategy implements PaymentStrategy {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment of: " + amount);
        // Credit card specific logic
    }
}

@ApplicationScoped
@Named("paypal") // Use @Named or a custom @Qualifier
public class PayPalPaymentStrategy implements PaymentStrategy {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing PayPal payment of: " + amount);
        // PayPal specific logic
    }
}
```

### Selecting the Strategy:

You have a few options for selecting the desired strategy:
a) Using @Named and @Inject:
If you know which strategy you need at compile time, you can use @Named to specify the desired implementation.
```java
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/payments")
public class PaymentResource {

    @Inject
    @Named("creditCard") // Inject the specific strategy by name
    PaymentStrategy creditCardStrategy;

    @GET
    @Path("/creditcard")
    @Produces(MediaType.TEXT_PLAIN)
    public String makeCreditCardPayment() {
        creditCardStrategy.processPayment(100.0);
        return "Credit card payment processed.";
    }
}
```
b) Using Custom Qualifiers:
For more type-safe and flexible selection, create custom CDI qualifiers.
```java
import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface CreditCardPayment {}

@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface PayPalPayment {}


Then, annotate your concrete strategies and injection points with these qualifiers:

// ... (CreditCardPaymentStrategy and PayPalPaymentStrategy with @CreditCardPayment and @PayPalPayment respectively)

@Path("/payments")
public class PaymentResource {

    @Inject
    @CreditCardPayment // Inject the specific strategy using the custom qualifier
    PaymentStrategy creditCardStrategy;

    // ...
}
```

c) Using a Strategy Factory (for dynamic selection):
If the strategy needs to be chosen dynamically at runtime based on some criteria, use a factory.
```java
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PaymentStrategyFactory {

    @Inject
    @Any // Inject all available PaymentStrategy implementations
    Instance<PaymentStrategy> paymentStrategies;

    public PaymentStrategy getStrategy(String type) {
        for (PaymentStrategy strategy : paymentStrategies) {
            if (strategy.getClass().isAnnotationPresent(Named.class) &&
                strategy.getClass().getAnnotation(Named.class).value().equals(type)) {
                return strategy;
            }
            // Or use custom qualifiers for more robust selection
        }
        throw new IllegalArgumentException("Unknown payment strategy: " + type);
    }
}

// In your resource:
@Path("/payments")
public class PaymentResource {

    @Inject
    PaymentStrategyFactory strategyFactory;

    @GET
    @Path("/dynamic/{type}")
    @Produces(MediaType.TEXT_PLAIN)
    public String makeDynamicPayment(String type) {
        PaymentStrategy strategy = strategyFactory.getStrategy(type);
        strategy.processPayment(200.0);
        return type + " payment processed.";
    }
}
```
This approach leverages CDI's capabilities for managing and injecting beans, making the Strategy Pattern implementation in Quarkus clean and maintainable.




