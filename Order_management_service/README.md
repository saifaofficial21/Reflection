# Order Management Service

Spring Boot microservice exposing a REST API for orders with **in-memory** storage (`ConcurrentHashMap`), Bean Validation on DTOs, enforced status transitions, and centralized error handling via `@RestControllerAdvice`.

- **Java:** 17+  
- **Framework:** Spring Boot 4.x  
- **Base URL:** `http://localhost:8080`

## API overview

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/orders` | Create order (`customerName`, `amount`) — returns `201` |
| `GET` | `/orders/{orderId}` | Get order by id |
| `PUT` | `/orders/{orderId}/status` | Update status (`PROCESSING` \| `COMPLETED`) |
| `GET` | `/orders` | List all orders |

## Status rules

Valid transitions: **NEW → PROCESSING → COMPLETED**. Terminal state **COMPLETED** allows no further changes.

## Run

```bash
./mvnw.cmd spring-boot:run
./mvnw.cmd test
```

---

## Diagrams

### Order lifecycle (state machine)

```mermaid
stateDiagram-v2
    direction LR
    [*] --> NEW : POST /orders
    NEW --> PROCESSING : PUT /{orderId}/status
    PROCESSING --> COMPLETED : PUT /{orderId}/status
    COMPLETED --> [*]

    note right of NEW
        Initial state on creation.
        Only valid next: PROCESSING
    end note

    note right of PROCESSING
        Order is being fulfilled.
        Only valid next: COMPLETED
    end note

    note right of COMPLETED
        Terminal state.
        No further transitions allowed.
    end note
```

### Request flow (sequence)

```mermaid
sequenceDiagram
    autonumber
    participant Client
    participant OrderController
    participant OrderServiceImpl
    participant InMemoryStore
    participant GlobalExceptionHandler

    Client->>OrderController: POST /orders with JSON body
    OrderController->>OrderController: @Valid — Bean Validation
    alt Validation fails
        OrderController->>GlobalExceptionHandler: MethodArgumentNotValidException
        GlobalExceptionHandler-->>Client: 400 Bad Request and validation errors
    else Validation passes
        OrderController->>OrderServiceImpl: createOrder(request)
        OrderServiceImpl->>OrderServiceImpl: generate UUID orderId
        OrderServiceImpl->>InMemoryStore: put(orderId, order)
        OrderServiceImpl-->>OrderController: OrderResponse
        OrderController-->>Client: 201 Created + OrderResponse
    end

    Client->>OrderController: PUT /orders/{orderId}/status {status}
    OrderController->>OrderServiceImpl: updateOrderStatus(orderId, newStatus)
    OrderServiceImpl->>InMemoryStore: get(orderId)
    alt Order not found
        OrderServiceImpl->>GlobalExceptionHandler: OrderNotFoundException
        GlobalExceptionHandler-->>Client: 404 Not Found
    else Invalid transition
        OrderServiceImpl->>GlobalExceptionHandler: InvalidStatusTransitionException
        GlobalExceptionHandler-->>Client: 400 Bad Request
    else Transition valid
        OrderServiceImpl->>InMemoryStore: order.setStatus(newStatus)
        OrderServiceImpl-->>OrderController: OrderResponse
        OrderController-->>Client: 200 OK + OrderResponse
    end
```

### Component / architecture

```mermaid
graph TD
    subgraph clientLayer [Client Layer]
        C[HTTP Client / Postman]
    end

    subgraph apiLayer [API Layer]
        CTRL[OrderController @RestController]
        GEH[GlobalExceptionHandler @RestControllerAdvice]
    end

    subgraph dtoLayer [DTO Layer]
        COR[CreateOrderRequest]
        USR[UpdateStatusRequest]
        OR[OrderResponse]
    end

    subgraph serviceLayer [Service Layer]
        SVC[OrderService interface]
        SVCI[OrderServiceImpl @Service]
    end

    subgraph domainLayer [Domain Layer]
        ORDER[Order entity]
        STATUS[OrderStatus enum]
    end

    subgraph storageLayer [Storage Layer]
        MAP[ConcurrentHashMap in-memory store]
    end

    subgraph exceptionLayer [Exception Layer]
        ONF[OrderNotFoundException]
        IST[InvalidStatusTransitionException]
    end

    C -->|HTTP Request| CTRL
    CTRL -->|delegates| SVC
    SVC -.->|implemented by| SVCI
    SVCI -->|reads / writes| MAP
    SVCI -->|creates| ORDER
    ORDER -->|uses| STATUS
    CTRL -->|uses| COR
    CTRL -->|uses| USR
    CTRL -->|returns| OR
    SVCI -->|throws| ONF
    SVCI -->|throws| IST
    ONF -->|handled by| GEH
    IST -->|handled by| GEH
    GEH -->|error response| C
```

### In-memory logical schema (ER diagram)

There is no physical database; this documents the logical data held in memory (it would map cleanly to a relational model if persisted later).

```mermaid
erDiagram
    ORDER {
        string orderId
        string customerName
        float amount
        string status
        string createdAt
        string updatedAt
    }
    ORDER_STATUS_TRANSITION {
        string fromStatus
        string toStatus
        bool allowed
    }
    ORDER ||--o{ ORDER_STATUS_TRANSITION : governs
```

GitHub’s Mermaid renderer often fails when ER attribute comments contain **HTML-like tokens** (for example `>` or `|` inside quoted text). The diagram above avoids those characters so it can render reliably; see the prose and table below for full semantics.

**Valid transitions**

| From Status   | To Status     | Allowed |
|---------------|---------------|---------|
| `NEW`         | `PROCESSING`  | Yes     |
| `NEW`         | `COMPLETED`   | No      |
| `PROCESSING`  | `COMPLETED`   | Yes     |
| `PROCESSING`  | `NEW`         | No      |
| `COMPLETED`   | `NEW`         | No      |
| `COMPLETED`   | `PROCESSING`  | No      |

### Class diagram

```mermaid
classDiagram
    class Order {
        -String orderId
        -String customerName
        -Double amount
        -OrderStatus status
        -Instant createdAt
        -Instant updatedAt
        +setStatus(OrderStatus) void
        +getters() ...
    }

    class OrderStatus {
        <<enumeration>>
        NEW
        PROCESSING
        COMPLETED
        +canTransitionTo(OrderStatus) boolean
    }

    class OrderController {
        -OrderService orderService
        +createOrder(CreateOrderRequest) ResponseEntity
        +getOrder(String) ResponseEntity
        +updateStatus(String, UpdateStatusRequest) ResponseEntity
        +listOrders() ResponseEntity
    }

    class OrderService {
        <<interface>>
        +createOrder(CreateOrderRequest) OrderResponse
        +getOrderById(String) OrderResponse
        +updateOrderStatus(String, OrderStatus) OrderResponse
        +listAllOrders() List~OrderResponse~
    }

    class OrderServiceImpl {
        -Map store
        +createOrder(CreateOrderRequest) OrderResponse
        +getOrderById(String) OrderResponse
        +updateOrderStatus(String, OrderStatus) OrderResponse
        +listAllOrders() List~OrderResponse~
        -findOrThrow(String) Order
    }

    class CreateOrderRequest {
        -String customerName
        -Double amount
    }

    class UpdateStatusRequest {
        -OrderStatus status
    }

    class OrderResponse {
        -String orderId
        -String customerName
        -Double amount
        -OrderStatus status
        -Instant createdAt
        -Instant updatedAt
        +from(Order)$ OrderResponse
    }

    class OrderNotFoundException {
        +OrderNotFoundException(String)
    }

    class InvalidStatusTransitionException {
        +InvalidStatusTransitionException(OrderStatus, OrderStatus)
    }

    class GlobalExceptionHandler {
        +handleNotFound(OrderNotFoundException) ResponseEntity
        +handleBadTransition(InvalidStatusTransitionException) ResponseEntity
        +handleValidation(MethodArgumentNotValidException) ResponseEntity
    }

    OrderController --> OrderService
    OrderServiceImpl ..|> OrderService
    OrderServiceImpl --> Order
    Order --> OrderStatus
    OrderController --> CreateOrderRequest
    OrderController --> UpdateStatusRequest
    OrderController --> OrderResponse
    OrderServiceImpl ..> OrderNotFoundException
    OrderServiceImpl ..> InvalidStatusTransitionException
    GlobalExceptionHandler ..> OrderNotFoundException
    GlobalExceptionHandler ..> InvalidStatusTransitionException
    OrderResponse ..> Order
```

## Package layout

Implementation lives under `com.Reflection.Order_management_service` with subpackages `controller`, `dto`, `exception`, `model`, and `service`.
